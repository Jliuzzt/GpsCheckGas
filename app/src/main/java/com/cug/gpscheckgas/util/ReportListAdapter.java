package com.cug.gpscheckgas.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.greendao.RecordInfo;
import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by WF on 2016/3/30.
 */
public class ReportListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Map<String, String>> mData; // 存储的选择的答案
    private List<Map<String, String>> changeData; // 存储的spinner值
    private Map<Integer, Integer> mPosition = new HashMap<Integer, Integer>(); // 存储的spinner选择的位置

    private Integer index = -1;
    private int resultLen;

    private List<String> title = new ArrayList<String>();
    private List<String> type = new ArrayList<String>();
    private List<String> possasws = new ArrayList<String>();
    private List<String> normalasws = new ArrayList<String>();
    private List<String> mList;
    private Activity activity;
    private int errorNum = 0;

    private String status;   //浏览状态,finish或change
    private String asws;     //数据库中已有答案
    private String[] choosedAsws;    //选择的答案临时数组

    public ReportListAdapter(Activity activity, Context mContext, int resultLen, List<String> title, List<String> type, List<String> possasws, List<String> normalasws, String status, String asws) {
        this.activity = activity;
        this.mContext = mContext;
        this.resultLen = resultLen;
        this.title = title;
        this.type = type;
        this.possasws = possasws;
        this.normalasws = normalasws;
        this.status = status;
        this.asws = asws;
    }

    //传入data参数格式,只会调用一次,主要是初始化mData中的列表项数目和map中的键值对
    //如果是setData初始化和getView重写mData的冲突,其实可以把
    //    Map<String, String> item = null;
    //    for (int j = 0; j < Constants.resultLen.get(regionId); j++) {
    //        item = new HashMap<String, String>();
    //        item.put("title", "");
    //        item.put("type", "");
    //        item.put("possasws", "");
    //        item.put("normalasws", "");
    //        item.put("choosedasws", "未选择");
    //        item.put("error", "");
    //        mData.add(item);
    //    }
    //    reportListAdapter.setData(mData);
    //改一下写入下面的init,然后直接再构造方法里调init(),这样就会在初始化之后才调getView
    //还有一个方法,不改写代码,试试把ReportActivity中"reportList.setAdapter(reportListAdapter);"这行代码放到调用setData之后
    //这两个方法试试还会不会这样

    public void setData(List<Map<String, String>> data) {
        mData = data;
        init();
    }

    //列表初始化
    private void init() {
        choosedAsws = new String[mData.size()];
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                choosedAsws[i] = "";
            }
        }
        //spinner选择位置,-2是随便写的,只要不是>=0就可以
        for (int j = 0; j < resultLen; j++) {
            mPosition.put(j, -2);
        }


        if (status.equals("change")) {
            try {
                JSONArray jsonArray = new JSONArray(asws);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);
                    choosedAsws[k] = jsonObject.getString("choosedasws");
                    Log.e("chooseAsws", choosedAsws[k]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("changeData", e.toString());
            }
            Log.e("changeData", asws + "");
        }
    }

    @Override
    public int getCount() {
        return resultLen;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("getview", "getView========");
        Log.e("position", ""+position);
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_questionlist_item, null);
            holder = new ViewHolder();

            holder.question = (TextView) convertView.findViewById(R.id.question);
            holder.questionType = (TextView) convertView.findViewById(R.id.questionType);
            holder.note = (EditText) convertView.findViewById(R.id.note);
            holder.possAsws = (Spinner) convertView.findViewById(R.id.possAsws);
            holder.note.setTag(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.note.setTag(position);
            /*holder.question.setText(null);
            holder.questionType.setText(null);
            holder.note.setText(null);
            holder.possAsws.setSelection(-2);*/
        }


        //设置edittext焦点监听
        holder.note.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                    if (index != -1 && index == position) {
                        holder.note.requestFocus();
                    } else {
                        holder.note.clearFocus();
                    }
                }
                return false;
            }
        });
        //edittext文本监听类
        class MyTextWatcher implements TextWatcher {

            public MyTextWatcher(ViewHolder holder) {
                mHolder = holder;
            }

            private ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !"".equals(s.toString())) {
                    int position = (Integer) mHolder.note.getTag();
                    // 当EditText数据发生改变的时候存到mData变量中
                    mData.get(position).put("title", title.get(position));
                    mData.get(position).put("type",type.get(position));
                    mData.get(position).put("possasws", possasws.get(position));
                    mData.get(position).put("normalasws", normalasws.get(position));
                    mData.get(position).put("choosedasws", s.toString());
                }
            }
        }

        //当答案需手动输入时,spinner不可用
        //需要注意的一点:标识符"#",确认后台是否是这个标志,不然这段代码不会执行
        if(possasws.get(position).equals("#")){
//            holder.possAsws.setEnabled(false);
            //修改模式下自动填充edittext并保存数据
            if (status.equals("change")) {
                holder.note.setText(choosedAsws[position].toString());
                mData.get(position).put("title", title.get(position));
                mData.get(position).put("type", type.get(position));
                mData.get(position).put("possasws", possasws.get(position));
                mData.get(position).put("normalasws", normalasws.get(position));
                mData.get(position).put("choosedasws", choosedAsws[position].toString());
            }
            //首次提交模式下:添加edittext监听事件
            else if (status.equals("finish")){
                holder.note.addTextChangedListener(new MyTextWatcher(holder));
            }
        }
        //当答案通过spinner选择时,edittext不可用
        else {
//            holder.note.setEnabled(false);
//            Log.e("possasws",possasws.toString());
//            Log.e("possasws.get("+position+")",possasws.get(position));
            String ss[] = possasws.get(position).split(";");
//            Log.e("ss",ss.toString());
//            Log.e("ss.length",ss.length+"");
            mList = new ArrayList<String>();
            int choosed = 0;
            for (int i = 0; i < ss.length; i++) {
                mList.add(ss[i]);
                if (choosedAsws[position].equals(ss[i]) && status.equals("change")) {
                    Log.e("chooseAsws", i + "");
                    choosed = i;
                }
            }
            //设置spinner格式和数据填充
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, mList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater mInflater = LayoutInflater.from(mContext);
                    convertView = mInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                    TextView label = (TextView) convertView.findViewById(R.id.spinner_item_label);
                    label.setText(mList.get(position));
                    if (holder.possAsws.getSelectedItemPosition() == position) {
                        convertView.setBackgroundColor(mContext.getResources().getColor(
                                R.color.spinner_checked));
                        label.setTextColor(Color.parseColor("#36bbb1"));
                    } else {
                        convertView.setBackgroundColor(mContext.getResources().getColor(
                                R.color.spinner_unchecked));
                        label.setTextColor(Color.parseColor("#808080"));
                    }

                    return convertView;
                }
            };
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            holder.possAsws.setAdapter(arrayAdapter);
            //修改模式下,如有答案修改,刷新mData中数据
            //但我觉得修改时会触发setOnItemSelectedListener,好像"if (status.equals("change")) {}"这段代码可以不需要
            if (status.equals("change")) {
                holder.possAsws.setSelection(choosed, true);
                mData.get(position).put("title", title.get(position));
                mData.get(position).put("type", type.get(position));
                mData.get(position).put("possasws", possasws.get(position));
                mData.get(position).put("normalasws", normalasws.get(position));
                mData.get(position).put("choosedasws", holder.possAsws.getSelectedItem() + "");
                if (normalasws.get(position).equals(holder.possAsws.getSelectedItem().toString())) {
                    mData.get(position).put("error", "0");
                } else {
                    mData.get(position).put("error", "1");
                }
            }
            //spinner选择监听事件
            //这一块需要调试一下,下滑之后6-9项是否有调用到这个方法,mData有没有被写入
            holder.possAsws.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                //触发选择后保存spinner选中的位置mPosition
                //保存spinner选中的内容getSelectedItem()
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionSp, long id) {
                    mPosition.put(position, positionSp);
                    mData.get(position).put("title", title.get(position));
                    mData.get(position).put("type", type.get(position));
                    mData.get(position).put("possasws", possasws.get(position));
                    mData.get(position).put("normalasws", normalasws.get(position));
                    mData.get(position).put("choosedasws", holder.possAsws.getSelectedItem() + "");
                    if (normalasws.get(position).equals(holder.possAsws.getSelectedItem().toString())) {
                        mData.get(position).put("error", "0");
                    } else {
                        mData.get(position).put("error", "1");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        //滑动时从mData中按position取值,自动填充edittext中的数据,避免数据紊乱
        Object value = mData.get(position).get("choosedasws");
        if (value != null && !"".equals(value)&& !"未选择".equals(value)&& possasws.get(position).equals("#")) {
            holder.note.setText(value.toString());
        } else {
        }
        //同上,防止数据紊乱
        if (!title.isEmpty()) {
            holder.question.setText(title.get(position));
        }
        //同上,防止数据紊乱
        if (!type.isEmpty()){
            holder.questionType.setText(type.get(position));
        }
        //同上,防止数据紊乱
        Object valuePoss = mData.get(position).get("possasws");
        if(!"#".equals(valuePoss)) {
            holder.possAsws.setSelection(mPosition.get(position));
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView question;
        private TextView questionType;
        private Spinner possAsws;
        private EditText note;
    }

    public void getData(final int sheetId, final int regionId, String recordPic,String recordVideo,String recordNote, String recordStart, String recordEnd, String recordSub, int checkTime, Boolean isUplaod) {
        Gson gson = new Gson();
        //这里会取上面保存后的mData
        //调试时比较此时的mData与滑动选答案并未提交前状态下的mData是否一致
        String recordAsws = gson.toJson(mData);
        Log.e("jsonArray", recordAsws + "");
        Log.e("jsonArray", recordNote + "");
        Log.e("jsonArray", Constants.gpsInfo);

        int i = 0;
        while (i < mData.size()) {
            if (mData.get(i).get("error").equals("1")) {
                errorNum++;
            }
            i++;
        }

        final String key = String.valueOf(sheetId) + ";" + String.valueOf(regionId);
        final DBHelper dbHelper = DBHelper.getInstance(mContext);
        List<RecordInfo> recordInfos = dbHelper.getAllRecordInfo();

        RecordInfo recordInfo = null;
        int recordId = 0;
        //判断数据库是否有数据,没有就从第一条开始,有就从最后一条加一开始
        if (recordInfos.isEmpty()) {
            recordId = 1;
            recordInfo = new RecordInfo();
        } else if (!recordInfos.isEmpty()) {
            if (!Constants.rtr.containsKey(key)) {
                int id = new Long(recordInfos.get(recordInfos.size() - 1).getId()).intValue();
                recordId = id + 1;
                recordInfo = new RecordInfo();
            } else if (Constants.rtr.containsKey(key)) {
                String[] temp = Constants.rtr.get(key).split(";");
                recordId = Integer.parseInt(temp[0]);
                recordInfo = DBHelper.getInstance(mContext).getRecordById(recordId);
            }
        }

        recordInfo.setId((long) recordId);
        recordInfo.setRecordId(recordId);
        recordInfo.setRecordGps(Constants.gpsInfo);
        recordInfo.setRecordAsws(recordAsws);
        recordInfo.setRecordError(errorNum);
        recordInfo.setRecordPic(recordPic+";"+recordVideo);
        recordInfo.setRecordStart(recordStart);
        recordInfo.setRecordEnd(recordEnd);
        recordInfo.setRecordSub(recordSub);
//        recordInfo.setRecordStatus(false);
        recordInfo.setCheckerId(Constants.peopleId);
//        recordInfo.setCheckTime(null);
        recordInfo.setRecordNote(recordNote);

        //存入本地数据库
        dbHelper.updateRecordInfo(recordInfo);
        //上传后隔2秒从数据库取值,因为上传成功后会改写数据库中的responseId
        if (isUplaod == false) {
            uploadRecord(checkTime, regionId);
            final int finalRecordId = recordId;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RecordInfo recordInfo1 = dbHelper.getRecordById(finalRecordId);
                    String value = String.valueOf(finalRecordId) + ";" + recordInfo1.getResponseId() + ";" + String.valueOf(Constants.peopleId);
                    if (!Constants.rtr.containsKey(key)) {
                        Constants.rtr.put(key, value);
                        Log.e("putkey", key);
                        Log.e("putkey", Constants.rtr.get(key));
                    }
                }
            }, 2000);
        } else if (isUplaod == true) {
            if (Constants.rtr.containsKey(key)) {
                String[] ss = Constants.rtr.get(key).split(";");
                Log.e("ss", ss[1]);
                changeRecord(Integer.parseInt(ss[0]), ss[1]);
            }
        }
    }

    private void uploadRecord(int checkTime, int regionId) {
//        final String[] recordId = {"0"};
        final DBHelper dbHelper = DBHelper.getInstance(mContext);
        List<RecordInfo> recordInfos = dbHelper.getAllRecordInfo();
        RecordInfo recordInfo = new RecordInfo();
        if (recordInfos.isEmpty()) {
            recordInfo = dbHelper.getRecordById(1);
        } else {
            int id = recordInfos.get(recordInfos.size() - 1).getRecordId();
            recordInfo = dbHelper.getRecordById(id);
        }
        int ptrId = dbHelper.getPtrByPerId(checkTime, regionId);
        try {
            String[] ss = recordInfo.getRecordPic().split(";");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gps", recordInfo.getRecordGps());
            JSONArray array = new JSONArray(recordInfo.getRecordAsws());
            jsonObject.put("asws", array);
            jsonObject.put("error", recordInfo.getRecordError().toString());
            jsonObject.put("picture", ss[0]);
            jsonObject.put("vedio",ss[1]);
            jsonObject.put("start", recordInfo.getRecordStart());
            jsonObject.put("end", recordInfo.getRecordEnd());
            jsonObject.put("ptr_id", ptrId);
            jsonObject.put("gener_id", recordInfo.getCheckerId());
            jsonObject.put("note", recordInfo.getRecordNote());

//            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String url = Constants.UPLOADRECORD_DATA_STRING;

            RequestParams params = new RequestParams();
            params.put("record", jsonObject.toString());
            final RecordInfo finalRecordInfo = recordInfo;
            asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.e("onSuccess", new String(bytes));
//                    Constants.tempRecordId = new String(bytes);
                    finalRecordInfo.setResponseId(new String(bytes));
                    dbHelper.updateRecordInfo(finalRecordInfo);
                    Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("onFailure", new String(bytes));
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeRecord(int recordId, String responseId) {
        DBHelper dbHelper = DBHelper.getInstance(mContext);
        RecordInfo recordInfo = new RecordInfo();
        recordInfo = dbHelper.getRecordById(recordId);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.CHANGERECORD_DATA_STRING;

        RequestParams params = new RequestParams();
        Log.e("ss", responseId);
        String[] ss = recordInfo.getRecordPic().split(";");
        params.put("record_id", Integer.parseInt(responseId));
        params.put("asws", recordInfo.getRecordAsws());
        params.put("error", recordInfo.getRecordError().toString());
        params.put("picture", ss[0]);
        Log.d("tupian",ss[0]);
        params.put("vedio",ss[1]);
        params.put("sub", recordInfo.getRecordSub());
        params.put("note", recordInfo.getRecordNote());

        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("onSuccess", new String(bytes));
                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("onFailure", new String(bytes));
                Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
