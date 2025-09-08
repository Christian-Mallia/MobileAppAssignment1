package com.example.assignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager {

    public static final String DB_NAME = "restaurant.db";
    public static final int DB_VERSION = 2;  // bumped because we added "status"

    // ---------- DISHES ----------
    public static final String TABLE_DISH = "dishes";
    public static final String COL_DISH_ID = "id";
    public static final String COL_DISH_NAME = "name";
    public static final String COL_DISH_TYPE = "type";
    public static final String COL_DISH_INGREDIENTS = "ingredients";
    public static final String COL_DISH_PRICE = "price";

    private static final String CREATE_TABLE_DISH =
            "CREATE TABLE " + TABLE_DISH + " (" +
                    COL_DISH_ID + " INTEGER PRIMARY KEY, " +
                    COL_DISH_NAME + " TEXT, " +
                    COL_DISH_TYPE + " TEXT, " +
                    COL_DISH_INGREDIENTS + " TEXT, " +
                    COL_DISH_PRICE + " FLOAT);";

    // ---------- ORDERS ----------
    public static final String TABLE_ORDER = "orders";
    public static final String COL_ORDER_ID = "id";
    public static final String COL_ORDER_DINING = "diningOption";
    public static final String COL_ORDER_TABLE = "tableNumber";
    public static final String COL_ORDER_DISHES = "dishNames";
    public static final String COL_ORDER_TOTAL = "totalPrice";
    public static final String COL_ORDER_STATUS = "status"; // NEW

    private static final String CREATE_TABLE_ORDER =
            "CREATE TABLE " + TABLE_ORDER + " (" +
                    COL_ORDER_ID + " INTEGER PRIMARY KEY, " +
                    COL_ORDER_DINING + " TEXT, " +
                    COL_ORDER_TABLE + " TEXT, " +
                    COL_ORDER_DISHES + " TEXT, " +
                    COL_ORDER_TOTAL + " FLOAT, " +
                    COL_ORDER_STATUS + " TEXT DEFAULT 'Pending');";

    // ---------- DB Helper ----------
    private SQLHelper helper;
    private SQLiteDatabase db;

    public DatabaseManager(Context context) {
        helper = new SQLHelper(context);
        db = helper.getWritableDatabase();
    }

    // ---------- DISH CRUD ----------
    public boolean addDish(int id, String name, String type, String ingredients, float price) {
        ContentValues values = new ContentValues();
        values.put(COL_DISH_ID, id);
        values.put(COL_DISH_NAME, name);
        values.put(COL_DISH_TYPE, type);
        values.put(COL_DISH_INGREDIENTS, ingredients);
        values.put(COL_DISH_PRICE, price);

        try {
            db.insertOrThrow(TABLE_DISH, null, values);
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert dish failed: " + e.toString());
            return false;
        }
    }

    public Cursor getAllDishes() {
        return db.query(TABLE_DISH, null, null, null, null, null, null);
    }

    public void updateDish(int id, String name, String type, String ingredients, float price) {
        ContentValues values = new ContentValues();
        values.put(COL_DISH_NAME, name);
        values.put(COL_DISH_TYPE, type);
        values.put(COL_DISH_INGREDIENTS, ingredients);
        values.put(COL_DISH_PRICE, price);

        db.update(TABLE_DISH, values, COL_DISH_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteDish(int id) {
        db.delete(TABLE_DISH, COL_DISH_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ---------- ORDER CRUD ----------
    public boolean addOrder(int id, String dining, String table, String dishes, float total) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_ID, id);
        values.put(COL_ORDER_DINING, dining);
        values.put(COL_ORDER_TABLE, table);
        values.put(COL_ORDER_DISHES, dishes);
        values.put(COL_ORDER_TOTAL, total);
        values.put(COL_ORDER_STATUS, "Pending");

        try {
            db.insertOrThrow(TABLE_ORDER, null, values);
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert order failed: " + e.toString());
            return false;
        }
    }

    public Cursor getAllOrders() {
        return db.query(TABLE_ORDER, null, null, null, null, null, null);
    }

    public void updateOrder(int id, String dining, String table, String dishes, float total) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_DINING, dining);
        values.put(COL_ORDER_TABLE, table);
        values.put(COL_ORDER_DISHES, dishes);
        values.put(COL_ORDER_TOTAL, total);
        // do not overwrite status when editing
        db.update(TABLE_ORDER, values, COL_ORDER_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteOrder(int id) {
        db.delete(TABLE_ORDER, COL_ORDER_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void markOrderDone(int id) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_STATUS, "Done");
        db.update(TABLE_ORDER, values, COL_ORDER_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ---------- SQL Helper ----------
    private static class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_DISH);
            db.execSQL(CREATE_TABLE_ORDER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISH);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            onCreate(db);
        }
    }
}
