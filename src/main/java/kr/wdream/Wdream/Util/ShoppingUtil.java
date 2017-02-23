package kr.wdream.Wdream.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class ShoppingUtil {

    private static String url = ConstantModel.API_SHOP_URL + "product/process.json";

    public static ArrayList<Product> GetProductList(HashMap<String,String> params) throws Exception {
        ArrayList<Product> resultArray = new ArrayList<Product>();

        String code = "";

        String strConnect = Util.getRequestStr(url, "POST", params);

        JSONObject response = new JSONObject(strConnect);

        JSONObject result = response.getJSONObject("RESULT");

        code = result.getString("CODE");

        if(ConstantModel.GET_API_SUCCESS.equals(code)) {
            JSONArray data = response.getJSONArray("DATA");

            for (int i = 0; i < data.length(); i++) {
                JSONObject eachProduct = data.getJSONObject(i);

                String productID = eachProduct.getString("ID");
                String productTitle = eachProduct.getString("ITEM_TITLE");
                String productKeyWord = eachProduct.getString("ITEM_KEYWORD");
                String productImgPath = eachProduct.getString("ITEM_IMG");
                String itemCode = eachProduct.getString("ITEM_CODE");
                String itemMoney = eachProduct.getString("ITEM_MONEY");

                resultArray.add(new Product(productID, productTitle, productKeyWord, productImgPath, itemCode, itemMoney));
            }
        } else {
            return null;
        }
        return resultArray;
    }
}
