package com.aliakbar.android.prod.customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;

import java.util.List;

public class CustomerStaffFilterAdapter extends RecyclerView.Adapter<CustomerStaffFilterAdapter.CustomerStaffFilterViewHolder> {
    private Context mContext;
    private List<String> staffFilterList;

    CustomerStaffFilterAdapter(Context context, List<String> staffList) {
        mContext = context;
        staffFilterList = staffList;
    }

    @Override
    public CustomerStaffFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.customer_filter_manager_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent,false);
        return new CustomerStaffFilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerStaffFilterViewHolder holder, int position) {
        holder.nameView.setText(staffFilterList.get(position));
    }

    @Override
    public int getItemCount() {
        if (staffFilterList == null) return 0;
        return staffFilterList.size();
    }

    public List<String> swapCursor(List<String> c) {
        List<String> temp = staffFilterList;
        staffFilterList = c;
        notifyDataSetChanged();
        return temp;
    }

    class CustomerStaffFilterViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;

        public CustomerStaffFilterViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.customer_filter_view_name);
        }
    }
}
