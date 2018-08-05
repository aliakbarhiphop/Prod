package com.aliakbar.android.prod.data;

public class Customer {
    private int _id;
    private String managerName;
    private String name;
    private int deals;
    private int paid;
    private int credit;
    private int total;
    private int orders;

    Customer(int id, String mManagerName, String mName, int mDeals, int mTotal, int mPaid, int mCredit, int mOrder) {
        _id = id;
        managerName = mManagerName;
        name = mName;
        deals = mDeals;
        paid = mPaid;
        credit = mCredit;
        total = mTotal;
        orders = mOrder;
    }

    public int getId() {
        return _id;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getName() {
        return name;
    }

    public int getDeals() {
        return deals;
    }

    public int getTotal() {
        return total;
    }

    public int getPaid() {
        return paid;
    }

    public int getCredit() {
        return credit;
    }

    public int getOrders() {
        return orders;
    }
}
