package com.xigu.siesonfinancial.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xigu.siesonfinancial.MemberManagerActivity;
import com.xigu.siesonfinancial.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberAdapter extends BaseAdapter {
	private ArrayList<JSONObject> mDataList;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String[] validateStatus;
	private String mUID = "";
	private IMemberOprateListener IMemberOprateListener;

	public void setIMemberOprateListener(IMemberOprateListener iMemberOprateListener) {
		IMemberOprateListener = iMemberOprateListener;
	}

	public interface IMemberOprateListener {
		public void upgradeMember(String username, String uid);

		public void validateMember(String username, String uid);

		public void validateMemberrFace(String username, String uid);
	}

	public MemberAdapter(Context context, ArrayList<JSONObject> dataList) {
		super();
		this.mContext = context;
		this.mDataList = dataList;
		// validateStatus = ;
		// TODO
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_member_manage_fragment, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.member_manage_item_name);
			holder.upgrade = (TextView) convertView.findViewById(R.id.member_manage_item_oprate_upgrade);
			holder.validate = (TextView) convertView.findViewById(R.id.member_manage_item_oprate_validate);
			// holder.dilever =
			// convertView.findViewById(R.id.member_manage_item_oprate_dileve_line);
			holder.phone = (TextView) convertView.findViewById(R.id.member_manage_item_phone);
			holder.photo = (ImageView) convertView.findViewById(R.id.member_manage_item_photo);
			holder.status = (TextView) convertView.findViewById(R.id.member_manage_item_status);
			holder.type = (TextView) convertView.findViewById(R.id.member_manage_item_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.name.setText(mDataList.get(position).getString("username"));
			imageLoader.displayImage(mDataList.get(position).getString("avatar"), holder.photo);
			holder.phone.setText(mDataList.get(position).getString("phone"));
			holder.type.setText(mDataList.get(position).getString("def_name"));

			holder.upgrade.setText("审核");
			holder.upgrade.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// DOTO
					// 会员审核
					try {
						IMemberOprateListener.validateMember(mDataList.get(position).getString("username"),
								mDataList.get(position).getString("uid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			holder.validate.setText("人脸验证");
			holder.validate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO
					// 人脸认证
					try {
						IMemberOprateListener.validateMemberrFace(mDataList.get(position).getString("username"),
								mDataList.get(position).getString("uid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			String status = mDataList.get(position).getString("status");// 1待审核
																		// 2已审核
																		// null
																		// 为未设置人脸
																		// 否则为已设置人脸
			// holder.dilever.setVisibility(View.GONE);
			String toShowStatus = "";
			holder.upgrade.setVisibility(View.GONE);
			if ("2".equals(status)) {
				toShowStatus += "待审核";
				holder.upgrade.setVisibility(View.VISIBLE);
			} else if ("1".equals(status)) {
				toShowStatus += "已审核";
			}
			holder.validate.setVisibility(View.GONE);

			boolean isValidateFace = "null".equals(mDataList.get(position).getString("renlian"));
			if (!isValidateFace) {
				toShowStatus += "/已通过人脸验证";
				// holder.upgrade.setText("升级");
				// holder.upgrade.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // DOTO
				// // 会员升级
				// try {
				// IMemberOprateListener.upgradeMember(mDataList.get(position).getString("username"),
				// mDataList.get(position).getString("uid"));
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// });
				// holder.upgrade.setVisibility(View.VISIBLE);
			} else {
				toShowStatus += "/未通过人脸验证";
				holder.validate
						.setVisibility(holder.upgrade.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
			holder.status.setText(toShowStatus);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		ImageView photo;
		TextView name, phone, type, status, upgrade, validate;
		// View dilever;
	}

	// public void startScanQR(String username, String uid, String groupID) {
	// Intent intent = new Intent();
	// intent.setAction(MemberManagerActivity.ACTION_VALIDATE_FACE);
	// intent.putExtra("username", username);
	// intent.putExtra("uid", uid);
	// intent.putExtra("group", groupID);
	// mContext.sendBroadcast(intent);
	// }
}
