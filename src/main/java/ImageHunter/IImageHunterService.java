package ImageHunter;

import ImageHunter.models.IFileInfo;
import ImageHunter.models.ProgressInfo;

import java.util.function.Predicate;

public interface IImageHunterService{
    void Do(String startUrl, String folderPath, Predicate<IFileInfo> predicate);

    ProgressInfo getProgress();
}
