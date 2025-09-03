package com.example.assignment1;

public class Order {
    private int id;
    private String diningOption, tableNumber, dishNames;
    private Double totalPrice;

    public Order(int id, String dining, String table, String dishNames, Double total) {
        this.id = id; this.diningOption = dining;
        this.tableNumber = table; this.dishNames = dishNames; this.totalPrice = total;
    }

    public int getId() { return id; }
    public String getDiningOption() { return diningOption; }
    public String getTableNumber() { return tableNumber; }
    public String getDishNames() { return dishNames; }
    public Double getTotalPrice() { return totalPrice; }

    public void setDiningOption(String d) { diningOption = d; }
    public void setTableNumber(String t) { tableNumber = t; }
    public void setDishNames(String d) { dishNames = d; }
    public void setTotalPrice(Double p) { totalPrice = p; }
}