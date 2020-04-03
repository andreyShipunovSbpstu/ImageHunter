package ImageHunter;

import ImageHunter.configuration.IConfiguration;
import ImageHunter.models.DocumentInfoDto;
import ImageHunter.models.IFileInfo;
import ImageHunter.models.ProgressInfo;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ImageHunterServiceMultiThread implements IImageHunterService {

    @Inject
    public ImageHunterServiceMultiThread(Provider<IDocumentHunterService> documentHunterServiceProvider, IConfiguration configuration){
        this.documentHunterServiceProvider = documentHunterServiceProvider;
        this.configuration = configuration;
    }

    private final Logger logger = LogManager.getLogger(ImageHunterServiceMultiThread.class);
    Provider<IDocumentHunterService> documentHunterServiceProvider;

    AtomicInteger visitedUrl = new AtomicInteger(0);
    AtomicInteger totalUrls = new AtomicInteger(0);

    AtomicInteger imgIndexes = new AtomicInteger(0);
    AtomicReference<String> currentUrl = new AtomicReference<>();

    ArrayList<AtomicBoolean> isFinishedArray;
    ArrayList<Thread> threads;

    IConfiguration configuration;

    public void Do(String startUrl, String folderPath, Predicate<IFileInfo> predicate) {
        logger.info("Начинаем скачивать изображения");
        queue.add(startUrl);

        int cores = GetThreads();

        threads = new ArrayList<>(cores);
        isFinishedArray = new ArrayList<>(cores);

        logger.info("Создаем " + cores + " потоков обработки");
        for (int i = 0;i < cores;i++){
            isFinishedArray.add(new AtomicBoolean(false));
        }

        for (int i = 0;i < cores;i++){
            int finalI = i;
            var thread = new Thread(() -> {
                while (isFinishedArray.stream().filter(b-> b.get()).count() != isFinishedArray.size()){
                     ProcessUrl(isFinishedArray.get(finalI),folderPath, predicate);
                }
            });

            threads.add(thread);
            thread.setName("thread " + i);
            thread.start();
        }

        logger.info("Все потоки созданы, ожидаем завершение работы");
        for (int i = 0;i < cores;i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                logger.error("Произошла ошибка при попытке дождаться завершения потока", e);
            }
        }

        logger.info("Закончили скачивать изображения");
    }

    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    Set<String> visited = ConcurrentHashMap.newKeySet();
    Set<String> visitedImgUrls = ConcurrentHashMap.newKeySet();

    private void ProcessUrl(AtomicBoolean isFinished, String folderPath, Predicate<IFileInfo> predicate) {
        var service = documentHunterServiceProvider.get();
        DocumentInfoDto info = null;
        var startUrl = queue.poll();

        if(startUrl == null || !visited.add(startUrl)){
            return;
        }

        visitedUrl.incrementAndGet();
        try {
            info = service.GetDocumentInfo(startUrl);
        }
        catch (Exception e){
            logger.error("Произошла ошибка при скачивании веб страницы:" + startUrl, e);
        }

        if(info != null)
        {
            ProcessImagesOnPage(info, service, folderPath, predicate);

            totalUrls.addAndGet(info.getUrls().size());
            for (var url: info.getUrls()) {
                if(!visited.contains(url)){
                    queue.add(url);
                }
                else
                {
                    visitedUrl.incrementAndGet();
                }
            }
        }

        isFinished.set(queue.isEmpty());
    }

    private void ProcessImagesOnPage(DocumentInfoDto info, IDocumentHunterService service, String folderPath, Predicate<IFileInfo> predicate) {
        for (var img: info.getImgUrls()) {
            if (!visitedImgUrls.contains(img)) {
                visitedImgUrls.add(img);
                IFileInfo file;
                imgIndexes.incrementAndGet();
                currentUrl.set(img);
                try {
                    file = service.GetFile(img);
                } catch (Exception e) {
                    logger.error("Произошла ошибка при скачивании изображения:" + img, e);
                    continue;
                }

                var path = Path.of(folderPath, file.getName());
                if (predicate.test(file)) {
                    try {
                        file.saveFile(path);
                    } catch (IOException e) {
                        logger.error("Произошла ошибка при сохранении изображения:" + path.toString(), e);
                    }
                }
            }
        }
    }

    private int GetThreads(){
        int cores;

        if(this.configuration.getThreadCount() <= 0){
            cores = Runtime.getRuntime().availableProcessors() * 2;
        }
        else
        {
            cores = configuration.getThreadCount();
        }
        return cores;
    }

    public ProgressInfo getProgress(){
        return new ProgressInfo(visitedUrl.get(), totalUrls.get(), currentUrl.get(), imgIndexes.get());
    }
}
