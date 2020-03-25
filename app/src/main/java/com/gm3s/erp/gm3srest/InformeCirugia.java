package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.ListaOrdenServicio;
import com.gm3s.erp.gm3srest.View.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformeCirugia extends AppCompatActivity {
    TableLayout tabla_cad;
    String server = "";
    static String idDocumento, tipo_documento,folio_documento;
    private static PersistentCookieStore pc;
    private SharedPreference sharedPreference;
    List<String> nombreCatalogo = new ArrayList<String>();
    int selected = 0;
    int temp;
    EditText tmp;
    Integer idCampo;
    Button guardar_bt_cad;
    static ArrayList<Map> camposAdicionalesMap = new ArrayList<>();
    Spinner track_spn;
    final List<String> spinner_list=new ArrayList<String>();
    HashMap tracking_list = new HashMap();
    ArrayAdapter<String> adp1;
    boolean camposVacios = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_cirugia);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Campos Adicionales");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformeCirugia.this, ListaOrdenServicio.class);
                intent.putExtra("tipo_documento", tipo_documento);
                startActivity(intent);
                finish();
            }
        });

        tabla_cad = (TableLayout) findViewById(R.id.tabla_cad);
        guardar_bt_cad = (Button) findViewById(R.id.guardar_bt_cad);
        track_spn = (Spinner) findViewById(R.id.track_spn);
        //track_spn.setVisibility(View.INVISIBLE);
        adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        track_spn.setAdapter(adp1);

        Intent intent = getIntent();
        idDocumento = (String)intent.getSerializableExtra("idDocumento");
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");
        folio_documento = (String)intent.getSerializableExtra("folio_documento");

        //HttpAsyncTask a = new HttpAsyncTask();
        //a.execute(server + "/medialuna/spring/documento/campoAdicional/buscarDocCampoAdicionalValor?id=" + idDocumento + "&entidadDocumento=" + tipo_documento);



    HttpAsyncTask4 b = new HttpAsyncTask4();
      b.execute(server + "/medialuna/spring/documento/obtener/tracking/" + tipo_documento + "/" + idDocumento+"/");

        guardar_bt_cad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask3 b = new HttpAsyncTask3();
                b.execute(server + "/medialuna/spring/documento/campoAdicional/guardarDocCampoAdicionalValor");
            }
        });




      /*  Map m1 = new HashMap();
        m1.put("tipoActual","cotizacioncliente");
        m1.put("folioActual","2510");
        m1.put("idActual", "3730");
        tracking_list.add(m1);
        spinner_list.add(m1.get("tipoActual").toString()+"  [" + m1.get("folioActual").toString() + "]");



    adp1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,spinner_list);
    adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    track_spn.setAdapter(adp1);*/


        track_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
                // TODO Auto-generated method stub

                System.out.println(spinner_list.get(position));
                System.out.println(Integer.parseInt(spinner_list.get(position)));
                System.out.println("-->"+tracking_list);
                System.out.println("--->"+tracking_list.get(Integer.parseInt(spinner_list.get(position))));
                System.out.println("---->"+tracking_list.get(3730));

                tipo_documento = tracking_list.get(Integer.parseInt(spinner_list.get(position))).toString();
                idDocumento = spinner_list.get(position);
                HttpAsyncTask a = new HttpAsyncTask();
                a.execute(server + "/medialuna/spring/documento/campoAdicional/buscarDocCampoAdicionalValor?id=" + idDocumento + "&entidadDocumento=" + tipo_documento);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });



    }




    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println("camposAdicionales:" + result);

            if(result.equals("[]")){
                camposVacios = true;
                HttpAsyncTask5 a = new HttpAsyncTask5();
                a.execute(server + "/medialuna/spring/documento/campoAdicional/buscarDocCampoAdicional?entidadDocumento=" + tipo_documento);
            }
            else {
                camposVacios = false;
                convertirDatosCamposAdicionales(result);
            }
        }
    }

    public static String GET(String url) {


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
           // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            String json = "";

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
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");
            e.printStackTrace();
        }
        return result;
    }


    public void convertirDatosCamposAdicionales(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            camposAdicionalesMap.clear();
            tabla_cad.removeAllViews();
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {

                LinearLayout ly = new LinearLayout(InformeCirugia.this);
                ly.setOrientation(LinearLayout.VERTICAL);

                LinearLayout lyh = new LinearLayout(InformeCirugia.this);
                lyh.setOrientation(LinearLayout.HORIZONTAL);

                final HashMap campoAdicional =  new HashMap();
                campoAdicional.put("documentoCampoAdicional", (HashMap) ((HashMap) arrayData.get(i)).get("documentoCampoAdicional"));
                campoAdicional.put("valor", (String) ((HashMap) arrayData.get(i)).get("valor"));
                campoAdicional.put("orden", (Integer) ((HashMap) ((HashMap) arrayData.get(i)).get("documentoCampoAdicional")).get("orden"));

                Map mapaValortmp = new HashMap<>();
                mapaValortmp.put("idAdicional", ((Integer) ((HashMap) campoAdicional.get("documentoCampoAdicional")).get("id")));
                mapaValortmp.put("valor",campoAdicional.get("valor"));
                mapaValortmp.put("@class",Map.class.getName());
                camposAdicionalesMap.add(mapaValortmp);

                TableRow tr = new TableRow(this);



                TextView tv1 = new TextView(this);
                tv1.setText("" + ((HashMap) ((HashMap) campoAdicional.get("documentoCampoAdicional")).get("adicional")).get("nombre") + "   ");
                tv1.setTextSize(Float.parseFloat("18.0"));
                tv1.setTypeface(null, Typeface.BOLD);
               // tr.addView(tv1);



                lyh.addView(tv1);
                ly.addView(lyh);



                LinearLayout lyv = new LinearLayout(InformeCirugia.this);
                lyv.setOrientation(LinearLayout.HORIZONTAL);

                final EditText et2 = new EditText(this);
                et2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                et2.setPadding(0, 0, 0, 0);
                et2.setText("" + campoAdicional.get("valor"));
                et2.setTextSize(Float.parseFloat("15.0"));
                et2.addTextChangedListener(new TextWatcher() {
                    // the user's changes are saved here
                    public void onTextChanged(CharSequence c, int start, int before, int count) {
                        guardarCambios(((Integer) ((HashMap) campoAdicional.get("documentoCampoAdicional")).get("id")), et2.getText().toString());
                    }

                    public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                        // this space intentionally left blank
                    }

                    public void afterTextChanged(Editable c) {
                        // this one too
                    }
                });
                //  tr.addView(et2);


                if(((HashMap) campoAdicional.get("documentoCampoAdicional")).get("claveCatalogoSimple").toString()!=""){


                    final String claveCtg = ((HashMap) campoAdicional.get("documentoCampoAdicional")).get("claveCatalogoSimple").toString();
                    et2.setEnabled(false);

                    //TextView tv2 = new TextView(this);
                    //tv2.setText("" + campoAdicional.get("valor"));
                    //tv2.setTextSize(Float.parseFloat("15.0"));
                    //tv2.setVisibility(View.INVISIBLE);

                    Button btn3 = new Button(this);
                    btn3.setText("OPCIONES");
                  //  btn3.setBackgroundColor(Color.parseColor("#17c117"));
                    btn3.setTextColor(Color.parseColor("#2196F3"));
                    btn3.setTextSize(Float.parseFloat("10.0"));
                    btn3.setTypeface(null, Typeface.BOLD);
                   // btn3.setBackgroundResource(R.drawable.boton_verde_small);
                   // btn3.setLayoutParams(new TableRow.LayoutParams(220, 125));
                 //   btn3.setPadding(20,0,0,0);

                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tmp = et2;
                            idCampo = ((Integer) ((HashMap) campoAdicional.get("documentoCampoAdicional")).get("id"));
                            HttpAsyncTask2 b = new HttpAsyncTask2();
                            b.execute(server + "/medialuna/spring/listar/catalogo/" + claveCtg + "/");
                        }
                    });


                    lyv.addView(btn3);



                }
                lyv.addView(et2);
                ly.addView(lyv);


                tr.addView(ly);


                if(i%2==0){
                    tr.setBackgroundColor(Color.parseColor("#267F7FFF"));
                    // a.setTextColor(Color.parseColor("#FFFFFF"));
                }
                tabla_cad.addView(tr);

            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private class HttpAsyncTask5 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET5(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {

                convertirDatosCamposAdicionales2(result);

        }
    }

    public static String GET5(String url) {


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            String json = "";

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
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");
            e.printStackTrace();
        }
        return result;
    }


    public void convertirDatosCamposAdicionales2(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {

                 camposAdicionalesMap.clear();
                tabla_cad.removeAllViews();
                List<Object> arrayData = mapper.readValue(cadena, List.class);
                for (int i = 0; i < arrayData.size(); i++) {

                    LinearLayout ly = new LinearLayout(InformeCirugia.this);
                    ly.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout lyh = new LinearLayout(InformeCirugia.this);
                    lyh.setOrientation(LinearLayout.HORIZONTAL);

                    final HashMap campoAdicional =  new HashMap();
                    campoAdicional.put("nombre", ((String) ((HashMap) (arrayData.get(i))).get("nombre")));
                    campoAdicional.put("claveCatalogoSimpleAsociado", ((String) ((HashMap) (arrayData.get(i))).get("claveCatalogoSimpleAsociado")));
                    campoAdicional.put("id", ((Integer) ((HashMap) (arrayData.get(i))).get("id")));
                    campoAdicional.put("valor", "");
                    //camposAdicionales.add(campoAdicional);

                    Map mapaValortmp = new HashMap<>();
                    mapaValortmp.put("idAdicional", ((Integer) campoAdicional.get("id")));
                    mapaValortmp.put("valor",campoAdicional.get("valor"));
                    mapaValortmp.put("@class",Map.class.getName());
                    camposAdicionalesMap.add(mapaValortmp);

                    TableRow tr = new TableRow(this);



                    TextView tv1 = new TextView(this);
                    tv1.setText("" + campoAdicional.get("nombre").toString());
                    tv1.setTextSize(Float.parseFloat("18.0"));
                    tv1.setTypeface(null, Typeface.BOLD);
                    // tr.addView(tv1);



                    lyh.addView(tv1);
                    ly.addView(lyh);



                    LinearLayout lyv = new LinearLayout(InformeCirugia.this);
                    lyv.setOrientation(LinearLayout.HORIZONTAL);

                    final EditText et2 = new EditText(this);
                    et2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    et2.setPadding(0, 0, 0, 0);
                    et2.setText("" + campoAdicional.get("valor"));
                    et2.setTextSize(Float.parseFloat("15.0"));
                    et2.addTextChangedListener(new TextWatcher() {
                        // the user's changes are saved here
                        public void onTextChanged(CharSequence c, int start, int before, int count) {
                            guardarCambios(((Integer) campoAdicional.get("id")), et2.getText().toString());
                        }

                        public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                            // this space intentionally left blank
                        }

                        public void afterTextChanged(Editable c) {
                            // this one too
                        }
                    });
                    //  tr.addView(et2);


                    if(((String) campoAdicional.get("claveCatalogoSimpleAsociado"))!=""){


                        final String claveCtg = (((String) campoAdicional.get("claveCatalogoSimpleAsociado")).toString());
                        et2.setEnabled(false);

                        //TextView tv2 = new TextView(this);
                        //tv2.setText("" + campoAdicional.get("valor"));
                        //tv2.setTextSize(Float.parseFloat("15.0"));
                        //tv2.setVisibility(View.INVISIBLE);

                        Button btn3 = new Button(this);
                        btn3.setText("OPCIONES");
                        //  btn3.setBackgroundColor(Color.parseColor("#17c117"));
                        btn3.setTextColor(Color.parseColor("#2196F3"));
                        btn3.setTextSize(Float.parseFloat("10.0"));
                        btn3.setTypeface(null, Typeface.BOLD);
                        // btn3.setBackgroundResource(R.drawable.boton_verde_small);
                        // btn3.setLayoutParams(new TableRow.LayoutParams(220, 125));
                        //   btn3.setPadding(20,0,0,0);

                        btn3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tmp = et2;
                                idCampo =  ((Integer) campoAdicional.get("id"));
                                HttpAsyncTask2 b = new HttpAsyncTask2();
                                b.execute(server + "/medialuna/spring/listar/catalogo/"+claveCtg+"/");
                            }
                        });


                        lyv.addView(btn3);



                    }
                    lyv.addView(et2);
                    ly.addView(lyv);


                    tr.addView(ly);


                    if(i%2==0){
                        tr.setBackgroundColor(Color.parseColor("#267F7FFF"));
                        // a.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    tabla_cad.addView(tr);

            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST2(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            convertirDatosCatalogo(result);
        }
    }

    public String POST2(String url) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.addHeader("Content-Type", "application/json");
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


    public void convertirDatosCatalogo(String cadena) {
        nombreCatalogo.clear();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {
                nombreCatalogo.add((String) ((HashMap) arrayData.get(i)).get("nombre"));
            }
            CatalogoPopUp();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void CatalogoPopUp() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(InformeCirugia.this);
        builder.setTitle("Catalago");

        builder.setSingleChoiceItems(nombreCatalogo.toArray(new String[nombreCatalogo.size()]), 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                temp = which;
            }


        });
        // TODO Auto-generated method stub


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = temp;
                switch (selected) {
                    default:
                        tmp.setText(nombreCatalogo.get(selected));
                        guardarCambios(idCampo, nombreCatalogo.get(selected));
                        break;
                }

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }


    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET3(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),"Campos Adicionales Almacenados", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InformeCirugia.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    public static String GET3(String url) {

        System.out.println("GET" + idDocumento + " " + tipo_documento);

        ArrayList tipo0 = new ArrayList();
        tipo0.add(List.class.getName());
        tipo0.add(camposAdicionalesMap);



        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", Map.class.getName());
        map.put("entidadDocumento", tipo_documento);
        map.put("idDocumento", Integer.parseInt(idDocumento));
        map.put("valores", tipo0);


        String objectStr = JSONValue.toJSONString(map);


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(objectStr);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);
            String json = "";

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
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");
            e.printStackTrace();
        }
        return result;
    }

    public void guardarCambios(Integer idCampoAdicional,String valor){
        for(Map map : camposAdicionalesMap){
          if(map.get("idAdicional") == idCampoAdicional){
              map.put("valor",valor);
          }
        }

    }


    private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST4(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("resultado: " + result);
            if(result.equals("[]")){
                tracking_list.clear();
                spinner_list.clear();
                tracking_list.put(Integer.parseInt(idDocumento), tipo_documento);
                System.out.println(tracking_list);
                spinner_list.add(idDocumento);
                adp1.add(tipo_documento+"  [" + folio_documento + "]");
                adp1.notifyDataSetChanged();
            }else{
            convertirDatosTracking(result);
            }
        }
    }

    public String POST4(String url) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.addHeader("Content-Type", "application/json");
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


    public void convertirDatosTracking(String cadena) {
        tracking_list.clear();
        spinner_list.clear();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {

                if(!tracking_list.containsKey((Integer) ((HashMap) arrayData.get(i)).get("idActual"))){
                tracking_list.put((Integer) ((HashMap) arrayData.get(i)).get("idActual"), (String) ((HashMap) arrayData.get(i)).get("tipoActual"));
                spinner_list.add(((Integer) ((HashMap) arrayData.get(i)).get("idActual")).toString());
                adp1.add((String) ((HashMap) arrayData.get(i)).get("tipoActual")+"  [" + (Integer) ((HashMap) arrayData.get(i)).get("folioActual") + "]");
                adp1.notifyDataSetChanged();
                }
            }


           /* adp1=new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,spinner_list);
            adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            track_spn.setAdapter(adp1);*/


            // track_spn.setVisibility(View.VISIBLE);

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
