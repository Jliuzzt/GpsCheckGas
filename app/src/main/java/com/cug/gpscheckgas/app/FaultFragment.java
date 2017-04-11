package com.cug.gpscheckgas.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class FaultFragment extends Fragment implements View.OnClickListener/*, View.OnFocusChangeListener */{

//    private TextView time;
    private TextView title;
    private TextView content;
    private static TextView picture;
    private Button submit;
    private ImageButton camera, video,closePic;
    private View faultView = null;
    private LinearLayout fault;
    private static final String LOG_TAG = "HelloCamera";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private ImageView imageView;
    private RelativeLayout picView;
    private String faultPic = "无图片";
    private String faultVideo = "无视频";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        faultView = inflater.inflate(R.layout.fragment_fault, container, false);

        fault = (LinearLayout) faultView.findViewById(R.id.faultText);
//        time = (TextView) faultView.findViewById(R.id.time);
        title = (TextView) faultView.findViewById(R.id.deviceid);
        content = (TextView) faultView.findViewById(R.id.fault);
        picture = (TextView)faultView.findViewById(R.id.pic);
        submit = (Button) faultView.findViewById(R.id.submit);
        camera = (ImageButton) faultView.findViewById(R.id.camera);
        video = (ImageButton) faultView.findViewById(R.id.video);
        picView = (RelativeLayout) faultView.findViewById(R.id.picView);
        imageView = (ImageView) faultView.findViewById(R.id.imageView2);
        closePic = (ImageButton) faultView.findViewById(R.id.closePic);

//        time.setVisibility(View.GONE);
//        time.setText("");
        title.setText("");
        content.setText("");
        picture.setText("");
//        String currentTime = timestampToString(new Timestamp(System.currentTimeMillis()));
//        time.setText(currentTime);

//        time.setOnFocusChangeListener(this);
        submit.setOnClickListener(this);
        camera.setOnClickListener(this);
        video.setOnClickListener(this);
        closePic.setOnClickListener(this);
        return faultView;
    }

    public String timestampToString(Timestamp ts) {
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            tsStr = sdf.format(ts);
            return tsStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendFaultMessage() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.FAULT_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("title", title.getText());
        params.put("word", content.getText());
        params.put("time", timestampToString(new Timestamp(System.currentTimeMillis())));
        params.put("worker_id", Constants.peopleId);
        params.put("pic_url",faultPic);
        params.put("video",faultVideo);

        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                Log.e("onSuccess", new String(bytes));
//                time.setText("");
                title.setText("");
                content.setText("");
                picture.setText("");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "发送失败", Toast.LENGTH_SHORT).show();
                Log.e("onFailure", new String(bytes));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                sendFaultMessage();
                break;
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.video:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.closePic:
                fault.setEnabled(true);
                picView.setVisibility(View.INVISIBLE);
                picture.setVisibility(View.VISIBLE);
                picture.setText("（已有图片）");
                break;
            default:
                break;
        }
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = null;
        try {
            mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GpsCheckGas");
            Log.d(LOG_TAG, "Successfully created mediaStorageDir: " + mediaStorageDir);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error in Creating mediaStorageDir: " + mediaStorageDir);
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("LOG_TAG", requestCode + "");
        Log.e("LOG_TAG", resultCode + "");
        Log.e("LOG_TAG",data+"");
        // 如果是拍照
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {

            if (-1 == resultCode) {

                // Check if the result includes a thumbnail Bitmap
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(LOG_TAG, "data is NOT null, file on default position.");

                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    Toast.makeText(getContext(), "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();

                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        fault.setEnabled(false);
                        picView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(thumbnail);

                        try {
                            replaceFile(thumbnail, fileUri.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        uploadPic(fileUri.getPath(),"image");
                    }
                } else {
                    Log.d(LOG_TAG, "data IS null, file saved on target position.");

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
                    uploadPic(fileUri.getPath(),"image");
                    fault.setEnabled(false);
                    picView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }
            } else if (resultCode == 0) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        // 如果是录像
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            Log.d(LOG_TAG, "CAPTURE_VIDEO");
            Toast.makeText(getContext(),"视频正在上传，请稍等",Toast.LENGTH_SHORT).show();
            if (-1 == resultCode) {
                Log.d(LOG_TAG, "RESULT_OK");

                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(LOG_TAG, "data is NOT null, file on default position.");
                    uploadPic(fileUri.getPath(), "video");

                } else {

                    Log.d(LOG_TAG, "data IS null, file saved on target position.");

                    uploadPic(fileUri.getPath(), "video");

                }
            } else if (resultCode == 0) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
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

    public void uploadPic(String path,final String type) {
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
                    Log.e("videoonSuccess",new String(bytes));
                    if (type.equals("image")) {
                        if (faultPic.equals("无图片")) {
                            faultPic = new String(bytes);
                        } else {
                            faultPic = faultPic + "," + new String(bytes);
                        }
                        Toast.makeText(getContext(),"图片上传成功",Toast.LENGTH_SHORT).show();
                    } else if (type.equals("video")) {
                        Toast.makeText(getContext(),"视频上传成功",Toast.LENGTH_SHORT).show();
                        faultVideo = new String(bytes);
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    if (type.equals("image")) {
                        Toast.makeText(getContext(),"图片上传失败",Toast.LENGTH_SHORT).show();
                    } else if (type.equals("video")) {
                        Toast.makeText(getContext(),"视频上传失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()){
//            case R.id.time:
//                String currentTime = timestampToString(new Timestamp(System.currentTimeMillis()));
//                time.setText(currentTime);
//                break;
//            default:
//                break;
//        }
//    }
}
