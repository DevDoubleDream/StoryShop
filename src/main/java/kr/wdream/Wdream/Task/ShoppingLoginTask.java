package kr.wdream.Wdream.Task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Util.ShopLoginUtil;

/**
 * Created by deobeuldeulim on 2017. 3. 7..
 */

public class ShoppingLoginTask extends AsyncTask {

    private Bundle bundle;
    private Message msg;

    private String code = "-1";
    private String username;
    private String password;
    private Handler handler;

    private HashMap<String,String> paramLogin = new HashMap<String,String>();
    private HashMap<String,String> resultLogin = new HashMap<String,String>();

    public ShoppingLoginTask(String username, String password, Handler handler){
        this.username = username;
        this.password = password;
        this.handler  = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bundle = new Bundle();
        msg = new Message();

        paramLogin.put("cmd", "login");
        paramLogin.put("dist_dmn_nm", ConstantModel.TEST_SHOPPING_DOMAIN);
        paramLogin.put("user_id", username);
        paramLogin.put("user_pw", password);
        paramLogin.put("user_grade", "U");

    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            resultLogin = ShopLoginUtil.SetLogin(paramLogin);

            code = resultLogin.get("code");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if ("0000".equals(code)) {
            bundle.putSerializable("resultLogin", resultLogin);

            msg.setData(bundle);
            msg.what = ConstantModel.LOGIN_SHOP_SUCCESS;

            handler.sendMessage(msg);

        } else if ("-1".equals(code)){
            handler.sendEmptyMessage(ConstantModel.LOGIN_SHOP_ERROR);
        } else {
            bundle.putSerializable("resultLogin", resultLogin);

            msg.setData(bundle);
            msg.what = ConstantModel.LOGIN_SHOP_FAILED;

            handler.sendMessage(msg);
        }
    }
}
