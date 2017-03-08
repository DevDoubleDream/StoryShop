package kr.wdream.Wdream.Task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Cell.BasketProduct;
import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Util.BasketUtil;

/**
 * Created by parksangeun on 2017. 3. 4..
 */

public class GetBasketListTask extends AsyncTask {
    private HashMap<String,String> paramBasket = new HashMap<String,String>();
    private ArrayList<BasketProduct> resultArray = new ArrayList<BasketProduct>();

    private String userId;
    private String userCode;
    private Handler handler;

    private Bundle bundle;
    private Message msg;

    public GetBasketListTask(String userId, String userCode, Handler handler){
        this.userId   = userId;
        this.userCode = userCode;
        this.handler  = handler;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        paramBasket.put("cmd", "");
        paramBasket.put("user_id", userId);
        paramBasket.put("user_code", userCode);
        paramBasket.put("domain", "");

    }

    @Override
    protected Object doInBackground(Object[] params) {
        try{
            resultArray = BasketUtil.GetBasketList(paramBasket);

            if (resultArray != null) {
                bundle.putSerializable("resultBasket", resultArray);

                msg.what = ConstantModel.GET_BASKET_SUCCESS;
                msg.setData(bundle);

                handler.sendMessage(msg);
            } else {
                handler.sendEmptyMessage(ConstantModel.GET_BASKET_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(ConstantModel.GET_BASKET_FAILED);
        }
        return null;
    }
}
