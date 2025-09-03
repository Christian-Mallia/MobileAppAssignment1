package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class DishFormActivity extends AppCompatActivity {
    private EditText etName, etType, etIngredients, etPrice;
    private DatabaseManager db;
    private int dishId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_form);

        etName = findViewById(R.id.etName);
        etType = findViewById(R.id.etType);
        etIngredients = findViewById(R.id.etIngredients);
        etPrice = findViewById(R.id.etPrice);

        db = new DatabaseManager(this);

        // Check if editing existing dish
        if (getIntent().hasExtra("dishId")) {
            dishId = getIntent().getIntExtra("dishId", -1);
            loadDish(dishId);
            findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveDish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteDish());
    }

    private void loadDish(int id) {
        Cursor c = db.getAllDishes();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == id) {
                    etName.setText(c.getString(1));
                    etType.setText(c.getString(2));
                    etIngredients.setText(c.getString(3));
                    etPrice.setText(String.valueOf(c.getDouble(4)));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    private void saveDish() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String ing = etIngredients.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name, type, and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        float price = Float.parseFloat(priceStr);

        if (dishId == -1) {
            db.addDish(name, type, ing, price);
        } else {
            db.updateDish(dishId, name, type, ing, price);
        }
        finish();
    }

    private void deleteDish() {
        if (dishId != -1) {
            db.deleteDish(dishId);
            finish();
        }
    }
}
