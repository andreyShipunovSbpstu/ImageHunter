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

public class ImageHunterServiceMultiThread implements IImageHunterService {

    @Inject
    public ImageHunterServiceMultiThread(Provider<IDocumentHunterService> documentHunterServiceProvider, IConfiguration configuration){
        this.documentHunterServiceProvider = documentHunterServiceProvider;
        this.configuration = configuration;
    }

    Provider<IDocumentHunterService> documentHunterServiceProvider;

    AtomicInteger visitedUrl = new AtomicInteger(0);
    AtomicInteger totalUrls = new AtomicInteger(0);

    AtomicInteger imgIndexes = new AtomicInteger(0);
    AtomicReference<String> currentUrl = new AtomicReference<>();

    ArrayList<AtomicBoolean> isFinishedArray;
    ArrayList<Thread> threads;

    IConfiguration configuration;

    public void Do(String startUrl, String folderPath, Predicate<IFileInfo> predicate) {

        queue.add(startUrl);

        int cores = GetThreads();

        threads = new ArrayList<>(cores);
        isFinishedArray = new ArrayList<>(cores);

        for (int i = 0;i < cores;i++){
            isFinishedArray.add(new AtomicBoolean(false));
        }

        for (int i = 0;i < cores;i++){
            int finalI = i;
            var thread = new Thread(() -> {
                while (isFinishedArray.stream().filter(b-> b.get()).count() != isFinishedArray.size()){
                    try {
                        ProcessUrl(isFinishedArray.get(finalI),folderPath, predicate);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            threads.add(thread);
            thread.setName("thread " + i);
            thread.start();
        }

        for (int i = 0;i < cores;i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    Set<String> visited = ConcurrentHashMap.newKeySet();
    Set<String> visitedImgUrls = ConcurrentHashMap.newKeySet();

    private void ProcessUrl(AtomicBoolean isFinished, String folderPath, Predicate<IFileInfo> predicate) throws IOException {
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

    private void ProcessImagesOnPage(DocumentInfoDto info, IDocumentHunterService service, String folderPath, Predicate<IFileInfo> predicate) throws IOException {
        for (var img: info.getImgUrls()) {
            if (!visitedImgUrls.contains(img)) {
                visitedImgUrls.add(img);
                IFileInfo file;
                try {
                    file = service.GetFile(img);
                    imgIndexes.incrementAndGet();
                    currentUrl.set(img);
                } catch (Exception e) {
                    continue;
                }
                if (predicate.test(file)) {
                    file.saveFile(Path.of(folderPath, file.getName()).toString());
                }
            }
        }
    }

    private int GetThreads(){
        int cores = 1;

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
        var p = new ProgressInfo(visitedUrl.get(), totalUrls.get(), currentUrl.get(), imgIndexes.get());
        return p;
    }
}
