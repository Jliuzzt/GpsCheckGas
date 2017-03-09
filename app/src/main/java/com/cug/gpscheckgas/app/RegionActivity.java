package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
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
import com.cug.gpscheckgas.util.RegionTaskAdapter;
import com.cug.greendao.HistoryRoute;
import com.cug.greendao.PeopleInfo;
import com.cug.greendao.PeriodInfo;
import com.cug.greendao.RecordInfo;
import com.cug.greendao.RegionInfo;
import com.cug.greendao.SheetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
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

public class RegionActivity extends Activity implements View.OnClickListener, GridView.OnItemClickListener {

    private RegionTaskAdapter regionTaskAdapter = null;
    private List<RegionInfo> regionInfos = null;
    private List<PeriodInfo> periodInfos;
    private GridView regionGridView = null;
    private Button qrcodeButton;
    private ProgressBar loading;
    private MapView mMapView = null;
    private final static int REQUEST_CODE = 1;
    private ImageButton backButton;
    private Spinner period;
    private Button finish, submit;
    private ArrayAdapter<String> arrayAdapter = null;
    private PeriodInfo periodInfo = null;
    private List<String> mList;
    private int sheetId;
    private String status;
    private int[] periodIds;
    private final static int RESULT_CODE = 1;
    private final static int RESULT_CODE_2 = 2;
    private final static int RESULT_CODE_3 = 3;
    private final static int RESULT_CODE_BACK = 4;
    private int checkTime = -2;
    private TextView sheetName, sheetIntro;
    private Boolean beginCheck = false;
    private BaiduMap mBaiduMap;
    private MapStatus mMapStatus;
    private Marker mMarkerRegion;
    private Marker mMarkerPeople;
    private Polyline mPolylineWalk;
    private Polyline mPolyline;
    BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png");
    BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");
    BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
    private boolean isOverWalk;

    private RelativeLayout mMarkerInfoLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_region);
//        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
//        dbHelper.deletePeriodInfo();
//        addDataToDb();
//        addDataToDb1();

        periodIds = new int[100];
//        for (int a = 0; a < 100; a++) {
//            Constants.flag[a] = false;
//        }

        sheetName = (TextView) findViewById(R.id.sheetName);
        sheetIntro = (TextView) findViewById(R.id.sheetIntro);
        period = (Spinner) findViewById(R.id.spinner);
        regionGridView = (GridView) findViewById(R.id.regiontask);

        sheetId = this.getIntent().getIntExtra("sheetId", 0);
        final String name = this.getIntent().getStringExtra("sheetName");
        status = this.getIntent().getStringExtra("status");

        String intro = DBHelper.getInstance(getApplicationContext()).getSheetIntro(sheetId);
        sheetIntro.setText(intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sheetName.setText(name);
            }
        }, 2000);


        periodInfos = DBHelper.getInstance(getApplicationContext()).queryPeriod(sheetId);
        getData();

//
//        periodInfos = DBHelper.getInstance(getApplicationContext()).queryPeriod(sheetId);
//
//        mList = new ArrayList<String>();
////        mList.add("请选择班次与时间点");
//        for (int i = 0; i < periodInfos.size(); i++) {
//            mList.add(periodInfos.get(i).getPeriodShift() + "  -  " + periodInfos.get(i).getPeriodTime());
//        }
//
//        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, mList) {
//
//            @Override
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
//                convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
//                TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
//                label.setText(mList.get(position));
//                if (period.getSelectedItemPosition() == position) {
//                    convertView.setBackgroundColor(getResources().getColor(
//                            R.color.spinner_checked));
//                    label.setTextColor(Color.parseColor("#36bbb1"));
//                } else {
//                    convertView.setBackgroundColor(getResources().getColor(
//                            R.color.spinner_unchecked));
//                    label.setTextColor(Color.parseColor("#808080"));
//                }
//
//                return convertView;
//            }
//        };
//        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        period.setAdapter(arrayAdapter);
//        period.setOnItemSelectedListener(this);

//        regionInfos = DBHelper.getInstance(getApplication()).getAllRegionInfo();
//        regionTaskAdapter = new RegionTaskAdapter(this, regionInfos);
//        regionGridView.setAdapter(regionTaskAdapter);

        mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        String[] selfLocation = Constants.gpsInfo.split(",");
        LatLng latLng = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
        mMapStatus = new MapStatus.Builder().target(latLng).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);


        loading = (ProgressBar) findViewById(R.id.loading);
        qrcodeButton = (Button) findViewById(R.id.qrcode);
        qrcodeButton.setOnClickListener(this);

        backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(this);

        finish = (Button) findViewById(R.id.finish);
        submit = (Button) findViewById(R.id.submit);
        if (status.equals("finish")) {
            finish.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
//            finish.setOnClickListener(this);
        } else if (status.equals("change")) {
            submit.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
            qrcodeButton.setVisibility(View.GONE);
            submit.setOnClickListener(this);
            regionGridView.setOnItemClickListener(this);
        }
        initMapClickEvent();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.qrcode:
                if (regionInfos.get(0).getRegionType().equals("route")) {
                    setRoute();
                }
                period.setEnabled(false);
                beginCheck = true;
                finish.setVisibility(View.VISIBLE);
                finish.setOnClickListener(this);
                intent = new Intent(RegionActivity.this, MipcaActivityCapture.class);
                //把所选的时间字段也传到record页面
