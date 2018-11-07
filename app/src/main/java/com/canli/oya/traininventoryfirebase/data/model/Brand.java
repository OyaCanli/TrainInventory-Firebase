package com.canli.oya.traininventoryfirebase.data.model;

import java.io.Serializable;

public class Brand implements Serializable {

    private String brandName;
    private String brandLogoUri;
    private String webUrl;

    public Brand() {
    }

    public Brand(String brandName, String brandLogoUri, String webUrl) {
        this.brandName = brandName;
        this.brandLogoUri = brandLogoUri;
        this.webUrl = webUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

}
