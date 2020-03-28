package ImageHunter.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationByProperties implements IConfiguration {

    public ConfigurationByProperties(String path){
        var properties = new Properties();
        try (var input = new FileInputStream(path)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        startUrl = properties.getProperty("StartUrl");
        minImageSizeInBytes = Integer.parseInt(properties.getProperty("MinImageSizeInBytes"));
        imageStoreFolder = properties.getProperty("DownloadFolder");
        threadCount = Integer.parseInt(properties.getProperty("ThreadCount"));
    }

    final String startUrl;
    final Integer minImageSizeInBytes;
    final String imageStoreFolder;
    final Integer threadCount;

    @Override
    public String getStartUrl() {
        return startUrl;
    }

    @Override
    public Integer getMinImageSizeInBytes() {
        return minImageSizeInBytes;
    }

    @Override
    public String getImageStoreFolder() {
        return imageStoreFolder;
    }

    @Override
    public Integer getThreadCount() {
        return threadCount;
    }
}
