package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrderFormActivity extends AppCompatActivity {
    private EditText etId, etTable, etTotal;
    private RadioGroup rgDining;
    private LinearLayout containerEntree, containerMain, containerDrink;
    private DatabaseManager db;
    private int orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        etId = findViewById(R.id.etId);
        etTable = findViewById(R.id.etTable);
        etTotal = findViewById(R.id.etTotal);
        rgDining = findViewById(R.id.rgDining);

        containerEntree = findViewById(R.id.containerEntree);
        containerMain = findViewById(R.id.containerMain);
        containerDrink = findViewById(R.id.containerDrink);

        db = new DatabaseManager(this);

        // 1) Build grouped dish checkboxes
        loadDishCheckboxes();

        // 2) If editing, restore values and pre-check boxes
        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", -1);
            loadOrder(orderId);
            findViewById(R.id.btnDelete).setVisibility(android.view.View.VISIBLE);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveOrder());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteOrder());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /** Creates a checkbox per dish, grouped by type, and wires it to recalc the total on change */
    private void loadDishCheckboxes() {
        Cursor c = db.getAllDishes();
        if (c.moveToFirst()) {
            do {
                String name = c.getString(1);
                String type = c.getString(2);
                float price = c.getFloat(4);

                CheckBox cb = new CheckBox(this);
                cb.setText(name + " ($" + price + ")");
                cb.setTag(price); // store price for easy math
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> recalculateTotal());

                if ("entree".equalsIgnoreCase(type)) {
                    containerEntree.addView(cb);
                } else if ("main".equalsIgnoreCase(type)) {
                    containerMain.addView(cb);
                } else if ("drink".equalsIgnoreCase(type)) {
                    containerDrink.addView(cb);
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    /** Extract clean dish name from checkbox text like: "Burger ($12.5)" -> "Burger" */
    private String nameFromCheckbox(CheckBox cb) {
        String txt = cb.getText().toString();
        int cut = txt.indexOf(" ($");
        return (cut > 0) ? txt.substring(0, cut) : txt;
    }

    /** Sum up prices of all checked boxes and refresh etTotal */
    private void recalculateTotal() {
        float total = 0f;
        total += sumChecked(containerEntree);
        total += sumChecked(containerMain);
        total += sumChecked(containerDrink);
        etTotal.setText(String.valueOf(total));
    }

    private float sumChecked(LinearLayout container) {
        float subtotal = 0f;
        for (int i = 0; i < container.getChildCount(); i++) {
            CheckBox cb = (CheckBox) container.getChildAt(i);
            if (cb.isChecked()) {
                Float price = (Float) cb.getTag();
                subtotal += (price == null ? 0f : price);
            }
        }
        return subtotal;
    }

    /** Collect selected dish names into a CSV for saving */
    private String collectSelectedDishesCsv() {
        ArrayList<String> names = new ArrayList<>();
        collectNamesFrom(containerEntree, names);
        collectNamesFrom(containerMain, names);
        collectNamesFrom(containerDrink, names);
        return String.join(", ", names);
    }

    private void collectNamesFrom(LinearLayout container, ArrayList<String> out) {
        for (int i = 0; i < container.getChildCount(); i++) {
            CheckBox cb = (CheckBox) container.getChildAt(i);
            if (cb.isChecked()) out.add(nameFromCheckbox(cb));
        }
    }

    private String getSelectedDiningOption() {
        int checkedId = rgDining.getCheckedRadioButtonId();
        if (checkedId == R.id.rbDineIn) return "Dine-in";
        if (checkedId == R.id.rbTakeAway) return "Take-away";
        return "";
    }

    /** Load an existing order, pre-check the saved dishes, then recalc once */
    private void loadOrder(int id) {
        Cursor c = db.getAllOrders();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == id) {
                    etId.setText(String.valueOf(c.getInt(0)));

                    String dining = c.getString(1);
                    if ("Dine-in".equalsIgnoreCase(dining)) rgDining.check(R.id.rbDineIn);
                    else if ("Take-away".equalsIgnoreCase(dining)) rgDining.check(R.id.rbTakeAway);

                    etTable.setText(c.getString(2));

                    // Pre-check saved dishes
                    String[] saved = c.getString(3).split(",\\s*");
                    Set<String> savedSet = new HashSet<>(Arrays.asList(saved));
                    precheckBoxes(savedSet);

                    // Recalculate from the current checked state (ignore stored total to avoid drift)
                    recalculateTotal();
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    private void precheckBoxes(Set<String> names) {
        precheckInContainer(containerEntree, names);
        precheckInContainer(containerMain, names);
        precheckInContainer(containerDrink, names);
    }

    private void precheckInContainer(LinearLayout container, Set<String> names) {
        for (int i = 0; i < container.getChildCount(); i++) {
            CheckBox cb = (CheckBox) container.getChildAt(i);
            cb.setChecked(names.contains(nameFromCheckbox(cb)));
        }
    }

    private void saveOrder() {
        String idStr = etId.getText().toString().trim();
        String dining = getSelectedDiningOption();
        String table = etTable.getText().toString().trim();

        // Always recompute total & dishes at save time (source of truth = checkboxes)
        recalculateTotal();
        String totalStr = etTotal.getText().toString().trim();
        String dishesCsv = collectSelectedDishesCsv();

        if (idStr.isEmpty() || dining.isEmpty() || dishesCsv.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "ID, dining option, at least 1 dish, and total are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        float total = Float.parseFloat(totalStr);

        if (orderId == -1) { // new
            boolean ok = db.addOrder(id, dining, table, dishesCsv, total);
            if (!ok) {
                Toast.makeText(this, "Error: Order ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // update existing
            db.updateOrder(id, dining, table, dishesCsv, total);
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