//                String shiftTime = period.getSelectedItem().toString();
                intent.putExtra("region_type", "site");
                intent.putExtra("check_time", checkTime);
                intent.putExtra("status", status);
                intent.putExtra("sheet_id", sheetId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.back:
//                intent = new Intent();
//                intent.putExtra("sheet_id", sheetId);
//                setResult(RESULT_CODE, intent);
//                finish();
                if (beginCheck == true) {
                    new AlertDialog.Builder(this).setMessage("未完成任务时不可关闭窗口")
                            .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    mMapView.onDestroy();
                    intent = new Intent();
                    setResult(RESULT_CODE_BACK, intent);
                    finish();
                }
                break;
            case R.id.finish:
                new AlertDialog.Builder(this).setMessage("巡检任务已完成？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Constants.ScanRegionids.clear();//清空扫描记录的巡检区域ID
//                                Constants.doneTime++;
                                mMapView.onDestroy();
                                isOverWalk = true;
                                int doneTime = DBHelper.getInstance(getApplicationContext()).getDoneTimeById(sheetId);
                                SheetInfo sheetInfo = DBHelper.getInstance(getApplicationContext()).getSheetInfoById(sheetId);
                                sheetInfo.setDoneTime(doneTime + 1);
                                sheetInfo.setLength(periodInfos.size());
                                if (doneTime + 1 < periodInfos.size()) {
                                    sheetInfo.setIsDoing(true);
                                    sheetInfo.setIsDone(false);
                                } else if (doneTime + 1 == periodInfos.size()) {
                                    sheetInfo.setIsDoing(false);
                                    sheetInfo.setIsDone(true);
                                }
                                DBHelper.getInstance(getApplicationContext()).updateSheetInfo(sheetInfo);
                                Constants.isSelected.put(checkTime, 2);
                                Intent intent = new Intent();
                                intent.putExtra("sheet_id", sheetId);
//                                intent.putExtra("length", periodInfos.size());
//                                intent.putExtra("doneTime", Constants.doneTime);
                                setResult(RESULT_CODE, intent);
                                finish();

                            }
                        })
                        .setNegativeButton("不是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                break;
            case R.id.submit:
                new AlertDialog.Builder(this).setMessage("确认提交修改吗？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMapView.onDestroy();
                                Intent intent = new Intent();
                                intent.putExtra("sheet_id", sheetId);
                                setResult(RESULT_CODE_2, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("不是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (beginCheck == true) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                new AlertDialog.Builder(this).setMessage("未完成任务时不可关闭窗口")
                        .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int regionId = data.getExtras().getInt("region_id");
            Constants.checkTime = data.getIntExtra("check_time", -1);

//            String recordId = data.getStringExtra("record_id");
//            if (!Constants.rtr.containsKey(regionId)) {
//                Constants.rtr.put(regionId, recordId);
//            }
            Log.e("regionID", regionId + "");
            for (int i = 0; i < regionInfos.size(); i++) {
                if (regionInfos.get(i).getRegionId() == regionId) {
                    if (regionInfos.get(i).getRegionType().equals("site")) {
                        int sort = regionInfos.get(i).getRegionSort();
//                    regionTaskAdapter.updateView(regionId);

//                    Constants.regioinId = sort;
                        RegionInfo regionInfo = DBHelper.getInstance(getApplication()).getOneRegionByPeriod(checkTime, sort);
                        regionInfo.setIsDone(true);
                        DBHelper.getInstance(getApplicationContext()).updateRegionInfo(regionInfo);

                        List<RegionInfo> regionInfos = DBHelper.getInstance(getApplication()).getRegionByPeriod(checkTime);
                        regionTaskAdapter.updateChangeView(regionInfos);
//                    regionTaskAdapter.updateView(sort);
//                    regionTaskAdapter.notifyDataSetChanged();
                    } else if (regionInfos.get(i).getRegionType().equals("route")) {
                        RegionInfo regionInfo = DBHelper.getInstance(getApplication()).getOneRegionByRegion(checkTime, regionId);
                        regionInfo.setIsDone(true);
                        DBHelper.getInstance(getApplicationContext()).updateRegionInfo(regionInfo);
                        getRoute(regionInfos.get(i).getRegionId(), true);
                        Log.e("getRegionId", regionInfos.get(i).getRegionId() + "");
//                        getRoute(10, true);
                        String[] gps = regionInfos.get(i).getRegionGps().split(",");
                        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding_done);
                        LatLng l = new LatLng(Double.valueOf(gps[1].toString()), Double.valueOf(gps[0].toString()));
                        MarkerOptions option = new MarkerOptions().position(l).icon(bd)
                                .zIndex(1).draggable(true);
                        mMarkerRegion = (Marker) (mBaiduMap.addOverlay(option));
                    }
                }
            }
//            String shiftTime = period.getSelectedItem().toString();
//            if(checkTime.equals(shiftTime)) {
//            int regionId = regionInfos.get(position).getId();


//            }
        } else if (resultCode == 3) {
        }

    }

    public void getData() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.PERIOD_DATA_STRING;

        RequestParams paramsPeriod = new RequestParams();
        paramsPeriod.put("index", "sheet");
        paramsPeriod.put("sheet_id", sheetId);

        if (periodInfos.isEmpty()) {
            asyncHttpClient.get(url, paramsPeriod, new BaseJsonHttpResponseHandler<JSONArray>() {
                @Override
                public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                    Log.e("onSuccess", jsonArray + "");
                    try {
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(k);
                            int id = jsonObject.getInt("id");
                            periodIds[k] = id;
                            String time = jsonObject.getString("time");
                            String shift = jsonObject.getString("shift");

                            PeriodInfo periodInfo = new PeriodInfo((long) id, id, sheetId, time, shift);
                            DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                            dbHelper.insertPeriodInfo(periodInfo);
                        }
                        Log.e("onSuccess", "PeriodInfo插入成功");

                        periodInfos = DBHelper.getInstance(getApplicationContext()).queryPeriod(sheetId);
                        //已检查巡检次数
//                        if (status.equals("finish")&&beginCheck == true) {
//                            Constants.doneTime++;
//                        }

                        mList = new ArrayList<String>();
                        //        mList.add("请选择班次与时间点");
                        for (int l = 0; l < periodInfos.size(); l++) {
                            String usedTime = compareTime(periodInfos.get(l).getPeriodShift().split(":"));
                            if (usedTime != null) {
                                mList.add(periodInfos.get(l).getPeriodShift() + "  -  " + periodInfos.get(l).getPeriodTime());
                            }
                        }

                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, mList) {

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
                                convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                                TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
                                label.setText(mList.get(position));
                                if (period.getSelectedItemPosition() == position) {
                                    convertView.setBackgroundColor(getResources().getColor(
                                            R.color.spinner_checked));
                                    label.setTextColor(Color.parseColor("#36bbb1"));
                                } else {
                                    convertView.setBackgroundColor(getResources().getColor(
                                            R.color.spinner_unchecked));
                                    label.setTextColor(Color.parseColor("#808080"));
                                }

                                return convertView;
                            }
                        };
                        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        period.setAdapter(arrayAdapter);
                        period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String shiftTime = period.getSelectedItem().toString();
                                Log.e("shiftTime", shiftTime);
                                String[] time = shiftTime.split("  -  ");
                                String[] ctime = time[0].split(":");
                                checkTime = DBHelper.getInstance(getApplication()).queryPeriodId(time[0], time[1], sheetId);
                                Log.e("onSuccess", checkTime + "");
//                                Constants.isSelected.put(checkTime, false);
                                DateFormat sdf = new SimpleDateFormat("HH:mm");
                                String[] currentTime = sdf.format(new Timestamp(System.currentTimeMillis())).split(":");
                                if(Integer.parseInt(ctime[0]) - Integer.parseInt(currentTime[0]) > 0){
                                    Toast.makeText(RegionActivity.this, "您不在当前时间点，巡检结果可能有误，请按规范操作！", Toast.LENGTH_SHORT).show();
                                }
                                getPeriodData(checkTime);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("onSuccess", "PeriodInfo插入失败");
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONArray jsonArray) {
                    Log.e("onFailure", jsonArray + "");
                }

                @Override
                protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                    JSONArray jsonArray = new JSONArray(s);
                    return jsonArray;
                }
            });

        } else {
            Log.e("onSuccess", "periodInfos本地数据");
//            periodInfos = DBHelper.getInstance(getApplicationContext()).queryPeriod(sheetId);
            //已检查巡检次数
//            if (status.equals("finish")&&beginCheck == true) {
//                Constants.doneTime++;
//            }
            mList = new ArrayList<String>();
            //        mList.add("请选择班次与时间点");
            for (int l = 0; l < periodInfos.size(); l++) {
                String usedTime = compareTime(periodInfos.get(l).getPeriodShift().split(":"));
                if (usedTime != null) {
                    mList.add(periodInfos.get(l).getPeriodShift() + "  -  " + periodInfos.get(l).getPeriodTime());
                }
            }

            arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, mList) {

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
                    convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                    TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
                    label.setText(mList.get(position));
                    if (period.getSelectedItemPosition() == position) {
                        convertView.setBackgroundColor(getResources().getColor(
                                R.color.spinner_checked));
                        label.setTextColor(Color.parseColor("#36bbb1"));
                    } else {
                        convertView.setBackgroundColor(getResources().getColor(
                                R.color.spinner_unchecked));
                        label.setTextColor(Color.parseColor("#808080"));
                    }

                    return convertView;
                }
            };
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            period.setAdapter(arrayAdapter);
            period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String shiftTime = period.getSelectedItem().toString();
                    String[] time = shiftTime.split("  -  ");
                    checkTime = DBHelper.getInstance(getApplication()).queryPeriodId(time[0], time[1], sheetId);
                    Log.e("onSuccess", checkTime + "");
