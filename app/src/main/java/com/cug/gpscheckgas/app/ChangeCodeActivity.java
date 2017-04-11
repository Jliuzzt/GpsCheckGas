package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cug.gpscheckgas.R;

public class ChangeCodeActivity extends Activity/*AppCompatActivity*/ implements View.OnClickListener {

    private ImageButton backButton;
    private Button comfirmChange;

//    int i=0;
//    private ProgressBar progressBar=null;
//    private Button downLoadBtn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_code);

        backButton = (ImageButton)findViewById(R.id.back);
        backButton.setOnClickListener(this);
        comfirmChange = (Button) findViewById(R.id.comfirm);
        comfirmChange.setOnClickListener(this);

//        progressBar=(ProgressBar) findViewById(R.id.progressBar);
//        downLoadBtn=(Button) findViewById(R.id.downLoadBtn);
//        downLoadBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                i = 0;
//                handler.sendEmptyMessage(new Message().what = 1);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                intent = new Intent(this, PersonActivity.class);
                this.setResult(1, intent);
                this.finish();
                break;
            case R.id.comfirm:
                Toast.makeText(this,"已成功修改登录密码",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, PersonActivity.class);
                this.setResult(1, intent);
                this.finish();
                break;
        }
    }


//    Handler handler=new Handler(){
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    i+=20;
//                    progressBar.setProgress(i);
//                    if(i!=100){
//                        handler.sendEmptyMessageDelayed(new Message().what=1,500);
////                        downLoadBtn.setText(i+"%");
//                    }else if(i==100){
//                        downLoadBtn.setText("下载完成");
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        };
//    };
}
