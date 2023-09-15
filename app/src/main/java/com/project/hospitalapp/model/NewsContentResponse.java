package com.project.hospitalapp.model;

import com.google.gson.annotations.SerializedName;

public class NewsContentResponse {
    @SerializedName("result")
    private String result;

    @SerializedName("img_summary")
    private String imgSummary;

    @SerializedName("content")
    private String content;

    @SerializedName("datetime")
    private String datetime;

    public String getResult() {
        return result;
    }

    public String getImgSummary() {
        return imgSummary;
    }

    public String getContent() {
        return content;
    }

    public String getDatetime() {
        return datetime;
    }
}
