package com.example.darshank.news_gateway;

import java.io.Serializable;


public class NewsSources implements Serializable {
    String sId;
    String sUrl;
    String sName;
    String sCategory;
    public void setsId(String sId) {
        this.sId = sId;
    }
    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }
    public String getsName() {
        return sName;
    }
    public void setsName(String sName) {
        this.sName = sName;
    }
    public String getsCategory() {
        return sCategory;
    }
    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }
}

