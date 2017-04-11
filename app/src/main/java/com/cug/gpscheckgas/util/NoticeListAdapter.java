package com.cug.gpscheckgas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cug.gpscheckgas.R;
import com.cug.greendao.Notices;

import java.util.List;

/**
 * Created by WF on 2016/3/24.
 */
public class NoticeListAdapter extends BaseAdapter {

//    private List<UndoTaskInfo> undoTaskInfoList;
    private List<Notices> notices;
    private Context mContext;

    public NoticeListAdapter(Context mContext, List<Notices> notices) {
        this.mContext = mContext;
        this.notices = notices;
    }


    @Override
    public int getCount() {
        if (notices == null) {
            return 0;
        } else {
            return notices.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (notices != null) {
            return notices.get(position);
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
        Notices notice = notices.get(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_noticelist_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.status = (Button) convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (notice != null) {
            viewHolder.title.setText(notice.getTitle());
            viewHolder.time.setText(notice.getSendTime());
            viewHolder.content.setText(notice.getDescription());

        }

//        UndoTaskInfo data =(UndoTaskInfo)getItem(position);
        if(notice.getIsChecked()){
            viewHolder.status.setBackgroundResource(R.drawable.notice_type);}
        else {
            viewHolder.status.setBackgroundResource(R.drawable.task_type);
        }

        return convertView;
    }


    public class ViewHolder {
        private TextView title;
        private TextView time;
        private TextView content;
        public Button status;

    }

    public void updateView(List<Notices> notices){
        this.notices = notices;
        notifyDataSetChanged();
    }

}
