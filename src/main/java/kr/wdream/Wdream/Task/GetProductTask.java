package kr.wdream.Wdream.Task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.Wdream.Util.ShoppingUtil;

/**
 * Created by deobeuldeulim on 2017. 2. 24..
 */

//상품 목록을 받아오는 비동기식 스레드
public class GetProductTask extends AsyncTask {

    //Properties
    private HashMap<String,String> paramProducts = new HashMap<String,String>();
    private ArrayList<Product> resultProducts    = new ArrayList<Product>();

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
        paramProducts.put("cmd", "zone");
        paramProducts.put("dist_dmn_nm", ConstantModel.SHOPPING_DOMAIN);
        paramProducts.put("zone_id", "LYMBCNTZN2CAT0000");
//        paramProducts.put("page", page);
//        paramProducts.put("pagesize", pageSize);
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
}