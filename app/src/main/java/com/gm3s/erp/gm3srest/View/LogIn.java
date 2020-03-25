package com.gm3s.erp.gm3srest.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.BuildConfig;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Model.Usuario;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class LogIn extends AppCompatActivity {
    //gm3software all
    private SharedPreference sharedPreference;
    private static List<Cookie> cookies;
    private static boolean validacion;
    private EditText company;
    private EditText nombre;
    private EditText password;
    private Button ingresar;
    private Button escaner;
    static String server = "";
    int count = 3;
    Usuario person = new Usuario();
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor prefsEditor;
    private static PersistentCookieStore pc;
    public static ImageView loader;
    private ImageView btn_server;
    private TextView registro_txt;

    //VARIABLES PARA LA VERSION DE LA APP

    public TextView versionApp;
    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPreference = new SharedPreference();


        pc = new PersistentCookieStore(getApplicationContext());


        CookieSyncManager.createInstance(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefsEditor = mPrefs.edit();
        company = (EditText) findViewById(R.id.server1);
        nombre = (EditText) findViewById(R.id.server2);
        password = (EditText) findViewById(R.id.server3);
        final EditText[] a = {company, nombre, password};
        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);


        versionName = BuildConfig.VERSION_NAME;
        versionApp = (TextView) findViewById(R.id.textView22);

        versionApp.setText(versionName);

        ingresar = (Button) findViewById(R.id.input_a1_4);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Helper.isConnected3(getApplicationContext())) {
                    if (sharedPreference.getValue(getApplicationContext()) == null) {

                        Toast.makeText(getApplicationContext(), "No has elegido un servidor, por favor elige uno de las opciones de arriba", Toast.LENGTH_LONG).show();
                    } else {
                        if (Helper.validate(a)) {

                            ingresar.setEnabled(false);

                            person.setUser(nombre.getText().toString());
                            person.setCompania(company.getText().toString());
                            person.setPassword(password.getText().toString());
                            server = sharedPreference.getValue(getApplicationContext());
                            loader.setVisibility(View.INVISIBLE);
                            loader.setVisibility(View.VISIBLE);
                            new HttpAsyncTask().execute(server + "/medialuna/j_spring_security_check");

                        } else {
                            Toast.makeText(getApplicationContext(), "Favor de completar los campos de acceso", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                }
            }
        });


        btn_server = (ImageView) findViewById(R.id.btn_server);
        btn_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(LogIn.this, Configuracion.class));
            }
        });



    }


    public static String POST(String url, Usuario person) {
        String result = "";
        try {
            InputStream inputStream = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(server + "/medialuna/j_spring_security_check");
            httppost.addHeader("Accept-Language", "es");


            //  HttpClient client = HttpClientBuilder.create().build();
            //  HttpGet request = new HttpGet("http://mkyong.com");
            //  HttpResponse response = client.execute(request);

            try {
                List<NameValuePair> values = new ArrayList<NameValuePair>(3);
                values.add(new BasicNameValuePair("j_empresa", person.getCompania()));
                values.add(new BasicNameValuePair("j_username", person.getUser()));
                if (person.getUser().equals("administrador")) {
                    person.setPassword("passwor aqui");
                }
                values.add(new BasicNameValuePair("j_password", person.getPassword()));
                httppost.setEntity(new UrlEncodedFormEntity(values,"utf-8"));
                HttpResponse httpResponse = httpclient.execute(httppost);
                Log.v("response code", httpResponse.getStatusLine()
                        .getStatusCode() + "");
                cookies = httpclient.getCookieStore().getCookies();
                System.out.println("cookiesHeader LI " + cookies);
              //  System.out.println("cookiesHeader HF " + httpclient.get);
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
                        CookieManager.getInstance().setCookie(cookie.getDomain(), cookieString);
                    }
                }
                pc.addCookie(cookies.get(0));
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null){
                    result = Helper.convertInputStreamToString(inputStream);
                    System.out.println("response " + result);
                }
                else
                    result = "Error en la peticon";
                if (result.contains("Su sesión ha expirado")) {
                    validacion = false;
                } else {

                    validacion = true;
                }
            } catch (IOException e) {
                System.out.println("\"Fallo en parametros de conexion con servidor (1)");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Fallo en la conexion con servidor (1)");
            e.printStackTrace();
        }
        return result;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], person);

        }


        @Override
        protected void onPostExecute(String result) {
            if (validacion) {
                String encodedurl = "";
                try {
                    if (person.getUser().equals("administrador")) {
                        encodedurl = server + "/medialuna/spring/app/access/" + URLEncoder.encode(person.getPassword(), "utf-8") + "/" + person.getUser();
                    } else {
                        encodedurl = server + "/medialuna/spring/app/access/" + URLEncoder.encode(person.getPassword(), "utf-8") + "/" + person.getCompania() + "_" + URLEncoder.encode(person.getUser(), "utf-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                new HttpAsyncTask2().execute(encodedurl);
            } else {
                Toast.makeText(getApplicationContext(), "Campos de acceso incorrectos", Toast.LENGTH_LONG).show();
                Intent localIntent = new Intent(LogIn.this.getApplicationContext(), LogIn.class);
                startActivity(localIntent);
            }
        }
    }


    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST2(urls[0], person);

        }


        @Override
        protected void onPostExecute(String result) {
            loader.setVisibility(View.INVISIBLE);
            ingresar.setEnabled(true);
            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_SHORT).show();
                pc.clear();
            } else {
                if (result.equals("1")) {
                    Intent localIntent = new Intent(LogIn.this.getApplicationContext(), Confirmation.class);
                    localIntent.putExtra("usercompany", person.getCompania());
                    localIntent.putExtra("username", person.getUser());
                    startActivity(localIntent);

                } else {
                    Intent localIntent = new Intent(LogIn.this.getApplicationContext(), MainActivity.class);
                    localIntent.putExtra("usercompany", person.getCompania());
                    localIntent.putExtra("username", person.getUser());
                    startActivity(localIntent);
                }
            }
        }
    }


    public static String POST2(String url, Usuario person) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(cookies.get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("'Content-Type", "text/html; CHARSET=UTF-8");
            String json = "";
            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = Helper.convertInputStreamToString(inputStream);
                else
                    result = "Error en la peticion";
                if (result.contains("No se logró realizar la autenticación")) {
                    System.out.println("\nUsuario no autenticado");
                } else {
                    System.out.println("\nFuncion ejecutada");
                }
            } catch (IOException e) {
                System.out.println("\"Fallo en parametros de conexion con servidor (2)");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Fallo en la conexion con servidor (2)");
            e.printStackTrace();
        }


        return result;
    }


    @Override
    public void onRestart() {
        super.onRestart();
        pc.clear();
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}