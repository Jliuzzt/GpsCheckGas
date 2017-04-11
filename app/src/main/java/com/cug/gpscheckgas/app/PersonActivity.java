package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.greendao.PeopleInfo;

public class PersonActivity extends Activity/*AppCompatActivity*/ implements View.OnClickListener {

    private ImageButton backButton;
    private Button close;
    private TextView userName;
    private TextView name;
    private TextView branchName;
    private TextView branchType;
    private TextView company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person);

        backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(this);

        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(this);
        userName = (TextView) findViewById(R.id.userName);
        name = (TextView) findViewById(R.id.name);
        branchName = (TextView) findViewById(R.id.branchName);
        branchType = (TextView) findViewById(R.id.branchType);
        company = (TextView) findViewById(R.id.company);
        getPersonData();
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
            case R.id.close:
                intent = new Intent(this, MainActivity.class);
                this.setResult(1, intent);
                this.finish();
                break;
        }
    }

    public void getPersonData() {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        PeopleInfo peopleInfo = dbHelper.getPeopleById(Constants.peopleId);
        if (peopleInfo != null){
            name.setText(peopleInfo.getName());
            userName.setText(peopleInfo.getPeopleName());
            branchName.setText(peopleInfo.getBranchName());
            branchType.setText(peopleInfo.getBranchType());
            company.setText(peopleInfo.getCompany());
        }
        else{
            Toast.makeText(PersonActivity.this, "暂无信息，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
}
