package com.aliakbar.android.prod.data;

public class Staff {
    private int _id;

    private String username;
    private String name;
    private String password;
    private int client;
    private int sales;
    private int total;
    private int paid;
    private int credit;
    private int order;

    Staff(int mId, String mName, String mUsername, int mClient, int mTotal, int mOrder, int mSales) {
        _id = mId;
        name = mName;
        username = mUsername;
        client = mClient;
        total = mTotal;
        order = mOrder;
        sales = mSales;
    }

    Staff(int mId, String mName, String mUsername, String mPassword, int mClient, int mTotal, int mPaid, int mCredit, int mOrder, int mSales) {
        _id = mId;
        name = mName;
        username = mUsername;
        client = mClient;
        total = mTotal;
        order = mOrder;
        sales = mSales;
        password = mPassword;
        paid = mPaid;
        credit = mCredit;
    }

    public int getCredit() {
        return credit;
    }

    public int getPaid() {
        return paid;
    }

    public int getTotal() {
        return total;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return _id;
    }

    public int getClient() {
        return client;
    }

    public int getOrder() {
        return order;
    }

    public int getSales() {
        return sales;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}
