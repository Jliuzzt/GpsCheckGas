package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.cug.gpscheckgas.R;

import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends Activity/*AppCompatActivity*/ {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first);

        final Intent it = new Intent(this, LoginActivity.class); //你要转向的Activity
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(it); //执行
                FirstActivity.this.finish();
            }
        };
        timer.schedule(task, 1000 * 2); //10秒后
    }

}
