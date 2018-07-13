package com.zucc.ygg31501102.personmanager.fragments.schedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter;
import com.zucc.ygg31501102.personmanager.modal.Expend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>{
    private List<Expend> expendsList;
    private List<String> textList;
    private View view;
    private RecyclerViewAdapter.OnItemClickListener OnItemClickListener = null;


    public ScheduleAdapter(List<String> textList) {
        this.textList = textList;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ScheduleViewHolder holder = new ScheduleViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item, parent,
                false));
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expend_type_item, null);

        return holder;
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, final int position)
    {
//        String date = getStringDate(expendsList.get(position).getExpendcreatedate());
//        holder.money.setText(""+expendsList.get(position).getNumber());
//        holder.item_date.setText(date);
//        holder.item_title.setText(expendsList.get(position).getExpendname());
//        holder.item_img.setImageResource(map.get(expendsList.get(position).getExpendtype()));
//
        if( OnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener.onClick(position);
                }
            });
            holder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }
    public int  removeData(int position) {
        int id = expendsList.get(position).getExpendid();
        expendsList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
        return id;
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener onItemClickListener ){
        this.OnItemClickListener=onItemClickListener;
    }

    @Override
    public int getItemCount()
    {
        return textList.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder
    {

//        TextView money;
//        TextView item_date;
//        TextView item_title;
//        ImageView item_img;
        public ScheduleViewHolder(View view)
        {
            super(view);
//            money = (TextView) view.findViewById(R.id.item_money);
//            item_date = (TextView) view.findViewById(R.id.item_date);
//            item_title = (TextView) view.findViewById(R.id.item_title);
//            item_img = (ImageView) view.findViewById(R.id.item_img);
        }
    }


}
