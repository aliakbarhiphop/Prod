package com.aliakbar.android.prod.data;

public class Stock {
    private int _id;
    private String itemname;
    private int price;
    private int orders;
    private int sales;

    Stock(int id, String name, int pri, int ord, int sal) {
        _id = id;
        itemname = name;
        price = pri;
        orders = ord;
        sales = sal;
    }

    public int getId() {
        return _id;
    }

    public String getItemname() {
        return itemname;
    }

    public int getPrice() {
        return price;
    }

    public int getOrder() {
        return orders;
    }

    public int getSales() {
        return sales;
    }
}
