package com.aliakbar.android.prod.sales;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;

import java.util.ArrayList;
import java.util.List;

public class SalesInsideAdapter extends RecyclerView.Adapter<SalesInsideAdapter.SalesInsideViewHolder> {
    Context context;
    List<SalesItem> salesItems = new ArrayList<>();

    @Override
    public SalesInsideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForItem = R.layout.sales_inside_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);
        return new SalesInsideAdapter.SalesInsideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesInsideViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E2DDDD"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        SalesItem salesItem = salesItems.get(position);

        String itemListString = "" + salesItem.getId() + "/" + salesItem.getItemName() + "/" + salesItem.getPrice();
        holder.itemsView.setText(itemListString);
        holder.quantityView.setText("" + salesItem.getQty());
        holder.totalView.setText("" + salesItem.getTotal());
        holder.discView.setText("" + salesItem.getDisc());
        holder.payView.setText("" + salesItem.getPay());
    }

    @Override
    public int getItemCount() {
        if (salesItems.isEmpty()) return 0;
        return salesItems.size();
    }

    public SalesInsideAdapter(Context mContext,List<SalesItem> salesItemsList) {
        context = mContext;
        salesItems=salesItemsList;
    }

    class SalesInsideViewHolder extends RecyclerView.ViewHolder {
        TextView itemsView, quantityView, totalView, discView, payView;

        public SalesInsideViewHolder(View itemView) {
            super(itemView);
            itemsView = itemView.findViewById(R.id.salesInsideItem);
            quantityView = itemView.findViewById(R.id.salesInsideQuantity);
            totalView = itemView.findViewById(R.id.salesInsideTotal);
            discView = itemView.findViewById(R.id.salesInsideDisc);
            payView = itemView.findViewById(R.id.salesInsidePay);
        }
    }
}
