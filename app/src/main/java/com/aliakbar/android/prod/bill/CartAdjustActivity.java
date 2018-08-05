package com.aliakbar.android.prod.bill;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.aliakbar.android.prod.bill.BillActivity.STATIC_DISC_PER;

public class CartAdjustActivity extends AppCompatActivity implements CartAdjustAdapter.CartAdjustAdapterOnClickListener {
    RequestQueue CartAdjustQueue;

    public static boolean CART_ADJUST_SUCCESS = false;

    RelativeLayout parent;

    Toast toast;

    ImageView closeIcon;
    TextView balanceText, infoText;
    RecyclerView recyclerView;
    CartAdjustAdapter adjustAdapter;

    static int Dtotal, DdiscPer, Ddisc, Dpay;

    int TEMP_PAY;

    int swipeID, swipeTOTAL, swipePAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_adjust);
        closeIcon = findViewById(R.id.close_icon_cart_adjust);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        balanceText = findViewById(R.id.cart_adjust_balance);
        infoText = findViewById(R.id.info_text);

        setDiff();

        recyclerView = findViewById(R.id.cart_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adjustAdapter = new CartAdjustAdapter(this, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int limitOfItem;

                final RecyclerView.ViewHolder tempHolde = viewHolder;
                String swipeID = "" + (int) tempHolde.itemView.getTag();
                int swipeTOTAL = (int) tempHolde.itemView.getTag(1);
                int swipePAY = (int) tempHolde.itemView.getTag(2);

//TODO
                CartAdjustFragment cartAdjustFragment = new CartAdjustFragment();

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // get the display mode
                int displaymode = getResources().getConfiguration().orientation;
                if (displaymode == 1) { // it portrait mode
                    fragmentTransaction.replace(android.R.id.content, cartAdjustFragment);
                } else {// its landscape
                    fragmentTransaction.replace(android.R.id.content, cartAdjustFragment);
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //TODO END


                if (Dpay < 0) {
                    int tempPay = -1 * Dpay;
                    if (swipePAY >= tempPay) {
                        cartAdjustFragment.setupInput(Dpay, 0);
                        //TODO --> 1 NOTE SUBSTARCT

                    } else {
                        //TODO --> 2 NOTE  SUBSTARCT
                        limitOfItem = tempPay - (tempPay - swipePAY);
                        cartAdjustFragment.setupInput(Dpay, limitOfItem);
                    }
                } else {

                    //TODO --> 1 NOTE  ADDITION
                    cartAdjustFragment.setupInput(Dpay, 0);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEnabledTrue();
        displayAdjustCart();
    }

    @Override
    public void onClick(String mCurrentItemId, int mCurrentTotal, int mCurrentPay) {
        int UPDATE_PAY, UPDATE_DISC, UPDATE_DISC_PER;

        if (Dpay < 0) {
            int tempPay = -1 * Dpay;
            if (mCurrentPay >= tempPay) {
                UPDATE_PAY = mCurrentPay + Dpay;

                TEMP_PAY = BillActivity.STATIC_PAY + Dpay;

                UPDATE_DISC = mCurrentTotal - UPDATE_PAY;
                UPDATE_DISC_PER = 100 - (int) (((double) UPDATE_PAY / (double) mCurrentTotal) * 100.0);
            } else {
                UPDATE_PAY = 0;
                UPDATE_DISC = mCurrentTotal;
                UPDATE_DISC_PER = 100;

                if (toast != null) {
                    toast.cancel();
                }
                int tempValue = tempPay - (tempPay - mCurrentPay);

                TEMP_PAY = BillActivity.STATIC_PAY - tempValue;

                toast = Toast.makeText(CartAdjustActivity.this, "This Item Can Only Substract " + tempValue, Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            UPDATE_PAY = mCurrentPay + Dpay;

            TEMP_PAY = BillActivity.STATIC_PAY + Dpay;

            UPDATE_DISC = mCurrentTotal - UPDATE_PAY;
            UPDATE_DISC_PER = 100 - (int) (((double) UPDATE_PAY / (double) mCurrentTotal) * 100.0);
        }

        List<InsertMysql> insert = new ArrayList<>();
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_PAY, "" + UPDATE_PAY));
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT, "" + UPDATE_DISC));
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT_PER, "" + UPDATE_DISC_PER));

        updateCart(ProdContract.Cart.TABLE_NAME, insert, ProdContract.Cart._ID + "=?", new String[]{mCurrentItemId});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setEnabledTrue();
    }

    void setDiff() {
        Dtotal = BillActivity.total;
        DdiscPer = BillActivity.discPer - STATIC_DISC_PER;
        Ddisc = BillActivity.disc - BillActivity.STATIC_DISC;
        Dpay = BillActivity.pay - BillActivity.STATIC_PAY;

        balanceText.setText("" + Dpay);

        if (Dpay < 0) {
            int tempPay = -1 * Dpay;
            infoText.setText("Substaract " + tempPay + "From The Pay Of The Any Following Items.");
        } else {
            infoText.setText("Add " + Dpay + "To The Pay Of The Any Following Items.");
        }
    }

    public static void initializeDValues() {
        Dtotal = 0;
        DdiscPer = 0;
        Ddisc = 0;
        Dpay = 0;
    }

    void displayAdjustCart() {
        String tableName = ProdContract.Cart.TABLE_NAME + " INNER JOIN " + ProdContract.Bill.TABLE_NAME
                + " ON " + ProdContract.Bill.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_BILL_ID
                + " INNER JOIN " + ProdContract.Stock.TABLE_NAME
                + " ON " + ProdContract.Stock.FULL_COLUMN_ID + " = " + ProdContract.Cart.FULL_COLUMN_ITEM_ID;

        String[] projection = new String[]{ProdContract.Cart.FULL_COLUMN_ID, ProdContract.Stock.FULL_COLUMN_ID
                , ProdContract.Stock.FULL_COLUMN_ITEM_NAME, ProdContract.Stock.FULL_COLUMN_PRICE, ProdContract.Cart.FULL_COLUMN_QUANTITY
                , ProdContract.Cart.FULL_COLUMN_TOTAL, ProdContract.Cart.FULL_COLUMN_DISCOUNT_PER, ProdContract.Cart.FULL_COLUMN_DISCOUNT
                , ProdContract.Cart.FULL_COLUMN_PAY};

        String selection = ProdContract.Bill.FULL_COLUMN_ID + "=?";
        String[] selectionArg = new String[]{BillActivity.billId};

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
                        List<Cart> cartListFromServer = JsonExtract.extractCartJson(jsonObject.getJSONArray("cursor"));
                        if (!cartListFromServer.isEmpty()) {
                            adjustAdapter.swapCursor(cartListFromServer);
                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartAdjustActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartAdjustActivity.this, "Something Error Occured", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartAdjustActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        CartAdjustQueue.add(volleyRequest);
    }

    void updateCart(String tableName, List<InsertMysql> values, String selection, String selectionArgs[]) {

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(tableName, values, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        BillActivity.STATIC_PAY = TEMP_PAY;
                        BillActivity.STATIC_DISC = BillActivity.total - BillActivity.STATIC_PAY;
                        BillActivity.STATIC_DISC_PER = 100 - (int) (((double) BillActivity.STATIC_PAY / (double) BillActivity.total) * 100.0);
                        CART_ADJUST_SUCCESS = true;
                        TEMP_PAY = 0;
                        finish();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartAdjustActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CartAdjustActivity.this, "Failed To Update", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CartAdjustActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CartAdjustActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        CartAdjustQueue.add(volleyRequest);
    }

    public void setEnabledFalse() {
        parent = findViewById(R.id.parent_cart_adjust);
        parent.setEnabled(false);
    }

    public void setEnabledTrue() {
        parent = findViewById(R.id.parent_cart_adjust);
        parent.setEnabled(true);
    }

    public void fromFragmentAdjust(int inputValue) {
        int UPDATE_PAY, UPDATE_DISC, UPDATE_DISC_PER;
        setEnabledTrue();
        if (Dpay < 0) {
            TEMP_PAY = BillActivity.STATIC_PAY - inputValue;
            UPDATE_PAY = swipePAY - inputValue;
            UPDATE_DISC = swipeTOTAL - UPDATE_PAY;
            UPDATE_DISC_PER = 100 - (int) (((double) UPDATE_PAY / (double) swipeTOTAL) * 100.0);
        } else {
            UPDATE_PAY = swipePAY + inputValue;

            TEMP_PAY = BillActivity.STATIC_PAY + inputValue;

            UPDATE_DISC = swipeTOTAL - UPDATE_PAY;
            UPDATE_DISC_PER = 100 - (int) (((double) UPDATE_PAY / (double) swipeTOTAL) * 100.0);
        }
        List<InsertMysql> insert = new ArrayList<>();
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_PAY, "" + UPDATE_PAY));
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT, "" + UPDATE_DISC));
        insert.add(new InsertMysql(ProdContract.Cart.COLUMN_DISCOUNT_PER, "" + UPDATE_DISC_PER));

        updateCart(ProdContract.Cart.TABLE_NAME, insert, ProdContract.Cart._ID + "=?", new String[]{"" + swipeID});
    }
}
