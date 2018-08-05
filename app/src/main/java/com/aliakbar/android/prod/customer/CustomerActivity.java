package com.aliakbar.android.prod.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.bill.BillActivity;
import com.aliakbar.android.prod.data.Customer;
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

import static com.aliakbar.android.prod.customer.CustomerFilterActivity.creditGT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.creditLL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.creditLT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.creditUL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.dealsGT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.dealsLL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.dealsLT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.dealsUL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.orderGT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.orderLL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.orderLT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.orderUL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.paidGT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.paidLL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.paidLT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.paidUL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.totalGT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.totalLL;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.totalLT;
import static com.aliakbar.android.prod.customer.CustomerFilterActivity.totalUL;

public class CustomerActivity extends AppCompatActivity implements CustomerAdapter.CustomerAdapterOnClickListener {

    private Toast toast;

    public static boolean STAFF_MODE = false, BILL_FLAG = false;

    String selectionFilterString;
    String[] selectionArgFilterArray;

    ImageView nameIcon, backIcon;
    FrameLayout searchIcon, filterIcon;
    EditText searchText;

    RelativeLayout addItemFrame;

    LinearLayout frameFilter;
    TextView nameFilter, idFilter, managerFilter, dealsFilter, totalFilter, paidFilter, creditFilter, orderFilter;

    FrameLayout emptyView;

    boolean searchfirstTime = true;
    int CURRENT_SELECT_FILTER = 1;

