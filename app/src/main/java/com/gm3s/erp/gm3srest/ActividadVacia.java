package com.gm3s.erp.gm3srest;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ActividadVacia extends AppCompatActivity {

    private static PersistentCookieStore pc;
    static final String COOKIES_HEADER = "Set-Cookie";
    static CookieManager msCookieManager = new CookieManager();
    HashMap<String, String> postDataParams = new HashMap<>();
    Button conectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_vacia);


        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        //msCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        conectar = (Button) findViewById(R.id.conectar);
        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkAsyncTask nat = new NetworkAsyncTask();
                nat.execute();
            }
        });

       /* pc = new PersistentCookieStore(this);

        StringBuffer chaine = new StringBuffer("");
        try{
            URL url = new URL("http://192.168.100.5:8080/medialuna/j_spring_security_check");


            List<NameValuePair> values = new ArrayList<NameValuePair>(3);
            values.add(new BasicNameValuePair("j_empresa", "FFMM"));
            values.add(new BasicNameValuePair("j_username", "ENGARCIA"));
            values.add(new BasicNameValuePair("j_password", "12345678"));
            httppost.setEntity(new UrlEncodedFormEntity(values));

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            //connection.setRequestProperty("Cookie",  TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
            connection.connect();


            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }*/


    }

    protected String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    private class NetworkAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                postDataParams.put("j_empresa", "TIENDAS");
                postDataParams.put("j_username", "EXPOEMP");
                postDataParams.put("j_password", "98749874");

                URL url;
                String response = "";
                try {
                    url = new URL("https://tiendas.gm3s-erp.com:8989/medialuna/j_spring_security_check");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(false);
                    //conn.getHeaderFields();


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();


                    loadResponseCookies(conn, msCookieManager);
                   /* Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                    if (cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            System.out.println("cookie " + cookie);
                            msCookieManager.getCookieStore().add(null, HttpCookie.p
                            arse(cookie).get(0));
                        }
                    }*/


                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    } else {
                        response = "";

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("response " + response);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void loadResponseCookies(@Nullable HttpURLConnection conn, @Nullable CookieManager cookieManager) {

        //do nothing in case a null cokkie manager object is passed
       /* if (cookieManager == null || conn == null) {
            System.out.println("cookieManager " + cookieManager);
            System.out.println("conn " + conn);
            return;
        }*/

        List<String> cookiesHeader = conn.getHeaderFields().get(COOKIES_HEADER);
        System.out.println("cookiesHeader " + conn.getHeaderFields());
        System.out.println("cookiesHeader 1" + conn.getHeaderFields().get(COOKIES_HEADER));
        System.out.println("cookiesHeader 2" + conn.getHeaderFields().keySet());
        if (cookiesHeader != null) {
            for (String cookieHeader : cookiesHeader) {
                List<HttpCookie> cookies;
                try {
                    cookies = HttpCookie.parse(cookieHeader);
                } catch (NullPointerException e) {
                    // log.warn(MessageFormat.format("{0} -- Null header for the cookie : {1}", conn.getURL().toString(), cookieHeader.toString()));
                    //ignore the Null cookie header and proceed to the next cookie header
                    continue;
                }

                if (cookies != null) {
                    System.out.println("cookies " + cookies);
                    // Debug("{0} -- Reading Cookies from the response :", conn.getURL().toString());
                   /* for (HttpCookie cookie : cookies) {
                        Debug(cookie.toString());
                    }*/
                    if (cookies.size() > 0) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookieHeader).get(0));
                    }
                }
            }
        }
    }
}
