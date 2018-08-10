package com.aliakbar.android.prod.sales;

public class SalesItem {
    private int _id;
    private String itemName;
    private int price;
    private int qty;
    private int total;
    private int disc;
    private int pay;

    public SalesItem(int id, String name, int pri, int qt, int tot, int dis, int payy) {
        _id = id;
        itemName = name;
        price = pri;
        qty = qt;
        total = tot;
        disc = dis;
        pay = payy;
    }

    public int getId() {
        return _id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public int getTotal() {
        return total;
    }

    public int getDisc() {
        return disc;
    }

    public int getPay() {
        return pay;
    }
}
