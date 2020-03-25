package com.gm3s.erp.gm3srest.View;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Model.SharedPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class Confirmation extends Activity {
    private Button verificar;
    private SharedPreference sharedPreference;
    private ImageView loader;
    String[] cookieArray;
    private static boolean validacion;
    private EditText code;
    List<Cookie> cookies;
    private static SharedPreferences mPrefs;
    private static String json;
    private static Cookie c;
    private static PersistentCookieStore pc;
    String server = "";
    String user = "";
    String company = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(getApplicationContext());

        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        company = intent.getStringExtra("usercompany");
        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);

        code = (EditText) findViewById(R.id.input_a3_2);
        final EditText[] a = {code};

        verificar = (Button) findViewById(R.id.input_a3_3);
        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                if (Helper.isConnected3(getApplicationContext())) {
                    if (sharedPreference.getValue(getApplicationContext()) == null) { // Si tiene algo
                        Toast.makeText(getApplicationContext(), "No has elegido un servidor, por favor elige uno de las opciones del menu", Toast.LENGTH_LONG).show();
                        Intent localIntent = new Intent(Confirmation.this.getApplicationContext(), LogIn.class);
                        startActivity(localIntent);
                    } else {
                        if (Helper.validate(a)) {
                            new HttpAsyncTask().execute(server + "/medialuna/spring/app/verificated/" + company + "_" + user + "/" + code.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "Favor de completar los campos", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent(Confirmation.this.getApplicationContext(), LogIn.class);
                    startActivity(localIntent);
                }


            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }

        @Override
        protected void onPostExecute(String result) {

            if (validacion) {
                if (result.equals("1")) {
                    Intent localIntent = new Intent(Confirmation.this.getApplicationContext(), MainActivity.class);
                    localIntent.putExtra("usercompany", company);
                    localIntent.putExtra("username", user);
                    startActivity(localIntent);
                    Toast.makeText(getApplicationContext(), "Usuario validado", Toast.LENGTH_LONG).show();
                }

                else {
                    Intent localIntent = new Intent(Confirmation.this.getApplicationContext(), LogIn.class);
                    startActivity(localIntent);
                    Toast.makeText(getApplicationContext(), "Codigo de acceso denegado", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Usuario no valido", Toast.LENGTH_LONG).show();
            }
        }
    }


    public static String POST(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            String json = "";

            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = Helper.convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {
                    validacion = false;
                } else {
                    validacion = true;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }



    @Override
    public void onRestart() {
        super.onRestart();
        Intent localIntent = new Intent(Confirmation.this.getApplicationContext(), LogIn.class);
        startActivity(localIntent);
        pc.clear();
    }

}
