package kr.wdream.Wdream.Cell;

/**
 * Created by deobeuldeulim on 2017. 3. 6..
 */

public class ProductOptionCell {
    String optionId;
    String optionName;
    String optionDetailId;
    String optionTitle;

    int optionBaseCount;

    public ProductOptionCell(int optionBaseCount, String optionId, String optionName, String optionDetailId, String optionTitle){
        this.optionBaseCount = optionBaseCount;
        this.optionId = optionId;
        this.optionName = optionName;
        this.optionDetailId = optionDetailId;
        this.optionTitle = optionTitle;
    }

    public void setOptionBaseCount(int optionBaseCount) {
        this.optionBaseCount = optionBaseCount;
    }

    public void setOptionDetailId(String optionDetailId) {
        this.optionDetailId = optionDetailId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public String getOptionDetailId() {
        return optionDetailId;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public int getOptionBaseCount() {
        return optionBaseCount;
    }
}
