package kr.wdream.ui.Components;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by deobeuldeulim on 2016. 11. 23..
 */

public class TranUtil {
    public static HashMap<String,String> resultTran(HashMap<String,String> paramTran) throws Exception {
        String strConnect = "";
        String code = "-1";
        String message = "번역 오류, 관리자에게 문의하세요.";
        HashMap<String,String> resultTran = new HashMap<String,String>();

        resultTran.put("code", code);
        resultTran.put("message", message);

        Log.d("TransUtil", "번역시작");

        strConnect = getRequestStr("http://toptalk.ktopplu.co.kr/api/process.php", "POST", paramTran);

        JSONObject response = new JSONObject(strConnect);

        if (response != null) {
            JSONObject result = response.getJSONObject("RESULT");

            code = result.getString("CODE");
            message = result.getString("MESSAGE");

            JSONObject data = response.getJSONObject("DATA");

            resultTran.put("source", data.getString("SOURCE"));
            resultTran.put("translations", data.getString("TRANSLATIONS"));
            resultTran.put("code", code);
            resultTran.put("message", message);
        }


        return resultTran;
    }

    public static String getRequestStr(String url, String method, HashMap<String,String> params) throws Exception{
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;

        BufferedReader br = null;
        String rstGetData = "";
        InputStream inputStream = null;
        OutputStream outputStream = null;

        String paramStr = buildParameter(params);
        if(paramStr != null)
            url += "?" + paramStr;

        URL targetURL = new URL(url);

        try{
            conn = (HttpURLConnection) targetURL.openConnection();

            Log.e("TranUtil", "target : " + targetURL);
            if (conn != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(100000);
                conn.setUseCaches(false);
                conn.setRequestMethod(method);

                if (method.equals("POST")) {
                    conn.setDoOutput(true);
                    outputStream = conn.getOutputStream();
                    outputStream.write(paramStr.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    for(;;){
                        String line = br.readLine();

                        if(line == null)
                            break;

                        sb.append(line);
                    }
                    rstGetData = sb.toString();
                    br.close();
                }
                conn.disconnect();
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (br != null) {
                br.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rstGetData;
    }

    public static String buildParameter(HashMap<String,String> params) throws UnsupportedEncodingException {
        if (params == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for(Iterator<String> i = params.keySet().iterator(); i.hasNext(); ){
            String key = (String)i.next();
            sb.append(key);
            sb.append("=");
            sb.append(URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8"));

            if(i.hasNext())
                sb.append("&");
        }

        return sb.toString();
    }
}
