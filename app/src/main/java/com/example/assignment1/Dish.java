package com.example.assignment1;

public class Dish {
    private int id;
    private String name, type, ingredients;
    private double price;

    public Dish(int id, String name, String type, String ingredients, double price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ingredients = ingredients;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIngredients() {
        return ingredients;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String n) {
        name = n;
    }

    public void setType(String t) {
        type = t;
    }

    public void setIngredients(String i) {
        ingredients = i;
    }

    public void setPrice(double p) {
        price = p;
    }
}