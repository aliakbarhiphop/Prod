package com.aliakbar.android.prod.data;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aliakbar.android.prod.network.VolleyHelper;
import com.aliakbar.android.prod.network.VolleyRequest;
import com.aliakbar.android.prod.sales.SalesActivity;
import com.aliakbar.android.prod.sales.SalesItem;
import com.aliakbar.android.prod.stock.StockActivity;
import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonExtract {
    //STOCK
    public static List<Stock> extractStockJson(JSONArray stockJson) throws JSONException {
        List<Stock> returnStockList = new ArrayList<>();
        for (int i = 0; i < stockJson.length(); i++) {
            JSONObject obj = stockJson.getJSONObject(i);

            returnStockList.add(new Stock(obj.getInt(ProdContract.Stock._ID), obj.getString(ProdContract.Stock.COLUMN_ITEM_NAME)
                    , obj.getInt(ProdContract.Stock.COLUMN_PRICE), obj.getInt(ProdContract.Stock.COLUMN_ORDER)
                    , obj.getInt(ProdContract.Stock.COLUMN_SALES)));

        }
        return returnStockList;
    }

    //CUSTOMER
    public static List<Customer> extractCustomerJson(JSONArray customerJson) throws JSONException {
        List<Customer> returnCustomerList = new ArrayList<>();
        for (int i = 0; i < customerJson.length(); i++) {
            JSONObject obj = customerJson.getJSONObject(i);

            returnCustomerList.add(new Customer(obj.getInt(ProdContract.Customer.FULL_COLUMN_ID)
                    , obj.getString(ProdContract.Staff.FULL_COLUMN_STAFF_NAME)
                    , obj.getString(ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME), obj.getInt(ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL)
                    , obj.getInt(ProdContract.Customer.FULL_COLUMN_TOTAL), obj.getInt(ProdContract.Customer.FULL_COLUMN_PAID)
                    , obj.getInt(ProdContract.Customer.FULL_COLUMN_CREDIT), obj.getInt(ProdContract.Customer.FULL_COLUMN_ORDER)));
        }
        return returnCustomerList;
    }

    //STAFF
    public static List<Staff> extractDisplayStaffJson(JSONArray staffJson) throws JSONException {
        List<Staff> returnStaffList = new ArrayList<>();
        for (int i = 0; i < staffJson.length(); i++) {
            JSONObject obj = staffJson.getJSONObject(i);

            returnStaffList.add(new Staff(obj.getInt(ProdContract.Staff._ID)
                    , obj.getString(ProdContract.Staff.COLUMN_STAFF_NAME)
                    , obj.getString(ProdContract.Staff.COLUMN_STAFF_USERNAME)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_CLIENT)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_TOTAL)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_ORDER)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_SALE)));
        }
        return returnStaffList;
    }

    public static List<Staff> extractStaffJson(JSONArray staffJson) throws JSONException {
        List<Staff> returnStaffList = new ArrayList<>();
        for (int i = 0; i < staffJson.length(); i++) {
            JSONObject obj = staffJson.getJSONObject(i);

            returnStaffList.add(new Staff(obj.getInt(ProdContract.Staff._ID)
                    , obj.getString(ProdContract.Staff.COLUMN_STAFF_NAME)
                    , obj.getString(ProdContract.Staff.COLUMN_STAFF_USERNAME)
                    , obj.getString(ProdContract.Staff.COLUMN_STAFF_PASSWORD)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_CLIENT)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_TOTAL)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_PAID)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_CREDIT)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_ORDER)
                    , obj.getInt(ProdContract.Staff.COLUMN_STAFF_SALE)));
        }
        return returnStaffList;
    }

    //BILL
    public static List<Cart> extractCartJson(JSONArray cartJson) throws JSONException {
        List<Cart> returnCartList = new ArrayList<>();

        for (int i = 0; i < cartJson.length(); i++) {
            JSONObject obj = cartJson.getJSONObject(i);

            returnCartList.add(new Cart(obj.getInt(ProdContract.Cart.FULL_COLUMN_ID)
                    , obj.getInt(ProdContract.Stock.FULL_COLUMN_ID)
                    , obj.getString(ProdContract.Stock.FULL_COLUMN_ITEM_NAME)
                    , obj.getInt(ProdContract.Stock.FULL_COLUMN_PRICE)
                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_QUANTITY)
                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_TOTAL)
                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_DISCOUNT_PER)
                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_PAY)));
        }
        return returnCartList;
    }
}
