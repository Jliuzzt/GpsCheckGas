package com.cug.gpscheckgas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cug.gpscheckgas.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WF on 2016/4/18.
 */
public class RecordDetailAdapter extends BaseAdapter {
    private JSONObject jsonObject = new JSONObject();
    private Context mContext;
    private int id;
    private String region = "", periodTime = "", periodShift = "", submit = "", error = "", status = "", gener = "", start = "", branch = "", type = "", checkTime = "", end = "";

    public RecordDetailAdapter(Context mContext, JSONObject jsonObject) {
        this.mContext = mContext;
        this.jsonObject = jsonObject;
    }


    @Override
    public int getCount() {
        if (jsonObject.length() > 0) {
            return jsonObject.length();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (jsonObject.length() > 0) {

            return jsonObject;

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
            checkTime = jsonObject.getString("check_time");
            end = jsonObject.getString("end");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_recorddetail_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.regionName = (TextView) convertView.findViewById(R.id.regionName);
            viewHolder.shift = (TextView) convertView.findViewById(R.id.shift);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.error = (TextView) convertView.findViewById(R.id.error);
            viewHolder.start = (TextView) convertView.findViewById(R.id.start);
            viewHolder.end = (TextView) convertView.findViewById(R.id.end);
            viewHolder.sub = (TextView) convertView.findViewById(R.id.submit);
            viewHolder.checkTime = (TextView) convertView.findViewById(R.id.checkTime);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            viewHolder.gener = (TextView) convertView.findViewById(R.id.gener);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (jsonObject.length() > 0) {
            viewHolder.regionName.setText(region);
            viewHolder.shift.setText(periodShift);
            viewHolder.time.setText(periodTime);
            viewHolder.error.setText(error);
            viewHolder.start.setText(start);
            viewHolder.end.setText(end);
            viewHolder.sub.setText(submit);
            viewHolder.checkTime.setText(checkTime);
            if (status.equals("0")) {
                viewHolder.status.setText("未审核");
            } else if (status.equals("1")) {
                viewHolder.status.setText("已审核");
            }
            viewHolder.gener.setText(gener);
            viewHolder.type.setText(type);
        }
        return convertView;
    }

    public final class ViewHolder {
        private TextView regionName;
        private TextView shift;
        private TextView time;
        private TextView error;
        private TextView start;
        private TextView end;
        private TextView sub;
        private TextView checkTime;
        private TextView status;
        private TextView gener;
        private TextView type;
    }
}
