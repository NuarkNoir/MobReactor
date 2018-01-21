package com.nuark.mobile.joyreactor;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class ReadProxy {

    static void loadProxyJSON() {

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Document d = Jsoup.connect("http://api.foxtools.ru/v2/Proxy.html").get();
                    Element el = d.select("tbody tr").first();
                    Globals.Proxie.setIP(el.select("td:nth-child(1)").first().text());
                    Globals.Proxie.setPORT(el.select("td:nth-child(2)").first().text());
                    System.out.println("IP: " + el.select("td:nth-child(1)").first().text());
                    System.out.println("PORT: " + el.select("td:nth-child(2)").first().text());
                    System.setProperty("http.proxyHost", Globals.Proxie.getIP());
                    System.setProperty("http.proxyPort", Globals.Proxie.getPORT());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
