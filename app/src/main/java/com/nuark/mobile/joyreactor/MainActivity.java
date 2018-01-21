package com.nuark.mobile.joyreactor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ArrayList<String> links, tags_list, authors_list;
    private static final ArrayList<String> date_date_list = new ArrayList<>();
    private static final ArrayList<String> date_time_list = new ArrayList<>();
    private static Document d = null;
    private static Context cont;
    private final Activity act = this;
    private boolean just_start;
    private static int nextPageNum;
    private static int currPg;
    private static String currPage;
    static SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = this;
        sPref = getPreferences(MODE_APPEND);
        if (Globals.getSavePath() == ""){
            mkd("/MRF");
        } else {
            mkd("/" + Globals.getSavePath());
        }
        links = new ArrayList<>();
        tags_list = new ArrayList<>();
        authors_list = new ArrayList<>();
        {
            int gc = Globals.Cookies.getPlainCookies();
            if (gc != 0) {
                Toast.makeText(cont, R.string.error_getPlainCookies + gc, Toast.LENGTH_SHORT).show();
            }
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.ndo, R.string.ndc);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Button forwarder = (Button) findViewById(R.id.forwarder);
        forwarder.setVisibility(GONE);
        forwarder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPageNum = currPg - 1;
                start_loading();
                Toast.makeText(cont, "Грузим дальше...", Toast.LENGTH_SHORT).show();
            }
        });
        final Button backer = (Button) findViewById(R.id.backer);
        backer.setVisibility(GONE);
        backer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPageNum = currPg + 1;
                start_loading();
                Toast.makeText(cont, "Подгружаем прошлое...", Toast.LENGTH_SHORT).show();
            }
        });
        Button syncer = (Button) findViewById(R.id.syncer);
        syncer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPageNum = currPg;
                start_loading();
                Toast.makeText(cont, "Грузим...", Toast.LENGTH_SHORT).show();
                backer.setVisibility(View.VISIBLE);
                forwarder.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
         //Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case (R.id.action_load_tfa):
                openTFA();
                break;
            case (R.id.action_login):
                openLoginActivity();
                break;
            case (R.id.action_settings):
                openSettingsActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void start_loading() {
        loadingTask lT = new loadingTask();
        lT.execute();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(cont, com.nuark.mobile.joyreactor.LoginActivity.class);
        cont.startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(cont, com.nuark.mobile.joyreactor.Settings.class);
        cont.startActivity(intent);
    }

    private void openTFA() {
        Toast.makeText(cont,"Launching test activity...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FullscreenPictureActivity.class);
        intent.putExtra("URL", "http://gelbooru.com/samples/86/0d/sample_860dc031628b1605a3d5092a442b2a92.jpg");
        startActivity(intent);
    }

    private void mkd(String name) {
        String path = Environment.getExternalStorageDirectory().getPath() + name;
        File dir_to_save = new File(path);
        try {
            dir_to_save.mkdirs();
        } catch (Exception ex) {
            Toast.makeText(cont, "Ошибка! \n" + ex.getCause() + "\n" + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshUser(){
        try {
            ImageView userava = (ImageView) findViewById(R.id.userAvatar);
            TextView tv = (TextView) findViewById(R.id.usersName);
            Glide.with(cont).load(Globals.getUserAvatarUrl()).override(60, 60).placeholder(R.mipmap.ic_launcher).into(userava);
            tv.setText(Globals.getUsername());
        } catch (Exception ex) {
            Toast.makeText(cont, "ЕГГОГ::: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshUserState(View view) {
        refreshUser();
    }

    private class loadingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            gettr(nextPageNum);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!just_start) {
                links.clear();
                authors_list.clear();
                tags_list.clear();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView lv = (ListView) findViewById(R.id.listview);
            Collections.reverse(links);
            Collections.reverse(authors_list);
            Collections.reverse(tags_list);
            CustomListAdapter arrad = new CustomListAdapter(
                    act,
                    links,
                    authors_list,
                    tags_list,
                    date_date_list,
                    date_time_list
            );
            arrad.notifyDataSetChanged();
            lv.setAdapter(arrad);
            just_start = false;
            super.onPostExecute(result);
        }
    }

    private static void gettr(int nxtPg) {
        try {
            if (nxtPg > 0) {
                currPage = Globals.getJoyUrl() + "tag/r34/" + nextPageNum;
            }
            else {
                currPage = Globals.getJoyUrl() + "tag/r34";
            }
            if (Globals.Cookies.Cookies != null) {
                d = Jsoup.connect(currPage).get();
            } else {
                d = Jsoup.connect(currPage).cookies(Globals.Cookies.getCookies()).timeout(10000).get();
            }
            System.out.println("URL: " + currPage);
        } catch (Exception ex) {
            System.out.println("Ашипка D: \n" + ex.getMessage() + "\nOn: " + currPage);
        }

        if (d != null) {
            Elements postContainers = d.select(".postContainer");
            Elements k = d.select(".pagination .pagination_main .pagination_expanded .current");
            for (Element element : k) {
                currPg = Integer.parseInt(element.text());
            }

            for (Element el : postContainers) {
                Elements l = el.select(".post_top .uhead .uhead_nick img");
                Elements m = el.select(".post_top .taglist");
                Elements j = el.select(".image a");

                for (Element _item : j) {
                    if (_item.hasAttr("href"))
                        links.add(_item.attr("href"));
                    else
                        links.add(postContainers.select(".post_top .post_content .image img").attr("src"));

                    authors_list.add(l.attr("alt"));
                    tags_list.add(m.text());
                    date_date_list.add(el.select(".ufoot  .date .date").text());
                    date_time_list.add(el.select(".ufoot  .date .time").text());
                }
            }
        }
    }
}
