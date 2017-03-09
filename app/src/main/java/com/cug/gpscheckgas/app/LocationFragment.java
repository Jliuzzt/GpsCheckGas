package com.cug.gpscheckgas.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.Location;
import com.cug.gpscheckgas.util.LocationUtil;
import com.cug.greendao.HistoryRoute;
import com.cug.greendao.PeriodInfo;
import com.cug.greendao.SheetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class LocationFragment extends Fragment implements View.OnClickListener /*implements BaiduMap.OnMapLoadedCallback*/ {

    private static MapView mMapView = null;
    private View loactionView = null;
    private static BaiduMap mBaiduMap;
    private static MapStatus mMapStatus;
    private static Polyline mPolyline;
    private static Polyline mPolylineWalk;
//    private InfoWindow mInfoWindow;
//    private Marker mMarkerA;
//    BitmapDescriptor bd = BitmapDescriptorFactory
//            .fromResource(R.drawable.icon_gcoding);
//    List<LatLng> points = new ArrayList<LatLng>();
//    List<LatLng> pointsWalk = new ArrayList<LatLng>();

//    LatLng p5 = new LatLng(30.513, 114.406);
//    LatLng p6 = new LatLng(30.5133, 114.404);
//    LatLng p7 = new LatLng(30.5143, 114.402);
//    LatLng p8 = new LatLng(30.5178, 114.398);
//    LatLng p9 = new LatLng(30.5184, 114.397);
//    LatLng p10 = new LatLng(30.5201, 114.395);
//    LatLng p11 = new LatLng(30.5221, 114.392);
//    LatLng p12 = new LatLng(30.5227, 114.389);
//    LatLng p13 = new LatLng(30.5236, 114.385);
//    LatLng p14 = new LatLng(30.5237, 114.384);
//    LatLng p15 = new LatLng(30.5248, 114.379);
//    LatLng p16 = new LatLng(30.5251, 114.378);
//    LatLng p17 = new LatLng(30.5255, 114.377);
//    LatLng p18 = new LatLng(30.5262, 114.375);
//    LatLng p19 = new LatLng(30.5286, 114.371);
//    LatLng p20 = new LatLng(30.53, 114.369);
//    LatLng p21 = new LatLng(30.5309, 114.367);
//    LatLng p22 = new LatLng(30.5313, 114.367);
//    LatLng p23 = new LatLng(30.5326, 114.362);
//    LatLng p24 = new LatLng(30.5328, 114.361);
//    LatLng p25 = new LatLng(30.5328, 114.361);
//    LatLng p26 = new LatLng(30.5332, 114.359);
//    LatLng p27 = new LatLng(30.5337, 114.358);
//    LatLng p28 = new LatLng(30.5344, 114.355);
//    LatLng p29 = new LatLng(30.5358, 114.351);
//    LatLng p30 = new LatLng(30.5357, 114.351);
//    LatLng p31 = new LatLng(30.5368, 114.348);
//    LatLng p32 = new LatLng(30.5375, 114.346);


    BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png");
    BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");
    static BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");

//    Button twoD;
//    private final static int SCANNIN_GREQUEST_CODE = 1;
//
//    /**
//     * 显示扫描结果
//     */
//    private TextView mTextView;

//---------------------------历史轨迹---------------------------

    private Button input, finish;
    private static Spinner choosePeriod, chooseDate;
    private static List<HistoryRoute> historyRoutes;
    private static List<HistoryRoute> daliyHistoryRoutes;
    private static SheetInfo sheetInfo;
    private static PeriodInfo periodInfo;
    private static ArrayAdapter<String> arrayAdapter = null;
    private static ArrayAdapter<String> arrayAdapter2 = null;
    private static Marker mMarkerPeople;
    private Context mContext;
    private boolean isOverWalk;
    private static final int TEMP_SHEET_ID = -1;
    private static final int TEMP_PERIOD_ID = -1;
    private static final int REQUEST_CODE = 5;
    private int regionId;

    private Location mLocation;
    private LocationUtil locationUtil;
    private MainActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "dadgfg");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "dadgfg");
        Constants.isMapLoad = true;
        loactionView = inflater.inflate(R.layout.fragment_location, container, false);
        mMapView = (MapView) loactionView.findViewById(R.id.bmapView);
        input = (Button) loactionView.findViewById(R.id.deviceInput);
        finish = (Button) loactionView.findViewById(R.id.inputFinish);
        choosePeriod = (Spinner) loactionView.findViewById(R.id.choosePeriod);
        chooseDate = (Spinner) loactionView.findViewById(R.id.chooseDate);
        input.setOnClickListener(this);
        finish.setOnClickListener(this);
        mContext = this.getContext();

        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        String[] selfLocation = Constants.gpsInfo.split(",");
        LatLng latLng = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_self_location);
        MarkerOptions option = new MarkerOptions().position(latLng).icon(bd).zIndex(0).draggable(true);
        mMarkerPeople = (Marker) (mBaiduMap.addOverlay(option));
        drawMap(mContext);

        return loactionView;
    }

    public static void drawMap(final Context context) {
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        String[] selfLocation = Constants.gpsInfo.split(",");
        final LatLng latLng = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
        mMapStatus = new MapStatus.Builder().target(latLng).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
//        initOverLay(30.513, 114.406);
//        initOverLay(30.5133, 114.404);
//        initOverLay(30.5143, 114.402);
//        initOverLay(30.5178, 114.398);
//        initOverLay(30.5184, 114.397);
//        initOverLay(30.5201, 114.395);
//        initOverLay(30.5221, 114.392);
//        initOverLay(30.5227, 114.389);
//        initOverLay(30.5236, 114.385);
//        initOverLay(30.5237, 114.384);
//        initOverLay(30.5248, 114.379);
//        initOverLay(30.5251, 114.378);
//        initOverLay(30.5255, 114.377);
//        initOverLay(30.5262, 114.375);
//        initOverLay(30.5286, 114.371);
//        initOverLay(30.53, 114.369);
//        initOverLay(30.5309, 114.367);
//        initOverLay(30.5313, 114.367);
//        initOverLay(30.5326, 114.362);
//        initOverLay(30.5328, 114.361);
//        initOverLay(30.5328, 114.361);
//        initOverLay(30.5332, 114.359);
//        initOverLay(30.5337, 114.358);
//        initOverLay(30.5344, 114.355);
//        initOverLay(30.5358, 114.351);
//        initOverLay(30.5357, 114.351);
//        initOverLay(30.5368, 114.348);
//        initOverLay(30.5375, 114.346);
//
//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(final Marker marker) {
//                final View popup = View.inflate(getContext(), R.layout.poplayout, null);
//                TextView deviceId = (TextView) popup.findViewById(R.id.deviceid);
//                TextView location = (TextView) popup.findViewById(R.id.location);
//                popup.setBackgroundResource(R.drawable.poi_background_3);
//
////                Button button = new Button(getContext());
////                button.setBackgroundResource(R.drawable.poi_red);
//                InfoWindow.OnInfoWindowClickListener listener = null;
//                if (marker == mMarkerA) {
//                    deviceId.setText("设备号：10010");
//                    location.setText("地址：鲁磨路388号");
//
//                    listener = new InfoWindow.OnInfoWindowClickListener() {
//                        public void onInfoWindowClick() {
////                            LatLng ll = marker.getPosition();
////                            LatLng llNew = new LatLng(ll.latitude + 0.005,
////                                    ll.longitude + 0.005);
////                            marker.setPosition(llNew);
//                            Refresh();
//                            mBaiduMap.hideInfoWindow();
//                        }
//                    };
//                    LatLng ll = marker.getPosition();
//                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(popup), ll, -47, listener);
//                    mBaiduMap.showInfoWindow(mInfoWindow);
//                }
//                return true;
//            }
//
//        });
//
//
////        List<LatLng> points = new ArrayList<LatLng>();
//
//        points.add(p5);
//        points.add(p6);
//        points.add(p7);
//        points.add(p8);
//        points.add(p9);
//        points.add(p10);
//        points.add(p11);
//        points.add(p12);
//        points.add(p13);
//        points.add(p14);
//        points.add(p15);
//        points.add(p16);
//        points.add(p17);
//        points.add(p18);
//        points.add(p19);
//        points.add(p20);
//        points.add(p21);
//        points.add(p22);
//        points.add(p23);
//        points.add(p24);
//        points.add(p25);
//        points.add(p26);
//        points.add(p27);
//        points.add(p28);
//        points.add(p29);
//        points.add(p30);
//        points.add(p31);
//        points.add(p32);
//
//        pointsWalk.add(p31);
//        pointsWalk.add(p32);
////        OverlayOptions ooPolyline = new PolylineOptions().width(10)
////                .color(0xAA4D5A5A).points(points);
////        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
//
//        OverlayOptions ooPolylineWalk = new PolylineOptions().width(20)
//                .color(0xAAB9B973).points(pointsWalk);
//        mPolylineWalk = (Polyline) mBaiduMap.addOverlay(ooPolylineWalk);
//
//
//        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
////        textureList.add(mRedTexture);
//        textureList.add(mBlueTexture);
////        textureList.add(mGreenTexture);
//        List<Integer> textureIndexs = new ArrayList<Integer>();
//        int i = 0;
//        if (i >= 0 && i <= 27) {
//            textureIndexs.add(i);
//        }
//        OverlayOptions ooPolyline11 = new PolylineOptions().width(20)
//                .points(points).dottedLine(true).customTextureList(textureList).textureIndex(textureIndexs);
//        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline11);
//
//
//        //扫描二维码
//        mTextView = (TextView) loactionView.findViewById(R.id.result);
//        twoD = (Button) loactionView.findViewById(R.id.device_2d);
//        twoD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getContext(), MipcaActivityCapture.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
////                startActivity(intent);
//            }
//        });
//

//-------------------------历史轨迹--------------------------------
//        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_self_location);
//        MarkerOptions option = new MarkerOptions().position(latLng).icon(bd)
//                .zIndex(0).draggable(true);
//        mMarkerPeople = (Marker) (mBaiduMap.addOverlay(option));

        final Timer timer = new Timer(true);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (!Constants.isLogout) {
                    mMarkerPeople.remove();
                    String[] selfLocation = Constants.gpsInfo.split(",");
                    LatLng latLng = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
                    BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_self_location);
                    MarkerOptions option = new MarkerOptions().position(latLng).icon(bd).zIndex(0).draggable(true);
                    mMarkerPeople = (Marker) (mBaiduMap.addOverlay(option));
                    mMarkerPeople.setPosition(latLng);
                } else if (Constants.isLogout) {
                    timer.cancel();
                }
            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.scheduleAtFixedRate(task, 0000, 2000);

