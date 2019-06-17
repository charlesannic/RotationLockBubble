package com.cannic.apps.rlbubble.services;

import android.accessibilityservice.AccessibilityService;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;

import com.cannic.apps.rlbubble.activities.MainActivity;
import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.activities.NotificationsActivity;
import com.cannic.apps.rlbubble.io.DatabaseHelper;
import com.cannic.apps.rlbubble.receivers.LockEventReceiver;
import com.cannic.apps.rlbubble.utils.Utils;
import com.cannic.apps.rlbubble.views.FloatingBubble;
import com.google.android.material.shape.ShapeAppearanceModel;

public class BubbleService extends AccessibilityService {

    private int FOREGROUND_ID = 1510;

    private static BubbleService INSTANCE; // Singleton used so that the service can be accessed through different activities.

    private WindowManager windowManager;
    private FloatingBubble bubble;

    private BroadcastReceiver lockEventReceiver;

    private String currentApp; // Package name of the application currently displayed on the screen.

    public boolean isRunning = false; // True if the service is running.
    private boolean isDeviceTurnedLeft = false; // True if the device is being turned to the left. This is used to change the icon.

    private CountDownTimer countDownTimer;
    private boolean isCountdownFinished = false; // Used to not display the bubble after the display time has been reached.

    public static BubbleService getInstance() {
        return INSTANCE;
    }

