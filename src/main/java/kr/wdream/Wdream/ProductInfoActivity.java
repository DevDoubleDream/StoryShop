package kr.wdream.Wdream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 24..
 */

public class ProductInfoActivity extends Activity implements View.OnClickListener{

    //UI
    private ImageView imgProduct;
    private TextView txtProductTitle;
    private TextView txtProductPrice;
    private WebView webView;


    //Properties
    private String productTitle;
    private String productPrice;
    private String productID;
    private String imagePath;
    private String productDetail;

    private RelativeLayout.LayoutParams paramFloating;

    private ArrayAdapter<String> testAdapter;
    private ArrayAdapter<String> selectAdapter;
    private String[] testOption = {"1","2","3","4","5"};
    private String[] testSelect = {"dd","sdf", "234"};

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

                    break;

                case ConstantModel.GET_EACH_PRODUCT_FAILED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productinfo);
        initView();
        GetIntent();


        try {
            new GetImageTask(imagePath).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void initView(){
        imgProduct = (ImageView)findViewById(R.id.imgProduct);
        txtProductTitle = (TextView) findViewById(R.id.txtProductTitle);
        txtProductPrice = (TextView) findViewById(R.id.txtProductPrice);
        webView = (WebView)findViewById(R.id.webView);
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
            case R.id.btnOpen:
            case R.id.btnBuyInFloating:
                // lytOptionTitle
            break;

            case R.id.lytOptionTitle:
                //listOptions
                break;
        }
    }
}
