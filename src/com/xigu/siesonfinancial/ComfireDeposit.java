package com.xigu.siesonfinancial;

import org.json.JSONException;
import org.json.JSONObject;

import com.xigu.siesonfinancial.service.SiesonService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComfireDeposit extends Activity {

	private static final String TAG = "ComfireDeposit";
	public static int RES_COMFIRED = 1;// 确认提款
	public static int RES_CANCELED = 0;// 取消提款

	private ProgressDialog dialog;
	private TextView mTvBankName;
	private TextView mTvBankCardNum;
	private TextView mTvTotalCount;
	private EditText mEtDeposit;
	private String mAmount = "";
	private String mBankCardNum = "";
	private String mBankName = "";
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("result");
			JSONObject resultJsonObject = null;
			try {
				resultJsonObject = new JSONObject(result);
				String msg = resultJsonObject.getString("message");
				showToast(msg);
//				if (0 == resultJsonObject.getInt("status")) {
//					ComfireDeposit.this.setResult(RES_COMFIRED);
//				}else{
//					ComfireDeposit.this.setResult(RES_CANCELED);
//				}
				finish();
			} catch (Exception e) {
				Log.e(TAG, "转换json数据出错 : " + result);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setFinishOnTouchOutside(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_deposit);
		if (null != getIntent()) {
			Intent intent = getIntent();
			mAmount = intent.getStringExtra("amount");
			mBankName = intent.getStringExtra("bankname");
			mBankCardNum = intent.getStringExtra("cardname");
		}
		initViews();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SiesonService.ACTION_COMFIREM_DEPOSIT_RESULT);
		registerReceiver(mReceiver, filter);
	}

	private void initViews() {
		mTvTotalCount = (TextView) findViewById(R.id.total_count);
		mTvTotalCount.setText(mAmount);
		mTvBankName = (TextView) findViewById(R.id.bank_name);
		mTvBankName.setText(mBankName);
		mTvBankCardNum = (TextView) findViewById(R.id.bank_card_num);
		mTvBankCardNum.setText(mBankCardNum);
		mEtDeposit = (EditText) findViewById(R.id.deposit_conut);
		mEtDeposit.setHint("您本次最多可提取金额：￥" + mAmount);
	}

	public void comfireDepositNow(View view) {
		Log.d(TAG, "ComfireDepositNow");
		if (!TextUtils.isEmpty(mEtDeposit.getEditableText().toString().trim())) {
			int money = Integer.valueOf(mEtDeposit.getEditableText().toString().trim());
			int amount = Integer.valueOf(mAmount);
			if (money > 0 && money < amount) {
				JSONObject vars = new JSONObject();
				try {
					vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
					vars.put("amount", money);
					vars.put("action", "sy_mdtx");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.putExtra(SiesonService.VARS, vars.toString());
				intent.setAction(SiesonService.ACTION_COMFIREM_DEPOSIT);
				sendBroadcast(intent);
				if(null ==dialog){
					dialog = new ProgressDialog(ComfireDeposit.this);
					dialog.setCanceledOnTouchOutside(false);
				}
				dialog.show();
			} else {
				showToast("请检查提款金额");
			}
		}
		// setResult(RES_COMFIRED);
		// finish();
	}

	public void cancelDeposit(View view) {
		Log.d(TAG, "cancelDeposit");
//		setResult(RES_CANCELED);
		finish();
	}

	private void showToast(String msg) {
		Toast.makeText(ComfireDeposit.this, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
