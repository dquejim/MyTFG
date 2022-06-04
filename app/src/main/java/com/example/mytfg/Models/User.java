package com.example.mytfg.Models;

public class User {
    private String name;
    private String password;
    private String number;
    private String adress;
    private String fav_food;

    public User(String name, String password, String number, String adress,String fav_food){
        this.name = name;
        this.password = password;
        this.number = number;
        this.adress = adress;
        this.fav_food = fav_food;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }

    public String getAdress() {
        return adress;
    }

    public String getFav_food(){
        return fav_food;
    }

    public void setFav_food(String fav_food) {
        this.fav_food = fav_food;
    }
}
