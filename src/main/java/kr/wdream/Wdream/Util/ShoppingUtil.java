package kr.wdream.Wdream.Util;

import android.util.Log;

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

    private static String url = ConstantModel.API_WINCASHSHOP_URL;

    public static ArrayList<Product> GetProductList(HashMap<String,String> params) throws Exception {
        ArrayList<Product> resultArray = new ArrayList<Product>();

        String code = "";

        String strConnect = Util.getRequestStr(url, "POST", params);

        JSONObject response = new JSONObject(strConnect);

        JSONObject result = response.getJSONObject("RESULT");

        code = result.getString("CODE");

        if(ConstantModel.GET_API_SUCCESS.equals(code)) {
            JSONObject data = response.getJSONObject("DATA");

            JSONArray array = data.getJSONArray("LIST");

            for (int i = 0; i < array.length(); i++) {
                JSONObject eachProduct = array.getJSONObject(i);

                String productID = eachProduct.getString("PRDT_ID");
                String productTitle = eachProduct.getString("PRDT_NM");
                String productImgPath = eachProduct.getString("BIG_IMG_PATH");
                String itemMoney      = eachProduct.getString("SLL_MNY");
                String productDetail  = eachProduct.getString("PRDT_EXPLN");

                resultArray.add(new Product(productID, productTitle, productImgPath, itemMoney, productDetail));
            }
        } else {
            return null;
        }
        return resultArray;
    }

    public static HashMap<String,String> GetEachProduct(HashMap<String,String> paramProduct) throws Exception {
        HashMap<String,String> resultProduct = new HashMap<String,String>();
        String code = "-1";
        String msg  = "상품정보를 가져오지 못했습니다.";

        resultProduct.put("code", code);
        resultProduct.put("msg", msg);

        String strConnect = Util.getRequestStr(url, "POST", paramProduct);

        JSONObject response = new JSONObject(strConnect);

        if (response != null) {
            JSONObject result = response.getJSONObject("RESULT");

            code = result.getString("CODE");
            msg  = result.getString("MESSAGE");

            JSONObject data = response.getJSONObject("DATA");

            String productTitle     = data.getString("ITEM_TITLE");
            String productKeyword   = data.getString("ITEM_KEYWORD");
            String productPrice     = data.getString("ITEM_MONEY");
            String itemCode         = data.getString("ITEM_CODE");
//            String optionUse        = data.getString("OPTION_USE");

            String productInfo      = data.getString("ITEM_TEXT");

            resultProduct.put("productTitle", productTitle);
            resultProduct.put("productKeyword", productKeyword);
            resultProduct.put("productPrice", productPrice);
            resultProduct.put("itemCode", itemCode);
//            resultProduct.put("optionUse", optionUse);
            resultProduct.put("productInfo", productInfo);
            resultProduct.put("code", code);
            resultProduct.put("msg", msg);
        }

        return resultProduct;

    }
}
