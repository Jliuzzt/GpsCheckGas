package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.RecordListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;

public class RecordActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RecordListAdapter recordListAdapter = null;
    private ImageButton back;
    private ListView recordList;
    private JSONArray recordArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
        recordList = (ListView) findViewById(R.id.recordList);
        getAllRecord();
        recordList.setOnItemClickListener(this);
    }

    public void getAllRecord() {
        Timestamp ts1 = new Timestamp(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000);
        Timestamp ts2 = new Timestamp(System.currentTimeMillis());

        String recordStart = timestampToString(ts1);
        String recordEnd = timestampToString(ts2);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.RECORD_DATA_STRING;

        String generid = String.format("%04d",Constants.peopleId);

        RequestParams params = new RequestParams();
        params.put("index", "gener");
        params.put("gener_id", generid);
        params.put("start", recordStart);
        params.put("end", recordEnd);

        Log.e("getAllRecord:当前巡检员ID ",""+generid+recordStart+"-------"+recordEnd);

        asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<JSONArray>() {


            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                Log.e("onSuccess", jsonArray + "");
                recordArray = jsonArray;
                recordListAdapter = new RecordListAdapter(getApplicationContext(), jsonArray);
                recordList.setAdapter(recordListAdapter);
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONArray jsonArray) {
                Log.e("onFailure", jsonArray + "");
                Toast.makeText(RecordActivity.this, "巡检记录获取失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                Log.e("parseResponse: ------",s);
                JSONArray jsonArray = new JSONArray(s);
                return jsonArray;
            }
        });

    }

    public static String timestampToString(Timestamp ts) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (recordArray != null) {
            Intent intent = new Intent(this, RecordDetailActivity.class);
            try {
                JSONObject jsonObject = recordArray.getJSONObject(position);
                Log.e("onItemClick:取到的一行巡检记录 ",String.valueOf(jsonObject));
                intent.putExtra("all_record", String.valueOf(jsonObject));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RecordActivity.this, "巡检记录获取失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RecordActivity.this, "无巡检记录", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                intent = new Intent(this, MainActivity.class);
                this.setResult(1, intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
