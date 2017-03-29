package com.jelliroo.mallmapbeta.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jelliroo.mallmapbeta.R;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView) findViewById(R.id.imageView3);



    }

    @Override
    protected void onStart() {
        super.onStart();
        imageView.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);
        imageView.startAnimation(bottomUp);
        imageView.setVisibility(View.VISIBLE);

        bottomUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Animation bottomDown = AnimationUtils.loadAnimation(SplashActivity.this,
                                R.anim.fade_out);
                        imageView.startAnimation(bottomDown);
                        bottomDown.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                imageView.setVisibility(View.GONE);
                                Intent intent = new Intent(SplashActivity.this, LaunchActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
