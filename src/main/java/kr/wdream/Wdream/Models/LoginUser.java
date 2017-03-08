package kr.wdream.Wdream.Models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by deobeuldeulim on 2017. 3. 8..
 */

public class LoginUser {
    public static String userId;
    public static String userCode;
    public static String userName;
    public static String vizAt;
    public static String rgstViz;

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static void setUserCode(String userCode) {
        LoginUser.userCode = userCode;
    }

    public static void setRgstViz(String rgstViz) {
        LoginUser.rgstViz = rgstViz;
    }

    public static void setUserId(String userId) {
        LoginUser.userId = userId;
    }

    public static void setUserName(String userName) {
        LoginUser.userName = userName;
    }

    public static void setVizAt(String vizAt) {
        LoginUser.vizAt = vizAt;
    }

    public static void setLoginUser(String userId, String userCode, String userName, String vizAt, String rgstViz){
        LoginUser.userId = userId;
        LoginUser.userCode = userCode;
        LoginUser.userName = userName;
        LoginUser.rgstViz = rgstViz;
        LoginUser.vizAt = vizAt;
    }

    public static void setAutoLoginUser(Context context, String userId, String userCode, String userName, String vizAt, String rgstViz){
        LoginUser.userId = userId;
        LoginUser.userCode = userCode;
        LoginUser.userName = userName;
        LoginUser.rgstViz = rgstViz;
        LoginUser.vizAt = vizAt;

        sp = context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("USER_ID", LoginUser.userId);
        editor.putString("USER_CODE", LoginUser.userCode);
        editor.putString("USER_NAME", LoginUser.userName);
        editor.putString("RGST_VIZ", LoginUser.rgstViz);
        editor.putString("VIZ_AT", LoginUser.vizAt);
        editor.commit();
    }

    public static String getRgstViz() {
        return rgstViz;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getVizAt() {
        return vizAt;
    }

    public static String getUserCode() {
        return userCode;
    }

    public static HashMap<String,String> getLoginUser(){
        HashMap<String,String> resultUser = new HashMap<String,String>();

        resultUser.put("userId", userId);
        resultUser.put("userCode", userCode);
        resultUser.put("userName", userName);
        resultUser.put("rgstViz", rgstViz);
        resultUser.put("vizAt", vizAt);

        return resultUser;
    }

    public static HashMap<String,String> getAutoLoginUser(Context context){
        HashMap<String,String> resultUser = new HashMap<String,String>();

        sp = context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        if (!"".equals(sp.getString("USER_ID", ""))) {
            userId = sp.getString("USER_ID", "");
            userCode = sp.getString("USER_CODE", "");
            userName = sp.getString("USER_NAME", "");
            rgstViz  = sp.getString("RGST_VIZ", "");
            vizAt    = sp.getString("VIZ_AT", "");
        } else {
            resultUser.put("userId", userId);
            resultUser.put("userCode", userCode);
            resultUser.put("userName", userName);
            resultUser.put("rgstViz", rgstViz);
            resultUser.put("vizAt", vizAt);
        }

        resultUser.put("userId", userId);
        resultUser.put("userCode", userCode);
        resultUser.put("userName", userName);
        resultUser.put("rgstViz", rgstViz);
        resultUser.put("vizAt", vizAt);

        return resultUser;
    }

    public static void userLogout(Context context){
        sp = context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.clear();
        editor.commit();

        userId = "";
        userCode = "";
        userName = "";
        rgstViz = "";
        vizAt = "";
    }
}
