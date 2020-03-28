package ImageHunter.models;

import java.util.Collection;

public class DocumentInfoDto{

    private Collection<String> _urls;

    private Collection<String> _imgUrls;

    public void setUrls(Collection<String> urls){
        _urls = urls;
    }

    public void setImgUrls(Collection<String> imgUrls){
        _imgUrls = imgUrls;
    }

    public Collection<String> getUrls(){
        return _urls;
    }

    public Collection<String> getImgUrls(){
        return _imgUrls;
    }
}

