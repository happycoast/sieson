package com.xigu.siesonfinancial;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Application;

public class MyApplication extends Application {
    private String storeId = "1";

    // 濉啓鎮ㄧ敵璇风殑APP_ID
    public static final String APP_ID = "10033996";
    // 濉啓鎮ㄧ敵璇风殑SECRET_ID
    public static final String SECRET_ID = "AKIDTnRVPvm8z6WhBtDCgukHPmMFyvoeAYFQ";
    // 濉啓鎮ㄧ敵璇风殑SECRET_KEY
    public static final String SECRET_KEY = "pEkErCx657xVoCPNgetuYoJuTnEbESfZ";
    // 30 days
    protected static int EXPIRED_SECONDS = 2592000;

    public static int getEXPIRED_SECONDS() {
        return EXPIRED_SECONDS;
    }

    public static void setEXPIRED_SECONDS(int eXPIRED_SECONDS) {
        EXPIRED_SECONDS = eXPIRED_SECONDS;
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static String getSecretId() {
        return SECRET_ID;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // max width, max
                                                   // height锛屽嵆淇濆瓨鐨勬瘡涓紦瀛樻枃浠剁殑鏈�澶ч暱瀹�
                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
                // null) // Can slow ImageLoader, use it carefully (Better don't
                // use it)/璁剧疆缂撳瓨鐨勮缁嗕俊鎭紝鏈�濂戒笉瑕佽缃繖涓�
                .threadPoolSize(3)// 绾跨▼姹犲唴鍔犺浇鐨勬暟閲�
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You
                                                                               // can
                                                                               // pass
                                                                               // your
                                                                               // own
                                                                               // memory
                                                                               // cache
                                                                               // implementation/浣犲彲浠ラ�氳繃鑷繁鐨勫唴瀛樼紦瀛樺疄鐜�
                .memoryCacheSize(2 * 1024 * 1024).discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())// 灏嗕繚瀛樼殑鏃跺�欑殑URI鍚嶇О鐢∕D5
                                                                       // 鍔犲瘑
                .tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(100) // 缂撳瓨鐨勬枃浠舵暟閲�
                // .discCache(new UnlimitedDiscCache(cacheDir))//鑷畾涔夌紦瀛樿矾寰�
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout
                                                                                                        // (5
                                                                                                        // s),
                                                                                                        // readTimeout
                                                                                                        // (30
                                                                                                        // s)瓒呮椂鏃堕棿
                .writeDebugLogs() // Remove for release app
                .build();// 寮�濮嬫瀯寤�
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 鍏ㄥ眬鍒濆鍖栨閰嶇疆
    }
}
