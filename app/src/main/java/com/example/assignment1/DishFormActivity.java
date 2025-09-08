package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DishFormActivity extends AppCompatActivity {
    private EditText etID, etName, etIngredients, etPrice;
    private RadioGroup rgType;
    private DatabaseManager db;
    private int dishId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_form);

        etID = findViewById(R.id.etID);
        etName = findViewById(R.id.etName);
        etIngredients = findViewById(R.id.etIngredients);
        etPrice = findViewById(R.id.etPrice);
        rgType = findViewById(R.id.rgType);

        db = new DatabaseManager(this);

        if (getIntent().hasExtra("dishId")) {
            dishId = getIntent().getIntExtra("dishId", -1);
            loadDish(dishId);
            findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveDish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteDish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadDish(int id) {
        Cursor c = db.getAllDishes();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == id) {
                    etID.setText(String.valueOf(c.getInt(0)));
                    etName.setText(c.getString(1));
                    String type = c.getString(2);
                    etIngredients.setText(c.getString(3));
                    etPrice.setText(String.valueOf(c.getDouble(4)));

                    if ("Entree".equalsIgnoreCase(type)) rgType.check(R.id.rbEntree);
                    else if ("Main".equalsIgnoreCase(type)) rgType.check(R.id.rbMain);
                    else if ("Drink".equalsIgnoreCase(type)) rgType.check(R.id.rbDrink);

                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    private String getSelectedType() {
        int checkedId = rgType.getCheckedRadioButtonId();
        if (checkedId == R.id.rbEntree) return "Entree";
        if (checkedId == R.id.rbMain) return "Main";
        if (checkedId == R.id.rbDrink) return "Drink";
        return "";
    }

    private void saveDish() {
        String idStr = etID.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String type = getSelectedType();
        String ing = etIngredients.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (idStr.isEmpty() || name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "ID, name, type, and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        float price = Float.parseFloat(priceStr);

        if (dishId == -1) { // adding new
            boolean ok = db.addDish(id, name, type, ing, price);
            if (!ok) {
                Toast.makeText(this, "Error: Dish ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            db.updateDish(id, name, type, ing, price);
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
