package com.aliakbar.android.prod.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.staff.StaffActivity;

import java.util.ArrayList;

public class CustomerFilterActivity extends AppCompatActivity {
    Toast toast;
    RecyclerView staffFilterRecycler;
    CustomerStaffFilterAdapter mAdapterStaff;
    public static ArrayList<String> MANAGER_LIST_FILTER;

    public static boolean filterOccured = false;

    FrameLayout clearAllFrame;

    Button dealsClear, orderClear, totalClear, paidClear, creditClear;

    EditText dealsULText, dealsLLText, dealsLTText, dealsGTText;
    EditText orderULText, orderLLText, orderLTText, orderGTText;
    EditText totalULText, totalLLText, totalLTText, totalGTText;
    EditText paidULText, paidLLText, paidLTText, paidGTText;
    EditText creditULText, creditLLText, creditLTText, creditGTText;

    public static int dealsUL, dealsLL, dealsLT, dealsGT;
    public static int orderUL, orderLL, orderLT, orderGT;
    public static int totalUL, totalLL, totalLT, totalGT;
    public static int paidUL, paidLL, paidLT, paidGT;
    public static int creditUL, creditLL, creditLT, creditGT;

    android.support.design.widget.TextInputLayout dealsLTLay, dealsGTLay;
    android.support.design.widget.TextInputLayout orderLTLay, orderGTLay;
    android.support.design.widget.TextInputLayout totalLTLay, totalGTLay;
    android.support.design.widget.TextInputLayout paidLTLay, paidGTLay;
    android.support.design.widget.TextInputLayout creditLTLay, creditGTLay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_filter);

        initializeEditTextFrame();

        ImageView backIcon = findViewById(R.id.customer_filter_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dealsULText = findViewById(R.id.customer_filter_deals_ul);
        dealsULText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                dealsLTLay.setErrorEnabled(false);
                dealsGTLay.setErrorEnabled(false);


                try {


                    if (!dealsULText.getText().toString().trim().matches("")) {
                        ul = Integer.parseInt(dealsULText.getText().toString().trim());
                        dealsLTLay.setError("Less Than UL");
                        dealsLTLay.setErrorEnabled(true);
                        if (!dealsLLText.getText().toString().trim().matches("")) {
                            ll = Integer.parseInt(dealsLLText.getText().toString().trim());
                            if (ul > ll) {
                                dealsLTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                dealsGTLay.setError("Greater Than LL");
                                dealsGTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!dealsLLText.getText().toString().trim().matches("")) {
                            dealsLTLay.setError("Less Than UL");
                            dealsLTLay.setErrorEnabled(true);
                            dealsGTLay.setError("Greater Than LL");
                            dealsGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dealsULText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dealsLTText.setText("");
                dealsGTText.setText("");
                return false;
            }
        });
        dealsLLText = findViewById(R.id.customer_filter_deals_ll);
        dealsLLText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                dealsLTLay.setErrorEnabled(false);
                dealsGTLay.setErrorEnabled(false);
                try {
                    if (!dealsLLText.getText().toString().trim().matches("")) {
                        ll = Integer.parseInt(dealsLLText.getText().toString().trim());
                        dealsGTLay.setError("Greater Than LL");
                        dealsGTLay.setErrorEnabled(true);
                        if (!dealsULText.getText().toString().trim().matches("")) {
                            ul = Integer.parseInt(dealsULText.getText().toString().trim());
                            if (ul > ll) {
                                dealsGTLay.setError("Greater Than LL");
                                dealsGTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                dealsLTLay.setError("Less Than UL");
                                dealsLTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!dealsULText.getText().toString().trim().matches("")) {
                            dealsLTLay.setError("Less Than UL");
                            dealsLTLay.setErrorEnabled(true);
                            dealsGTLay.setError("Greater Than LL");
                            dealsGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dealsLLText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dealsLTText.setText("");
                dealsGTText.setText("");
                return false;
            }
        });
        dealsGTText = findViewById(R.id.customer_filter_deals_gt);
        dealsGTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dealsLTText.setText("");
                dealsULText.setText("");
                dealsLLText.setText("");
                return false;
            }
        });
        dealsLTText = findViewById(R.id.customer_filter_deals_lt);
        dealsLTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dealsGTText.setText("");
                dealsULText.setText("");
                dealsLLText.setText("");
                return false;
            }
        });
        dealsClear = findViewById(R.id.customer_filter_deals_clear);
        dealsClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeDeals();
                dealsULText.setText("");
                dealsLLText.setText("");
                dealsGTText.setText("");
                dealsLTText.setText("");
            }
        });

        orderULText = findViewById(R.id.customer_filter_order_ul);
        orderULText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                orderLTLay.setErrorEnabled(false);
                orderGTLay.setErrorEnabled(false);
                try {
                    if (!orderULText.getText().toString().trim().matches("")) {
                        ul = Integer.parseInt(orderULText.getText().toString().trim());
                        orderLTLay.setError("Less Than UL");
                        orderLTLay.setErrorEnabled(true);
                        if (!orderLLText.getText().toString().trim().matches("")) {
                            ll = Integer.parseInt(orderLLText.getText().toString().trim());
                            if (ul > ll) {
                                orderLTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                orderGTLay.setError("Greater Than LL");
                                orderGTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!orderLLText.getText().toString().trim().matches("")) {
                            orderLTLay.setError("Less Than UL");
                            orderLTLay.setErrorEnabled(true);
                            orderGTLay.setError("Greater Than LL");
                            orderGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        orderULText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                orderGTText.setText("");
                orderLTText.setText("");
                return false;
            }
        });
        orderLLText = findViewById(R.id.customer_filter_order_ll);
        orderLLText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                orderLTLay.setErrorEnabled(false);
                orderGTLay.setErrorEnabled(false);
                try {
                    if (!orderLLText.getText().toString().trim().matches("")) {
                        ll = Integer.parseInt(orderLLText.getText().toString().trim());
                        orderGTLay.setError("Greater Than LL");
                        orderGTLay.setErrorEnabled(true);
                        if (!orderULText.getText().toString().trim().matches("")) {
                            ul = Integer.parseInt(orderULText.getText().toString().trim());
                            if (ul > ll) {
                                orderGTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                orderLTLay.setError("Less Than UL");
                                orderLTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!orderULText.getText().toString().trim().matches("")) {
                            orderLTLay.setError("Less Than UL");
                            orderLTLay.setErrorEnabled(true);
                            orderGTLay.setError("Greater Than LL");
                            orderGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        orderLLText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                orderGTText.setText("");
                orderLTText.setText("");
                return false;
            }
        });
        orderGTText = findViewById(R.id.customer_filter_order_gt);
        orderGTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                orderLTText.setText("");
                orderLLText.setText("");
                orderULText.setText("");
                return false;
            }
        });
        orderLTText = findViewById(R.id.customer_filter_order_lt);
        orderLTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                orderGTText.setText("");
                orderLLText.setText("");
                orderULText.setText("");
                return false;
            }
        });
        orderClear = findViewById(R.id.customer_filter_order_clear);
        orderClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeOrder();
                orderULText.setText("");
                orderLLText.setText("");
                orderGTText.setText("");
                orderLTText.setText("");
            }
        });

        totalULText = findViewById(R.id.customer_filter_total_ul);
        totalULText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                totalLTLay.setErrorEnabled(false);
                totalGTLay.setErrorEnabled(false);
                try {
                    if (!totalULText.getText().toString().trim().matches("")) {
                        ul = Integer.parseInt(totalULText.getText().toString().trim());
                        totalLTLay.setError("Less Than UL");
                        totalLTLay.setErrorEnabled(true);
                        if (!totalLLText.getText().toString().trim().matches("")) {
                            ll = Integer.parseInt(totalLLText.getText().toString().trim());
                            if (ul > ll) {
                                totalLTLay.setError("Less Than UL");
                                totalLTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                totalGTLay.setError("Greater Than LL");
                                totalGTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!totalLLText.getText().toString().trim().matches("")) {
                            totalLTLay.setError("Less Than UL");
                            totalLTLay.setErrorEnabled(true);
                            totalGTLay.setError("Greater Than LL");
                            totalGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        totalULText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalLTText.setText("");
                totalGTText.setText("");
                return false;
            }
        });
        totalLLText = findViewById(R.id.customer_filter_total_ll);
        totalLLText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                totalLTLay.setErrorEnabled(false);
                totalGTLay.setErrorEnabled(false);
                try {
                    if (!totalLLText.getText().toString().trim().matches("")) {
                        ll = Integer.parseInt(totalLLText.getText().toString().trim());
                        totalGTLay.setError("Greater Than LL");
                        totalGTLay.setErrorEnabled(true);
                        if (!totalULText.getText().toString().trim().matches("")) {
                            ul = Integer.parseInt(totalULText.getText().toString().trim());
                            if (ul > ll) {
                                totalGTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                totalLTLay.setError("Less Than UL");
                                totalLTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!totalULText.getText().toString().trim().matches("")) {
                            totalLTLay.setError("Less Than UL");
                            totalLTLay.setErrorEnabled(true);
                            totalGTLay.setError("Greater Than LL");
                            totalGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        totalLLText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalLTText.setText("");
                totalGTText.setText("");
                return false;
            }
        });
        totalGTText = findViewById(R.id.customer_filter_total_gt);
        totalGTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalLLText.setText("");
                totalLTText.setText("");
                totalULText.setText("");
                return false;
            }
        });
        totalLTText = findViewById(R.id.customer_filter_total_lt);
        totalLTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalLLText.setText("");
                totalGTText.setText("");
                totalULText.setText("");
                return false;
            }
        });
        totalClear = findViewById(R.id.customer_filter_total_clear);
        totalClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeTotal();
                totalULText.setText("");
                totalLLText.setText("");
                totalGTText.setText("");
                totalLTText.setText("");
            }
        });

        paidULText = findViewById(R.id.customer_filter_paid_ul);
        paidULText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                paidLTLay.setErrorEnabled(false);
                paidGTLay.setErrorEnabled(false);
                try {
                    if (!paidULText.getText().toString().trim().matches("")) {
                        ul = Integer.parseInt(paidULText.getText().toString().trim());
                        paidLTLay.setError("Less Than UL");
                        paidLTLay.setErrorEnabled(true);
                        if (!paidLLText.getText().toString().trim().matches("")) {
                            ll = Integer.parseInt(paidLLText.getText().toString().trim());
                            if (ul > ll) {
                                paidLTLay.setError("Less Than UL");
                                paidLTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                paidGTLay.setError("Greater Than LL");
                                paidGTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!paidLLText.getText().toString().trim().matches("")) {
                            paidLTLay.setError("Less Than UL");
                            paidLTLay.setErrorEnabled(true);
                            paidGTLay.setError("Greater Than LL");
                            paidGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        paidULText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paidGTText.setText("");
                paidLTText.setText("");
                return false;
            }
        });
        paidLLText = findViewById(R.id.customer_filter_paid_ll);
        paidLLText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                paidLTLay.setErrorEnabled(false);
                paidGTLay.setErrorEnabled(false);
                try {
                    if (!paidLLText.getText().toString().trim().matches("")) {
                        ll = Integer.parseInt(paidLLText.getText().toString().trim());
                        paidGTLay.setError("Greater Than LL");
                        paidGTLay.setErrorEnabled(true);
                        if (!paidULText.getText().toString().trim().matches("")) {
                            ul = Integer.parseInt(paidULText.getText().toString().trim());
                            if (ul > ll) {
                                paidGTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                paidLTLay.setError("Less Than UL");
                                paidLTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!paidULText.getText().toString().trim().matches("")) {
                            paidLTLay.setError("Less Than UL");
                            paidLTLay.setErrorEnabled(true);
                            paidGTLay.setError("Greater Than LL");
                            paidGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        paidLLText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paidGTText.setText("");
                paidLTText.setText("");
                return false;
            }
        });
        paidGTText = findViewById(R.id.customer_filter_paid_gt);
        paidGTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paidLTText.setText("");
                paidLLText.setText("");
                paidULText.setText("");
                return false;
            }
        });
        paidLTText = findViewById(R.id.customer_filter_paid_lt);
        paidLTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paidGTText.setText("");
                paidLLText.setText("");
                paidULText.setText("");
                return false;
            }
        });
        paidClear = findViewById(R.id.customer_filter_paid_clear);
        paidClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializePaid();
                paidULText.setText("");
                paidLLText.setText("");
                paidGTText.setText("");
                paidLTText.setText("");
            }
        });

        creditULText = findViewById(R.id.customer_filter_credit_ul);
        creditULText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                creditLTLay.setErrorEnabled(false);
                creditGTLay.setErrorEnabled(false);
                try {
                    if (!creditULText.getText().toString().trim().matches("")) {
                        ul = Integer.parseInt(creditULText.getText().toString().trim());
                        creditLTLay.setError("Less Than UL");
                        creditLTLay.setErrorEnabled(true);
                        if (!creditLLText.getText().toString().trim().matches("")) {
                            ll = Integer.parseInt(creditLLText.getText().toString().trim());
                            if (ul > ll) {
                                creditLTLay.setError("Less Than UL");
                                creditLTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                creditGTLay.setError("Greater Than LL");
                                creditGTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!creditLLText.getText().toString().trim().matches("")) {
                            creditLTLay.setError("Less Than UL");
                            creditLTLay.setErrorEnabled(true);
                            creditGTLay.setError("Greater Than LL");
                            creditGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        creditULText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creditGTText.setText("");
                creditLTText.setText("");
                return false;
            }
        });
        creditLLText = findViewById(R.id.customer_filter_credit_ll);
        creditLLText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ul, ll;
                creditLTLay.setErrorEnabled(false);
                creditGTLay.setErrorEnabled(false);
                try {
                    if (!creditLLText.getText().toString().trim().matches("")) {
                        ll = Integer.parseInt(creditLLText.getText().toString().trim());
                        creditGTLay.setError("Greater Than LL");
                        creditGTLay.setErrorEnabled(true);
                        if (!creditULText.getText().toString().trim().matches("")) {
                            ul = Integer.parseInt(creditULText.getText().toString().trim());
                            if (ul > ll) {
                                creditGTLay.setErrorEnabled(false);
                            } else if (ul < ll) {
                                creditLTLay.setError("Less Than UL");
                                creditLTLay.setErrorEnabled(true);
                            }
                        }
                    } else {
                        if (!creditULText.getText().toString().trim().matches("")) {
                            creditLTLay.setError("Less Than UL");
                            creditLTLay.setErrorEnabled(true);
                            creditGTLay.setError("Greater Than LL");
                            creditGTLay.setErrorEnabled(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.i("", " is not a number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        creditLLText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creditGTText.setText("");
                creditLTText.setText("");
                return false;
            }
        });
        creditGTText = findViewById(R.id.customer_filter_credit_gt);
        creditGTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creditLLText.setText("");
                creditULText.setText("");
                creditLTText.setText("");
                return false;
            }
        });
        creditLTText = findViewById(R.id.customer_filter_credit_lt);
        creditLTText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creditLLText.setText("");
                creditULText.setText("");
                creditGTText.setText("");
                return false;
            }
        });
        creditClear = findViewById(R.id.customer_filter_credit_clear);
        creditClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeCredit();
                creditULText.setText("");
                creditLLText.setText("");
                creditGTText.setText("");
                creditLTText.setText("");
            }
        });

        clearAllFrame = findViewById(R.id.customer_filter_clear_all);
        clearAllFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MANAGER_LIST_FILTER = new ArrayList<>();
                mAdapterStaff.swapCursor(MANAGER_LIST_FILTER);
                initializeAll();
                dealsULText.setText("");
                dealsLLText.setText("");
                dealsGTText.setText("");
                dealsLTText.setText("");

                orderULText.setText("");
                orderLLText.setText("");
                orderGTText.setText("");
                orderLTText.setText("");

                totalULText.setText("");
                totalLLText.setText("");
                totalGTText.setText("");
                totalLTText.setText("");

                paidULText.setText("");
                paidLLText.setText("");
                paidGTText.setText("");
                paidLTText.setText("");

                creditULText.setText("");
                creditLLText.setText("");
                creditGTText.setText("");
                creditLTText.setText("");
            }
        });


        staffFilterRecycler = findViewById(R.id.customer_filter_staff_filter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        staffFilterRecycler.setLayoutManager(layoutManager);
        staffFilterRecycler.setHasFixedSize(true);
        mAdapterStaff = new CustomerStaffFilterAdapter(this, MANAGER_LIST_FILTER);
        staffFilterRecycler.setAdapter(mAdapterStaff);

        Button staffListClear = findViewById(R.id.customer_filter_staff_filter_clear);
        staffListClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MANAGER_LIST_FILTER = new ArrayList<>();
                mAdapterStaff.swapCursor(MANAGER_LIST_FILTER);
            }
        });
        Button staffListAdd = findViewById(R.id.customer_filter_staff_filter_add);
        staffListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerFilterActivity.this, StaffActivity.class);
                StaffActivity.CUSTOMER_MANAGER_FLAG = true;
                startActivity(intent);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final RecyclerView.ViewHolder tempHolde = viewHolder;
                MANAGER_LIST_FILTER.remove(tempHolde.getAdapterPosition());
                mAdapterStaff.swapCursor(MANAGER_LIST_FILTER);
            }
        }).attachToRecyclerView(staffFilterRecycler);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StaffActivity.CUSTOMER_MANAGER_FLAG) {
            StaffActivity.CUSTOMER_MANAGER_FLAG = false;


            if (!MANAGER_LIST_FILTER.isEmpty()) {
                boolean isThere = false;
                for (int i = 0; i < MANAGER_LIST_FILTER.size(); i++) {
                    if (MANAGER_LIST_FILTER.get(i).equals(StaffActivity.CUSTOMER_NEED_NAME)) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(CustomerFilterActivity.this, "This Is Already Added", Toast.LENGTH_LONG);toast.show();
                        isThere = true;
                    }
                }
                if (!isThere) {
                    MANAGER_LIST_FILTER.add(StaffActivity.CUSTOMER_NEED_NAME);
                    mAdapterStaff.swapCursor(MANAGER_LIST_FILTER);
                }
            } else {
                MANAGER_LIST_FILTER.add(StaffActivity.CUSTOMER_NEED_NAME);
                mAdapterStaff.swapCursor(MANAGER_LIST_FILTER);
            }


            StaffActivity.CUSTOMER_NEED_ID = "";
            StaffActivity.CUSTOMER_NEED_NAME = "";
        }
        fillFilter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        initializeAll();
        addFilter();

    }

    void initializeEditTextFrame() {
        dealsLTLay = findViewById(R.id.customer_filter_deals_ll_);
        dealsGTLay = findViewById(R.id.customer_filter_deals_ul_);

        orderLTLay = findViewById(R.id.customer_filter_order_ll_);
        orderGTLay = findViewById(R.id.customer_filter_order_ul_);

        totalLTLay = findViewById(R.id.customer_filter_total_ll_);
        totalGTLay = findViewById(R.id.customer_filter_total_ul_);

        paidLTLay = findViewById(R.id.customer_filter_paid_ll_);
        paidGTLay = findViewById(R.id.customer_filter_paid_ul_);

        creditLTLay = findViewById(R.id.customer_filter_credit_ll_);
        creditGTLay = findViewById(R.id.customer_filter_credit_ul_);
    }

    void fillFilter() {
        //1
        if (dealsUL != 0) {
            dealsULText.setText("" + dealsUL);
        }
        if (dealsLL != 0) {
            dealsLLText.setText("" + dealsLL);
        }
        if (dealsLT != 0) {
            dealsLTText.setText("" + dealsLT);
        }
        if (dealsGT != 0) {
            dealsGTText.setText("" + dealsGT);
        }

        //2

        if (orderUL != 0) {
            orderULText.setText("" + orderUL);
        }
        if (orderLL != 0) {
            orderLLText.setText("" + orderLL);
        }
        if (orderLT != 0) {
            orderLTText.setText("" + orderLT);
        }
        if (orderGT != 0) {
            orderGTText.setText("" + orderGT);
        }

        //3

        if (totalUL != 0) {
            totalULText.setText("" + totalUL);
        }
        if (totalLL != 0) {
            totalLLText.setText("" + totalLL);
        }
        if (totalLT != 0) {
            totalLTText.setText("" + totalLT);
        }
        if (totalGT != 0) {
            totalGTText.setText("" + totalGT);
        }

        //4
        if (paidUL != 0) {
            paidULText.setText("" + paidUL);
        }
        if (paidLL != 0) {
            paidLLText.setText("" + paidLL);
        }
        if (paidLT != 0) {
            paidLTText.setText("" + paidLT);
        }
        if (paidGT != 0) {
            paidGTText.setText("" + paidGT);
        }

        //5
        if (creditUL != 0) {
            creditULText.setText("" + creditUL);
        }
        if (creditLL != 0) {
            creditLLText.setText("" + creditLL);
        }
        if (creditLT != 0) {
            creditLTText.setText("" + creditLT);
        }
        if (creditGT != 0) {
            creditGTText.setText("" + creditGT);
        }
    }

    void addFilter() {
        //SETTING STAFF NAME
        if (!MANAGER_LIST_FILTER.isEmpty()) {
            filterOccured = true;
        }

        //1
        if (!dealsULText.getText().toString().trim().equals("") && !dealsLLText.getText().toString().trim().equals("")) {
            if (Integer.parseInt(dealsULText.getText().toString().trim()) > Integer.parseInt(dealsLLText.getText().toString().trim())) {
                dealsUL = Integer.parseInt(dealsULText.getText().toString().trim());
                dealsLL = Integer.parseInt(dealsLLText.getText().toString().trim());
                filterOccured = true;
            }
        }
        if (!dealsGTText.getText().toString().trim().equals("")) {
            dealsGT = Integer.parseInt(dealsGTText.getText().toString().trim());
            filterOccured = true;
        }
        if (!dealsLTText.getText().toString().trim().equals("")) {
            dealsLT = Integer.parseInt(dealsLTText.getText().toString().trim());
            filterOccured = true;
        }

        //2

        if (!orderULText.getText().toString().trim().equals("") && !orderLLText.getText().toString().trim().equals("")) {
            if (Integer.parseInt(orderULText.getText().toString().trim()) > Integer.parseInt(orderLLText.getText().toString().trim())) {
                orderUL = Integer.parseInt(orderULText.getText().toString().trim());
                orderLL = Integer.parseInt(orderLLText.getText().toString().trim());
                filterOccured = true;
            }
        }
        if (!orderGTText.getText().toString().trim().equals("")) {
            orderGT = Integer.parseInt(orderGTText.getText().toString().trim());
            filterOccured = true;
        }
        if (!orderLTText.getText().toString().trim().equals("")) {
            orderLT = Integer.parseInt(orderLTText.getText().toString().trim());
            filterOccured = true;
        }
        //3

        if (!totalULText.getText().toString().trim().equals("") && !totalLLText.getText().toString().trim().equals("")) {
            if (Integer.parseInt(totalULText.getText().toString().trim()) > Integer.parseInt(totalLLText.getText().toString().trim())) {
                totalUL = Integer.parseInt(totalULText.getText().toString().trim());
                totalLL = Integer.parseInt(totalLLText.getText().toString().trim());
                filterOccured = true;
            }

        }
        if (!totalGTText.getText().toString().trim().equals("")) {
            totalGT = Integer.parseInt(totalGTText.getText().toString().trim());
            filterOccured = true;
        }
        if (!totalLTText.getText().toString().trim().equals("")) {
            totalLT = Integer.parseInt(totalLTText.getText().toString().trim());
            filterOccured = true;
        }
        //4

        if (!paidULText.getText().toString().trim().equals("") && !paidLLText.getText().toString().trim().equals("")) {
            if (Integer.parseInt(paidULText.getText().toString().trim()) > Integer.parseInt(paidLLText.getText().toString().trim())) {
                paidUL = Integer.parseInt(paidULText.getText().toString().trim());
                paidLL = Integer.parseInt(paidLLText.getText().toString().trim());
                filterOccured = true;
            }

        }
        if (!paidGTText.getText().toString().trim().equals("")) {
            paidGT = Integer.parseInt(paidGTText.getText().toString().trim());
            filterOccured = true;
        }
        if (!paidLTText.getText().toString().trim().equals("")) {
            paidLT = Integer.parseInt(paidLTText.getText().toString().trim());
            filterOccured = true;
        }
        //5

        if (!creditULText.getText().toString().trim().equals("") && !creditLLText.getText().toString().trim().equals("")) {
            if (Integer.parseInt(creditULText.getText().toString().trim()) > Integer.parseInt(creditLLText.getText().toString().trim())) {
                creditUL = Integer.parseInt(creditULText.getText().toString().trim());
                creditLL = Integer.parseInt(creditLLText.getText().toString().trim());
                filterOccured = true;
            }
        }
        if (!creditGTText.getText().toString().trim().equals("")) {
            creditGT = Integer.parseInt(creditGTText.getText().toString().trim());
            filterOccured = true;
        }
        if (!creditLTText.getText().toString().trim().equals("")) {
            creditLT = Integer.parseInt(creditLTText.getText().toString().trim());
            filterOccured = true;
        }
    }

    public static void initializeAll() {
        filterOccured = false;
        initializeDeals();
        initializeOrder();
        initializeTotal();
        initializePaid();
        initializeCredit();
    }

    public static void initializeDeals() {
        dealsUL = 0;
        dealsLL = 0;
        dealsGT = 0;
        dealsLT = 0;
    }

    public static void initializeOrder() {
        orderUL = 0;
        orderLL = 0;
        orderGT = 0;
        orderLT = 0;
    }

    public static void initializeTotal() {
        totalUL = 0;
        totalLL = 0;
        totalGT = 0;
        totalLT = 0;
    }

    public static void initializePaid() {
        paidUL = 0;
        paidLL = 0;
        paidGT = 0;
        paidLT = 0;
    }

    public static void initializeCredit() {
        creditUL = 0;
        creditLL = 0;
        creditGT = 0;
        creditLT = 0;
    }
}
