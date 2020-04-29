package com.example.darshank.news_gateway;

import java.io.Serializable;
public class NewsSourcesOnNav implements Serializable {

    private String name;
    private String category;
    public NewsSourcesOnNav(String name, String category) {
        this.name = name;
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
}
