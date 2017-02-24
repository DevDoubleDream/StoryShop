package kr.wdream.Wdream.Models;

import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class ConstantModel {
    // Http URL
    public static final String API_SHOP_URL = LocaleController.getString("ApiShopUrl", R.string.ApiShopUrl);

    // HttpRequest Result
    public static final int GET_PRODUCTLIST_SUCESS  = 2000;
    public static final int GET_PRODUCTLIST_FAILURE = 2001;
    public static final int ADD_PRODUCTLIST_SUCESS  = 2002;
    public static final int ADD_PRODUCTLIST_FAILURE = 2003;

    public static final String GET_API_SUCCESS      = "1";
    public static final String GET_API_FAILURE      = "-1";

    // request Code & MSG
    public static final String RESULT_ERROR_CODE = LocaleController.getString("ResultCodeError", R.string.ResultCodeError);
    public static final String RESULT_ERROR_MSG  = LocaleController.getString("ResultMsgError", R.string.ResultMsgError);

    // Count for getting products
    public static int page = 1;
    public static int pageSize = 20;
}
