package ImageHunter.configuration;

import ImageHunter.DocumentHunterService;
import ImageHunter.IDocumentHunterService;
import ImageHunter.IImageHunterService;
import ImageHunter.ImageHunterServiceMultiThread;
import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IConfiguration.class).toInstance(new ConfigurationByProperties("config.properties"));
        bind(IDocumentHunterService.class).to(DocumentHunterService.class);
        bind(IImageHunterService.class).to(ImageHunterServiceMultiThread.class);
    }
}
