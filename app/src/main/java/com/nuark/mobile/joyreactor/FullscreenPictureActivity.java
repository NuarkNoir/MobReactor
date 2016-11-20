package com.nuark.mobile.joyreactor;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

public class FullscreenPictureActivity extends Activity {
    static String url;
    private final ThinDownloadManager downloadManager = new ThinDownloadManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_picture);

        Bundle extras = getIntent().getExtras();

        url = extras.getString("URL");
        TextView textView = (TextView) findViewById(R.id.bP_Url);
        String ttd = getString(R.string.url_string) + url;
        textView.setText(ttd);
        ImageView imageView = (ImageView) findViewById(R.id.basePict);
        Glide.with(this).load(url).into(imageView);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        Button btn = (Button) findViewById(R.id.downloadButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri downloadUri = Uri.parse(url);
                Uri destinationUri = destinationUri = Uri.parse(Environment.getExternalStorageDirectory()
                        .getPath() + "/MRF/" + downloadUri.getLastPathSegment());
                DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setDestinationURI(destinationUri)
                        .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Toast.makeText(FullscreenPictureActivity.this, "Успешно сохранено!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        String errMessage = "Ошибка загрузки изображения.\nОшибка: " + errorCode + " :: " + errorMessage;
                        Toast.makeText(FullscreenPictureActivity.this, errMessage, Toast.LENGTH_LONG).show();
                        downloadManager.release();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        progressBar.setProgress(progress);
                    }
                });
                int downloadId = downloadManager.add(downloadRequest);
                Toast.makeText(FullscreenPictureActivity.this, "Изображение будет сохранено в ФС.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
