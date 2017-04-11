package com.cug.gpscheckgas.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.ReportListAdapter;
import com.cug.greendao.RecordInfo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ReportActivity extends /*AppCompatActivity*/ Activity implements View.OnClickListener {

    private ReportActivity activity;
    private ReportListAdapter reportListAdapter = null;
    private List<RecordInfo> recordInfos = null;
    private ListView reportList = null;
    private EditText note;
    private ImageButton gps, camera, video, back, closePic/*, open*/;
    private Button finish, submit;
    private ImageView imageView;
    private RelativeLayout picView;

    private int regionId;
    private int checkTime;
    private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> editData = new ArrayList<Map<String, String>>();
    private String recordPic = "无图片";
    private String recordVideo = "无视频";
//    private int resultLen;

    private String recordNote;
    private static final String LOG_TAG = "HelloCamera";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private String status = null;
    private String asws;
    private String aswsnote;
    private String recordId = null;
    private int sheetId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_report);
        activity = this;
        if (Constants.recordStart == null) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Constants.recordStart = timestampToString(ts);
        }

//        Constants.title = new ArrayList<String>();
//        Constants.possasws = new ArrayList<String>();
//        Constants.normalasws = new ArrayList<String>();

        Log.d("xxx","ReportActivity.....");
        sheetId = this.getIntent().getIntExtra("sheet_id", -1);
        regionId = this.getIntent().getIntExtra("result", -2);
        Log.e("数据库中的recordId", regionId + "");
        checkTime = this.getIntent().getIntExtra("check_time", -2);
        //两个activity共有参数
        status = this.getIntent().getStringExtra("status");
//        asws = this.getIntent().getStringExtra("asws");
//        aswsnote = this.getIntent().getStringExtra("note");
        recordId = this.getIntent().getStringExtra("record_id");

        reportList = (ListView) findViewById(R.id.questionList);
        note = (EditText) findViewById(R.id.note);
        finish = (Button) findViewById(R.id.finish_report);
        submit = (Button) findViewById(R.id.submit_report);
        if (status.equals("finish")) {
            finish.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
            finish.setOnClickListener(this);
        } else if (status.equals("change")) {
            DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
            RecordInfo recordInfo = dbHelper.getRecordById(Integer.parseInt(recordId));
            asws = recordInfo.getRecordAsws();
            Log.e("数据库中的asws", asws);
            aswsnote = recordInfo.getRecordNote();
            recordNote = recordInfo.getRecordNote();
            recordPic = recordInfo.getRecordPic();
            note.setText(recordNote);
            finish.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(this);
        }

        if (!Constants.title.containsKey(regionId) && !Constants.possasws.containsKey(regionId) && !Constants.normalasws.containsKey(regionId)) {
            Log.e("question", "无question数据！");
            getAllQuestion(regionId);
        } else {
            Log.e("question", "已有question数据！");
            reportListAdapter = new ReportListAdapter(activity, getApplicationContext(), Constants.resultLen.get(regionId), Constants.title.get(regionId), Constants.type.get(regionId), Constants.possasws.get(regionId), Constants.normalasws.get(regionId), status, asws);
            reportList.setAdapter(reportListAdapter);
            Map<String, String> item = null;
            for (int j = 0; j < Constants.resultLen.get(regionId); j++) {
                item = new HashMap<String, String>();
                item.put("title", Constants.title.get(regionId).get(j));
                item.put("type", Constants.type.get(regionId).get(j));
                item.put("possasws", Constants.possasws.get(regionId).get(j));
                item.put("normalasws", Constants.normalasws.get(regionId).get(j));
                item.put("choosedasws", "未选择");
                item.put("error", "1");
                mData.add(item);
            }
            reportListAdapter.setData(mData);
        }

        gps = (ImageButton) findViewById(R.id.gps);
        gps.setOnClickListener(this);
        camera = (ImageButton) findViewById(R.id.camera);
        video = (ImageButton) findViewById(R.id.video);
