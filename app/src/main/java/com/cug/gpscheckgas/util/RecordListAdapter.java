package com.cug.gpscheckgas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cug.gpscheckgas.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WF on 2016/4/18.
 */
public class RecordListAdapter extends BaseAdapter {
    private JSONArray jsonArray = new JSONArray();
    private Context mContext;
    private int id;
    private String region = "", periodTime = "", periodShift = "", submit = "", error = "", status = "", gener = "", start = "", branch = "", type = "", check_time = "", end = "";

    public RecordListAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        if (jsonArray.length() > 0) {
            return jsonArray.length();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (jsonArray.length() > 0) {
            try {
                return jsonArray.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            id = jsonObject.getInt("id");
            region = jsonObject.getString("region");
            periodTime = jsonObject.getString("period_time");
            periodShift = jsonObject.getString("period_shift");
            submit = jsonObject.getString("submit");
            error = jsonObject.getString("error");
            status = jsonObject.getString("status");
            gener = jsonObject.getString("gener");
            start = jsonObject.getString("start");
            branch = jsonObject.getString("branch");
            type = jsonObject.getString("type");
            check_time = jsonObject.getString("check_time");
            end = jsonObject.getString("end");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_recordlist_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.subTime = (TextView) convertView.findViewById(R.id.subTime);
            viewHolder.regionName = (TextView) convertView.findViewById(R.id.regionName);
            viewHolder.errorNum = (TextView) convertView.findViewById(R.id.errorNum);
            viewHolder.status = (Button) convertView.findViewById(R.id.check_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (jsonArray.length() > 0) {
            viewHolder.subTime.setText(submit);
            viewHolder.regionName.setText(region);
                viewHolder.errorNum.setText(error);
            if(status.equals("0")){
                viewHolder.status.setText("未审核");
            }else if(status.equals("1")){
                viewHolder.status.setText("已审核");
            }
        }
        return convertView;
    }

    public final class ViewHolder {
        private TextView subTime;
        private TextView regionName;
        private TextView errorNum;
        private Button status;
    }
}
