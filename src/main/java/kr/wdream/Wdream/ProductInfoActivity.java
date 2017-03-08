package kr.wdream.Wdream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.wdream.Wdream.Cell.ProductInformationCell;
import kr.wdream.Wdream.Cell.ProductOptionCell;
import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Task.GetEachProductTask;
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
    private FrameLayout rootView;

    private ImageView imgProduct;
    private TextView txtProductTitle;
    private TextView txtProductPrice;
    private WebView webView;

    private TextView txtProductCode;
    private TextView txtOrigin;
    private TextView txtDeliveryInfo;
    private TextView txtDeliveryMethod;

    private TextView btnInformation;
    private TextView btnNotice;
    private LinearLayout lytNotice;

    private TextView txtSellerNotice;
    private TableLayout tableLayout;

    private LinearLayout lytBaseOption;

    // FloatingLayout
    private RelativeLayout lytFloatingRoot;
    private ImageView imgIncreasing;

    private LinearLayout lytFloatingBuy;
    private RelativeLayout btnBasket;
    private ImageView imgBasket;
    private Button btnFloatingBuy;

    private RelativeLayout btnIncreasing;

    private ListView listSelectedProducts;
    private TextView txtOptionBase;
    private String[] testData = {"1","2","3"};

    //Properties
    private final int DYNAMIC_VIEW_ID = 0x7000;
    private final int DYNAMIC_TEXT_BASE = 0x8000;
    private final int DYNAMIC_TEXT_ADD  = 0x8500;
    private final int DYNAMIC_LIST_BASE = 0x9000;
    private final int DYNAMIC_LIST_ADD  = 0x9500;

    private String productTitle;
    private String productPrice;
    private String productID;
    private String imagePath;
    private String productDetail;

    // GetEachProductTask Properties
    private ArrayList<ProductOptionCell> arrayOptions = new ArrayList<ProductOptionCell>();
    private ArrayList<ProductInformationCell> arrayInform = new ArrayList<ProductInformationCell>();
    private HashMap<String, Object> resultProductInfo = new HashMap<String,Object>();
    private HashMap<String,String> hashDelivery = new HashMap<String,String>();
    private String code;
    private String msg;
    private String productDeliveryPrice;
    private String productDeliveryDC;
    private String productDeliveryFree;
    private String productDeliveryInfo;
    private String productOrigin;

    //Handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what){
                case ConstantModel.GET_PRODUCT_IMAGE_SUCCESS:
                    Bitmap bitmap = bundle.getParcelable("image");

                    //View insertData
                    imgProduct.setImageBitmap(bitmap);
                    txtProductTitle.setText(productTitle);
                    txtProductPrice.setText(productPrice);

                    String summary =  "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>" +
                            "<style>\n" +
                            "table.__se_tbl{width:100% !important; border-left:0 none !important; border-right:0 none !important; border-top:1px solid #acacac !important; border-bottom:1px solid #acacac !important; border-collapse:collapse;}\n" +
                            "table.__se_tbl td{height:28px !important; min-height:28px !important; padding:10px 20px !important; border-top:1px solid #e1e1e1 !important; text-align:left !important; word-break:break-all;}\n" +
                            "table.__se_tbl td:first-child{width:80px !important; height:28px !important; min-height:28px !important; padding:10px 20px !important; border-top:1px solid #e1e1e1 !important; border-left:0 none !important; background-color:#f9f9f9 !important; vertical-align:top !important; color:#333 !important; font-size:14px !important; font-weight:bold !important; line-height:30px !important; text-align:left !important;}\n" +
                            "table.__se_tbl tbody > tr:first-child th{border-top:0 none !important;}\n" +
                            "table.__se_tbl tbody > tr:first-child td{border-top:0 none !important;}\n" +
                            "img{width:100% !important;}\n" +
                            "</style>" +
                            "<html>" +
                            "<body>" +
                            productDetail +
                            "</body>" +
                            "</html>";

                    webView.getSettings().setDefaultTextEncodingName("UTF-8");
                    webView.loadData(summary, "text/html; charset=UTF-8", null);
                    break;

                case ConstantModel.GET_PRODUCT_IMAGE_FAILURE:
                    break;

                case ConstantModel.GET_EACH_PRODUCT_SUCCESS:

                    resultProductInfo = (HashMap<String, Object>) bundle.getSerializable("resultProduct");

                    code = (String)resultProductInfo.get("code");

                    if("0000".equals(code)) {
                        resultProductInfo = (HashMap<String, Object>) bundle.getSerializable("resultProduct");

                        arrayOptions = (ArrayList<ProductOptionCell>) resultProductInfo.get("arrayOptions");
                        arrayInform = (ArrayList<ProductInformationCell>) resultProductInfo.get("arrayInformation");

                        hashDelivery = (HashMap<String, String>) resultProductInfo.get("hashDelivery");

                        productDeliveryPrice = (String)resultProductInfo.get("strProductDeliveryPrice");
                        productDeliveryFree = (String)resultProductInfo.get("strProductDeliveryFree");
                        productDeliveryDC = (String)resultProductInfo.get("strProductDeliveryDC");
                        productDeliveryInfo = (String)resultProductInfo.get("strProductDeliveryInfo");

                        productOrigin = (String)resultProductInfo.get("productOrigin");

                        txtProductCode.setText(productID);
                        txtOrigin.setText(productOrigin);
                        String temp = "";

                        Iterator<String> iter = hashDelivery.keySet().iterator();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            temp += hashDelivery.get(key) + " / ";
                        }
                        temp = temp.substring(0, temp.length() - 3);

                        txtDeliveryInfo.setText((String)resultProductInfo.get("productDeliveryInfo"));
                        txtDeliveryMethod.setText(temp);

                        txtSellerNotice.setText("[전자상거래에 관한 상품정보 제공에 관한 고시] 항목에 의거 [" + resultProductInfo.get("productCompanyName") + "]에 의해 등록된 정보입니다.");
                        int j=0;
                        for(int i=0; i<arrayInform.size(); i++){
                            TableRow row = new TableRow(ProductInfoActivity.this);
                            TableLayout.LayoutParams paramTableRow = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramTableRow.setMargins(PxToDp.dpToPx(16), PxToDp.dpToPx(16), PxToDp.dpToPx(16), 0);
                            row.setId(DYNAMIC_VIEW_ID + i);


                            TableRow.LayoutParams paramRow = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 12);
                            TableRow.LayoutParams paramRow2 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                            TextView txtCol1 = new TextView(ProductInfoActivity.this);
                            txtCol1.setId(DYNAMIC_VIEW_ID + i + 1);
                            txtCol1.setText(arrayInform.get(j).getInfoTitle());
                            txtCol1.setTextColor(Color.parseColor("#999999"));

                            TextView txtCol2 = new TextView(ProductInfoActivity.this);
                            txtCol2.setId(DYNAMIC_VIEW_ID + i + 2);
                            txtCol2.setText(arrayInform.get(j).getInfoText());
                            txtCol2.setTextColor(Color.parseColor("#212121"));
                            txtCol2.setGravity(Gravity.CENTER);

                            row.addView(txtCol1, paramRow);
                            row.addView(txtCol2, paramRow2);

                            tableLayout.addView(row, paramTableRow);

                            j++;
                        }

