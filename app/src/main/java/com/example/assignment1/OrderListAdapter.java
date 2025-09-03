package com.example.assignment1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        TextView tvSummary;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvSummary = itemView.findViewById(R.id.tvSummary);
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
        String summary = o.getDiningOption() + " | Table: " + o.getTableNumber()
                + " | $" + o.getTotalPrice();
        holder.tvSummary.setText(summary);
        holder.itemView.setOnClickListener(v -> listener.onClick(o.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
