package com.example.assignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager  {

    public static final String DB_NAME = "mealOrder";
    public static final int DB_VERSION = 1;
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    // Dish table
    private static final String TABLE_DISH = "dishes";
    private static final String COL_DISH_ID = "id";
    private static final String COL_DISH_NAME = "name";
    private static final String COL_DISH_TYPE = "type";
    private static final String COL_DISH_ING = "ingredients";
    private static final String COL_DISH_PRICE = "price";

    // Order table
    private static final String TABLE_ORDER = "orders";
    private static final String COL_ORDER_ID = "id";
    private static final String COL_ORDER_DINING = "diningOption";
    private static final String COL_ORDER_TABLE = "tableNumber";
    private static final String COL_ORDER_DISHES = "dishNames";
    private static final String COL_ORDER_TOTAL = "totalPrice";

    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }
    public DatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }
    public void close() {
        helper.close();
    }

}
