package com.cug.gpscheckgas.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.Constants;
import com.cug.gpscheckgas.util.DBHelper;
import com.cug.gpscheckgas.util.NoticeListAdapter;
import com.cug.greendao.Notices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.sql.Timestamp;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class NoticeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    View noticeView = null;
    private static NoticeListAdapter noticeListAdapter = null;
    //    private List<UndoTaskInfo> undoTaskInfo = null;
    private List<Notices> notices = null;
    private static ListView noticeListView = null;
    private static TextView noNotice;
    private Button alarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        addDataToDb();
        noticeView = inflater.inflate(R.layout.fragment_notice, container, false);
        notices = DBHelper.getInstance(getContext()).getAllNotices();
        noticeListView = (ListView) noticeView.findViewById(R.id.noticeList);
        noticeListAdapter = new NoticeListAdapter(this.getContext(), notices);
        noticeListView.setAdapter(noticeListAdapter);

        noticeListView.setOnItemClickListener(this);

        noNotice = (TextView) noticeView.findViewById(R.id.noNotice);
        alarm = (Button)noticeView.findViewById(R.id.alarm);
        alarm.setOnClickListener(this);
        return noticeView;
    }

    public static void updateView(Context context) {
        if (DBHelper.getInstance(context).getAllNotices() != null && noticeListAdapter != null) {
            noNotice.setVisibility(View.GONE);
            noticeListView.setVisibility(View.VISIBLE);
            noticeListAdapter.updateView(DBHelper.getInstance(context).getAllNotices());
        } else if (DBHelper.getInstance(context).getAllNotices() == null) {
            noNotice.setVisibility(View.VISIBLE);
            noticeListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//      noticeListAdapter.setClicked(true);
//      UndoTaskInfo data = (UndoTaskInfo) noticeListAdapter.getItem(position);
        notices = DBHelper.getInstance(getContext()).getAllNotices();
        Notices notice = notices.get(position);
        if (!notice.getIsChecked()) {
            notice.setIsChecked(true);
            DBHelper.getInstance(getContext()).updateNoticeInfo(notice);
//                    undoTaskInfo.get(position).setStatus(true);
            noticeListAdapter.notifyDataSetChanged();
        }
    }

    public void pushAlarmMessage() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = Constants.ALARM_DATA_STRING;

        RequestParams params = new RequestParams();
        params.put("gps", Constants.gpsInfo);
        params.put("time", new Timestamp(System.currentTimeMillis()));
        params.put("worker_id", Constants.peopleId);

        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Toast.makeText(getContext(), "呼救成功", Toast.LENGTH_SHORT).show();
                Log.e("onSuccess", new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "呼救失败", Toast.LENGTH_SHORT).show();
                Log.e("onFailure", new String(bytes));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alarm:
                pushAlarmMessage();
                break;
            default:
                break;
        }
    }
}
