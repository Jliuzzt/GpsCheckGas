package com.cug.gpscheckgas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cug.gpscheckgas.R;
import com.cug.greendao.SheetInfo;

import java.util.List;

/**
 * Created by WF on 2016/4/6.
 */
public class DoneListAdapter extends BaseAdapter {

    private List<SheetInfo> doneTaskInfoList;
    private Context mContext;
    //    private int sheetIdTemp = -1;
//    private int[] sheetIdTemp;

    public DoneListAdapter(Context mContext, List<SheetInfo> doneTaskInfoList) {
        this.mContext = mContext;
        this.doneTaskInfoList = doneTaskInfoList;
//        init();
    }

//    private void init() {
//        sheetIdTemp = new int[doneTaskInfoList.size()];
//        if (doneTaskInfoList != null) {
//            for (int i = 0; i < doneTaskInfoList.size(); i++) {
//                sheetIdTemp[i] = -1;
//            }
//        }
//    }

    @Override
    public int getCount() {
        if (doneTaskInfoList == null) {
            return 0;
        } else {
            return doneTaskInfoList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (doneTaskInfoList != null) {
            return doneTaskInfoList.get(position);
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
        SheetInfo doneTaskInfo = doneTaskInfoList.get(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_donetasklist_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.examine = (Button) convertView.findViewById(R.id.examine);
            viewHolder.item = convertView.findViewById(R.id.task_item);

            viewHolder.examine.setTag(position);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (doneTaskInfo != null) {
            viewHolder.location.setText(doneTaskInfo.getSheetName());
            viewHolder.content.setText(doneTaskInfo.getSheetIntro());
            if (doneTaskInfo.getLength().equals(-1)) {
                viewHolder.time.setText("未下载或未完成");
            } else {
                int restTime = doneTaskInfo.getLength() - doneTaskInfo.getDoneTime();
                if (restTime != 0) {
                    viewHolder.time.setText("已完成 " + doneTaskInfo.getDoneTime() + " 个时间段");
                } else if (restTime == 0) {
                    viewHolder.time.setText("该巡检表已完成");
                }
            }
//            viewHolder.download.setTag(position);
        }

//        if (position == (sheetIdTemp[position] - 1)) {
//            viewHolder.item.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.item.setVisibility(View.GONE);
//        }
        if (doneTaskInfo.getIsDoing() == false && doneTaskInfo.getIsDone() == false) {
            viewHolder.item.setVisibility(View.GONE);
        } else if (doneTaskInfo.getIsDoing() == true && doneTaskInfo.getIsDone() == false) {
            viewHolder.item.setVisibility(View.VISIBLE);
        } else if (doneTaskInfo.getIsDoing() == false && doneTaskInfo.getIsDone() == true) {
            viewHolder.item.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public final class ViewHolder {
        private TextView location;
        private TextView time;
        private TextView content;
        private Button examine;
        private View item;
    }

    public boolean updateView(List<SheetInfo> doneTaskInfoList) {
//        if (Constants.sheetId != -1) {
//            int sheetId = Constants.sheetId;
//            sheetIdTemp[sheetId - 1] = sheetId;
//            notifyDataSetChanged();
//            return true;
//        } else {
//            return false;
//        }
//        sheetIdTemp[sheetId - 1] = sheetId;
        this.doneTaskInfoList = doneTaskInfoList;
        notifyDataSetChanged();
        return true;
    }
}
