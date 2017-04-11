package com.cug.gpscheckgas.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.RefreshableView;
import com.cug.gpscheckgas.util.UndoListAdapter;
import com.cug.greendao.SheetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UndoTaskFragment extends Fragment {
    private UndoListAdapter undoListAdapter = null;
    private List<SheetInfo> undoTaskInfo = null;
    private List<SheetInfo> sheetInfos = null;
    private ListView undoListView = null;
    private Context mContext;
    private View undoTaskView;

    private final static int REQUEST_CODE = 1;
    private final static int RESULT_CODE = 1;
    private int i = 0;
    private RefreshableView refreshableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "undo");
//        addDataToDb1();
        undoTaskView = inflater.inflate(R.layout.fragment_undo_task, container, false);
        refreshableView = (RefreshableView) undoTaskView.findViewById(R.id.refreshable_view);
        undoListView = (ListView) undoTaskView.findViewById(R.id.undolist);

//        undoTaskInfo = DBHelper.getInstance(getContext()).getAllSheetInfo();

        //后面要把branchId传进来

//        Constants.branchId = DBHelper.getInstance(getContext()).getBranchById(Constants.peopleId);
        undoTaskInfo = DBHelper.getInstance(getContext()).getSheetInfoByBId(Constants.branchId);
        undoListAdapter = new UndoListAdapter(this.getContext(), undoTaskInfo);
        undoListAdapter.updateRestTime(undoTaskInfo);
//        undoListAdapter.updateRestTime(Constants.sid, Constants.length, Constants.doneTime2);
//        for (int count = 0; count < undoTaskInfo.size(); count++) {
//            int doneTime = DBHelper.getInstance(getContext()).getDoneTimeById(undoTaskInfo.get(count).getSheetId());
//            int length = DBHelper.getInstance(getContext()).getLengthById(undoTaskInfo.get(count).getSheetId());
//            if (doneTime == length) {
//                undoListAdapter.updateView(undoTaskInfo.get(count).getSheetId());
//                Log.e("isAll","表一已完成");
//            }
//        }
        undoListView.setAdapter(undoListAdapter);

//        undoListView.setEnabled(false);
        undoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取当前选中行
                int sheetId = undoTaskInfo.get(position).getSheetId();
                String sheetName = undoTaskInfo.get(position).getSheetName();
                // 将点击行下标传到RegionActivity
                Intent intent = new Intent();
                intent.setClass(getContext(), RegionActivity.class);
//                intent.putExtra("intro",regionIntro);
                intent.putExtra("sheetName", sheetName);
                intent.putExtra("sheetId", sheetId);
                intent.putExtra("status", "finish");
//                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE);
//                getRegionIntro(sheetId);
            }
        });

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh()  {
                //onRefresh方法中进行耗时操作，比如向服务器请求最新数据等
//                    updateSheetInfo();
                Message msg = mHandler.obtainMessage();
                msg.arg1 = 0;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0);
        return undoTaskView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            int sheetId = data.getExtras().getInt("sheet_id");
//            Constants.length = data.getExtras().getInt("length");
//            Constants.doneTime2 = data.getExtras().getInt("doneTime");
//            int doneTime = DBHelper.getInstance(getContext()).getDoneTimeById(sheetId);
//            int length = DBHelper.getInstance(getContext()).getLengthById(sheetId);
            undoTaskInfo = DBHelper.getInstance(getContext()).getSheetInfoByBId(Constants.branchId);
            undoListAdapter.updateRestTime(undoTaskInfo);
//            undoListAdapter.notifyDataSetChanged();
//            if (doneTime == length) {
//                Log.e("sheetid", sheetId + "");
//                undoListAdapter.updateView(sheetId);
//            }
//            undoListAdapter.notifyDataSetChanged();
//            doneListAdapter.updataView(id);
//            doneListAdapter.notifyDataSetChanged();
//            Constants.sheetId = Constants.sid;
        }
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {

                DBHelper.getInstance(getContext()).deleteAllData();
                DBHelper.getInstance(getContext()).initDataClear();
                Constants.clearQuestion();
                getAllSheet();
//                updateSheetInfo();
//                try {
//                    Thread.sleep(3000);
//                    undoTaskInfo = DBHelper.getInstance(getContext()).getSheetInfoByBId(Constants.branchId);
//                    undoListAdapter.updateRestTime(undoTaskInfo);
                    refreshableView.finishRefreshing();
                    undoListView.postInvalidate();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    };

    private void updateSheetInfo() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.SHEET_DATA_STRING;

        RequestParams paramsSheet = new RequestParams();
        paramsSheet.put("index", "branch");
        paramsSheet.put("branch_id", Constants.branchId);
        asyncHttpClient.get(url, paramsSheet, new BaseJsonHttpResponseHandler<JSONArray>() {
            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                Log.e("onSuccess", jsonArray + "");
                try {
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        getSheetIntro(id, name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONArray jsonArray) {
                Log.e("onFailure", jsonArray + "");
                Toast.makeText(mContext, "刷新巡检表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                JSONArray jsonArray = new JSONArray(s);
                return jsonArray;
            }
        });

    }

    private void getAllSheet(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.SHEET_DATA_STRING;

        RequestParams paramsSheet = new RequestParams();
        paramsSheet.put("index", "branch");
        paramsSheet.put("branch_id", Constants.branchId);
        Log.e("branchID", Constants.branchId + "");


            asyncHttpClient.get(url, paramsSheet, new BaseJsonHttpResponseHandler<JSONArray>() {
                @Override
                public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                    Log.e("onSuccess", jsonArray + "");
                    try {
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(k);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            getSheetIntro(id, name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONArray jsonArray) {
                    Log.e("onFailure", jsonArray + "");
                    Toast.makeText(mContext, "刷新巡检表失败", Toast.LENGTH_SHORT).show();

                }

                @Override
                protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                    JSONArray jsonArray = new JSONArray(s);
                    return jsonArray;
                }
            });

    }

    private void getSheetIntro(final int sheetId, final String name) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.SHEET_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("index", "sheet");
        params.put("sheet_id", sheetId);
        asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONObject jsonObject) {
                try {
                    String sheetIntro = jsonObject.getString("intro");
                    DBHelper dbHelper = DBHelper.getInstance(mContext);
                    SheetInfo sheetInfo = dbHelper.getSheetInfoById(sheetId);
                    if (sheetInfo == null) {
                        sheetInfo = new SheetInfo((long) sheetId, sheetId, name, sheetIntro, Constants.branchId, false, 0, -1, false, false);
                        dbHelper.insertSheetInfo(sheetInfo);
                    }
                    Log.e("onSuccess", "插入成功");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONObject jsonObject) {
                Log.e("onFailure", jsonObject + "");
                Toast.makeText(mContext, "刷新巡检表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JSONObject parseResponse(String s, boolean b) throws Throwable {
                JSONObject jsonObject = new JSONObject(s);
                return jsonObject;
            }
        });

    }

}
