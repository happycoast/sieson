package com.xigu.siesonfinancial.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xigu.siesonfinancial.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WaiterListAdapter extends BaseAdapter {
	private ArrayList<JSONObject> mWaiterList = new ArrayList<JSONObject>();
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public WaiterListAdapter(Context context, ArrayList<JSONObject> waiterList) {
		super();
		this.mContext = context;
		this.mWaiterList = waiterList;
		// BitmapFactory.Options decodingOptions =new Options();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .decodingOptions(decodingOptions)//设置图片的解码配置
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				.displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mWaiterList.size();
	}

	@Override
	public JSONObject getItem(int position) {
		return mWaiterList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_waiter_list, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.tv_waiter_list_item_name);
			holder.imageView = (ImageView) convertView.findViewById(R.id.iv_waiter_list_item_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			imageLoader.displayImage(
					"http://101.201.148.205:8571/" + mWaiterList.get(position).getString("touxiang"),
					holder.imageView);
			if (0 == position) {
				holder.name.setText("无");
				holder.imageView.setVisibility(View.GONE);
			} else {
				holder.name.setText(mWaiterList.get(position).getString("real_name"));
				holder.imageView.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView name;
	}
}
