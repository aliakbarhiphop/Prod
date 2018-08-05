package com.aliakbar.android.prod.customer;

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
import com.aliakbar.android.prod.data.Customer;
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

import java.util.ArrayList;
import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractCustomerJson;

public class CustomerDetailActivity extends AppCompatActivity {
    Toast toast;
    String staffId;

    TextView idTextView, nameTextView, managerTextView, dealTextView, totalTextView, paidTextView, creditTextView, orderTextView;

    String idFromIntent;

    RequestQueue customerDetailRequestQueue, deleteCustomerRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        deleteCustomerRequestQueue = Volley.newRequestQueue(CustomerDetailActivity.this);
        customerDetailRequestQueue = Volley.newRequestQueue(CustomerDetailActivity.this);

        Intent extractIdIntent = getIntent();
        idFromIntent = extractIdIntent.getStringExtra("selectedCustomerId");

        ImageView backIcon = findViewById(R.id.customer_detail_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        idTextView = findViewById(R.id.customer_detail_id);
        nameTextView = findViewById(R.id.customer_detail_name);
        managerTextView = findViewById(R.id.customer_detail_manager);
        dealTextView = findViewById(R.id.customer_detail_deals);
        totalTextView = findViewById(R.id.customer_detail_total);
        paidTextView = findViewById(R.id.customer_detail_paid);
        creditTextView = findViewById(R.id.customer_detail_credit);
        orderTextView = findViewById(R.id.customer_detail_order);

        FrameLayout deleteFrame = findViewById(R.id.customer_detail_delete);
        deleteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFunction();
            }
        });

        FrameLayout editFrame = (FrameLayout) findViewById(R.id.customer_detail_edit);
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(CustomerDetailActivity.this, CustomerAddActivity.class);
                CustomerAddActivity.editMode = true;
                detailIntent.putExtra("selectedCustomerId", idFromIntent);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CustomerAddActivity.editMode) {
            CustomerAddActivity.editMode = false;
        }
        setCustomer();
    }

    void setCustomer() {
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
                        nameTextView.setText(name);

                        String id = "" + customer.getId();
                        idTextView.setText(id);

                        String managerName = customer.getManagerName();
                        managerTextView.setText("Manager :" + managerName);

                        String deals = "" + customer.getDeals();
                        dealTextView.setText(deals);

                        String order = "" + customer.getOrders();
                        orderTextView.setText(order);

                        String total = "" + customer.getTotal();
                        totalTextView.setText(total);

                        String paid = "" + customer.getPaid();
                        paidTextView.setText(paid);

                        String credit = "" + customer.getCredit();
                        creditTextView.setText(credit);

                        //SETUP STAFF ID
                        JSONArray jsonArray = jsonObject.getJSONArray("cursor");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        int staffIdJson = obj.getInt(ProdContract.Staff.FULL_COLUMN_ID);
                        staffId = "" + staffIdJson;

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerDetailActivity.this, "Database Empty", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerDetailActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        customerDetailRequestQueue.add(volleyRequest);
    }

    void deleteFunction() {
        DialogInterface.OnClickListener yesButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tableName = ProdContract.Customer.TABLE_NAME;
                        String selection = ProdContract.Customer._ID + "=?";
                        String[] selectionArgs = new String[]{idFromIntent};
                        deleteCustomer(tableName, selection, selectionArgs);
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDetailActivity.this);
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

    void deleteCustomer(String tableName, String selection, String[] selectionArgs) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.deleteVolly(tableName, selection, selectionArgs)
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
                        toast = Toast.makeText(CustomerDetailActivity.this, "Deleted Successfully.Deleted " + jsonObject.getInt("affectedRaw")
                                        + " Item"
                                , Toast.LENGTH_SHORT);
                        toast.show();
                        if (!staffId.matches("0")) {
                            updateDeleteStaff();
                        }
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT);toast.show();
                        } else if (jsonObject.getString("status").equals("DELETE ERROR")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerDetailActivity.this, "Failed To Delete", Toast.LENGTH_SHORT);toast.show();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);toast.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        deleteCustomerRequestQueue.add(volleyRequest);
    }

    void updateDeleteStaff() {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(ProdContract.Staff.TABLE_NAME
                , new String[]{ProdContract.Staff.COLUMN_STAFF_CLIENT}, ProdContract.Staff._ID + " = ?", new String[]{staffId}, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {


                        JSONArray customerJson = jsonObject.getJSONArray("cursor");

                        JSONObject obj = customerJson.getJSONObject(0);

                        int client = obj.getInt(ProdContract.Staff.COLUMN_STAFF_CLIENT);

                        if (client <= 0) {
                            client = 0;
                        } else {
                            client--;
                        }
                        String clientString = "" + client;
                        ArrayList<InsertMysql> ins = new ArrayList<>();
                        ins.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_CLIENT, clientString));
                        updateStaffClent(ins);


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
        deleteCustomerRequestQueue.add(volleyRequest);
    }

    void updateStaffClent(List<InsertMysql> insertMysqls) {

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Staff.TABLE_NAME
                , insertMysqls, ProdContract.Staff._ID + " = ?", new String[]{staffId})
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        customerDetailRequestQueue.add(volleyRequest);
    }

}
