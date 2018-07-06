package com.gd.guandou.guandou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.bean.GameTreasure;
import com.gd.guandou.guandou.untils.DateUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class GameTreasureAdapter extends BaseAdapter {

    private Context context;
    private List<GameTreasure> recordList;


    public GameTreasureAdapter(Context context, List<GameTreasure> recordList) {
        this.context = context;
        this.recordList = recordList;
    }


    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {

        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.item_gain_records,parent,false);
        }
        BillViewHolder holder = (BillViewHolder) view.getTag();
        if (holder == null) {
            holder = new BillViewHolder();
            holder.tvText = (TextView) view.findViewById(R.id.tv_item_gain_record_text);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_item_gain_record_date);
            holder.tvGain = (TextView) view.findViewById(R.id.tv_item_gain_record);
            view.setTag(holder);
        }
        holder.tvDate.setText(DateUtils.getdatebymyself(Long.parseLong(recordList.get(position).getCreateDate()),"yyyy-MM-dd"));
        holder.tvGain.setText("+"+recordList.get(position).getJfNum());
        holder.tvText.setText(recordList.get(position).getRecource());

        return view;
    }



    class BillViewHolder {
        TextView tvDate,tvText,tvGain;
    }



}
