package com.aliakbar.android.prod.data;

public class Cart {
    private int _id;
    private int itemId;
    private String itemName;
    private int price;
    private int quantity;
    private int total;
    private int discountPercentage;
    private int pay;

    public Cart(int id,int itemid, String itemname, int mPrice, int mQuantity, int mTotal, int disc, int mPay) {
        _id = id;
        itemName = itemname;
        itemId=itemid;
        price = mPrice;
        quantity = mQuantity;
        total = mTotal;
        discountPercentage = disc;
        pay = mPay;
    }

    public int get_id() {
        return _id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotal() {
        return total;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public int getPay() {
        return pay;
    }

    public int getItemId() {
        return itemId;
    }
}
