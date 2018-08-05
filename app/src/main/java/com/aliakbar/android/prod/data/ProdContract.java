package com.aliakbar.android.prod.data;

import android.provider.BaseColumns;

public class ProdContract {
    public static final class Stock implements BaseColumns {
        public static final String TABLE_NAME = "stock";

        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ORDER = "orders";
        public static final String COLUMN_SALES = "sales";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_ITEM_NAME = TABLE_NAME + "." + COLUMN_ITEM_NAME;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
        public static final String FULL_COLUMN_ORDER = TABLE_NAME + "." + COLUMN_ORDER;
        public static final String FULL_COLUMN_SALES = TABLE_NAME + "." + COLUMN_SALES;

    }

    public static final class Customer implements BaseColumns {
        public static final String TABLE_NAME = "customer";

        public static final String COLUMN_CUSTOMER_MANAGER_ID = "managerId";
        public static final String COLUMN_CUSTOMER_NAME = "name";
        public static final String COLUMN_TOTAL_DEAL = "deals";
        public static final String COLUMN_PAID = "paid";
        public static final String COLUMN_CREDIT = "credit";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_ORDER = "orders";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_CUSTOMER_MANAGER_ID = TABLE_NAME + "." + COLUMN_CUSTOMER_MANAGER_ID;
        public static final String FULL_COLUMN_CUSTOMER_NAME = TABLE_NAME + "." + COLUMN_CUSTOMER_NAME;
        public static final String FULL_COLUMN_TOTAL_DEAL = TABLE_NAME + "." + COLUMN_TOTAL_DEAL;
        public static final String FULL_COLUMN_PAID = TABLE_NAME + "." + COLUMN_PAID;
        public static final String FULL_COLUMN_CREDIT = TABLE_NAME + "." + COLUMN_CREDIT;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_ORDER = TABLE_NAME + "." + COLUMN_ORDER;
    }

    public static final class Staff implements BaseColumns {
        public static final String TABLE_NAME = "staff";

        public static final String COLUMN_STAFF_USERNAME = "username";
        public static final String COLUMN_STAFF_NAME = "name";
        public static final String COLUMN_STAFF_PASSWORD = "password";

        public static final String COLUMN_STAFF_CLIENT = "client";
        public static final String COLUMN_STAFF_SALE = "sales";
        public static final String COLUMN_STAFF_TOTAL = "total";
        public static final String COLUMN_STAFF_PAID = "paid";
        public static final String COLUMN_STAFF_CREDIT = "credit";
        public static final String COLUMN_STAFF_ORDER = "orders";


        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_STAFF_USERNAME = TABLE_NAME + "." + COLUMN_STAFF_USERNAME;
        public static final String FULL_COLUMN_STAFF_NAME = TABLE_NAME + "." + COLUMN_STAFF_NAME;
        public static final String FULL_COLUMN_STAFF_PASSWORD = TABLE_NAME + "." + COLUMN_STAFF_PASSWORD;

        public static final String FULL_COLUMN_STAFF_CLIENT = TABLE_NAME + "." + COLUMN_STAFF_CLIENT;
        public static final String FULL_COLUMN_STAFF_SALE = TABLE_NAME + "." + COLUMN_STAFF_SALE;
        public static final String FULL_COLUMN_STAFF_TOTAL = TABLE_NAME + "." + COLUMN_STAFF_TOTAL;
        public static final String FULL_COLUMN_STAFF_PAID = TABLE_NAME + "." + COLUMN_STAFF_PAID;
        public static final String FULL_COLUMN_STAFF_CREDIT = TABLE_NAME + "." + COLUMN_STAFF_CREDIT;
        public static final String FULL_COLUMN_STAFF_ORDER = TABLE_NAME + "." + COLUMN_STAFF_ORDER;

    }


    public static final class Bill implements BaseColumns {
        public static final String TABLE_NAME = "bill";

        public static final String COLUMN_DATE = "billDate";
        public static final String COLUMN_CUSTOMER_ID = "customerId";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_DISCOUNT_PER = "discPer";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_PAY = "pay";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_CUSTOMER_ID = TABLE_NAME + "." + COLUMN_CUSTOMER_ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_DISCOUNT_PER = TABLE_NAME + "." + COLUMN_DISCOUNT_PER;
        public static final String FULL_COLUMN_DISCOUNT = TABLE_NAME + "." + COLUMN_DISCOUNT;
        public static final String FULL_COLUMN_PAY = TABLE_NAME + "." + COLUMN_PAY;
    }


    public static final class Cart implements BaseColumns {
        public static final String TABLE_NAME = "cart";

        public static final String COLUMN_BILL_ID = "billId";
        public static final String COLUMN_ITEM_ID = "itemId";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_DISCOUNT_PER = "discPer";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_PAY = "pay";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_BILL_ID = TABLE_NAME + "." + COLUMN_BILL_ID;
        public static final String FULL_COLUMN_ITEM_ID = TABLE_NAME + "." + COLUMN_ITEM_ID;
        public static final String FULL_COLUMN_QUANTITY = TABLE_NAME + "." + COLUMN_QUANTITY;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_DISCOUNT_PER = TABLE_NAME + "." + COLUMN_DISCOUNT_PER;
        public static final String FULL_COLUMN_DISCOUNT = TABLE_NAME + "." + COLUMN_DISCOUNT;
        public static final String FULL_COLUMN_PAY = TABLE_NAME + "." + COLUMN_PAY;
    }


    public static final class Payment implements BaseColumns {
        public static final String TABLE_NAME = "payment";

        public static final String COLUMN_DATE = "paymentDate";
        public static final String COLUMN_BILL_ID = "billId";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_PAID = "paid";
        public static final String COLUMN_CREDIT = "credit";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_BILL_ID = TABLE_NAME + "." + COLUMN_BILL_ID;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_PAID = TABLE_NAME + "." + COLUMN_PAID;
        public static final String FULL_COLUMN_CREDIT = TABLE_NAME + "." + COLUMN_CREDIT;
    }
}


