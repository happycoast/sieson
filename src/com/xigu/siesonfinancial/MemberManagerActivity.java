package com.xigu.siesonfinancial;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.qcloudfr_android_sdk.QcloudFrSDK;
import com.qcloud.qcloudfr_android_sdk.sign.QcloudFrSign;
import com.xigu.siesonfinancial.service.SiesonService;
import com.xigu.siesonfinancial.view.MemberAdapter.IMemberOprateListener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MemberManagerActivity extends Activity
		implements OnClickListener, IListViewLoadListener, IMemberOprateListener {
	private static final String TAG = "kecc";
	public static final int REQ_TAKE_PIC = 10;

	// public static final String ACTION_VALIDATE_FACE =
	// "com.siesion.action.ACTION_VALIDATE_FACE";
	public static final int TYPE_ALL = 0;
	public static final int TYPE_VALIDATE = 1;
	public static final int TYPE_UNVALIDATE = 2;
	private ArrayList<String> gruops_ids = new ArrayList<String>();

	private ProgressDialog dialog;
	private TextView mTvAll, mTvValidate, mTvUnvalidate;
	private EditText mEtSearch;
	private ArrayList<JSONObject> mAllList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mValidateList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mUnValidateList = new ArrayList<JSONObject>();
	private MemberManageFrag mAllFrag;
	private MemberManageFrag mValidatedFrag;
	private MemberManageFrag mUnValidatedFrag;
	private String status = "0";

	private FragmentManager mFM;
	private Intent querryIntent;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (SiesonService.ACTION_GET_MEMBER_DATA_RESULT.equals(intent.getAction())) {
				handlGetMemberDateResult(intent);
			}
			if (SiesonService.ACTION_ADD_MEMBER_RESULT.equals(intent.getAction())) {
				handlAddMemberResult(intent);
			}
			if (SiesonService.ACTION_VALIDATE_MEMBER_RESULT.equals(intent.getAction())) {
				handlValidateMemberResult(intent);
			}
		}
	};

	private String searchKey = "";
	private String validateName = "";
	private String validateUid = "-1";
	private String validateGroupId = "";
	private boolean canLoadMore = true;
	private int page = 1;
	private String mUsername;
	private String mUid;
	private String mGroup;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_member_manage);
		querryIntent = new Intent();
		querryIntent.setAction(SiesonService.ACTION_GET_MEMBER_DATA);
		try {
			JSONObject vars = new JSONObject();
			vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
			vars.put("action", "sy_hysj");
			querryIntent.putExtra(SiesonService.VARS, vars.toString());
		} catch (Exception e) {
		}
		initeViews();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SiesonService.ACTION_GET_MEMBER_DATA_RESULT);
		intentFilter.addAction(SiesonService.ACTION_ADD_MEMBER_RESULT);
		registerReceiver(receiver, intentFilter);
	}

	protected void handlValidateMemberResult(Intent intent) {
		dialog.dismiss();
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		Log.d(TAG, result);
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
				selectFragment(TYPE_VALIDATE);
			}
			showtoast(resultJsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void handlAddMemberResult(Intent intent) {
		dialog.dismiss();
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		Log.d(TAG, result);
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
				selectFragment(TYPE_ALL);
			}
			showtoast(resultJsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void startScanQR() {
		Log.d(TAG, "StartScanQR");
		Intent intent = new Intent(MemberManagerActivity.this, TakePictureActivity.class);
		startActivityForResult(intent, REQ_TAKE_PIC);
	}

	private void initeViews() {
		gruops_ids.add("0");
		mFM = getFragmentManager();
		mTvAll = (TextView) findViewById(R.id.tv_mem_all);
		mTvValidate = (TextView) findViewById(R.id.tv_mem_validated);
		mTvUnvalidate = (TextView) findViewById(R.id.tv_mem_unvalidated);
		mEtSearch = (EditText) findViewById(R.id.member_manage_search_key);
		mTvAll.setOnClickListener(this);
		mTvValidate.setOnClickListener(this);
		mTvUnvalidate.setOnClickListener(this);
		selectFragment(TYPE_ALL);
	}

	protected void handlGetMemberDateResult(Intent intent) {
		String result = intent.getStringExtra("result");
		JSONObject resultJsonObject = null;
		Log.d(TAG, result);
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
						mAllFrag.getDataList().addAll(temp);
					} else {
						mAllFrag.getDataList().removeAll(mAllFrag.getDataList());
						mAllFrag.getDataList().addAll(temp);
						mAllFrag.getAdapter().setIMemberOprateListener(this);
					}
					mAllFrag.getAdapter().notifyDataSetChanged();
				} else if ("1".equals(status)) {
					if (page > 1) {
						mValidatedFrag.getDataList().addAll(temp);
					} else {
						mValidatedFrag.getDataList().removeAll(mValidatedFrag.getDataList());
						mValidatedFrag.getDataList().addAll(temp);
						mValidatedFrag.getAdapter().setIMemberOprateListener(this);
					}
					mValidatedFrag.getAdapter().notifyDataSetChanged();
				} else if ("2".equals(status)) {
					if (page > 1) {
						mUnValidatedFrag.getDataList().addAll(temp);
					} else {
						mUnValidatedFrag.getDataList().removeAll(mUnValidatedFrag.getDataList());
						mUnValidatedFrag.getDataList().addAll(temp);
						mUnValidatedFrag.getAdapter().setIMemberOprateListener(this);
					}
					mUnValidatedFrag.getAdapter().notifyDataSetChanged();
				}
			} else {
				canLoadMore = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void selectFragment(int index) {
		FragmentTransaction transaction = mFM.beginTransaction();
		switch (index) {
		case TYPE_ALL:
			if (mAllFrag == null) {
				mAllFrag = new MemberManageFrag(TYPE_ALL, mAllList);
				mAllFrag.setListViewLoadListener(this);
				// mAllFrag.getAdapter().setIMemberOprateListener(this);
			}
			transaction.replace(R.id.member_manage_content, mAllFrag);
			break;
		case TYPE_VALIDATE:
			if (mValidatedFrag == null) {
				mValidatedFrag = new MemberManageFrag(TYPE_VALIDATE, mValidateList);
				mValidatedFrag.setListViewLoadListener(this);
				// mValidatedFrag.getAdapter().setIMemberOprateListener(this);
			}
			transaction.replace(R.id.member_manage_content, mValidatedFrag);
			break;
		case TYPE_UNVALIDATE:
			if (mUnValidatedFrag == null) {
				mUnValidatedFrag = new MemberManageFrag(TYPE_UNVALIDATE, mUnValidateList);
				mUnValidatedFrag.setListViewLoadListener(this);
				// mValidatedFrag.getAdapter().setIMemberOprateListener(this);
			}
			transaction.replace(R.id.member_manage_content, mUnValidatedFrag);
			break;
		default:
			break;
		}
		status = index + "";
		resetBg(index);
		transaction.commit();
		querryData(status, searchKey, "1");
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

	private void resetBg(int index) {
		page = 1;
		canLoadMore = true;
		switch (index) {
		case TYPE_ALL:
			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_selected);
			mTvValidate.setBackgroundResource(R.drawable.bg_tab_middle_normal);
			mTvUnvalidate.setBackgroundResource(R.drawable.bg_tab_right_corner_normal);
			break;
		case TYPE_VALIDATE:
			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_normal);
			mTvValidate.setBackgroundResource(R.drawable.bg_tab_middle_selected);
			mTvUnvalidate.setBackgroundResource(R.drawable.bg_tab_right_corner_normal);
			break;
		case TYPE_UNVALIDATE:
			mTvAll.setBackgroundResource(R.drawable.bg_tab_left_corner_normal);
			mTvValidate.setBackgroundResource(R.drawable.bg_tab_middle_normal);
			mTvUnvalidate.setBackgroundResource(R.drawable.bg_tab_right_corner_selected);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_mem_all:
			selectFragment(TYPE_ALL);
			break;
		case R.id.tv_mem_validated:
			selectFragment(TYPE_VALIDATE);
			break;
		case R.id.tv_mem_unvalidated:
			selectFragment(TYPE_UNVALIDATE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// if(null != mAllFrag){
		// mAllFrag.getAdapter().setIMemberOprateListener(this);
		// }
		// if(null != mValidatedFrag){
		// mValidatedFrag.getAdapter().setIMemberOprateListener(this);
		// }
		// if(null != mUnValidatedFrag){
		// mValidatedFrag.getAdapter().setIMemberOprateListener(this);
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_TAKE_PIC:
			if (resultCode == Activity.RESULT_OK) {
				if (null == dialog) {
					dialog = new ProgressDialog(this);
					dialog.setCanceledOnTouchOutside(false);
				}
				dialog.show();
				new NewPersonTestTask().execute();
			}
			break;

		default:
			break;
		}
	}

	class NewPersonTestTask extends AsyncTask<Void, Void, Boolean> {
		String requestResult = "";

		@Override
		protected Boolean doInBackground(Void... arg0) {
			StringBuffer mySign = new StringBuffer("");
			MyApplication application = (MyApplication) getApplication();
			String appId = application.getAppId();
			QcloudFrSign.appSign(appId, application.getSecretId(), application.getSecretKey(),
					System.currentTimeMillis() / 1000 + application.getEXPIRED_SECONDS(), "", mySign);

			QcloudFrSDK sdk = new QcloudFrSDK(appId, mySign.toString());
			try {
				int picLoc = 0;
				JSONObject response = null;
				for (int i = 0; i < 3; i++) {

					String image_path = Environment.getExternalStorageDirectory().getPath() + "/seisonImage"
							+ "/validate_" + picLoc + ".jpg";
					// JSONObject response = sdk.FaceVerify(image_path,
					// validateUid);
					response = sdk.NewPerson(image_path, validateUid, gruops_ids, validateName);
					if (0 == response.getInt("errorcode")) {
						// TODO
						// 将服务器返回人脸信息录入数据库
						add2database(response);
						break;
					} else if (-1101 == response.getInt("errorcode")) {
						continue;
					} else {
						dialog.dismiss();
						showtoast(response.getString("errormsg"));
						break;
					}
				}
				requestResult = response.toString();
			} catch (IOException e) {
				e.printStackTrace();
				requestResult = e.getMessage();
			} catch (JSONException e) {
				e.printStackTrace();
				requestResult = e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, requestResult);
			// Toast.makeText(MemberManagerActivity.this, requestResult,
			// Toast.LENGTH_LONG).show();
			// mTv.setText("请求结束\n" + requestResult);
			// new FaceVerifyTestTask().execute();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mTv.setText("发起请求");
		}
	}

	class addPersonPicTask extends AsyncTask<Void, Void, Boolean> {
		String requestResult = "";

		@Override
		protected Boolean doInBackground(Void... arg0) {
			StringBuffer mySign = new StringBuffer("");
			MyApplication application = (MyApplication) getApplication();
			String appId = application.getAppId();
			QcloudFrSign.appSign(appId, application.getSecretId(), application.getSecretKey(),
					System.currentTimeMillis() / 1000 + application.getEXPIRED_SECONDS(), "", mySign);

			QcloudFrSDK sdk = new QcloudFrSDK(appId, mySign.toString());
			try {
				String image_path0 = Environment.getExternalStorageDirectory().getPath() + "/seisonImage"
						+ "/validate_0.jpg";
				String image_path1 = Environment.getExternalStorageDirectory().getPath() + "/seisonImage"
						+ "/validate_1.jpg";
				String image_path2 = Environment.getExternalStorageDirectory().getPath() + "/seisonImage"
						+ "/validate_2.jpg";
				// JSONObject response = sdk.FaceVerify(image_path,
				// validateUid);
				// JSONObject response = sdk.NewPerson(image_path, validateUid,
				// gruops_ids, validateName);
				ArrayList<String> image_path_arr = new ArrayList<String>();
				image_path_arr.add(image_path0);
				image_path_arr.add(image_path1);
				image_path_arr.add(image_path2);
				JSONObject response = sdk.AddFace(validateUid, image_path_arr);
				Log.v(TAG, response.toString());
				requestResult = response.toString();
			} catch (IOException e) {
				e.printStackTrace();
				requestResult = e.getMessage();
			} catch (JSONException e) {
				e.printStackTrace();
				requestResult = e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, requestResult);
			Toast.makeText(MemberManagerActivity.this, requestResult, Toast.LENGTH_LONG).show();
			// mTv.setText("请求结束\n" + requestResult);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mTv.setText("发起请求");
		}
	}

	@Override
	public void loadMore(int type) {
		if (canLoadMore) {
			page++;
			querryData(status, searchKey, page + "");
		}
	}

	public void add2database(JSONObject response) {
		// TODO Auto-generated method stub
		JSONObject vars = new JSONObject();
		// JSONArray renlian = new JSONArray();
		try {
			vars.put("uid", validateUid);
			vars.put("renlian", response.getString("face_id"));
			vars.put("cloud_user_id", validateUid);
			vars.put("action", "sy_mdrllr");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.setAction(SiesonService.ACTION_ADD_MEMBER);
		intent.putExtra(SiesonService.VARS, vars.toString());
		sendBroadcast(intent);
	}

	@Override
	public void upgradeMember(String username, String uid) {
		// TODO Auto-generated method stub
		showtoast("upgradeMember");
	}

	@Override
	public void validateMember(String username, String uid) {
		// TODO Auto-generated method stub
		// showtoast("validateMember");
		if (null == dialog) {
			dialog = new ProgressDialog(this);
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
		JSONObject vars = new JSONObject();
		try {
			vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
			vars.put("uid", uid);
			vars.put("action", "sy_mdhjsh");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.setAction(SiesonService.ACTION_VALIDATE_MEMBER);
		intent.putExtra(SiesonService.VARS, vars.toString());
		sendBroadcast(intent);
	}

	@Override
	public void validateMemberrFace(String username, String uid) {
		// TODO Auto-generated method stub
		// showtoast("validateMemberrFace");
		validateName = username;
		validateUid = uid;
		gruops_ids.removeAll(gruops_ids);
		gruops_ids.add(((MyApplication) getApplication()).getStoreId());
		Intent intent = new Intent(MemberManagerActivity.this, TakePictureActivity.class);
		intent.putExtra("take_count", 3);
		startActivityForResult(intent, REQ_TAKE_PIC);
	}

	private void showtoast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
