package com.zucc.ygg31501102.personmanager.fragments.schedule;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.Schedule;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter.getStringDate;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>{
    private List<Schedule> schedulesList;
    private View view;
    private RecyclerViewAdapter.OnItemClickListener OnItemClickListener = null;
    private Uri photoUri = Uri.fromFile(new File("/storage/emulated/0/DCIM/Alipay/1490526003679.jpeg"));

    public ScheduleAdapter(List<Schedule> schedulesList) {
        this.schedulesList = schedulesList;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ScheduleViewHolder holder = new ScheduleViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.schedule_item, parent,
                false));
//        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expend_type_item, null);

        return holder;
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, final int position)
    {
        holder.schedule_item_title.setText(""+schedulesList.get(position).getTitle());
        String date = getStringDate(schedulesList.get(position).getStartDate());
        holder.schedule_item_start_time.setText(date);
        date = getStringDate(schedulesList.get(position).getEndDate());
        holder.schedule_item_end_time.setText(date);
        try {
            Uri uri = Uri.fromFile(new File(schedulesList.get(position).getImage()));
            holder.schedule_item_img.setImageURI(uri);
        }catch(Exception e){
            holder.schedule_item_img.setImageURI(photoUri);
        }

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
    public int[]  removeData(int position) {
        int id []= {schedulesList.get(position).getScheduleid(),schedulesList.get(position).getDays()};
        schedulesList.remove(position);
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
        return schedulesList.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder
    {

        TextView schedule_item_title;
        TextView schedule_item_start_time;
        TextView schedule_item_end_time;
        ImageView schedule_item_img;
        public ScheduleViewHolder(View view)
        {
            super(view);
            schedule_item_title = (TextView) view.findViewById(R.id.schedule_item_title);
            schedule_item_start_time = (TextView) view.findViewById(R.id.schedule_item_start_time);
            schedule_item_end_time = (TextView) view.findViewById(R.id.schedule_item_end_time);
            schedule_item_img = (ImageView) view.findViewById(R.id.schedule_item_img);
        }
    }
}
