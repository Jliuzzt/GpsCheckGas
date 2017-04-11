package com.cug.gpscheckgas.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.ContainerViewPager;
import com.cug.gpscheckgas.util.Location;
import com.cug.gpscheckgas.util.LocationUtil;
import com.cug.gpscheckgas.util.MyViewPager;
import com.cug.gpscheckgas.util.ViewPagerAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final int TAB_TASK = 0;
    public static final int TAB_LOCATION = 1;
    public static final int TAB_FAULT = 2;
    public static final int TAB_NOTICE = 3;

    public ContainerViewPager containerViewPager;
    public RadioButton radioTask;
    public RadioButton radioLocation;
    public RadioButton radioFault;
    public RadioButton radioNotice;

    TaskFragment taskFragment;

    private ResideMenu resideMenu;
    private MainActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemPerson;
    private ResideMenuItem itemInput;
    private ResideMenuItem itemRecord;
    private ResideMenuItem itemSettings;
//    private ResideMenuItem itemExit;

    private Location mLocation;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        containerViewPager = (ContainerViewPager) findViewById(R.id.viewpager);
        radioTask = (RadioButton) findViewById(R.id.radioTask);
        radioLocation = (RadioButton) findViewById(R.id.radioLocation);
        radioFault = (RadioButton) findViewById(R.id.radioFault);
        radioNotice = (RadioButton) findViewById(R.id.radioNotice);

        radioTask.setOnClickListener(this);
        radioLocation.setOnClickListener(this);
        radioFault.setOnClickListener(this);
        radioNotice.setOnClickListener(this);

        initView();
        addPageChangeListener();

        mContext = this;
        setUpMenu();
