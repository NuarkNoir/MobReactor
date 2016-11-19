package com.nuark.mobile.joyreactor;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    static SharedPreferences sPref;
    static EditText edText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        edText = (EditText) findViewById(R.id.diredit);
        sPref = MainActivity.sPref;
    }

    void saveText() {
        sPref = getPreferences(MODE_APPEND);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("save_directory", edText.getText().toString());
        ed.apply();
        Toast.makeText(this, "Путь сохранён", Toast.LENGTH_SHORT).show();
    }
    void loadText() {
        sPref = getPreferences(MODE_APPEND);
        String savedText = sPref.getString("save_directory", "" );
        edText.setText(savedText);
    }

    public void load(View view) {
        loadText();
    }

    public void save(View view) {
        saveText();
    }
}
