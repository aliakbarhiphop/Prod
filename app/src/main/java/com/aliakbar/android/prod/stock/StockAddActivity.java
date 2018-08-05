package com.aliakbar.android.prod.stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.InsertMysql;
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

import java.util.ArrayList;
import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractStockJson;

public class StockAddActivity extends AppCompatActivity {

    public static boolean editMode = false;

    String idForUpdate;

    boolean saleFirstTime = true;
    boolean orderFirstTime = true;
    boolean priceFirstTime = true;

    boolean ediTextTouched = false;

    private EditText mNameAdd;
    private EditText mTotalSale;
    private EditText mPriceAdd;
    private EditText mOrderAdd;

    RequestQueue addStockRequestQueue, stockEditRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_add);

        ImageView backIcon = (ImageView) findViewById(R.id.add_stock_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ediTextTouched) {
                    finish();
                    return;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
            }
        });

        mNameAdd = (EditText) findViewById(R.id.add_stock_item_name);
        mNameAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ediTextTouched = true;
                return false;
            }
        });

        mTotalSale = (EditText) findViewById(R.id.add_stock_sale);

        mPriceAdd = (EditText) findViewById(R.id.add_stock_price);

        mOrderAdd = (EditText) findViewById(R.id.add_stock_order);

        if (!editMode) {

            mOrderAdd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (orderFirstTime) {
                        orderFirstTime = false;
                        mOrderAdd.setText("");
                    }
                    return false;
                }
            });

            mPriceAdd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (priceFirstTime) {
                        priceFirstTime = false;
                        mPriceAdd.setText("");
                    }
                    return false;
                }
            });

            mTotalSale.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (saleFirstTime) {
                        saleFirstTime = false;
                        mTotalSale.setText("");
                    }
                    return false;
                }
            });

        } else {
            mOrderAdd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            mPriceAdd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            mTotalSale.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
        }


        ImageView saveIcon = (ImageView) findViewById(R.id.add_stock_save);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, price, totSales, order;
                name = mNameAdd.getText().toString().trim();
                price = mPriceAdd.getText().toString().trim();
                totSales = mTotalSale.getText().toString().trim();
                order = mOrderAdd.getText().toString().trim();
                if (name.matches("") || price.matches("") || totSales.matches("") || order.matches("")) {
                    Toast toast = Toast.makeText(StockAddActivity.this, "Some Fields Are Empty", Toast.LENGTH_SHORT);
                    if (toast.getView().isShown()) {
                        toast.cancel();
                    }
                    toast.show();
                } else {
                    List<InsertMysql> values = new ArrayList<>();
                    values.add(new InsertMysql(ProdContract.Stock.COLUMN_ITEM_NAME, name));
                    values.add(new InsertMysql(ProdContract.Stock.COLUMN_PRICE, price));
                    values.add(new InsertMysql(ProdContract.Stock.COLUMN_ORDER, order));
                    values.add(new InsertMysql(ProdContract.Stock.COLUMN_SALES, totSales));
                    values.add(new InsertMysql(ProdContract.Stock.COLUMN_ENABLED, "0"));
                    if (editMode) {
                        updateStock(values);
                    } else {
                        saveItem(values);
                    }
                }
            }
        });
        addStockRequestQueue = Volley.newRequestQueue(StockAddActivity.this);
        stockEditRequestQueue = Volley.newRequestQueue(StockAddActivity.this);

        if (editMode) {
            Intent extractIdIntent = getIntent();
            String idFromIntent = extractIdIntent.getStringExtra("selectedStockId");
            idForUpdate = idFromIntent;
            setUpEditMode(idFromIntent);
        }
    }


    @Override
    public void onBackPressed() {
        if (!ediTextTouched) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    void saveItem(List<InsertMysql> insert) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.insertVolly(ProdContract.Stock.TABLE_NAME, insert)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StockAddActivity.this, "Saved Successfully. Id :" + jsonObject.getInt("idOfLastInsertedRaw")
                                , Toast.LENGTH_SHORT).show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("INSERT ERROR")) {
                            Toast.makeText(StockAddActivity.this, "Failed To Insert", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        addStockRequestQueue.add(volleyRequest);
    }

    void setUpEditMode(String idEdit) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idEdit};
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
                        mNameAdd.setText(name);

                        String price = "" + stock.getPrice();
                        mPriceAdd.setText(price);

                        String order = "" + stock.getOrder();
                        mOrderAdd.setText(order);

                        String sales = "" + stock.getSales();
                        mTotalSale.setText(sales);

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            Toast.makeText(StockAddActivity.this, "Database Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        stockEditRequestQueue.add(volleyRequest);
    }

    void updateStock(List<InsertMysql> insert) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idForUpdate};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Stock.TABLE_NAME, insert, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StockAddActivity.this, "Update Successfully. Affected :" + jsonObject.getInt("affectedRaw")
                                        + " Raw"
                                , Toast.LENGTH_SHORT).show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            Toast.makeText(StockAddActivity.this, "Failed To Update", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        addStockRequestQueue.add(volleyRequest);
    }
}
