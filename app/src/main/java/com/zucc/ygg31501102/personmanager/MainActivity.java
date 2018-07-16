package com.zucc.ygg31501102.personmanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.database.PersonManagerDatabaseHelper;
import com.zucc.ygg31501102.personmanager.fragments.BaseFragment;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.IncomeExpenditureFragment;
import com.zucc.ygg31501102.personmanager.fragments.schedule.ScheduleFragment;
import com.zucc.ygg31501102.personmanager.service.ScheduleRemind;

import java.io.FileNotFoundException;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import static com.zucc.ygg31501102.personmanager.BottomNavigationViewHelper.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static PersonManagerDatabaseHelper databaseHelper;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private PopupWindow window;
    private String[] FloatNavTitles = {"添加收支","添加日程"};
    public static int currentNavItem = 0;
    private ScheduleRemind.ScheduleRemindBinder scheduleRemindBinder;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager mLBM;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            scheduleRemindBinder = (ScheduleRemind.ScheduleRemindBinder) iBinder;
                scheduleRemindBinder.Timer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new PersonManagerDatabaseHelper(this,"PersonManager.db",null,1);
        databaseHelper.getWritableDatabase();
        mLBM = LocalBroadcastManager.getInstance(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                currentNavItem = 0;
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_dashboard:
                                currentNavItem = 1;
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.navigation_notifications:
                                currentNavItem = 2;
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.navigation_daiding:
                                currentNavItem = 3;
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*viewpager 配置结束*/

        final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*悬浮菜单*/
                // 构建一个popupwindow的布局
                View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);
                // 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
                ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
                lsvMore.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, FloatNavTitles));
                lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;
                        switch (position){
                            case 0:
                                currentNavItem = 0;
                                intent = new Intent(MainActivity.this, AddExpendActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                currentNavItem = 1;
                                intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                                startActivity(intent);
                                break;
                        }
                        window.dismiss();
                    }
                });
                //创建PopupWindow对象，指定宽度和高度
                window = new PopupWindow(popupView, 330, 600);
                //设置动画
                window.setAnimationStyle(R.style.popup_window_anim);
                // 设置背景颜色
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11112233")));
                // 设置可以获取焦点
                window.setFocusable(true);
                //设置可以触摸弹出框以外的区域
                window.setOutsideTouchable(true);
                // 更新popupwindow的状态
                window.update();
                // 以下拉的方式显示，并且可以设置显示的位置
                window.showAsDropDown(fab_add, -200, 0);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //开启推送服务
        Intent startIntent = new Intent(this, ScheduleRemind.class);
        bindService(startIntent,connection,BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zucc.ygg31501102.personmanager.removeschedule");
        localReceiver = new LocalReceiver();
        mLBM.registerReceiver(localReceiver,intentFilter);
        //sqlite可视化
        SQLiteStudioService.instance().start(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());

        adapter.addFragment(new IncomeExpenditureFragment());
        adapter.addFragment(new ScheduleFragment());
        adapter.addFragment(BaseFragment.newInstance("发现"));
        adapter.addFragment(BaseFragment.newInstance("更多"));
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.balance_statistics) {
            Intent intent = new Intent(MainActivity.this, BalanceStatisticsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setupViewPager(viewPager);
        viewPager.setCurrentItem(currentNavItem);

    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplication(),"接受到更新广播", Toast.LENGTH_SHORT).show();
            setupViewPager(viewPager);
            viewPager.setCurrentItem(currentNavItem);
        }
    }
}
