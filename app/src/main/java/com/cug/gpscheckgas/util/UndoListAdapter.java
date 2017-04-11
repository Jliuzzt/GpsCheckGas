package com.cug.gpscheckgas.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cug.gpscheckgas.R;
import com.cug.greendao.SheetInfo;

import java.util.List;

/**
 * Created by WF on 2016/3/14.
 */
public class UndoListAdapter extends BaseAdapter {

    private List<SheetInfo> undoTaskInfoList;
    private Context mContext;
    private Integer index = -1;
    private int i = 0;

    //    private static HashMap<Integer, Boolean> isSelected;
    private int[] sheetIdTemp;
//    private int restTime, sid;

    public UndoListAdapter(Context mContext, List<SheetInfo> undoTaskInfoList) {
        this.mContext = mContext;
        this.undoTaskInfoList = undoTaskInfoList;
//        this.mInflater = LayoutInflater.from(mContext);
        init();
    }

    private void init() {
//        isSelected = new HashMap<Integer, Boolean>();
        if (undoTaskInfoList != null) {
            sheetIdTemp = new int[undoTaskInfoList.size()];
            for (int i = 0; i < undoTaskInfoList.size(); i++) {
//                if(!isDownload.containsKey(i)) {
//                    isDownload.put(i, false);
//                }
                sheetIdTemp[i] = -1;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (undoTaskInfoList == null) {
            return 0;
        } else {
            return undoTaskInfoList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (undoTaskInfoList != null) {
            return undoTaskInfoList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SheetInfo undoTaskInfo = undoTaskInfoList.get(position);

        final ViewHolder viewHolder;
//        MyListener myListener = null;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_tasklist_item, parent, false);

            viewHolder = new ViewHolder();
            //可以理解为从vlist获取view  之后把view返回给ListView
//            myListener = new MyListener(position,viewHolder);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.download = (Button) convertView.findViewById(R.id.download);
//            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            viewHolder.item = convertView.findViewById(R.id.task_item);
            viewHolder.download.setTag(position);
//            viewHolder.download .setOnTouchListener(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    // TODO Auto-generated method stub
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
////                        index = (Integer) v.getTag();
//                        index = position;
//                        if (index != -1 && index == position) {
//                            viewHolder.download.requestFocus();
//                        } else {
//                            viewHolder.download.clearFocus();
//                        }
//                    }
//                    return false;
//                }
//            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (undoTaskInfo != null) {
//            if(undoTaskInfo.getSheetId()!=sheetIdTemp) {
            viewHolder.location.setText(undoTaskInfo.getSheetName());
            viewHolder.content.setText(undoTaskInfo.getSheetIntro());
            if (undoTaskInfo.getLength().equals(-1)) {
                viewHolder.time.setText("未下载或未完成");
            } else {
                int restTime = undoTaskInfo.getLength() - undoTaskInfo.getDoneTime();
                viewHolder.time.setText("剩余 " + restTime + " 个时间段未完成");
            }
//            if (position == sid - 1) {
//                viewHolder.time.setText("剩余 "+restTime+" 个时间段未完成");
//            }else{
//                viewHolder.time.setText("未下载或未完成");
//            }
//            viewHolder.download.setTag(position);
        }
//        }
//        if (position == sheetIdTemp[position] - 1) {
//            viewHolder.item.setVisibility(View.GONE);
//        } else {
//            viewHolder.item.setVisibility(View.VISIBLE);
//        }

        if (undoTaskInfo.getIsDoing() == false && undoTaskInfo.getIsDone() == false) {
            viewHolder.item.setVisibility(View.VISIBLE);
        } else if (undoTaskInfo.getIsDoing() == true && undoTaskInfo.getIsDone() == false) {
            viewHolder.item.setVisibility(View.VISIBLE);
        } else if (undoTaskInfo.getIsDoing() == false && undoTaskInfo.getIsDone() == true) {
            viewHolder.item.setVisibility(View.GONE);
        }

        viewHolder.download.setOnClickListener(new MyListener(position, viewHolder));
        if (/*isDownload.get(position) == true&&*/undoTaskInfo.getIsDownLoad() == true) {
            viewHolder.download.setFocusable(false);
            viewHolder.download.setText("已下载");
            viewHolder.download.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.download.setBackgroundResource(R.drawable.button_focus);
        } else if (/*isDownload.get(position) == false&&*/undoTaskInfo.getIsDownLoad() == false) {
            viewHolder.download.setFocusable(true);
            viewHolder.download.setText("下载");
            viewHolder.download.setTextColor(Color.parseColor("#ff9d09"));
            viewHolder.download.setBackgroundResource(R.drawable.button_normal);
        }
        return convertView;
    }

    public final class ViewHolder {
        private TextView location;
        private TextView time;
        private TextView content;
        private Button download;
        //        private ProgressBar progressBar;
        private View item;
    }

    class MyListener implements View.OnClickListener {
        int mPosition;
        ViewHolder viewHolder = new ViewHolder();

        public MyListener(int mPosition, ViewHolder viewHolder) {
            this.mPosition = mPosition;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {

//            ViewHolder viewHolder = new ViewHolder();
//            viewHolder.download = (Button) v.findViewById(R.id.download);
//            vh.progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//            v.setTag(viewHolder);
//            handler.sendEmptyMessage(new Message().what = 1);


//            int position = (Integer)viewHolder.download.getTag();

            int id = v.getId();
            if (id == viewHolder.download.getId()) {
                SheetInfo sheetInfo = undoTaskInfoList.get(mPosition);
                if (/*isDownload.get(mPosition) == false&&*/sheetInfo.getIsDownLoad() == false) {
//                    isDownload.put(mPosition, true);   // 根据点击的情况来将其位置和相应的状态存入
                    sheetInfo.setIsDownLoad(true);
                    DBHelper.getInstance(mContext).updateSheetInfo(sheetInfo);
                    viewHolder.download.setFocusable(false);
                    Toast.makeText(mContext, "此巡检表已下载，可点击查看任务", Toast.LENGTH_SHORT).show();
                } else if (/*isDownload.get(mPosition) == true&&*/sheetInfo.getIsDownLoad() == true) {
//                    Constants.isDownload.put(mPosition, false);  // 根据点击的情况来将其位置和相应的状态存入
//                    viewHolder.download.setFocusable(true);
//                    viewHolder.download.setBackgroundResource(R.drawable.button_normal);
//                    viewHolder.download.setText("下载");
                    viewHolder.download.setFocusable(false);
                    Toast.makeText(mContext, "此巡检表已下载，可点击查看任务", Toast.LENGTH_SHORT).show();
                }
                undoTaskInfoList = DBHelper.getInstance(mContext).getSheetInfoByBId(Constants.branchId);
                notifyDataSetChanged();
            }
        }

    }


//    public void updateView(int sheetId) {
//        sheetIdTemp[sheetId - 1] = sheetId;
////        sheetIdTemp = sheetId-1;
//        notifyDataSetChanged();
//    }

    public void updateRestTime(List<SheetInfo> undoTaskInfoList) {
//        restTime = length - doneTime;
//        sid = sheetId;
        this.undoTaskInfoList = undoTaskInfoList;
        notifyDataSetChanged();
    }

}