package com.aliakbar.android.prod.bill;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartAdjustAdapter extends RecyclerView.Adapter<CartAdjustAdapter.CartAdjustViewHolder> {
    List<Cart> carts = new ArrayList<>();
    Context context;
    CartAdjustAdapterOnClickListener mListener;

    public interface CartAdjustAdapterOnClickListener {
        void onClick(String mCurrentItemId, int mCurrentTotal, int mCurrentPay);
    }

    public CartAdjustAdapter(Context mContext, CartAdjustAdapterOnClickListener cartAdjustAdapterOnClickListener) {
        context = mContext;
        mListener = cartAdjustAdapterOnClickListener;
    }

    @Override
    public CartAdjustViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.cart_adjust_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CartAdjustViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdjustViewHolder holder, int position) {
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

        String pay = "" + cart.getPay();
        holder.payText.setText(pay);


        holder.itemView.setTag(cart.getItemId());

        holder.itemView.setTag(1, cart.getTotal());

        holder.itemView.setTag(2,  cart.getPay());
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

    class CartAdjustViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, priceText, quantityText, totalText, payText;

        public CartAdjustViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.cart_adjust_view_item);
            priceText = itemView.findViewById(R.id.cart_adjust_view_price);
            quantityText = itemView.findViewById(R.id.cart_adjust_view_quantity);
            totalText = itemView.findViewById(R.id.cart_adjust_view_total);
            payText = itemView.findViewById(R.id.cart_adjust_view_pay);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Cart cart = carts.get(getAdapterPosition());
            String id = "" + cart.get_id();
            int total = cart.getTotal();
            int pay = cart.getPay();

            mListener.onClick(id, total,  pay);
        }
    }
}
