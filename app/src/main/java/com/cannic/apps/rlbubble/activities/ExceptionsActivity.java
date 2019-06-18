package com.cannic.apps.rlbubble.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.adapters.AppAdapter;
import com.cannic.apps.rlbubble.io.DatabaseHelper;
import com.cannic.apps.rlbubble.java.App;
import com.cannic.apps.rlbubble.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExceptionsActivity extends AppCompatActivity implements View.OnClickListener, AppAdapter.Listener {

    private AlertDialog permissionDialog;

    private Toolbar toolbar;
    private ImageView ivArrowBack;
    private RecyclerView rvInstalledApps;
    private ExtendedFloatingActionButton fab;
    private List<CheckBox> checkBoxes;
    private List<App> installedApps;
    private AppAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getTheme(this));
        setContentView(R.layout.activity_exceptions);

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
        rvInstalledApps = findViewById(R.id.installed_apps_list);
        rvInstalledApps.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        rvInstalledApps.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                rvInstalledApps.setPadding(insets.getSystemWindowInsetLeft(),
                        rvInstalledApps.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom() + Utils.dpToPx(88));
                return insets;
            }
        });
        fab = findViewById(R.id.fab);
        fab.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        fab.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                params.bottomMargin += insets.getSystemWindowInsetBottom();
                return insets;
            }
        });
        fab.setOnClickListener(this);
        updateFab();

        ivArrowBack = findViewById(R.id.iv_arrow_back);
        ivArrowBack.setOnClickListener(this);

        progressBar = findViewById(R.id.progress_bar);

        rvInstalledApps.setLayoutManager(new LinearLayoutManager(ExceptionsActivity.this));
        // Use of an AsyncTask to avoid a freeze when starting the application when loading applications.
        new SettingAppListTask().execute();
    }

    public class SettingAppListTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            installedApps = getInstalledApps();
            installedApps.sort(new Comparator<App>() {
                @Override
                public int compare(App o1, App o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            installedApps.add(0, new App(false, null, null, null));
            checkBoxes = new ArrayList<>();
            adapter = new AppAdapter(ExceptionsActivity.this, ExceptionsActivity.this, installedApps, checkBoxes);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            rvInstalledApps.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the application has accessibility permission, the dialog is dismissed.
        if (Utils.isAccessibilitySettingsOn(this)) {
            if (permissionDialog != null && permissionDialog.isShowing())
                permissionDialog.dismiss();
        }
        // Otherwise, we ask for permission.
        else
            permissionDialog = Utils.BuildAccessibilityPermissionDialog(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivArrowBack)
            finish();
        else if (v == fab) {

            boolean noExceptions = DatabaseHelper.ExceptionHelper.getAllExceptions().size() == 0;

            if(noExceptions) {
                for(App app : installedApps) {
                    app.setChecked(false);
                    DatabaseHelper.ExceptionHelper.insertException(app);
                }
                for(CheckBox checkBox : checkBoxes)
                    if(checkBox != null)
                        checkBox.setChecked(false);
            }
            else {
                DatabaseHelper.ExceptionHelper.deleteAllExceptions();
                for(App app : installedApps) {
                    app.setChecked(true);
                }
                for(CheckBox checkBox : checkBoxes)
                    if(checkBox != null)
                        checkBox.setChecked(true);
            }
            updateFab();
        }
    }

    private void updateFab() {
        int nbException = DatabaseHelper.ExceptionHelper.getAllExceptions().size();

        if (nbException > 0) {
            fab.setText(getResources().getString(R.string.check_all));
            fab.setIconResource(R.drawable.ic_check_box_24dp);

        } else {
            fab.setText(getResources().getString(R.string.uncheck_all));
            fab.setIconResource(R.drawable.ic_check_box_outline_24dp);
        }
    }

    private List<App> getInstalledApps() {
        List<App> res = new ArrayList<>();
        List<String> excludedApps = DatabaseHelper.ExceptionHelper.getAllExceptions();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            if (p.versionName == null)
                continue ;
            if(getPackageManager().getLaunchIntentForPackage(p.packageName) == null)
                continue;

            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            String packageName = p.packageName;
            Drawable icon = p.applicationInfo.loadIcon(getPackageManager());

            if(excludedApps.contains(packageName))
                res.add(new App(false, appName, packageName, icon));
            else
                res.add(new App(true, appName, packageName, icon));
        }
        return res;
    }

    // If an application has been checked or unchecked, the fab may be updated.
    @Override
    public void dataSetChanged() {
        updateFab();
    }

}
