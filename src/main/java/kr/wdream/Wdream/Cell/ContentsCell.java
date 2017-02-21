package kr.wdream.Wdream.Cell;

import java.util.HashMap;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class ContentsCell {
    private String prdt_id;
    private String prdt_nm;
    private String prdt_path;
    private String sml_img_path;
    private String mdl_img_path;
    private String big_img_path;

    private String code;
    private String msg;

    public ContentsCell(){

    }

    public ContentsCell(String prdt_id, String prdt_nm, String prdt_path, String sml_img_path, String mdl_img_path, String big_img_path){
        this.prdt_id = prdt_id;
        this.prdt_nm = prdt_nm;
        this.prdt_path = prdt_id;
        this.sml_img_path = sml_img_path;
        this.mdl_img_path = mdl_img_path;
        this.big_img_path = big_img_path;
    }

    public String getPrdt_id(){ return prdt_id;}
    public String getPrdt_nm(){ return prdt_nm;}
    public String getPrdt_path(){ return prdt_path;}
    public String getSml_img_path(){ return sml_img_path;}
    public String getMdl_img_path(){ return mdl_img_path;}
    public String getBig_img_path(){ return big_img_path;}

    public String getCode(){return code;}
    public String getMsg(){return msg;}

    public void setCode(String code){ this.code = code;}
    public void setMsg(String msg){ this.msg = msg;}

    public HashMap<String,String> getCodeMsg(){
        HashMap<String,String> returnHash = new HashMap<String,String>();

        returnHash.put("code", code);
        returnHash.put("msg", msg);

        return returnHash;
    }


}
