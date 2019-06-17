package com.cannic.apps.rlbubble.java;

import android.graphics.drawable.Drawable;

public class App {

    private boolean isChecked;
    private String name;
    private String packageName;
    private Drawable icon;

    public App(boolean isChecked, String name, String packageName, Drawable icon) {
        this.isChecked = isChecked;
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
