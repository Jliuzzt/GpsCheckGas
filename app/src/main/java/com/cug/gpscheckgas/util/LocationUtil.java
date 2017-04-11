package com.cug.gpscheckgas.util;

import android.app.Activity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by WF on 2016/4/6.
 */
public class LocationUtil {
    private Activity mContext;
    private Location mLocation;
    private String gps;

    public LocationUtil(Activity mContext, Location mLocation) {
        this.mContext = mContext;
        this.mLocation = mLocation;
        init();
    }

    public void init() {
        mLocation = ((BaseApplication) mContext.getApplication()).mLocation;
    }

    public void registerListener() {
        mLocation.registerListener(listener);
        //注册监听
        int type = mContext.getIntent().getIntExtra("from", 0);
        if (type == 0) {
            mLocation.setLocationOption(mLocation.getDefaultLocationClientOption());
        } else if (type == 1) {
            mLocation.setLocationOption(mLocation.getOption());
        }
    }

    public void unregisterListener() {
        mLocation.unregisterListener(listener); //注销掉监听
    }

    public void startService() {
        mLocation.start();
        Log.e("startService", "startService");
    }

    public void stopService() {
        mLocation.stop(); //停止定位服务
        Log.e("stopService", "stopService");
    }

    public BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (location != null && location.getLocType() != BDLocation.TypeServerError) {
                gps = location.getLongitude() + "," + location.getLatitude();
                Constants.gpsInfo = "";
                Constants.gpsInfo = gps;
                Log.e("GPS", Constants.gpsInfo);
//                Toast.makeText(mContext, gps, Toast.LENGTH_SHORT).show();

//                final Timer timer = new Timer(true);
//                final Handler handler = new Handler() {
//                    public void handleMessage(Message msg) {
//                        if (!Constants.isUploadOver) {
////                            postRoute();
//                        } else {
//                            timer.cancel();
//                        }
//                    }
//                };
//
//                TimerTask task = new TimerTask() {
//                    public void run() {
//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
//                    }
//                };
//                timer.scheduleAtFixedRate(task, 0000, 30000);
            }
        }
    };

    private void postRoute() {
        Constants.isUploadOver = false;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.POST_ROUTE_DATA_STRING;

        String[] gps = Constants.gpsInfo.split(",");

        RequestParams params = new RequestParams();
        Log.e("peopleid", Constants.peopleId + "");
        params.put("gener_id", Constants.peopleId);
        params.put("longitude", gps[0]);
        params.put("latitude", gps[1]);
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("onSuccessgps", new String(bytes));
                Constants.isUploadOver = true;
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("onFailuregps", new String(bytes));
                Log.e("onFailuregps", throwable.toString());
            }
        });
    }

}
