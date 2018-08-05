package com.aliakbar.android.prod.staff;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractStaffJson;

public class StaffAddActivity extends AppCompatActivity {

    EditText nameText, usernameText, passwordText, saleText, orderText, totalText, paidText, creditText;

    ImageView backIcon, saveIcon;

    String idForUpdate;

    boolean clientFirstTime = true;
    boolean saleFirstTime = true;
    boolean orderFirstTime = true;
    boolean totalFirstTime = true;
    boolean paidFirstTime = true;
    boolean creditFirstTime = true;

    boolean ediTextTouched = false;

    public static boolean editableMode = false;

    RequestQueue addStaffRequestQueue, staffEditRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add);

        addStaffRequestQueue = Volley.newRequestQueue(this);
        staffEditRequestQueue = Volley.newRequestQueue(this);

        backIcon = findViewById(R.id.add_staff_back);
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

        nameText = findViewById(R.id.add_staff_name);
        nameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ediTextTouched = true;
                return false;
            }
        });
        usernameText = findViewById(R.id.add_staff_username);
        usernameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ediTextTouched = true;
                return false;
            }
        });
        passwordText = findViewById(R.id.add_staff_password);
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ediTextTouched = true;
                return false;
            }
        });


        if (editableMode) {

            saleText = findViewById(R.id.add_staff_sale);
            saleText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            orderText = findViewById(R.id.add_staff_order);
            orderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            totalText = findViewById(R.id.add_staff_total);
            totalText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            paidText = findViewById(R.id.add_staff_paid);
            paidText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
            creditText = findViewById(R.id.add_staff_credit);
            creditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    return false;
                }
            });
        } else {



            saleText = findViewById(R.id.add_staff_sale);
            saleText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (saleFirstTime) {
                        saleFirstTime = false;
                        saleText.setText("");
                    }
                    return false;
                }
            });
            orderText = findViewById(R.id.add_staff_order);
            orderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (orderFirstTime) {
                        orderText.setText("");
                        orderFirstTime = false;
                    }
                    return false;
                }
            });
            totalText = findViewById(R.id.add_staff_total);
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
            paidText = findViewById(R.id.add_staff_paid);
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
            creditText = findViewById(R.id.add_staff_credit);
            creditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ediTextTouched = true;
                    if (clientFirstTime) {
                        creditFirstTime = false;
                        creditText.setText("");
                    }
                    return false;
                }
            });
        }
        saveIcon = findViewById(R.id.add_staff_save);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, username, password, client="0", order, sale, total, paid, credit;
                name = nameText.getText().toString().trim();
                username = usernameText.getText().toString().trim();
                password = passwordText.getText().toString().trim();


                order = orderText.getText().toString().trim();
                sale = saleText.getText().toString().trim();
                total = totalText.getText().toString().trim();
                paid = paidText.getText().toString().trim();
                credit = creditText.getText().toString().trim();
                if (name.matches("") || username.matches("") || password.matches("")
                        || order.matches("") || sale.matches("") || total.matches("")
                        || paid.matches("") || credit.matches("")) {
                    Toast toast = Toast.makeText(StaffAddActivity.this, "Some Fields Are Empty", Toast.LENGTH_SHORT);
                    if (toast.getView().isShown()) {
                        toast.cancel();
                    }
                    toast.show();
                } else {
                    List<InsertMysql> values = new ArrayList<>();
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_NAME, name));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_USERNAME, username));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_PASSWORD, password));

                    if(!editableMode){
                        values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_CLIENT, client));
                    }

                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_ORDER, order));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_SALE, sale));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_TOTAL, total));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_PAID, paid));
                    values.add(new InsertMysql(ProdContract.Staff.COLUMN_STAFF_CREDIT, credit));
                    if (editableMode) {
                        updateStaff(values);
                    } else {
                        saveStaff(values);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (editableMode) {
            Intent extractIdIntent = getIntent();
            String idFromIntent = extractIdIntent.getStringExtra("selectedStaffId");
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


    void saveStaff(List<InsertMysql> insert) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.insertVolly(ProdContract.Staff.TABLE_NAME, insert)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StaffAddActivity.this, "Saved Successfully. Id :" + jsonObject.getInt("idOfLastInsertedRaw")
                                , Toast.LENGTH_SHORT).show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("INSERT ERROR")) {
                            Toast.makeText(StaffAddActivity.this, "Failed To Insert", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        addStaffRequestQueue.add(volleyRequest);
    }

    void setUpEditMode(String idEdit) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idEdit};
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
                        nameText.setText(name);

                        String username = staff.getUsername();
                        usernameText.setText(username);

                        String password = staff.getPassword();
                        passwordText.setText(password);

                        String sales = "" + staff.getSales();
                        saleText.setText(sales);

                        String order = "" + staff.getOrder();
                        orderText.setText(order);

                        String total = "" + staff.getTotal();
                        totalText.setText(total);

                        String paid = "" + staff.getPaid();
                        paidText.setText(paid);

                        String credit = "" + staff.getCredit();
                        creditText.setText(credit);

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            Toast.makeText(StaffAddActivity.this, "Database Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        staffEditRequestQueue.add(volleyRequest);
    }

    void updateStaff(List<InsertMysql> insert) {
        String selection = "_id=?";
        String selectionArgs[] = new String[]{idForUpdate};
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.updateVolly(ProdContract.Staff.TABLE_NAME, insert, selection, selectionArgs)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        Toast.makeText(StaffAddActivity.this, "Update Successfully. Affected :" + jsonObject.getInt("affectedRaw")
                                        + " Raw"
                                , Toast.LENGTH_SHORT).show();
                        ediTextTouched = false;
                        finish();

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffAddActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("UPDATE ERROR")) {
                            Toast.makeText(StaffAddActivity.this, "Failed To Update", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffAddActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffAddActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffAddActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffAddActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        addStaffRequestQueue.add(volleyRequest);
    }
}
