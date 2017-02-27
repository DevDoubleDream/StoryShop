package kr.wdream.Wdream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.common.PxToDp;
import kr.wdream.storyshop.R;
import kr.wdream.ui.Components.LayoutHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by deobeuldeulim on 2017. 2. 24..
 */

public class ProductInfoActivity extends Activity implements View.OnClickListener{

    //UI
    private RelativeLayout root;

    private ImageView imgProduct;

    private TextView txtTitle;
    private TextView txtPrice;

    private WebView webView;

    private RelativeLayout floatingLayout;
    private Button btnOpen;
    private Button btnBuyInFloating;

    private LinearLayout lytOptionTitle;
    private TextView txtOptionTitle;

    private ListView listOption;

    private ListView listSelected;

    private LinearLayout lytBottomButton;
    private Button btnBasket;
    private Button btnBuy;

    //Properties
    private String productTitle;
    private String productPrice;
    private String itemCode;
    private String productID;
    private String imagePath;
    private String keyword;

    private RelativeLayout.LayoutParams paramFloating;

    private ArrayAdapter<String> testAdapter;
    private String[] test = {"dd","sdf", "234"};

    //Handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstantModel.GET_PRODUCT_IMAGE_SUCCESS:
                    Bundle bundle = msg.getData();
                    Bitmap bitmap = bundle.getParcelable("image");

                    //View insertData
                    imgProduct.setImageBitmap(bitmap);
                    txtTitle.setText(productTitle);
                    txtPrice.setText(productPrice);

                    break;

