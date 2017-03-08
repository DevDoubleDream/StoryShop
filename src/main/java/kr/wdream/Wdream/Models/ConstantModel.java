package kr.wdream.Wdream.Models;

import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */


/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class ConstantModel {
    // Http URL
    public static final String API_WINCASHSHOP_URL = "http://api.wincash.co.kr/shop/shopprocess.asp";
    public static final String API_IMAGE_URL = "http://data.shopminiso.com";

    // HttpRequest Result
    public static final int GET_PRODUCTLIST_SUCESS    = 2000;
    public static final int GET_PRODUCTLIST_FAILURE   = 2001;
    public static final int ADD_PRODUCTLIST_SUCESS    = 2002;
    public static final int ADD_PRODUCTLIST_FAILURE   = 2003;
    public static final int GET_PRODUCT_IMAGE_SUCCESS = 2004;
    public static final int GET_PRODUCT_IMAGE_FAILURE = 2005;
    public static final int GET_EACH_PRODUCT_SUCCESS  = 2006;
    public static final int GET_EACH_PRODUCT_FAILED   = 2007;
    public static final int GET_BASKET_SUCCESS        = 2008;
    public static final int GET_BASKET_FAILED         = 2009;
    public static final int LOGIN_SHOP_SUCCESS        = 2010;
    public static final int LOGIN_SHOP_FAILED         = 2011;
    public static final int LOGIN_SHOP_ERROR          = 2012;

    public static final String GET_API_SUCCESS      = "0000";
    public static final String GET_API_FAILURE      = "-1";

    // request Code & MSG

    //  ShoppingMall Domain
    public static final String SHOPPING_DOMAIN = "www.wpoint.co.kr";
    public static final String TEST_SHOPPING_DOMAIN = "greatmrpark.wpoint.co.kr";

    // Count for getting products
    public static int page = 1;
    public static int pageSize = 20;

}
