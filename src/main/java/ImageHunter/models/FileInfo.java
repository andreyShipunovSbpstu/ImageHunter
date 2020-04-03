package ImageHunter.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileInfo implements IFileInfo {

    public FileInfo(String name, long size, byte[] content){

        this.name = name;
        this.size = size;
        this.content = content;
    }

    private final String name;
    private final long size;
    private final byte[] content;

    @Override
    public String getName() { return name; }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void saveFile(Path fullPath) throws IOException {
        Files.write(fullPath, getContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
