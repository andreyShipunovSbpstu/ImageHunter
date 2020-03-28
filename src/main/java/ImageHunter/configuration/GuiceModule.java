package ImageHunter.configuration;

import ImageHunter.DocumentHunterServiceImpl;
import ImageHunter.IDocumentHunterService;
import ImageHunter.IImageHunterService;
import ImageHunter.ImageHunterServiceMultiThread;
import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IConfiguration.class).toInstance(new ConfigurationByProperties("config.properties"));
        bind(IDocumentHunterService.class).to(DocumentHunterServiceImpl.class);
        bind(IImageHunterService.class).to(ImageHunterServiceMultiThread.class);
    }
}
