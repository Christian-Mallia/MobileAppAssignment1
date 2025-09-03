package com.example.assignment1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DishListAdapter extends RecyclerView.Adapter<DishListAdapter.VH> {

    public interface OnDishClick {
        void onClick(int dishId);
    }

    private List<Dish> items;
    private OnDishClick listener;

    public DishListAdapter(List<Dish> items, OnDishClick listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvTypePrice;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTypePrice = itemView.findViewById(R.id.tvTypePrice);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dish, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Dish d = items.get(position);
        holder.tvName.setText(d.getName());
        holder.tvTypePrice.setText(d.getType() + " • $" + d.getPrice());
        holder.itemView.setOnClickListener(v -> listener.onClick(d.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
