package kr.wdream.Wdream.Models;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class Product {

    //MARK:- Properties
    private String strProductID; // 해당 상품의 아이디 (옵션이 있을 수 있기 때문)
    private String strProductTitle;
    private String strProductImgPath;

    private String strItemMoney;
    private String strProductDetail;


    public Product( String productID, String productTitle, String productImgPath, String itemMoney, String productDetail){

        strProductID      = productID;
        strProductTitle   = productTitle;
        strProductImgPath = productImgPath;

        strItemMoney      = itemMoney;
        strProductDetail  = productDetail;
    }

    public String getStrItemMoney() {
        return strItemMoney;
    }

    public String getStrProductID() {
        return strProductID;
    }

    public String getStrProductImgPath() {
        return strProductImgPath;
    }


    public String getStrProductTitle() {
        return strProductTitle;
    }

    public String getStrProductDetail() {
        return strProductDetail;
    }
}
