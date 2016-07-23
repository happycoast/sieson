package com.xigu.siesonfinancial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * @author 柯常城 Email:happycoast1211@gmail.com 修改日期：2016年4月17日
 */
@SuppressLint("NewApi")
public class TakePictureActivity extends Activity {
	private static String TAG = "kecc";
	private SurfaceView mPreviewSurface;
	private SurfaceView mFaceSurface;
	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;
	// 缩放矩阵
	private Matrix mScaleMatrix = new Matrix();

	private Toast mToast;
	private long mLastClickTime;
	private boolean isTaking = false;
	private boolean isFocused = false;
	private int mTakeCount = 0;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mPaintColor = 0x80ff0000;
	private final int START_AUTO_FOCUS = 0;
	private int mTotakeConut = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START_AUTO_FOCUS:
				mCamera.startFaceDetection();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_detect);
		if (null != getIntent()) {
			mTotakeConut = getIntent().getIntExtra("take_count", 1);
		}
		initUI();
	}

	private void initUI() {

		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setColor(mPaintColor);
		mPaint.setStrokeWidth(3);

		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mFaceSurface.setZOrderOnTop(true);

		mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// 点击SurfaceView，切换摄相头
		mFaceSurface.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 只有一个摄相头，不支持切换
				if (Camera.getNumberOfCameras() == 1) {
					showTip("只有后置摄像头，不能切换");
					return;
				}
				closeCamera();
				if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
					mCameraId = CameraInfo.CAMERA_FACING_BACK;
				} else {
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
				}
				openCamera();
			}
		});

		// 长按SurfaceView 500ms后松开，摄相头聚集
		mFaceSurface.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastClickTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_UP:
					if (System.currentTimeMillis() - mLastClickTime > 500) {
						mCamera.autoFocus(null);
						return true;
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
		mToast = Toast.makeText(TakePictureActivity.this, "", Toast.LENGTH_SHORT);

	}

	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// initCamera(width, height);
			//实现自动对焦  
            mCamera.autoFocus(mAutoFocusCallback); 
		}
	};
	private boolean isInit;

	private void initCamera(int width, int height) {
		if (!isInit) {
			// viewWidth = width;
			// viewHeight = height;
			isInit = true;
			mCamera.autoFocus(null);
			Camera.Parameters parameters = mCamera.getParameters();
			// 摄像头旋转
			// if (android.os.Build.VERSION_CODES.GINGERBREAD >
			// android.os.Build.VERSION.SDK_INT) {
			mCamera.setDisplayOrientation(90);
			// } else {
			// parameters.set("rotation", 180);
			// }
			List<Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
			double aspectTolerance = Double.MAX_VALUE;
			int preWidth = 0, preHeight = 0;
			double scale = ((double) width) / height;
			for (int i = 0, len = previewSizes.size(); i < len; i++) {
				Size s = previewSizes.get(i);
				double sizeScale = ((double) s.height) / s.width;
				if (Math.abs(scale - sizeScale) < aspectTolerance) {
					aspectTolerance = Math.abs(scale - sizeScale);
					preWidth = s.height;
					preHeight = s.width;
				}
			}
			if (preWidth != 0) {
				parameters.setPreviewSize(preWidth, preHeight);
				// mSurface.setLayoutParams(new LayoutParams(720, 1280));
				Size s = parameters.getPreviewSize();
				Log.e("", s.width + " " + s.height);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// openCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeCamera();
	}

	private void takePic(byte[] data) {
		try {
			String image_path = Environment.getExternalStorageDirectory().getPath() + "/seisonImage";
			File file = new File(image_path);
			if (!file.exists()) {
				file.mkdirs();// 创建文件夹
			}
			data2file(data, image_path + "/validate_" + mTakeCount + ".jpg");
			// data2file(data, image_path + "/validate.jpg");
		} catch (Exception e) {
		}
	}

	private void data2file(byte[] w, String fileName) throws Exception {// 将二进制数据转换为文件的函数
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(w);
			out.close();
		} catch (Exception e) {
			if (out != null)
				out.close();
			throw e;
		}
	}

	private void openCamera() {
		if (null != mCamera) {
			return;
		}

		if (!checkCameraPermission()) {
			showTip("摄像头权限未打开，请打开后再试");
			return;
		}

		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}

		try {
			mCamera = Camera.open(mCameraId);
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}
		Parameters params = mCamera.getParameters();
		// params.setPreviewFormat(ImageFormat.NV21);
		// params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);

		List<String> supportedFocusModes = params.getSupportedFocusModes();
		for (Iterator iterator = supportedFocusModes.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (Parameters.FOCUS_MODE_CONTINUOUS_VIDEO.equals(string)) {
				params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				break;
			}
		}
		try {
			mCamera.setParameters(params);
			// mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
			mCamera.startFaceDetection();
			mCamera.setFaceDetectionListener(mFaceDetectionListener);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean equalRate(Size s, float th) {
		return th == (s.width / s.height);
	}

	private Face[] mTmpFaces = null;
	private boolean isFaceDetected = false;
	private FaceDetectionListener mFaceDetectionListener = new FaceDetectionListener() {

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (null != faces && faces.length == 1) {
				mTmpFaces = faces;
			} else {
				mTmpFaces = null;
			}
			if(null != mCamera){
				mCamera.autoFocus(mAutoFocusCallback);
			}
		}
	};
	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mCamera.stopPreview();
			takePic(data);
			mTakeCount++;
			isTaking = false;
			isFocused = false;
			if (mTakeCount == mTotakeConut) {
				setResult(RESULT_OK);
				finish();
			} else {
				mCamera.startPreview();
				mCamera.cancelAutoFocus();
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mCamera.startFaceDetection();
					}
				}, 1000);
			}
		}
	};
	private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				if (null != mTmpFaces && !isTaking) {
					isTaking = true;
					mCamera.takePicture(new ShutterCallback() {
						@Override
						public void onShutter() {
						}
					}, null, mPictureCallback);
				}
			}
		}
	};

	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

	private void showTip(String string) {
		mToast.setText(string);
		mToast.show();
	}

	private boolean checkCameraPermission() {
		int status = checkPermission(permission.CAMERA, Process.myPid(), Process.myUid());
		if (PackageManager.PERMISSION_GRANTED == status) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void back(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
