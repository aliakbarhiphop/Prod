package com.aliakbar.android.prod.sales;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Sales;

import java.util.ArrayList;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {
    List<Sales> sales = new ArrayList<>();
    SalesAdapterOnClickListener salesAdapterOnClickListener;
    Context context;

    @Override
    public SalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForItem = R.layout.sales_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);

        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        Sales mySales = sales.get(position);

        holder.dateView.setText(mySales.getDate());
        holder.nameView.setText(mySales.getName());
        holder.staffView.setText(mySales.getStaff());
        holder.billView.setText("" + mySales.getBillId());
        holder.totalView.setText("" + mySales.getTotal());
        holder.discView.setText("" + mySales.getDisc());
        holder.payView.setText("" + mySales.getPay());

        SalesInsideAdapter adapter = new SalesInsideAdapter(context, mySales.getSalesItems());
        holder.salesItemRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.salesItemRecycler.setLayoutManager(layoutManager);
        holder.salesItemRecycler.setHasFixedSize(true);
    }

    @Override
    public int getItemCount() {
        if (sales.isEmpty()) return 0;
        return sales.size();
    }

    public interface SalesAdapterOnClickListener {
        void onClick(String mCurrentItemId);
    }

    public SalesAdapter(Context mContext, SalesAdapterOnClickListener adapter) {
        context = mContext;
        salesAdapterOnClickListener = adapter;
    }


    public List<Sales> swapCursor(List<Sales> c) {
        List<Sales> temp = sales;
        sales = c;
        notifyDataSetChanged();
        return temp;
    }

    class SalesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateView, billView, nameView, staffView, totalView, discView, payView;
        RecyclerView salesItemRecycler;

        public SalesViewHolder(View itemView) {
            super(itemView);

            dateView = itemView.findViewById(R.id.SIdate);
            billView = itemView.findViewById(R.id.SIbillId);
            nameView = itemView.findViewById(R.id.SIname);
            staffView = itemView.findViewById(R.id.SIstaff);
            totalView = itemView.findViewById(R.id.SItotal);
            discView = itemView.findViewById(R.id.SIdisc);
            payView = itemView.findViewById(R.id.SIpay);

            salesItemRecycler = itemView.findViewById(R.id.SIrecycler);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //TODO BILL ID ON SELECT
        }
    }

}
