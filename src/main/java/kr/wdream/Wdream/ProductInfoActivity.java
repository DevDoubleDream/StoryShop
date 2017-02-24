package kr.wdream.Wdream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.wdream.Wdream.Models.Product;

/**
 * Created by deobeuldeulim on 2017. 2. 24..
 */

public class ProductInfoActivity extends Activity {

    //UI
    private RelativeLayout root;

    private TextView txtTitle;
    private TextView txtPrice;

    private WebView webView;

    //Properties
    private ArrayList<Product> productList = new ArrayList<Product>();

    private String productTitle;
    private String productPrice;
    private String itemCode;
    private String productID;
    private String imagePath;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setContentView(root);

        GetIntent();
    }

    private void GetIntent(){
        productTitle = getIntent().getStringExtra("title");
        productPrice = getIntent().getStringExtra("price");
        itemCode     = getIntent().getStringExtra("itemCode");
        productID    = getIntent().getStringExtra("productID");
        imagePath    = getIntent().getStringExtra("imagePath");
        keyword      = getIntent().getStringExtra("keyword");

        Log.d("상은", "productTitle : " + productTitle);
    }

    private void initView(){
        root = new RelativeLayout(this);

        txtTitle = new TextView(this);
        txtPrice = new TextView(this);

    }
}
