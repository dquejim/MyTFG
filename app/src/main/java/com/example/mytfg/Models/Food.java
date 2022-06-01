package com.example.mytfg.Models;

public class Food {
    private String number;
    private String category;
    private String product;
    private String price;

    public Food(String number, String category, String product, String price){
        this.number = number;
        this.category = category;
        this.product = product;
        this.price = price;
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


}