//                    Constants.isSeletecd.put(checkTime, false);
                    getPeriodData(checkTime);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void getPeriodData(final int periodId) {
        isOverWalk = false;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.REGION_DATA_STRING;

        RequestParams paramsRegion = new RequestParams();
        paramsRegion.put("index", "period");
        paramsRegion.put("period_id", periodId);
//        Boolean isSelected = false;
//
        regionInfos = DBHelper.getInstance(getApplication()).getRegionByPeriod(periodId);
        if (regionInfos.isEmpty()) {
//            for (int i = 0; i < 100; i++) {
//                if (Constants.tempId[i] == periodId) {
//                    isSelected = true;
//                    Log.e("onSuccess", "empty but true");
//                }
//            }
            Constants.isSelected.put(periodId, 0);
        } else {
//            isSelected = true;
            if (!Constants.isSelected.containsKey(periodId)) {
                Constants.isSelected.put(periodId, 1);
            }
        }
        //1表示已选择过而未完成
        if (Constants.isSelected.get(periodId) == 1) {
            if (status.equals("finish")) {
                if (regionInfos.get(0).getRegionType().equals("site")) {
                    Log.e("onSuccess", "已有数据1");
                    regionGridView.setVisibility(View.VISIBLE);
                    mMapView.setVisibility(View.GONE);
                    finish.setVisibility(View.VISIBLE);
                    qrcodeButton.setEnabled(true);
                    qrcodeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_button_sub));
                    regionInfos = DBHelper.getInstance(getApplication()).getRegionByPeriod(checkTime);
                    regionTaskAdapter = new RegionTaskAdapter(getApplicationContext(), regionInfos);
                    regionGridView.setAdapter(regionTaskAdapter);
                    if (Constants.checkTime == checkTime) {
                        regionTaskAdapter.updateChangeView(regionInfos);
                    }
                } else if (regionInfos.get(0).getRegionType().equals("route")) {
                    regionGridView.setVisibility(View.GONE);
                    mMapView.setVisibility(View.VISIBLE);
                    finish.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    qrcodeButton.setEnabled(true);
                    qrcodeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_button_sub));
