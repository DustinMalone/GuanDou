package com.gd.guandou.guandou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.bean.PurchashHistory;
import com.gd.guandou.guandou.view.PinnedSectionListView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class PurchaseHistoryAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter{

    private Context context;
    private List<PurchashHistory> recordList;
    private static final int NO_PINE_VIEW = 0;
    private static final int PINE_VIEW = 1;
    private boolean isPined = false;

    public PurchaseHistoryAdapter(Context context, List<PurchashHistory> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public int getViewTypeCount() {
        return 2;//悬浮布局 和 正常布局
    }

    @Override
    public int getItemViewType(int position) {
//        TransactionRecord.DataEntity entity = recordList.get(position);
//        isPined = position == getFirstPosByMonth(entity.getMonth());

        isPined = null!=recordList.get(position).getTitle()&&!TextUtils.isEmpty(recordList.get(position).getTitle().toString());
        if (isPined){
            return PINE_VIEW;//悬浮布局
        }else {
            return NO_PINE_VIEW;//正常布局
        }
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
        /*TransactionRecord.DataEntity entity = recordList.get(position);
        isPined = position == getFirstPosByMonth(entity.getMonth());*/

        isPined = null!=recordList.get(position).getTitle()&&!TextUtils.isEmpty(recordList.get(position).getTitle().toString());
        if (isPined){
            Log.e("pos",recordList.get(position).getTitle().toString());
            view = bindPineView(position,convertView,parent);
        }else {
            view = bindNoPineView(position,convertView,parent);
        }
        return view;
    }
    //需要实现的方法，return true时显示的布局将会悬浮。
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return isPined;
    }

    private View bindNoPineView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.lv_purchase_history_content,parent,false);
        }
        BillViewHolder holder = (BillViewHolder) view.getTag();
        if (holder == null) {
            holder = new BillViewHolder();
            holder.ivGoodsPic = (ImageView) view.findViewById(R.id.iv_purchase_history_pic);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_purchase_history_date);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_purchase_history_time);
            holder.tvGoodsName = (TextView) view.findViewById(R.id.tv_purchase_history_goodsname);
            holder.tvGoodsPrice = (TextView) view.findViewById(R.id.tv_purchase_history_goodsprice);
            view.setTag(holder);
        }
        holder.tvDate.setText(recordList.get(position).getDate());
        holder.tvTime.setText(recordList.get(position).getTime());
        holder.tvGoodsName.setText(recordList.get(position).getGoodsName());
        holder.tvGoodsPrice.setText(recordList.get(position).getGoodsPrices());
        return view;
    }

    private View bindPineView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.lv_purchase_history_head,parent,false);
        }
        BillPineHolder holder = (BillPineHolder) view.getTag();
        if (holder == null) {
            holder = new BillPineHolder();
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_purchase_history_head_title);
//            holder.tvFilter = (TextView) view.findViewById(R.id.tv_purchase_history_filter);
            view.setTag(holder);
        }

        holder.tvTitle.setText(recordList.get(position).getTitle().toString());

        return view;
    }

    class BillViewHolder {
        TextView tvDate, tvTime, tvGoodsName, tvGoodsPrice;
        ImageView ivGoodsPic;
    }

    class BillPineHolder {
        TextView tvTitle;
        TextView tvFilter;
    }

    private int getFirstPosByMonth(int month){
        int index = -1;
//        for (int i = 0; i < recordList.size(); i++) {
//            if (recordList.get(i).getMonth() == month){
//                return i;
//            }
//        }
        return index;
    }
}
