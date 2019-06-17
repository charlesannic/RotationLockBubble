package com.cannic.apps.rlbubble.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.cannic.apps.rlbubble.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.ShapeAppearanceModel;

public class FloatingBubble extends FrameLayout {

    private FloatingActionButton fab;

    public FloatingBubble(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.floating_button, this);

        fab = findViewById(R.id.fab);
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void setCustomSize(int size)
    {
        fab.setCustomSize(size);
    }

    public void setShapeAppearance(ShapeAppearanceModel shapeAppearance) {
        fab.setShapeAppearance(shapeAppearance);
    }
}
