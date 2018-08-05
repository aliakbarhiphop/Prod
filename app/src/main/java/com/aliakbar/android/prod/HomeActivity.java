package com.aliakbar.android.prod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.aliakbar.android.prod.bill.CartActivity;
import com.aliakbar.android.prod.customer.CustomerActivity;
import com.aliakbar.android.prod.customer.CustomerFilterActivity;
import com.aliakbar.android.prod.staff.StaffActivity;
import com.aliakbar.android.prod.stock.StockActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    CardView stockCard, customerCard, staffCard, addBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        stockCard = findViewById(R.id.home_stock);
        stockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stockActivity = new Intent(HomeActivity.this, StockActivity.class);
                startActivity(stockActivity);
            }
        });

        customerCard = findViewById(R.id.home_customer);
        customerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerFilterActivity.MANAGER_LIST_FILTER = new ArrayList<>();
                CustomerFilterActivity.initializeAll();
                Intent intent = new Intent(HomeActivity.this, CustomerActivity.class);
                startActivity(intent);
            }
        });

        staffCard = findViewById(R.id.home_staff);
        staffCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StaffActivity.class);
                startActivity(intent);
            }
        });

        addBill = findViewById(R.id.home_add_bill);
        addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }
}
