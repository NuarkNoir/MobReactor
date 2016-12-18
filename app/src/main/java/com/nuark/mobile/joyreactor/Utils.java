package com.nuark.mobile.joyreactor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {

    static boolean checkWithRegExp(String userNameString) {
        Pattern p = Pattern.compile("([^/-]([\\d]+\\.([pngjeifbmpa]+)))$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userNameString);
        return m.matches();
    }

    static String url_decoder(String url) {
        String decoded_url = null;
        try {
            decoded_url = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded_url;
    }
}
