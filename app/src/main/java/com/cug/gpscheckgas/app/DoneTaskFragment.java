package com.cug.gpscheckgas.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.DoneListAdapter;
import com.cug.greendao.SheetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class DoneTaskFragment extends Fragment {

    private static DoneListAdapter doneListAdapter = null;
    private List<SheetInfo> doneTaskInfo = null;
    private ListView doneListView = null;
    private Context mContext;
    View doneTaskView;
    private Boolean isdone = false;
    private TextView noTask;
    private final static int REQUEST_CODE = 2;
    private final static int RESULT_CODE = 2;
//    private RefreshableView refreshableView;

    @Override
    public void onStart() {
        Log.e("onStart", "done");
        super.onStart();
        doneTaskInfo = DBHelper.getInstance(getContext()).getSheetInfoByBId(Constants.branchId);
        for (int count = 0; count < doneTaskInfo.size(); count++) {
            int doneTime = DBHelper.getInstance(getContext()).getDoneTimeById(doneTaskInfo.get(count).getSheetId());
            if (doneTime > 0) {
//                isdone =doneListAdapter.updateView(doneTaskInfo.get(count).getSheetId());
                isdone = doneListAdapter.updateView(doneTaskInfo);
                break;
            } else {
                isdone = false;
            }
        }
        doneListView.setAdapter(doneListAdapter);
        if (isdone == false) {
            noTask = (TextView) doneTaskView.findViewById(R.id.noDoneTask);
//            refreshableView.setVisibility(View.GONE);
            doneListView.setVisibility(View.GONE);
            noTask.setVisibility(View.VISIBLE);
        } else if (isdone == true) {
            noTask = (TextView) doneTaskView.findViewById(R.id.noDoneTask);
//            refreshableView.setVisibility(View.VISIBLE);
            doneListView.setVisibility(View.VISIBLE);
            noTask.setVisibility(View.GONE);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("onCreate", "done");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "done");
//        addDataToDb();
        doneTaskView = inflater.inflate(R.layout.fragment_done_task, container, false);
        doneListView = (ListView) doneTaskView.findViewById(R.id.donelist);

        doneTaskInfo = DBHelper.getInstance(getContext()).getSheetInfoByBId(Constants.branchId);
        doneListAdapter = new DoneListAdapter(this.getContext(), doneTaskInfo);

        doneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int sheetId = doneTaskInfo.get(position).getSheetId();
                String sheetName = doneTaskInfo.get(position).getSheetName();
                Intent intent = new Intent();
                intent.setClass(getContext(), RegionActivity.class);
                intent.putExtra("sheetId", sheetId);
                intent.putExtra("status", "change");
                intent.putExtra("sheetName", sheetName);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return doneTaskView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            int id = data.getExtras().getInt("sheet_id");
            Log.e("done", id + "");
        }
    }
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
    private void addDataToDb() {
        SheetInfo undoTaskInfos1 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos2 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos3 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos4 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos5 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos6 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos7 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);
        SheetInfo undoTaskInfos8 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", 1, false, null, null, false, false);

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        dbHelper.insertSheetInfo(undoTaskInfos1);
        dbHelper.insertSheetInfo(undoTaskInfos2);
        dbHelper.insertSheetInfo(undoTaskInfos3);
        dbHelper.insertSheetInfo(undoTaskInfos4);
        dbHelper.insertSheetInfo(undoTaskInfos5);
        dbHelper.insertSheetInfo(undoTaskInfos6);
        dbHelper.insertSheetInfo(undoTaskInfos7);
        dbHelper.insertSheetInfo(undoTaskInfos8);
    }

}
