package kr.wdream.Wdream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.wdream.Wdream.Adapter.ProductsAdapter;
import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.Wdream.Util.AddProductTask;
import kr.wdream.Wdream.Util.GetProductTask;
import kr.wdream.storyshop.R;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2017. 2. 22..
 */

public class ShoppingMainActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener{

    //UI
    private LinearLayout    main;

    private DrawerLayout    drawerLayout;
    private ListView        listDrawer;

    private RelativeLayout  navigationBar;
    private TextView        txtNaviTitle;
    private ImageButton     btnBasket;
    private ImageButton     btnMenu;

    private GridView        gridProduct;
    private ProductsAdapter adapter;

    //Properties
    private ArrayList<Product> arrayProduct = new ArrayList<Product>();

    private boolean gridLock;


    // 각 스레드들의 동작 완료 후 Main 스레드 동작
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ConstantModel.GET_PRODUCTLIST_SUCESS:
                    Bundle bundle = msg.getData();

                    arrayProduct = (ArrayList<Product>) bundle.getSerializable("productList");

                    adapter = new ProductsAdapter(ShoppingMainActivity.this, arrayProduct);
                    adapter.notifyDataSetChanged();
                    gridProduct.setAdapter(adapter);

                    break;

                case ConstantModel.GET_PRODUCTLIST_FAILURE:
                    Toast.makeText(ShoppingMainActivity.this, getResources().getString(R.string.ResultMsgError), Toast.LENGTH_SHORT).show();
                    break;

                case ConstantModel.ADD_PRODUCTLIST_SUCESS:
                    Bundle addBundle = msg.getData();

                    ArrayList<Product> arrayAdd = (ArrayList<Product>) addBundle.getSerializable("productList");

                    arrayProduct.addAll(arrayAdd);
                    adapter.notifyDataSetChanged();

                    gridLock = false;
                    break;

                case ConstantModel.ADD_PRODUCTLIST_FAILURE:
                    break;
            }
        }
    };

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        new GetProductTask(ConstantModel.page, ConstantModel.pageSize, handler).execute();
    }

    private void initView(){
        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        setContentView(main);

        drawerLayout    = new DrawerLayout(this);
        listDrawer      = new ListView(this);

        navigationBar   = new RelativeLayout(this);
        txtNaviTitle    = new TextView(this);
        btnMenu         = new ImageButton(this);
        btnBasket       = new ImageButton(this);

        gridProduct     = new GridView(this);


        //UI 기능 구현
        gridProduct.setNumColumns(GridView.AUTO_FIT);
        gridProduct.setGravity(Gravity.CENTER);

        gridProduct.setOnItemClickListener(this);
        gridProduct.setOnScrollListener(this);
        btnBasket.setOnClickListener(this);
        btnMenu.setOnClickListener(this);


        main.addView(gridProduct, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    @Override
    public void onClick(View v){
        if (v == btnBasket) {

        }

        if (v == btnMenu) {

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product clickProduct = (Product) parent.getAdapter().getItem(position);

        Log.d("상은", "clickProduct : " + clickProduct.getStrProductTitle());

        Intent intent = new Intent(ShoppingMainActivity.this, ProductInfoActivity.class);

        intent.putExtra("title", clickProduct.getStrProductTitle());
        intent.putExtra("price", clickProduct.getStrItemMoney());
        intent.putExtra("itemCode",clickProduct.getStrItemCode());
        intent.putExtra("productID", clickProduct.getStrProductID());
        intent.putExtra("imagePath", clickProduct.getStrProductImgPath());
        intent.putExtra("keyword", clickProduct.getStrProductKeyword());

        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int count = totalItemCount - visibleItemCount;

        if (firstVisibleItem >= count && totalItemCount != 0 && !gridLock) {
            // 리스트 뒤에 붙이기
            gridLock = true;
            ConstantModel.page++;
            new AddProductTask(ConstantModel.page, ConstantModel.pageSize, handler).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConstantModel.page = 1;
    }
}
