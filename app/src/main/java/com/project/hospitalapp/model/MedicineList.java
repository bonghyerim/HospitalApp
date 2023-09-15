package com.project.hospitalapp.model;

import java.util.ArrayList;

public class MedicineList {

    public ArrayList<Medicine> items;

    public int count;


    public ArrayList<Medicine> getItems() {
        return items;
    }

    public void setItems(ArrayList<Medicine> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
