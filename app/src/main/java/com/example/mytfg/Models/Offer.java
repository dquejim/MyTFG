package com.example.mytfg.Models;

public class Offer {
    private String id;
    private String name;
    private String day;
    private String price;

    public Offer(String id, String name, String day, String price){
        this.id = id;
        this.name = name;
        this.day = day;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDay() {
        return day;
    }

    public String getPrice() {
        return price;
    }
}
