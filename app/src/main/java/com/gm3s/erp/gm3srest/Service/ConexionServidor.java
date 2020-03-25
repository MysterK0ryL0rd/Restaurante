package com.gm3s.erp.gm3srest.Service;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by usuario on 28/01/16.
 */
public class ConexionServidor extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {

        return POST(urls[0]);

    }

    @Override
    protected void onPostExecute(String result) {
    }


    public static String POST(String url) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {

                } else {

                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }

}
