package com.example.darshank.news_gateway;
import java.io.Serializable;
import java.util.ArrayList;

public class FrameLayout implements Serializable {


    private ArrayList<NewsSources> sourceList = new ArrayList<NewsSources>();
    private ArrayList<IndividualArticle> articleList = new ArrayList <IndividualArticle>();
    private ArrayList<String> categories = new ArrayList <String>();
    private int currentSource;
    private int currentArticle;
    public ArrayList <NewsSources> getSourceList() {
        return sourceList;
    }
    public void setSourceList(ArrayList <NewsSources> sourceList) {
        this.sourceList = sourceList;
    }
    public void setArticleList(ArrayList <IndividualArticle> articleList) {
        this.articleList = articleList;
    }
    public ArrayList <String> getCategories() {
        return categories;
    }
    public void setCategories(ArrayList <String> categories) {
        this.categories = categories;
    }

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }

    public void setCurrentArticle(int currentArticle) {
        this.currentArticle = currentArticle;
    }
}


