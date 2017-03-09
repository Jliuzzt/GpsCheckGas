package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.service.LongRunningService;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.Location;
import com.cug.gpscheckgas.util.LocationUtil;
import com.cug.greendao.PeopleInfo;
import com.cug.greendao.SheetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends /*AppCompatActivity*/ Activity implements View.OnClickListener {


    private DBHelper dbHelper;
    private List<SheetInfo> sheetInfos = null;
    private Button loginButton;
    private EditText /*username, */password;
    private AutoCompleteTextView username;
    private Button clearUser, clearPass;
    private ProgressBar loading;
    private LoginActivity mContext;
    private Location mLocation;
    private LocationUtil locationUtil;

    int akBtnId = 0;
    int initBtnId = 0;
    int richBtnId = 0;
    int setTagBtnId = 0;
    int delTagBtnId = 0;
    int clearLogBtnId = 0;
    int showTagBtnId = 0;
    int unbindBtnId = 0;
    int setunDisturbBtnId = 0;

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil = new LocationUtil(mContext, mLocation);
        locationUtil.registerListener();
        locationUtil.startService();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mContext = this;

        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        clearUser = (Button) findViewById(R.id.clearUser);
        clearPass = (Button) findViewById(R.id.clearPass);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        loading = (ProgressBar) findViewById(R.id.loading);


//        username.setOnFocusChangeListener(this);
//        password.setOnFocusChangeListener(this);
        username.addTextChangedListener(userWatcher);
        password.addTextChangedListener(passWatcher);
        clearUser.setOnClickListener(this);
        clearPass.setOnClickListener(this);
        dbHelper = DBHelper.getInstance(this);
//        addDataToDb();

        Resources resource = this.getResources();
        String pkgName = this.getPackageName();

//        setContentView(resource.getIdentifier("main", "layout", pkgName));
        akBtnId = resource.getIdentifier("btn_initAK", "id", pkgName);
        initBtnId = resource.getIdentifier("btn_init", "id", pkgName);
        richBtnId = resource.getIdentifier("btn_rich", "id", pkgName);
        setTagBtnId = resource.getIdentifier("btn_setTags", "id", pkgName);
        delTagBtnId = resource.getIdentifier("btn_delTags", "id", pkgName);
        clearLogBtnId = resource.getIdentifier("btn_clear_log", "id", pkgName);
        showTagBtnId = resource.getIdentifier("btn_showTags", "id", pkgName);
        unbindBtnId = resource.getIdentifier("btn_unbindTags", "id", pkgName);
        setunDisturbBtnId = resource.getIdentifier("btn_setunDisturb", "id",
                pkgName);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "Gnqgc6nhdMQ0qz7itHQ9tBP4");
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                resource.getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        cBuilder.setNotificationSound(Uri.withAppendedPath(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        // 推送高级设置，通知栏样式设置为下面的ID
        PushManager.setNotificationBuilder(this, 1, cBuilder);

        Intent intent = new Intent(this, LongRunningService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);


        initAutoComplete(username);
    }


    @Override
    public void onClick(View v) {
        Log.e("username", username.getText().toString());
        Log.e("password", password.getText().toString());
        switch (v.getId()) {
            case R.id.login:
                new AlertDialog.Builder(this).setTitle("打卡上班吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("打卡上班", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.setVisibility(View.VISIBLE);
                                userLogin(false);
                            }
                        })
                        .setNegativeButton("今日已打卡", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.setVisibility(View.VISIBLE);
                                userLogin(true);
//                                locationUtil.unregisterListener();
//                                locationUtil.stopService();
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
                            }
                        }).show();
                break;
            case R.id.clearUser:
                username.setText("");
                initAutoComplete(username);
                username.showDropDown();
                break;
            case R.id.clearPass:
                password.setText("");
                break;
        }
    }

    private void initAutoComplete(AutoCompleteTextView auto) {
        List<PeopleInfo> peopleInfos = DBHelper.getInstance(mContext).getAllPeopleInfo();
        if (peopleInfos != null) {
            String[] hisArrays = new String[peopleInfos.size()];
            for (int i = 0; i < peopleInfos.size(); i++) {
                hisArrays[i] = peopleInfos.get(i).getPeopleName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, hisArrays);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            if (hisArrays.length > 0) {
                auto.setAdapter(adapter);
                auto.setDropDownHeight(350);
                auto.setThreshold(1);
                auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        AutoCompleteTextView view = (AutoCompleteTextView) v;
                        if (hasFocus) {
                            view.showDropDown();
                        }
                    }
                });
            }
        }
    }

    public void userLogin(final boolean isLogin) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.LOGIN_DATA_STRING;

        RequestParams paramsLogin = new RequestParams();
        paramsLogin.put("username", username.getText().toString());
        paramsLogin.put("password", password.getText().toString());

        if (isLogin) {
            PeopleInfo peopleInfo = DBHelper.getInstance(getApplicationContext()).getPeopleByNP(username.getText().toString(), password.getText().toString());
            if (peopleInfo == null) {
                Toast.makeText(LoginActivity.this, "未打卡或输入错误", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            } else {
                if (peopleInfo.getIsLogin()) {
                    Constants.peopleId = peopleInfo.getId().intValue();
                    Constants.teamName = peopleInfo.getTeamName();
                    Constants.branchId = peopleInfo.getBranchId();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    postRoute();
                    locationUtil.unregisterListener();
                    locationUtil.stopService();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "今日未打卡，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            asyncHttpClient.get(url, paramsLogin, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int i, Header[] headers, String s, JSONObject jsonObject) {
                    Log.e("onSuccess", jsonObject + "");
                    try {
                        int peopleId = jsonObject.getInt("peopleid");
//                        int peopleId = 1;
//                    Log.e("11",peopleId+"");
                        String userName = jsonObject.getString("username");
                        Log.e("11", userName + "");
                        String name = jsonObject.getString("name");
                        int teamId = jsonObject.getInt("team_id");
                        String password = jsonObject.getString("password");
                        int branchId = jsonObject.getInt("branch_id");
                        Log.e("11", branchId + "");
                        String branchName = jsonObject.getString("branchname");
                        String branchType = jsonObject.getString("branchtype");
                        String company = jsonObject.getString("comname");
                        String teamName = jsonObject.getString("tean_name");
                        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                        //---------------在每日打卡登录时加入清除数据代码-------------
                        dbHelper.initDataClear();
                        dbHelper.deleteAllData();
                        Constants.clearQuestion();
                        //----------------END-------------------------------------

                        if (dbHelper.getAllPeopleInfo().isEmpty()) {
                            PeopleInfo peopleInfo = new PeopleInfo((long) peopleId, userName, name, teamId, password, branchId, branchName, branchType, company, teamName, true);
                            dbHelper.insertPeopleInfo(peopleInfo);
                            Log.e("login", "插入成功");
                        } else if (!dbHelper.getAllPeopleInfo().isEmpty() && dbHelper.getPeopleById((long) peopleId) == null) {
                            PeopleInfo peopleInfo = new PeopleInfo((long) peopleId, userName, name, teamId, password, branchId, branchName, branchType, company, teamName, true);
                            dbHelper.insertPeopleInfo(peopleInfo);
                            Log.e("login", "插入成功2");
                        } else {
                            if (dbHelper.getPeopleById((long) peopleId).getIsLogin()) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            } else {
                                PeopleInfo peopleInfo = dbHelper.getPeopleById((long) peopleId);
                                peopleInfo.setIsLogin(true);
                                dbHelper.updatePeopleInfo(peopleInfo);
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(LoginActivity.this, "今日未打卡,请先登录", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Constants.peopleId = peopleId;
                        Constants.teamName = teamName;
                        Constants.branchId = branchId;
                        postRoute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getAllSheet();
                }

                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONObject jsonObject) {
                    Log.e("onFailure", throwable + "");
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                }

                @Override
                protected JSONObject parseResponse(String s, boolean b) throws Throwable {
                    JSONObject jsonObject = new JSONObject(s);
                    return jsonObject;
                }
            });
        }
    }

    private void getAllSheet() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.SHEET_DATA_STRING;

        RequestParams paramsSheet = new RequestParams();
        paramsSheet.put("index", "branch");
        paramsSheet.put("branch_id", Constants.branchId);
        Log.e("branchID", Constants.branchId + "");
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        sheetInfos = dbHelper.getSheetInfoByBId(Constants.branchId);
        if (sheetInfos == null) {
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
                    Toast.makeText(LoginActivity.this, "获取巡检表失败", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                }

                @Override
                protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                    JSONArray jsonArray = new JSONArray(s);
                    return jsonArray;
                }
            });
        } else {
            Log.e("onSuccess", "本地数据");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    locationUtil.unregisterListener();
                    locationUtil.stopService();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }

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
                Log.e("onSuccess", jsonObject + "");
                try {
                    String sheetIntro = jsonObject.getString("intro");
                    SheetInfo sheetInfo = new SheetInfo((long) sheetId, sheetId, name, sheetIntro, Constants.branchId, false, 0, -1, false, false);
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.insertSheetInfo(sheetInfo);
                    Log.e("onSuccess", "插入成功");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            locationUtil.unregisterListener();
                            locationUtil.stopService();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONObject jsonObject) {
                Log.e("onFailure", jsonObject + "");
                Toast.makeText(LoginActivity.this, "获取巡检表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JSONObject parseResponse(String s, boolean b) throws Throwable {
                JSONObject jsonObject = new JSONObject(s);
                return jsonObject;
            }
        });

    }

    private void addDataToDb() {

        SheetInfo undoTaskInfos1 = new SheetInfo(null, 1, "巡检表一", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos2 = new SheetInfo(null, 2, "巡检表二", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos3 = new SheetInfo(null, 3, "巡检表三", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos4 = new SheetInfo(null, 4, "巡检表四", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos5 = new SheetInfo(null, 5, "巡检表五", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos6 = new SheetInfo(null, 6, "巡检表六", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos7 = new SheetInfo(null, 7, "巡检表七", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        SheetInfo undoTaskInfos8 = new SheetInfo(null, 8, "巡检表八", "巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容巡检内容", -1, false, 0, 1, false, false);
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.insertSheetInfo(undoTaskInfos1);
        dbHelper.insertSheetInfo(undoTaskInfos2);
        dbHelper.insertSheetInfo(undoTaskInfos3);
        dbHelper.insertSheetInfo(undoTaskInfos4);
        dbHelper.insertSheetInfo(undoTaskInfos5);
        dbHelper.insertSheetInfo(undoTaskInfos6);
        dbHelper.insertSheetInfo(undoTaskInfos7);
        dbHelper.insertSheetInfo(undoTaskInfos8);
    }

    private TextWatcher userWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            clearUser.setVisibility(View.VISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            clearUser.setVisibility(View.GONE);
            clearPass.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            clearUser.setVisibility(View.VISIBLE);
            clearPass.setVisibility(View.GONE);
        }
    };

    private TextWatcher passWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            clearPass.setVisibility(View.VISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            clearUser.setVisibility(View.GONE);
            clearPass.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            clearUser.setVisibility(View.GONE);
            clearPass.setVisibility(View.VISIBLE);
        }
    };

    private void postRoute() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.POST_ROUTE_DATA_STRING;

        String[] gps = Constants.gpsInfo.split(",");

        RequestParams params = new RequestParams();
        Log.e("peopleid", Constants.peopleId + "");
        params.put("gener_id", Constants.peopleId);
        params.put("longitude", gps[0]);
        params.put("latitude", gps[1]);
        params.put("islogin", "1");
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
