package com.example.assignment1;

import static androidx.core.util.TimeUtils.formatDuration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.text.format.DateUtils;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.VH> {

    public interface OnOrderClick {
        void onClick(int orderId);
    }

    private List<Order> items;
    private OnOrderClick listener;

    public OrderListAdapter(List<Order> items, OnOrderClick listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDining, tvTable, tvDishes, tvTotal, tvStatus, tvOrderTime;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDining = itemView.findViewById(R.id.tvDining);
            tvTable = itemView.findViewById(R.id.tvTable);
            tvDishes = itemView.findViewById(R.id.tvDishes);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = items.get(position);

        holder.tvOrderId.setText("Order ID: " + o.getId());
        holder.tvDining.setText("Dining: " + o.getDiningOption());
        holder.tvTable.setText("Table: " + (o.getTableNumber().isEmpty() ? "-" : o.getTableNumber()));
        holder.tvDishes.setText("Dishes: " + o.getDishNames());
        holder.tvTotal.setText("Total: $" + o.getTotalPrice());
        holder.tvStatus.setText("Status: " + o.getStatus());

        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(o.getOrderTime(), now, DateUtils.MINUTE_IN_MILLIS);
        holder.tvOrderTime.setText("Processing Time: " + relativeTime);

        holder.itemView.setOnClickListener(v -> listener.onClick(o.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
