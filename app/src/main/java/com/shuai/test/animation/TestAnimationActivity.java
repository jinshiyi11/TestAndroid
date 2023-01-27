package com.shuai.test.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shuai.test.R;

public class TestAnimationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnStart;
    private View mViewAnimation;
    private AnimatorSet mInAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_animation);

        mBtnStart = findViewById(R.id.btn_start);
        mViewAnimation = findViewById(R.id.view_animation);

        mBtnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start) {
            startInAnimation();
        }
    }

    private void startInAnimation() {
        if (mInAnimation == null) {
            mInAnimation = new AnimatorSet();
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mViewAnimation, "alpha", 0, 1);
            alphaAnimation.setDuration(300);

            ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mViewAnimation,"scaleX",0,1);
            scaleXAnimation.setDuration(500);

            ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mViewAnimation,"scaleY",0,1);
            scaleYAnimation.setDuration(500);

            mInAnimation.playTogether(alphaAnimation,scaleXAnimation,scaleYAnimation);

        }
        //mInAnimation.start();
    }
}
