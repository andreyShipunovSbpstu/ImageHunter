package ImageHunter.models;

public class ProgressInfo{

    public ProgressInfo(int visitedUrl, int totalUrls, String currentImage, int totalImagesDownloaded){
        this.visitedUrl = visitedUrl;
        this.totalUrls = totalUrls;
        this.currentImage = currentImage;
        this.totalImagesDownloaded = totalImagesDownloaded;
    }

    private final int visitedUrl;
    private final int totalUrls;
    private final String currentImage;
    private final int totalImagesDownloaded;

    public int getVisitedUrl(){
        return visitedUrl;
    }

    public int getTotalUrls(){
        return totalUrls;
    }

    public String getCurrentImage(){
        return currentImage;
    }

    public int getTotalImagesDownloaded(){
        return totalImagesDownloaded;
    }
}
