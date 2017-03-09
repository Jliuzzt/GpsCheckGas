package com.baidu.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.greendao.Notices;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by WF on 2016/4/29.
 */
public class PushReceiver extends PushMessageReceiver {

    public static final String TAG = PushReceiver.class.getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.e(TAG, responseString);

        if (errorCode == 0) {
            // 绑定成功
            Log.e(TAG, "绑定成功");
        }else{
            Log.e(TAG, "绑定失败");
        }
//        // Demo更新界面展示代码，处理逻辑
//        updateContent(context, responseString);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.d(TAG, "onUnbind");
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.e(TAG, "onSetTags");
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.e(TAG, "onDelTags");
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        Log.e(TAG, "onListTags");
    }

    /**
     * 接收透传消息的函数。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.e(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Demo更新界面展示代码，处理逻辑
//        updateContent(context, messageString);
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.e(TAG, notifyString);

        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String value = null;
                if (!customJson.isNull("key")) {
                    value = customJson.getString("key");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        updateContent(context, title,description);
    }

    @Override
     public void onNotificationArrived(Context context, String title, String description, String customContentString) {
        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.e(TAG, notifyString);

        // 自定义内容获取方式，key和value对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String value = null;
                if (!customJson.isNull("key")) {
                    value = customJson.getString("key");
                    // 參考 onNotificationClicked中的提示从自定义内容获取具体值

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateContent(context, title,description);
    }

    private void updateContent(Context context, String title,String description) {
        Log.e(TAG, "updateContent");

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            tsStr = sdf.format(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Notices notices = new Notices();
        notices.setTitle(title);
        notices.setDescription(description);
        notices.setIsChecked(false);
        notices.setSendTime(tsStr);
        DBHelper.getInstance(context).insertNoticesInfo(notices);
    }
}
