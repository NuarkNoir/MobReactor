package com.nuark.mobile.joyreactor;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.nuark.mobile.joyreactor.R.layout.activity_login;

public class LoginActivity extends AppCompatActivity {

    private static String login, pass, csrf_token;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText loginLV = (EditText) findViewById(R.id.login_input);
                EditText passwordLV = (EditText) findViewById(R.id.password_input);
                login = loginLV.getText().toString();
                pass = passwordLV.getText().toString();
                view = v;
                try {
                    loginTask lT = new loginTask();
                    lT.execute();
                    Snackbar.make(v, "Логинимся...", LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Snackbar.make(v, "Exception!\n" + ex.getMessage() + "\n\nCaused by:\n" + ex.getCause(), LENGTH_LONG).show();
                }
            }
        });
    }

    private class loginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Globals.makeAuth(login, pass);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView tv = (TextView) findViewById(R.id.csrf_token);
            tv.setText(Globals.csrf_token + ":::" + Globals.logined + "::: as " + Globals.getUsername());
            if (!Globals.logined) {
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                Snackbar.make(view, "Ошибка авторизации!", LENGTH_SHORT).show();
            }
            else {
                tv.setTextColor(getResources().getColor(R.color.colorAccent));
                Snackbar.make(view, "Авторизован как " + login + "!", LENGTH_SHORT).show();
            }
        }
    }
}
