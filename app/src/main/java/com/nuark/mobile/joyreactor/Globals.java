package com.nuark.mobile.joyreactor;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

class Globals {

    static String getJoyUrl(){
        return "http://pornreactor.cc/";
    }

    static SharedPreferences preferences = MainActivity.sPref;

    static String getSavePath(){
        return preferences.getString("save_directory", "");
    }

    static boolean setSavePath(String path){
        try {
            SharedPreferences.Editor ed = preferences.edit();
            ed.putString("save_directory", path);
            ed.apply();
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    static String userName = null;

    static String getUsername(){
        if (userName != null) {
            return userName;
        }
        else {
            return "anon";
        }
    }

    static boolean setUsername(Elements loginHtmlBlock){
        try {
            userName = loginHtmlBlock.select("a[id=settings]").text();
            System.out.println("setUsernameState" + userName);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    static String uAU = null;

    static boolean setUserAvatarUrl(){
        try {
            loadingTask lt = new loadingTask();
            lt.execute();
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    static String getUserAvatarUrl(){
        if (uAU != null) {
            return uAU;
        }
        else {
            return "http://img0.pornreactor.cc/images/default_avatar.jpeg";
        }
    }

    static class loadingTask extends AsyncTask<Void, Void, Void> {
        //http://try.jsoup.org/~a5UybIswFzRGoKv9HQQQe784nlk
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document d = Jsoup.connect(Globals.getJoyUrl() + "user/" + Globals.getUsername()).get();
                uAU = d.select(".sidebar_block .user img").attr("src");
            } catch (Exception ex) {
                System.out.println("ЕГГОГ::: " + ex.getMessage());
            }
            return null;
        }
    }

    static class Cookies {
        static Map<String, String> Cookies = null;

        static Map<String, String> getCookies(){
            return Cookies;
        }

        static boolean setCookies(Map<String, String> cookies){
            try {
                Cookies = cookies;
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        static boolean containCookies(Map<String, String> cookies){
            if (Cookies != null) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
