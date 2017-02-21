package kr.wdream.Wdream.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class Util {
    private static final String LOG_TAG = "UTIL";
    public static String getRequestStr(String url, String method, HashMap<String,String> params) throws Exception{
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;

        BufferedReader br = null;
        String rstGetData = "";
        InputStream inputStream = null;
        OutputStream outputStream = null;

        String paramStr = buildParameters(params);

        if(paramStr != "")
            url += "?" + paramStr;

        URL targetUrl = new URL(url);

        Log.d(LOG_TAG, "targetURL : " + targetUrl);

        conn = (HttpURLConnection) targetUrl.openConnection();

        if (conn != null) {
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(1000);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);

            if ("POST".equals(method)) {
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
                    if(line==null)
                        break;

                    sb.append(line);
                }

                rstGetData = sb.toString();
                br.close();
            }
            conn.disconnect();
        }

        if (br != null) {
            br.close();
        }

        if(conn!=null)
            conn.disconnect();


        return rstGetData;
    }

    public static String buildParameters(HashMap<String,String> params) throws UnsupportedEncodingException {
        if(params==null)
            return "";

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
