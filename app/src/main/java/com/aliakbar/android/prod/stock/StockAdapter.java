package com.aliakbar.android.prod.stock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliakbar.android.prod.R;
import com.aliakbar.android.prod.data.Stock;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private Context mContext;
    private List<Stock> stockList;

    private StockAdapterOnClickListener mClickHandler;


    public interface StockAdapterOnClickListener {
        void onClick(String mSStockId, String mSStockName, String mSStockPrice);
    }

    public StockAdapter(Context context, StockAdapterOnClickListener stockAdapterOnClickListener) {
        mContext = context;
        mClickHandler = stockAdapterOnClickListener;
    }


    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.stock_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new StockViewHolder(view);
    }


    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        String idTemp = "" + stock.getId();
        holder.idStockView.setText(idTemp);

        String priceTemp = "" + stock.getPrice();
        holder.priceStockView.setText(priceTemp);

        holder.nameStockView.setText(stock.getItemname());

        String orderTemp = "" + stock.getOrder();
        holder.orderStockView.setText(orderTemp);

        String saleTemp = "" + stock.getSales();
        holder.salesStockView.setText(saleTemp);
    }


    @Override
    public int getItemCount() {
        if (stockList == null) return 0;
        return stockList.size();
    }

    public List<Stock> swapCursor(List<Stock> c) {
        List<Stock> temp = stockList;
        stockList = c;
        notifyDataSetChanged();
        return temp;
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView idStockView, priceStockView, nameStockView, orderStockView, salesStockView;

        public StockViewHolder(View itemView) {
            super(itemView);
            idStockView = (TextView) itemView.findViewById(R.id.id_stockview);
            priceStockView = (TextView) itemView.findViewById(R.id.price_stockview);
            nameStockView = (TextView) itemView.findViewById(R.id.name_stockview);
            orderStockView = (TextView) itemView.findViewById(R.id.order_stockview);
            salesStockView = (TextView) itemView.findViewById(R.id.sales_stockview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Stock stock = stockList.get(adapterPosition);
            String idCurrentSelected = "" + stock.getId();
            String nameCurrentSelected = "" + stock.getItemname();
            String priceCurrentSelected = "" + stock.getPrice();
            mClickHandler.onClick(idCurrentSelected, nameCurrentSelected, priceCurrentSelected);
        }
    }
}
