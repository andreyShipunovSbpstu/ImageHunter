package ImageHunter;

import ImageHunter.configuration.ConfigurationByProperties;
import ImageHunter.configuration.GuiceModule;
import ImageHunter.configuration.IConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        var injector = InitDI();

        var configuration = injector.getInstance(IConfiguration.class);
        var hunter = injector.getInstance(IImageHunterService.class);

        var executor = StartProgressUpdater(hunter);

        hunter.Do(configuration.getStartUrl(), configuration.getImageStoreFolder(), iFileInfo -> iFileInfo.getSize() > configuration.getMinImageSizeInBytes());

        executor.shutdown();
    }

    private static Injector InitDI(){
        return Guice.createInjector(new GuiceModule());
    }

    private static ScheduledExecutorService StartProgressUpdater(IImageHunterService hunter){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            var p = hunter.getProgress();

            if(p.getTotalUrls() <= 0)
                return;

            System.out.println("Обработано " + p.getVisitedUrl() + " ссылок, всего нужно обработать "  +  p.getTotalUrls() + " ссылок "
                    + "\nСохранено изображений " + p.getTotalImagesDownloaded()
                    + "\nТекущая url изображения:" + p.getCurrentImage());
        };
        executor.scheduleWithFixedDelay(task, 3, 3, TimeUnit.SECONDS);

        return executor;
    }
}

