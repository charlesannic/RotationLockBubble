package com.cannic.apps.rlbubble.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.utils.Utils;

public class BehaviorActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {

    private ImageView ivArrowBack;
    private Toolbar toolbar;
    private LinearLayout scrollView;

    // Back to portrait on lock
    private LinearLayout btnBackToPortraitOnLock;
    private Switch swBackToPortraitOnLock;

    // Back to portrait auto
    private LinearLayout btnBackToPortraitAuto;
    private Switch swBackToPortraitAuto;

    // Duration
    private TextView tvDuration;
    private SeekBar sbDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getTheme(this));
        setContentView(R.layout.activity_behavior);

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

        ivArrowBack = findViewById(R.id.iv_arrow_back);
        ivArrowBack.setOnClickListener(this);

        // Back to portrait on lock
        btnBackToPortraitOnLock = findViewById(R.id.btn_back_to_portrait_on_lock);
        btnBackToPortraitOnLock.setOnClickListener(this);
        swBackToPortraitOnLock = findViewById(R.id.sw_back_to_portrait_on_lock);
        swBackToPortraitOnLock.setChecked(Utils.getPreferences(this, getResources().getString(R.string.portrait_on_lock_preference), 0) == 1);
        swBackToPortraitOnLock.setOnCheckedChangeListener(this);


        // Back to portrait auto
        btnBackToPortraitAuto = findViewById(R.id.btn_back_to_portrait_auto);
        btnBackToPortraitAuto.setOnClickListener(this);
        swBackToPortraitAuto = findViewById(R.id.sw_back_to_portrait_auto);
        swBackToPortraitAuto.setChecked(Utils.getPreferences(this, getResources().getString(R.string.automatic_portrait_preference), 0) == 1);
        swBackToPortraitAuto.setOnCheckedChangeListener(this);

        // Duration
        tvDuration = findViewById(R.id.tv_duration);
        sbDuration = findViewById(R.id.sb_duration);
        sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDurationLabel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 10) {
                    seekBar.setProgress(0, true);
                    Utils.updateBubbleDuration(BehaviorActivity.this, 2);
                } else if (seekBar.getProgress() < 30) {
                    seekBar.setProgress(20, true);
                    Utils.updateBubbleDuration(BehaviorActivity.this, 4);
                } else if (seekBar.getProgress() < 50) {
                    seekBar.setProgress(40, true);
                    Utils.updateBubbleDuration(BehaviorActivity.this, 6);
                } else if (seekBar.getProgress() < 70) {
                    seekBar.setProgress(60, true);
                    Utils.updateBubbleDuration(BehaviorActivity.this, 8);
                } else {
                    seekBar.setProgress(80, true);
                    Utils.updateBubbleDuration(BehaviorActivity.this, 10);
                }
            }
        });
        int duration = Utils.getPreferences(this, getResources().getString(R.string.duration_preference), 10);
        switch (duration) {
            case 2:
                duration = 0;
                break;
            case 4:
                duration = 20;
                break;
            case 6:
                duration = 40;
                break;
            case 8:
                duration = 60;
                break;
            default:
                duration = 80;
                break;
        }
        sbDuration.setProgress(duration);
        setDurationLabel(duration);
    }

    @Override
    public void onClick(View view) {
        if (view == ivArrowBack)
            finish();
        else if (view == btnBackToPortraitOnLock)
            swBackToPortraitOnLock.performClick();
        else if (view == btnBackToPortraitAuto)
            swBackToPortraitAuto.performClick();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == swBackToPortraitOnLock) {
            Utils.setPreferences(this, getResources().getString(R.string.portrait_on_lock_preference), b ? 1 : 0);
        } else if (compoundButton == swBackToPortraitAuto) {
            Utils.setPreferences(this, getResources().getString(R.string.automatic_portrait_preference), b ? 1 : 0);
        }
    }

    private void setDurationLabel(int progress) {
        if (progress < 10) {
            tvDuration.setText(getResources().getString(R.string.seconds, 2));
        } else if (progress < 30) {
            tvDuration.setText(getResources().getString(R.string.seconds, 4));
        } else if (progress < 50) {
            tvDuration.setText(getResources().getString(R.string.seconds, 6));
        } else if (progress < 70) {
            tvDuration.setText(getResources().getString(R.string.seconds, 8));
        } else {
            tvDuration.setText(getResources().getString(R.string.seconds, 10));
        }
    }
}
