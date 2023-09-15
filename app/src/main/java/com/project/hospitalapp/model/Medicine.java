package com.project.hospitalapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Medicine implements Serializable {

    public int id;
    public String medicineName;
    public String startMedicine;
    public String endMedicine;
    private ArrayList<String> foodTag;
    private ArrayList<String> alarm;

    public Medicine(int id, String medicineName, String startMedicine, String endMedicine, ArrayList<String> foodTag, ArrayList<String> alarm) {
        this.id = id;
        this.medicineName = medicineName;
        this.startMedicine = startMedicine;
        this.endMedicine = endMedicine;
        this.foodTag = foodTag;
        this.alarm = alarm;
    }

    public Medicine(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getStartMedicine() {
        return startMedicine;
    }

    public void setStartMedicine(String startMedicine) {
        this.startMedicine = startMedicine;
    }

    public String getEndMedicine() {
        return endMedicine;
    }

    public void setEndMedicine(String endMedicine) {
        this.endMedicine = endMedicine;
    }

    public ArrayList<String> getFoodTag() {
        return foodTag;
    }

    public void setFoodTag(ArrayList<String> foodTag) {
        this.foodTag = foodTag;
    }

    public ArrayList<String> getAlarm() {
        return alarm;
    }

    public void setAlarm(ArrayList<String> alarm) {
        this.alarm = alarm;
    }
}
