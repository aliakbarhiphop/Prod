package com.aliakbar.android.prod.customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private Context mContext;
    private List<Customer> customerList;

    private CustomerAdapterOnClickListener mClickHandler;

    public CustomerAdapter(Context context, CustomerAdapterOnClickListener customerAdapterOnClickListener) {
        mContext = context;
        mClickHandler = customerAdapterOnClickListener;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.customer_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        String id = "" + customer.getId();
        holder.idView.setText(id);

        String name = customer.getName();
        holder.nameView.setText(name);

        String manager = customer.getManagerName();
        holder.managerView.setText(manager);

        String deals = "" + customer.getDeals();
        holder.dealsView.setText(deals);

        String total = "" + customer.getTotal();
        holder.totalView.setText(total);

        String paid = "" + customer.getPaid();
        holder.paidView.setText(paid);

        String credit = "" + customer.getCredit();
        holder.creditView.setText(credit);

        String order = "" + customer.getOrders();
        holder.orderView.setText(order);
    }

    @Override
    public int getItemCount() {
        if (customerList == null) return 0;
        return customerList.size();
    }

    public List<Customer> swapAdapter(List<Customer> c) {
        List<Customer> temp = customerList;
        customerList = c;
        notifyDataSetChanged();
        return temp;
    }

    public interface CustomerAdapterOnClickListener {
        void onClick(String mCustomerCurrentRaw, String mCustomerSelectedName);
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView, idView, dealsView, managerView, totalView, paidView, creditView, orderView;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name_customer_view);
            idView = itemView.findViewById(R.id.id_customer_view);
            dealsView = itemView.findViewById(R.id.deals_customer_view);
            managerView = itemView.findViewById(R.id.manager_customer_view);
            totalView = itemView.findViewById(R.id.total_customer_view);
            paidView = itemView.findViewById(R.id.paid_customer_view);
            creditView = itemView.findViewById(R.id.credit_customer_view);
            orderView = itemView.findViewById(R.id.order_customer_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Customer customer = customerList.get(adapterPosition);
            String idCurrentSelected = "" + customer.getId();
            mClickHandler.onClick(idCurrentSelected, customer.getName());
        }
    }
}
