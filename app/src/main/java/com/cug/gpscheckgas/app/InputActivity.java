package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class InputActivity extends Activity/*AppCompatActivity*/ implements View.OnClickListener {

    private TextView name;
    private TextView intro;
    private Button comfirm;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);

        name = (TextView) findViewById(R.id.regionName);
        intro = (TextView) findViewById(R.id.regionIntro);
        comfirm = (Button) findViewById(R.id.comfirm);
        back = (ImageButton) findViewById(R.id.back);
        comfirm.setOnClickListener(this);
        back.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                intent = new Intent();
                this.setResult(3, intent);
                this.finish();
                break;
            case R.id.comfirm:
                uploadNewRegion();
                break;
        }
    }

    public void uploadNewRegion(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.ADDREGION_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("name", name.getText().toString());
        params.put("intro", intro.getText().toString());
        params.put("branch_id", Constants.branchId);
        params.put("gps", Constants.gpsInfo);
        params.put("type", "temp");
        params.put("range", "0");
        params.put("gener_id", Constants.peopleId);

        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("onSuccess", new String(bytes));
                String regionId = new String(bytes);
                Intent intent = new Intent();
                intent.putExtra("region_id",Integer.parseInt(regionId));
                setResult(3, intent);
                finish();
                Toast.makeText(InputActivity.this, "录入成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("onFailure", new String(bytes));
                Log.e("onFailure", throwable+"");
                Toast.makeText(InputActivity.this, "录入失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
