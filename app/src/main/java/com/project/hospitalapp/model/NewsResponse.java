package com.project.hospitalapp.model;

import java.util.List;

public class NewsResponse {

    private String result;
    private List<NewsItem> items;
    private String datetime;

    public String getResult() {
        return result;
    }

    public List<NewsItem> getItems() {
        return items;
    }

    public String getDatetime() {
        return datetime;
    }
}
