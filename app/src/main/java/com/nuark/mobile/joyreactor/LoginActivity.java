package com.nuark.mobile.joyreactor;

import android.app.Application;
import android.app.Notification;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.nuark.mobile.joyreactor.R.layout.activity_login;

public class LoginActivity extends AppCompatActivity {

    private static String login, pass, csrf_token;
    static Boolean logined = false;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTask lT = new loginTask();
                lT.execute();
                view = v;
                Snackbar.make(v, "Логинимся...", LENGTH_SHORT).show();
            }
        });
    }

    class loginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            makeAuth();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView tv = (TextView) findViewById(R.id.csrf_token);
            tv.setText(csrf_token + ":::" + logined + ":::" + Globals.getUsername());
            if (!logined) {
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                Snackbar.make(view, "Ошибка авторизации!", LENGTH_SHORT).show();
            }
            else {
                tv.setTextColor(getResources().getColor(R.color.colorAccent));
                Snackbar.make(view, "Авторизован как " + login + "!", LENGTH_SHORT).show();
            }
        }
    }

    void makeAuth() {
        try {
            String loginURL = Globals.getJoyUrl() + "/login";
            String itemURL = Globals.getJoyUrl();
            String useragent = "Maxthon";

            EditText loginLV = (EditText) findViewById(R.id.login_input);
            EditText passwordLV = (EditText) findViewById(R.id.password_input);

            login = loginLV.getText().toString();
            pass = passwordLV.getText().toString();

            //получаем страницу входа
            Connection connection1 = HttpConnection.connect(itemURL)
                    .ignoreHttpErrors(true)
                    .userAgent(useragent);
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
                    .followRedirects(true);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
