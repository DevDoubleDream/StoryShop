package kr.wdream.Wdream.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.wdream.Wdream.Cell.ProductInformationCell;
import kr.wdream.Wdream.Cell.ProductOptionCell;
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

    public static HashMap<String,Object> GetEachProduct(HashMap<String,String> paramProduct) throws Exception {
        HashMap<String,Object> resultProduct = new HashMap<String,Object>();
        String code = "-1";
        String msg  = "상품정보를 가져오지 못했습니다.";

        resultProduct.put("code", code);
        resultProduct.put("msg", msg);

        String strConnect = Util.getRequestStr(url, "POST", paramProduct);

        JSONObject response = new JSONObject(strConnect);

        if (response != null) {
            JSONObject result = response.getJSONObject("RESULT");

            code = result.getString("CODE");
            Log.d("상은", "Code in util : " + code);
            msg  = result.getString("MESSAGE");

            JSONObject data = response.getJSONObject("DATA");

            JSONObject product = data.getJSONObject("PRODUCT");

            String productId = product.getString("PRDT_ID");
            String productTitle = product.getString("PRDT_NM");
            String productImage = product.getString("BIG_IMG");
            String productOrigin = product.getString("CNTRY");
            String productPrice = product.getString("SLL_MNY");

            HashMap<String,String> hashDelivery = new HashMap<String,String>();

            JSONArray arrayDelivery = product.getJSONArray("PRODUCTDLVRYMTHD");
            for(int i=0; i<arrayDelivery.length(); i++){
                JSONObject jsonProduct = arrayDelivery.getJSONObject(i);

                hashDelivery.put(jsonProduct.getString("DLVRY_MTHD_ID"), jsonProduct.getString("DLVRY_MTHD"));
            }

            JSONObject productDeliveryPrice = product.getJSONObject("PRODUCTDLVRY");
            String strProductDeliveryPrice = productDeliveryPrice.getString("BNDL_DLVRY_MNY");
            String strProductDeliveryDC    = productDeliveryPrice.getString("DC_DLVRY_MNY_YN");
            String strProductDeliveryFree  = productDeliveryPrice.getString("DC_DLVRY_MNY_BASE");

            String productDeliveryInfo     = product.getString("DLVRY_PSB_CNT_CMNT");
            String productCompanyName      = product.getString("CO_NM");

            JSONArray optionBase = product.getJSONArray("PRODUCTOPTIONBASE");

            int optionBaseCount = optionBase.length();
            Log.d("상은", "optionBaseCount : " + optionBaseCount);

            ArrayList<ProductOptionCell> arrayOptions = new ArrayList<ProductOptionCell>();
            for (int i=0; i<optionBase.length(); i++){
                JSONObject option = optionBase.getJSONObject(i);

                String optionId = option.getString("OPTN_ID");
                String optionName = option.getString("OPTN_NM");
                JSONArray arrayDetail = option.getJSONArray("OPTIONLIST");
                for(int j=0; j<arrayDetail.length(); j++){
                    JSONObject optionDetail = arrayDetail.getJSONObject(j);
                    String optionDetailId = optionDetail.getString("OPTN_DTL_ID");
                    String optionTitle    = optionDetail.getString("OPTN_DTL_NM");

                    Log.d("상은", "optionBaseCount : " + optionBaseCount);

                    arrayOptions.add(new ProductOptionCell(optionBaseCount, optionId, optionName, optionDetailId, optionTitle));
                }
            }

            JSONArray arrayProductInformation = data.getJSONArray("PRODUCTNOTICE");
            ArrayList<ProductInformationCell> arrayInformation = new ArrayList<ProductInformationCell>();

            for (int j=0; j<arrayProductInformation.length(); j++){
                JSONObject eachInform = arrayProductInformation.getJSONObject(j);

                String infromTitle = eachInform.getString("ITEM_NAME");
                String informText  = eachInform.getString("ITEM_VALUE_D");

                arrayInformation.add(new ProductInformationCell(infromTitle, informText));
            }

            resultProduct.put("code", code);
            resultProduct.put("msg", msg);
            resultProduct.put("productId", productId);
            resultProduct.put("productTitle", productTitle);
            resultProduct.put("productImage", productImage);
            resultProduct.put("productOrigin", productOrigin);
            resultProduct.put("productPrice", productPrice);
            resultProduct.put("hashDelivery", hashDelivery);
            resultProduct.put("strProductDeliveryPrice", strProductDeliveryPrice);
            resultProduct.put("strProductDeliveryDC", strProductDeliveryDC);
            resultProduct.put("strProductDeliveryFree", strProductDeliveryFree);
            resultProduct.put("productDeliveryInfo", productDeliveryInfo);
            resultProduct.put("arrayOptions", arrayOptions);
            resultProduct.put("arrayInformation", arrayInformation);
            resultProduct.put("productCompanyName", productCompanyName);

        }

        return resultProduct;

    }
}
