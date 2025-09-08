package com.example.assignment1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        rv = findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseManager(this);
        loadOrders();

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, OrderFormActivity.class));
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        Cursor c = db.getAllOrders();
        List<Order> orders = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                orders.add(new Order(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getDouble(4),
                        c.getString(5)
                ));
            } while (c.moveToNext());
        }
        c.close();

        OrderListAdapter adapter = new OrderListAdapter(orders, orderId -> {
            Intent i = new Intent(this, OrderFormActivity.class);
            i.putExtra("orderId", orderId);
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(); // refresh when returning
    }
}
