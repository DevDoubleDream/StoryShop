package kr.wdream.Wdream.Util;

import android.os.AsyncTask;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class GetProductImageUtil extends AsyncTask {

    //Properties
    private Handler handler;
    private String  strUrl;

    public GetProductImageUtil(String imagePath, Handler handler){
        this.handler = handler;
        this.strUrl     = imagePath;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            URL url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
