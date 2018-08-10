package com.aliakbar.android.prod.data;

import com.aliakbar.android.prod.sales.SalesItem;

import java.util.List;

public class Sales {

    private List<SalesItem> salesItems;

    private int billId;
    private String date;
    private String name;
    private String staff;
    private int total;
    private int disc;
    private int pay;

    public Sales(List<SalesItem> salesItem, int bill, String dat, String nam, String staf, int tot, int dis, int payy) {
        salesItems = salesItem;
        billId = bill;
        date = dat;
        name = nam;
        staff = staf;
        total = tot;
        disc = dis;
        pay = payy;
    }

    public int getBillId() {
        return billId;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getStaff() {
        return staff;
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

    public List<SalesItem> getSalesItems() {
        return salesItems;
    }
}
