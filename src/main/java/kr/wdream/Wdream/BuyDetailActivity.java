package kr.wdream.Wdream;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.wdream.storyshop.R;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2017. 3. 6..
 */

public class BuyDetailActivity extends Activity {

    //UI
    private LinearLayout rootView;

    private RelativeLayout navigationBar;
    private TextView txtTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        setContentView(rootView);
    }

    public void initView(){
        rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);


        navigationBar = new RelativeLayout(this);
        navigationBar.setBackgroundResource(R.color.shopNaviTitleColor);
        txtTitle = new TextView(this);
        txtTitle.setText("구매하기??");
        txtTitle.setTextColor(Color.WHITE);
        navigationBar.addView(txtTitle, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, 50, RelativeLayout.CENTER_IN_PARENT));

        rootView.addView(navigationBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));



    }
}