                case ConstantModel.GET_PRODUCT_IMAGE_FAILURE:
                    break;
            }
        }
    };

    private static class Metric {
        public static final int IMAGE_WIDTH = 200;
        public static final int FLOATING_LAYOUT_SMALL = 80;

        public static final int BTN_OPEN_WIDTH = 100;
        public static final int BTN_OPEN_HEIGHT = 30;

        public static final int BTN_FLOATING_HEIGHT = 50;

        public static final int OPTION_TITLE_HEIGHT = 30;

        public static final int BTN_TWO_LAYOUT_HEIGHT = 50;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setContentView(root);

        GetIntent();

        try {
            new GetImageTask(imagePath).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void GetIntent(){
        productTitle = getIntent().getStringExtra("title");
        productPrice = getIntent().getStringExtra("price");
        itemCode     = getIntent().getStringExtra("itemCode");
        productID    = getIntent().getStringExtra("productID");
        imagePath    = getIntent().getStringExtra("imagePath");
        keyword      = getIntent().getStringExtra("keyword");
    }

    private void initView(){
        root = new RelativeLayout(this);

        // Image & Text (about Product
        imgProduct = new ImageView(this);
        imgProduct.setId(R.id.imgProduct);

        txtTitle = new TextView(this);
        txtTitle.setId(R.id.txtTitle);
        txtTitle.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams paramTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramTitle.addRule(RelativeLayout.BELOW, R.id.imgProduct);
        txtTitle.setLayoutParams(paramTitle);

        txtPrice = new TextView(this);
        txtPrice.setId(R.id.txtPrice);
        txtPrice.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams paramPrice = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramPrice.addRule(RelativeLayout.BELOW, R.id.txtTitle);
        txtPrice.setLayoutParams(paramPrice);

        // Floating Layout initiating.
        floatingLayout = new RelativeLayout(this);
        floatingLayout.setBackgroundColor(Color.BLUE);
        paramFloating = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(Metric.FLOATING_LAYOUT_SMALL));
        paramFloating.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        floatingLayout.setLayoutParams(paramFloating);

        //Open Button Initiate
        btnOpen = new Button(this);
        btnOpen.setId(R.id.btnOpen);
        btnOpen.setOnClickListener(this);
        btnOpen.setText("열림");
        RelativeLayout.LayoutParams paramButtonOpen = new RelativeLayout.LayoutParams(PxToDp.dpToPx(Metric.BTN_OPEN_WIDTH), PxToDp.dpToPx(Metric.BTN_OPEN_HEIGHT));
        paramButtonOpen.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
        btnOpen.setLayoutParams(paramButtonOpen);

        btnBuyInFloating = new Button(this);
        btnBuyInFloating.setId(R.id.btnBuyInFloating);
        btnBuyInFloating.setOnClickListener(this);
        btnBuyInFloating.setText("구매하기");
        RelativeLayout.LayoutParams paramBtnInFloating = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(Metric.BTN_FLOATING_HEIGHT));
        paramBtnInFloating.addRule(RelativeLayout.BELOW, R.id.btnOpen);
        btnBuyInFloating.setLayoutParams(paramBtnInFloating);

        //Option Title Layout
        lytOptionTitle = new LinearLayout(this);
        lytOptionTitle.setOrientation(LinearLayout.HORIZONTAL);
        lytOptionTitle.setBackgroundColor(Color.WHITE);
        lytOptionTitle.setVisibility(GONE);
        lytOptionTitle.setOnClickListener(this);

        txtOptionTitle = new TextView(this);
        txtOptionTitle.setText("옵션을 선택하세요");
        txtOptionTitle.setTextColor(Color.BLACK);
        txtOptionTitle.setGravity(Gravity.CENTER_VERTICAL);

        lytOptionTitle.addView(txtOptionTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        //Option list initiate
        listOption = new ListView(this);
        listOption.setId(R.id.listOption);
        listOption.setVisibility(GONE);
        testAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); // 삭제 가능
        for(int i=0; i<test.length; i++){
            testAdapter.add(test[i]);
        }
        listOption.setAdapter(testAdapter); // 여기까지 삭제 가능


        //List of Selected products
        listSelected = new ListView(this);
        listSelected.setId(R.id.listSelected);
        listSelected.setVisibility(GONE);
        listSelected.setAdapter(testAdapter); // 삭제 가능

        //Bottom Layout (Two Button)
        lytBottomButton = new LinearLayout(this);
        lytBottomButton.setOrientation(LinearLayout.HORIZONTAL);
        lytBottomButton.setVisibility(GONE);


        //Basket & Buy Button initiate
        LinearLayout.LayoutParams paramTwoButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Metric.BTN_TWO_LAYOUT_HEIGHT, 1);

        btnBasket = new Button(this);
        btnBasket.setText("장바구니");
        btnBasket.setOnClickListener(this);
        btnBasket.setLayoutParams(paramTwoButton);

        btnBuy = new Button(this);
        btnBuy.setText("구매하기");
        btnBuy.setOnClickListener(this);
        btnBuy.setLayoutParams(paramTwoButton);

        lytBottomButton.addView(btnBasket);
        lytBottomButton.addView(btnBuy);

        // Add SubLayout to FloatingLayout
        floatingLayout.addView(btnOpen);
        floatingLayout.addView(btnBuyInFloating);
        floatingLayout.addView(lytOptionTitle);
        floatingLayout.addView(listOption);
        floatingLayout.addView(listSelected);
        floatingLayout.addView(lytBottomButton, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        // Add Layout to Root
        root.addView(imgProduct, LayoutHelper.createRelative(Metric.IMAGE_WIDTH, Metric.IMAGE_WIDTH));
        root.addView(txtTitle);
        root.addView(txtPrice);

        root.addView(floatingLayout);
    }

    //GetImageTask
    private class GetImageTask extends AsyncTask{

        private URL url = null;
        private Message msg = null;
        private Bundle bundle = null;

        public GetImageTask(String url) throws MalformedURLException {
            this.url = new URL(ConstantModel.API_IMAGE_URL + url);
            msg = new Message();

            msg.what = ConstantModel.GET_PRODUCT_IMAGE_FAILURE;
            bundle = new Bundle();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

                bundle.putParcelable("image", bitmap);
                msg.setData(bundle);
                msg.what = ConstantModel.GET_PRODUCT_IMAGE_SUCCESS;

                handler.sendMessage(msg);

            } catch (IOException e) {

                handler.sendMessage(msg);

                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpen:
            case R.id.btnBuyInFloating:
                // lytOptionTitle
                RelativeLayout.LayoutParams paramLytOptionTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(Metric.OPTION_TITLE_HEIGHT));
                paramLytOptionTitle.addRule(RelativeLayout.BELOW, R.id.btnOpen);
                lytOptionTitle.setLayoutParams(paramLytOptionTitle);

                // listSelected
                RelativeLayout.LayoutParams paramListSelected = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramListSelected.addRule(RelativeLayout.BELOW, R.id.lytOptionTitle);
                listSelected.setLayoutParams(paramListSelected);

                //lytBottomButton
                RelativeLayout.LayoutParams paramLytBottom = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Metric.BTN_TWO_LAYOUT_HEIGHT);
                paramLytBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                lytBottomButton.setLayoutParams(paramLytBottom);

                paramFloating = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramFloating.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                floatingLayout.setLayoutParams(paramFloating);

                btnBuyInFloating.setVisibility(GONE);

                listOption.setVisibility(VISIBLE);
                lytBottomButton.setVisibility(VISIBLE);

                break;

            case R.id.lytOptionTitle:
                //listOptions
                RelativeLayout.LayoutParams paramListOption = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramListOption.addRule(RelativeLayout.BELOW, R.id.lytOptionTitle);
                listOption.setLayoutParams(paramListOption);

                listOption.setVisibility(VISIBLE);
                break;
        }
    }
}
