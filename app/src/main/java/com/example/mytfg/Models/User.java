package com.example.mytfg.Models;

public class User {
    private String name;
    private String password;
    private String number;
    private String adress;

    public User(String name, String password, String number, String adress){
        this.name = name;
        this.password = password;
        this.number = number;
        this.adress = adress;
    }

    public User() {
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


}
