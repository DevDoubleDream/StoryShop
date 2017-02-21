package kr.wdream.Wdream.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import kr.wdream.Wdream.Cell.NotiVO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by deobeuldeulim on 2016. 12. 6..
 */

public class NotiUtil {
    public static ArrayList<NotiVO> getNotiList(HashMap<String,String> paramNoti) throws Exception {
        String strConnect = "";
        String code = "-1";
        String msg = "목록을 가져오지 못했습니다.";

        ArrayList<NotiVO> resultNoti = new ArrayList<NotiVO>();

        strConnect = Util.getRequestStr("http://toptalk.ktopplu.co.kr/api/process.php", "POST", paramNoti);

        JSONObject response = new JSONObject(strConnect);

        if (response != null) {
            JSONObject data = response.getJSONObject("DATA");
            JSONObject result = response.getJSONObject("RESULT");

            code = result.getString("CODE");

            if ("0000".equals(code)) {
                JSONArray list = data.getJSONArray("LIST");

                for(int i=0; i<list.length(); i++){
                    JSONObject item = list.getJSONObject(i);

                    String notice_no = item.getString("NOTICE_NO");
                    String subject = item.getString("SUBJECT");

                    if (subject.length() == 0) {
                        subject = "-";
                    }

                    String content = item.getString("CONTENT");
                    if (content.length() == 0) {
                        content = "-";
                    }

                    String update_dt = item.getString("UPDATE_DT");
                    if (update_dt.length() == 0) {
                        update_dt = "-";
                    }

                    resultNoti.add(new NotiVO(notice_no, subject, content, update_dt));
                }
            }
        }

        return resultNoti;
    }
}
