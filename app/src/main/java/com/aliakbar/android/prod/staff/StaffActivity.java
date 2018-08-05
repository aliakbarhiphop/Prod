package com.aliakbar.android.prod.staff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.List;

import static com.aliakbar.android.prod.data.JsonExtract.extractDisplayStaffJson;

public class StaffActivity extends AppCompatActivity implements StaffAdapter.StaffAdapterOnClickListener {

    public static String CUSTOMER_NEED_ID, CUSTOMER_NEED_NAME;

    public static boolean CUSTOMER_MANAGER_FLAG = false;

    StaffAdapter mAdapter;

    RequestQueue staffRequestQueue, searchRequestQueue;

    ImageView searchIcon, backIcon, nameIcon;
    EditText searchText;
    LinearLayout searchFrame;

    RelativeLayout addFrame;

    FrameLayout emptyView;

    RecyclerView recyclerView;

    TextView nameSearch, idSearch, usernameSearch, clientSearch;

    int CURRENT_SELECT_SEARCH = 1;

    boolean SearchfirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        staffRequestQueue = Volley.newRequestQueue(StaffActivity.this);
        searchRequestQueue = Volley.newRequestQueue(StaffActivity.this);

        searchIcon = findViewById(R.id.staff_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchfirstTime) {
                    SearchfirstTime = false;
                    addFrame.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.GONE);
                    searchText.setVisibility(View.VISIBLE);
                    showKeybord(searchText);
                    searchFrame.setVisibility(View.VISIBLE);
                } else {
                    SearchfirstTime = true;
                    hideKeybord();
                    displayStaff();
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    CURRENT_SELECT_SEARCH = 1;
                    AllSelectBackgroundChangeToDefault();
                    nameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    searchFrame.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                }
            }
        });
        backIcon = findViewById(R.id.staff_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchfirstTime) {
                    finish();
                } else {
                    SearchfirstTime = true;
                    hideKeybord();
                    displayStaff();
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    CURRENT_SELECT_SEARCH = 1;
                    AllSelectBackgroundChangeToDefault();
                    nameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    searchFrame.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                    if (!CUSTOMER_MANAGER_FLAG){
                        addFrame.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        nameIcon = findViewById(R.id.staff_name);
        searchText = findViewById(R.id.staff_search_edit_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fromEditText = charSequence.toString();
                if (fromEditText.matches("")) {
                    displayStaff();
                } else {
                    String columnName = getColumnNameForSearch(CURRENT_SELECT_SEARCH);
                    String selectionSearch = ProdContract.Staff._ID + " > ? AND " + columnName + " LIKE ?";
                    String[] selectionArgsSerach = {"0", fromEditText + "%"};
                    searchStaff(selectionSearch, selectionArgsSerach);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchFrame = findViewById(R.id.staff_frame_search_id);

        addFrame = findViewById(R.id.staff_bottom_actionbar);
        addFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffActivity.this, StaffAddActivity.class);
                startActivity(intent);
            }
        });

        emptyView = findViewById(R.id.staff_empty_view);

        nameSearch = findViewById(R.id.staff_name_filter);
        nameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllSelectBackgroundChangeToDefault();
                CURRENT_SELECT_SEARCH = 1;
                nameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        idSearch = findViewById(R.id.staff_id_filter);
        idSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllSelectBackgroundChangeToDefault();
                CURRENT_SELECT_SEARCH = 2;
                idSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        usernameSearch = findViewById(R.id.staff_username_filter);
        usernameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllSelectBackgroundChangeToDefault();
                CURRENT_SELECT_SEARCH = 3;
                usernameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        clientSearch = findViewById(R.id.staff_client_filter);
        clientSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllSelectBackgroundChangeToDefault();
                CURRENT_SELECT_SEARCH = 4;
                clientSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        recyclerView = findViewById(R.id.staff_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new StaffAdapter(StaffActivity.this, StaffActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayStaff();
        if (CUSTOMER_MANAGER_FLAG) {
            addFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(String mStaffCurrentRaw, String currentName) {
        if (CUSTOMER_MANAGER_FLAG) {
            CUSTOMER_NEED_ID = mStaffCurrentRaw;
            CUSTOMER_NEED_NAME = currentName;
            finish();
        } else {
            Intent intent = new Intent(StaffActivity.this, StaffDetailActivity.class);
            intent.putExtra("selectedStaffId", mStaffCurrentRaw);
            startActivity(intent);
        }
    }

    void AllSelectBackgroundChangeToDefault() {
        nameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        idSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        usernameSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        clientSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    String getColumnNameForSearch(int contextNumber) {
        String retTableName;
        switch (contextNumber) {
            case 1:
                retTableName = ProdContract.Staff.COLUMN_STAFF_NAME;
                break;
            case 2:
                retTableName = ProdContract.Staff._ID;
                break;
            case 3:
                retTableName = ProdContract.Staff.COLUMN_STAFF_USERNAME;
                break;
            case 4:
                retTableName = ProdContract.Staff.COLUMN_STAFF_CLIENT;
                break;
            default:
                retTableName = null;
        }
        return retTableName;
    }

    void showKeybord(View kview) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        kview.requestFocus();
        inputMethodManager.showSoftInput(kview, 0);
    }

    void hideKeybord() {
        View Kview = StaffActivity.this.getCurrentFocus();
        if (Kview != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(Kview.getWindowToken(), 0);
            }
        }
    }

    void displayStaff() {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Staff.TABLE_NAME
                , new String[]{ProdContract.Staff._ID, ProdContract.Staff.COLUMN_STAFF_CLIENT, ProdContract.Staff.COLUMN_STAFF_NAME
                        , ProdContract.Staff.COLUMN_STAFF_USERNAME, ProdContract.Staff.COLUMN_STAFF_TOTAL, ProdContract.Staff.COLUMN_STAFF_ORDER
                        , ProdContract.Staff.COLUMN_STAFF_SALE}
                , ProdContract.Staff._ID + " > ?", new String[]{"0"}, ProdContract.Staff.COLUMN_STAFF_NAME + " ASC")
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Staff> stockListFromServer = extractDisplayStaffJson(jsonObject.getJSONArray("cursor"));
                        if (stockListFromServer != null) {
                            mAdapter.swapCursor(stockListFromServer);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        staffRequestQueue.add(volleyRequest);
    }

    void searchStaff(String selection, String[] selectionArgs) {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Staff.TABLE_NAME
                , new String[]{ProdContract.Staff._ID, ProdContract.Staff.COLUMN_STAFF_CLIENT, ProdContract.Staff.COLUMN_STAFF_NAME
                        , ProdContract.Staff.COLUMN_STAFF_USERNAME, ProdContract.Staff.COLUMN_STAFF_TOTAL, ProdContract.Staff.COLUMN_STAFF_ORDER
                        , ProdContract.Staff.COLUMN_STAFF_SALE}
                , selection, selectionArgs, ProdContract.Staff.COLUMN_STAFF_NAME + " ASC")
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Staff> stockListFromServer = extractDisplayStaffJson(jsonObject.getJSONArray("cursor"));
                        if (stockListFromServer != null) {
                            mAdapter.swapCursor(stockListFromServer);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StaffActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StaffActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StaffActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StaffActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StaffActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        staffRequestQueue.add(volleyRequest);
    }

}
