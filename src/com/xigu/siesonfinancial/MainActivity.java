package com.xigu.siesonfinancial;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.xigu.siesonfinancial.service.SiesonService;
import com.xigu.siesonfinancial.view.MyDialog;
import com.zxing.activity.CaptureActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener {
    // private final String TAG = getClass().getSimpleName();
    private final String TAG = "kecc";
    public static final int REQ_TAKE_PIC = 10;
    public static final int REQ_START_QR = 20;
    public static final int REQ_BUY = 30;
    // public static final int DIALOG_

    private Intent mServiceIntent = null;
    private Handler mHandler = new MyHandler(MainActivity.this) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handlerTodo(msg);
        };
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SiesonService.ACTION_CHECK_QRCODE_RESULT.equals(intent.getAction())) {
                handlCheckQrCodeResult(intent);
            }
        }
    };
    private EditText mEtQrcode;
    private String mScanResultImgPath = "";
    private String mOrderNum = "";
    private Dialog mDialog = null;

    public void handlerTodo(Message msg) {
        switch (msg.what) {
        case SiesonService.CHECK_QRCODE_RESULT:
            break;
        default:
            break;
        }
    }

    private void handlCheckQrCodeResult(Intent intent) {
        Log.e(TAG, "onReceive : " + intent.getAction());
        if (mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        // mEtQrcode.getText().clear();
        String result = intent.getStringExtra("result");
        JSONObject resultJsonObject = null;
        try {
            resultJsonObject = new JSONObject(result);
        } catch (Exception e) {
            Log.e(TAG, "Jsonz字符串转换异常 : " + result);
        }
        if (null == resultJsonObject) {
            return;
        }
        int status = -1;
        String message = "";
        String oid = "";
        String sor = "";
        try {
            status = resultJsonObject.getInt("status");
            message = resultJsonObject.getString("message");
            if (0 == status) {
                oid = resultJsonObject.getString("oid");
                sor = resultJsonObject.getString("sor");
                // mOrderNum = result;
                Log.d(TAG, "oid == " + oid);
                JSONObject vars = new JSONObject();
                vars.put("oid", oid);
                vars.put("sor", sor);
                vars.put("action", "sy_ddyz");
                Log.d(TAG, result);
                // 跳转消费页面
                Intent consumeInfoActivityIntent = new Intent(MainActivity.this, ConsumeInfoActivity.class);
                consumeInfoActivityIntent.putExtra(SiesonService.VARS, vars.toString());
                startActivityForResult(consumeInfoActivityIntent, 11);
            } else if (1 == status) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initeViews();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SiesonService.ACTION_CHECK_QRCODE_RESULT);
        registerReceiver(mReceiver, intentFilter);
    }

    private void initeViews() {
        mEtQrcode = (EditText) findViewById(R.id.et_qrcode);
        findViewById(R.id.num_0).setOnClickListener(this);
        findViewById(R.id.num_1).setOnClickListener(this);
        findViewById(R.id.num_2).setOnClickListener(this);
        findViewById(R.id.num_3).setOnClickListener(this);
        findViewById(R.id.num_4).setOnClickListener(this);
        findViewById(R.id.num_5).setOnClickListener(this);
        findViewById(R.id.num_6).setOnClickListener(this);
        findViewById(R.id.num_7).setOnClickListener(this);
        findViewById(R.id.num_8).setOnClickListener(this);
        findViewById(R.id.num_9).setOnClickListener(this);
        findViewById(R.id.num_del).setOnClickListener(this);
        findViewById(R.id.num_enter).setOnClickListener(this);
        mServiceIntent = new Intent(MainActivity.this, SiesonService.class);
        startService(mServiceIntent);
    }

    public void startScanQR() {
        Log.d(TAG, "StartScanQR");
        Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
        startActivityForResult(intent, REQ_TAKE_PIC);
    }

    private File generateFile() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检查SD卡挂载状态
            return null;
        }
        String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String image_path = Environment.getExternalStorageDirectory().getPath() + "/seisonImage";
        File file = new File(image_path);
        if (!file.exists()) {
            file.mkdirs();// 创建文件
        }
        // String fileName = image_path + File.pathSeparator + name;
        mScanResultImgPath = image_path + "/" + name;
        return new File(mScanResultImgPath);
    }

    public void validateUser() {
        Log.d(TAG, "ValidateUser");
        // new TestTask().execute();
    }

    public void userManage(View view) {
        // TODO
        Intent Intent = new Intent(MainActivity.this, MemberManagerActivity.class);
        startActivity(Intent);
    }

    public void startFinancialData(View view) {
        Log.d(TAG, "startFinancialData");
        Intent intent = new Intent(MainActivity.this, FinancialDataActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO
        switch (requestCode) {
        case REQ_TAKE_PIC:
            if (resultCode == Activity.RESULT_OK) {
                //
            }
            break;
        case REQ_BUY:
            if (resultCode == RESULT_OK) {
                mEtQrcode.getEditableText().clear();
            }
            break;
        case REQ_START_QR:
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("result");
                mEtQrcode.getEditableText().clear();
                mEtQrcode.setText(result);
            }
            break;
        default:
            break;
        }
    }

    private void enter() {
        if (mEtQrcode.length() != 0) {
//            switch (Integer.valueOf(mEtQrcode.getEditableText().toString().trim())) {
//            case 101:
//                showSuccess();
//                return;
//            default:
//                break;
//            }

            if (null == mDialog) {
                mDialog = new ProgressDialog(MainActivity.this);
            }
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            JSONObject vars = new JSONObject();
            try {
                vars.put("store_id", ((MyApplication) getApplication()).getStoreId());
                vars.put("code", mEtQrcode.getEditableText().toString().trim());
                vars.put("action", "sy_ddcx");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("kecc", vars.toString());
            Intent checkQrCodeIntent = new Intent();
            checkQrCodeIntent.setAction(SiesonService.ACTION_CHECK_QRCODE);
            checkQrCodeIntent.putExtra(SiesonService.VARS, vars.toString());
            sendBroadcast(checkQrCodeIntent);
        } else {
            showtoast("验证码输入为空，请检查");
        }
    }
    private void showSuccess() {
        ObjectAnimator animator = new ObjectAnimator();
        AlertDialog.Builder builder = new Builder(this);
        final AlertDialog alertDialog = builder.create();
        ImageView imageView = new ImageView(this);
        imageView.measure(getResources().getDrawable(R.drawable.success).getIntrinsicWidth(),
                getResources().getDrawable(R.drawable.success).getIntrinsicHeight());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true ;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.success, options);
        imageView.setImageResource(R.drawable.success);
        animator.ofFloat(imageView, "scaleX", 0.0f, 1.0f).setDuration(618).start();
        animator.ofFloat(imageView, "scaleY", 0.0f, 1.0f).setDuration(618).start();
        animator.ofFloat(imageView, "alpha", 0.0f, 1.0f).setDuration(618).start();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(imageView);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (options.outHeight * 3); // 高度设置为屏幕的0.6
        p.width = (int) (options.outWidth * 3); // 宽度设置为屏幕的0.65
        window.setAttributes(p);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                finish();
            }
        }, 2618) ;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        // int keyCode = event.getKeyCode();
        // Log.d(TAG, keyCode + "");
        // if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode ==
        // KeyEvent.KEYCODE_ENTER) {
        // enter();
        // return true;
        // }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.num_0:
            pressed(0);
            break;
        case R.id.num_1:
            pressed(1);
            break;
        case R.id.num_2:
            pressed(2);
            break;
        case R.id.num_3:
            pressed(3);
            break;
        case R.id.num_4:
            pressed(4);
            break;
        case R.id.num_5:
            pressed(5);
            break;
        case R.id.num_6:
            pressed(6);
            break;
        case R.id.num_7:
            pressed(7);
            break;
        case R.id.num_8:
            pressed(8);
            break;
        case R.id.num_9:
            pressed(9);
            break;
        case R.id.num_del:
            pressed(10);
            break;
        case R.id.num_enter:
            enter();
            break;
        default:
            break;
        }
    }

    private void pressed(int i) {
        Editable text = mEtQrcode.getText();
        if (i == 10) {
            int lenght = text.length();
            if (lenght == 0) {
                return;
            } else if (lenght == 1) {
                text.clear();
            } else {
                text.delete(lenght - 1, lenght);
            }
        } else {
            text.append(i + "");
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
        stopService(mServiceIntent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:

            break;
        case MotionEvent.ACTION_MOVE:

            break;
        case MotionEvent.ACTION_UP:

            break;
        default:
            break;
        }
        return false;
    }

    public void startScanQR(View view) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQ_START_QR);
    }

    private void showtoast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