//        open = (ImageButton) findViewById(R.id.open);
        camera.setOnClickListener(this);
        video.setOnClickListener(this);
//        open.setOnClickListener(this);


        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageView2);
        picView = (RelativeLayout) findViewById(R.id.picView);
        closePic = (ImageButton) findViewById(R.id.closePic);
        closePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.finish_report:
                recordNote = (note.getText()).toString();
//                if (Constants.recordEnd == null) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                Constants.recordEnd = timestampToString(ts);
//                }
                reportListAdapter.getData(sheetId, regionId, recordPic, recordVideo, recordNote, Constants.recordStart, Constants.recordEnd, Constants.recordEnd, checkTime, false);
                Toast.makeText(ReportActivity.this, "报告已保存，待提交", Toast.LENGTH_SHORT).show();
                intent = new Intent();
//                intent.putExtra("record_id", recordId);
                intent.putExtra("region_id", regionId);
                intent.putExtra("check_time", checkTime);
                this.setResult(RESULT_OK, intent);
//                this.startActivity(intent);
                finish();
                break;
            case R.id.submit_report:
                recordNote = (note.getText()).toString();
//                if (Constants.recordSub == null) {
                Timestamp ts2 = new Timestamp(System.currentTimeMillis());
                Constants.recordSub = timestampToString(ts2);
//                }
//                if (Constants.rtr.containsKey(regionId)) {
//                    String[] ss =Constants.rtr.get(regionId).split(";");
//                }
                String startTime = DBHelper.getInstance(getApplicationContext()).getStartTimeById(Integer.parseInt(recordId));
                String endTime = DBHelper.getInstance(getApplicationContext()).getEndTimeById(Integer.parseInt(recordId));
                reportListAdapter.getData(sheetId, regionId, recordPic, recordVideo, recordNote, startTime, endTime, Constants.recordSub, checkTime, true);
                Toast.makeText(ReportActivity.this, "报告已提交，待审核", Toast.LENGTH_SHORT).show();
                intent = new Intent();
//                intent.putExtra("record_id", recordId2);
//                intent.putExtra("region_id", regionId);
//                intent.putExtra("check_time", checkTime);
                this.setResult(3, intent);
