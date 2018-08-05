package com.aliakbar.android.prod.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Customer;
import com.aliakbar.android.prod.data.InsertMysql;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.network.VolleyHelper;
import com.aliakbar.android.prod.network.VolleyRequest;
import com.aliakbar.android.prod.staff.StaffActivity;
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
import java.util.HashMap;
import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractCustomerJson;

public class CustomerAddActivity extends AppCompatActivity {
    Toast toast;
    String staffId = "0";

    String staffIdNever = "";

    public static boolean editMode = false;
    TextView managerName;
    String idForUpdate;

    boolean dealsFirstTime = true;
    boolean orderFirstTime = true;
    boolean totalFirstTime = true;
    boolean paidFirstTime = true;
    boolean ceditFirstTime = true;

    boolean ediTextTouched = false;

    private EditText nameText;
    private EditText dealsText;
    private EditText totalText;
    private EditText paidText;
    private EditText creditText;
    private EditText orderText;

    RequestQueue addCustomerRequestQueue, customerEditRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);

        addCustomerRequestQueue = Volley.newRequestQueue(CustomerAddActivity.this);
        customerEditRequestQueue = Volley.newRequestQueue(CustomerAddActivity.this);

        ImageView backIcon = findViewById(R.id.add_customer_back);
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

        nameText = findViewById(R.id.add_customer_name);
        nameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ediTextTouched = true;
                return false;
            }
        });

        dealsText = findViewById(R.id.add_customer_deals);
        totalText = findViewById(R.id.add_customer_total);
        paidText = findViewById(R.id.add_customer_paid);
        creditText = findViewById(R.id.add_customer_credit);
        orderText = findViewById(R.id.add_customer_order);

        if (!editMode) {
            dealsText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (dealsFirstTime) {
                        dealsFirstTime = false;
                        dealsText.setText("");
                    }
                    return false;
                }
            });
            totalText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (totalFirstTime) {
                        totalFirstTime = false;
                        totalText.setText("");
                    }
                    return false;
                }
            });
            paidText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (paidFirstTime) {
                        paidFirstTime = false;
                        paidText.setText("");
                    }
                    return false;
                }
            });
            creditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (ceditFirstTime) {
                        ceditFirstTime = false;
                        creditText.setText("");
                    }
                    return false;
                }
            });
            orderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (orderFirstTime) {
                        orderFirstTime = false;
                        orderText.setText("");
                    }
                    return false;
                }
            });
        } else {
            dealsText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            totalText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            paidText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            creditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            orderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
        }

        ImageView saveIcon = findViewById(R.id.add_customer_save);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, deals, total, paid, credit, order;
                name = nameText.getText().toString().trim();
                deals = dealsText.getText().toString().trim();
                total = totalText.getText().toString().trim();
                paid = paidText.getText().toString().trim();
                credit = creditText.getText().toString().trim();
                order = orderText.getText().toString().trim();
                if (name.matches("") || deals.matches("") || total.matches("") || paid.matches("") || credit.matches("") ||
                        order.matches("")) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Some Fields Are Empty", Toast.LENGTH_SHORT);

                    toast.show();
                } else {
                    List<InsertMysql> values = new ArrayList<>();
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_CUSTOMER_NAME, name));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_TOTAL_DEAL, deals));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_TOTAL, total));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_PAID, paid));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_CREDIT, credit));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_ORDER, order));
                    values.add(new InsertMysql(ProdContract.Customer.COLUMN_CUSTOMER_MANAGER_ID, staffId));
                    setUpUpdate();
                    if (editMode) {
                        updateCustomer(values);
                    } else {
                        saveCustomer(values);
                    }
                }
            }
        });

        if (editMode) {
            Intent extractIdIntent = getIntent();
            String idFromIntent = extractIdIntent.getStringExtra("selectedCustomerId");
            idForUpdate = idFromIntent;
            setUpEditMode(idFromIntent);
        }

        managerName = findViewById(R.id.customer_add_manager);
        Button addButton = findViewById(R.id.customer_add_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerAddActivity.this, StaffActivity.class);
                StaffActivity.CUSTOMER_MANAGER_FLAG = true;
                StaffActivity.CUSTOMER_NEED_ID = "";
                StaffActivity.CUSTOMER_NEED_NAME = "";
                startActivity(intent);
            }
        });
        Button naButton = findViewById(R.id.customer_add_na);
        naButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staffId = "0";
                managerName.setText("Manager :NA");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StaffActivity.CUSTOMER_MANAGER_FLAG) {
            StaffActivity.CUSTOMER_MANAGER_FLAG = false;
            staffId = StaffActivity.CUSTOMER_NEED_ID;
            managerName.setText(StaffActivity.CUSTOMER_NEED_NAME);
            if (StaffActivity.CUSTOMER_NEED_ID.matches("")||StaffActivity.CUSTOMER_NEED_NAME.matches("") ) {
                managerName.setText("Manager :NA");
                staffId="0";
            }
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

    void setUpEditMode(String idFromIntent) {

        String[] projection = new String[]{ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Customer.FULL_COLUMN_ID
                , ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Staff.FULL_COLUMN_ID, ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL, ProdContract.Customer.FULL_COLUMN_TOTAL
                , ProdContract.Customer.FULL_COLUMN_PAID, ProdContract.Customer.FULL_COLUMN_CREDIT, ProdContract.Customer.FULL_COLUMN_ORDER};

        String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;

        String selection = ProdContract.Customer.FULL_COLUMN_ID + "=?";
        String selectionArgs[] = new String[]{idFromIntent};

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                tableName
                , projection
                , selection, selectionArgs, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Customer> customerListFromServer = extractCustomerJson(jsonObject.getJSONArray("cursor"));

                        Customer customer = customerListFromServer.get(0);

                        String name = customer.getName();
                        nameText.setText(name);

                        String deals = "" + customer.getDeals();
                        dealsText.setText(deals);

                        String order = "" + customer.getOrders();
                        orderText.setText(order);

                        String total = "" + customer.getTotal();
                        totalText.setText(total);

                        String paid = "" + customer.getPaid();
                        paidText.setText(paid);

                        String credit = "" + customer.getCredit();
                        creditText.setText(credit);

                        String manager = customer.getManagerName();
                        managerName.setText("Manager :" + manager);

                        //SETUP STAFF ID
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        int staffIdJson = obj.getInt(ProdContract.Staff.FULL_COLUMN_ID);
                        staffId = "" + staffIdJson;
                        staffIdNever = staffId;

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "Database Empty", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CustomerAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        customerEditRequestQueue.add(volleyRequest);
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


    void saveCustomer(List<InsertMysql> insert) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.insertVolly(ProdContract.Customer.TABLE_NAME, insert)
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
                        toast = Toast.makeText(CustomerAddActivity.this, "Saved Successfully", Toast.LENGTH_SHORT);
                        toast.show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("INSERT ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "Failed To Insert", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerAddActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        addCustomerRequestQueue.add(volleyRequest);
    }

    void updateCustomer(List<InsertMysql> insert) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idForUpdate};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Customer.TABLE_NAME, insert, selection, selectionArgs)
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
                        toast = Toast.makeText(CustomerAddActivity.this, "Update Successfully. Affected :" + jsonObject.getInt("affectedRaw")
                                        + " Raw"
                                , Toast.LENGTH_SHORT);
                        toast.show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerAddActivity.this, "Failed To Update", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerAddActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        customerEditRequestQueue.add(volleyRequest);
    }


    void setUpUpdate() {
        if (editMode) {
            if (!staffId.matches(staffIdNever)) {
                if (staffIdNever.matches("0")) {
                    selectUpdate(new String[]{staffId});
                } else if (staffId.matches("0")) {
                    selectUpdate(new String[]{staffIdNever});
                } else {
                    selectUpdate(new String[]{staffIdNever});
                    selectUpdate(new String[]{staffId});
                }
            }
        } else {
            if (!staffId.matches("0")) {
                selectUpdate(new String[]{staffId});
            }
        }
    }


    void selectUpdate(final String[] staffUpId) {
        HashMap<String, String> upHashMap = VolleyHelper.selectVolly(ProdContract.Staff.TABLE_NAME, new String[]{ProdContract.Staff.COLUMN_STAFF_CLIENT}
                , ProdContract.Staff._ID + " = ?", staffUpId, null);

        final VolleyRequest volleyRequest = new VolleyRequest(upHashMap
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        int clientJson = obj.getInt(ProdContract.Staff.COLUMN_STAFF_CLIENT);

                        if (editMode) {
                            if (staffUpId[0].matches(staffIdNever)) {
                                if (clientJson <= 0) {
                                    clientJson = 0;
                                } else {
                                    clientJson--;
                                }
                            } else if (staffUpId[0].matches(staffId)) {
                                if (clientJson <= 0) {
                                    clientJson = 1;
                                } else {
                                    clientJson = clientJson + 1;
                                }
                            }
                        } else {
                            if (clientJson <= 0) {
                                clientJson = 1;
                            } else {
                                clientJson = clientJson + 1;
                            }
                        }
                        List<InsertMysql> upStaff = new ArrayList<>();
                        String clientJsonString = "" + clientJson;
                        upStaff.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_CLIENT, clientJsonString));
                        updateStaffClient(ProdContract.Staff._ID + " = ?", staffUpId, upStaff);

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        customerEditRequestQueue.add(volleyRequest);
    }


    void updateStaffClient(String uSelection, final String[] uSelectionArgs, List<InsertMysql> upValue) {

        HashMap<String, String> upHashMap = VolleyHelper.updateVolly(ProdContract.Staff.TABLE_NAME, upValue, uSelection, uSelectionArgs);

        final VolleyRequest volleyRequest = new VolleyRequest(upHashMap
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {

                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        customerEditRequestQueue.add(volleyRequest);
    }
}


