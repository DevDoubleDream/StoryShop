package kr.wdream.Wdream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Adapter.ProductsAdapter;
import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.LoginUser;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.Wdream.Task.AddProductTask;
import kr.wdream.Wdream.Task.GetProductTask;
import kr.wdream.Wdream.Task.ShoppingLoginTask;
import kr.wdream.Wdream.common.PxToDp;
import kr.wdream.storyshop.R;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2017. 2. 22..
 */

public class ShoppingMainActivity extends Activity implements View.OnClickListener,
        AbsListView.OnScrollListener{

    //UI
    private LinearLayout    main;

    private DrawerLayout    drawerLayout;
    private ListView        listDrawer;

    private RelativeLayout  navigationBar;
    private TextView        txtNaviTitle;
    private ImageView     btnBasket;
    private ImageView     btnMenu;
    private ImageView     btnFinish;

    private ImageView       imgBanner;
    private GridView        gridProduct;
    private ProductsAdapter adapter;

    private ListView navList;

    //Properties
    private ArrayList<Product> arrayProduct = new ArrayList<Product>();
    private String[] drawerTestArray = {"장바구니", "얍얍", "챱챱"};
    private boolean gridLock;

    private HashMap<String,String> loginUserInfo = new HashMap<String,String>();
    private String userId;
    private String userCode;


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

        getLoginUser();

        initView();

        new GetProductTask(ConstantModel.page, ConstantModel.pageSize, handler).execute();
    }

    public void getLoginUser(){
        loginUserInfo = LoginUser.getAutoLoginUser(ShoppingMainActivity.this);

        userId = loginUserInfo.get("userId");
        userCode = loginUserInfo.get("userCode");
    }

    private void initView(){
        drawerLayout    = new DrawerLayout(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(drawerLayout);

        //DrawerLayout 추가
        navList = new ListView(this);
        navList.setBackgroundColor(Color.BLACK);
        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerTestArray);
        navList.setAdapter(drawerAdapter);
        DrawerLayout.LayoutParams paramDrawer = new DrawerLayout.LayoutParams(PxToDp.dpToPx(300), ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);

        drawerLayout.addView(main, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,LayoutHelper.MATCH_PARENT));
        drawerLayout.addView(navList, paramDrawer);

        listDrawer      = new ListView(this);

        navigationBar   = new RelativeLayout(this);
        txtNaviTitle    = new TextView(this);
        btnMenu         = new ImageView(this);
        btnBasket       = new ImageView(this);
        btnFinish       = new ImageView(this);

        imgBanner       = new ImageView(this);
        gridProduct     = new GridView(this);


        //navigationBar 버튼 추가
        RelativeLayout.LayoutParams paramMenu = new RelativeLayout.LayoutParams(PxToDp.dpToPx(26), PxToDp.dpToPx(12));
        paramMenu.addRule(RelativeLayout.CENTER_VERTICAL);
        paramMenu.setMargins(0, 0, PxToDp.dpToPx(11), 0);
        btnMenu.setId(R.id.btnMenu);
        btnMenu.setLayoutParams(paramMenu);
        btnMenu.setImageResource(R.drawable.m_i_shop_menu);
        btnMenu.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnMenu.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout.LayoutParams paramBasket = new RelativeLayout.LayoutParams(PxToDp.dpToPx(22), PxToDp.dpToPx(18));
        paramBasket.addRule(RelativeLayout.RIGHT_OF, R.id.btnMenu);
        paramBasket.addRule(RelativeLayout.CENTER_VERTICAL);
        btnBasket.setLayoutParams(paramBasket);
        btnBasket.setImageResource(R.drawable.m_i_shop_basket);
        btnBasket.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBasket.setBackgroundColor(Color.TRANSPARENT);


        RelativeLayout.LayoutParams paramNaviTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramNaviTitle.addRule(RelativeLayout.CENTER_IN_PARENT);
        txtNaviTitle.setLayoutParams(paramNaviTitle);
        txtNaviTitle.setText("쇼핑몰");
        txtNaviTitle.setTextColor(Color.parseColor("#FEFEFE"));
        txtNaviTitle.setTextSize(20);

        RelativeLayout.LayoutParams paramFinish = new RelativeLayout.LayoutParams(PxToDp.dpToPx(15), PxToDp.dpToPx(15));
        paramFinish.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramFinish.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnFinish.setLayoutParams(paramFinish);
        btnFinish.setImageResource(R.drawable.m_i_shop_finish);
        btnFinish.setScaleType(ImageView.ScaleType.FIT_XY);
        btnFinish.setBackgroundColor(Color.TRANSPARENT);


        navigationBar.setPadding(PxToDp.dpToPx(18), 0, PxToDp.dpToPx(18), 0);
        navigationBar.setBackgroundResource(R.color.shopNaviTitleColor);
        navigationBar.addView(txtNaviTitle);
        navigationBar.addView(btnMenu);
        navigationBar.addView(btnBasket);
        navigationBar.addView(btnFinish);

        //UI 기능 구현
        imgBanner.setImageResource(R.drawable.test_banner);
        imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);

        gridProduct.setNumColumns(GridView.AUTO_FIT);
        gridProduct.setHorizontalSpacing(PxToDp.dpToPx(14));
        gridProduct.setVerticalSpacing(PxToDp.dpToPx(8));
        gridProduct.setGravity(Gravity.CENTER);
        gridProduct.setPadding(PxToDp.dpToPx(8),PxToDp.dpToPx(16), PxToDp.dpToPx(8), 0);
        gridProduct.setBackgroundResource(R.color.lightgray);

        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //임시 로그인 페이지
                        loginUserInfo = LoginUser.getAutoLoginUser(ShoppingMainActivity.this);

                        userId = loginUserInfo.get("userId");
                        userCode = loginUserInfo.get("userCode");
                        Log.d("상은", "userId : " + userId);
                        if(!"".equals(userId)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingMainActivity.this);
                            dialog.setTitle("로그아웃");
                            dialog.setMessage("로그아웃 하시겠습니까?");
                            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginUser.userLogout(ShoppingMainActivity.this);
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } else {
                            Intent intent = new Intent(ShoppingMainActivity.this, ShoppingLoginTask.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

        gridProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product clickProduct = (Product) parent.getAdapter().getItem(position);

                Log.d("상은", "clickProduct : " + clickProduct.getStrProductTitle());

                Intent intent = new Intent(ShoppingMainActivity.this, ProductInfoActivity.class);

                intent.putExtra("title", clickProduct.getStrProductTitle());
                intent.putExtra("price", clickProduct.getStrItemMoney());
                intent.putExtra("productID", clickProduct.getStrProductID());
                intent.putExtra("imagePath", clickProduct.getStrProductImgPath());
                intent.putExtra("productDetail", clickProduct.getStrProductDetail());

                startActivity(intent);
            }
        });
        gridProduct.setOnScrollListener(this);
        btnBasket.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnFinish.setOnClickListener(this);


        LinearLayout.LayoutParams paramNavi = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(50));
        LinearLayout.LayoutParams paramBanner = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDp.dpToPx(155));
        LinearLayout.LayoutParams paramProduct = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//        main.addView(navigationBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
        main.addView(navigationBar, paramNavi);
//        main.addView(imgBanner, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 155));
        main.addView(imgBanner, paramBanner);
//        main.addView(gridProduct, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        main.addView(gridProduct, paramProduct);
    }

    @Override
    public void onClick(View v){
        if (v == btnBasket) {

                Intent intent = new Intent(ShoppingMainActivity.this, BasketActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userCode", userCode);
                startActivity(intent);


        }

        if (v == btnMenu) {
            drawerLayout.openDrawer(navList, true);
        }

        if (v == btnFinish) {
            finish();
        }
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

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            finish();
        }
    }
}
