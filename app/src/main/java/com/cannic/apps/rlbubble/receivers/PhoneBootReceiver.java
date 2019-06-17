package com.cannic.apps.rlbubble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.services.BubbleService;
import com.cannic.apps.rlbubble.utils.Utils;

public class PhoneBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                && Utils.getPreferences(context, context.getResources().getString(R.string.enable_preference), 0) == 1)
            context.startForegroundService(new Intent(context, BubbleService.class));
    }
}
