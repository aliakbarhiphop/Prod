package com.aliakbar.android.prod.staff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.customer.CustomerActivity;
import com.aliakbar.android.prod.data.InsertMysql;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.data.Staff;
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

import static com.aliakbar.android.prod.data.JsonExtract.extractStaffJson;

public class StaffDetailActivity extends AppCompatActivity {
    String idFromIntent;

    RequestQueue staffDetailRequestQueue, deleteStaffRequestQueue;


    ImageView backIcon;
    FrameLayout editIcon, deleteIcon;

    TextView nameView, usernameView, idView, passwordView, clientView, salesView, orderView, totalView, paidView, creditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        staffDetailRequestQueue = Volley.newRequestQueue(StaffDetailActivity.this);
        deleteStaffRequestQueue = Volley.newRequestQueue(StaffDetailActivity.this);

        Intent extractIdIntent = getIntent();
        idFromIntent = extractIdIntent.getStringExtra("selectedStaffId");

        nameView = findViewById(R.id.staff_detail_name);
        usernameView = findViewById(R.id.staff_detail_username);
        idView = findViewById(R.id.staff_detail_id);
        passwordView = findViewById(R.id.staff_detail_password);
        clientView = findViewById(R.id.staff_detail_clents);
        salesView = findViewById(R.id.staff_detail_sales);
        orderView = findViewById(R.id.staff_detail_order);
        totalView = findViewById(R.id.staff_detail_total);
        paidView = findViewById(R.id.staff_detail_paid);
        creditView = findViewById(R.id.staff_detail_credit);

        backIcon = findViewById(R.id.staff_detail_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteIcon = findViewById(R.id.staff_detail_delete);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFunction();
            }
        });

        editIcon = findViewById(R.id.staff_detail_edit);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDetailActivity.this, StaffAddActivity.class);
                intent.putExtra("selectedStaffId", idFromIntent);
                StaffAddActivity.editableMode = true;
                startActivity(intent);
            }
        });

        LinearLayout viewClient = findViewById(R.id.staff_detail_view_client);
        viewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDetailActivity.this, CustomerActivity.class);
                intent.putExtra("idFromStaff", idFromIntent);
                CustomerActivity.STAFF_MODE = true;
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CustomerActivity.STAFF_MODE) {
            CustomerActivity.STAFF_MODE = false;
        }
        if (StaffAddActivity.editableMode) {
            StaffAddActivity.editableMode = false;
        }
        setStaff();
    }

    void setStaff() {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idFromIntent};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Staff.TABLE_NAME
                , null
                , selection, selectionArgs, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Staff> stockListFromServer = extractStaffJson(jsonObject.getJSONArray("cursor"));

                        Staff staff = stockListFromServer.get(0);

                        String name = staff.getName();
                        nameView.setText(name);

                        String id = "" + staff.getId();
                        idView.setText(id);

                        String username = staff.getUsername();
                        usernameView.setText(username);

                        String password = staff.getPassword();
                        passwordView.setText(password);

                        String client = "" + staff.getClient();
                        clientView.setText(client);

                        String sales = "" + staff.getSales();
                        salesView.setText(sales);

                        String order = "" + staff.getOrder();
                        orderView.setText(order);

                        String total = "" + staff.getTotal();
                        totalView.setText(total);

                        String paid = "" + staff.getPaid();
                        paidView.setText(paid);

                        String credit = "" + staff.getCredit();
                        creditView.setText(credit);

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            Toast.makeText(StaffDetailActivity.this, "Database Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        staffDetailRequestQueue.add(volleyRequest);
    }

    void deleteFunction() {
        DialogInterface.OnClickListener yesButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tableName = ProdContract.Staff.TABLE_NAME;
                        String selection = ProdContract.Staff._ID + "=?";
                        String[] selectionArgs = new String[]{idFromIntent};
                        setUpCustomer();
                        deleteStaff(tableName, selection, selectionArgs);
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(StaffDetailActivity.this);
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

    void deleteStaff(String tableName, String selection, String[] selectionArgs) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.deleteVolly(tableName, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StaffDetailActivity.this, "Deleted Successfully.Deleted " + jsonObject.getInt("affectedRaw")
                                        + " Item"
                                , Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffDetailActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("DELETE ERROR")) {
                            Toast.makeText(StaffDetailActivity.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffDetailActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffDetailActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffDetailActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        deleteStaffRequestQueue.add(volleyRequest);
    }

    void setUpCustomer() {
        String tableName = ProdContract.Customer.TABLE_NAME;
        String[] custProjection = new String[]{ProdContract.Customer._ID};
        String[] custSelectionArg = new String[]{idFromIntent};
        String selection = ProdContract.Customer.COLUMN_CUSTOMER_MANAGER_ID + " = ?";

        selectUpdateCustomerManager(tableName, custProjection, selection, custSelectionArg);
    }

    void selectUpdateCustomerManager(String table, String[] proj, String sel, String[] selArg) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(table, proj, sel, selArg, null)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray customerJson = jsonObject.getJSONArray("cursor");
                        for (int i = 0; i < customerJson.length(); i++) {
                            JSONObject obj = customerJson.getJSONObject(i);

                            String idForUpdateCust = "" + obj.getInt(ProdContract.Customer._ID);
                            updateCustomerManager(new String[]{idForUpdateCust});
                        }


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
        deleteStaffRequestQueue.add(volleyRequest);
    }

    void updateCustomerManager(String[] selectionArgCust){
        ArrayList<InsertMysql> insUpd=new ArrayList<>();
        insUpd.add(new InsertMysql(ProdContract.Customer.COLUMN_CUSTOMER_MANAGER_ID,"0"));
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Customer.TABLE_NAME
                , insUpd, ProdContract.Customer._ID + " = ?", selectionArgCust)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        staffDetailRequestQueue.add(volleyRequest);
    }
}
