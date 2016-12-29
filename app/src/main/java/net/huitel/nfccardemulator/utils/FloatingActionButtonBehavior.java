package net.huitel.nfccardemulator.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alan on 21/07/2016.
 * https://lab.getbase.com/introduction-to-coordinator-layout-on-android/
 * https://github.com/ggajews/coordinatorlayoutwithfabdemo/
 * Redefining default FAB behaviour.
 * @see layout/app_bar_main.xml
 */
public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>{

    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {}

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        float translationY = Math.min(0, parent.getBottom() - child.getBottom());
        child.setTranslationY(translationY);
    }
}