    /**
     * Enables to retrieve the application package currently on the screen.
     * @param event Accessibility event triggered.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
                if(accessibilityNodeInfo != null) {
                    currentApp = accessibilityNodeInfo.getPackageName().toString();

                    // If the application currently in use is excluded, the screen is reset to portrait mode and the bubble hidden.
                    if (isCurrentAppExcluded()) {

                        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
                        if (params.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                            windowManager.updateViewLayout(bubble, params);
                        }

                        hideBubble();
                    }
                }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        DatabaseHelper.getInstance(this);

        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);

        // Broadcast receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        lockEventReceiver = new LockEventReceiver();
        registerReceiver(lockEventReceiver, filter);

        Intent notificationIntent = new Intent(this, NotificationsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        String channelId = createNotificationChannel("SRLBubbleService", "Screen Rotation Lock Bubble Service");

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_screen_rotation_left)
                .setContentTitle(getResources().getString(R.string.notification_title)).setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .setCategory(Notification.CATEGORY_STATUS)
                .setOngoing(true)
                .setContentText(getResources().getString(R.string.notification_desc)).setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentIntent(pendingIntent).build();

        startForeground(FOREGROUND_ID,
                notification);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        bubble = new FloatingBubble(this);
        bubble.getFab().setClickable(true);

        setBubble();

        OrientationEventListener listener=new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI)
        {
            public void onOrientationChanged(int orientation) {
                updateBubble();
                if (isRunning && !isCurrentAppExcluded()) {
                    final WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
                    setBubbleGravity(params);

                    if (MainActivity.isServiceAskedToStop())
                        hideBubble();
                    else if (MainActivity.isRunningOnTop())
                        showBubble(null);
                    else if (orientation >= 230 && orientation <= 290
                            && params.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    {
                        isDeviceTurnedLeft = true;
                        setIconOrientation();
                        showBubble(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    else if (orientation >= 70 && orientation <= 110
                            && params.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    {
                        isDeviceTurnedLeft = false;
                        setIconOrientation();
                        showBubble(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                    else if (((orientation >= 340 && orientation <= 360) || (orientation <= 20 && orientation >= 0))
                            && params.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    {
                        if (Utils.getPreferences(BubbleService.this, getResources().getString(R.string.automatic_portrait_preference), 0) == 1)
                            changeScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        else {
                            isDeviceTurnedLeft = params.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                            setIconOrientation();
                            showBubble(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }
                    else
                    {
                        isCountdownFinished = false;
                        hideBubble();
                    }
                } else {
                    isCountdownFinished = false;
                    hideBubble();
                }
            }
        };

        setCountDownTimer();

        if(listener.canDetectOrientation())
            listener.enable();

        Utils.setPreferences(this, getResources().getString(R.string.enable_preference), 1);
        isRunning = true;

        //showBubble(this, null);
    }

    private boolean isCurrentAppExcluded() {
        boolean currentAppIsExcluded = false;

        for(String s : DatabaseHelper.ExceptionHelper.getAllExceptions()) {
            if (s != null && s.equals(currentApp)) {
                currentAppIsExcluded = true;
                break;
            }
        }

        return  currentAppIsExcluded;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(lockEventReceiver);

        if (bubble != null) windowManager.removeView(bubble);

        Utils.setPreferences(this, getResources().getString(R.string.enable_preference), 0);
        isRunning = false;
    }

    public void hideBubble() {
        if (bubble != null && bubble.getParent() != null && bubble.getFab().isOrWillBeShown()) {
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
            params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            bubble.setLayoutParams(params);
            windowManager.updateViewLayout(bubble, bubble.getLayoutParams());

            countDownTimer.cancel();

            bubble.getFab().hide();
        }
    }

    public void changeScreenOrientation(final Integer orientationFlag) {
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
        params.screenOrientation = orientationFlag;
        windowManager.updateViewLayout(bubble, params);
    }

    public void showBubble(final Integer orientationFlag) {
        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1
                && orientationFlag != null)
            changeScreenOrientation(orientationFlag);
        else if (isCountdownFinished)
            hideBubble();
        else if (bubble != null && bubble.getParent() != null && !bubble.getFab().isOrWillBeShown()) {
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
            params.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            bubble.setLayoutParams(params);
            windowManager.updateViewLayout(bubble, bubble.getLayoutParams());

            if (orientationFlag == null)
                bubble.getFab().setOnClickListener(null);
            else {
                bubble.getFab().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideBubble();
                        changeScreenOrientation(orientationFlag);
                    }
                });

                countDownTimer.cancel();
                setCountDownTimer();
                countDownTimer.start();
            }

            bubble.getFab().show();
        }
    }

    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_MIN);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.setImportance(NotificationManager.IMPORTANCE_NONE);
        chan.setShowBadge(false);

        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        service.createNotificationChannel(chan);
        return channelId;
    }

    public boolean bubbleHasParent() {
        return bubble != null && bubble.getParent() != null;
    }

    private void setBubble() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        setBubbleParameters(params);

        windowManager.addView(bubble, params);
    }

    public void updateBubble() {
        if (!bubbleHasParent())
            return;

        WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();
        setBubbleParameters(params);
        windowManager.updateViewLayout(bubble, bubble.getLayoutParams());
    }

    private void setBubbleParameters(WindowManager.LayoutParams params) {
        int size = Utils.getPreferences(this, getResources().getString(R.string.size_preference), 1);
        switch (size) {
            case 0:
                size = Utils.dpToPx(40) + Utils.dpToPx(16);
                break;
            case 2:
                size = Utils.dpToPx(72) + Utils.dpToPx(16);
                break;
            default:
                size = Utils.dpToPx(56) + Utils.dpToPx(16);
                break;
        }
        int backgroundColor = Utils.getPreferences(this, getResources().getString(R.string.background_color_preference), Color.WHITE);
        int iconColor = Utils.getPreferences(this, getResources().getString(R.string.icon_color_preference), Color.BLACK);

        // color
        bubble.getFab().setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        bubble.getFab().setImageResource(R.drawable.ic_screen_rotation_left);
        bubble.getFab().setImageTintList(ColorStateList.valueOf(iconColor));
        bubble.getFab().setScaleType(ImageView.ScaleType.CENTER);
        setIconOrientation();

        // radius
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel();
        shapeAppearanceModel.setCornerRadius(size);
        bubble.setShapeAppearance(shapeAppearanceModel);

        // gravity
        setBubbleGravity(params);

        // size
        params.height = size;
        params.width = size;
        bubble.setCustomSize(size);

        // update
        bubble.setLayoutParams(params);
    }

    private void setCountDownTimer() {
        int duration = Utils.getPreferences(this, getResources().getString(R.string.duration_preference), 10);
        countDownTimer = new CountDownTimer(duration * 1000, 2000) {
            public void onTick(long millisUntilFinished) {
                ValueAnimator valueAnimator;
                valueAnimator = ValueAnimator.ofInt(90, 0);
                valueAnimator.setDuration(1000);
                valueAnimator.setInterpolator(new OvershootInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        bubble.getFab().setRotation((int) animation.getAnimatedValue());
                    }
                });

                valueAnimator.start();
            }

            public void onFinish() {
                isCountdownFinished = true;
                hideBubble();
            }
        };
    }

    private void setIconOrientation() {
        if (isDeviceTurnedLeft)
            bubble.getFab().setRotationY(180);
        else
            bubble.getFab().setRotationY(0);
    }

    private void setBubbleGravity(WindowManager.LayoutParams params) {
        int size = Utils.getPreferences(this, getResources().getString(R.string.size_preference), 1);
        switch (size) {
            case 0:
                size = Utils.dpToPx(40) + Utils.dpToPx(16);
                break;
            case 2:
                size = Utils.dpToPx(72) + Utils.dpToPx(16);
                break;
            default:
                size = Utils.dpToPx(56) + Utils.dpToPx(16);
                break;
        }
        int side = Utils.getPreferences(this, getResources().getString(R.string.side_preference), 0);
        float offset = Utils.getPreferences(this, getResources().getString(R.string.offset_preference), 0f);

        final Display display = windowManager.getDefaultDisplay();
        final Point screenSize = new Point();
        display.getSize(screenSize);
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        if((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        )
        {
            params.gravity = Gravity.BOTTOM | Gravity.START;
        }
        else if ((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
        )
        {
            params.gravity = Gravity.TOP | Gravity.START;
        }
        else if ((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        )
        {
            params.gravity = Gravity.BOTTOM | Gravity.END;
        }
        else if ((side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        )
        {
            params.gravity = Gravity.TOP | Gravity.END;
        }

        if((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
        )
        {
            params.x = 0;
            params.y = Math.round((screenHeight - size) * offset);
        }
        else if ((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        )
        {
            params.x = Math.round((screenWidth - size) * offset);
            params.y = 0;
        }
        else if ((side == 1 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        )
        {
            params.x = 0;
            params.y = Math.round((screenHeight - size) * offset);
        }
        else if ((side == 0 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
                (side == 2 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (side == 3 && params.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        )
        {
            params.x = Math.round((screenWidth - size) * offset);
            params.y = 0;
        }
    }

}