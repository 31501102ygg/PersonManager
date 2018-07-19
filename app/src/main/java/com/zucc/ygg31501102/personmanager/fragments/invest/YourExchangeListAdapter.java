package com.zucc.ygg31501102.personmanager.fragments.invest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.Exchange;
import com.zucc.ygg31501102.personmanager.modal.YourExchangeAccount;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class YourExchangeListAdapter extends RecyclerView.Adapter<YourExchangeListAdapter.MyViewHolder>  {
    private List<YourExchangeAccount> yourExchangeAccounts = new ArrayList<YourExchangeAccount>();
    private YourExchangeListAdapter.OnItemClickListener OnItemClickListener = null;
    DecimalFormat decimalFormat =new DecimalFormat("0.00");

    public YourExchangeListAdapter(List<YourExchangeAccount> exchangeList){
        this.yourExchangeAccounts = exchangeList;
    }
    @NonNull
    @Override
    public YourExchangeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        YourExchangeListAdapter.MyViewHolder holder = new YourExchangeListAdapter.MyViewHolder(LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.invest_exchange_list_item, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull YourExchangeListAdapter.MyViewHolder myViewHolder, final int position) {
        myViewHolder.invest.setText(decimalFormat.format(yourExchangeAccounts.get(position).getNumber()));
        myViewHolder.money.setText(yourExchangeAccounts.get(position).getMoneyTypeName());
        myViewHolder.flagimage.setImageResource(InvestFragment.FlagsMap.get(yourExchangeAccounts.get(position).getMoneyTypeName()));
        if( OnItemClickListener!= null){
            myViewHolder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener.onClick(position);
                }
            });
//            myViewHolder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    OnItemClickListener.onLongClick(position);
//                    return false;
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return yourExchangeAccounts.size();
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
        ImageView flagimage;
        public MyViewHolder(View view)
        {
            super(view);
            money = (TextView) view.findViewById(R.id.invest_item_title);
            invest = (TextView) view.findViewById(R.id.invest_item_money);
            flagimage = (ImageView) view.findViewById(R.id.invest_item_img);
        }
    }
    public interface OnItemClickListener{
        void onClick( int position);
//        void onLongClick( int position);
    }
    public void setOnItemClickListener(YourExchangeListAdapter.OnItemClickListener onItemClickListener ){
        this.OnItemClickListener=onItemClickListener;
    }
}
