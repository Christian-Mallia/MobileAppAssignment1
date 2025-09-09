package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrderFormActivity extends AppCompatActivity {
    private EditText etId, etTable, etTotal;
    private TextView tvProcessingTime;
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

        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDone = findViewById(R.id.btnDone);

        tvProcessingTime = findViewById(R.id.tvProcessingTime);

        db = new DatabaseManager(this);

        loadDishCheckboxes();

        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", -1);
            loadOrder(orderId);
            btnDelete.setVisibility(Button.VISIBLE);
            btnDone.setVisibility(Button.VISIBLE); // show Mark Done button
        }

        // Button handlers
        btnSave.setOnClickListener(v -> saveOrder());
        btnDelete.setOnClickListener(v -> deleteOrder());
        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> markOrderDone());
    }

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

    private String nameFromCheckbox(CheckBox cb) {
        String txt = cb.getText().toString();
        int cut = txt.indexOf(" ($");
        return (cut > 0) ? txt.substring(0, cut) : txt;
    }

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

    private void loadOrder(int id) {
        Cursor c = db.getAllOrders();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndexOrThrow("id")) == id) {
                    // ID
                    etId.setText(String.valueOf(c.getInt(c.getColumnIndexOrThrow("id"))));

                    // Dining option
                    String dining = c.getString(c.getColumnIndexOrThrow("diningOption"));
                    if ("Dine-in".equalsIgnoreCase(dining)) {
                        rgDining.check(R.id.rbDineIn);
                    } else if ("Take-away".equalsIgnoreCase(dining)) {
                        rgDining.check(R.id.rbTakeAway);
                    }

                    // Table number
                    etTable.setText(c.getString(c.getColumnIndexOrThrow("tableNumber")));

                    // Dishes
                    String[] saved = c.getString(c.getColumnIndexOrThrow("dishNames")).split(",\\s*");
                    Set<String> savedSet = new HashSet<>(Arrays.asList(saved));
                    precheckBoxes(savedSet);

                    // Recalculate from checkboxes instead of trusting stored total
                    recalculateTotal();

                    // Status
                    String status = c.getString(c.getColumnIndexOrThrow("status"));
                    Toast.makeText(this, "Order Status: " + status, Toast.LENGTH_SHORT).show();

                    // Order Time + Processing Time
                    int colTime = c.getColumnIndex("orderTime");
                    if (colTime != -1) {  // make sure the column exists
                        long orderTime = c.getLong(colTime);
                        long now = System.currentTimeMillis();
                        String processingTime = formatDuration(now - orderTime);
                        tvProcessingTime.setText("Processing Time: " + processingTime);
                    } else {
                        tvProcessingTime.setText("Processing Time: N/A");
                    }

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

    private void markOrderDone() {
        if (orderId != -1) {
            db.markOrderDone(orderId);
            Toast.makeText(this, "Order marked as Done", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;

        if (hours > 0) {
            return String.format("%d hr %d min %d sec", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d sec", seconds);
        }
    }
}