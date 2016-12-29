package net.huitel.nfccardemulator.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import net.huitel.nfccardemulator.R;
import net.huitel.nfccardemulator.utils.NFCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public String TAG = MainActivity.class.getName();
    Toolbar toolbar;

    private FloatingActionButton fab, fab_call, fab_more, fab_nfc;
    private ImageView rotating_image;
    private AnimatorSet bounceAnimatorSet, additionnalFabClosure, additionnalFabOpening;
    private ObjectAnimator rotationAnimator, fabForwardRotation, fabBackwardRotation;
    private boolean isFabOpen = false;
    private boolean isSnackBarShown = false;
    private MainActivity mActivity;


    public MainActivity() {
        super();
        if (mActivity == null)
            mActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rotating_image = (ImageView) findViewById(R.id.rotating_image);
        if (rotating_image != null)
            rotating_image.setOnClickListener(this);
        setRotation();
        rotationAnimator.start();

        setBounceAnimators();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        fab_more = (FloatingActionButton) findViewById(R.id.fab_more);
        fab_nfc = (FloatingActionButton) findViewById(R.id.fab_nfc);

        setFabRotations();
        setAdditionnalFabsAnimations();

        fab.setOnClickListener(this);
        fab_call.setOnClickListener(this);
        fab_more.setOnClickListener(this);
        fab_nfc.setOnClickListener(this);
    }

    private void setRotation() {
        rotationAnimator = ObjectAnimator.ofFloat(rotating_image, "rotation", 0, 360);
        rotationAnimator.setDuration(4000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimator.setInterpolator(new LinearInterpolator());
    }

    private void setFabRotations() {
        fabForwardRotation = ObjectAnimator.ofFloat(fab, "rotation", 0, 360);
        fabForwardRotation.setDuration(500);
        fabForwardRotation.setInterpolator(new LinearInterpolator());

        fabBackwardRotation = ObjectAnimator.ofFloat(fab, "rotation", 360, 0);
        fabBackwardRotation.setDuration(500);
        fabBackwardRotation.setInterpolator(new LinearInterpolator());
    }


    private void setBounceAnimators() {
        bounceAnimatorSet = new AnimatorSet();
        ObjectAnimator enlargeX = ObjectAnimator.ofFloat(rotating_image, "scaleX", 1, 1.5f);
        enlargeX.setDuration(800);
        enlargeX.setInterpolator(new LinearInterpolator());

        ObjectAnimator enlargeY = ObjectAnimator.ofFloat(rotating_image, "scaleY", 1, 1.5f);
        enlargeY.setDuration(800);
        enlargeY.setInterpolator(new LinearInterpolator());

        ObjectAnimator bounceX = ObjectAnimator.ofFloat(rotating_image, "scaleX", 1.5f, 1);
        bounceX.setDuration(1000);
        bounceX.setInterpolator(new BounceInterpolator());

        ObjectAnimator bounceY = ObjectAnimator.ofFloat(rotating_image, "scaleY", 1.5f, 1);
        bounceY.setDuration(1000);
        bounceY.setInterpolator(new BounceInterpolator());

        bounceAnimatorSet.play(enlargeX).with(enlargeY);
        bounceAnimatorSet.play(bounceY).with(bounceX).after(enlargeY);
    }

    private void setAdditionnalFabsAnimations() {
        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        fabs.add(fab_call);
        fabs.add(fab_more);
        fabs.add(fab_nfc);
        long delayBetweenAnimations = 100L;

        additionnalFabClosure = new AnimatorSet();
        for (int i = 0; i < fabs.size(); i++) {
            FloatingActionButton curFab = fabs.get(i);
            ObjectAnimator fade = ObjectAnimator.ofFloat(curFab, "alpha", 1.0f, 0.0f);
            fade.setDuration(300);
            fade.setInterpolator(new AccelerateInterpolator());

            ObjectAnimator reduceX = ObjectAnimator.ofFloat(curFab, "scaleX", 0.8f, 0.0f);
            reduceX.setDuration(300);
            reduceX.setInterpolator(new LinearInterpolator());

            ObjectAnimator reduceY = ObjectAnimator.ofFloat(curFab, "scaleY", 0.8f, 0.0f);
            reduceY.setDuration(300);
            reduceY.setInterpolator(new LinearInterpolator());

            // We calculate the delay for this Animation, each animation starts 100ms
            // after the previous one
            long delay = i * delayBetweenAnimations;
            additionnalFabClosure.play(reduceX).after(delay);
            additionnalFabClosure.play(reduceX).with(reduceY);
            additionnalFabClosure.play(reduceY).with(fade);
        }

        additionnalFabOpening = new AnimatorSet();
        for (int i = fabs.size()-1; i >= 0; i--) {
            FloatingActionButton curFab = fabs.get(i);
            ObjectAnimator appear = ObjectAnimator.ofFloat(curFab, "alpha", 0.0f, 1.0f);
            appear.setDuration(300);
            appear.setInterpolator(new AccelerateInterpolator());

            ObjectAnimator enlargeX = ObjectAnimator.ofFloat(curFab, "scaleX", 0.0f, 0.8f);
            enlargeX.setDuration(300);
            enlargeX.setInterpolator(new LinearInterpolator());

            ObjectAnimator enlargeY = ObjectAnimator.ofFloat(curFab, "scaleY", 0.0f, 0.8f);
            enlargeY.setDuration(300);
            enlargeY.setInterpolator(new LinearInterpolator());

            // We calculate the delay for this Animation, each animation starts 100ms
            // after the previous one
            long delay = Math.abs(i-fabs.size()) * delayBetweenAnimations;
            additionnalFabClosure.play(enlargeX).after(delay);
            additionnalFabOpening.play(enlargeX).with(enlargeY);
            additionnalFabOpening.play(enlargeY).with(appear);
        }


    }

    /**
     * Manages FAB (Floating Action Button) animations and displays a snackbar.
     */
    public void animateFABWithSnackbar(final Snackbar snackbar) {
        if (isFabOpen) {

            fabForwardRotation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    View[] views = new View[]{fab_nfc, fab_more, fab_call};
                    animateAdditionalFABs(isFabOpen);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (snackbar != null)
                        snackbar.show();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            fabForwardRotation.start();

            fab_nfc.setClickable(false);
            fab_more.setClickable(false);
            fab_call.setClickable(false);
            isFabOpen = false;

        } else {

            fabBackwardRotation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animateAdditionalFABs(isFabOpen);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            fabBackwardRotation.start();
            fab_nfc.setClickable(true);
            fab_more.setClickable(true);
            fab_call.setClickable(true);
            isFabOpen = true;
        }
    }


    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFABWithSnackbar(null);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Manages the delayed animations to display the additional floating action buttons.
     *
     * @param isFabOpen isFabOpen attribute's value at method call
     *                  (animation issue when the attribute itself is used becaused modified before
     *                  being used in this method)
     */
    public void animateAdditionalFABs(final boolean isFabOpen) {
        if (isFabOpen)
            additionnalFabClosure.start();
        else
            additionnalFabOpening.start();
    }

    /**
     * If no NFC Adapter is enabled, a Snackbar suggests to open NFC settings and turn NFC or
     * displays a Toast and opens NFC settings if Android API is under 21.
     * Otherwise, a Snackbar or a Toast tells the user that NFC is already enabled
     */
    protected void manageNfcAdapter() {
        if (!NFCUtils.isNfcEnabled()) {

            //If Android API is high enough to allow Snackbars (21 lollipop)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Snackbar snackbar = Snackbar.make(fab, getResources().getString(R.string.nfc_activation_toast_text), Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white))
                        .setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                fab.setClickable(true);
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                                fab.setClickable(false);
                            }
                        });
                animateFABWithSnackbar(snackbar);

            } else {
                //If Android API is too low to allow Snackbars, we use a Toast
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nfc_activation_toast_text), Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }

        } else { //If NFC Adapter is already enabled

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                Snackbar.make(fab, getResources().getString(R.string.nfc_already_enabled_toast_text), Snackbar.LENGTH_LONG)
                        .show();
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nfc_already_enabled_toast_text), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Displays a Snackbar before calling the developper.
     * The action button cancels the calling.
     */
    protected void callFabAction() {
        //If Android API is high enough to allow Snackbars (21 lollipop)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Snackbar snackbar = Snackbar.make(fab, getResources().getString(R.string.call_toast_text), Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.white))
                    .setAction(getResources().getString(R.string.annuler), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            call(true);
                        }
                    })
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            if (event != DISMISS_EVENT_ACTION && event != DISMISS_EVENT_CONSECUTIVE)
                                call(false);
                            fab.setClickable(true);
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            super.onShown(snackbar);
                            fab.setClickable(false);
                        }
                    });
            animateFABWithSnackbar(snackbar);
        } else {
            //If Android API is too low to allow Snackbars, we use a Toast
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.call_toast_text), Toast.LENGTH_LONG).show();
            call(false);
        }
    }

    /**
     * If canceled is not true, then a CALL Intent is launched,
     * nothing is done otherwise.
     *
     * @param canceled boolean, the call is canceled if true.
     */
    private void call(boolean canceled) {
        if (!canceled) {
            Intent callIntent;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                callIntent = new Intent(Intent.ACTION_CALL);

            } else {
                callIntent = new Intent(Intent.ACTION_DIAL);
            }
            callIntent.setData(Uri.parse("tel:" + "+33606421679"));
            startActivity(callIntent);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rotating_image:
                if (!bounceAnimatorSet.isRunning())
                    bounceAnimatorSet.start();
                break;
            case R.id.fab:
                animateFABWithSnackbar(null);
                break;
            case R.id.fab_nfc:
                manageNfcAdapter();
                break;
            case R.id.fab_more:
                animateFABWithSnackbar(null);
                Toast.makeText(getApplicationContext(), "Touche pas à ce bouton!!", Toast.LENGTH_LONG).show();
                break;
            case R.id.fab_call:
                callFabAction();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


}
