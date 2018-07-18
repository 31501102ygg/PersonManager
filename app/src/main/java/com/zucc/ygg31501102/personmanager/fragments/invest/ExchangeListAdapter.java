package com.zucc.ygg31501102.personmanager.fragments.invest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter;
import com.zucc.ygg31501102.personmanager.modal.Exchange;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExchangeListAdapter extends RecyclerView.Adapter<ExchangeListAdapter.MyViewHolder> {
    private List<Exchange> exchanges = new ArrayList<Exchange>();
    private ExchangeListAdapter.OnItemClickListener OnItemClickListener = null;
    DecimalFormat decimalFormat =new DecimalFormat("0.00");

    public ExchangeListAdapter(List<Exchange> exchangeList){
        this.exchanges = exchangeList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ExchangeListAdapter.MyViewHolder holder = new ExchangeListAdapter.MyViewHolder(LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.invest_exchange_list_item, viewGroup,
                false));
        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        myViewHolder.invest.setText(decimalFormat.format(exchanges.get(position).getBankConversionPri()));
        myViewHolder.money.setText(exchanges.get(position).getName());
        if( OnItemClickListener!= null){
            myViewHolder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

//    public int  removeData(int position) {
//        int id = expendsList.get(position).getExpendid();
//        expendsList.remove(position);
//        //删除动画
//        notifyItemRemoved(position);
//        notifyDataSetChanged();
//        return id;
//    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView money;
        TextView invest;
        public MyViewHolder(View view)
        {
            super(view);
            money = (TextView) view.findViewById(R.id.invest_item_title);
            invest = (TextView) view.findViewById(R.id.invest_item_money);
        }
    }
    public interface OnItemClickListener{
        void onClick( int position);
    }
    public void setOnItemClickListener(ExchangeListAdapter.OnItemClickListener onItemClickListener ){
        this.OnItemClickListener=onItemClickListener;
    }
}
