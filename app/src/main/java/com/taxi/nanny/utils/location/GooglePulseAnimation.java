package com.taxi.nanny.utils.location;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GooglePulseAnimation
{
    public static void addPulseEffect(final LatLng userLatlng, final GoogleMap googleMap)
    {
        final Circle[] lastUserCircle = {null};
        long pulseDuration = 1000;

        if (lastUserCircle[0] != null)
            lastUserCircle[0].setCenter(userLatlng);

        valueAnimate(100, pulseDuration,

                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (lastUserCircle[0] != null)
                            lastUserCircle[0].setRadius((Float) animation.getAnimatedValue());
                        else {
                            lastUserCircle[0] = googleMap.addCircle(new CircleOptions()
                                    .center(userLatlng)
                                    .radius((Float) animation.getAnimatedValue())
                                    .strokeWidth(0.5f)
                                    .fillColor(Color.parseColor("#84FCDBD1"))
                                    .strokeColor(Color.RED));
                        }
                    }
                });

    }

    static ValueAnimator valueAnimate(float accuracy, long duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator va = ValueAnimator.ofFloat(0, accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.start();
        return va;
    }


    public static void setMarkerBounce(final Marker marker)
    {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/duration), 0);
                marker.setAnchor(0.5f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                } else {
                    setMarkerBounce(marker);
                }
            }
        });
    }
}