//                this.startActivity(intent);
                finish();
                break;
            case R.id.gps:
                if (status.equals("finish") && Constants.teamName != null && Constants.teamName != "巡检员") {
                    new AlertDialog.Builder(this).setTitle("确定要修改本区域GPS吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeRegionGps();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else if (status.equals("finish") && Constants.teamName.equals("巡检员")) {
                    Toast.makeText(ReportActivity.this, "您没有修改本区域GPS的权限", Toast.LENGTH_SHORT).show();
                } else if (status.equals("change")) {
                    Toast.makeText(ReportActivity.this, "不可操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.camera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // create a file to save the image
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                // 此处这句intent的值设置关系到后面的onActivityResult中会进入哪个分支，即关系到data是否为null，如果此处指定，则后来的data为null
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                break;
            case R.id.video:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                // create a file to save the video
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                break;
//            case R.id.open:
//                intent = new Intent(Intent.ACTION_GET_CONTENT);
//                File file = new File("file://"+Environment.getExternalStorageDirectory().getPath()+"/GpsCheckGas");
////                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "file://"+Environment.getExternalStorageDirectory().getPath()+"/Picture/GpsCheckGas/");
//                intent.setDataAndType(Uri.fromFile(file), "image/*");
//                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
//                break;
            case R.id.back:
//                intent = new Intent(this, RegionActivity.class);
//                this.setResult(1, intent);
//                finish();

                //测试用
                intent = new Intent();
                intent.putExtra("region_id", regionId);
                intent.putExtra("check_time", checkTime);
                this.setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.closePic:
                reportList.setEnabled(true);
                picView.setVisibility(View.INVISIBLE);
                break;
        }
    }


    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        try {
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GpsCheckGas");
            Log.d(LOG_TAG, "Successfully created mediaStorageDir: " + mediaStorageDir);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error in Creating mediaStorageDir: " + mediaStorageDir);

        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(LOG_TAG, "onActivityResult: requestCode: " + requestCode
                + ", resultCode: " + requestCode + ", data: " + data);
        // 如果是拍照
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            Log.d(LOG_TAG, "CAPTURE_IMAGE");
            Toast.makeText(getApplicationContext(), "图片正在上传，请稍等", Toast.LENGTH_SHORT).show();
            if (RESULT_OK == resultCode) {
                Log.d(LOG_TAG, "RESULT_OK");

                // Check if the result includes a thumbnail Bitmap
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(LOG_TAG, "data is NOT null, file on default position.");

                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    // Image captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
                            Toast.LENGTH_LONG).show();

                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        reportList.setEnabled(false);
                        picView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(thumbnail);

                        try {
                            replaceFile(thumbnail, fileUri.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        uploadPic(fileUri.getPath(), "image");
                    }
                } else {

                    Log.d(LOG_TAG, "data IS null, file saved on target position.");
                    // If there is no thumbnail image data, the image
                    // will have been stored in the target output URI.

                    // Resize the full image to fit in out image view.
                    //压缩图片，否则无法正常转为string
                    int width = imageView.getWidth();
                    int height = imageView.getHeight();

                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(imageWidth / width, imageHeight
                            / height);

                    // Decode the image file into a Bitmap sized to fill the
                    // View
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            factoryOptions);
                    try {
                        replaceFile(bitmap, fileUri.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("picpath", fileUri.getPath());
                    uploadPic(fileUri.getPath(), "image");
                    reportList.setEnabled(false);
                    picView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        // 如果是录像
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            Log.d(LOG_TAG, "CAPTURE_VIDEO");
            Toast.makeText(getApplicationContext(), "视频正在上传，请稍等", Toast.LENGTH_SHORT).show();
            if (RESULT_OK == resultCode) {
                Log.d(LOG_TAG, "RESULT_OK");

                // Check if the result includes a thumbnail Bitmap
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(LOG_TAG, "data is NOT null, file on default position.");

                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    // Image captured and saved to fileUri specified in the Intent
//                    Toast.makeText(this, "文件保存在 \n" + data.getData(),
//                            Toast.LENGTH_LONG).show();

//                    if (data.hasExtra("dat")) {
//                        Bitmap thumbnail = data.getParcelableExtra("data");
//                        reportList.setEnabled(false);
//                        picView.setVisibility(View.VISIBLE);
//                        imageView.setImageBitmap(thumbnail);
//
//                        try {
//                            replaceFile(thumbnail, fileUri.getPath());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    uploadPic(fileUri.getPath(), "video");
//                    }
                } else {

                    Log.d(LOG_TAG, "data IS null, file saved on target position.");
                    // If there is no thumbnail image data, the image
                    // will have been stored in the target output URI.

                    // Resize the full image to fit in out image view.
//                    //压缩图片，否则无法正常转为string
//                    int width = imageView.getWidth();
//                    int height = imageView.getHeight();
//
//                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
//
//                    factoryOptions.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);
//
//                    int imageWidth = factoryOptions.outWidth;
//                    int imageHeight = factoryOptions.outHeight;
//
//                    // Determine how much to scale down the image
//                    int scaleFactor = Math.min(imageWidth / width, imageHeight
//                            / height);
//
//                    // Decode the image file into a Bitmap sized to fill the
//                    // View
//                    factoryOptions.inJustDecodeBounds = false;
//                    factoryOptions.inSampleSize = scaleFactor;
//                    factoryOptions.inPurgeable = true;
//
//                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
//                            factoryOptions);
//                    try {
//                        replaceFile(bitmap, fileUri.getPath());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("picpath", fileUri.getPath());
                    uploadPic(fileUri.getPath(), "video");
//                    reportList.setEnabled(false);
//                    picView.setVisibility(View.VISIBLE);
//                    imageView.setImageBitmap(bitmap);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

    }

    public void getAllQuestion(final int regionId) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.QUESTION_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("index", "region");
        params.put("region_id", regionId);

        asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<JSONArray>() {
            @Override
            public void onSuccess(int i, Header[] headers, String s, JSONArray jsonArray) {
                try {
                    List<String> list1 = new ArrayList<String>();
                    List<String> list2 = new ArrayList<String>();
                    List<String> list3 = new ArrayList<String>();
                    List<String> list4 = new ArrayList<String>();
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        list1.add(jsonObject.getString("title"));
                        list2.add(jsonObject.getString("possasws"));
                        list3.add(jsonObject.getString("normalasws"));
                        list4.add(jsonObject.getString("type"));
                    }
                    Constants.title.put(regionId, list1);
                    Constants.possasws.put(regionId, list2);
                    Constants.normalasws.put(regionId, list3);
                    Constants.type.put(regionId,list4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("取到的问题数量：", ""+jsonArray);//取出的数据全且正常
                Constants.resultLen.put(regionId, jsonArray.length());
                reportListAdapter = new ReportListAdapter(activity, getApplicationContext(), jsonArray.length(), Constants.title.get(regionId), Constants.type.get(regionId), Constants.possasws.get(regionId), Constants.normalasws.get(regionId), status, asws);
                reportList.setAdapter(reportListAdapter);
                Map<String, String> item = null;
                for (int j = 0; j < jsonArray.length(); j++) {
                    try {
                        JSONObject ob = jsonArray.getJSONObject(j);
                        item = new HashMap<String, String>();
                        item.put("title",ob.getString("title"));
                        item.put("type",ob.getString("type"));
                        item.put("possasws",ob.getString("possasws"));
                        item.put("normalasws",ob.getString("normalasws"));
                        item.put("choosedasws", "未选择");
                        item.put("error", "1");
                        mData.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                reportListAdapter.setData(mData);

                Toast.makeText(ReportActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSONArray jsonArray) {
                Toast.makeText(ReportActivity.this, "扫描失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                Log.e("onFailure", throwable.toString());
            }

            @Override
            protected JSONArray parseResponse(String s, boolean b) throws Throwable {
                JSONArray jsonArray = new JSONArray(s);
                return jsonArray;
            }
        });

    }

    public String timestampToString(Timestamp ts) {
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        try {
            tsStr = sdf.format(ts);
            return tsStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void replaceFile(Bitmap bm, String fileName) throws Exception {
        File file = new File(fileName);
        //检测图片是否存在
        if (file.exists()) {
            file.delete();  //删除原图片
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        //100表示不进行压缩，70表示压缩率为30%
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

    public void uploadPic(String path, final String type) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.UPLOADFILE_DATA_STRING;
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            RequestParams params = new RequestParams();
            try {
                params.put("filename", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.e("videoonSuccess", new String(bytes));
                    if (type.equals("image")) {
                        if (recordPic.equals("无图片")) {
                            recordPic = new String(bytes);
                        } else {
                            recordPic = recordPic + "," + new String(bytes);
                        }
                        Toast.makeText(getApplicationContext(), "图片上传成功", Toast.LENGTH_SHORT).show();
                    } else if (type.equals("video")) {
                        Toast.makeText(getApplicationContext(), "视频上传成功", Toast.LENGTH_SHORT).show();
                        recordVideo = new String(bytes);
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    if (type.equals("image")) {
                        Toast.makeText(getApplicationContext(), "图片上传失败", Toast.LENGTH_SHORT).show();
                    } else if (type.equals("video")) {
                        Toast.makeText(getApplicationContext(), "视频上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void changeRegionGps() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.CHANGEGPS_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("id", regionId);
        params.put("gps", Constants.gpsInfo);
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("gps", new String(bytes));
                Toast.makeText(ReportActivity.this, "成功修改区域位置", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(ReportActivity.this, "修改区域位置失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
