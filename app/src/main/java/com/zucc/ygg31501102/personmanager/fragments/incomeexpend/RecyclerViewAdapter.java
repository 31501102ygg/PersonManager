package com.zucc.ygg31501102.personmanager.fragments.incomeexpend;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.Expend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
    {
        private List<Expend> expendsList;
        private View view;
        private OnItemClickListener OnItemClickListener = null;
        private final Map<String,Integer> map = new HashMap<String,Integer>();
        private final String [] types = {"餐饮食品", "衣服饰品", "居家生活", "行车交通",
                "休闲娱乐", "文化教育", "健康医疗", "投资支出", "其他支出",
                "工作收入", "投资收入", "其他收入"};
        private final int[] Icons = {
                R.drawable.rice, R.drawable.icons8_clothes_48, R.drawable.bed_empty,
                R.drawable.bus, R.drawable.microphone_variant, R.drawable.lead_pencil,
                R.drawable.hospital, R.drawable.chart_line, R.drawable.lock_question,
                R.drawable.worker, R.drawable.chart_areaspline, R.drawable.lock_question,
        };

        public RecyclerViewAdapter(List<Expend> expendsList) {
            this.expendsList = expendsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.item, parent,
                    false));
            initTypesMap();

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            String date = getStringDate(longToDate(expendsList.get(position).getExpendcreatedate()));
            holder.money.setText(""+expendsList.get(position).getNumber());
            holder.item_date.setText(date);
            holder.item_title.setText(expendsList.get(position).getExpendname());
            holder.item_img.setImageResource(map.get(expendsList.get(position).getExpendtype()));

            if( OnItemClickListener!= null){
                holder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnItemClickListener.onClick(position);
                    }
                });
                holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnItemClickListener.onLongClick(position);
                        return false;
                    }
                });
            }
        }
        public String getRemark(int position) {
            String expendremark = expendsList.get(position).getExpendremark();
            return expendremark;
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
        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this.OnItemClickListener=onItemClickListener;
        }

        @Override
        public int getItemCount()
        {
            return expendsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView money;
            TextView item_date;
            TextView item_title;
            ImageView item_img;
            public MyViewHolder(View view)
            {
                super(view);
                money = (TextView) view.findViewById(R.id.item_money);
                item_date = (TextView) view.findViewById(R.id.item_date);
                item_title = (TextView) view.findViewById(R.id.item_title);
                item_img = (ImageView) view.findViewById(R.id.item_img);
            }
        }

        public static String getStringDate(Date currentTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateString = formatter.format(currentTime);
            return dateString;
        }

        private void initTypesMap(){
            for (int i=0;i<types.length;i++){
                map.put(types[i],Icons[i]);
            }
        }

        public Date longToDate(long intDate){
            Date date;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowTimeStr = sdf.format(intDate);
                date = sdf.parse(nowTimeStr);
            }catch (Exception e){
                return null;
            }
            return date;
        }
    }
