package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderFormActivity extends AppCompatActivity {
    private EditText etId, etTable, etDishes, etTotal;
    private RadioGroup rgDining;
    private DatabaseManager db;
    private int orderId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        etId = findViewById(R.id.etId);
        etTable = findViewById(R.id.etTable);
        etDishes = findViewById(R.id.etDishes);
        etTotal = findViewById(R.id.etTotal);
        rgDining = findViewById(R.id.rgDining);

        db = new DatabaseManager(this);

        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", -1);
            loadOrder(orderId);
            findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveOrder());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteOrder());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadOrder(int id) {
        Cursor c = db.getAllOrders();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == id) {
                    etId.setText(String.valueOf(c.getInt(0)));

                    String dining = c.getString(1);
                    if ("Dine-in".equalsIgnoreCase(dining)) {
                        rgDining.check(R.id.rbDineIn);
                    } else if ("Take-away".equalsIgnoreCase(dining)) {
                        rgDining.check(R.id.rbTakeAway);
                    }

                    etTable.setText(c.getString(2));
                    etDishes.setText(c.getString(3));
                    etTotal.setText(String.valueOf(c.getDouble(4)));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    private String getSelectedDiningOption() {
        int checkedId = rgDining.getCheckedRadioButtonId();
        if (checkedId == R.id.rbDineIn) return "Dine-in";
        if (checkedId == R.id.rbTakeAway) return "Take-away";
        return "";
    }

    private void saveOrder() {
        String idStr = etId.getText().toString().trim();
        String dining = getSelectedDiningOption();
        String table = etTable.getText().toString().trim();
        String dishes = etDishes.getText().toString().trim();
        String totalStr = etTotal.getText().toString().trim();

        if (idStr.isEmpty() || dining.isEmpty() || dishes.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "ID, dining option, dishes, and total are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        float total = Float.parseFloat(totalStr);

        if (orderId == -1) { // new order
            boolean ok = db.addOrder(id, dining, table, dishes, total);
            if (!ok) {
                Toast.makeText(this, "Error: Order ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // update existing
            db.updateOrder(id, dining, table, dishes, total);
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
