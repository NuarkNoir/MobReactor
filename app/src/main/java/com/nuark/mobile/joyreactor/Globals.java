package com.nuark.mobile.joyreactor;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

class Globals {

    static String getJoyUrl() {
        return "http://pornreactor.cc/";
    }

    private static final SharedPreferences preferences = MainActivity.sPref;

    static String getSavePath() {
        return preferences.getString("save_directory", "");
    }

    static void setSavePath(String path) {
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("save_directory", path);
        ed.apply();
    }

    private static String userName = null;

    static String getUsername() {
        if (userName != null) {
            return userName;
        } else {
            return "anon";
        }
    }

    private static void setUsername(Elements loginHtmlBlock) {
        userName = loginHtmlBlock.select("a[id=settings]").text();
        System.out.println("setUsernameState" + userName);
    }

    private static String uAU = null;

    private static void setUserAvatarUrl() {
        loadingTask lt = new loadingTask();
        lt.execute();
    }

    static String getUserAvatarUrl() {
        if (uAU != null) {
            return uAU;
        } else {
            return "http://img0.pornreactor.cc/images/default_avatar.jpeg";
        }
    }

    private static class loadingTask extends AsyncTask<Void, Void, Void> {
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

        static Map<String, String> getCookies() {
            return Cookies;
        }

        static void setCookies(Map<String, String> cookies) {
            Cookies = cookies;
        }

        static int getPlainCookies(){
            try {
                gPc gpc = new gPc();
                gpc.execute();
                return 1;
            } catch (Exception ex) {
                return 0;
            }
        }

        static int status_code;

        static class gPc extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Connection conn = HttpConnection.connect(getJoyUrl())
                            .ignoreHttpErrors(true)
                            .userAgent("Maxthon")
                            .timeout(10000);
                    Connection.Response response1 = conn.execute();
                    setCookies(response1.cookies());
                    status_code = 0;
                } catch (Exception ex) {
                    System.out.printf(ex.getLocalizedMessage());
                    status_code = ex.hashCode();
                }
                return null;
            }
        }
    }

    static String csrf_token = "n/a";
    static Boolean logined = false;

    static void makeAuth(String login, String pass) {
        try {
            String loginURL = Globals.getJoyUrl() + "/login";
            String itemURL = Globals.getJoyUrl();
            String useragent = "Maxthon";

            //получаем страницу входа
            Connection connection1 = HttpConnection.connect(itemURL)
                    .ignoreHttpErrors(true)
                    .userAgent(useragent)
                    .timeout(10000);
            Connection.Response response1 = connection1
                    .execute();
            //цапаем оттуда токен
            csrf_token = response1.parse().getElementById("signin__csrf_token").attr("value");
            System.out.println("token:::" + csrf_token);

            for (Map.Entry<String, String> cookie : response1.cookies().entrySet()) {
                System.out.println("cookie1");
                System.out.println(cookie.getKey() + " : " + cookie.getValue());
            }
            for (Map.Entry<String, String> head : response1.headers().entrySet()) {
                System.out.println("headers1");
                System.out.println(head.getKey() + " : " + head.getValue());
            }
            //делаем пост запрос
            Connection connection2 = connection1.url(loginURL)
                    .cookies(response1.cookies())
                    .ignoreHttpErrors(true)
                    .data("signin[username]", login)
                    .data("signin[password]", pass)
                    .data("signin[_csrf_token]", csrf_token)
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .timeout(10000);

            Connection.Response response2 = connection2.execute();
            for (Map.Entry<String, String> cookie : response2.cookies().entrySet()) {
                System.out.println("cookie2");
                System.out.println(cookie.getKey() + " : " + cookie.getValue());
            }
            for (Map.Entry<String, String> head : response2.headers().entrySet()) {
                System.out.println("headers2");
                System.out.println(head.getKey() + " : " + head.getValue());
            }

            Document tt2 = response2.parse();
            String t2 = tt2.select(".login").html();
            System.out.println("login tag2:::" + t2);
            String error2 = tt2.select(".error_list").text();
            System.out.println("error tag2:::" + error2);
            // получаем страницу (уже должны быть авторизованы)
            if (t2.contains("Выход")) logined = true;
            Globals.Cookies.setCookies(response2.cookies());
            Globals.setUsername(tt2.select(".login"));
            Globals.setUserAvatarUrl();
            System.out.println(Globals.Cookies.getCookies());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Proxie {
        static String IP, PORT;

        public static String getIP() {
            return IP;
        }

        public static void setIP(String _IP) {
            IP = _IP;
        }

        public static String getPORT() {
            return PORT;
        }

        public static void setPORT(String _PORT) {
            PORT = _PORT;
        }
    }
}
