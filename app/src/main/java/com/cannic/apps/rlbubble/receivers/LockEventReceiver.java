package com.cannic.apps.rlbubble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.services.BubbleService;
import com.cannic.apps.rlbubble.utils.Utils;

public class LockEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                && Utils.getPreferences(context, context.getResources().getString(R.string.portrait_on_lock_preference), 0) == 1)
            BubbleService.getInstance().changeScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
