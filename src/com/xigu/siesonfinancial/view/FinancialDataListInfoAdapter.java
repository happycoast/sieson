package com.xigu.siesonfinancial.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.xigu.siesonfinancial.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FinancialDataListInfoAdapter extends BaseAdapter {
    private ArrayList<JSONObject> mDataList;
    private Context mContext;

    // 初始化状态字符串
    // 0已取消1待发货(商城)\待验证(项目) 2待收货(商城)\待服务(项目) 3待评价 4已评价 6拼单中 7退款中 8已退款
    private String[] ORDER_STATUS;

    public FinancialDataListInfoAdapter(Context context, ArrayList<JSONObject> dataList) {
        super();
        this.mContext = context;
        this.mDataList = dataList;
        ORDER_STATUS = mContext.getResources().getStringArray(R.array.order_status);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_consume_info_fragment, parent, false);
            holder = new ViewHolder();
            holder.consumeName = (TextView) convertView.findViewById(R.id.financial_data_item_tv_consume_name);
            holder.consumePhone = (TextView) convertView.findViewById(R.id.financial_data_item_tv_consume_phone);
            holder.orderDate = (TextView) convertView.findViewById(R.id.financial_data_item_tv_order_date);
            holder.orderNum = (TextView) convertView.findViewById(R.id.financial_data_item_tv_order_num);
            holder.payCount = (TextView) convertView.findViewById(R.id.financial_data_item_tv_paycount);
            holder.status = (TextView) convertView.findViewById(R.id.financial_data_item_tv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.consumeName.setText(mDataList.get(position).getString("receiver"));
            holder.consumePhone.setText(mDataList.get(position).getString("phone"));
            holder.orderDate.setText(mDataList.get(position).getString("create_time"));
            holder.orderNum.setText(mDataList.get(position).getString("order_no"));
            holder.payCount.setText(mDataList.get(position).getString("amount"));
            int status = Integer.valueOf(mDataList.get(position).getString("status"));
            holder.status.setText(ORDER_STATUS[status]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView orderNum, orderDate, payCount, consumeName, consumePhone, status;
    }
}
