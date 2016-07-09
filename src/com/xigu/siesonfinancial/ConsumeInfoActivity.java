package com.xigu.siesonfinancial;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qcloud.qcloudfr_android_sdk.QcloudFrSDK;
import com.qcloud.qcloudfr_android_sdk.sign.QcloudFrSign;
import com.xigu.siesonfinancial.service.SiesonService;
import com.xigu.siesonfinancial.view.ConsumeListInfoAdapter;
import com.xigu.siesonfinancial.view.WaiterListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConsumeInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = "kecc";
	private static final String IMAGE_URL_PRE = "http://101.201.148.205:8571/";
	private static final int TAKE_PIC = 10;
	private ListView mLvConsume;
	private ConsumeListInfoAdapter mAdapter;
	private ArrayList<JSONObject> mConsumeList = new ArrayList<JSONObject>();

	private EditText consumeName;
	private EditText consumeSex;
	private EditText consumePhone;

	private LinearLayout mLlFxs;
	private LinearLayout mLlTrs;
	private LinearLayout mLlZl;
	private LinearLayout mLlHjgw;
	private LinearLayout mLlTpds;
	private ProgressDialog dialog;

	private String mVipUid = "";
	private String selectedFxsUid = "";
	private String selectedTrsUid = "";
	private String selectedZlUid = "";
	private String selectedHjgwUid = "";
	private String selectedTpdsUid = "";
	private static final int POS_FXS = 0;
	private static final int POS_TRS = 1;
	private static final int POS_ZL = 2;
	private static final int POS_HJGW = 3;
	private static final int POS_TPDS = 4;
	private ArrayList<JSONObject> mFxsUidList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mTrsUidList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mZlUidList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mHjgwUidList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mTpdsUidList = new ArrayList<JSONObject>();
	private JSONObject defualtFxs = new JSONObject();
	private JSONObject defualtTrs = new JSONObject();
	private JSONObject defualtZl = new JSONObject();
	private JSONObject defualtHjgw = new JSONObject();
	private JSONObject defualtTpds = new JSONObject();
	private WaiterListAdapter mFxsAdapter;
	private WaiterListAdapter mTrsAdapter;
	private WaiterListAdapter mUZlAdapter;
	private WaiterListAdapter mHjgwAdapter;
	private WaiterListAdapter mTpdsAdapter;
	private ListPopupWindow listPopupWindow;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (SiesonService.ACTION_GET_ORDER_RESULT.equals(intent.getAction())) {
				handleGetOrderResult(intent);
			}
			if (SiesonService.ACTION_COMFIREM_BUY_RESULT.equals(intent.getAction())) {
				handleComfirmBuyResult(intent);
			}
		}
	};
	private Options decodingOptions = new Options();
	private DisplayImageOptions options;
	private String mOid = "";
	private String mCode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (null != getIntent()) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(SiesonService.ACTION_GET_ORDER_RESULT);
			registerReceiver(mReceiver, intentFilter);

			Intent intent = new Intent();
			intent.putExtra(SiesonService.VARS, getIntent().getStringExtra(SiesonService.VARS));
			intent.setAction(SiesonService.ACTION_GET_ORDER);
			sendBroadcast(intent);
		}
		setContentView(R.layout.activity_consume_info);
		initeviews();
	}

	protected void handleComfirmBuyResult(Intent intent) {
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (Exception e) {
			Log.e(TAG, "转换Json异常  : " + result);
		}
		if (null == resultJsonObject) {
			return;
		}
		Log.d(TAG, result);
		try {
			Toast.makeText(this, resultJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
			if(0 == resultJsonObject.getInt("status")){
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		
	}

	private void handleGetOrderResult(Intent intent) {
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (Exception e) {
			Log.e(TAG, "转换Json异常  : " + result);
		}
		if (null == resultJsonObject) {
			return;
		}
		Log.d(TAG, result);
		try {
			JSONObject jsondata = resultJsonObject.getJSONObject("data");
			mOid = resultJsonObject.getString("oid");
			mCode = resultJsonObject.getString("sor");
			// JSONArray itemInfo = resultJsonObject.getJSONArray("item_info");
			// for (int i = 0; i < itemInfo.length(); i++) {
			// mConsumeList.add(itemInfo.getJSONObject(i));
			// }
			JSONObject itemInfo = jsondata.getJSONObject("item_info");
			JSONObject vipInfo = jsondata.getJSONObject("vip_info");

			mConsumeList.add(itemInfo);
			mAdapter.notifyDataSetChanged();
			consumeName.setText(vipInfo.getString("vip_name"));
			consumeSex.setText("1".equals(vipInfo.getString("vip_sex")) ? "男" : "女");
			consumePhone.setText(vipInfo.getString("vip_phone"));

			JSONObject fsxInfo = jsondata.getJSONObject("fxs_info");
			initUidLists(fsxInfo);
			if (1 == jsondata.getInt("face")) {
				mVipUid = jsondata.getJSONObject("vip_info").getString("vip_uid");
				startTakePic();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void startTakePic() {
		Intent takepicture = new Intent(ConsumeInfoActivity.this, TakePictureActivity.class);
		takepicture.putExtra("take_count", 1);
		startActivityForResult(takepicture, TAKE_PIC);
	}

	private void initUidLists(JSONObject fsxInfo) throws JSONException {
		JSONArray fsx = fsxInfo.getJSONObject("131").getJSONArray("list");
		int len = fsx.length();
		mFxsUidList.add(defualtFxs);
		for (int i = 0; i < len; i++) {
			mFxsUidList.add(fsx.getJSONObject(i));
		}
		JSONArray trs = fsxInfo.getJSONObject("132").getJSONArray("list");
		len = trs.length();
		mTrsUidList.add(defualtTrs);
		for (int i = 0; i < len; i++) {
			mTrsUidList.add(trs.getJSONObject(i));
		}
		JSONArray zl = fsxInfo.getJSONObject("133").getJSONArray("list");
		len = zl.length();
		mZlUidList.add(defualtZl);
		for (int i = 0; i < len; i++) {
			mZlUidList.add(zl.getJSONObject(i));
		}
		JSONArray hjgw = fsxInfo.getJSONObject("134").getJSONArray("list");
		len = hjgw.length();
		mHjgwUidList.add(defualtHjgw);
		for (int i = 0; i < len; i++) {
			mHjgwUidList.add(hjgw.getJSONObject(i));
		}
		JSONArray tpds = fsxInfo.getJSONObject("135").getJSONArray("list");
		len = tpds.length();
		mTpdsUidList.add(defualtTpds);
		for (int i = 0; i < len; i++) {
			mTpdsUidList.add(tpds.getJSONObject(i));
		}
	}

	private void initeviews() {
		mLvConsume = (ListView) findViewById(R.id.lv_consume_list);
		mAdapter = new ConsumeListInfoAdapter(ConsumeInfoActivity.this, mConsumeList);
		mLvConsume.setAdapter(mAdapter);
		consumeName = (EditText) findViewById(R.id.et_consume_info_name);
		consumeSex = (EditText) findViewById(R.id.et_consume_info_sex);
		consumePhone = (EditText) findViewById(R.id.et_consume_info_phone);

		mLlFxs = (LinearLayout) findViewById(R.id.ll_consume_info_fxs);
		mLlFxs.setOnClickListener(this);
		mLlHjgw = (LinearLayout) findViewById(R.id.ll_consume_info_hjgw);
		mLlHjgw.setOnClickListener(this);
		mLlTpds = (LinearLayout) findViewById(R.id.ll_consume_info_tpds);
		mLlTpds.setOnClickListener(this);
		mLlTrs = (LinearLayout) findViewById(R.id.ll_consume_info_trs);
		mLlTrs.setOnClickListener(this);
		mLlZl = (LinearLayout) findViewById(R.id.ll_consume_info_zl);
		mLlZl.setOnClickListener(this);

		// 璁剧疆榛樿鏈嶅姟浜哄憳
		gernerateDefultWaiters();

		// listPopupWindow = new ListPopupWindow(this);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher) // 璁剧疆鍥剧墖鍦ㄤ笅杞芥湡闂存樉绀虹殑鍥剧墖
				.showImageForEmptyUri(R.drawable.ic_launcher)// 璁剧疆鍥剧墖Uri涓虹┖鎴栨槸閿欒鐨勬椂鍊欐樉绀虹殑鍥剧墖
				.showImageOnFail(R.drawable.ic_launcher) // 璁剧疆鍥剧墖鍔犺浇/瑙ｇ爜杩囩▼涓敊璇椂鍊欐樉绀虹殑鍥剧墖
				.cacheInMemory(true)// 璁剧疆涓嬭浇鐨勫浘鐗囨槸鍚︾紦瀛樺湪鍐呭瓨涓�
				.cacheOnDisc(true)// 璁剧疆涓嬭浇鐨勫浘鐗囨槸鍚︾紦瀛樺湪SD鍗′腑
				.considerExifParams(true) // 鏄惁鑰冭檻JPEG鍥惧儚EXIF鍙傛暟锛堟棆杞紝缈昏浆锛�
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 璁剧疆鍥剧墖浠ュ浣曠殑缂栫爜鏂瑰紡鏄剧ず
				.bitmapConfig(Bitmap.Config.RGB_565)// 璁剧疆鍥剧墖鐨勮В鐮佺被鍨�//
				// TODO
				.decodingOptions(decodingOptions)// 璁剧疆鍥剧墖鐨勮В鐮侀厤缃�
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis涓轰綘璁剧疆鐨勪笅杞藉墠鐨勫欢杩熸椂闂�
				// 璁剧疆鍥剧墖鍔犲叆缂撳瓨鍓嶏紝瀵筨itmap杩涜璁剧疆
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(false)// 璁剧疆鍥剧墖鍦ㄤ笅杞藉墠鏄惁閲嶇疆锛屽浣�
				.displayer(new RoundedBitmapDisplayer(20))// 鏄惁璁剧疆涓哄渾瑙掞紝寮у害涓哄灏�
				.displayer(new FadeInBitmapDisplayer(100))// 鏄惁鍥剧墖鍔犺浇濂藉悗娓愬叆鐨勫姩鐢绘椂闂�
				.build();// 鏋勫缓瀹屾垚
	}

	private void gernerateDefultWaiters() {
		try {
			defualtFxs.put("uid", "");
			defualtFxs.put("real_name", getResources().getString(R.string.name_fxs));
			defualtFxs.put("gonghao", "");
			defualtFxs.put("touxiang", "");
			defualtTrs.put("uid", "");
			defualtTrs.put("real_name", getResources().getString(R.string.name_trs));
			defualtTrs.put("gonghao", "");
			defualtTrs.put("touxiang", "");
			defualtZl.put("uid", "");
			defualtZl.put("real_name", getResources().getString(R.string.name_zl));
			defualtZl.put("gonghao", "");
			defualtZl.put("touxiang", "");
			defualtHjgw.put("uid", "");
			defualtHjgw.put("real_name", getResources().getString(R.string.name_hjgw));
			defualtHjgw.put("gonghao", "");
			defualtHjgw.put("touxiang", "");
			defualtTpds.put("uid", "");
			defualtTpds.put("real_name", getResources().getString(R.string.name_tpds));
			defualtTpds.put("gonghao", "");
			defualtTpds.put("touxiang", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_consume_info_fxs:
			mFxsAdapter = new WaiterListAdapter(ConsumeInfoActivity.this, mFxsUidList);
			showPop(v, mFxsAdapter, POS_FXS);
			break;
		case R.id.ll_consume_info_trs:
			mTrsAdapter = new WaiterListAdapter(ConsumeInfoActivity.this, mTrsUidList);
			showPop(v, mTrsAdapter, POS_TRS);
			break;
		case R.id.ll_consume_info_zl:
			mUZlAdapter = new WaiterListAdapter(ConsumeInfoActivity.this, mZlUidList);
			showPop(v, mUZlAdapter, POS_ZL);
			break;
		case R.id.ll_consume_info_hjgw:
			mHjgwAdapter = new WaiterListAdapter(ConsumeInfoActivity.this, mHjgwUidList);
			showPop(v, mHjgwAdapter, POS_HJGW);
			break;
		case R.id.ll_consume_info_tpds:
			mTpdsAdapter = new WaiterListAdapter(ConsumeInfoActivity.this, mTpdsUidList);
			showPop(v, mTpdsAdapter, POS_TPDS);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PIC:
			if (resultCode == RESULT_OK) {

				if (null == dialog) {
					dialog = new ProgressDialog(ConsumeInfoActivity.this);
					dialog.setCanceledOnTouchOutside(false);
				}
				dialog.show();
				new FaceVerifyTestTask().execute();
			}
			break;

		default:
			break;
		}
	}

	public void comfirmBuy(View view) {
		// "store_id":"6","oid":"776","code":"166823538211","a131":"128","a132":"0","a133":"0","a134":"0","a135":"0","action":"sy_xfyz"
		JSONObject vars = new JSONObject();
		try {
			vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
			vars.put("oid", mOid);
			vars.put("code", mCode);
			vars.put("a131", selectedFxsUid);
			vars.put("a132", selectedTrsUid);
			vars.put("a133", selectedZlUid);
			vars.put("a134", selectedHjgwUid);
			vars.put("a135", selectedTpdsUid);
			vars.put("action", "sy_xfyz");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null == dialog){
			dialog = new ProgressDialog(this);
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
		
		Intent intent = new Intent();
		intent.setAction(SiesonService.ACTION_COMFIREM_DEPOSIT);
		intent.putExtra(SiesonService.VARS, vars.toString());
		sendBroadcast(intent);
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			Log.d(TAG, "loadedImage.getWidth() ==>" + loadedImage.getWidth() + "\nloadedImage.getHeight(); == >"
					+ loadedImage.getHeight());
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}
	};
	public int validateCount = 3;

	private void showPop(View v, final WaiterListAdapter adapter, final int pos) {
		final LinearLayout ll = (LinearLayout) v;
		final ImageView imageView = (ImageView) ll.getChildAt(0);
		// int width = ImageView.getMeasuredWidth();
		// int height = ImageView.getMeasuredHeight();
		// Log.d(TAG, "width ==>"+width +" height ==>"+ll.getHeight());
		listPopupWindow = new ListPopupWindow(ConsumeInfoActivity.this);
		listPopupWindow.setAnchorView(v);
		listPopupWindow.setVerticalOffset(imageView.getPaddingLeft());
		listPopupWindow.setHeight(ll.getHeight() * 2);
		listPopupWindow.setAdapter(adapter);
		listPopupWindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String url = "";
				String name = "";
				String uid = "";
				try {
					url = adapter.getItem(position).getString("touxiang");
					name = adapter.getItem(position).getString("real_name");
					uid = adapter.getItem(position).getString("uid");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (TextUtils.isEmpty(url)) {
					switch (pos) {
					case POS_FXS:
						imageView.setImageResource(R.drawable.icon1);
						break;
					case POS_TRS:
						imageView.setImageResource(R.drawable.icon2);
						break;
					case POS_ZL:
						imageView.setImageResource(R.drawable.icon3);
						break;
					case POS_HJGW:
						imageView.setImageResource(R.drawable.icon4);
						break;
					case POS_TPDS:
						imageView.setImageResource(R.drawable.icon5);
						break;
					default:
						break;
					}
				} else {
					imageLoader.displayImage(IMAGE_URL_PRE + url, imageView, options, imageLoadingListener);
				}
				((TextView) ll.getChildAt(1)).setText(name);
				switch (pos) {
				case POS_FXS:
					selectedFxsUid = uid;
					break;
				case POS_HJGW:
					selectedHjgwUid = uid;
					break;
				case POS_TPDS:
					selectedTpdsUid = uid;
					break;
				case POS_TRS:
					selectedTrsUid = uid;
					break;
				case POS_ZL:
					selectedZlUid = uid;
					break;
				default:
					break;
				}
				listPopupWindow.dismiss();
				listPopupWindow = null;
			}
		});
		listPopupWindow.show();
	}

	class FaceVerifyTestTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject requestResult;

		@Override
		protected Boolean doInBackground(Void... arg0) {
			StringBuffer mySign = new StringBuffer("");
			MyApplication application = (MyApplication) getApplication();
			String appId = application.getAppId();
			QcloudFrSign.appSign(appId, application.getSecretId(), application.getSecretKey(),
					System.currentTimeMillis() / 1000 + application.getEXPIRED_SECONDS(), "", mySign);

			QcloudFrSDK sdk = new QcloudFrSDK(appId, mySign.toString());
			try {
				String image_path = Environment.getExternalStorageDirectory().getPath() + "/seisonImage"
						+ "/validate_0.jpg";
				JSONObject response = sdk.FaceVerify(image_path, mVipUid);
				// Log.v(TAG, response.toString());
				requestResult = response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, requestResult.toString());
			dialog.dismiss();
			try {
				if (!requestResult.getBoolean("ismatch")) {
					if (validateCount == 0) {
						finish();
					}
					AlertDialog.Builder builder = new Builder(ConsumeInfoActivity.this);
					builder.setTitle("认证失败");
					builder.setMessage("重新认证？最多还可以认证" + validateCount + "次");
					builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (null == ConsumeInfoActivity.this.dialog) {
								ConsumeInfoActivity.this.dialog = new ProgressDialog(ConsumeInfoActivity.this);
								ConsumeInfoActivity.this.dialog.setCanceledOnTouchOutside(false);
							}
							ConsumeInfoActivity.this.dialog.show();
							validateCount--;
							startTakePic();
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					builder.setCancelable(false);
					builder.create().show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Toast.makeText(ConsumeInfoActivity.this, requestResult,
			// Toast.LENGTH_LONG).show();
			// mTv.setText("请求结束\n" + requestResult);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mTv.setText("发起请求");
		}
	}

}