    RecyclerView mRecyclerView;
    CustomerAdapter mAdapter;
    RequestQueue customerRequestQueue, searchRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        filterIcon = findViewById(R.id.customer_filter);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerActivity.this, CustomerFilterActivity.class);
                startActivity(intent);
            }
        });

        searchIcon = findViewById(R.id.customer_search);
        nameIcon = findViewById(R.id.customer_name);
        backIcon = findViewById(R.id.customer_back);

        searchText = findViewById(R.id.customer_search_edit_text);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchText.getRight() - searchText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        searchText.setText("");
                    }
                }
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fromEditText = charSequence.toString();
                if (fromEditText.matches("")) {
                    if (STAFF_MODE) {
                        helper();
                    } else {
                        displayCustomer();
                    }

                } else {
                    String columnName = getColumnNameForSearch(CURRENT_SELECT_FILTER);
                    String selectionSearch = columnName + " LIKE ?";
                    String[] selectionArgsSearch = {fromEditText + "%"};
                    if (STAFF_MODE) {
                        performStaff(selectionSearch, selectionArgsSearch);
                    } else {
                        searchCustomer(selectionSearch, selectionArgsSearch);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addItemFrame = findViewById(R.id.customer_bottom_actionbar);
        addItemFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerActivity.this, CustomerAddActivity.class);
                startActivity(intent);
            }
        });

        emptyView = findViewById(R.id.customer_empty_view);

        frameFilter = findViewById(R.id.customer_frame_search);
        nameFilter = findViewById(R.id.customer_name_filter);
        nameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 1;
                allFilterBackgroundChangeToDefault();
                nameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        idFilter = findViewById(R.id.customer_id_filter);
        idFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 2;
                allFilterBackgroundChangeToDefault();
                idFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        managerFilter = findViewById(R.id.customer_manager_filter);
        managerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 4;
                allFilterBackgroundChangeToDefault();
                managerFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        dealsFilter = findViewById(R.id.customer_deals_filter);
        dealsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 3;
                allFilterBackgroundChangeToDefault();
                dealsFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        totalFilter = findViewById(R.id.customer_total_filter);
        totalFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 5;
                allFilterBackgroundChangeToDefault();
                totalFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        paidFilter = findViewById(R.id.customer_paid_filter);
        paidFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 6;
                allFilterBackgroundChangeToDefault();
                paidFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        creditFilter = findViewById(R.id.customer_credit_filter);
        creditFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 7;
                allFilterBackgroundChangeToDefault();
                creditFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        orderFilter = findViewById(R.id.customer_order_filter);
        orderFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 8;
                allFilterBackgroundChangeToDefault();
                orderFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchfirstTime) {
                    searchfirstTime = false;
                    addItemFrame.setVisibility(View.GONE);
                    filterIcon.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.GONE);
                    searchText.setVisibility(View.VISIBLE);
                    showKeybord(searchText);
                    frameFilter.setVisibility(View.VISIBLE);
                } else {
                    searchfirstTime = true;
                    hideKeybord();
                    if (STAFF_MODE) {
                        helper();
                    } else {
                        filterIcon.setVisibility(View.VISIBLE);
                        addItemFrame.setVisibility(View.VISIBLE);
                        displayCustomer();
                    }
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    allFilterBackgroundChangeToDefault();
                    nameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    CURRENT_SELECT_FILTER = 1;
                    frameFilter.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                }
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchfirstTime) {
                    finish();
                } else {
                    searchfirstTime = true;
                    hideKeybord();
                    if (STAFF_MODE) {
                        helper();
                    } else {
                        addItemFrame.setVisibility(View.VISIBLE);
                        displayCustomer();
                        filterIcon.setVisibility(View.VISIBLE);
                    }
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    allFilterBackgroundChangeToDefault();
                    nameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    CURRENT_SELECT_FILTER = 1;
                    frameFilter.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                }
            }
        });

        mRecyclerView = findViewById(R.id.customer_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CustomerAdapter(CustomerActivity.this, CustomerActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        customerRequestQueue = Volley.newRequestQueue(CustomerActivity.this);
        searchRequestQueue = Volley.newRequestQueue(CustomerActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (STAFF_MODE || BILL_FLAG) {
            helper();
        } else {
            checkFilter();
            displayCustomer();
        }
    }

    @Override
    public void onClick(String mCustomerCurrentRaw, String mCustomerSelectedName) {
        if (BILL_FLAG) {
            BillActivity.customerId = mCustomerCurrentRaw;
            BillActivity.customerName = mCustomerSelectedName;
            finish();
        } else {
            Intent detailIntent = new Intent(CustomerActivity.this, CustomerDetailActivity.class);
            detailIntent.putExtra("selectedCustomerId", mCustomerCurrentRaw);
            startActivity(detailIntent);
        }
    }

    void showKeybord(View kview) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        kview.requestFocus();
        inputMethodManager.showSoftInput(kview, 0);
    }

    void hideKeybord() {
        View Kview = CustomerActivity.this.getCurrentFocus();
        if (Kview != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(Kview.getWindowToken(), 0);
            }
        }
    }

    private void allFilterBackgroundChangeToDefault() {
        nameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        idFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        managerFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        dealsFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        totalFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        paidFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        creditFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        orderFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private String getColumnNameForSearch(int contextNumber) {
        String tableNameForSearch = null;
        switch (contextNumber) {
            case 1:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME;
                break;

            case 2:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_ID;
                break;

            case 3:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL;
                break;

            case 4:
                tableNameForSearch = ProdContract.Staff.FULL_COLUMN_STAFF_NAME;
                break;

            case 5:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_TOTAL;
                break;

            case 6:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_PAID;
                break;

            case 7:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_CREDIT;
                break;

            case 8:
                tableNameForSearch = ProdContract.Customer.FULL_COLUMN_ORDER;
                break;
        }
        return tableNameForSearch;
    }

    void displayCustomer() {
        String[] projection = new String[]{ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Customer.FULL_COLUMN_ID
                , ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL, ProdContract.Customer.FULL_COLUMN_TOTAL
                , ProdContract.Customer.FULL_COLUMN_PAID, ProdContract.Customer.FULL_COLUMN_CREDIT, ProdContract.Customer.FULL_COLUMN_ORDER};

        String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;

        HashMap<String, String> hashMap;
        if (CustomerFilterActivity.filterOccured && !selectionFilterString.matches("")) {
            hashMap = VolleyHelper.selectVolly(
                    tableName, projection, selectionFilterString, selectionArgFilterArray, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC");
        } else {
            hashMap = VolleyHelper.selectVolly(
                    tableName, projection, null, null, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC");
        }
        final VolleyRequest volleyRequest = new VolleyRequest(hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Customer> customerListFromServer = JsonExtract.extractCustomerJson(jsonObject.getJSONArray("cursor"));
                        if (customerListFromServer != null) {
                            mAdapter.swapAdapter(customerListFromServer);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        customerRequestQueue.add(volleyRequest);
    }

    void searchCustomer(String searchSelection, String[] searchSelectionArg) {
        String[] projection = new String[]{ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Customer.FULL_COLUMN_ID
                , ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL, ProdContract.Customer.FULL_COLUMN_TOTAL
                , ProdContract.Customer.FULL_COLUMN_PAID, ProdContract.Customer.FULL_COLUMN_CREDIT, ProdContract.Customer.FULL_COLUMN_ORDER};

        String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;

        HashMap<String, String> hashMap;
        if (CustomerFilterActivity.filterOccured && !selectionFilterString.matches("")) {

            String[] searchFilterSelectionArg = new String[selectionArgFilterArray.length + 1];

            System.arraycopy(selectionArgFilterArray, 0, searchFilterSelectionArg, 0, selectionArgFilterArray.length);

            searchFilterSelectionArg[searchFilterSelectionArg.length - 1] = searchSelectionArg[0];

            selectionFilterString = selectionFilterString + " AND " + searchSelection;

            hashMap = VolleyHelper.selectVolly(
                    tableName, projection, selectionFilterString, searchFilterSelectionArg, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC");
        } else {
            hashMap = VolleyHelper.selectVolly(
                    tableName, projection, searchSelection, searchSelectionArg, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC");
        }


        final VolleyRequest volleyRequest = new VolleyRequest(hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Customer> customerListFromServer = JsonExtract.extractCustomerJson(jsonObject.getJSONArray("cursor"));
                        if (customerListFromServer != null) {
                            mAdapter.swapAdapter(customerListFromServer);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        searchRequestQueue.add(volleyRequest);
    }

    void checkFilter() {
        selectionArgFilterArray = new String[0];
        selectionFilterString = "";

        if (CustomerFilterActivity.filterOccured) {

            List<String> filterList = new ArrayList<>();

            //1
            if (dealsLT != 0) {
                selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL + " < ?";
                filterList.add("" + dealsLT);
            } else if (dealsGT != 0) {
                selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL + " > ?";
                filterList.add("" + dealsGT);
            } else if (dealsUL != 0 && dealsLL != 0) {
                if (dealsUL > dealsLL) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL + " BETWEEN ? AND ?";
                    filterList.add("" + dealsLL);
                    filterList.add("" + dealsUL);
                }
            }

            //CLEARING STAFF FILTER
            if (!CustomerFilterActivity.MANAGER_LIST_FILTER.isEmpty()) {
                if (selectionFilterString.matches("")) {

                    selectionFilterString = ProdContract.Staff.FULL_COLUMN_STAFF_NAME + " IN ( ? ";
                    for (int i = 1; i < CustomerFilterActivity.MANAGER_LIST_FILTER.size(); i++) {
                        selectionFilterString = selectionFilterString + ", ? ";
                    }
                    selectionFilterString = selectionFilterString + ")";

                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Staff.FULL_COLUMN_STAFF_NAME + " IN ( ? ";
                    for (int i = 1; i < CustomerFilterActivity.MANAGER_LIST_FILTER.size(); i++) {
                        selectionFilterString = selectionFilterString + ", ? ";
                    }
                    selectionFilterString = selectionFilterString + ")";

                }
                filterList.addAll(CustomerFilterActivity.MANAGER_LIST_FILTER);
            }


            //2
            if (orderLT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_ORDER + " < ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_ORDER + " < ?";
                }
                filterList.add("" + orderLT);
            } else if (orderGT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_ORDER + " > ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_ORDER + " > ?";
                }
                filterList.add("" + orderGT);
            } else if (orderUL != 0 && orderLL != 0) {
                if (orderUL > orderLL) {
                    if (selectionFilterString.matches("")) {
                        selectionFilterString = ProdContract.Customer.FULL_COLUMN_ORDER + " BETWEEN ? AND ?";
                    } else {
                        selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_ORDER + " BETWEEN ? AND ?";
                    }
                    filterList.add("" + orderLL);
                    filterList.add("" + orderUL);
                }
            }

            //3
            if (totalLT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL + " < ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_TOTAL + " < ?";
                }
                filterList.add("" + totalLT);
            } else if (totalGT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL + " > ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_TOTAL + " > ?";
                }
                filterList.add("" + totalGT);
            } else if (totalUL != 0 && totalLL != 0) {
                if (totalUL > totalLL) {
                    if (selectionFilterString.matches("")) {
                        selectionFilterString = ProdContract.Customer.FULL_COLUMN_TOTAL + " BETWEEN ? AND ?";
                    } else {
                        selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_TOTAL + " BETWEEN ? AND ?";
                    }
                    filterList.add("" + totalLL);
                    filterList.add("" + totalUL);
                }
            }

            //4
            if (paidLT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_PAID + " < ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_PAID + " < ?";
                }
                filterList.add("" + paidLT);
            } else if (paidGT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_PAID + " > ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_PAID + " > ?";
                }
                filterList.add("" + paidGT);
            } else if (paidUL != 0 && paidLL != 0) {
                if (paidUL > paidLL) {
                    if (selectionFilterString.matches("")) {
                        selectionFilterString = ProdContract.Customer.FULL_COLUMN_PAID + " BETWEEN ? AND ?";
                    } else {
                        selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_PAID + " BETWEEN ? AND ?";
                    }
                    filterList.add("" + paidLL);
                    filterList.add("" + paidUL);
                }
            }

            //5
            if (creditLT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_CREDIT + " < ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_CREDIT + " < ?";
                }
                filterList.add("" + creditLT);
            } else if (creditGT != 0) {
                if (selectionFilterString.matches("")) {
                    selectionFilterString = ProdContract.Customer.FULL_COLUMN_CREDIT + " > ?";
                } else {
                    selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_CREDIT + " > ?";
                }
                filterList.add("" + creditGT);
            } else if (creditUL != 0 && creditLL != 0) {
                if (creditUL > creditLL) {
                    if (selectionFilterString.matches("")) {
                        selectionFilterString = ProdContract.Customer.FULL_COLUMN_CREDIT + " BETWEEN ? AND ?";
                    } else {
                        selectionFilterString = selectionFilterString + " AND " + ProdContract.Customer.FULL_COLUMN_CREDIT + " BETWEEN ? AND ?";
                    }
                    filterList.add("" + creditLL);
                    filterList.add("" + creditUL);
                }
            }
            // END
            selectionArgFilterArray = filterList.toArray(new String[filterList.size()]);
        }
    }

    void helper() {
        filterIcon.setVisibility(View.GONE);
        addItemFrame.setVisibility(View.GONE);
        if (STAFF_MODE) {
            performStaff(null, null);
        } else {
            String[] projection = new String[]{ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Customer.FULL_COLUMN_ID
                    , ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL, ProdContract.Customer.FULL_COLUMN_TOTAL
                    , ProdContract.Customer.FULL_COLUMN_PAID, ProdContract.Customer.FULL_COLUMN_CREDIT, ProdContract.Customer.FULL_COLUMN_ORDER};

            String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                    + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;

            final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                    tableName, projection, null, null, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC")
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Login Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            List<Customer> customerListFromServer = JsonExtract.extractCustomerJson(jsonObject.getJSONArray("cursor"));
                            if (customerListFromServer != null) {
                                mAdapter.swapAdapter(customerListFromServer);
                                emptyView.setVisibility(View.GONE);
                            }
                        } else {
                            if (jsonObject.getString("status").equals("INVALID")) {
                                if (toast != null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(CustomerActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(CustomerActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                        toast = Toast.makeText(CustomerActivity.this, "Server Error", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (error instanceof TimeoutError) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(CustomerActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (error instanceof NetworkError) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(CustomerActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
            customerRequestQueue.add(volleyRequest);
        }

    }

    void performStaff(String select, String[] selectArg) {

        String[] projection = new String[]{ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME, ProdContract.Customer.FULL_COLUMN_ID
                , ProdContract.Staff.FULL_COLUMN_STAFF_NAME, ProdContract.Customer.FULL_COLUMN_TOTAL_DEAL, ProdContract.Customer.FULL_COLUMN_TOTAL
                , ProdContract.Customer.FULL_COLUMN_PAID, ProdContract.Customer.FULL_COLUMN_CREDIT, ProdContract.Customer.FULL_COLUMN_ORDER};

        String tableName = ProdContract.Customer.TABLE_NAME + " INNER JOIN " + ProdContract.Staff.TABLE_NAME
                + " ON " + ProdContract.Customer.FULL_COLUMN_CUSTOMER_MANAGER_ID + " = " + ProdContract.Staff.FULL_COLUMN_ID;


        String idFromStaff;
        Intent extractIdIntent = getIntent();
        idFromStaff = extractIdIntent.getStringExtra("idFromStaff");

        HashMap<String, String> hashMap;
        if (select == null) {
            String selectionSearch = ProdContract.Staff.FULL_COLUMN_ID + " = ?";
            String[] selectionArgsSearch = {idFromStaff};

            hashMap = VolleyHelper.selectVolly(tableName, projection, selectionSearch, selectionArgsSearch
                    , ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC ");
        } else {
            select = select + " AND " + ProdContract.Staff.FULL_COLUMN_ID + " = ?";
            String[] selectionArggs = new String[selectArg.length + 1];
            System.arraycopy(selectArg, 0, selectionArggs, 0, selectArg.length);
            selectionArggs[selectArg.length] = idFromStaff;
            hashMap = VolleyHelper.selectVolly(tableName, projection, select, selectionArggs, ProdContract.Customer.FULL_COLUMN_CUSTOMER_NAME + " ASC ");
        }


        final VolleyRequest volleyRequest = new VolleyRequest(hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Customer> customerListFromServer = JsonExtract.extractCustomerJson(jsonObject.getJSONArray("cursor"));
                        if (customerListFromServer != null) {
                            mAdapter.swapAdapter(customerListFromServer);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CustomerActivity.this, "User Not Found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT);
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
                    toast = Toast.makeText(CustomerActivity.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(CustomerActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        searchRequestQueue.add(volleyRequest);
    }
}
