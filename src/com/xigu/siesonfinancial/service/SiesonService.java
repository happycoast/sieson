package com.xigu.siesonfinancial.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SiesonService extends Service {
	private final String TAG = "kecc";
	// private final String TAG = getClass().getSimpleName();
	public static final String VARS = "vars";
	public static final String ACTION_CHECK_QRCODE = "com.siesion.action.CHECK_QRCODE";
	public static final String ACTION_CHECK_QRCODE_RESULT = "com.siesion.action.CHECK_QRCODE_RESULT";
	public static final String ACTION_GET_ORDER = "com.siesion.action.GET_ORDER";
	public static final String ACTION_GET_ORDER_RESULT = "com.siesion.action.GET_ORDERE_RESULT";
	public static final String ACTION_GET_FINANCIAL_DATA = "com.siesion.action.GET_FINANCIALDATA";
	public static final String ACTION_GET_FINANCIAL_DATA_RESULT = "com.siesion.action.GET_FINANCIALDATA_RESULT";
	public static final String ACTION_GET_CONUT_DATA = "com.siesion.action.GET_CONUT_DATA";
	public static final String ACTION_GET_CONUT_DATA_RESULT = "com.siesion.action.GET_CONUT_DATA_RESULT";
	public static final String ACTION_GET_MEMBER_DATA = "com.siesion.action.GET_MEMBER_DATA";
	public static final String ACTION_GET_MEMBER_DATA_RESULT = "com.siesion.action.GET_MEMBER_DATA_RESULT";
	public static final String ACTION_ADD_MEMBER = "com.siesion.action.ADD_MEMBER";
	public static final String ACTION_ADD_MEMBER_RESULT = "com.siesion.action.ADD_MEMBER_RESULT";
	public static final String ACTION_VALIDATE_MEMBER = "com.siesion.action.VALIDATE_MEMBER";
	public static final String ACTION_VALIDATE_MEMBER_RESULT = "com.siesion.action.VALIDATE_MEMBER_RESULT";
	public static final String ACTION_COMFIREM_DEPOSIT = "com.siesion.action.COMFIREM_DEPOSIT";
	public static final String ACTION_COMFIREM_DEPOSIT_RESULT = "com.siesion.action.COMFIREM_DEPOSIT_RESULT";
	public static final String ACTION_COMFIREM_BUY = "com.siesion.action.COMFIREM_BUY";
	public static final String ACTION_COMFIREM_BUY_RESULT = "com.siesion.action.COMFIREM_BUY_RESULT";
	public static final String ACTION_GET_WAITER = "com.siesion.action.GET_WAITER";
	public static final String ACTION_GET_WAITER_RESULT = "com.siesion.action.GET_WAITER_RESULT";
	// private static final String ADDRESS_QRCODE =
	// "http://115.28.38.247:8094/index.php/Home/Index/search";
	private static final String ADDRESS_QRCODE = "http://sieson.whhxrc.com/api/api.mingfa.php/?appv=2.0.5&protocolv=2.0&version=v2";
	public static final int CHECK_QRCODE_RESULT = 1;
	public static final int GET_ORDER_RESULT = 2;
	public static final int GET_FINANCIALDATA_RESULT = 3;
	public static final int GET_VALIDATE_DATA_RESULT = 4;
	public static final int GET_CONUT_DATA_RESULT = 5;
	public static final int COMFIREM_DEPOSIT_RESULT = 6;
	public static final int ADD_MEMBER_RESULT = 7;
	public static final int VALIDATE_MEMBER_RESULT = 8;
	public static final int COMFIREM_BUY_RESULT = 9;
	public static final int GET_WAITER_RESULT = 10;

	private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case CHECK_QRCODE_RESULT:
				intent.setAction(ACTION_CHECK_QRCODE_RESULT);
				break;
			case GET_ORDER_RESULT:
				intent.setAction(ACTION_GET_ORDER_RESULT);
				break;
			case GET_FINANCIALDATA_RESULT:
				intent.setAction(ACTION_GET_FINANCIAL_DATA_RESULT);
				break;
			case GET_CONUT_DATA_RESULT:
				intent.setAction(ACTION_GET_CONUT_DATA_RESULT);
				break;
			case GET_VALIDATE_DATA_RESULT:
				intent.setAction(ACTION_GET_MEMBER_DATA_RESULT);
				break;
			case ADD_MEMBER_RESULT:
				intent.setAction(ACTION_ADD_MEMBER_RESULT);
				break;
			case VALIDATE_MEMBER_RESULT:
				intent.setAction(ACTION_VALIDATE_MEMBER_RESULT);
				break;
			case COMFIREM_DEPOSIT_RESULT:
				intent.setAction(ACTION_COMFIREM_DEPOSIT_RESULT);
				break;
			case COMFIREM_BUY_RESULT:
				intent.setAction(ACTION_COMFIREM_BUY_RESULT);
				break;
			case GET_WAITER_RESULT:
				intent.setAction(ACTION_GET_WAITER_RESULT);
				break;
			default:
				break;
			}
			intent.putExtra("result", (String) msg.obj);
			sendBroadcast(intent);
		};
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());
			if (ACTION_CHECK_QRCODE.equals(intent.getAction())) {
				handleCheckQrCode(intent);
			}
			if (ACTION_GET_ORDER.equals(intent.getAction())) {
				handleGetOrder(intent);
			}
			if (ACTION_GET_FINANCIAL_DATA.equals(intent.getAction())) {
				handleGetFinancialData(intent);
			}
			if (ACTION_GET_CONUT_DATA.equals(intent.getAction())) {
				handleGetConutData(intent);
			}
			if (ACTION_GET_MEMBER_DATA.equals(intent.getAction())) {
				handleGetMemberData(intent);
			}
			if (ACTION_COMFIREM_DEPOSIT.equals(intent.getAction())) {
				handleComfirmeDeposit(intent);
			}
			if (ACTION_COMFIREM_BUY.equals(intent.getAction())) {
				handleComfirmeBuy(intent);
			}
			if (ACTION_ADD_MEMBER.equals(intent.getAction())) {
				handleAddMember(intent);
			}
			if (ACTION_VALIDATE_MEMBER.equals(intent.getAction())) {
				handleValidateMember(intent);
			}
			if (ACTION_GET_WAITER.equals(intent.getAction())) {
				handleGetWaiter(intent);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
	}

	protected void handleGetWaiter(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), GET_WAITER_RESULT);
	}

	protected void handleComfirmeBuy(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), COMFIREM_BUY_RESULT);
	}

	protected void handleValidateMember(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), VALIDATE_MEMBER_RESULT);
	}

	protected void handleAddMember(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), ADD_MEMBER_RESULT);
	}

	private void handleGetMemberData(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), GET_VALIDATE_DATA_RESULT);
	}

	private void handleGetFinancialData(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), GET_FINANCIALDATA_RESULT);
	}

	private void handleGetConutData(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), GET_CONUT_DATA_RESULT);
	}

	private void handleGetOrder(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), GET_ORDER_RESULT);
	}

	private void handleCheckQrCode(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), CHECK_QRCODE_RESULT);
	}

	private void handleComfirmeDeposit(Intent intent) {
		startNetwork(intent.getStringExtra(VARS), COMFIREM_DEPOSIT_RESULT);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(Integer.MAX_VALUE);
		intentFilter.addAction(ACTION_CHECK_QRCODE);
		intentFilter.addAction(ACTION_GET_ORDER);
		intentFilter.addAction(ACTION_GET_FINANCIAL_DATA);
		intentFilter.addAction(ACTION_GET_CONUT_DATA);
		intentFilter.addAction(ACTION_GET_MEMBER_DATA);
		intentFilter.addAction(ACTION_COMFIREM_DEPOSIT);
		intentFilter.addAction(ACTION_COMFIREM_BUY);
		intentFilter.addAction(ACTION_ADD_MEMBER);
		intentFilter.addAction(ACTION_VALIDATE_MEMBER);
		intentFilter.addAction(ACTION_GET_WAITER);
		registerReceiver(mReceiver, intentFilter);
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(mReceiver);
		stopSelf();
	}

	private void startNetwork(final String vars, final int req) {
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(ADDRESS_QRCODE + "&vars=" + vars);
					Log.d(TAG, url.toString());
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					// set header
					connection.setRequestMethod("GET");
					connection.connect();
					// 读取响应
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String lines;
					StringBuffer resposeBuffer = new StringBuffer("");
					while ((lines = reader.readLine()) != null) {
						lines = new String(lines.getBytes(), "utf-8");
						resposeBuffer.append(lines);
					}
					reader.close();
					// 断开连接
					connection.disconnect();
					Message message = mHandler.obtainMessage(req);
					message.obj = resposeBuffer.toString();
					mHandler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "网络请求失败~");
				}
			}
		});
	}
}
