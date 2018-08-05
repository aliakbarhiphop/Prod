package com.aliakbar.android.prod.staff;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Staff;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    private Context mContext;
    private List<Staff> staffList;
    private StaffAdapterOnClickListener mClickHandler;

    StaffAdapter(Context context, StaffAdapterOnClickListener staffAdapterOnClickListener) {
        mContext = context;
        mClickHandler = staffAdapterOnClickListener;
    }

    public interface StaffAdapterOnClickListener {
        void onClick(String mStaffCurrentRaw,String currentName);
    }

    @Override
    public StaffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.staff_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);

        holder.nameView.setText(staff.getName());

        holder.usernameView.setText(staff.getUsername());

        String client = "" + staff.getClient();
        holder.clientView.setText(client);

        String id = "" + staff.getId();
        holder.idView.setText(id);

        String total = "" + staff.getTotal();
        holder.totalView.setText(total);

        String order = "" + staff.getOrder();
        holder.orderView.setText(order);

        String sales = "" + staff.getSales();
        holder.saleView.setText(sales);
    }

    @Override
    public int getItemCount() {
        if (staffList == null) return 0;
        return staffList.size();
    }

    public List<Staff> swapCursor(List<Staff> c) {
        List<Staff> temp = staffList;
        staffList = c;
        notifyDataSetChanged();
        return temp;
    }

    class StaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView idView, nameView, usernameView, orderView, clientView, totalView, saleView;

        public StaffViewHolder(View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.id_staffview);
            nameView = itemView.findViewById(R.id.name_staffview);
            usernameView = itemView.findViewById(R.id.username_staffview);
            orderView = itemView.findViewById(R.id.order_staffview);
            clientView = itemView.findViewById(R.id.client_staffview);
            totalView = itemView.findViewById(R.id.total_staffview);
            saleView = itemView.findViewById(R.id.sales_staffview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Staff staff = staffList.get(adapterPosition);
            String idCurrentSelected = "" + staff.getId();
            String nameCurrent=staff.getName();
            mClickHandler.onClick(idCurrentSelected,nameCurrent);
        }
    }
}
