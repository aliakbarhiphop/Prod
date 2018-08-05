package com.aliakbar.android.prod.bill;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliakbar.android.prod.DatePickerFragment;
import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.customer.CustomerActivity;
import com.aliakbar.android.prod.data.InsertMysql;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.aliakbar.android.prod.bill.CartAdjustActivity.CART_ADJUST_SUCCESS;
import static com.aliakbar.android.prod.bill.CartAdjustActivity.initializeDValues;

public class BillActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    static boolean VALUED_CHANGED = false;
    RequestQueue BillActivityQueue;

    public static boolean SUCCESS_CHECKER, DONE_EVERYTHING;

    Toast toast;
    public static String customerId, customerName;
    public static String date, billId;
    public static int total, discPer, disc, pay;
    public static int STATIC_DISC_PER, STATIC_DISC, STATIC_PAY;

    EditText dateEditText, totalText, discPerText, discText, payText, customerNameText;

    boolean PAY_LISTENER_FLAG = false, DISC_PER_LISTENER_FLAG = false, DISC_LISTENER_FLAG = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        BillActivityQueue = Volley.newRequestQueue(BillActivity.this);

        ImageView backIcon = findViewById(R.id.add_bill_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FrameLayout datePicker = findViewById(R.id.add_bill_date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        FrameLayout customerPicker = findViewById(R.id.add_bill_customer_picker);
        customerPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerActivity.BILL_FLAG = true;
                customerId = "";
                customerName = "";
                Intent intent = new Intent(BillActivity.this, CustomerActivity.class);
                startActivity(intent);
            }
        });

        customerNameText = findViewById(R.id.add_bill_customer);

        dateEditText = findViewById(R.id.add_bill_date);
        dateSet();

        totalText = findViewById(R.id.add_bill_total);

        discPerText = findViewById(R.id.add_bill_discount_per);
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
                setAllFlagsFalse();
                DISC_PER_LISTENER_FLAG = true;
                return false;
            }
        });
        discPerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (DISC_PER_LISTENER_FLAG) {
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

        discText = findViewById(R.id.add_bill_discount);
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
                setAllFlagsFalse();
                DISC_LISTENER_FLAG = true;
                return false;
            }
        });
        discText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (DISC_LISTENER_FLAG) {
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
        payText = findViewById(R.id.add_bill_pay);
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
                setAllFlagsFalse();
                PAY_LISTENER_FLAG = true;
                return false;
            }
        });
        payText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(BillActivity.this,"CARAAAAAP IS OKAY",Toast.LENGTH_LONG).show();
                if (PAY_LISTENER_FLAG) {
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

        ImageView saveIcon = findViewById(R.id.add_bill_save);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBill();
            }
        });

        setValues();

        VALUED_CHANGED = true;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CustomerActivity.BILL_FLAG) {
            CustomerActivity.BILL_FLAG = false;
            if (!customerName.matches("") && !customerId.matches("")) {
                customerNameText.setText(customerName);
            }
        }
        if (CART_ADJUST_SUCCESS) {
            CART_ADJUST_SUCCESS = false;
            saveBill();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        date = format1.format(c.getTime());
        dateEditText.setText(date);
    }

    void dateSet() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat Sformat = new SimpleDateFormat("yyyy-MM-dd");
        date = Sformat.format(c.getTime());
        dateEditText.setText(date);
    }

    public static void initiaizeValues() {
        date = "";
        customerId = "";
        customerName = "";
        total = 0;
        disc = 0;
        discPer = 0;
        billId = "";
        pay = 0;

        STATIC_DISC_PER = 0;
        STATIC_DISC = 0;
        STATIC_PAY = 0;
    }

    void setValues() {
        if (!VALUED_CHANGED) {
            Intent extractIdIntent = getIntent();
            billId = extractIdIntent.getStringExtra("BILL_ID");
            total = Integer.parseInt(extractIdIntent.getStringExtra("TOTAL"));
            pay = Integer.parseInt(extractIdIntent.getStringExtra("PAY"));
            disc = total - pay;
            discPer = 100 - (int) (((double) pay / (double) total) * 100.0);
            if (total == 0) {
                discPer = 0;
            }


            STATIC_DISC_PER = discPer;
            STATIC_DISC = disc;
            STATIC_PAY = pay;
        }

        totalText.setText("" + total);
        discPerText.setText("" + discPer);
        discText.setText("" + disc);
        payText.setText("" + pay);
    }

    void setAllFlagsFalse() {
        PAY_LISTENER_FLAG = false;
        DISC_LISTENER_FLAG = false;
        DISC_PER_LISTENER_FLAG = false;
    }

    void removeError() {
        discPerText.setError(null);
        discText.setError(null);
        payText.setError(null);
    }

    void changeCartItem() {
        Intent intent = new Intent(BillActivity.this, CartAdjustActivity.class);
        initializeDValues();
        startActivity(intent);
    }

    void saveBill() {
        String SEdate, SEname, SEtotal, SEdiscPer, SEdisc, SEpay;
        SEdate = dateEditText.getText().toString();
        SEname = customerNameText.getText().toString();
        SEtotal = totalText.getText().toString();
        SEdiscPer = discPerText.getText().toString();
        SEdisc = discText.getText().toString();
        SEpay = payText.getText().toString();

        if (SEdate.matches("") || SEname.matches("") || SEtotal.matches("") || SEdiscPer.matches("")
                || SEdisc.matches("") || SEpay.matches("")) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(BillActivity.this, "Every Field Must Be Fill", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            if (SEdate.matches(date) && SEname.matches(customerName) && SEtotal.matches(total + "") && SEdiscPer.matches("" + discPer)
                    && SEdisc.matches("" + disc) && SEpay.matches("" + pay)) {

                setAllFlagsFalse();
                PAY_LISTENER_FLAG = true;
                payText.setText("" + pay);

                if (STATIC_PAY != pay) {
                    changeCartItem();
                } else {

                    List<InsertMysql> insert = new ArrayList<>();
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DATE, date));
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_CUSTOMER_ID, customerId));
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_TOTAL, "" + total));
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DISCOUNT_PER, "" + discPer));
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_DISCOUNT, "" + disc));
                    insert.add(new InsertMysql(ProdContract.Bill.COLUMN_PAY, "" + pay));

                    superUpdateMethod(insert, ProdContract.Bill._ID + "=?", new String[]{billId}, ProdContract.Bill.TABLE_NAME);

                    setCustomerForUpdate(ProdContract.Customer.TABLE_NAME, new String[]{ProdContract.Customer.COLUMN_TOTAL_DEAL, ProdContract.Customer.COLUMN_TOTAL
                            , ProdContract.Customer.COLUMN_CREDIT}, ProdContract.Customer._ID + "=?", new String[]{customerId});

                    setStaffForUpdate();

                    setStockForUpdate();

                    DONE_EVERYTHING = true;
                    finish();
                }
            }
        }
    }

    void superUpdateMethod(List<InsertMysql> values, String selection, String[] selectionArg, String tableName) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(tableName, values, selection, selectionArg)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        if (SUCCESS_CHECKER) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "Bill Saved Successfully", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "Failed To Update", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(BillActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        BillActivityQueue.add(volleyRequest);
    }

    void setCustomerForUpdate(final String tableName, final String[] projection, final String selection, final String[] selectionArg) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(tableName, projection, selection, selectionArg, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);

                        List<InsertMysql> insert = new ArrayList<>();


                        int changingValue = obj.getInt(projection[0]);
                        changingValue++;
                        insert.add(new InsertMysql(ProdContract.Customer.COLUMN_TOTAL_DEAL, "" + changingValue));

                        changingValue = obj.getInt(projection[1]);
                        changingValue = changingValue + total;
                        insert.add(new InsertMysql(ProdContract.Customer.COLUMN_TOTAL, "" + changingValue));

                        changingValue = obj.getInt(projection[2]);
                        changingValue = changingValue + total;
                        insert.add(new InsertMysql(ProdContract.Customer.COLUMN_CREDIT, "" + changingValue));


                        String Tselection = ProdContract.Customer._ID + "=?";
                        String[] TselectionArg = new String[]{customerId};

                        superUpdateMethod(insert, Tselection, TselectionArg, ProdContract.Customer.TABLE_NAME);
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(BillActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        BillActivityQueue.add(volleyRequest);
    }

    void setStaffForUpdate() {
        final String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;
        final String[] projection = new String[]{ProdContract.Staff.FULL_COLUMN_ID, ProdContract.Staff.FULL_COLUMN_STAFF_SALE, ProdContract.Staff.FULL_COLUMN_STAFF_TOTAL
                , ProdContract.Staff.FULL_COLUMN_STAFF_CREDIT};
        final String selection = ProdContract.Customer.FULL_COLUMN_ID + "=?";
        final String[] selectionArg = new String[]{customerId};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(tableName, projection, selection, selectionArg, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);

                        List<InsertMysql> insert = new ArrayList<>();


                        int changingValue = obj.getInt(projection[1]);
                        changingValue++;
                        insert.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_SALE, "" + changingValue));

                        changingValue = obj.getInt(projection[2]);
                        changingValue = changingValue + total;
                        insert.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_TOTAL, "" + changingValue));

                        changingValue = obj.getInt(projection[3]);
                        changingValue = changingValue + total;
                        insert.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_CREDIT, "" + changingValue));


                        String Tselection = ProdContract.Staff._ID + "=?";
                        String[] TselectionArg = new String[]{"" + obj.getInt(projection[0])};

                        if (obj.getInt(projection[0]) != 0) {
                            superUpdateMethod(insert, Tselection, TselectionArg, ProdContract.Staff.TABLE_NAME);
                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(BillActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        BillActivityQueue.add(volleyRequest);
    }

    void setStockForUpdate() {
        final String tableName = ProdContract.Cart.TABLE_NAME + " INNER JOIN " + ProdContract.Stock.TABLE_NAME
                + " ON " + ProdContract.Cart.FULL_COLUMN_ITEM_ID + " = " + ProdContract.Stock.FULL_COLUMN_ID;
        final String[] projection = new String[]{ProdContract.Cart.FULL_COLUMN_QUANTITY, ProdContract.Stock.FULL_COLUMN_ID, ProdContract.Stock.FULL_COLUMN_SALES};

        final String selection = ProdContract.Cart.FULL_COLUMN_BILL_ID + "=?";
        final String[] selectionArg = new String[]{billId};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(tableName, projection, selection, selectionArg, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);

                            List<InsertMysql> insert = new ArrayList<>();

                            int changingValue = obj.getInt(projection[2]);
                            changingValue = changingValue + obj.getInt(projection[0]);
                            insert.add(new InsertMysql(ProdContract.Stock.COLUMN_SALES, "" + changingValue));

                            String Tselection = ProdContract.Stock._ID + "=?";
                            String[] TselectionArg = new String[]{"" + obj.getInt(projection[1])};

                            SUCCESS_CHECKER = true;

                            superUpdateMethod(insert, Tselection, TselectionArg, ProdContract.Stock.TABLE_NAME);

                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BillActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(BillActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(BillActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        BillActivityQueue.add(volleyRequest);
    }

}

