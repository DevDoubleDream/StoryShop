package kr.wdream.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.wdream.storyshop.R;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2016. 12. 6..
 */

public class AppVersionActivity extends BaseFragment {
    private static final String LOG_TAG = "AppVersionActivity";

    @Override
    public View createView(final Context context) {

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(context.getString(R.string.version));
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
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ImageView imgIcon = new ImageView(context);
        imgIcon.setImageResource(R.drawable.ic_launcher_big);

        TextView currentVersionText = new TextView(context);
        currentVersionText.setTextSize(17);
        currentVersionText.setGravity(Gravity.CENTER);
        currentVersionText.setText(context.getString(R.string.currentVersionText));
        currentVersionText.setTextColor(Color.BLACK);

        TextView currentVersion = new TextView(context);
        currentVersion.setTextSize(17);
        currentVersion.setGravity(Gravity.CENTER);
        currentVersion.setText("1.0.0");
        currentVersion.setTextColor(Color.BLACK);

        Button button = new Button(context);
        button.setTextSize(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
        }else{
            button.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }
        button.setText(context.getString(R.string.latestVersion));
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "뾰로로롱", Toast.LENGTH_SHORT).show();
            }
        });

        linearLayout.addView(imgIcon, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 67, 0, 0));
        linearLayout.addView(currentVersionText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 17, 0, 0));
        linearLayout.addView(currentVersion, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 5, 0, 0));
        linearLayout.addView(button, LayoutHelper.createLinear(157, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 42, 0, 0));

        return fragmentView;
    }
}
