package kr.wdream.Wdream.Util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;

/**
 * Created by deobeuldeulim on 2017. 3. 2..
 */

public class GetEachProductTask extends AsyncTask {
    //Properties
    private HashMap<String,String> paramProduct = new HashMap<String,String>();
    private HashMap<String,String> resultProduct = new HashMap<String,String>();

    private String itemCode;
    private Handler handler;

    private Bundle bundle;
    private Message msg;

    public GetEachProductTask(String itemCode, Handler handler){
        this.itemCode = itemCode;
        this.handler  = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        paramProduct.put("cmd", "/product/process.json");
        paramProduct.put("mode", "R");
        paramProduct.put("item_code", itemCode);

        msg = new Message();
        bundle = new Bundle();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            resultProduct = ShoppingUtil.GetEachProduct(paramProduct);

            bundle.putSerializable("resultProduct", resultProduct);

            msg.what = ConstantModel.GET_EACH_PRODUCT_SUCCESS;
            msg.setData(bundle);

            handler.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
            resultProduct.put("code", "-1");
            resultProduct.put("msg", "상품 정보를 가져올 수 없습니다.");

            bundle.putSerializable("resultProduct", resultProduct);

            msg.what = ConstantModel.GET_EACH_PRODUCT_FAILED;
            msg.setData(bundle);

            handler.sendMessage(msg);
        }
        return null;
    }
}
