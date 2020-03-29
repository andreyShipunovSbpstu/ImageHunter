package ImageHunter.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileInfoImpl implements IFileInfo {

    public FileInfoImpl(String name, long size, byte[] content){

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
    public void saveFile(String fullPath) throws IOException {
        Files.write(Paths.get(fullPath), getContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
