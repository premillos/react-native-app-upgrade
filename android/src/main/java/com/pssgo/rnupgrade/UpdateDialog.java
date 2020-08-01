package com.pssgo.rnupgrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

class UpdateDialog {

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }

    public static void goToDownload(Context context, String downloadUrl, String hash) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra(Constants.APK_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(Constants.APK_HASH, hash);
        context.startService(intent);
    }
    
}
