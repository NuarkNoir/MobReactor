package com.nuark.mobile.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText edText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        edText = (EditText) findViewById(R.id.diredit);
        loadText();
    }

    void saveText() {
        Globals.setSavePath(edText.getText().toString());
        Toast.makeText(this, "Путь сохранён", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        edText.setText(Globals.getSavePath());
    }

    public void load(View view) {
        loadText();
    }

    public void save(View view) {
        saveText();
    }
}
