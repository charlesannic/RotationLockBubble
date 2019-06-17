package com.cannic.apps.rlbubble.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.activities.ExceptionsActivity;
import com.cannic.apps.rlbubble.activities.MainActivity;
import com.cannic.apps.rlbubble.services.BubbleService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    /**
     *
     * @param context Context from which the method is called.
     * @return The right theme according to the device theme (light or dark).
     */
    public static int getTheme(Context context) {
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                return R.style.DarkTheme;
            case Configuration.UI_MODE_NIGHT_NO:
                return R.style.LightTheme;
            default:
                return R.style.LightTheme;
        }
    }

    /**
     * Determines if global dark theme is enable.
     * @param context Context from which the method is called.
     * @return True if global dark theme is enable.
     */
    public static boolean isDarkThemeEnable(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Builds and displays the Dialog to change the side of the bubble.
     * @param context Context from which the method is called.
     * @param textView TextView to be changed when a choice has been made.
     */
    public static void BuildSideDialog(final Context context, final TextView textView) {
        int defaultValue = Utils.getPreferences(context, context.getResources().getString(R.string.side_preference), 0);

        final CharSequence[] items =
                {context.getResources().getString(R.string.left),
                        context.getResources().getString(R.string.top),
                        context.getResources().getString(R.string.right),
                        context.getResources().getString(R.string.bottom)};

        new MaterialAlertDialogBuilder(context, Utils.isDarkThemeEnable(context) ? R.style.DarkAlertDialogTheme : R.style.LightAlertDialogTheme)
            .setTitle(context.getResources().getString(R.string.side_popup_desc))
                .setSingleChoiceItems(items, defaultValue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        updateBubbleSide(context, item);

                        switch (item) {
                            case 0:
                                textView.setText(items[0]);
                                dialog.dismiss();
                                break;
                            case 1:
                                textView.setText(items[1]);
                                dialog.dismiss();
                                break;
                            case 2:
                                textView.setText(items[2]);
                                dialog.dismiss();
                                break;
                            case 3:
                                textView.setText(items[3]);
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * Builds and displays the Dialog to to give permission for overlay.
     * @param context Context from which the method is called.
     */
    public static AlertDialog BuildOverlayPermissionDialog(final Context context) {
        return new MaterialAlertDialogBuilder(context, Utils.isDarkThemeEnable(context) ? R.style.DarkAlertDialogTheme : R.style.LightAlertDialogTheme)
                .setTitle(context.getResources().getString(R.string.missing_permission))
                .setMessage(context.getResources().getString(R.string.overlays_permission_popup_desc))
                .setPositiveButton(context.getResources().getString(R.string.give_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).finish(); // The user must not be able to interact with the activity without permission.
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ((MainActivity)context).finish(); // The user must not be able to interact with the activity without permission.
                    }
                })
                .setCancelable(true).show();
    }

    /**
     * Builds and displays the Dialog to to give permission for accessibility.
     * @param context Context from which the method is called.
     */
    public static AlertDialog BuildAccessibilityPermissionDialog(final Context context) {
        return new MaterialAlertDialogBuilder(context, Utils.isDarkThemeEnable(context) ? R.style.DarkAlertDialogTheme : R.style.LightAlertDialogTheme)
                .setTitle(context.getResources().getString(R.string.missing_permission))
                .setMessage(context.getResources().getString(R.string.accessibility_permission_popup_desc))
                .setPositiveButton(context.getResources().getString(R.string.give_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ExceptionsActivity)context).finish(); // The user must not be able to interact with the activity without permission.
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ((ExceptionsActivity)context).finish(); // The user must not be able to interact with the activity without permission.
                    }
                })
                .setCancelable(true).show();
    }

    /**
     * Determines if the application has access to accessibility services.
     * @param context Context from which the method is called.
     * @return True if the application has access to accessibility services.
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + BubbleService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Converts a value in dp to pixels.
     * @param dp Value in dp.
     * @return Value in pixels.
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



    // Preferences utils
    private static final String PREFERENCES_NAME = "RLBPreferences";

    /**
     * Changes the bubble size preferences.
     * @param context Context from which the method is called.
     * @param px Size in pixels.
     */
    public static void updateBubbleSize(Context context, int px) {
        Utils.setPreferences(context, context.getResources().getString(R.string.size_preference), px);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();
    }

    /**
     * Changes the bubble duration preferences.
     * @param context Context from which the method is called.
     * @param seconds Duration in seconds.
     */
    public static void updateBubbleDuration(Context context, int seconds) {
        Utils.setPreferences(context, context.getResources().getString(R.string.duration_preference), seconds);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();
    }

    /**
     * Changes the bubble side preferences.
     * @param context Context from which the method is called.
     * @param side Side.
     */
    public static void updateBubbleSide(Context context, int side) {
        Utils.setPreferences(context, context.getResources().getString(R.string.side_preference), side);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();    }

    /**
     * Changes the bubble background color preferences.
     * @param context Context from which the method is called.
     * @param color Background color.
     */
    public static void updateBubbleBackgroundColor(Context context, int color) {
        Utils.setPreferences(context, context.getResources().getString(R.string.background_color_preference), color);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();
    }

    /**
     * Changes the bubble icon color preferences.
     * @param context Context from which the method is called.
     * @param color Icon color.
     */
    public static void updateBubbleIconColor(Context context, int color) {
        Utils.setPreferences(context, context.getResources().getString(R.string.icon_color_preference), color);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();
    }

    /**
     * Changes the bubble offset preferences.
     * @param context Context from which the method is called.
     * @param offset Offset.
     */
    public static void updateBubbleOffset(Context context, float offset) {
        Utils.setPreferences(context, context.getResources().getString(R.string.offset_preference), offset);
        if (BubbleService.getInstance() != null)
            BubbleService.getInstance().updateBubble();
    }


    public static void setPreferences(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static void setPreferences(Context context, String key, float value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }
    public static int getPreferences(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.  getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }
    public static float getPreferences(Context context, String key, float defaultValue) {
        SharedPreferences prefs = context.  getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        return prefs.getFloat(key, defaultValue);
    }
}
