package com.cannic.apps.rlbubble.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowInsets;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.io.DatabaseHelper;
import com.cannic.apps.rlbubble.services.BubbleService;
import com.cannic.apps.rlbubble.utils.Utils;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean isRunningOnTop = false;
    private static boolean isServiceAskedToStop = false;

    private AlertDialog permissionDialog;

    private Toolbar toolbar;
    private LinearLayout scrollView;

    private SwitchCompat swEnable;

    // Appearance
    // Background color
    private LinearLayout btnBackgroundColor;
    private View cvBackgroundColor;
    // Icon color
    private LinearLayout btnIconColor;
    private View cvIconColor;
    // Size
    private TextView tvSize;
    private SeekBar sbSize;

    // Position
    // Side
    private LinearLayout btnSide;
    private TextView tvSide;
    // Offset
    private SeekBar sbOffset;

    // Others
    private LinearLayout btnBehavior;
    private LinearLayout btnExceptions;
    private TextView tvNbExceptions;
    private LinearLayout btnNotifications;
    private LinearLayout btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting database.
        DatabaseHelper.getInstance(this);

        // Updating theme.
        setTheme(Utils.getTheme(this));

        // Splash screen for first use.
        if (Utils.getPreferences(this, getResources().getString(R.string.first_use_preference), 0) != 1) {
            Utils.setPreferences(this, getResources().getString(R.string.first_use_preference), 1);
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        // Setting toolbar and scrollview paddings according to the window insets.
        toolbar = findViewById(R.id.toolbar);
        toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        toolbar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                toolbar.setPadding(insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        0);
                return insets;
            }
        });
        scrollView = findViewById(R.id.scroll_view);
        scrollView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        scrollView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                scrollView.setPadding(insets.getSystemWindowInsetLeft(),
                        scrollView.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                return insets;
            }
        });

        // Appearance
        // Background color
        btnBackgroundColor = findViewById(R.id.btn_background_color);
        btnBackgroundColor.setOnClickListener(this);
        cvBackgroundColor = findViewById(R.id.cv_background_color);
        int color = Utils.getPreferences(this, getResources().getString(R.string.background_color_preference), -1);
        if (color == -1)
            Utils.setPreferences(this, getResources().getString(R.string.background_color_preference), getResources().getColor(R.color.default_bubble_background, getTheme()));
        cvBackgroundColor.setBackgroundColor(
                Utils.getPreferences(this, getResources().getString(R.string.background_color_preference), getResources().getColor(R.color.default_bubble_background, getTheme())));
        // Icon color
        btnIconColor = findViewById(R.id.btn_icon_color);
        btnIconColor.setOnClickListener(this);
        cvIconColor = findViewById(R.id.cv_icon_color);
        color = Utils.getPreferences(this, getResources().getString(R.string.icon_color_preference), -1);
        if (color == -1)
            Utils.setPreferences(this, getResources().getString(R.string.icon_color_preference), getResources().getColor(R.color.default_bubble_icon, getTheme()));
        cvIconColor.setBackgroundColor(
                Utils.getPreferences(this, getResources().getString(R.string.icon_color_preference), getResources().getColor(R.color.default_bubble_icon, getTheme())));
        // Size
        tvSize = findViewById(R.id.tv_size);
        sbSize = findViewById(R.id.sb_size);
        sbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSizeTextView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // The bubble size preferences are changed when the user has made his choice.
                if (seekBar.getProgress() < 25) {
                    seekBar.setProgress(0, true);
                    Utils.updateBubbleSize(MainActivity.this, 0);
                } else if (seekBar.getProgress() < 75) {
                    seekBar.setProgress(50, true);
                    Utils.updateBubbleSize(MainActivity.this, 1);
                } else {
                    seekBar.setProgress(100, true);
                    Utils.updateBubbleSize(MainActivity.this, 2);
                }
            }
        });
        int size = getSizeProgress();
        sbSize.setProgress(size);
        setSizeTextView(size);

        // Position
        // Side
        btnSide = findViewById(R.id.btn_side);
        tvSide = findViewById(R.id.tv_side);
        setSideTextView();
        btnSide.setOnClickListener(this);

        swEnable = findViewById(R.id.sw_enable);
        swEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
                    isServiceAskedToStop = !isChecked;
                    if (isChecked)
                        startService(new Intent(MainActivity.this, BubbleService.class));
                    else
                        stopService(new Intent(MainActivity.this, BubbleService.class));
                }
            }
        });
        swEnable.setChecked(Settings.canDrawOverlays(this) && Utils.getPreferences(this, getResources().getString(R.string.enable_preference), 0) == 1);
        //Offset
        sbOffset = findViewById(R.id.sb_offset);
        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float offset = progress / 100f;
                Utils.updateBubbleOffset(MainActivity.this, offset);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        int offset = Math.round(Utils.getPreferences(this, getResources().getString(R.string.offset_preference), 0f) * 100);
        sbOffset.setProgress(offset);

        // Others
        // Behavior
        btnBehavior = findViewById(R.id.btn_behavior);
        btnBehavior.setOnClickListener(this);
        // Exceptions
        btnExceptions = findViewById(R.id.btn_exceptions);
        btnExceptions.setOnClickListener(this);
        tvNbExceptions = findViewById(R.id.tv_exceptions);
        // Notifications
        btnNotifications = findViewById(R.id.btn_notifications);
        btnNotifications.setOnClickListener(this);
        // About
        btnAbout = findViewById(R.id.btn_about);
        btnAbout.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // The onPause() method call indicates that the MainActivity activity is not the one displayed on the screen.
        isRunningOnTop = false;

        // If the application has permission to overlay, the bubble is hidden.
        if (Settings.canDrawOverlays(this) && BubbleService.getInstance() != null)
                BubbleService.getInstance().hideBubble();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the application has permission to overlay, the dialog is dismissed and the bubble is displayed.
        if (Settings.canDrawOverlays(this)) {
            if (permissionDialog != null)
                permissionDialog.dismiss();
            if(BubbleService.getInstance() != null)
                BubbleService.getInstance().showBubble(null); // We pass null as a parameter to indicate that the bubble must have no effect, we just want to show it.
        }
        // Otherwise, we ask for permission.
        else if (permissionDialog == null || !permissionDialog.isShowing()) {
            permissionDialog = Utils.BuildOverlayPermissionDialog(this);
        }

        // If accessibility services are not given, the user is informed that the exceptions cannot work.
        if (!Utils.isAccessibilitySettingsOn(this))
            tvNbExceptions.setText(getResources().getString(R.string.missing_permission));
        // Otherwise, the number of exceptions is displayed.
        else
            setNbExceptionsTextView();

        // The onResume() method call indicates that the MainActivity activity is the one displayed on the screen.
        isRunningOnTop = true;
    }

    @Override
    public void onClick(View view) {
        if (view == btnBackgroundColor) {
            showColorPickerViewDialog(getResources().getString(R.string.bubble_color), true);
        } else if (view == btnIconColor) {
            showColorPickerViewDialog(getResources().getString(R.string.icon_color), false);
        } else if (view == btnSide) {
            Utils.BuildSideDialog(MainActivity.this, tvSide);
        } else if (view == btnBehavior) {
            startActivity(new Intent(MainActivity.this, BehaviorActivity.class));
        } else if (view == btnExceptions) {
            startActivity(new Intent(MainActivity.this, ExceptionsActivity.class));
        } else if (view == btnNotifications) {
            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
        } else if (view == btnAbout) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
    }

    /**
     * Determines the progress of the seekbar according to the size given in preferences.
     * @return Progress of the seekbar of the bubble size.
     */
    private int getSizeProgress() {
        int size = Utils.getPreferences(this, getResources().getString(R.string.size_preference), 1);

        // Three sizes are possible: 0 (small), 1 (medium) and 2 (large).
        // They correspond respectively to a progress of 0, 50 and 100% on the seekbar.
        switch (size) {
            case 0:
                size = 0;
                break;
            case 2:
                size = 100;
                break;
            default:
                size = 50;
                break;
        }
        return size;
    }

    /**
     * Creates a ColorPickView dialog.
     * @param title Dialog title
     * @param setBackgroundColor Must be true if the dialog is intended to change the background color of the bubble, and false for the icon color.
     */
    private void showColorPickerViewDialog(String title, final boolean setBackgroundColor) {
        String preference = getResources().getString(setBackgroundColor ? R.string.background_color_preference : R.string.icon_color_preference);

        new ColorPickerDialog.Builder(this, Utils.isDarkThemeEnable(this) ? R.style.DarkAlertDialogTheme : R.style.LightAlertDialogTheme)
                .setTitle(title)
                .setPreferenceName(preference)
                .setPositiveButton(getResources().getString(R.string.confirm),
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                if(setBackgroundColor) {
                                    cvBackgroundColor.setBackgroundColor(envelope.getColor());
                                    Utils.updateBubbleBackgroundColor(MainActivity.this, envelope.getColor());
                                } else {
                                    cvIconColor.setBackgroundColor(envelope.getColor());
                                    Utils.updateBubbleIconColor(MainActivity.this, envelope.getColor());
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .show();
    }

    /**
     * Sets the TextView text indicating the side of the bubble, according to preferences.
     */
    private void setSideTextView() {
        switch (Utils.getPreferences(this, getResources().getString(R.string.side), 0)) {
            case 0:
                tvSide.setText(getResources().getString(R.string.left));
                break;
            case 1:
                tvSide.setText(getResources().getString(R.string.top));
                break;
            case 2:
                tvSide.setText(getResources().getString(R.string.right));
                break;
            case 3:
                tvSide.setText(getResources().getString(R.string.bottom));
                break;
        }
    }

    /**
     * Sets the TextView text indicating the number of exceptions, according to preferences.
     */
    private void setNbExceptionsTextView() {
        int nbExceptions = DatabaseHelper.ExceptionHelper.getAllExceptions().size();

        switch (nbExceptions) {
            case 0:
                tvNbExceptions.setText(getResources().getString(R.string.no_exception));
                break;
            case 1:
                tvNbExceptions.setText(getResources().getString(R.string.one_exception));
                break;
            default:
                tvNbExceptions.setText(getResources().getString(R.string.many_exceptions, nbExceptions));
                break;
        }
    }

    /**
     * Sets the TextView text indicating the size of the bubble, according to preferences.
     */
    private void setSizeTextView(int progress) {
        if (progress < 25) {
            tvSize.setText(getResources().getString(R.string.small));
        } else if (progress < 75) {
            tvSize.setText(getResources().getString(R.string.medium));
        } else {
            tvSize.setText(getResources().getString(R.string.large));
        }
    }

    public static boolean isRunningOnTop() {
        return isRunningOnTop;
    }

    public static boolean isServiceAskedToStop() {
        return isServiceAskedToStop;
    }
}
