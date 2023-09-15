package com.project.hospitalapp.model;

import java.io.Serializable;
import java.util.Calendar;

public class Alarm implements Serializable {


    public int generateUniqueId() {
        // 고유한 ID를 생성하여 반환하는 메소드
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        return (int) currentTimeMillis;
    }

//    public void setUniqueId() {
//        this.id = generateUniqueId();
//    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int year;
    public int month;
    public int day;
    public int hour;

    public String alarm;
    public int minute;
    public String content;

    public int alarmId;


    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }




    public Alarm(String content, String alarm) {
        this.content = content;
        this.alarm = alarm;
    }




//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

//    public Alarm(int year, int month, int day, int hour, int minute, String content, int id) {
//        this.year = year;
//        this.month = month;
//        this.day = day;
//        this.hour = hour;
//        this.minute = minute;
//        this.content = content;
//        this.id = id;
//    }

    public Alarm(int year, int month, int day, int hour, int minute, String content) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.content = content;
    }

    public Alarm(String content) {
        this.content = content;
    }

    public Alarm(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }


    public Alarm(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }




}
