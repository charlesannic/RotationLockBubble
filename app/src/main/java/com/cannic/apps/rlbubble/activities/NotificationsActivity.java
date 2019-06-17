package com.cannic.apps.rlbubble.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.utils.Utils;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout scrollView;

    private Button btnAppSettings, btnSystemSettings;
    private ImageView ivArrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getTheme(this));
        setContentView(R.layout.activity_notifications);

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

        btnAppSettings = findViewById(R.id.app_notification_settings);
        btnSystemSettings = findViewById(R.id.system_notification_settings);

        btnAppSettings.setOnClickListener(this);
        btnSystemSettings.setOnClickListener(this);

        ivArrowBack = findViewById(R.id.iv_arrow_back);
        ivArrowBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAppSettings) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            //String mPackageName = (new ActivityManager()).getRunningAppProcesses().get(0).
            // for Android 8 and above
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

            startActivity(intent);
        } else if (v == btnSystemSettings) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

            // for Android 8 and above
            intent.putExtra("android.provider.extra.APP_PACKAGE", "android");

            startActivity(intent);
        } else if (v == ivArrowBack)
            finish();
    }
}
