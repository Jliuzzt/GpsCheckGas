package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.RecordDetailAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordDetailActivity extends Activity implements View.OnClickListener {
    private ImageButton back;
    private ListView detailList;
    private RecordDetailAdapter recordDetailAdapter = null;
    private JSONObject jsonObject = null;

    private TextView recordRegionName;
    private TextView recordShift;
    private TextView recordTime;
    private TextView recordError;
    private TextView recordStart;
    private TextView recordEnd;
    private TextView recordSub;
    private TextView recordCheckTime;
    private TextView recordStatus;
//    private TextView recordComment;
    private TextView recordGener;
    private TextView recordType;
    private String region = "", periodTime = "", periodShift = "", submit = "", error = "", status = "", comment = "", gener = "", start = "", branch = "", type = "", checkTime = "", end = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record_detail);

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);

        recordRegionName = (TextView) findViewById(R.id.regionName);
        recordShift = (TextView) findViewById(R.id.shift);
        recordTime = (TextView) findViewById(R.id.time);
        recordError = (TextView) findViewById(R.id.error);
        recordStart = (TextView) findViewById(R.id.start);
        recordEnd = (TextView) findViewById(R.id.end);
        recordSub = (TextView) findViewById(R.id.submit);
        recordCheckTime = (TextView) findViewById(R.id.checkTime);
        recordStatus = (TextView) findViewById(R.id.status);
//        recordComment = (TextView) findViewById(R.id.comment);
        recordGener = (TextView) findViewById(R.id.gener);
        recordType = (TextView) findViewById(R.id.type);

        try {
            jsonObject = new JSONObject(this.getIntent().getStringExtra("all_record"));
            region = jsonObject.getString("region");
            periodTime = jsonObject.getString("period_time");
            periodShift = jsonObject.getString("period_shift");
            submit = jsonObject.getString("submit");
            error = jsonObject.getString("error");
            status = jsonObject.getString("status");
//            comment = jsonObject.getString("comment");
            gener = jsonObject.getString("gener");
            start = jsonObject.getString("start");
            branch = jsonObject.getString("branch");
            type = jsonObject.getString("type");
            checkTime = jsonObject.getString("check_time");
            end = jsonObject.getString("end");
            if (jsonObject.length() > 0) {
                recordRegionName.setText(region);
                recordShift.setText(periodShift);
                recordTime.setText(periodTime);
                recordError.setText(error);
                recordStart.setText(start);
                recordEnd.setText(end);
                recordSub.setText(submit);
                if (checkTime.equals("")) {
                    recordCheckTime.setText("无");
                } else {
                    recordCheckTime.setText(checkTime);
                }
                if (status.equals("0")) {
                    recordStatus.setText("未审核");
//                    recordComment.setText("无");
                } else if (status.equals("1")) {
                    recordStatus.setText("已审核");
//                    recordComment.setText(comment);
                }
                recordGener.setText(gener);
                if (type.equals("site")) {
                    recordType.setText("站场");
                } else if (type.equals("route")) {
                    recordType.setText("管线");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "巡检记录获取失败，请稍后再试", Toast.LENGTH_SHORT).show();
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
