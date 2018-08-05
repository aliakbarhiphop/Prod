package com.aliakbar.android.prod.network;

import com.aliakbar.android.prod.data.InsertMysql;
import com.aliakbar.android.prod.data.ProdContract;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class VolleyHelper {

    private static HashMap<String, String> returnParameter;

    public static HashMap<String, String> insertVolly(String tableName, List<InsertMysql> insertMysqls) {
        returnParameter = new HashMap<>();

        returnParameter.put("action", "insert");

        returnParameter.put("tableName", tableName);

        int insertSize = insertMysqls.size();
        String[] columnName = new String[insertSize];
        String[] insertValue = new String[insertSize];
        for (int i = 0; i < insertSize; i++) {
            InsertMysql insert = insertMysqls.get(i);
            columnName[i] = insert.getColumnName();
            insertValue[i] = insert.getValues();
        }
        JSONArray columnNameJsonArray = new JSONArray();
        for (String tempColumnName : columnName) {
            columnNameJsonArray.put(tempColumnName);
        }
        JSONArray insertValueJsonArray = new JSONArray();
        for (String tempInsertValue : insertValue) {
            insertValueJsonArray.put(tempInsertValue);
        }
        String columnNameJsonArrayString = columnNameJsonArray.toString();
        String insertValueJsonArrayString = insertValueJsonArray.toString();

        returnParameter.put("columnNames", columnNameJsonArrayString);

        returnParameter.put("insertValues", insertValueJsonArrayString);

        return returnParameter;
    }

    public static HashMap<String, String> selectVolly(String tableName, String[] projection, String selection, String[] selectionArg, String sortOrder) {
        returnParameter = new HashMap<>();

        returnParameter.put("action", "select");

        returnParameter.put("tableName", tableName);

        if (projection == null) {
            projection = setProjection(tableName);
        }

        JSONArray projectionJsonArray = new JSONArray();
        for (String tempProjection : projection) {
            projectionJsonArray.put(tempProjection);
        }
        String projectionJsonString = projectionJsonArray.toString();
        returnParameter.put("projection", projectionJsonString);

        if (selection != null) {

            returnParameter.put("selection", selection);

            JSONArray selectionArgsJsonArray = new JSONArray();
            for (String tempSelectionArg : selectionArg) {
                selectionArgsJsonArray.put(tempSelectionArg);
            }
            String selectionArgsJsonString = selectionArgsJsonArray.toString();
            returnParameter.put("selectionArg", selectionArgsJsonString);
        } else {
            returnParameter.put("selection", "");

            returnParameter.put("selectionArg", "");
        }

        if (sortOrder != null) {
            returnParameter.put("sortOrder", sortOrder);
        } else {
            returnParameter.put("sortOrder", "");
        }

        return returnParameter;
    }

    public static HashMap<String, String> updateVolly(String tableName, List<InsertMysql> updateMysqls, String selection, String[] selectionArg) {
        returnParameter = new HashMap<>();
        returnParameter.put("action", "update");

        returnParameter.put("tableName", tableName);

        int insertSize = updateMysqls.size();
        String[] columnName = new String[insertSize];
        String[] insertValue = new String[insertSize];
        for (int i = 0; i < insertSize; i++) {
            InsertMysql insert = updateMysqls.get(i);
            columnName[i] = insert.getColumnName();
            insertValue[i] = insert.getValues();
        }
        JSONArray columnNameJsonArray = new JSONArray();
        for (String tempColumnName : columnName) {
            columnNameJsonArray.put(tempColumnName);
        }
        JSONArray insertValueJsonArray = new JSONArray();
        for (String tempInsertValue : insertValue) {
            insertValueJsonArray.put(tempInsertValue);
        }
        String columnNameJsonArrayString = columnNameJsonArray.toString();
        String insertValueJsonArrayString = insertValueJsonArray.toString();

        returnParameter.put("columnNames", columnNameJsonArrayString);
        returnParameter.put("insertValues", insertValueJsonArrayString);

        if (selection != null) {

            returnParameter.put("selection", selection);

            JSONArray selectionArgsJsonArray = new JSONArray();
            for (String tempSelectionArg : selectionArg) {
                selectionArgsJsonArray.put(tempSelectionArg);
            }
            String selectionArgsJsonString = selectionArgsJsonArray.toString();
            returnParameter.put("selectionArg", selectionArgsJsonString);
        } else {
            returnParameter.put("selection", "");

            returnParameter.put("selectionArg", "");
        }

        return returnParameter;
    }

    public static HashMap<String, String> deleteVolly(String tableName, String selection, String[] selectionArgs) {
        returnParameter = new HashMap<>();
        returnParameter.put("action", "delete");

        returnParameter.put("tableName", tableName);

        if (selection != null) {

            returnParameter.put("selection", selection);

            JSONArray selectionArgsJsonArray = new JSONArray();
            for (String tempSelectionArg : selectionArgs) {
                selectionArgsJsonArray.put(tempSelectionArg);
            }
            String selectionArgsJsonString = selectionArgsJsonArray.toString();
            returnParameter.put("selectionArg", selectionArgsJsonString);
        } else {
            returnParameter.put("selection", "");

            returnParameter.put("selectionArg", "");
        }

        return returnParameter;
    }

    private static String[] setProjection(String tableName) {
        if (tableName.equals(ProdContract.Stock.TABLE_NAME)) {
            return new String[]{ProdContract.Stock._ID, ProdContract.Stock.COLUMN_ITEM_NAME, ProdContract.Stock.COLUMN_PRICE
                    , ProdContract.Stock.COLUMN_ORDER, ProdContract.Stock.COLUMN_SALES};
        } else if (tableName.equals(ProdContract.Customer.TABLE_NAME)) {
            return new String[]{ProdContract.Customer._ID, ProdContract.Customer.COLUMN_CUSTOMER_NAME
                    , ProdContract.Customer.COLUMN_TOTAL_DEAL, ProdContract.Customer.COLUMN_TOTAL, ProdContract.Customer.COLUMN_PAID
                    , ProdContract.Customer.COLUMN_CREDIT, ProdContract.Customer.COLUMN_ORDER};

        } else if (tableName.equals(ProdContract.Staff.TABLE_NAME)) {
            return new String[]{ProdContract.Staff._ID, ProdContract.Staff.COLUMN_STAFF_NAME, ProdContract.Staff.COLUMN_STAFF_USERNAME
                    , ProdContract.Staff.COLUMN_STAFF_PASSWORD, ProdContract.Staff.COLUMN_STAFF_CLIENT, ProdContract.Staff.COLUMN_STAFF_SALE
                    , ProdContract.Staff.COLUMN_STAFF_ORDER, ProdContract.Staff.COLUMN_STAFF_TOTAL, ProdContract.Staff.COLUMN_STAFF_PAID
                    , ProdContract.Staff.COLUMN_STAFF_CREDIT};
        }
        return null;
    }
}