package com.aliakbar.android.prod.stock;

import android.annotation.SuppressLint;
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
import com.aliakbar.android.prod.bill.AddCart;
import com.aliakbar.android.prod.data.ProdContract;
import com.aliakbar.android.prod.data.Stock;
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

import static com.aliakbar.android.prod.data.JsonExtract.extractStockJson;

public class StockActivity extends AppCompatActivity
        implements StockAdapter.StockAdapterOnClickListener {

    public static boolean CART_FLAG = false;

    private StockAdapter mAdapter;
    private RecyclerView mRecyclerView;

    RequestQueue stockRequestQueue, filterRequestQueue;

    RelativeLayout addNewFrame;

    ImageView searchIcon, nameIcon, backIcon;
    EditText searchText;
    boolean SearchfirstTime = true;
    LinearLayout optionFrameSearch;
    TextView mNameFilter, mIdFilter;
    private int CURRENT_SELECT_FILTER = 1;

    FrameLayout mEmptyView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        mEmptyView = (FrameLayout) findViewById(R.id.stock_empty_view);

        nameIcon = (ImageView) findViewById(R.id.stock_name);
        nameIcon.setVisibility(View.VISIBLE);

        searchText = (EditText) findViewById(R.id.stock_search_edit_text);
        searchText.setVisibility(View.GONE);
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
                    displayStock();
                } else {
                    String columnName = getColumnNameForSearch(CURRENT_SELECT_FILTER);
                    String selectionSearch = columnName + " LIKE ?";
                    String[] selectionArgsSerach = {fromEditText + "%"};
                    filterStock(selectionSearch, selectionArgsSerach);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        optionFrameSearch = (LinearLayout) findViewById(R.id.stock_frame_search_id);
        optionFrameSearch.setVisibility(View.GONE);

        searchIcon = (ImageView) findViewById(R.id.stock_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchfirstTime) {
                    SearchfirstTime = false;
                    addNewFrame.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.GONE);
                    searchText.setVisibility(View.VISIBLE);
                    showKeybord(searchText);
                    optionFrameSearch.setVisibility(View.VISIBLE);
                } else {
                    SearchfirstTime = true;
                    hideKeybord();
                    displayStock();
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    CURRENT_SELECT_FILTER = 1;
                    AllSelectBackgroundChangeToDefault();
                    mNameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    optionFrameSearch.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                    if (!CART_FLAG)
                        addNewFrame.setVisibility(View.VISIBLE);
                }
            }
        });

        backIcon = (ImageView) findViewById(R.id.stock_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchfirstTime) {
                    finish();
                } else {
                    SearchfirstTime = true;
                    hideKeybord();
                    displayStock();
                    searchText.setText("");
                    searchText.setVisibility(View.GONE);
                    CURRENT_SELECT_FILTER = 1;
                    AllSelectBackgroundChangeToDefault();
                    mNameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    optionFrameSearch.setVisibility(View.GONE);
                    nameIcon.setVisibility(View.VISIBLE);
                    if (!CART_FLAG)
                        addNewFrame.setVisibility(View.VISIBLE);
                }
            }
        });

        mNameFilter = (TextView) findViewById(R.id.stock_name_filter);
        mNameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 1;
                AllSelectBackgroundChangeToDefault();
                mNameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        mIdFilter = (TextView) findViewById(R.id.stock_id_filter);
        mIdFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_SELECT_FILTER = 2;
                AllSelectBackgroundChangeToDefault();
                mIdFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.stock_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StockAdapter(StockActivity.this, StockActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        addNewFrame = (RelativeLayout) findViewById(R.id.stock_bottom_actionbar);
        addNewFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StockAdd = new Intent(StockActivity.this, StockAddActivity.class);
                startActivity(StockAdd);
            }
        });

        stockRequestQueue = Volley.newRequestQueue(StockActivity.this);
        filterRequestQueue = Volley.newRequestQueue(StockActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CART_FLAG) {
            if (CART_FLAG) {
                addNewFrame.setVisibility(View.GONE);
            }
        }
        displayStock();
    }

    @Override
    public void onClick(String mSStockId, String mSStockName, String mSStockPrice) {
        if (CART_FLAG) {
            AddCart.FROM_STOCK_ID = mSStockId;
            AddCart.FROM_STOCK_NAME = mSStockName;
            AddCart.FROM_STOCK_PRICE = mSStockPrice;
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            finish();
        } else {
            Intent detailIntent = new Intent(StockActivity.this, StockDetailActivity.class);
            detailIntent.putExtra("selectedStockId", mSStockId);
            startActivity(detailIntent);
        }
    }

    void AllSelectBackgroundChangeToDefault() {
        mNameFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mIdFilter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    String getColumnNameForSearch(int contextNumber) {
        String retTableName;
        switch (contextNumber) {
            case 1:
                retTableName = ProdContract.Stock.COLUMN_ITEM_NAME;
                break;
            case 2:
                retTableName = ProdContract.Stock._ID;
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
        View Kview = StockActivity.this.getCurrentFocus();
        if (Kview != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(Kview.getWindowToken(), 0);
            }
        }
    }

    void displayStock() {
        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Stock.TABLE_NAME
                , new String[]{ProdContract.Stock._ID, ProdContract.Stock.COLUMN_ITEM_NAME, ProdContract.Stock.COLUMN_PRICE
                        , ProdContract.Stock.COLUMN_ORDER, ProdContract.Stock.COLUMN_SALES}
                , null, null, ProdContract.Stock.COLUMN_ITEM_NAME + " ASC")
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Stock> stockListFromServer = extractStockJson(jsonObject.getJSONArray("cursor"));
                        if (stockListFromServer != null) {
                            mAdapter.swapCursor(stockListFromServer);
                            mEmptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        stockRequestQueue.add(volleyRequest);
    }

    void filterStock(String filterSelection, String[] filterSelectionArg) {

        final VolleyRequest volleyRequest = new VolleyRequest(VolleyHelper.selectVolly(
                ProdContract.Stock.TABLE_NAME
                , new String[]{ProdContract.Stock._ID, ProdContract.Stock.COLUMN_ITEM_NAME, ProdContract.Stock.COLUMN_PRICE
                        , ProdContract.Stock.COLUMN_ORDER, ProdContract.Stock.COLUMN_SALES}
                , filterSelection, filterSelectionArg, ProdContract.Stock.COLUMN_ITEM_NAME + " ASC")
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Login Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {

                        List<Stock> stockListFromServer = extractStockJson(jsonObject.getJSONArray("cursor"));
                        if (stockListFromServer != null) {
                            mAdapter.swapCursor(stockListFromServer);
                            mEmptyView.setVisibility(View.GONE);
                        }

                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            Toast.makeText(StockActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("status").equals("EMPTY DATABASE")) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(StockActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(StockActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(StockActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
        filterRequestQueue.add(volleyRequest);
    }
}