//                    refresh();
                    initOverLay(regionInfos);
                    initMarkerClickEvent();
                }
            } else if (status.equals("change")) {
                Toast.makeText(getApplicationContext(), "此时间点未完成", Toast.LENGTH_SHORT).show();
                regionGridView.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
                mMapView.setVisibility(View.GONE);
            }
        } //2表示已选择并已完成
        else if (Constants.isSelected.get(periodId) == 2) {
//        if (isSelected == true) {
            if (status.equals("finish")) {
                Toast.makeText(getApplicationContext(), "此时间点已完成", Toast.LENGTH_SHORT).show();
                regionGridView.setVisibility(View.INVISIBLE);
                finish.setVisibility(View.INVISIBLE);
                qrcodeButton.setEnabled(false);
                qrcodeButton.setBackgroundColor(Color.parseColor("#e0e0e0"));
            } else if (status.equals("change")) {
                if (regionInfos.get(0).getRegionType().equals("site")) {
                    Log.e("onSuccess", "已有数据2");
                    mMapView.setVisibility(View.GONE);
                    regionGridView.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    regionInfos = DBHelper.getInstance(getApplication()).getRegionByPeriod(checkTime);
                    regionTaskAdapter = new RegionTaskAdapter(getApplicationContext(), regionInfos);
                    regionGridView.setAdapter(regionTaskAdapter);
                    if (Constants.checkTime == checkTime) {
                        regionTaskAdapter.updateChangeView(regionInfos);
                    }
                } else if (regionInfos.get(0).getRegionType().equals("route")) {
                    regionGridView.setVisibility(View.GONE);
                    mMapView.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
//                    refresh();
                    initOverLay(regionInfos);
//                    updateMapView(regionInfos);
                    initMarkerClickEvent();
                }
            }
//        } else if (isSelected == false) {
        } //0表示从未选择过，也未完成过
        else if (Constants.isSelected.get(periodId) == 0) {
            if (status.equals("finish")) {
                loading.setVisibility(View.VISIBLE);
                regionGridView.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
                qrcodeButton.setEnabled(true);
                qrcodeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_button_sub));
                asyncHttpClient.get(url, paramsRegion, new BaseJsonHttpResponseHandler<JSONArray>() {
                    @Override
                    public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                        Log.e("onSuccess", jsonArray + "");
                        try {
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(k);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
//                            String branch = jsonObject.getString("branch");
                                String intro = jsonObject.getString("intro");
                                String type = jsonObject.getString("type");
                                int sort = jsonObject.getInt("sort");
                                int ptrId = jsonObject.getInt("ptr_id");
                                getOneRegion(id, name, intro, type, sort, ptrId);
//                                RegionInfo regionInfo = new RegionInfo(id, checkTime, sheetId, name, intro, sort, type, ptrId, false);
//                                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
//                                dbHelper.insertRegionInfo(regionInfo);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("onSuccess", "RegionInfo插入成功");
                                    regionInfos = DBHelper.getInstance(getApplication()).getRegionByPeriod(checkTime);
                                    if (!regionInfos.isEmpty()) {
                                        if (regionInfos.get(0).getRegionType().equals("site")) {
                                            regionTaskAdapter = new RegionTaskAdapter(getApplicationContext(), regionInfos);
                                            regionGridView.setAdapter(regionTaskAdapter);
                                            if (Constants.checkTime == checkTime) {
                                                regionTaskAdapter.updateChangeView(regionInfos);
                                            }

                                            //表示这项已选过
//                            Constants.tempId[periodId] = periodId;
                                            Constants.isSelected.put(periodId, 1);
                                            regionGridView.setVisibility(View.VISIBLE);
                                            loading.setVisibility(View.GONE);
                                        } else if (regionInfos.get(0).getRegionType().equals("route")) {
                                            regionGridView.setVisibility(View.GONE);
                                            mMapView.setVisibility(View.VISIBLE);
                                            loading.setVisibility(View.GONE);
//                                        refresh();
                                            initOverLay(regionInfos);
                                            initMarkerClickEvent();
                                        }
                                    } else if (regionInfos.isEmpty()) {
                                        loading.setVisibility(View.GONE);
                                        Toast.makeText(RegionActivity.this, "该时间段无巡检内容", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, 1000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("onSuccess", "RegionInfo插入失败");
                        }


                    }

                    @Override
                    public void onFailure(int i, Header[] headers, Throwable throwable, String
                            s, JSONArray jsonArray) {
                        Log.e("onFailure", jsonArray + "");
                    }

                    @Override
                    protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                        JSONArray jsonArray = new JSONArray(s);
                        return jsonArray;
                    }
                });
            } else if (status.equals("change")) {
                Toast.makeText(getApplicationContext(), "此时间点未完成", Toast.LENGTH_SHORT).show();
                regionGridView.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
                mMapView.setVisibility(View.GONE);
            }
        }
    }

    private void getOneRegion(final int regionId, final String name, final String intro, final String type, final int sort, final int ptrId) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.REGION_DATA_STRING;

        RequestParams paramsRegion = new RequestParams();
        paramsRegion.put("index", "region");
        paramsRegion.put("region_id", regionId);

        asyncHttpClient.get(url, paramsRegion, new BaseJsonHttpResponseHandler<JSONObject>() {

            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONObject jsonObject) {
                Log.e("onSuccess", jsonObject + "");
                try {
                    String gps = jsonObject.getString("gps");
                    RegionInfo regionInfo = new RegionInfo(regionId, checkTime, sheetId, name, intro, sort, type, ptrId, false, gps);
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.insertRegionInfo(regionInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("onSuccess", "RegionInfo插入失败");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONObject jsonObject) {
                Log.e("onFailure", jsonObject + "");
            }

            @Override
            protected JSONObject parseResponse(String s, boolean b) throws Throwable {
                JSONObject jsonObject = new JSONObject(s);
                return jsonObject;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        for (int i = 0; i < regionInfos.size(); i++) {
//            if (regionInfos.get(i).getRegionSort() == position) {
        String key = String.valueOf(regionInfos.get(position).getSheetId()) + ";" + String.valueOf(regionInfos.get(position).getRegionId());
        Log.e("getkey", key);
        if (Constants.rtr.containsKey(key)) {
            Log.e("getkey", Constants.rtr.get(key));
            String[] ss = Constants.rtr.get(key).split(";");
            String recordId = ss[0];
            int generId = Integer.parseInt(ss[2]);
            RecordInfo recordInfo = DBHelper.getInstance(getApplicationContext()).getRecordById(Integer.parseInt(recordId));
            if (recordInfo.getIsChanged() == false) {
                Log.e("getkey", Constants.rtr.get(key));
                if (generId == Constants.peopleId) {
                    Intent intent = new Intent(RegionActivity.this, ReportActivity.class);
                    intent.putExtra("status", status);
                    intent.putExtra("record_id", recordId);
                    intent.putExtra("result", regionInfos.get(position).getRegionId());
                    intent.putExtra("sheet_id", sheetId);
                    Log.e("result", regionInfos.get(position).getRegionId() + "");
                    startActivityForResult(intent, RESULT_CODE_3);
                    recordInfo.setIsChanged(true);
                    DBHelper.getInstance(getApplicationContext()).updateRecordInfo(recordInfo);
                } else {
                    Toast.makeText(RegionActivity.this, "您无权限查看及修改", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegionActivity.this, "此报告已修改过或无权限查看", Toast.LENGTH_SHORT).show();
            }
        }
//            }
//        }
    }

    private void initOverLay(List<RegionInfo> regionInfos) {
        int i = 0;
        double latitude = 0.0000000000, longitude = 0.0000000000;
        int regionId = 0;
        Boolean status = false;
        while (i < regionInfos.size()) {
            status = regionInfos.get(i).getIsDone();
            regionId = regionInfos.get(i).getRegionId();

            String[] gps = regionInfos.get(i).getRegionGps().split(",");
            Log.d("gps", "gps1："+gps[0]+",gps2:"+gps[1]);
            longitude = Double.valueOf(gps[0].toString());
            latitude = Double.valueOf(gps[1].toString());
            BitmapDescriptor bd;
            if (status) {
                bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding_done);
                getRoute(regionId, true);
            } else {
                bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
                getRoute(regionId, false);
//                getRoute(7, false);
//                getRoute(10, false);
            }
            LatLng l = new LatLng(latitude, longitude);
            MarkerOptions option = new MarkerOptions().position(l).icon(bd)
                    .zIndex(1).draggable(true);
            mMarkerRegion = (Marker) (mBaiduMap.addOverlay(option));
            Bundle bundle = new Bundle();
            bundle.putSerializable("regionInfo", regionInfos.get(i));
            mMarkerRegion.setExtraInfo(bundle);

            i++;
        }
        String[] selfLocation = Constants.gpsInfo.split(",");
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_self_location);
        LatLng l = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
        MarkerOptions option = new MarkerOptions().position(l).icon(bd)
                .zIndex(0).draggable(true);
        mMarkerPeople = (Marker) (mBaiduMap.addOverlay(option));
        Bundle bundle = new Bundle();
        bundle.putSerializable("peopleInfo", DBHelper.getInstance(getApplicationContext()).getPeopleById(new Long(Constants.peopleId)));
        mMarkerPeople.setExtraInfo(bundle);
    }

    private void getRoute(int regionId, final boolean status) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.ROUTE_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("index", "region");
        params.put("region_id", regionId);
        final List<LatLng> pointsWalk = new ArrayList<LatLng>();
        asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<JSONArray>() {
            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                Log.e("onSuccess", jsonArray + "");
                Log.e("jsonArray", jsonArray.length() + "");
                try {
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        String gener = jsonObject.getString("gener");
                        String longitude = jsonObject.getString("longitude");
                        String latitude = jsonObject.getString("latitude");
                        String time = jsonObject.getString("time");
                        String region = jsonObject.getString("region");

                        LatLng points = new LatLng(Double.valueOf(latitude.toString()), Double.valueOf(longitude.toString()));
                        pointsWalk.add(points);
                        Log.e("pointsWalk", pointsWalk + "");
//                        mPolylineWalk.setPoints(pointsWalk);

                    }
                    Log.e("pointsWalk", pointsWalk.size() + "");

                    if (status) {
                        OverlayOptions mPolyline = new PolylineOptions().width(15)
                                .points(pointsWalk).dottedLine(true).customTexture(mGreenTexture);
                        mPolylineWalk = (Polyline) mBaiduMap.addOverlay(mPolyline);
                    } else {
                        OverlayOptions mPolyline = new PolylineOptions().width(15)
                                .points(pointsWalk).dottedLine(true).customTexture(mBlueTexture);
                        mPolylineWalk = (Polyline) mBaiduMap.addOverlay(mPolyline);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("onSuccess", "RegionInfo插入失败");
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String
                    s, JSONArray jsonArray) {
                Log.e("onFailure", jsonArray + "");
            }

            @Override
            protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                JSONArray jsonArray = new JSONArray(s);
                return jsonArray;
            }
        });
    }

    public void refresh() {
        final List<LatLng> pointsWalk = new ArrayList<LatLng>();
        final Handler handler = new Handler() {

            public void handleMessage(Message msg) {
                //                LatLng p1 = new LatLng(30.51 + x, 114.31 + x);
                String[] selfLocation = Constants.gpsInfo.split(",");
                LatLng p1 = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
                pointsWalk.add(p1);
//                    mPolylineWalk.setPoints(pointsWalk);
//                    mMarkerRegion.setPosition(p1);
            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 1000, 3000);


    }

//    private void updateMapView(List<RegionInfo> regionInfos) {
//        int i = 0;
//        int regionId = 0;
//        Boolean status = false;
//        while (i < regionInfos.size()) {
//            status = regionInfos.get(i).getIsDone();
//            regionId = regionInfos.get(i).getRegionId();
//
//            if (status) {
//                getRoute(regionId, true);
////                getRoute(10, true);
////            String[] gps = DBHelper.getInstance(getApplicationContext()).getOneRegionByRegion(periodId, regionId).getRegionGps().split(",");
//                String[] gps = regionInfos.get(i).getRegionGps().split(",");
//                BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding_done);
//                LatLng l = new LatLng(Double.valueOf(gps[1].toString()), Double.valueOf(gps[0].toString()));
//                MarkerOptions option = new MarkerOptions().position(l).icon(bd)
//                        .zIndex(9).draggable(true);
//                mMarkerRegion = (Marker) (mBaiduMap.addOverlay(option));
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("regionInfo", regionInfos.get(i));
//                mMarkerRegion.setExtraInfo(bundle);
//            }
//            i++;
//        }
//    }

    private void initMapClickEvent() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mMarkerInfoLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();

            }
        });
    }

    private void initMarkerClickEvent() {

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                mMarkerInfoLy.setVisibility(View.VISIBLE);
                // 根据商家信息为详细信息布局设置信息
                if (marker.getZIndex() == 0) {
                    PeopleInfo peopleInfo = (PeopleInfo) marker.getExtraInfo().get("peopleInfo");
                    popupPeopleInfo(mMarkerInfoLy, peopleInfo);
                } else if (marker.getZIndex() == 1) {
                    RegionInfo regionInfo = (RegionInfo) marker.getExtraInfo().get("regionInfo");
                    popupRegionInfo(mMarkerInfoLy, regionInfo);
                }
                return true;
            }
        });

    }

    protected void popupRegionInfo(RelativeLayout mMarkerLy, final RegionInfo regionInfo) {
        ViewHolder viewHolder = null;
        if (mMarkerLy.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.regionName = (TextView) mMarkerLy
                    .findViewById(R.id.regionName);
            viewHolder.regionIntro = (TextView) mMarkerLy
                    .findViewById(R.id.regionIntro);
            viewHolder.openRecord = (Button) mMarkerLy
                    .findViewById(R.id.record);
            viewHolder.line = mMarkerLy.findViewById(R.id.line);

            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) mMarkerLy.getTag();
        viewHolder.regionName.setText(regionInfo.getRegionName());
        viewHolder.regionIntro.setText(regionInfo.getRegionIntro());
        viewHolder.openRecord.setVisibility(View.GONE);
        viewHolder.line.setVisibility(View.GONE);
//        viewHolder.openRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (status.equals("finish")) {
//                    period.setEnabled(false);
//                    Intent intent = new Intent(RegionActivity.this, ReportActivity.class);
//                    intent.putExtra("status", status);
//                    intent.putExtra("check_time", checkTime);
//                    intent.putExtra("result", regionInfo.getRegionId());
//                    Log.e("result", regionInfo.getRegionId() + "");
//                    intent.putExtra("sheet_id", sheetId);
//                    startActivityForResult(intent, REQUEST_CODE);
//                } else if (status.equals("change")) {
//                    String key = String.valueOf(regionInfo.getSheetId()) + ";" + String.valueOf(regionInfo.getRegionId());
//                    if (Constants.rtr.containsKey(key)) {
//                        String[] ss = Constants.rtr.get(key).split(";");
//                        String recordId = ss[0];
//                        int generId = Integer.parseInt(ss[2]);
//                        RecordInfo recordInfo = DBHelper.getInstance(getApplicationContext()).getRecordById(Integer.parseInt(recordId));
//                        if (recordInfo.getIsChanged() == false) {
//                            if (generId == Constants.peopleId) {
//                                Intent intent = new Intent(RegionActivity.this, ReportActivity.class);
//                                intent.putExtra("status", status);
//                                intent.putExtra("record_id", recordId);
//                                intent.putExtra("result", regionInfo.getRegionId());
//                                intent.putExtra("sheet_id", sheetId);
//                                startActivityForResult(intent, RESULT_CODE_3);
//                                recordInfo.setIsChanged(true);
//                                DBHelper.getInstance(getApplicationContext()).updateRecordInfo(recordInfo);
//                            } else {
//                                Toast.makeText(RegionActivity.this, "您无权限查看及修改", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(RegionActivity.this, "此报告已修改过或无权限查看", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        });
    }

    protected void popupPeopleInfo(RelativeLayout mMarkerLy, final PeopleInfo peopleInfo) {
        ViewHolder viewHolder = null;
        if (mMarkerLy.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.regionName = (TextView) mMarkerLy
                    .findViewById(R.id.regionName);
            viewHolder.regionIntro = (TextView) mMarkerLy
                    .findViewById(R.id.regionIntro);
            viewHolder.openRecord = (Button) mMarkerLy
                    .findViewById(R.id.record);
            viewHolder.line = mMarkerLy.findViewById(R.id.line);
            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) mMarkerLy.getTag();
        viewHolder.regionName.setText(peopleInfo.getBranchName());
        viewHolder.regionIntro.setText(peopleInfo.getPeopleName());
        viewHolder.openRecord.setText("详细信息");
        viewHolder.line.setVisibility(View.VISIBLE);
        viewHolder.openRecord.setVisibility(View.VISIBLE);
        viewHolder.openRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegionActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ViewHolder {
        TextView regionName;
        TextView regionIntro;
        Button openRecord;
        View line;
    }

    private void isPolygonContainsPoint() {
//        new SpatialRelationUtil().isPolygonContainsPoint();


    }

    private void setRoute() {

        final Timer timer = new Timer();
        final List<LatLng> pointsWalk = new ArrayList<LatLng>();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isOverWalk) {
                    String[] selfLocation = Constants.gpsInfo.split(",");
                    LatLng p1 = new LatLng(Double.valueOf(selfLocation[1].toString()), Double.valueOf(selfLocation[0].toString()));
                    pointsWalk.add(p1);
                    if (pointsWalk.size() > 1 && pointsWalk.size() < 3) {
                        OverlayOptions mPolylineWalk = new PolylineOptions().width(20)
                                .color(0xAAB9B973).points(pointsWalk);
                        mPolyline = (Polyline) mBaiduMap.addOverlay(mPolylineWalk);
                    } else if (pointsWalk.size() > 3) {
                        mPolyline.setPoints(pointsWalk);
                        mMarkerPeople.setPosition(p1);
                    }
                    HistoryRoute historyRoute = DBHelper.getInstance(getApplicationContext()).getRouteBySP(sheetId, checkTime);
                    try {
                        JSONArray jsonArray;
                        if (historyRoute == null) {
                            jsonArray = new JSONArray();
                            historyRoute = new HistoryRoute();
                            historyRoute.setSheetId(sheetId);
                            historyRoute.setPeriodId(checkTime);
                            historyRoute.setGenerId(Constants.peopleId);
                            historyRoute.setStartTime(timestampToString(new Timestamp(System.currentTimeMillis())));
                        } else {
                            jsonArray = new JSONArray(historyRoute.getPoints());
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("latitude", selfLocation[1]);
                        jsonObject.put("longitude", selfLocation[0]);
                        jsonArray.put(jsonObject);
                        historyRoute.setPoints(jsonArray.toString());
                        Log.e("route", jsonArray.toString());
                        DBHelper.getInstance(getApplicationContext()).updateHistoryRoute(historyRoute);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    timer.cancel();
                }
//                mPolyline.remove();

            }
        }, 1000, 5000);

    }

    private String timestampToString(Timestamp ts) {
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

    private String compareTime(String[] time) {
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        String[] currentTime = sdf.format(new Timestamp(System.currentTimeMillis())).split(":");
        /*if (Integer.parseInt(currentTime[0]) > Integer.parseInt(time[0])) {
            return time[0] + ":" + time[1];
        } else {
            if (Integer.parseInt(currentTime[1]) >= Integer.parseInt(time[1])) {
                return time[0] + ":" + time[1];
            } else {
                return null;
            }
        }*/
        if ((Integer.parseInt(currentTime[0]) - 2) < Integer.parseInt(time[0])) {
            return time[0] + ":" + time[1];
        } else {
            return null;
        }

    }
}
