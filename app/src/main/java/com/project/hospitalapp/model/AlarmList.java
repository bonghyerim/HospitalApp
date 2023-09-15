package com.project.hospitalapp.model;

import java.util.ArrayList;

public class AlarmList {

    private String result;
    private int count;
    private ArrayList<Alarm> items;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Alarm> getItems() {
        return items;
    }

    public void setItems(ArrayList<Alarm> items) {
        this.items = items;
    }
}
