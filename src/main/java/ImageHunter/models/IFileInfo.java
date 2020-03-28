package ImageHunter.models;

import java.io.IOException;

public interface IFileInfo{
    String getName();

    long getSize();

    byte[] getContent();

    void saveFile(String fullPath) throws IOException;
}
