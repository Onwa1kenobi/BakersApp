package com.udacity.julius.bakersapp;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ameh on 28/07/2017.
 */

 class NetworkUtils {

    private static final String
            STRING_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    static URL buildUrl() {
        Uri uri = Uri.parse(STRING_URL).buildUpon().build();

        URL mUrl = null;

        try {
            mUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return mUrl;
    }

    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
