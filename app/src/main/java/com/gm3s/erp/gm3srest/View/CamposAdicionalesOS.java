package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CamposAdicionalesOS extends AppCompatActivity {
    private static PersistentCookieStore pc;
    Button btn_aceptar;
    Button btn_cirujano;
    EditText fecha_ca_et;
    EditText hora_ca_et;
    EditText dir_ca_et;
    EditText pac_ca_et;
    EditText cir_ca_et;
    String server = "";
    private SharedPreference sharedPreference;
    LinkedList<Map<String,Object>> valoresCamposAdicionales = new LinkedList();
    String tipo_documento = "";
    String idDocumento = "";
    List<String> listaCirujanos = new ArrayList<>();
    int selected = 0;
    int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campos_adicionales_os);

       pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
       server = sharedPreference.getValue(this);
       btn_aceptar = (Button) findViewById(R.id.btn_aceptar);
       btn_cirujano = (Button) findViewById(R.id.btn_cirujano);
       fecha_ca_et = (EditText) findViewById(R.id.fecha_ca_et);
       hora_ca_et = (EditText) findViewById(R.id.hora_ca_et);
       dir_ca_et = (EditText) findViewById(R.id.dir_ca_et);
       pac_ca_et = (EditText) findViewById(R.id.pac_ca_et);
       cir_ca_et = (EditText) findViewById(R.id.cir_ca_et);

        Intent intent = getIntent();
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");
        idDocumento = (String)intent.getSerializableExtra("idDocumento");

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoresCamposAdicionales.clear();
                Map<String,Object> m1 = new HashMap();
                m1.put("idAdicional", 27);
                m1.put("valor", fecha_ca_et.getText().toString());
                valoresCamposAdicionales.add(m1);
                Map<String,Object> m2 = new HashMap();
                m2.put("idAdicional",30);
                m2.put("valor", hora_ca_et.getText().toString());
                valoresCamposAdicionales.add(m2);
                Map<String,Object> m3 = new HashMap();
                m3.put("idAdicional", 25);
                m3.put("valor", dir_ca_et.getText().toString());
                valoresCamposAdicionales.add(m3);
                Map<String,Object> m4 = new HashMap();
                m4.put("idAdicional", 36);
                m4.put("valor", pac_ca_et.getText().toString());
                valoresCamposAdicionales.add(m4);
                Map<String,Object> m5 = new HashMap();
                m5.put("idAdicional", 35);
                m5.put("valor", cir_ca_et.getText().toString());
                valoresCamposAdicionales.add(m5);



                HttpAsyncTask14 c = new HttpAsyncTask14();
                c.execute(server + "/medialuna/spring/documento/app/ordenServicio");
            }
        });

       /* btn_cirujano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCirujano();
            }
        });*/

        HttpAsyncTask15 d = new HttpAsyncTask15();
        d.execute(server + "/medialuna/spring/listar/catalogo/10601/");
    }

    private class HttpAsyncTask14 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST14(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CamposAdicionalesOS.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public String POST14(String url) {
        Map map3 = new HashMap();
        map3.put("valoresCA", valoresCamposAdicionales);
        map3.put("@class", ArrayList.class.getName());

        HashMap map2 = new HashMap();
        map2.put("idDocumento", idDocumento);
        map2.put("entidadDocumento",tipo_documento);
        map2.put("@class", HashMap.class.getName());

        Map totalisimo = new HashMap();
        totalisimo.put("@class", HashMap.class.getName());
        totalisimo.put("valores",map3.toString());
        totalisimo.put("info", map2.toString());

        JSONObject jsonOBJECT1 = new JSONObject(totalisimo);
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");

            StringEntity params = new StringEntity(jsonOBJECT1.toString());
            System.out.println("LO que se envia 1" + jsonOBJECT1.toString());
            System.out.println("LO que se envia 2" + totalisimo);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                    System.out.println("Y por aqui el result: " + result);
                } else
                    result = "Did not work!";
            } catch (IOException e) {
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");
            e.printStackTrace();
        }
        return result;
    }


    private class HttpAsyncTask15 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) { return POST15(urls[0]); }

        @Override
        protected void onPostExecute(String result) {  convertirDatosCirujano(result);}
    }

    public String POST15(String url) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.addHeader("Content-Type", "application/json");
            String json = "";
            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);

                } else
                    result = "Did not work!";
                if (result.contains("GM3s Software Index")) {
                    System.out.println();
                } else {
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");
            e.printStackTrace();
        }
        return result;
    }


    public void convertirDatosCirujano(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            listaCirujanos.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {
                listaCirujanos.add((String) ((HashMap) arrayData.get(i)).get("nombre"));
            }
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

   public void alertCirujano(){
      AlertDialog.Builder builder = new AlertDialog.Builder(CamposAdicionalesOS.this);
       builder.setTitle("Ciujano");
       builder.setSingleChoiceItems(listaCirujanos.toArray(new String[listaCirujanos.size()]), 0, new DialogInterface.OnClickListener() {

           @Override
           public void onClick(DialogInterface dialog, int which) {
               // TODO Auto-generated method stub
               temp = which;
           }


       });

       builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

           @Override
           public void onClick(DialogInterface dialog, int which) {
               selected = temp;
               switch (selected) {
                   default:
                            cir_ca_et.setText(listaCirujanos.get(selected));
                       break;
               }
               dialog.cancel();
           }
       });

       builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
           }
       });

       AlertDialog al = builder.create();
       al.show();
   }
}
