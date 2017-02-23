package kr.wdream.Wdream;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.Wdream.Util.ShoppingUtil;
import kr.wdream.Wdream.Util.Util;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 22..
 */

public class ShoppingMainActivity extends Activity implements View.OnClickListener{

    //UI
    private LinearLayout    main;

    private DrawerLayout    drawerLayout;
    private ListView        listDrawer;

    private RelativeLayout  navigationBar;
    private TextView        txtNaviTitle;
    private ImageButton     btnBasket;
    private ImageButton     btnMenu;

    private GridView        gridProduct;

    //Properties
    private ArrayList<Product> arrayProduct = new ArrayList<Product>();

    private int page;
    private int pageSize;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ConstantModel.GET_PRODUCTLIST_SUCESS:
                    Bundle bundle = msg.getData();

                    arrayProduct = (ArrayList<Product>) bundle.getSerializable("productList");

                    break;

                case ConstantModel.GET_PRODUCTLIST_FAILURE:
                    Toast.makeText(ShoppingMainActivity.this, getResources().getString(R.string.ResultMsgError), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        setContentView(main);

        initView();
    }

    private void initView(){
        drawerLayout    = new DrawerLayout(this);
        listDrawer      = new ListView(this);

        navigationBar   = new RelativeLayout(this);
        txtNaviTitle    = new TextView(this);
        btnMenu         = new ImageButton(this);
        btnBasket       = new ImageButton(this);

        gridProduct     = new GridView(this);

        btnBasket.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v == btnBasket) {

        }

        if (v == btnMenu) {

        }
    }

    //상품 목록을 받아오는 비동기식 스레드
    private class GetProductTask extends AsyncTask {

        //Properties
        private HashMap<String,String> paramProducts = new HashMap<String,String>();
        private ArrayList<Product> resultProducts    = new ArrayList<Product>();

        private String url = ConstantModel.API_SHOP_URL + "product/process.php";
        private String page;
        private String pageSize;


        private Handler handler; // MainThread에서 결과를 반영하기 위한 Handler

        public GetProductTask(int page, int pageSize, Handler handler){
            this.page     = String.valueOf(page);
            this.pageSize = String.valueOf(pageSize);
            this.handler  = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paramProducts.put("cmd", "/product/process.json");
            paramProducts.put("mode", "L");
            paramProducts.put("page", page);
            paramProducts.put("pagesize", pageSize);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                resultProducts = ShoppingUtil.GetProductList(paramProducts);

                if (resultProducts != null) {

                    Message msg = new Message();
                    msg.what    = ConstantModel.GET_PRODUCTLIST_SUCESS;

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("productList", resultProducts);
                    msg.setData(bundle);

                    handler.sendMessage(msg);

                } else {
                    handler.sendEmptyMessage(ConstantModel.GET_PRODUCTLIST_FAILURE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
}
