package kr.wdream.Wdream.Cell;

/**
 * Created by parksangeun on 2017. 3. 4..
 */


public class BasketProduct {
    private String productTitle;
    private String productPrice;
    private String productNumber;
    private String productOption;
    private String productImagePath;

    public BasketProduct(String productTitle, String productPrice, String productNumber, String productOption, String productImagePath){
        this.productTitle   = productTitle;
        this.productPrice   = productPrice;
        this.productNumber  = productNumber;
        this.productImagePath = productImagePath;
        this.productOption = productOption;

    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public void setProductOption(String productOption) {
        this.productOption = productOption;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductImagePath() {
        return productImagePath;
    }

    public String getProductOption() {
        return productOption;
    }
}