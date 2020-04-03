package ImageHunter.models;

import java.io.IOException;
import java.nio.file.Path;

public interface IFileInfo{
    String getName();

    long getSize();

    byte[] getContent();

    void saveFile(Path fullPath) throws IOException;
}
