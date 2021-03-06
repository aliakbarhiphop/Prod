package com.aliakbar.android.prod.sales;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.data.Sales;
import com.aliakbar.android.prod.network.VolleyHelper;
import com.aliakbar.android.prod.network.VolleyRequest;
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

public class SalesActivity extends AppCompatActivity implements SalesAdapter.SalesAdapterOnClickListener {
    List<Sales> salesList;
    int sizeOfSalesItem, trackSalesIncrement = 0;

    SalesAdapter salesAdapter;
    private RecyclerView recyclerView;
    RequestQueue salesRequestQueue;
    FrameLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        emptyView = findViewById(R.id.sales_empty_view);

        recyclerView = findViewById(R.id.sales_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        salesAdapter = new SalesAdapter(this, this);
        recyclerView.setAdapter(salesAdapter);
        salesRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySales();
    }

    void displaySales() {
        String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Bill.TABLE_NAME
                + " ON " + ProdContract.Bill.FULL_COLUMN_CUSTOMER_ID + " = " + ProdContract.Customer.FULL_COLUMN_ID
                + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Staff.FULL_COLUMN_ID + " = " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID;
        String[] projection = new String[]{ProdContract.Bill.FULL_COLUMN_ID, ProdContract.Bill.FULL_COLUMN_DATE
                , ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Bill.FULL_COLUMN_TOTAL
                , ProdContract.Bill.COLUMN_DISCOUNT_PER, ProdContract.Bill.FULL_COLUMN_PAY};

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(tableName
                , projection, null, null, ProdContract.Bill.FULL_COLUMN_CUSTOMER_ID + " DESC")
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        extractSales(jsonObject.getJSONArray("cursor"), SalesActivity.this);
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(SalesActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SalesActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(SalesActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(SalesActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(SalesActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        salesRequestQueue.add(volleyRequest);
    }

    void extractSales(JSONArray salesArray, Context context) throws JSONException {
        Sales sales;
        salesList = new ArrayList<>();
        sizeOfSalesItem = salesArray.length();
        for (int i = 0; i < sizeOfSalesItem; i++) {
            JSONObject obj = salesArray.getJSONObject(i);
            selectItemForSales(new Sales(null, obj.getInt(ProdContract.Bill.FULL_COLUMN_ID), obj.getString(ProdContract.Bill.FULL_COLUMN_DATE)
                    , obj.getString(ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME), obj.getString(ProdContract.Staff.FULL_COLUMN_STAFF_NAME)
                    , obj.getInt(ProdContract.Bill.FULL_COLUMN_TOTAL), obj.getInt(ProdContract.Bill.COLUMN_DISCOUNT_PER)
                    , obj.getInt(ProdContract.Bill.FULL_COLUMN_PAY)), context);
        }
    }

    void selectItemForSales(final Sales sales, final Context context) {
        RequestQueue SalesForItemQueue = Volley.newRequestQueue(context);
        String tableName = ProdContract.Stock.TABLE_NAME + " INNER JOIN " + ProdContract.Cart.TABLE_NAME
                + " ON " + ProdContract.Stock.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_ITEM_ID;
        String[] projection = new String[]{ProdContract.Stock.FULL_COLUMN_ID, ProdContract.Stock.FULL_COLUMN_ITEM_NAME
                , ProdContract.Stock.FULL_COLUMN_PRICE, ProdContract.Cart.FULL_COLUMN_QUANTITY, ProdContract.Cart.FULL_COLUMN_TOTAL
                , ProdContract.Cart.FULL_COLUMN_DISCOUNT_PER, ProdContract.Cart.FULL_COLUMN_PAY};
        String selection = ProdContract.Cart.FULL_COLUMN_BILL_ID;
        String[] selectionArg = new String[]{"" + sales.getBillId()};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(tableName
                , projection
                , selection, selectionArg, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jArray = jsonObject.getJSONArray("cursor");
                        List<SalesItem> salesItem = new ArrayList<>();
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject obj = jArray.getJSONObject(i);

                            salesItem.add(new SalesItem(obj.getInt(ProdContract.Stock.FULL_COLUMN_ID)
                                    , obj.getString(ProdContract.Stock.FULL_COLUMN_ITEM_NAME), obj.getInt(ProdContract.Stock.FULL_COLUMN_PRICE)
                                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_QUANTITY), obj.getInt(ProdContract.Cart.FULL_COLUMN_TOTAL)
                                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_DISCOUNT_PER)
                                    , obj.getInt(ProdContract.Cart.FULL_COLUMN_PAY)));
                        }
                        salesList.add(new Sales(salesItem, sales.getBillId(), sales.getDate(), sales.getName(), sales.getStaff(), sales.getTotal()
                                , sales.getDisc(), sales.getPay()));
                        trackSalesIncrement++;
                        if (sizeOfSalesItem == trackSalesIncrement) {
                            trackSalesIncrement = 0;
                            emptyView.setVisibility(View.GONE);
                            salesAdapter.swapCursor(salesList);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(context, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(context, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        SalesForItemQueue.add(volleyRequest);
    }

    @Override
    public void onClick(String mCurrentItemId) {
        //TODO
    }
}
