package com.gm3s.erp.gm3srest.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Model.Usuario;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class Registro extends AppCompatActivity {

    private static boolean validacion;
    private Button continuar;
    private static List<Cookie> cookies;
    EditText cmp;
    private SharedPreference sharedPreference;
    EditText usr;
    EditText psw;
    EditText ema;
    Usuario person = new Usuario();
    static String server = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(getApplicationContext());
        CookieSyncManager.createInstance(this);

        cmp = (EditText) findViewById(R.id.input_a2_1);
        usr = (EditText) findViewById(R.id.input_a2_2);
        psw = (EditText) findViewById(R.id.input_a2_3);
        ema = (EditText) findViewById(R.id.input_a2_4);
        final EditText[] a = {cmp, usr, psw, ema};


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Registro");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Registro.this, LogIn.class);
                startActivity(intent);
            }
        });
        Toast.makeText(getApplicationContext(), "Servidor:" + server, Toast.LENGTH_LONG).show();

        continuar = (Button) findViewById(R.id.input_a2_6);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Helper.isConnected3(getApplicationContext())) {
                    if (sharedPreference.getValue(getApplicationContext()) == null) { // Si tiene algo
                        Toast.makeText(getApplicationContext(), "No has elegido un servidor, por favor elige uno de las opciones del menu", Toast.LENGTH_LONG).show();
                        Intent localIntent = new Intent(Registro.this.getApplicationContext(), LogIn.class);
                        startActivity(localIntent);
                    } else {
                        if (Helper.validate(a)) {
                            person.setUser(usr.getText().toString());
                            person.setCompania(cmp.getText().toString());
                            person.setPassword(psw.getText().toString());
                            person.setEmail(ema.getText().toString());
                            person.setCode(generateCode());
                            Toast.makeText(getApplicationContext(), "Server: " + server, Toast.LENGTH_LONG).show();
                            new HttpAsyncTask().execute(server + "/medialuna/j_spring_security_check");
                        } else {
                            Toast.makeText(getApplicationContext(), "Favor de completar los campos de acceso", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent(Registro.this.getApplicationContext(), LogIn.class);
                    startActivity(localIntent);
                }
            }
         });

    }


     public String generateCode() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(30, random).toString(32);
    }


    public static String POST(String url, Usuario person) {
        String result = "";
        try {
            InputStream inputStream = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(server + "/medialuna/j_spring_security_check");
            try {
                List<NameValuePair> values = new ArrayList<NameValuePair>(3);
                values.add(new BasicNameValuePair("j_empresa", person.getCompania()));
                values.add(new BasicNameValuePair("j_username", person.getUser()));
                values.add(new BasicNameValuePair("j_password", person.getPassword()));
                httppost.setEntity(new UrlEncodedFormEntity(values,"utf-8"));
                HttpResponse httpResponse = httpclient.execute(httppost);
                cookies = httpclient.getCookieStore().getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
                        CookieManager.getInstance().setCookie(cookie.getDomain(), cookieString);
                    }
                }
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = Helper.convertInputStreamToString(inputStream);
                else
                    result = "Error en la peticon";
                if (result.contains("Su sesión ha expirado")) {
                    System.out.println("\nUsuario no autenticado");
                    validacion = false;
                } else {
                    System.out.println("\nUsuario encontrado en la BD");
                    validacion = true;
                }
            } catch (IOException e) {
                System.out.println("\"Fallo en parametros de conexion con servidor (1)");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Fallo en la conexion con servidor (2)");
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
                new HttpAsyncTask2().execute(server + "/medialuna/spring/app/registro/" + person.getUser() + "/" + person.getCompania() + "/" + person.getPassword() + "/" + person.getEmail() + "/" + person.getCode());
            } else {
                Toast.makeText(getApplicationContext(), "Campos de acceso incorrectos", Toast.LENGTH_LONG).show();
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
            if (validacion) {
                //cookies.removeAll()
                Toast.makeText(getApplicationContext(), "Revisa tu bandeja de entrada para comprobar tu codigo de seguridad", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Ingresalo al volver a entrar a la aplicacion", Toast.LENGTH_LONG).show();
                Intent localIntent = new Intent(Registro.this.getApplicationContext(), LogIn.class);
                startActivity(localIntent);
            } else {
                Toast.makeText(getApplicationContext(), "Usuario no valido", Toast.LENGTH_LONG).show();
                Intent localIntent = new Intent(Registro.this.getApplicationContext(), LogIn.class);
                startActivity(localIntent);

            }
        }
    }


    public static String POST2(String url, Usuario person) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(cookies.get(0));
            HttpPost httppost = new HttpPost(url);
            String json = "";
            try
            {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = Helper.convertInputStreamToString(inputStream);
                else
                    result = "Error en la peticion";
                if (result.contains("Su sesión ha expirado")) {
                    System.out.println("\nUsuario no autenticado");
                    validacion = false;
                } else {
                    validacion = true;
                    System.out.println("\nUsuario autenticado (BD Server)");
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





}



