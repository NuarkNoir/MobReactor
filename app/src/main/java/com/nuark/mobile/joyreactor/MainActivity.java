package com.nuark.mobile.joyreactor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static String base_url = "http://pornreactor.cc/tag/";
    static ArrayList<String> links = new ArrayList<>();
    static ArrayList<String> urls_list = new ArrayList<>();
    static ArrayList<String> tags_list = new ArrayList<>();
    static ArrayList<String> authors_list = new ArrayList<>();
    static ArrayList<String> date_date_list = new ArrayList<>();
    static ArrayList<String> date_time_list = new ArrayList<>();
    static CustomListAdapter arrad;
    static Document d = null;
    static Context cont;
    static Elements postContainers, j, l, m, k;
    Activity act = this;
    boolean just_start;
    static int nextPageNum, currPg;
    static String currPage;
    static SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = this;
        sPref = getPreferences(MODE_APPEND);
        String savedText = sPref.getString("save_directory", "" );
        mkd(savedText);
        if (savedText == "") {
            mkd("/MRF");
        } else {
            mkd(savedText);
        }
        urls_list.add(base_url + "r34");
        links = new ArrayList<>();
        tags_list = new ArrayList<>();
        authors_list = new ArrayList<>();
		
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
         //Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case (R.id.action_load):
                nextPageNum = currPg;
                start_loading();
                Toast.makeText(cont, "<>", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.action_load_tfa):
                openTFA();
                break;
            case (R.id.action_settings):
                openSettings();
                break;
            case (R.id.action_load_nextpage):
                nextPageNum = currPg - 1;
                start_loading();
                Toast.makeText(cont, ">>", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.action_load_previouspage):
                nextPageNum = currPg + 1;
                start_loading();
                Toast.makeText(cont, "<<", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.action_login):
                openLoginActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadNextPage() {
        if (nextPageNum != 0){
            loadingTask lT = new loadingTask();
            lT.execute();
        }
    }

    public void start_loading() {
        loadingTask lT = new loadingTask();
        lT.execute();
    }

    public void openSettings() {
        Intent intent = new Intent(cont, com.nuark.mobile.joyreactor.SettingsActivity.class);
        cont.startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(cont, com.nuark.mobile.joyreactor.LoginActivity.class);
        cont.startActivity(intent);
    }

    public void openTFA() {
        Toast.makeText(cont,"Launching test activity...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FullscreenPictureActivity.class);
        intent.putExtra("URL", "http://gelbooru.com/samples/86/0d/sample_860dc031628b1605a3d5092a442b2a92.jpg");
        startActivity(intent);
    }

    void mkd(String name) {
        String path = Environment.getExternalStorageDirectory().getPath() + name;
        File dir_to_save = new File(path);
        try {
            dir_to_save.mkdirs();
        } catch (Exception ex) {
            Toast.makeText(cont, "Ошибка! \n" + ex.getCause() + "\n" + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    class loadingTask extends AsyncTask<Void, Void, Void> {
        //http://try.jsoup.org/~a5UybIswFzRGoKv9HQQQe784nlk
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
            arrad = new CustomListAdapter(act, links, authors_list, tags_list, date_date_list, date_time_list);
            arrad.notifyDataSetChanged();
            lv.setAdapter(arrad);
            just_start = false;
            super.onPostExecute(result);
        }
    }

    public static void gettr(int nxtPg) {
        try {
            if (nxtPg > 0) {
                currPage = urls_list.get(0) + "/" + nextPageNum;
            }
            else {
                currPage = urls_list.get(0);
            }
            d = Jsoup.connect(currPage).get();
            System.out.println("URL: " + currPage);
        } catch (Exception ex) {
            System.out.println("Ашипка D: \n" + ex.getMessage() + "\nOn: " + currPage);
        }

        if (d != null) {
            postContainers = d.select(".postContainer");
            k = d.select(".pagination .pagination_main .pagination_expanded .current");
            for (Element element : k) {
                currPg = Integer.parseInt(element.text());
            }

            for (Element el : postContainers) {
                l = el.select(".post_top .uhead .uhead_nick img");  //Parsing author of post
                m = el.select(".post_top .taglist");                //Parsing image tags
                j = el.select(".image a");

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