//                        arrayOptions = (ArrayList<ProductOptionCell>) resultProductInfo.get("arrayOptions");
//                        for(int i=0; i<arrayOptions.size(); i++){
//                            RelativeLayout.LayoutParams paramTxtOptionBase = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                            if (i == 0) {
//                                paramTxtOptionBase.addRule(RelativeLayout.BELOW, R.id.btnIncreasing);
//                            } else {
//                                paramTxtOptionBase.addRule(RelativeLayout.BELOW, DYNAMIC_TEXT_BASE);
//                            }
//
//                            TextView txtOptionsBase = new TextView(ProductInfoActivity.this);
//                            txtOptionsBase.setId(DYNAMIC_TEXT_BASE + i);
//                            txtOptionsBase.setTextColor(Color.parseColor("#212121"));
//                            txtOptionsBase.setTextSize(14);
//                            txtOptionsBase.setText("옵션 선택222");
//                            txtOptionsBase.setBackgroundResource(R.drawable.m_i_detail_notice_background);
//
//
//                            lytBaseOption.addView(txtOptionsBase, paramTxtOptionBase);
//                        }
//
//                        btnIncreasing.setBackgroundResource(R.drawable.m_i_detail_increasing_background);


                    } else {
                        Log.d("상은", "가져오기 실패");
                    }

                    break;

                case ConstantModel.GET_EACH_PRODUCT_FAILED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_productinfo);

        initView();

        GetIntent();

        try {
            new GetImageTask(imagePath).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetEachProductTask(productID, handler).execute();

    }

    private void initView(){
        rootView = (FrameLayout)findViewById(R.id.rootView);
        imgProduct = (ImageView)findViewById(R.id.imgProduct);
        txtProductTitle = (TextView) findViewById(R.id.txtProductTitle);
        txtProductPrice = (TextView) findViewById(R.id.txtProductPrice);

        txtProductCode = (TextView) findViewById(R.id.txtProductCode);
        txtOrigin     = (TextView) findViewById(R.id.txtOrigin);
        txtDeliveryInfo = (TextView)findViewById(R.id.txtDeliveryInfo);
        txtDeliveryMethod = (TextView) findViewById(R.id.txtDeliveryMethod);

        btnInformation = (TextView)findViewById(R.id.btnInformation);
        btnNotice = (TextView)findViewById(R.id.btnNotice);

        lytNotice = (LinearLayout)findViewById(R.id.lytNotice);

        webView = (WebView)findViewById(R.id.webView);
        txtSellerNotice = (TextView)findViewById(R.id.txtSellerNotice);
        tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        lytFloatingRoot = new RelativeLayout(this);


        lytBaseOption = new LinearLayout(this);
        lytBaseOption.setOrientation(LinearLayout.VERTICAL);
        lytBaseOption.setId(R.id.lytBaseOption);
        lytBaseOption.setVisibility(GONE);

        btnIncreasing = new RelativeLayout(this);
        btnIncreasing.setId(R.id.btnIncreasing);
        btnIncreasing.setBackgroundResource(R.drawable.m_i_detail_increasing_background);
        RelativeLayout.LayoutParams paramBtnIncreasing = LayoutHelper.createRelative(41, 16);
        paramBtnIncreasing.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramBtnIncreasing.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        imgIncreasing = new ImageView(this);
        RelativeLayout.LayoutParams paramIncreasing = LayoutHelper.createRelative(11, 6);
        paramIncreasing.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgIncreasing.setImageResource(R.drawable.m_i_detail_up);
        imgIncreasing.setBackgroundColor(Color.TRANSPARENT);

        btnIncreasing.addView(imgIncreasing, paramIncreasing);

        txtOptionBase = new TextView(this);
        RelativeLayout.LayoutParams paramTxtOptionBase = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramTxtOptionBase.addRule(RelativeLayout.BELOW, R.id.btnIncreasing);
        txtOptionBase.setId(R.id.txtOptionBase);
        txtOptionBase.setTextColor(Color.parseColor("#212121"));
        txtOptionBase.setTextSize(14);
        txtOptionBase.setText("옵션 선택");
        txtOptionBase.setBackgroundResource(R.drawable.m_i_detail_notice_background);

        listSelectedProducts = new ListView(this);
        listSelectedProducts.setBackgroundColor(Color.WHITE);
        listSelectedProducts.setVisibility(GONE);

        listSelectedProducts.setId(R.id.listSelected);
        ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testData);
        listSelectedProducts.setAdapter(testAdapter);

        lytFloatingBuy = new LinearLayout(this);
        lytFloatingBuy.setOrientation(LinearLayout.HORIZONTAL);
        lytFloatingBuy.setBackgroundColor(Color.WHITE);
        lytFloatingBuy.setPadding(PxToDp.dpToPx(6), PxToDp.dpToPx(6), PxToDp.dpToPx(6), PxToDp.dpToPx(6));

        btnBasket = new RelativeLayout(this);
        btnBasket.setBackgroundColor(Color.parseColor("#1C1C1C"));
        btnBasket.setOnClickListener(this);

        imgBasket = new ImageView(this);
        imgBasket.setImageResource(R.drawable.m_i_detail_basket);
        btnBasket.addView(imgBasket, LayoutHelper.createRelative(23, 18, RelativeLayout.CENTER_IN_PARENT));

        btnFloatingBuy = new Button(this);
        btnFloatingBuy.setId(R.id.btnFloatingBuy);
        btnFloatingBuy.setText("구매하기");
        btnFloatingBuy.setBackgroundColor(Color.parseColor("#00CCE9"));

        btnIncreasing.setOnClickListener(this);
        btnFloatingBuy.setOnClickListener(this);
        btnBasket.setOnClickListener(this);

        btnInformation.setOnClickListener(this);
        btnNotice.setOnClickListener(this);

        txtOptionBase.setOnClickListener(this);

        lytFloatingRoot.addView(btnIncreasing, paramBtnIncreasing);
        lytFloatingBuy.addView(btnBasket, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 52, (float)4));
        lytFloatingBuy.addView(btnFloatingBuy, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 52, (float)1));

        lytFloatingRoot.addView(txtOptionBase, paramTxtOptionBase);
        RelativeLayout.LayoutParams paramListSelectedProducts = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramListSelectedProducts.addRule(RelativeLayout.BELOW, R.id.btnIncreasing);
        lytFloatingRoot.addView(listSelectedProducts, paramListSelectedProducts);

        RelativeLayout.LayoutParams paramLytFloatingBuy = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramLytFloatingBuy.addRule(RelativeLayout.BELOW, R.id.listSelected);
        lytFloatingRoot.addView(lytFloatingBuy, paramLytFloatingBuy);

        FrameLayout.LayoutParams paramFloatingRoot = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramFloatingRoot.gravity = Gravity.BOTTOM;
        rootView.addView(lytFloatingRoot, paramFloatingRoot);
    }

    private void GetIntent(){
        productTitle = getIntent().getStringExtra("title");
        productPrice = getIntent().getStringExtra("price");
        productID    = getIntent().getStringExtra("productID");
        imagePath    = getIntent().getStringExtra("imagePath");
        productDetail= getIntent().getStringExtra("productDetail");
    }

    //GetImageTask
    private class GetImageTask extends AsyncTask{

        private URL url = null;
        private Message msg = null;
        private Bundle bundle = null;

        public GetImageTask(String url) throws MalformedURLException {
            this.url = new URL(url);
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
            case R.id.btnIncreasing:
            case R.id.btnFloatingBuy:
                txtOptionBase.setVisibility(VISIBLE);
                break;
        }

        if(v == btnBasket){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("장바구니에 담았습니다.");
            dialog.setNegativeButton("장바구니로", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ProductInfoActivity.this, BasketActivity.class);
                    startActivity(intent);

                    finish();
                }
            });

            dialog.setPositiveButton("계속 쇼핑하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        }

        if (v == btnInformation) {
            btnInformation.setBackgroundResource(R.drawable.m_i_detail_text_background);
            btnNotice.setBackgroundResource(R.drawable.m_i_detail_text_background_g);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnInformation.setTextColor(getResources().getColor(R.color.shopNaviTitleColor, null));
                btnNotice.setTextColor(getResources().getColor(R.color.detailTextGray, null));
            } else {
                btnInformation.setTextColor(getResources().getColor(R.color.shopNaviTitleColor));
                btnNotice.setTextColor(getResources().getColor(R.color.detailTextGray));
            }

            lytNotice.setVisibility(GONE);
            webView.setVisibility(VISIBLE);
        }

        if (v == btnNotice) {

            btnInformation.setBackgroundResource(R.drawable.m_i_detail_text_background_g);
            btnNotice.setBackgroundResource(R.drawable.m_i_detail_text_background);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnInformation.setTextColor(getResources().getColor(R.color.detailTextGray, null));
                btnNotice.setTextColor(getResources().getColor(R.color.shopNaviTitleColor, null));
            } else {
                btnInformation.setTextColor(getResources().getColor(R.color.detailTextGray));
                btnNotice.setTextColor(getResources().getColor(R.color.shopNaviTitleColor));
            }

            lytNotice.setVisibility(VISIBLE);
            webView.setVisibility(GONE);
        }

        if (v == txtOptionBase) {
            listSelectedProducts.setVisibility(VISIBLE);
        }

    }
}
