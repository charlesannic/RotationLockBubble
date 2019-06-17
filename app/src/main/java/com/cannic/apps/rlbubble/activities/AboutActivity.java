package com.cannic.apps.rlbubble.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cannic.apps.rlbubble.BuildConfig;
import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivArrowBack;
    private Toolbar toolbar;
    private LinearLayout scrollView;

    private CardView btnContact;
    private LinearLayout btnRate;
    private LinearLayout btnReportBugs;
    private LinearLayout btnImproveApp;
    private LinearLayout btnSkydovesColorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getTheme(this));
        setContentView(R.layout.activity_about);

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

        btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(this);

        btnRate = findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(this);

        btnReportBugs = findViewById(R.id.btn_report_bugs);
        btnReportBugs.setOnClickListener(this);

        btnImproveApp = findViewById(R.id.btn_improve_app);
        btnImproveApp.setOnClickListener(this);

        btnSkydovesColorPicker = findViewById(R.id.btn_skydoves_color_picker);
        btnSkydovesColorPicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivArrowBack)
            finish();
        else if (v == btnContact) {
            Intent emailIntent;

            emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:cannic.apps@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[RLB] ");
            emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.device_information, BuildConfig.VERSION_NAME, Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT));

            startActivity(emailIntent);
        }
        else if (v == btnRate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cannic.apps.rlbubble")));
        }
        else if (v == btnReportBugs) {
            Intent emailIntent;

            emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:cannic.apps@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[RLB] Bug report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.device_information, BuildConfig.VERSION_NAME, Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT));

            startActivity(emailIntent);
        }
        else if (v == btnImproveApp) {
            String url = "https://github.com/charlesannic/RotationLockBubble";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else if (v == btnSkydovesColorPicker) {
            String url = "https://github.com/skydoves/ColorPickerView";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }
}
