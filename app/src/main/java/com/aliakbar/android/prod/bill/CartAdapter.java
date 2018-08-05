package com.aliakbar.android.prod.bill;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    List<Cart> carts = new ArrayList<>();
    Context context;
    CartAdapterOnClickListener cartAdapterOnClickListener;

    public interface CartAdapterOnClickListener {
        void onClick(String mCurrentItemId);
    }

    public CartAdapter(Context mContext, CartAdapterOnClickListener mCartAdapterOnClickListener) {
        context = mContext;
        cartAdapterOnClickListener = mCartAdapterOnClickListener;
    }


    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.cart_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E2DDDD"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        Cart cart = carts.get(position);

        String itemName = cart.getItemName();
        String itemId = "" + cart.getItemId();
        String printItem = itemId + " / " + itemName;
        holder.nameText.setText(printItem);

        String price = "" + cart.getPrice();
        holder.priceText.setText(price);

        String quantity = "" + cart.getQuantity();
        holder.quantityText.setText(quantity);

        String total = "" + cart.getTotal();
        holder.totalText.setText(total);

        String discountPer = "" + cart.getDiscountPercentage();
        holder.discText.setText(discountPer);

        String pay = "" + cart.getPay();
        holder.payText.setText(pay);

        int rawId = cart.getItemId();
        holder.itemView.setTag(rawId);
    }

    @Override
    public int getItemCount() {
        if (carts.isEmpty()) return 0;
        return carts.size();
    }


    public List<Cart> swapCursor(List<Cart> c) {
        List<Cart> temp = carts;
        carts = c;
        notifyDataSetChanged();
        return temp;
    }


    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, priceText, quantityText, totalText, discText, payText;

        public CartViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.cart_view_name);
            priceText = itemView.findViewById(R.id.cart_view_price);
            quantityText = itemView.findViewById(R.id.cart_view_quantity);
            totalText = itemView.findViewById(R.id.cart_view_total);
            discText = itemView.findViewById(R.id.cart_view_disc_per);
            payText = itemView.findViewById(R.id.cart_view_pay);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Cart cart = carts.get(getAdapterPosition());
            String id = "" + cart.get_id();
            cartAdapterOnClickListener.onClick(id);
        }
    }
}
