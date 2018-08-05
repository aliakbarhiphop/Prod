package com.aliakbar.android.prod.data;

public class InsertMysql {
    private String columnName;
    private String values;

    public InsertMysql(String mColumnName, String mValues) {
        columnName = mColumnName;
        values = mValues;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getValues() {
        return values;
    }
}
