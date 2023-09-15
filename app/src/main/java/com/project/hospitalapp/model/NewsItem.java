package com.project.hospitalapp.model;

import java.io.Serializable;
import java.util.List;

public class NewsItem implements Serializable {
    private String title;
    private String summary;
    private String img;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }
}

