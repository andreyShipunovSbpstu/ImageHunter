package ImageHunter;

import ImageHunter.models.DocumentInfoDto;
import ImageHunter.models.IFileInfo;

import java.io.IOException;

public interface IDocumentHunterService{
    DocumentInfoDto GetDocumentInfo(String url) throws IOException;

    IFileInfo GetFile(String url) throws IOException;
}

