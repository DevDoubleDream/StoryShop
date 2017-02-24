package kr.wdream.Wdream.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;

/**
 * Created by deobeuldeulim on 2017. 2. 24..
 */

public class AddProductTask extends AsyncTask{
    //Properties
    private HashMap<String,String> paramProducts = new HashMap<String,String>();
    private ArrayList<Product> resultProducts    = new ArrayList<Product>();

    private String page;
    private String pageSize;
    private Handler handler; // MainThread에서 결과를 반영하기 위한 Handler


    public AddProductTask(int page, int pageSize, Handler handler){
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
                msg.what    = ConstantModel.ADD_PRODUCTLIST_SUCESS;

                Bundle bundle = new Bundle();
                bundle.putSerializable("productList", resultProducts);
                msg.setData(bundle);

                handler.sendMessage(msg);

            } else {
                handler.sendEmptyMessage(ConstantModel.ADD_PRODUCTLIST_FAILURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
