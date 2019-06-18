package com.cannic.apps.rlbubble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.services.BubbleService;
import com.cannic.apps.rlbubble.utils.Utils;

public class AppUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)
                && Utils.getPreferences(context, context.getResources().getString(R.string.enable_preference), 0) == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(new Intent(context, BubbleService.class));
            else
                context.startService(new Intent(context, BubbleService.class));
        }
    }
}
