package com.cannic.apps.rlbubble.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDone;
    private LinearLayout scrollView;
    private FloatingActionButton bubble;
    private static CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getTheme(this));
        setContentView(R.layout.activity_welcome);

        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);

        scrollView = findViewById(R.id.scroll_view);
        scrollView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        scrollView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                scrollView.setPadding(scrollView.getPaddingLeft(),
                        insets.getSystemWindowInsetTop(),
                        scrollView.getPaddingRight(),
                        insets.getSystemWindowInsetBottom());

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btnDone.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                btnDone.setLayoutParams(params);

                return insets;
            }
        });

        bubble = findViewById(R.id.fab);
        bubble.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_bubble_background)));
        bubble.setImageResource(R.drawable.ic_screen_rotation_left);
        bubble.setImageTintList(ColorStateList.valueOf(getColor(R.color.default_bubble_icon)));
        bubble.setScaleType(ImageView.ScaleType.CENTER);

        countDownTimer = new CountDownTimer(20000, 2000) {
            public void onTick(long millisUntilFinished) {
                ValueAnimator valueAnimator;
                valueAnimator = ValueAnimator.ofInt(90, 0);
                valueAnimator.setDuration(1000);
                valueAnimator.setInterpolator(new OvershootInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        bubble.setRotation((int) animation.getAnimatedValue());
                    }
                });

                valueAnimator.start();
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onClick(View view) {
        if (view == btnDone)
            finish();
    }
}
