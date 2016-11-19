package com.nuark.mobile.joyreactor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {

    private static String login, password, login_container, password_container, csrf_token;
    static Document d;
    static Elements loginContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        loginTask lT = new loginTask();
        lT.execute();
    }

    class loginTask extends AsyncTask<Void, Void, Void> {
        //http://try.jsoup.org/~a5UybIswFzRGoKv9HQQQe784nlk
        @Override
        protected Void doInBackground(Void... params) {
            login();
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
            tv.setText(csrf_token);
        }
    }

    public static void login() {
        try {
            d = Jsoup.connect("http://pornreactor.cc/login").get();
            System.out.println("\n\n\n" + d.html() + "\n\n\n");
        } catch (Exception ex) {
            System.out.println("Ашипка D: \n" + ex.getMessage());
        }

        if (d != null) {
            loginContainer = d.select(".qtipped .sfSignin");
            for (Element _item : loginContainer) {
                csrf_token = _item.select("#signin__csrf_token").attr("value");
                //login_container = _item.select("input[type=text]").;
            }
            //System.out.println("\n\n\n" + loginContainer.html() + "\n\n\n");
        }
		
		ListView loginLV = (ListView) findViewById(R.id.);
		ListView passwordLV = (ListView) findViewById(R.id.);
		
		login = loginLV.text();
		password = passwordLV.text();
		
		Connection conn = Jsoup.connect("http://pornreactor.cc/login");
		conn.data("username", login);
		conn.data("password", password);
		conn.data("_csrf_token", csrf_token);
		conn.method(Connection.Method.POST);
		Response resp = conn.execute();
		System.out.println("statusCode: " + resp.statusCode());
		Document doc = conn.url("http://pornreactor.cc/").post();
		System.out.println(doc.select("ul.login_wr").html());
    }

}
