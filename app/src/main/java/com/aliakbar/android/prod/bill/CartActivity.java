package com.aliakbar.android.prod.bill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Cart;
import com.aliakbar.android.prod.data.InsertMysql;
import com.aliakbar.android.prod.data.JsonExtract;
import com.aliakbar.android.prod.data.ProdContract;
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
import java.util.HashMap;
import java.util.List;

import static com.aliakbar.android.prod.bill.BillActivity.SUCCESS_CHECKER;
import static com.aliakbar.android.prod.bill.BillActivity.VALUED_CHANGED;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterOnClickListener {
    Toast toast;
    String BILL_ID = "";
    String TOTAL = "";
    String PAY = "";

    LinearLayout emptyView;

    RequestQueue cartRequestQueue;
    ImageView moveNext;


    ImageView addNew;

    RecyclerView recyclerView;
    CartAdapter cartAdapter;

    TextView totalText, payText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRequestQueue = Volley.newRequestQueue(CartActivity.this);

        totalText = findViewById(R.id.cart_total);
        payText = findViewById(R.id.cart_pay);

        emptyView = findViewById(R.id.cart_empty_view);
        emptyView.setVisibility(View.GONE);

        ImageView backIcon = findViewById(R.id.cart_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ImageView delete = findViewById(R.id.cart_clear_all);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAll();
            }
        });

        addNew = findViewById(R.id.cart_add_new);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BILL_ID.matches("")) {
                    Intent intent = new Intent(CartActivity.this, AddCart.class);
                    intent.putExtra("BILL_ID", BILL_ID);
                    AddCart.initialiseAddCartValues();
                    startActivity(intent);
                }
            }
        });

        moveNext = findViewById(R.id.cart_next);
        moveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BILL_ID.matches("") && !PAY.matches("") && !TOTAL.matches("")) {
                    BillActivity.initiaizeValues();
                    Intent intent = new Intent(CartActivity.this, BillActivity.class);
                    intent.putExtra("PAY", PAY);
                    intent.putExtra("TOTAL", TOTAL);
                    intent.putExtra("BILL_ID", BILL_ID);
                    SUCCESS_CHECKER = false;
                    VALUED_CHANGED = false;
                    startActivity(intent);
                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Cart Is Empty", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        recyclerView = findViewById(R.id.cart_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cartAdapter = new CartAdapter(CartActivity.this, CartActivity.this);
        recyclerView.setAdapter(cartAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final RecyclerView.ViewHolder tempHolde = viewHolder;
                new AlertDialog.Builder(CartActivity.this)
                        .setMessage("Do You Want To Delete This Item?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int id = (int) tempHolde.itemView.getTag();
                                String stringId = "" + id;
                                deleteItemFromCart(stringId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                displayCart();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCart();
        if (BillActivity.DONE_EVERYTHING) {
            BillActivity.DONE_EVERYTHING = false;
            finish();
        }
        if (AddCart.editMode) {
            AddCart.editMode = false;
        }
    }

    @Override
    public void onClick(String mCurrentItemId) {
        Intent intent = new Intent(CartActivity.this, AddCart.class);
        intent.putExtra("idOfSelectedItem", mCurrentItemId);
        AddCart.editMode = true;
        startActivity(intent);
    }

    void displayCart() {
        String tableName = ProdContract.Cart.TABLE_NAME + " INNER JOIN " + ProdContract.Bill.TABLE_NAME
                + " ON " + ProdContract.Bill.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_BILL_ID
                + " INNER JOIN " + ProdContract.Stock.TABLE_NAME
                + " ON " + ProdContract.Stock.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_ITEM_ID;

        String[] projection = new String[]{ProdContract.Bill.FULL_COLUMN_ID, ProdContract.Cart.FULL_COLUMN_ID, ProdContract.Stock.FULL_COLUMN_ID
                , ProdContract.Stock.FULL_COLUMN_ITEM_NAME, ProdContract.Stock.FULL_COLUMN_PRICE, ProdContract.Cart.FULL_COLUMN_QUANTITY
                , ProdContract.Cart.FULL_COLUMN_TOTAL, ProdContract.Cart.FULL_COLUMN_DISCOUNT_PER, ProdContract.Cart.FULL_COLUMN_PAY};

        String selection = ProdContract.Bill.FULL_COLUMN_CUSTOMER_ID + "=?";
        String[] selectionArg = new String[]{"0"};

        String sortOrder = ProdContract.Cart.FULL_COLUMN_ID + " ASC ";

        HashMap<String, String> returnParameter = VolleyHelper.selectVolly(tableName, projection, selection, selectionArg,
                sortOrder);

        final VolleyRequest volleyRequest = new VolleyRequest(returnParameter
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        emptyView.setVisibility(View.GONE);
                        List<Cart> cartListFromServer = JsonExtract.extractCartJson(jsonObject.getJSONArray("cursor"));
                        if (!cartListFromServer.isEmpty()) {
                            cartAdapter.swapCursor(cartListFromServer);
                            JSONObject obj = jsonObject.getJSONArray("cursor").getJSONObject(0);
                            BILL_ID = "" + obj.getInt(ProdContract.Bill.FULL_COLUMN_ID);
                            calculateTotal(cartListFromServer);
                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                            PAY = "";
                            TOTAL = "";
                            payText.setText("0");
                            totalText.setText("0");
                            checkBillEmpty();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        cartRequestQueue.add(volleyRequest);
    }

    void calculateTotal(List<Cart> cartList) {
        int total = 0, pay = 0;
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            total = total + cart.getTotal();
            pay = pay + cart.getPay();
        }
        PAY = "" + pay;
        TOTAL = "" + total;
        totalText.setText(TOTAL);
        payText.setText(PAY);
    }

    void checkBillEmpty() {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Bill.TABLE_NAME
                , new String[]{ProdContract.Bill._ID}
                , ProdContract.Bill.COLUMN_CUSTOMER_ID + "=?", new String[]{"0"}, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONObject obj = jsonObject.getJSONArray("cursor").getJSONObject(0);
                        BILL_ID = "" + obj.getInt(ProdContract.Bill._ID);
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            insertIfNoZero();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        cartRequestQueue.add(volleyRequest);
    }

    void insertIfNoZero() {
        List<InsertMysql> insert = new ArrayList<>();
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DATE, null));
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_CUSTOMER_ID, "0"));
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_TOTAL, null));
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DISCOUNT_PER, null));
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DISCOUNT, null));
        insert.add(new InsertMysql(ProdContract.Bill.COLUMN_PAY, null));

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.insertVolly(ProdContract.Bill.TABLE_NAME, insert)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        displayCart();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("INSERT ERROR")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        cartRequestQueue.add(volleyRequest);
    }

    void deleteItemFromCart(String itemIdForDelete) {
        String tableName = ProdContract.Cart.TABLE_NAME;
        String selection = ProdContract.Cart.COLUMN_ITEM_ID + " = ? AND " + ProdContract.Cart.COLUMN_BILL_ID + " = ?";
        String[] selectionArgs = new String[]{itemIdForDelete, BILL_ID};

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.deleteVolly(tableName, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        displayCart();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("DELETE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartActivity.this, "Failed To Delete", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        cartRequestQueue.add(volleyRequest);
    }

    void deleteAll() {
        DialogInterface.OnClickListener yesButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tableName = ProdContract.Cart.TABLE_NAME;
                        String selection = ProdContract.Cart.COLUMN_BILL_ID + "=?";
                        String[] selectionArgs = new String[]{BILL_ID};

                        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.deleteVolly(tableName, selection, selectionArgs)
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("Login Response", response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        displayCart();
                                    } else {
                                        if (jsonObject.getString("status").equals("INVALID")) {
                                            if (toast != null) {
                                                toast.cancel();
                                            }
                                            toast = Toast.makeText(CartActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (jsonObject.getString("status").equals("DELETE ERROR")) {
                                            if (toast != null) {
                                                toast.cancel();
                                            }
                                            toast = Toast.makeText(CartActivity.this, "Failed To Delete", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(CartActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                                    toast = Toast.makeText(CartActivity.this, "Server Error", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else if (error instanceof TimeoutError) {
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(CartActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else if (error instanceof NetworkError) {
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(CartActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
                        cartRequestQueue.add(volleyRequest);
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Do You Want To Delete Everything From Cart");
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
}
