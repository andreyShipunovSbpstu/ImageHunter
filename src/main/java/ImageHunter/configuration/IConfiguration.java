package ImageHunter.configuration;

public interface IConfiguration {

    String getStartUrl();

    Integer getMinImageSizeInBytes();

    String getImageStoreFolder();

    Integer getThreadCount();
}

