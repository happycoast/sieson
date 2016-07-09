package com.xigu.siesonfinancial;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class MyHandler extends Handler {
    private final WeakReference<Activity> mActivity;

    public MyHandler(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mActivity.get() == null) {
            return;
        }
        super.handleMessage(msg);
    }
}