//        if( savedInstanceState == null )
//            changeFragment(new HomeFragment());
//        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "7tf6yfp0w1HdsPW5nznnguLp");
        Constants.isLogout = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil = new LocationUtil(mContext, mLocation);
        locationUtil.registerListener();
        locationUtil.startService();

    }

    @Override
    protected void onStop() {
//        Intent intent = new Intent(MainActivity.this, FloatViewService.class);
//        stopService(intent);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        mLocation.unregisterListener(mListener); //注销掉监听
//        mLocation.stop(); //停止定位服务
//        Log.e("onDestroy", Constants.gpsInfo);
        super.onDestroy();
        locationUtil.unregisterListener();
        locationUtil.stopService();
    }

    private void initView() {

        List<Fragment> fragments = new ArrayList<Fragment>();

        taskFragment = new TaskFragment();
        LocationFragment locationFragment = new LocationFragment();
        FaultFragment faultFragment = new FaultFragment();
        NoticeFragment noticeFragment = new NoticeFragment();
        fragments.add(taskFragment);
        fragments.add(locationFragment);
        fragments.add(faultFragment);
        fragments.add(noticeFragment);
        this.containerViewPager.setOffscreenPageLimit(0);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager(), fragments, containerViewPager);


    }

    private void addPageChangeListener() {
        containerViewPager.setOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int id) {
//                Intent intent = new Intent(MainActivity.this, FloatViewService.class);
                switch (id) {
                    case TAB_TASK:
                        radioTask.setChecked(true);
//                        stopService(intent);
                        if(Constants.isMapLoad) {
                            LocationFragment.loadMap();
                            LocationFragment.closeMap();
                        }
                        break;
                    case TAB_LOCATION:
                        radioLocation.setChecked(true);
//                        stopService(intent);
                        if(Constants.isMapLoad) {
                            LocationFragment.loadMap();
                            LocationFragment.drawMap(getApplicationContext());
                        }
                        break;
                    case TAB_FAULT:
                        radioFault.setChecked(true);
//                        stopService(intent);
                        if(Constants.isMapLoad) {
                        LocationFragment.loadMap();
                        LocationFragment.closeMap();}
                        break;
                    case TAB_NOTICE:
                        radioNotice.setChecked(true);
                        NoticeFragment.updateView(getApplicationContext());
//                        startService(intent);
                        if(Constants.isMapLoad) {
                        LocationFragment.loadMap();
                        LocationFragment.closeMap();}
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
//        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background_6);
        resideMenu.attachToActivity(this);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.8f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        // create menu items;

        itemPerson = new ResideMenuItem(this, R.drawable.icon_person, "个人信息", null);
        itemHome = new ResideMenuItem(this, R.drawable.icon_home, "首页");
        itemInput = new ResideMenuItem(this, R.drawable.icon_input, "录入站场区域");
        itemRecord = new ResideMenuItem(this, R.drawable.icon_record, "巡检记录");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "切换用户");
//        itemExit = new ResideMenuItem(this, R.drawable.icon_exit, "退出");

        itemPerson.setOnClickListener(this);
        itemHome.setOnClickListener(this);
        itemInput.setOnClickListener(this);
        itemRecord.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
//        itemExit.setOnClickListener(this);

        resideMenu.addMenuItem(itemPerson, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemInput, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemRecord, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
//        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_LEFT);

    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.radioTask:
                containerViewPager.setCurrentItem(TAB_TASK, false);
//                doubleClick(v);
                break;
            case R.id.radioLocation:
                containerViewPager.setCurrentItem(TAB_LOCATION, false);
                break;
            case R.id.radioFault:
                containerViewPager.setCurrentItem(TAB_FAULT, false);
                break;
            case R.id.radioNotice:
                containerViewPager.setCurrentItem(TAB_NOTICE, false);
//                intent = new Intent(this, FloatViewService.class);
//                this.startService(intent);
                break;
        }

        if (v == itemHome) {
            resideMenu.closeMenu();
        } else if (v == itemPerson) {
            intent = new Intent(this, PersonActivity.class);
            startActivity(intent);
            resideMenu.closeMenu();
        } else if (v == itemInput) {
            intent = new Intent(this, InputActivity.class);
            startActivity(intent);
            resideMenu.closeMenu();
        } else if (v == itemRecord) {
            intent = new Intent(this, RecordActivity.class);
            startActivity(intent);
            resideMenu.closeMenu();
        } else if (v == itemSettings) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            logoutPosition();
            Constants.logout();
            MainActivity.this.finish();
            resideMenu.closeMenu();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            mHandle.sendEmptyMessage(1);
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“确认”后的操作
//                            mLocation.unregisterListener(mListener);
//                            mLocation.stop();


//                            Constants.sheetId = -1;

                            logoutPosition();
                            Constants.clear();
                            MainActivity.this.finish();

                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

//    private Handler mHandle = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    if (mHandle.hasMessages(2)) {
//                        moveTaskToBack(true);
//                    } else {
//                        Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
//                        mHandle.sendEmptyMessageDelayed(2, 2000);
//                    }
//                    break;
//                case 2:
//                    break;
//            }
//        }
//    };

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
//
//            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
//                exit();
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
//
//    public void exit() {
//
//        if (!isExit) {
//            isExit = true;
//            Toast.makeText(getApplicationContext(), "再点击一次退出",
//                    Toast.LENGTH_SHORT).show();
//            mHandler.sendEmptyMessageDelayed(0, 2000);
//        } else {
//            moveTaskToBack(true);
//            //finish();
//            //System.exit(0);
//        }
//    }
//
//    private static Boolean isExit = false;
//
//
//
//    Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            isExit = false;
//        }
//    };


//    private void changeFragment(Fragment targetFragment){
//        resideMenu.clearIgnoredViewList();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.main_fragment, targetFragment, "fragment")
//                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private void logoutPosition(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.LOGOUT_POSITION_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("gener_id", Constants.peopleId);
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("onSuccess", new String(bytes));

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("onFailure",new String(bytes));
                Log.e("onFailure",throwable.toString());
            }
        });
    }
}
