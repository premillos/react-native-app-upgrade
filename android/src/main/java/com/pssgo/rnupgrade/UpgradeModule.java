package com.pssgo.rnupgrade;

import android.content.Context;
import android.app.ActivityManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Song on 2017/7/10.
 */
public class UpgradeModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext context;
    private static final String EVENT_NAME = "LOAD_PROGRESS";
    private String versionName = "1.0.0";
    private int versionCode = 1;

    public UpgradeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        PackageInfo pInfo = null;
        this.context = reactContext;
        try {
            pInfo = reactContext.getPackageManager().getPackageInfo(reactContext.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断服务是否正在运行
     *
     * @param serviceName 服务类的全路径名称 例如： com.jaychan.demo.service.PushService
     * @param context 上下文对象
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); //获取运行的服务,参数表示最多返回的数量

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true; //判断服务是否运行
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "upgrade";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("versionName", versionName);
        constants.put("versionCode", versionCode);
        return constants;
    }

    // app是否在升级中
    @ReactMethod
    public boolean isUpdating() {
        return isServiceRunning("com.pssgo.rnupgrade.DownloadService", context);
    }

    @ReactMethod
    public void upgrade(String apkUrl, String hash) {
        boolean isRunning = isServiceRunning("com.pssgo.rnupgrade.DownloadService", context);

        if (!isRunning) {
            // 如果没在运行下载 任务 则开启service下载
            UpdateDialog.goToDownload(context, apkUrl,apkUrl);
        }
    }

    // 发送app升级进度
    public static void sendProgress(int msg) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT_NAME, msg);
    }

}
