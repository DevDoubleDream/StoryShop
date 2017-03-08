package kr.wdream.Wdream.Cell;

/**
 * Created by deobeuldeulim on 2017. 3. 6..
 */

public class ProductInformationCell {
    String infoTitle;
    String infoText;

    public ProductInformationCell(String infoTitle, String infoText){
        this.infoTitle = infoTitle;
        this.infoText  = infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoText() {
        return infoText;
    }

    public String getInfoTitle() {
        return infoTitle;
    }
}
