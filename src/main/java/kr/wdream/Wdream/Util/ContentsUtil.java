package kr.wdream.Wdream.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import kr.wdream.Wdream.Cell.ContentsCell;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class ContentsUtil {
    public static ArrayList<ContentsCell> getContents(HashMap<String,String> paramContents) throws Exception {
        ArrayList<ContentsCell> resultContents = new ArrayList<ContentsCell>();
        String code = "-1";
        String msg = "상품 목록을 가져오지 못했습니다. 관리자에게 문의하세요.";

        ContentsCell contentsCell = new ContentsCell();
        contentsCell.setCode(code);
        contentsCell.setMsg(msg);

        String connectHttp = Util.getRequestStr("http://ttalk.wdream.kr/api/process.php", "POST",paramContents);

        JSONObject response = new JSONObject(connectHttp);

        JSONObject result = response.getJSONObject("RESULT");

        if (result != null) {
            code = result.getString("CODE");
            msg = result.getString("MESSAGE");

            contentsCell.setCode(code);
            contentsCell.setMsg(msg);
        }

        JSONObject data = response.getJSONObject("DATA");

        JSONArray arrayList = data.getJSONArray("LIST");

        for(int i=0; i<arrayList.length(); i++){
            JSONObject item = arrayList.getJSONObject(i);

            String prdt_nm = item.getString("PRDT_NM");
            String prdt_id = item.getString("PRDT_ID");
            String prdt_path = item.getString("PRDT_PATH");
            String sml_img_path = item.getString("SML_IMG_PATH");
            String mdl_img_path = item.getString("MDL_IMG_PATH");
            String big_img_path = item.getString("BIG_IMG_PATH");

            resultContents.add(new ContentsCell(prdt_id, prdt_nm, prdt_path, sml_img_path, mdl_img_path, big_img_path));
        }

        return resultContents;

    }
}