//        historyRoutes = DBHelper.getInstance(context).getRouteByGener(Constants.peopleId, timestampToString(new Timestamp(System.currentTimeMillis())));

        historyRoutes = DBHelper.getInstance(context).getAllRoutes();
        final List<String> dateList = new ArrayList<String>();
        boolean isHad = false;
        if (historyRoutes == null) {
            dateList.add("无历史日期");
        } else {
            for (int j = 0; j < historyRoutes.size(); j++) {
                if (dateList.size() < 1) {
                    dateList.add(historyRoutes.get(j).getStartTime());
                } else if (dateList.size() >= 1) {
                    for (int k = 0; k < dateList.size(); k++) {
                        if (dateList.get(k).toString().equals(historyRoutes.get(j).getStartTime())) {
                            isHad = true;
                            break;
                        }else{
                            isHad = false;
                        }
                    }
                    if (!isHad) {
                        dateList.add(historyRoutes.get(j).getStartTime());
                    }
                }
            }
        }

        arrayAdapter2 = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, dateList) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
                label.setText(dateList.get(position));
                if (choosePeriod.getSelectedItemPosition() == position) {
                    convertView.setBackgroundColor(context.getResources().getColor(
                            R.color.spinner_checked));
                    label.setTextColor(Color.parseColor("#36bbb1"));
                } else {
                    convertView.setBackgroundColor(context.getResources().getColor(
                            R.color.spinner_unchecked));
                    label.setTextColor(Color.parseColor("#808080"));
                }

                return convertView;
            }
        };
        arrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        chooseDate.setAdapter(arrayAdapter2);
        chooseDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (historyRoutes == null) {
                    Toast.makeText(context, "无历史巡检轨迹", Toast.LENGTH_SHORT).show();
                    choosePeriod.setEnabled(false);
                } else {
                    daliyHistoryRoutes = DBHelper.getInstance(context).getRouteByGener(Constants.peopleId, chooseDate.getSelectedItem().toString());
                    final List<String> mList = new ArrayList<String>();
                    if (daliyHistoryRoutes == null) {
                        mList.add("今日无历史巡检轨迹");
                    } else {
                        for (int j = 0; j < daliyHistoryRoutes.size(); j++) {
                            sheetInfo = DBHelper.getInstance(context).getSheetInfoById(daliyHistoryRoutes.get(j).getSheetId());
                            periodInfo = DBHelper.getInstance(context).getPeriodInfo(daliyHistoryRoutes.get(j).getPeriodId());
                            if (sheetInfo == null || periodInfo == null) {
                                mList.add(daliyHistoryRoutes.get(j).getId().toString() + "  -  " + "新录入巡检路线");
                            } else {
                                mList.add(daliyHistoryRoutes.get(j).getId().toString() + "  -  " + sheetInfo.getSheetName() + "  -  " + periodInfo.getPeriodTime() + "  -  " + periodInfo.getPeriodShift());
                            }
                        }
                    }

                    arrayAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, mList) {

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater mInflater = LayoutInflater.from(context);
                            convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                            TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
                            label.setText(mList.get(position));
                            if (choosePeriod.getSelectedItemPosition() == position) {
                                convertView.setBackgroundColor(context.getResources().getColor(
                                        R.color.spinner_checked));
                                label.setTextColor(Color.parseColor("#36bbb1"));
                            } else {
                                convertView.setBackgroundColor(context.getResources().getColor(
                                        R.color.spinner_unchecked));
                                label.setTextColor(Color.parseColor("#808080"));
                            }

                            return convertView;
                        }
                    };
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    choosePeriod.setAdapter(arrayAdapter);
                    choosePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (mPolyline != null) {
                                mPolyline.remove();
                            }
                            if (mPolylineWalk != null) {
                                mPolylineWalk.remove();
                            }
                            if (daliyHistoryRoutes == null) {
                                Toast.makeText(context, "今日无历史巡检轨迹", Toast.LENGTH_SHORT).show();
                            } else {
                                String[] routeInfo = choosePeriod.getSelectedItem().toString().split("  -  ");
                                HistoryRoute historyRoute = DBHelper.getInstance(context).getRouteById(Integer.parseInt(routeInfo[0]));

                                List<LatLng> pointsWalk = new ArrayList<LatLng>();
                                if (historyRoute == null) {
                                    Toast.makeText(context, "今日无历史巡检轨迹", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONArray jsonArray = new JSONArray(historyRoute.getPoints());
                                        int i = 0;
                                        while (i < jsonArray.length()) {
                                            Double latitude = Double.valueOf(jsonArray.getJSONObject(i).getString("latitude"));
                                            Double longitude = Double.valueOf(jsonArray.getJSONObject(i).getString("longitude"));
                                            Log.e("latitude", latitude + "");
                                            Log.e("longitude", longitude + "");
                                            LatLng latLng = new LatLng(latitude, longitude);
                                            pointsWalk.add(latLng);
                                            i++;
                                        }
                                        OverlayOptions mPolyline = new PolylineOptions().width(15)
                                                .points(pointsWalk).dottedLine(true).customTexture(mGreenTexture);
                                        mPolylineWalk = (Polyline) mBaiduMap.addOverlay(mPolyline);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "历史轨迹无法显示", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private static String timestampToString(Timestamp ts) {
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tsStr = sdf.format(ts);
            return tsStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public void initOverLay(double latitude, double longitude) {
//        // 定义Maker坐标点
//        LatLng l = new LatLng(latitude, longitude);
//        // 构建Marker图标
//
//        // 构建MarkerOption，用于在地图上添加Marker
////        OverlayOptions option = new MarkerOptions().position(ll).icon(bd1);
//        MarkerOptions option = new MarkerOptions().position(l).icon(bd)
//                .zIndex(9).draggable(true);
//        mMarkerA = (Marker) (mBaiduMap.addOverlay(option));
//        // 在地图上添加Marker，并显示
////        mBaiduMap.addOverlay(option);//添加当前信息图层
////        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
////        mBaiduMap.setMapStatus(u);
//
//    }
//
//
//    public void Refresh() {
//
//        final Handler handler = new Handler() {
//            int x = 25;
//
//            public void handleMessage(Message msg) {
//                //                LatLng p1 = new LatLng(30.51 + x, 114.31 + x);
//                if (x <= 28 && x >= 0) {
//                    LatLng p1 = new LatLng(points.get(x).latitude, points.get(x).longitude);
//                    pointsWalk.add(p1);
//                    mPolylineWalk.setPoints(pointsWalk);
////                mMarkerA.setPosition(p1);
//                    x = x - 1;
//                }
//            }
//        };
//
//        TimerTask task = new TimerTask() {
//            public void run() {
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        };
//
//        Timer timer = new Timer(true);
//        timer.schedule(task, 1000, 3000);
//    }

    public static void closeMap() {
        mMapView.onPause();
    }

    public static void loadMap() {
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Log.e("onDestroy", "dadgfg");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        Log.e("onPause", "dadgfg");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.e("onResume", "dadgfg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deviceInput:
//                mPolylineWalk.remove();
                new AlertDialog.Builder(mContext).setTitle("该巡检站是否已录入（有无二维码）？")
                        .setMessage("请每次录入一个巡线点数据")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("未录入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isOverWalk = false;
                                input.setVisibility(View.GONE);
                                finish.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(mContext, InputActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("已录入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isOverWalk = false;
                                input.setVisibility(View.GONE);
                                finish.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(mContext, MipcaActivityCapture.class);
                                intent.putExtra("region_type", "route");
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        }).show();
                break;
            case R.id.inputFinish:
                isOverWalk = true;
                Toast.makeText(mContext, "请等待", Toast.LENGTH_SHORT).show();
                finish.setVisibility(View.GONE);
                input.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
            regionId = data.getIntExtra("region_id", -1);
            if (Constants.teamName != null && Constants.teamName != "巡检员") {
                setRoute(regionId);
            } else {
                Toast.makeText(mContext, "您没有权限录入管线信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRoute(final int regionId) {
        Toast.makeText(mContext, "开始录入管线数据", Toast.LENGTH_SHORT).show();
        final Timer timer = new Timer(true);
        final List<LatLng> pointsWalk = new ArrayList<LatLng>();

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (!isOverWalk) {
                    String[] selfLocation = Constants.gpsInfo.split(",");
                    LatLng p1 = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
                    pointsWalk.add(p1);
                    postRoute(regionId, selfLocation[0].toString(), selfLocation[1].toString());
                    if (pointsWalk.size() > 1 && pointsWalk.size() < 3) {
                        OverlayOptions mPolylineWalk = new PolylineOptions().width(20)
                                .color(0xAAB9B973).points(pointsWalk);
                        mPolyline = (Polyline) mBaiduMap.addOverlay(mPolylineWalk);
                    } else if (pointsWalk.size() > 3) {
                        mPolyline.setPoints(pointsWalk);
                        mMarkerPeople.setPosition(p1);
                    }
                    HistoryRoute historyRoute = DBHelper.getInstance(mContext).getRouteBySPR(TEMP_SHEET_ID, TEMP_PERIOD_ID, regionId);
                    try {
                        JSONArray jsonArray;
                        if (historyRoute == null) {
                            jsonArray = new JSONArray();
                            historyRoute = new HistoryRoute();
                            historyRoute.setSheetId(TEMP_SHEET_ID);
                            historyRoute.setPeriodId(TEMP_PERIOD_ID);
                            historyRoute.setGenerId(Constants.peopleId);
                            historyRoute.setStartTime(timestampToString(new Timestamp(System.currentTimeMillis())));
                            historyRoute.setEndTime(String.valueOf(regionId));
                        } else {
                            jsonArray = new JSONArray(historyRoute.getPoints());
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("latitude", selfLocation[1]);
                        jsonObject.put("longitude", selfLocation[0]);
                        jsonArray.put(jsonObject);
                        historyRoute.setPoints(jsonArray.toString());
                        Log.e("route", jsonArray.toString());
                        DBHelper.getInstance(mContext).updateHistoryRoute(historyRoute);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "结束录入", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    drawMap(mContext);
                }
            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.scheduleAtFixedRate(task, 0000, 30000);

    }

    private void postRoute(int regionId, String longitude, String latitude) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.POST_ROUTE_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("gener_id", Constants.peopleId);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("region_id", regionId);
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("onSuccessgps", new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("onFailuregps", new String(bytes));
                Log.e("onFailuregps", throwable.toString());
            }
        });
    }

}
