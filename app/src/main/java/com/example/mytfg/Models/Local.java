package com.example.mytfg.Models;

public class Local {
    private String id;
    private String ubication;
    private String facebook_link;
    private String instagram_link;
    private String adress;
    private String number;

    public Local(String id, String ubication, String facebook_link, String instagram_link, String adress, String number){
        this.id = id;
        this.ubication = ubication;
        this.facebook_link = facebook_link;
        this.instagram_link = instagram_link;
        this.adress = adress;
        this.number = number;
    }

    public String getId() {
        return this.id;
    }

    public String getUbicationLink() {
        return this.ubication;
    }

    public String getInstagram_link() {
        return this.instagram_link;
    }

    public String getFacebook_link() {
        return this.facebook_link;
    }

    public String getAdress() {
        return this.adress;
    }

    public String getNumber(){ return this.number; }

}

