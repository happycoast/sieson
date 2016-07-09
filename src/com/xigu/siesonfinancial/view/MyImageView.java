package com.xigu.siesonfinancial.view;

/*
 * MyView.java
 * 
 * [AUTHOR]: Chunyen Liu
 * [SDK   ]: Android SDK 2.1 and up
 * [NOTE  ]: developer.com tutorial, "Face Detection with Android APIs"
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyImageView extends View {
    private Canvas mCanvas;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mDisplayStyle = 0;
    private int[] mPX = null;
    private int[] mPY = null;
    private int mPaintColor = 0x80ff0000;

    public MyImageView(Context c) {
        super(c);
        init();
    }

    public MyImageView(Context c, AttributeSet attrs) {
        super(c, attrs);
        init();
    }

    private void init() {

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mPaintColor);
        mPaint.setStrokeWidth(3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // set up detected face features for display
    public void setDisplayPoints(int[] xx, int[] yy, int total, int style) {
        mDisplayStyle = style;
        mPX = null;
        mPY = null;

        if (xx != null && yy != null && total > 0) {
            mPX = new int[total];
            mPY = new int[total];

            for (int i = 0; i < total; i++) {
                mPX[i] = xx[i];
                mPY[i] = yy[i];
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // if (mBitmap != null) {
        // canvas.drawBitmap(mBitmap, 0, 0, null);

        if (mPX != null && mPY != null) {
            for (int i = 0; i < mPX.length; i++) {
                if (mDisplayStyle == 1) {
                    canvas.drawCircle(mPX[i], mPY[i], 10.0f, mPaint);
                } else {
                    canvas.drawRect(mPX[i] - 20, mPY[i] - 20, mPX[i] + 20, mPY[i] + 20, mPaint);
                }
            }
        }
        // }else{
        // mBitmap=Bitmap.createBitmap(getWidth(), getHeight(),
        // Bitmap.Config.RGB_565);
        // }
    }
}