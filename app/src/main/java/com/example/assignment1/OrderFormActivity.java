package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderFormActivity extends AppCompatActivity {
    private EditText etDining, etTable, etDishes, etTotal;
    private DatabaseManager db;
    private int orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        etDining = findViewById(R.id.etDining);
        etTable = findViewById(R.id.etTable);
        etDishes = findViewById(R.id.etDishes);
        etTotal = findViewById(R.id.etTotal);

        db = new DatabaseManager(this);

        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", -1);
            loadOrder(orderId);
            findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveOrder());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteOrder());
    }

    private void loadOrder(int id) {
        Cursor c = db.getAllOrders();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == id) {
                    etDining.setText(c.getString(1));
                    etTable.setText(c.getString(2));
                    etDishes.setText(c.getString(3));
                    etTotal.setText(String.valueOf(c.getDouble(4)));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    private void saveOrder() {
        String dining = etDining.getText().toString().trim();
        String table = etTable.getText().toString().trim();
        String dishes = etDishes.getText().toString().trim();
        String totalStr = etTotal.getText().toString().trim();

        if (dining.isEmpty() || dishes.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "Dining, dishes, and total are required", Toast.LENGTH_SHORT).show();
            return;
        }

        float total = Float.parseFloat(totalStr);

        if (orderId == -1) {
            db.addOrder(dining, table, dishes, total);
        } else {
            db.updateOrder(orderId, dining, table, dishes, total);
        }
        finish();
    }

    private void deleteOrder() {
        if (orderId != -1) {
            db.deleteOrder(orderId);
            finish();
        }
    }
}
