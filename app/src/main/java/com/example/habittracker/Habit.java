package com.example.habittracker;

public class Habit {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String type;
    private String frequency;
    private int count;

    public Habit(int userId, String title, String description, String type, String frequency, int count) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.frequency = frequency;
        this.count = count;
    }

    public Habit(int id, int userId, String title, String description, String type, String frequency, int count) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.frequency = frequency;
        this.count = count;
    }

    public int getId(){
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", frequency='" + frequency + '\'' +
                ", count=" + count +
                '}';
    }
}