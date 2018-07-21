package com.zucc.ygg31501102.personmanager;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zucc.ygg31501102.personmanager.database.PersonManagerDatabaseHelper;
import com.zucc.ygg31501102.personmanager.modal.Schedule;
import com.zucc.ygg31501102.personmanager.modal.User;
import com.zucc.ygg31501102.personmanager.plug_in.CustomDatePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.MediaRecorder.VideoSource.CAMERA;

public class AddScheduleActivity extends AppCompatActivity {
    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE = 100;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener ;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private PersonManagerDatabaseHelper databaseHelper;
    private CustomDatePicker customDatePicker1,customDatePicker2;
    private TextView addressView;
    private ImageView back;
    private TextView scheduleTitle;
    private TextView startTime;
    private TextView endTime;
    private Spinner scheduleSpinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private EditText scheduleRemark;
    private ImageView photo;
    private Button addScheduleButton;
    private Uri photoUri = Uri.fromFile(new File("/storage/emulated/0/DCIM/Alipay/1490526003679.jpeg"));
    private String path;
    private Schedule addschedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addschedule);
        //获取定位、拍照权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请ACCESS_COARSE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }
        locationPosition();

        databaseHelper = new PersonManagerDatabaseHelper(this,"PersonManager.db",null,1);
        databaseHelper.getWritableDatabase();

        addressView = (TextView)findViewById(R.id.add_schedule_address);
        back = (ImageView) findViewById(R.id.add_schedule_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        startTime = (TextView)findViewById(R.id.add_schedule_start_time);
        endTime = (TextView)findViewById(R.id.add_schedule_end_time);
        initDatePickerStart();
        initDatePickerEnd();
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker1.show(startTime.getText().toString());
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker2.show(endTime.getText().toString());
            }
        });
        photo = (ImageView)findViewById(R.id.add_schedule_photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = Environment.getExternalStorageState();
                path = Environment.getExternalStorageDirectory() +
                        File.separator + Environment.DIRECTORY_DCIM + File.separator;
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String fileName = getPhotoFileName() + ".jpeg";
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoUri = Uri.fromFile(new File(path + fileName));
                    path+=fileName;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        scheduleTitle=(TextView)findViewById(R.id.add_schedule_title);
        scheduleRemark = (EditText)findViewById(R.id.add_schedule_remark);
        addScheduleButton = (Button)findViewById(R.id.schedule_add);
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    addSchedule();
                    finish();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"日程添加失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

//        photo.setImageURI(photoUri);

        scheduleSpinner = (Spinner)findViewById(R.id.add_schedule_spinner);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("0");
        data_list.add("1");
        data_list.add("2");
        data_list.add("3");
        data_list.add("4");
        data_list.add("5");
        data_list.add("6");
        data_list.add("7");
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        scheduleSpinner.setAdapter(arr_adapter);
        //默认选中0天
        scheduleSpinner.setSelection(0,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE){
//            Toast.makeText(this,"开始回调",Toast.LENGTH_SHORT).show();
            Uri uri = null;
            if(data != null && data.getData() != null){
                uri = data.getData();
            }
            if (uri==null){
                if (photoUri != null)
                    uri = photoUri;
            }
            compressBitmap(path,new File(path));
            photo.setImageURI(uri);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case WRITE_COARSE_LOCATION_REQUEST_CODE:
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                locationPosition();
            } else{
                // 没有获取到权限，做特殊处理
                Toast.makeText(this,"没有定位权限",Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }
    public void locationPosition(){
        //声明定位回调监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        String country = aMapLocation.getCountry();//国家信息
                        String province = aMapLocation.getProvince();//省信息
                        String city = aMapLocation.getCity();//城市信息
                        String district = aMapLocation.getDistrict();
                        String street = aMapLocation.getStreet();//街道
                        String poi = aMapLocation.getPoiName();
                        addressView.setText(poi);
                        Toast.makeText(AddScheduleActivity.this, country+province+city+district+street, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"请开启定位服务",Toast.LENGTH_SHORT).show();
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
//        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
//        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
    /*设置时间日期*/
    private void initDatePickerStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startTime.setText(now);
        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startTime.setText(time);
            }
        }, now,"2019-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 显示时和分
        customDatePicker1.setIsLoop(true); // 允许循环滚动
    }

    private void initDatePickerEnd() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        endTime.setText(now);
        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endTime.setText(time);
            }
        }, now,"2019-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

    public void addSchedule(){
        String title = scheduleTitle.getText().toString();
        String startDate = startTime.getText().toString();
        String endDate = endTime.getText().toString();
        String remark = scheduleRemark.getText().toString();
        String address = addressView.getText().toString();
        String picAddress ="";
        int state = 0;
        int days = Integer.valueOf(scheduleSpinner.getSelectedItem().toString());
        long longStartDate = dateToLong(StringToDate(startDate));
        long longEndDate = dateToLong(StringToDate(endDate));


        if (title==null){
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if (longEndDate < longStartDate){
            Toast.makeText(this,"开始时间不能晚于结束时间",Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this,""+User.currentUser.getUserid(),Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("scheduletitle",title);
        values.put("userid", User.currentUser.getUserid());
        values.put("startdate",longStartDate);
        values.put("enddate",longEndDate);
        values.put("scheduleaddress",address);
        values.put("picaddress",path);
        values.put("scheduleremark",remark);
        values.put("days",days);
        values.put("state",state);
        db.insert("schedules",null,values);
        Toast.makeText(getApplicationContext(),"日程添加成功",Toast.LENGTH_SHORT).show();
    }

    /*date和long互换*/
    public long dateToLong(Date date){
        long intDate = date.getTime();
        return intDate;
    }

    public Date StringToDate(String strDate){
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date = sdf.parse(strDate);
        }catch(Exception e){
            Toast.makeText(this,"时间格式不正确",Toast.LENGTH_SHORT).show();
            return null;
        }
        return date;
    }

    public Date longToDate(long intDate){
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTimeStr = sdf.format(intDate);
            date = sdf.parse(nowTimeStr);
            Toast.makeText(this,"时间（转化）:"+date.toString(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"时间转化出错",Toast.LENGTH_SHORT).show();
            return null;
        }
        return date;
    }
    /*图片压缩*/
    public static void compressBitmap(String filePath, File file){
        // 数值越高，图片像素越低
        int inSampleSize = 5;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 ,baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
