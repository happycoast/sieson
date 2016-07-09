package com.xigu.siesonfinancial;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xigu.siesonfinancial.service.SiesonService;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class FinancialDataActivity extends Activity implements OnClickListener, IListViewLoadListener {
	// private static final String TAG = "FinancialDataActivity";
	private static final String TAG = "kecc";
	public static final String DEPOSIT_DATA = "deposit_data";
	public static final int REQ_DEPOSIT_COMFIRE = 20;

	public static final int TYPE_ALL = 0;
	public static final int TYPE_CONSUMED = 1;
	public static final int TYPE_UNSONSUMED = 2;

	private TextView mTvAll, mTvConsumed, mTvUnconsumed, mTvAmount;
	private EditText mEtSearch;
	private ArrayList<JSONObject> mAllList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mConsumedList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mUnConsumedList = new ArrayList<JSONObject>();
	private ConsumeFrag mAllFrag;
	private ConsumeFrag mConsumedFrag;
	private ConsumeFrag mUnConsumedFrag;
	private String status = "0";

	private FragmentManager mFM;
	private Intent querryIntent;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (SiesonService.ACTION_GET_FINANCIAL_DATA_RESULT.equals(intent.getAction())) {
				handlGetFinancialDataResult(intent);
			}
			if (SiesonService.ACTION_GET_CONUT_DATA_RESULT.equals(intent.getAction())) {
				handlGetConutDataResult(intent);
			}
		}
	};

	private String searchKey = "";
	private boolean isQuerryingConut;
	private String mAmount = "";
	private String mBankCardNum = "";
	private String mBankName = "";
	private int page = 1;
	private boolean canLoadMore = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.financial_data);
		querryIntent = new Intent();
		querryIntent.setAction(SiesonService.ACTION_GET_FINANCIAL_DATA);
		try {
			JSONObject vars = new JSONObject();
			vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
			vars.put("action", "sy_mdsj");
			querryIntent.putExtra(SiesonService.VARS, vars.toString());
		} catch (Exception e) {
		}
		initeViews();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SiesonService.ACTION_GET_FINANCIAL_DATA_RESULT);
		intentFilter.addAction(SiesonService.ACTION_GET_CONUT_DATA_RESULT);
		registerReceiver(receiver, intentFilter);
	}

	protected void handlGetConutDataResult(Intent intent) {
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
			isQuerryingConut = false;
			if (0 == resultJsonObject.getInt("status")) {
				JSONObject data = resultJsonObject.getJSONObject("data");
				mAmount = data.getString("amount");
				mBankCardNum = data.getString("yhkh");
				mBankName = data.getString("yhmc");
				mTvAmount.setText(mAmount);
			}
		} catch (Exception e) {
			Log.e(TAG, "转换json数据出错 : " + result);
		}
	}

	private void initeViews() {
		mTvAmount = (TextView) findViewById(R.id.amount);
		mFM = getFragmentManager();
		mTvAll = (TextView) findViewById(R.id.tv_all);
		mTvConsumed = (TextView) findViewById(R.id.tv_consumed);
		mTvUnconsumed = (TextView) findViewById(R.id.tv_unconsumed);
		mEtSearch = (EditText) findViewById(R.id.financial_data_search_key);
		mTvAll.setOnClickListener(this);
		mTvConsumed.setOnClickListener(this);
		mTvUnconsumed.setOnClickListener(this);
		selectFragment(TYPE_ALL);
	}

	private void querryData(String status, String key, String page) {
		try {
			JSONObject vars = new JSONObject(querryIntent.getStringExtra(SiesonService.VARS));
			vars.put("status", status);
			vars.put("key", key);
			vars.put("page", page);
			querryIntent.putExtra(SiesonService.VARS, vars.toString());
		} catch (Exception e) {
		}
		sendBroadcast(querryIntent);
	}

	public void selectFragment(int index) {
		FragmentTransaction transaction = mFM.beginTransaction();
		switch (index) {
		case TYPE_ALL:
			if (mAllFrag == null) {
				mAllFrag = new ConsumeFrag(TYPE_ALL, mAllList);
				mAllFrag.setListViewLoadListener(this);
			}
			transaction.replace(R.id.consume_content, mAllFrag);
			break;
		case TYPE_CONSUMED:
			if (mConsumedFrag == null) {
				mConsumedFrag = new ConsumeFrag(TYPE_CONSUMED, mConsumedList);
				mConsumedFrag.setListViewLoadListener(this);
			}
			transaction.replace(R.id.consume_content, mConsumedFrag);
			break;
		case TYPE_UNSONSUMED:
			if (mUnConsumedFrag == null) {
				mUnConsumedFrag = new ConsumeFrag(TYPE_UNSONSUMED, mUnConsumedList);
				mUnConsumedFrag.setListViewLoadListener(this);
			}
			transaction.replace(R.id.consume_content, mUnConsumedFrag);
			break;
		default:
			break;
		}
		status = index + "";
		resetBg(index);
		transaction.commit();
		querryData(status, searchKey, page + "");
	}

	private void handlGetFinancialDataResult(Intent intent) {
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (Exception e) {
			Log.e(TAG, "转换json数据出错 : " + result);
		}
		if (null == resultJsonObject) {
			return;
		}
		try {
			if (0 == resultJsonObject.getInt("status")) {
				JSONArray jsonArray = resultJsonObject.getJSONArray("data");
				if (jsonArray.length() > 0) {
					canLoadMore = true;
				} else {
					canLoadMore = false;
				}
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				for (int i = 0; i < jsonArray.length(); i++) {
					temp.add(jsonArray.getJSONObject(i));
				}
				if ("0".equals(status)) {
					if (page > 1) {
						mAllFrag.getmDataList().addAll(temp);
					} else {
						mAllFrag.getmDataList().removeAll(mAllFrag.getmDataList());
						mAllFrag.getmDataList().addAll(temp);
					}
					mAllFrag.getAdapter().notifyDataSetChanged();
				} else if ("1".equals(status)) {
					if (page > 1) {
						mConsumedFrag.getmDataList().addAll(temp);
					} else {
						mConsumedFrag.getmDataList().removeAll(mConsumedFrag.getmDataList());
						mConsumedFrag.getmDataList().addAll(temp);
					}
					mConsumedFrag.getAdapter().notifyDataSetChanged();
				} else if ("2".equals(status)) {
					if (page > 1) {
						mUnConsumedFrag.getmDataList().addAll(temp);
					} else {
						mUnConsumedFrag.getmDataList().removeAll(mUnConsumedFrag.getmDataList());
						mUnConsumedFrag.getmDataList().addAll(temp);
					}
					mUnConsumedFrag.getAdapter().notifyDataSetChanged();
				}
			} else {
				canLoadMore = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void resetBg(int index) {
		page = 1;
		canLoadMore = true;
		switch (index) {
		case TYPE_ALL:
			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_selected);
			mTvConsumed.setBackgroundResource(R.drawable.bg_tab_middle_normal);
			mTvUnconsumed.setBackgroundResource(R.drawable.bg_tab_right_corner_normal);
			break;
		case TYPE_CONSUMED:

			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_normal);
			mTvConsumed.setBackgroundResource(R.drawable.bg_tab_middle_selected);
			mTvUnconsumed.setBackgroundResource(R.drawable.bg_tab_right_corner_normal);
			break;
		case TYPE_UNSONSUMED:

			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_normal);
			mTvConsumed.setBackgroundResource(R.drawable.bg_tab_middle_normal);
			mTvUnconsumed.setBackgroundResource(R.drawable.bg_tab_right_corner_selected);
			break;

		default:
			break;
		}
	}

	public void showComfireDeposit(View view) {
		Log.d(TAG, "ShowComfireDeposit");
		Intent intent = new Intent(FinancialDataActivity.this, ComfireDeposit.class);
		intent.putExtra("amount", mAmount);
		intent.putExtra("bankname", mBankName);
		intent.putExtra("cardname", mBankCardNum);
		startActivityForResult(intent, REQ_DEPOSIT_COMFIRE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_all:
			selectFragment(TYPE_ALL);
			break;
		case R.id.tv_consumed:
			selectFragment(TYPE_CONSUMED);
			break;
		case R.id.tv_unconsumed:
			selectFragment(TYPE_UNSONSUMED);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		querryConut();
	}

	private void querryConut() {
		if (isQuerryingConut) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(SiesonService.ACTION_GET_CONUT_DATA);
		JSONObject vars = new JSONObject();
		try {
			vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
			vars.put("action", "sy_mdye");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		intent.putExtra(SiesonService.VARS, vars.toString());
		isQuerryingConut = true;
		sendBroadcast(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void loadMore(int type) {
		if (canLoadMore) {
			page++;
			querryData(status, searchKey, page + "");
		}
	}
}
