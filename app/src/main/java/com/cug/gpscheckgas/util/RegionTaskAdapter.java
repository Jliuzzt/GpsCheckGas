package com.cug.gpscheckgas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cug.gpscheckgas.R;
import com.cug.greendao.RegionInfo;

import java.util.List;

/**
 * Created by WF on 2016/3/22.
 */
public class RegionTaskAdapter extends BaseAdapter {

    private List<RegionInfo> regionInfos;
    private Context mContext;
//    private int[] posiTemp;

    public RegionTaskAdapter(Context mContext, List<RegionInfo> regionInfos) {
        this.mContext = mContext;
        this.regionInfos = regionInfos;
//        init();
    }

//    private void init() {
//        posiTemp = new int[regionInfos.size()];
//        if (regionInfos != null) {
//            for (int i = 0; i < regionInfos.size(); i++) {
//                posiTemp[i] = -1;
//            }
//        }
//    }

    @Override
    public int getCount() {
        if (regionInfos == null) {
            return 0;
        } else {
            return regionInfos.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (regionInfos != null) {
            return regionInfos.get(position);
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
        RegionInfo regionInfo = regionInfos.get(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.select_regiontask_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.regionStatus = (ImageView) convertView.findViewById(R.id.regionimage);
            viewHolder.regionName = (TextView) convertView.findViewById(R.id.regionname);

            viewHolder.regionStatus.setTag(position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.regionStatus.setTag(position);
        }


        if (regionInfo != null) {
//            if (position == posiTemp[position]) {
//                viewHolder.regionStatus.setImageResource(R.drawable.icon_region_done);
//            } else {
//                viewHolder.regionStatus.setImageResource(R.drawable.icon_region_undo);
//            }
            if (regionInfo.getIsDone().equals(true)) {
                viewHolder.regionStatus.setImageResource(R.drawable.icon_region_done);
            } else {
                viewHolder.regionStatus.setImageResource(R.drawable.icon_region_undo);
            }
//            viewHolder.regionName.setText(regionTaskInfo.getRegionName());
            viewHolder.regionName.setText(regionInfo.getRegionName());
//            viewHolder.regionName.setText(regionInfo.getRegionSort() + "#机器");
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView regionStatus;
        private TextView regionName;

    }

//    //完成任务后更新图标
//    public void updateView(int sort) {
//        //用regionId查找Id
////        posiTemp[sort - 1] = sort;
//        posiTemp[sort] = sort;
//        Log.e("posiTemp", posiTemp + "");
//    }

    //切换spinner后刷新图标
    public void updateChangeView(List<RegionInfo> regionInfos) {
//        if (Constants.regioinId != -1) {
//            int sort = Constants.regioinId;
//            posiTemp[sort] = sort;
//            notifyDataSetChanged();
//        } else {
//        }
        this.regionInfos = regionInfos;
        notifyDataSetChanged();
    }

}
