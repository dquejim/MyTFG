package com.example.mytfg.Models;

import java.io.Serializable;

public class Product implements Serializable {
    private String number;
    private String category;
    private String product;
    private String price;
    private String description;

    public Product(String number, String category, String product, String price, String description){
        this.number = number;
        this.category = category;
        this.product = product;
        this.price = price;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public String getCategory() {
        return category;
    }

    public String getProduct() {
        return product;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription(){
        return this.description;
    }
}
