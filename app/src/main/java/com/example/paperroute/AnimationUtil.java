package com.example.paperroute;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class AnimationUtil {

    public static void animateCardView(View view, final Runnable onAnimationEnd) {

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.8f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.8f);
        scaleXAnimator.setDuration(200);
        scaleYAnimator.setDuration(200);

        // Scale up animation
        ObjectAnimator scaleXReverseAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1f);
        ObjectAnimator scaleYReverseAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1f);
        scaleXReverseAnimator.setDuration(200);
        scaleYReverseAnimator.setDuration(200);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleXAnimator).with(scaleYAnimator);
        animatorSet.play(scaleXReverseAnimator).with(scaleYReverseAnimator).after(scaleXAnimator);


        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
        });

        // Start the animation
        animatorSet.start();
    }

    public static void animateImageView(ImageView profileButton , final Runnable onAnimationEnd) {

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(profileButton, View.SCALE_X, 1f, 0.8f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(profileButton, View.SCALE_Y, 1f, 0.8f);
        scaleXAnimator.setDuration(200);
        scaleYAnimator.setDuration(200);

        // Scale up animation
        ObjectAnimator scaleXReverseAnimator = ObjectAnimator.ofFloat(profileButton, View.SCALE_X, 0.8f, 1f);
        ObjectAnimator scaleYReverseAnimator = ObjectAnimator.ofFloat(profileButton, View.SCALE_Y, 0.8f, 1f);
        scaleXReverseAnimator.setDuration(200);
        scaleYReverseAnimator.setDuration(200);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleXAnimator).with(scaleYAnimator);
        animatorSet.play(scaleXReverseAnimator).with(scaleYReverseAnimator).after(scaleXAnimator);


        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
        });

    }
}
