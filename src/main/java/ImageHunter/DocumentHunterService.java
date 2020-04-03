package ImageHunter;

import ImageHunter.models.DocumentInfoDto;
import ImageHunter.models.FileInfo;
import ImageHunter.models.IFileInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

public class DocumentHunterService implements IDocumentHunterService{
    public DocumentInfoDto GetDocumentInfo(String url) throws IOException {

        var dto  = new DocumentInfoDto();
        Document doc = Jsoup.connect(url).get();

        dto.setUrls(doc.select("a[href]").stream().map(element -> element.absUrl("href")).distinct().collect(Collectors.toList()));
        dto.setImgUrls(doc.select("img[src]").stream().map(element -> element.absUrl("src")).distinct().collect(Collectors.toList()));

        return dto;
    }

    private static final int MaxFileNameLength = 200;

    @Override
    public IFileInfo GetFile(String url) throws IOException {

        URL u = new URL(url);

        String fileName = url.substring(url.lastIndexOf('/') + 1);
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

        if(fileName.length() >= MaxFileNameLength){
            fileName = fileName.substring(0,MaxFileNameLength);
        }

        var bytes = u.openStream().readAllBytes();

        return new FileInfo(fileName, bytes.length, bytes);
    }
}

