package kr.wdream.Wdream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.wdream.Wdream.Adapter.BasketAdapter;
import kr.wdream.Wdream.Cell.BasketProduct;
import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Task.GetBasketListTask;
import kr.wdream.Wdream.common.PxToDp;

/**
 * Created by parksangeun on 2017. 3. 4..
 */

public class BasketActivity extends Activity {

    //UI
    private LinearLayout rootView;
    private RelativeLayout navigationBar;
    private TextView txtTitle;

    private ListView listMain;
    private BasketAdapter adapter;
    //Properties

    private String userId;
    private String userCode;

    private ArrayList<BasketProduct> arrayBasket = new ArrayList<BasketProduct>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentData();

        initView();

        setContentView(rootView);

    }

    public void getIntentData(){
        Intent getIntent = getIntent();

        userId = getIntent.getStringExtra("userId");
        userCode = getIntent.getStringExtra("userCode");

    }

    private void initView(){
        rootView      = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);

        navigationBar = new RelativeLayout(this);
        LinearLayout.LayoutParams paramNavi = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(50));
        navigationBar.setLayoutParams(paramNavi);

        txtTitle      = new TextView(this);
        RelativeLayout.LayoutParams paramTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramTitle.addRule(RelativeLayout.CENTER_IN_PARENT);
        txtTitle.setText("장바구니");
        txtTitle.setTextColor(Color.BLACK);
        txtTitle.setTextSize(PxToDp.dpToPx(5));
        txtTitle.setBackgroundColor(Color.TRANSPARENT);

        navigationBar.addView(txtTitle, paramTitle);

        listMain      = new ListView(this);
        listMain.setBackgroundColor(Color.BLACK);

        rootView.addView(navigationBar);
        rootView.addView(listMain, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetBasketListTask(userId, userCode, handler);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantModel.GET_BASKET_SUCCESS:

                    Bundle bundle = msg.getData();
                    arrayBasket = (ArrayList<BasketProduct>)bundle.getSerializable("arrayBasket");

                    adapter = new BasketAdapter(BasketActivity.this, arrayBasket);
                    listMain.setAdapter(adapter);

                    break;

                case ConstantModel.GET_BASKET_FAILED:
                    Toast.makeText(BasketActivity.this, "장바구니를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}

