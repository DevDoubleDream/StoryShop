package kr.wdream.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.wdream.storyshop.LocaleController;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2016. 12. 6..
 */

public class CenterActivity extends BaseFragment {
    @Override
    public View createView(Context context) {

        actionBar.setBackButtonImage(kr.wdream.storyshop.R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Center", kr.wdream.storyshop.R.string.center));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });


        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView = linearLayout;
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //        ((Frame) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            frameLayout.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray, null));
        }else{
            frameLayout.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray));
        }

        linearLayout.addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 300));

        ImageView imgView = new ImageView(context);
        imgView.setImageResource(kr.wdream.storyshop.R.drawable.ic_launcher_big);

        frameLayout.addView(imgView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        LinearLayout phoneLayout = new LinearLayout(context);
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imgPhone = new ImageView(context);
        imgPhone.setImageResource(kr.wdream.storyshop.R.drawable.m_i_setting_center);

        TextView txtPhoneText = new TextView(context);
        txtPhoneText.setText(context.getString(kr.wdream.storyshop.R.string.phoneNumberText));
        txtPhoneText.setTextColor(Color.BLACK);

        TextView txtPhone = new TextView(context);
        txtPhone.setGravity(Gravity.RIGHT);
        txtPhone.setText(context.getString(kr.wdream.storyshop.R.string.phoneNumber));
        txtPhone.setTextColor(Color.BLACK);

        phoneLayout.addView(imgPhone, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 17, 0, 0, 0));
        phoneLayout.addView(txtPhoneText, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 35, 0, 0, 0));
        phoneLayout.addView(txtPhone, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 0, 0, 17, 0));

        linearLayout.addView(phoneLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 57, Gravity.CENTER_VERTICAL));

        LinearLayout divider = new LinearLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            divider.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray, null));
        }else{
            divider.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray));
        }

        linearLayout.addView(divider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));

        LinearLayout timeLayout = new LinearLayout(context);
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imgTime = new ImageView(context);
        imgTime.setImageResource(kr.wdream.storyshop.R.drawable.m_i_setting_time);

        TextView txtTimeText = new TextView(context);
        txtTimeText.setText(context.getString(kr.wdream.storyshop.R.string.timeText));
        txtTimeText.setTextColor(Color.BLACK);

        TextView txtTime = new TextView(context);
        txtTime.setGravity(Gravity.RIGHT);
        txtTime.setText(context.getString(kr.wdream.storyshop.R.string.time));
        txtTime.setTextColor(Color.BLACK);

        timeLayout.addView(imgTime, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 17, 0, 0, 0));
        timeLayout.addView(txtTimeText, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 32, 0, 0, 0));
        timeLayout.addView(txtTime, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 0, 0, 17, 0));

        linearLayout.addView(timeLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 57, Gravity.CENTER_VERTICAL));


        LinearLayout divider2 = new LinearLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            divider2.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray, null));
        }else{
            divider2.setBackgroundColor(context.getResources().getColor(kr.wdream.storyshop.R.color.lightgray));
        }

        linearLayout.addView(divider2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));


        return fragmentView;
    }


    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {

    }
}
