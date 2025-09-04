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

public class DishListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DishListAdapter adapter;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);

        rv = findViewById(R.id.rvDishes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseManager(this);
        loadDishes();

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, DishFormActivity.class));
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDishes();
    }

    private void loadDishes() {
        Cursor c = db.getAllDishes();
        List<Dish> dishes = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Dish d = new Dish(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getDouble(4)
                );
                dishes.add(d);
            } while (c.moveToNext());
        }
        if (!c.isClosed()) c.close();

        adapter = new DishListAdapter(dishes, dishId -> {
            Intent i = new Intent(DishListActivity.this, DishFormActivity.class);
            i.putExtra("dishId", dishId);
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }
}
