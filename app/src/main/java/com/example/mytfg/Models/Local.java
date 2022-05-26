package com.example.mytfg.Models;

public class Local {
    private String id;
    private String ubication;
    private String adress;

    public Local(String id, String ubication, String adress){
        this.id = id;
        this.ubication = ubication;
        this.adress = adress;
    }

    public String getId() {
        return id;
    }

    public String getUbicationLink() {
        return ubication;
    }

    public String getAdress() {
        return adress;
    }
}

