package kr.wdream.Wdream.Models;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class Product {

    //MARK:- Properties
    private String strProductID; // 해당 상품의 아이디 (옵션이 있을 수 있기 때문)
    private String strProductTitle;
    private String strProductKeyword;
    private String strProductImgPath;

    private String strItemCode; // 해당 상품의 코드 (한 상품의 다양한 옵션들도 코드는 동일함)
    private String strItemMoney;


    public Product( String productID, String productTitle, String productKeyword, String productImgPath, String itemCode, String itemMoney){


        strProductID      = productID;
        strProductTitle   = productTitle;
        strProductKeyword = productKeyword;
        strProductImgPath = productImgPath;

        strItemCode       = itemCode;
        strItemMoney      = itemMoney;
    }

    public String getStrItemCode() {
        return strItemCode;
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

    public String getStrProductKeyword() {
        return strProductKeyword;
    }

    public String getStrProductTitle() {
        return strProductTitle;
    }

}
