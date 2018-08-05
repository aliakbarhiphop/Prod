package com.aliakbar.android.prod.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.InsertMysql;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.network.VolleyHelper;
import com.aliakbar.android.prod.network.VolleyRequest;
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

public class AddCart extends AppCompatActivity {
    RequestQueue AddCartQueue;

    public static boolean editMode = false;

    public static String FROM_STOCK_ID = "", FROM_STOCK_NAME = "", FROM_STOCK_PRICE = "";

    private Toast toast;

    static int quantity, itemPrice, total, discPer, disc, pay;

    static String billId, itemId, itemName;

    EditText itemNameText, priceText, quantityText, totalText, discText, discPerText, payText;

    boolean DISC_PER_FLAG = false, DISC_FLAG = false, PAY_FLAG = false;

    int SAVE_WARNING_NUMBER = 0;

    String idFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cart);

        AddCartQueue = Volley.newRequestQueue(AddCart.this);

        itemNameText = findViewById(R.id.add_cart_itemname);
        priceText = findViewById(R.id.add_cart_price);
        quantityText = findViewById(R.id.add_cart_quantity);
        totalText = findViewById(R.id.add_cart_total);

        discText = findViewById(R.id.add_cart_discount);
        discText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (payText.getText().toString().matches("")) {
                    payText.setText("" + pay);
                    payText.setError(null);
                }
                if (discPerText.getText().toString().matches("")) {
                    discPerText.setText("" + discPer);
                    discPerText.setError(null);
                }
                setAllFlagsToFalse();
                DISC_FLAG = true;
                return false;
            }
        });
        discText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (DISC_FLAG) {
                    if (!charSequence.toString().matches("")) {
                        try {
                            int Fdisc;
                            Fdisc = Integer.parseInt(charSequence.toString());
                            if (Fdisc <= total && Fdisc >= 0) {
                                removeError();
                                disc = Fdisc;
                                pay = total - disc;

                                discPer = 100 - (int) (((double) pay / (double) total) * 100.0);

                                discPerText.setText("" + discPer);
                                payText.setText("" + pay);
                            } else {
                                discText.setError("Entered Value is Not valid");
                            }

                        } catch (NumberFormatException e) {
                            Log.i("", " is not a number");
                        }
                    } else {
                        discText.setError("This Field Must Not Be Empty");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        discPerText = findViewById(R.id.add_cart_discount_per);
        discPerText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (discText.getText().toString().matches("")) {
                    discText.setText("" + disc);
                    discText.setError(null);
                }
                if (payText.getText().toString().matches("")) {
                    payText.setText("" + pay);
                    payText.setError(null);
                }
                setAllFlagsToFalse();
                DISC_PER_FLAG = true;
                return false;
            }
        });
        discPerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (DISC_PER_FLAG) {
                    if (!charSequence.toString().matches("")) {
                        try {
                            int FdiscPer;
                            FdiscPer = Integer.parseInt(charSequence.toString());
                            if (FdiscPer <= 100 && FdiscPer >= 0) {
                                removeError();

                                int afterDiscountPercentage = 100 - FdiscPer;
                                pay = (int) (((double) afterDiscountPercentage / 100.0) * (double) total);

                                disc = total - pay;
                                discPer = FdiscPer;
                                discText.setText("" + disc);
                                payText.setText("" + pay);
                            } else {
                                discPerText.setError("Entered Value is Not valid");
                            }
                        } catch (NumberFormatException e) {
                            Log.i("", " is not a number");
                        }
                    } else {
                        discPerText.setError("This Field Must Not Be Empty");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        payText = findViewById(R.id.add_cart_pay);
        payText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (discText.getText().toString().matches("")) {
                    discText.setText("" + disc);
                    discText.setError(null);
                }
                if (discPerText.getText().toString().matches("")) {
                    discPerText.setText("" + discPer);
                    discPerText.setError(null);
                }
                setAllFlagsToFalse();
                PAY_FLAG = true;
                return false;
            }
        });
        payText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(AddCart.this, "CARAAAAAP IS OKAY", Toast.LENGTH_LONG).show();
                if (PAY_FLAG) {
                    if (!charSequence.toString().matches("")) {
                        try {
                            int Fpay;
                            Fpay = Integer.parseInt(charSequence.toString());
                            if (Fpay <= total && Fpay >= 0) {
                                removeError();
                                pay = Fpay;
                                disc = total - pay;

                                discPer = 100 - (int) (((double) pay / (double) total) * 100.0);

                                discText.setText("" + disc);
                                discPerText.setText("" + discPer);

                            } else {
                                payText.setError("Entered Value is Not valid");
                            }
                        } catch (NumberFormatException e) {
                            Log.i("", " is not a number");
                        }
                    } else {
                        payText.setError("This Field Must Not Be Empty");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        quantityText.setText("" + quantity);
        quantityText.requestFocus();

        Button posQuantity = findViewById(R.id.add_cart_positive_quantity);
        posQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (itemId.matches("") || itemName.matches("")) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Select An Item", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (quantity == 100) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Quantity Reached 100", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    removeError();
                    setAllFlagsToFalse();
                    quantity++;
                    total = itemPrice * quantity;
                    discPer = 0;
                    disc = 0;
                    pay = total;

                    quantityText.setText("" + quantity);
                    totalText.setText("" + total);
                    discText.setText("" + disc);
                    discPerText.setText("" + discPer);
                    payText.setText("" + pay);
                }
            }
        });

        final Button negQuantity = findViewById(R.id.add_cart_negetive_quantity);
        negQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemId.matches("") || itemName.matches("")) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Select An Item", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (quantity == 1) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Quantity Can't Be Less Than 1", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    removeError();
                    setAllFlagsToFalse();
                    quantity--;
                    total = itemPrice * quantity;
                    discPer = 0;
                    disc = 0;
                    pay = total;

                    quantityText.setText("" + quantity);
                    totalText.setText("" + total);
                    discText.setText("" + disc);
                    discPerText.setText("" + discPer);
                    payText.setText("" + pay);
                }
            }
        });

        FrameLayout addNewStock = findViewById(R.id.add_cart_stock_picker);
        addNewStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FROM_STOCK_ID = "";
                FROM_STOCK_NAME = "";
                FROM_STOCK_PRICE = "";
                Intent intent = new Intent(AddCart.this, StockActivity.class);
                StockActivity.CART_FLAG = true;
                startActivity(intent);
            }
        });

        ImageView saveCart = findViewById(R.id.add_cart_save);
        saveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TitemName, TitemPrice, TitemQuantity, TitemTotal, TitemDis, TitemDisPer, TitemPay;
                TitemName = itemNameText.getText().toString();
                TitemPrice = priceText.getText().toString();
                TitemQuantity = quantityText.getText().toString();
                TitemTotal = totalText.getText().toString();
                TitemDis = discText.getText().toString();
                TitemDisPer = discPerText.getText().toString();
                TitemPay = payText.getText().toString();
                if (TitemName.matches("") || TitemPrice.matches("") || TitemQuantity.matches("") || TitemTotal.matches("")
                        || TitemDis.matches("") || TitemDisPer.matches("") || TitemPay.matches("")) {

                    SAVE_WARNING_NUMBER++;
                    if (SAVE_WARNING_NUMBER == 2) {
                        SAVE_WARNING_NUMBER = 0;
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(AddCart.this, "Every Field Must Be Fill", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    if (itemName.matches(TitemName) && TitemPrice.matches("" + itemPrice) && TitemQuantity.matches("" + quantity)
                            && TitemTotal.matches("" + total) && TitemDis.matches("" + disc) && TitemDisPer.matches("" + discPer)
                            && TitemPay.matches("" + pay)) {


                        setAllFlagsToFalse();
                        PAY_FLAG = true;

                        Toast.makeText(AddCart.this, "START CARAAAAAP", Toast.LENGTH_LONG).show();
                        payText.setText("" + pay);
                        Toast.makeText(AddCart.this, "END CARAAAAAP", Toast.LENGTH_LONG).show();

                        List<InsertMysql> insert = new ArrayList<>();
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_BILL_ID, billId));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_ITEM_ID, itemId));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_QUANTITY, TitemQuantity));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_TOTAL, TitemTotal));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT, TitemDis));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT_PER, TitemDisPer));
                        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_PAY, TitemPay));
                        if (editMode) {
                            updateCart(insert);
                        } else {
                            saveItemToCart(insert);
                        }
                    }
                }
            }
        });

        ImageView backIcon = findViewById(R.id.add_cart_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (editMode) {
            Intent extractIdIntent = getIntent();
            idFromIntent = extractIdIntent.getStringExtra("idOfSelectedItem");
            initialiseAddCartValues();
            setUpEditMode();

        } else {
            Intent extractIdIntent = getIntent();
            billId = extractIdIntent.getStringExtra("BILL_ID");
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StockActivity.CART_FLAG) {
            StockActivity.CART_FLAG = false;
            fromStockActivity();
        }
        if (!itemName.matches("") || itemPrice != 0) {
            discPerText.setEnabled(true);
            discText.setEnabled(true);
            payText.setEnabled(true);
        }
    }

    void setUpEditMode() {
        String selection = ProdContract.Cart.FULL_COLUMN_ID + "=?";
        String selectionArgs[] = new String[]{idFromIntent};

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Cart.TABLE_NAME + " INNER JOIN " + ProdContract.Stock.TABLE_NAME + " ON "
                        + ProdContract.Stock.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_ITEM_ID
                , new String[]{ProdContract.Cart.FULL_COLUMN_BILL_ID,
                        ProdContract.Stock.FULL_COLUMN_ID, ProdContract.Stock.FULL_COLUMN_ITEM_NAME, ProdContract.Stock.FULL_COLUMN_PRICE}
                , selection, selectionArgs, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        FROM_STOCK_ID = "" + obj.getInt(ProdContract.Stock.FULL_COLUMN_ID);
                        FROM_STOCK_NAME = obj.getString(ProdContract.Stock.FULL_COLUMN_ITEM_NAME);
                        FROM_STOCK_PRICE = "" + obj.getInt(ProdContract.Stock.FULL_COLUMN_PRICE);
                        billId = "" + obj.getInt(ProdContract.Cart.FULL_COLUMN_BILL_ID);
                        setItemDetail();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            toast = Toast.makeText(AddCart.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            toast = Toast.makeText(AddCart.this, "Database Empty", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    toast = Toast.makeText(AddCart.this, "Bad Response From Server", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        AddCartQueue.add(volleyRequest);
    }

    void fromStockActivity() {
        if (FROM_STOCK_PRICE.matches("") || FROM_STOCK_NAME.matches("") || FROM_STOCK_ID.matches("")) {
            return;
        }
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Cart.TABLE_NAME
                , new String[]{ProdContract.Cart._ID}
                , ProdContract.Cart.COLUMN_ITEM_ID + " = ? AND " + ProdContract.Cart.COLUMN_BILL_ID + " = ?", new String[]{FROM_STOCK_ID, billId}
                , null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(AddCart.this, "This Item Is Already Added", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(AddCart.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            setItemDetail();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddCart.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(AddCart.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(AddCart.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(AddCart.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        AddCartQueue.add(volleyRequest);
    }

    void setItemDetail() {
        setAllFlagsToFalse();
        removeError();

        itemId = FROM_STOCK_ID;

        itemName = FROM_STOCK_NAME;
        itemNameText.setText(itemName);

        itemPrice = Integer.parseInt(FROM_STOCK_PRICE);
        priceText.setText("" + itemPrice);

        quantity = 1;
        quantityText.setText("" + quantity);

        total = itemPrice * quantity;
        totalText.setText("" + total);

        discPer = 0;
        discPerText.setText("" + discPer);
        discPerText.setEnabled(true);

        disc = 0;
        discText.setText("" + disc);
        discText.setEnabled(true);

        pay = total;
        payText.setText("" + pay);
        payText.setEnabled(true);
    }

    public static void initialiseAddCartValues() {
        quantity = 1;
        itemPrice = 0;
        total = 0;
        discPer = 0;
        disc = 0;
        pay = 0;
        billId = "";
        itemId = "";
        itemName = "";
    }

    void setAllFlagsToFalse() {
        DISC_FLAG = false;
        DISC_PER_FLAG = false;
        PAY_FLAG = false;
    }

    void removeError() {
        discPerText.setError(null);
        discText.setError(null);
        payText.setError(null);
    }

    void saveItemToCart(List<InsertMysql> values) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.insertVolly(ProdContract.Cart.TABLE_NAME, values)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(AddCart.this, "Item Added Successfully", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(AddCart.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("INSERT ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(AddCart.this, "Failed To Insert", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Bad Response From Server", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        AddCartQueue.add(volleyRequest);
    }

    void updateCart(List<InsertMysql> values) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idFromIntent};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Cart.TABLE_NAME, values, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(AddCart.this, "Update Successfully", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(AddCart.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(AddCart.this, "Failed To Update", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Bad Response From Server", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(AddCart.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        AddCartQueue.add(volleyRequest);
    }
}