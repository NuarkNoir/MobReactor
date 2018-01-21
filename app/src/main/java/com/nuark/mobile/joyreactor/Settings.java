package com.nuark.mobile.joyreactor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText et, prp, prip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        et = (EditText) findViewById(R.id.savedirET);
        prip = (EditText) findViewById(R.id.proxy_ip);
        prp = (EditText) findViewById(R.id.proxy_port);
        if (Globals.getSavePath() == ""){
            et.setText("MRF");
        } else {
            et.setText(Globals.getSavePath());
        }
        if (Globals.Proxie.getIP() == "" || Globals.Proxie.getPORT() == ""){
            prip.setText("");
            prp.setText("");
        } else {
            prip.setText(Globals.Proxie.getIP());
            prp.setText(Globals.Proxie.getPORT());
        }
    }

    public void saveSet_savedir(View view) {
        Globals.setSavePath(et.getText().toString());
        Toast.makeText(this,
                "Папка сохранения будет изменена на:'" + et.getText().toString() + "'\nПерезапустите приложение.",
                Toast.LENGTH_SHORT)
                .show();
    }

    public void saveSet_loadfreeproxy(View view) {
        ReadProxy.loadProxyJSON();
        prip.setText(Globals.Proxie.getIP());
        prp.setText(Globals.Proxie.getPORT());
    }

    public void saveSet_saveproxy(View view) {
        Globals.Proxie.setIP(prip.getText().toString());
        Globals.Proxie.setPORT(prp.getText().toString());
        Toast.makeText(this, "Прокси задано!", Toast.LENGTH_SHORT).show();
    }
}
