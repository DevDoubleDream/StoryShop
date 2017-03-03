package kr.wdream.Wdream.Util;

import org.json.JSONObject;

import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;

/**
 * Created by deobeuldeulim on 2017. 3. 3..
 */

public class ShopLoginUtil {

    public static HashMap<String,String> SetLogin(HashMap<String,String> paramLogin) throws Exception{

        HashMap<String,String> resultLogin = new HashMap<String,String>();
        String code = "-1";
        String msg  = "로그인 실패";

        resultLogin.put("code", code);
        resultLogin.put("msg", msg);

        String strConnect = Util.getRequestStr(ConstantModel.API_WINCASHSHOP_URL, "POST", paramLogin);

        JSONObject response = new JSONObject(strConnect);

        if (response != null) {
            JSONObject result = response.getJSONObject("RESULT");

            code = result.getString("CODE");
            msg  = result.getString("MESSAGE");

            resultLogin.put("code", code);
            resultLogin.put("msg", msg);

            JSONObject data = response.getJSONObject("DATA");

            String userId = data.getString("SS_USER_ID");
            String userName = data.getString("SS_USER_NAME");
            String userSex = data.getString("SS_USER_SEX");
            String userCode = data.getString("SS_USER_MBCD");
            String userCardNo = data.getString("SS_USER_CARDNO");
            String userDomain = data.getString("SS_USER_DOMAIN");

            resultLogin.put("userId", userId);
            resultLogin.put("userName", userName);
            resultLogin.put("userSex", userSex);
            resultLogin.put("userCode", userCode);
            resultLogin.put("userCardNo", userCardNo);
            resultLogin.put("userDomain", userDomain);

        }

        return resultLogin;
    }
}
