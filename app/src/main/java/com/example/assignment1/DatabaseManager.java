package com.example.assignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager {
    public static final String DB_NAME = "mealOrder";
    public static final int DB_VERSION = 1;

    // ---- Dish Table ----
    public static final String TABLE_DISH = "dishes";
    public static final String COL_DISH_ID = "id";
    public static final String COL_DISH_NAME = "name";
    public static final String COL_DISH_TYPE = "type";
    public static final String COL_DISH_ING = "ingredients";
    public static final String COL_DISH_PRICE = "price";

    private static final String CREATE_TABLE_DISH =
            "CREATE TABLE " + TABLE_DISH + " (" +
                    COL_DISH_ID + " INTEGER PRIMARY KEY, " +
                    COL_DISH_NAME + " TEXT, " +
                    COL_DISH_TYPE + " TEXT, " +
                    COL_DISH_ING + " TEXT, " +
                    COL_DISH_PRICE + " FLOAT);";

    // ---- Order Table ----
    public static final String TABLE_ORDER = "orders";
    public static final String COL_ORDER_ID = "id";
    public static final String COL_ORDER_DINING = "diningOption";
    public static final String COL_ORDER_TABLE = "tableNumber";
    public static final String COL_ORDER_DISHES = "dishNames";
    public static final String COL_ORDER_TOTAL = "totalPrice";

    private static final String CREATE_TABLE_ORDER =
            "CREATE TABLE " + TABLE_ORDER + " (" +
                    COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ORDER_DINING + " TEXT, " +
                    COL_ORDER_TABLE + " TEXT, " +
                    COL_ORDER_DISHES + " TEXT, " +
                    COL_ORDER_TOTAL + " FLOAT);";

    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

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

    public boolean addDish(int id, String name, String type, String ingredients, float price) {
        synchronized (this.db) {
            ContentValues values = new ContentValues();
            values.put(COL_DISH_ID, id);   // now saving entered ID
            values.put(COL_DISH_NAME, name);
            values.put(COL_DISH_TYPE, type);
            values.put(COL_DISH_ING, ingredients);
            values.put(COL_DISH_PRICE, price);
            try {
                db.insertOrThrow(TABLE_DISH, null, values);
                return true;
            } catch (Exception e) {
                Log.e("DB_ERROR", "Insert dish failed: " + e.toString());
                return false;
            }
        }
    }

    public Cursor getAllDishes() {
        String[] columns = {COL_DISH_ID, COL_DISH_NAME, COL_DISH_TYPE, COL_DISH_ING, COL_DISH_PRICE};
        return db.query(TABLE_DISH, columns, null, null, null, null, null);
    }

    public void updateDish(int id, String name, String type, String ingredients, float price) {
        ContentValues values = new ContentValues();
        values.put(COL_DISH_NAME, name);
        values.put(COL_DISH_TYPE, type);
        values.put(COL_DISH_ING, ingredients);
        values.put(COL_DISH_PRICE, price);
        db.update(TABLE_DISH, values, COL_DISH_ID + "=" + id, null);
    }

    public void deleteDish(int id) {
        db.delete(TABLE_DISH, COL_DISH_ID + "=" + id, null);
    }

    public boolean addOrder(int id, String diningOption, String tableNumber, String dishNames, Float totalPrice) {
        synchronized (this.db) {
            ContentValues values = new ContentValues();
            values.put(COL_ORDER_ID, id);   // save entered ID
            values.put(COL_ORDER_DINING, diningOption);
            values.put(COL_ORDER_TABLE, tableNumber);
            values.put(COL_ORDER_DISHES, dishNames);
            values.put(COL_ORDER_TOTAL, totalPrice);
            try {
                db.insertOrThrow(TABLE_ORDER, null, values);
                return true;
            } catch (Exception e) {
                Log.e("DB_ERROR", "Insert order failed: " + e.toString());
                return false;
            }
        }
    }

    public Cursor getAllOrders() {
        String[] columns = {COL_ORDER_ID, COL_ORDER_DINING, COL_ORDER_TABLE, COL_ORDER_DISHES, COL_ORDER_TOTAL};
        return db.query(TABLE_ORDER, columns, null, null, null, null, null);
    }

    public void updateOrder(int id, String dining, String table, String dishNames, Float total) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_DINING, dining);
        values.put(COL_ORDER_TABLE, table);
        values.put(COL_ORDER_DISHES, dishNames);
        values.put(COL_ORDER_TOTAL, total);
        db.update(TABLE_ORDER, values, COL_ORDER_ID + "=" + id, null);
    }

    public void deleteOrder(int id) {
        db.delete(TABLE_ORDER, COL_ORDER_ID + "=" + id, null);
    }

    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c) {
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_DISH);
            db.execSQL(CREATE_TABLE_ORDER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database", "Upgrading database: dropping and recreating tables");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISH);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            onCreate(db);
        }
    }
}