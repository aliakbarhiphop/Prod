package com.aliakbar.android.prod.stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.data.Stock;
import com.aliakbar.android.prod.network.VolleyHelper;
import com.aliakbar.android.prod.network.VolleyRequest;
import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractStockJson;

public class StockDetailActivity extends AppCompatActivity {
    TextView idTextView, nameTextView, priceTextView, orderTextView, salesTextView;

    String idFromIntent;

    RequestQueue stockDetailRequestQueue, deleteStockRequestQueue;

    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent extractIdIntent = getIntent();
        idFromIntent = extractIdIntent.getStringExtra("selectedStockId");

        stockDetailRequestQueue = Volley.newRequestQueue(StockDetailActivity.this);
        deleteStockRequestQueue = Volley.newRequestQueue(StockDetailActivity.this);

        idTextView = (TextView) findViewById(R.id.stock_detail_id);
        nameTextView = (TextView) findViewById(R.id.stock_detail_name);
        priceTextView = (TextView) findViewById(R.id.stock_detail_price);
        orderTextView = (TextView) findViewById(R.id.stock_detail_order);
        salesTextView = (TextView) findViewById(R.id.stock_detail_sales);

        backIcon = (ImageView) findViewById(R.id.stock_detail_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FrameLayout deleteFrame = (FrameLayout) findViewById(R.id.stock_detail_delete);
        deleteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFunction();
            }
        });

        FrameLayout editFrame = (FrameLayout) findViewById(R.id.stock_detail_edit);
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(StockDetailActivity.this, StockAddActivity.class);
                StockAddActivity.editMode = true;
                detailIntent.putExtra("selectedStockId", idFromIntent);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StockAddActivity.editMode) {
            StockAddActivity.editMode = false;
        }
        setStock();
    }

    void setStock() {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idFromIntent};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Stock.TABLE_NAME
                , null
                , selection, selectionArgs, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Stock> stockListFromServer = extractStockJson(jsonObject.getJSONArray("cursor"));

                        Stock stock = stockListFromServer.get(0);

                        String name = stock.getItemname();
                        nameTextView.setText(name);

                        String id = "" + stock.getId();
                        idTextView.setText(id);

                        String price = "" + stock.getPrice();
                        priceTextView.setText(price);

                        String order = "" + stock.getOrder();
                        orderTextView.setText(order);

                        String sales = "" + stock.getSales();
                        salesTextView.setText(sales);

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            Toast.makeText(StockDetailActivity.this, "Database Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        stockDetailRequestQueue.add(volleyRequest);
    }

    void deleteFunction() {
        DialogInterface.OnClickListener yesButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tableName = ProdContract.Stock.TABLE_NAME;
                        String selection = ProdContract.Stock._ID + "=?";
                        String[] selectionArgs = new String[]{idFromIntent};
                        deleteStock(tableName, selection, selectionArgs);
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(StockDetailActivity.this);
        builder.setMessage("Are You Sure,Do You Want To Delete");
        builder.setPositiveButton("Yes", yesButtonClickListener);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void deleteStock(String tableName, String selection, String[] selectionArgs) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.deleteVolly(tableName, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StockDetailActivity.this, "Deleted Successfully.Deleted " + jsonObject.getInt("affectedRaw")
                                        + " Item"
                                , Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("DELETE ERROR")) {
                            Toast.makeText(StockDetailActivity.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        deleteStockRequestQueue.add(volleyRequest);
    }
}
