package kr.wdream.Wdream.Util;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Cell.BasketProduct;

/**
 * Created by parksangeun on 2017. 3. 4..
 */

public class BasketUtil {
    public static ArrayList<BasketProduct> GetBasketList (HashMap<String,String> paramBasket) throws Exception{
        ArrayList<BasketProduct> resultArray = new ArrayList<BasketProduct>();

        String strConnect = Util.getRequestStr("","POST", paramBasket);

        JSONObject response = new JSONObject(strConnect);

        JSONObject result = response.getJSONObject("RESULT");

        String code = result.getString("CODE");
        String msg  = result.getString("RESULT");

        if ("0000".equals(code)) {
            JSONObject data = response.getJSONObject("DATA");

            JSONArray arrayProduct = data.getJSONArray("LIST");

            for (int i=0; i<arrayProduct.length(); i++){
                JSONObject product = arrayProduct.getJSONObject(i);

                String productTitle = product.getString("");
                String productPrice = product.getString("");
                String productNumber = product.getString("");
                String productOption = product.getString("");
                String productImagePath = product.getString("");

                resultArray.add(new BasketProduct(productTitle, productPrice, productNumber, productOption, productImagePath));
            }
        }
        return resultArray;
    }
}
