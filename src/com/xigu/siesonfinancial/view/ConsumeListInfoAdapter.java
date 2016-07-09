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

public class ConsumeListInfoAdapter extends BaseAdapter {
    private ArrayList<JSONObject> mConsumeList = new ArrayList<JSONObject>();
    private Context mContext;

    public ConsumeListInfoAdapter(Context context, ArrayList<JSONObject> consumeList) {
        super();
        this.mContext = context;
        this.mConsumeList = consumeList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mConsumeList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return mConsumeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_consume_info_list, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_consume_info_list_item_name);
            holder.price = (TextView) convertView.findViewById(R.id.tv_consume_info_list_item_price);
            holder.status = (TextView) convertView.findViewById(R.id.tv_consume_info_list_item_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.name.setText(mConsumeList.get(position).getString("item_name"));
            holder.price.setText(mConsumeList.get(position).getString("item_price"));
            holder.status.setText(mConsumeList.get(position).getString("item_status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView name, price, status;
    }
}
