package com.zucc.ygg31501102.personmanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.MainActivity;
import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.Schedule;
import com.zucc.ygg31501102.personmanager.modal.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleRemind extends Service {
    private ScheduleRemindBinder binder = new ScheduleRemindBinder();
    private SQLiteDatabase database = MainActivity.databaseHelper.getWritableDatabase();
    private  Timer timer = new Timer();
    private  TimerTask task;
    private LocalBroadcastManager mLBM;
    private Intent mIntent;
    Context context;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
        mLBM = LocalBroadcastManager.getInstance(getApplicationContext());
        upDate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"日程提醒服务开启",Toast.LENGTH_SHORT).show();
        return binder;
    }

    public class ScheduleRemindBinder extends Binder{
//        private SQLiteDatabase database = MainActivity.databaseHelper.getWritableDatabase();
//        Bitmap bitmap;

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 想要执行的事件
                super.handleMessage(msg);
                try {
                    Schedule schedule = SearchLastedSchedule();
                    if (schedule != null){
                        looperStartSchedule(schedule);
                        looperEndSchedule();
                    }
                    Toast.makeText(getApplicationContext(),"定时调用",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        public void looperStartSchedule(Schedule schedule){
            Date now = new Date();
            if (now.getTime()>=schedule.getStartDate().getTime()) {
                notifySchedule(schedule);
                startSchedule(schedule);
            }
        }
        public void looperEndSchedule(){
            Schedule schedule = null;
            String [] columns = {"scheduleid","userid","startdate","enddate","scheduletitle","scheduleremark","days","picaddress","scheduleaddress","state"};
            Cursor cursor = database.query("schedules",columns,"userid=? and state>0",new String[]{User.currentUser.getId()},null,null,null);
            if (cursor.moveToFirst()) {
                do {
                    long now = new Date().getTime();
                    long endTime = cursor.getLong(cursor.getColumnIndex("enddate"));
                    schedule = new Schedule();
                    if (endTime<=now){
                        int state = cursor.getInt(cursor.getColumnIndex("state"));
                        schedule.setState(state);
                        schedule.setScheduleid(cursor.getInt(cursor.getColumnIndex("scheduleid")));
                        long date = cursor.getLong(cursor.getColumnIndex("startdate"));
                        schedule.setStartDate(longToDate(date));
                        schedule.setEndDate(longToDate(endTime));
                        schedule.setTitle(cursor.getString(cursor.getColumnIndex("scheduletitle")));
                        schedule.setRemark(cursor.getString(cursor.getColumnIndex("scheduleremark")));
                        schedule.setAddress(cursor.getString(cursor.getColumnIndex("scheduleaddress")));
                        schedule.setImage(cursor.getString(cursor.getColumnIndex("picaddress")));
                        int days = cursor.getInt(cursor.getColumnIndex("days"));
                        schedule.setDays(days);
                        schedule.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
                        updateScheduleOfState(schedule);
                        if (mIntent == null)
                            mIntent = new Intent("com.zucc.ygg31501102.personmanager.removeschedule");
                        mLBM.sendBroadcast(mIntent);
                    }
                } while (cursor.moveToNext());
            }
        }
        public void Timer() {
            Toast.makeText(getApplicationContext(),"remindBinder被调用",Toast.LENGTH_SHORT).show();
            task = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
            timer.schedule(task, 5000, 60000);
        }

        private Schedule SearchLastedSchedule(){
            Schedule schedule = null;
            String [] columns = {"scheduleid","userid","startdate","enddate","scheduletitle","scheduleremark","days","picaddress","scheduleaddress","state"};
            Cursor cursor = database.query("schedules",columns,"userid=? and state=0",new String[]{User.currentUser.getId()},null,null,"startdate ASC","0,1");
            if (cursor.moveToFirst()) {
                do {
                    schedule = new Schedule();
                    schedule.setScheduleid(cursor.getInt(cursor.getColumnIndex("scheduleid")));
                    long date = cursor.getLong(cursor.getColumnIndex("startdate"));
                    schedule.setStartDate(longToDate(date));
                    date = cursor.getLong(cursor.getColumnIndex("enddate"));
                    schedule.setEndDate(longToDate(date));
                    schedule.setTitle(cursor.getString(cursor.getColumnIndex("scheduletitle")));
                    schedule.setRemark(cursor.getString(cursor.getColumnIndex("scheduleremark")));
                    schedule.setAddress(cursor.getString(cursor.getColumnIndex("scheduleaddress")));
                    schedule.setImage(cursor.getString(cursor.getColumnIndex("picaddress")));
                    int days = cursor.getInt(cursor.getColumnIndex("days"));
                    schedule.setDays(days);
                    schedule.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
                    schedule.setState(cursor.getInt(cursor.getColumnIndex("state")));
                } while (cursor.moveToNext());
            }
            return schedule;
        }
        private void startSchedule(Schedule schedule){
            ContentValues values = new ContentValues();
            values.put("state",1);
            database.update("schedules",values,"scheduleid = ?",new String[]{""+schedule.getScheduleid()});
        }

        private void notifySchedule(Schedule latelySchedule){
            NotificationCompat.Builder nb = new NotificationCompat.Builder(context,"dafault");
//            Uri photoUri = Uri.fromFile(new File("/storage/emulated/0/DCIM/Alipay/1490526003679.jpeg"));
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            nb.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.header));
            nb.setSmallIcon(R.drawable.ic_notifications_black_24dp);
            nb.setContentTitle(latelySchedule.getTitle());
            nb.setContentText(latelySchedule.getRemark());

            nb.setAutoCancel(true);
            nb.setOnlyAlertOnce(true);
            nb.setDefaults(Notification.DEFAULT_VIBRATE);

            //设置事件 PengdingIntent
            Intent flag= new Intent(getApplicationContext(),MainActivity.class);
            //对于没有使用startAcitivty时，没有栈，必须设计一个glag否则无法跳转。
            flag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //给bulider加入intent
            PendingIntent pendingIntent= PendingIntent.getActivity(context,8,flag,PendingIntent.FLAG_CANCEL_CURRENT);
            nb.setContentIntent(pendingIntent);
            NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(33,nb.build());
        }


    }

    private void upDate(){
        Schedule schedule = null;
        String [] columns = {"scheduleid","userid","startdate","enddate","scheduletitle","scheduleremark","days","picaddress","scheduleaddress","state"};
        Cursor cursor = database.query("schedules",columns,"userid=? and state>0",new String[]{User.currentUser.getId()},null,null,"startdate ASC");
        if (cursor.moveToFirst()) {
            do {
                long now = new Date().getTime();
                long endTime = cursor.getLong(cursor.getColumnIndex("enddate"));
                if (endTime<=now) {
                    schedule = new Schedule();
                    int state = cursor.getInt(cursor.getColumnIndex("state"));
                    schedule.setScheduleid(cursor.getInt(cursor.getColumnIndex("scheduleid")));
                    long date = cursor.getLong(cursor.getColumnIndex("startdate"));
                    schedule.setStartDate(longToDate(date));
                    date = cursor.getLong(cursor.getColumnIndex("enddate"));
                    schedule.setEndDate(longToDate(date));
                    schedule.setTitle(cursor.getString(cursor.getColumnIndex("scheduletitle")));
                    schedule.setRemark(cursor.getString(cursor.getColumnIndex("scheduleremark")));
                    schedule.setAddress(cursor.getString(cursor.getColumnIndex("scheduleaddress")));
                    schedule.setImage(cursor.getString(cursor.getColumnIndex("picaddress")));
                    int days = cursor.getInt(cursor.getColumnIndex("days"));
                    schedule.setUserid(cursor.getString(cursor.getColumnIndex("userid")));

                    schedule.setDays(days);
                    updateScheduleOfState(schedule);
                }
            } while (cursor.moveToNext());
        }
    }
    private void updateScheduleOfState(Schedule schedule){
        ContentValues values = new ContentValues();
        if (schedule.getDays()>0) {
            Date startDate = schedule.getStartDate();
            long d1 = startDate.getTime();
            int addDays = schedule.getDays();
            d1 += addDays * 24 * 60 * 60 * 1000;
            values.put("startdate", d1);
            Date endDate = schedule.getStartDate();
            addDays = schedule.getDays();
            long d2 = endDate.getTime();
            d2 += addDays * 24 * 60 * 60 * 1000;
            values.put("enddate", d2);
            values.put("state", 0);
        }else {
            values.put("state",-1);
        }
        database.update("schedules",values,"scheduleid=?",new String[]{""+schedule.getScheduleid()});
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
