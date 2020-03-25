package com.gm3s.erp.gm3srest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MovimientosConsumos extends AppCompatActivity {



    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    TableLayout codigos;
    Button btn_mov_con;
    static EditText etn_mov_con;
    private Timer timer = new Timer();
    private final long DELAY = 3000;
    static String cantidad;
    static String orden;
    static String tipo_partida;
    int j,jj;
    int id_temporal;
    int cantidad_temporal;
    String[] nombre_filas = {"CORT", "CTDA", "PRIM", "STEL", "SCON", "SLAV", "SESP", "TOTAL"};
    EditText matriz[][];
    ArrayList<Integer> listTotales = new ArrayList<>();
    ArrayList<Integer> listDefinidos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos_consumos);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();

        etn_mov_con = (EditText) findViewById(R.id.etn_mov_con);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Movimientos Consumos");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        btn_mov_con = (Button) findViewById(R.id.btn_mov_con);
        btn_mov_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (etn_mov_con.getText().toString().trim().equals("")) {

                } else {

                    orden = etn_mov_con.getText().toString();
                    listTotales.clear();
                    listDefinidos.clear();

                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/produccion/buscar/tallas/app");



                }
            }
        });




    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            codigos.removeAllViews();
            if (result.equals("[]") || result.equals("")) {
                Toast.makeText(getApplicationContext(), "No se encontro el numero de orden ", Toast.LENGTH_SHORT).show();
            }else{

            crearTablaArticulos(result);
            }
        }
    }


    public static String POST(String url) {



        HashMap<String, String> map = new HashMap<String, String>();
        map.put("@class", HashMap.class.getName());
        map.put("orden", orden);
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


    public void crearTablaArticulos(String result) {
        System.out.println("result 100 " + result.substring(0,100));
        System.out.println("result 200 " + result.substring(100));

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<HashMap> arrayData = mapper.readValue(result, ArrayList.class);
            HashMap<String, String> columnas = (HashMap) arrayData.get(0);
            HashMap<String, ArrayList> filas = (HashMap) arrayData.get(1);
            HashMap<String, Integer> dimensiones = (HashMap) arrayData.get(2);
            //  codigos.removeAllViews();
            int filas_int = (Integer) dimensiones.get("filas");
            int columnas_int = (Integer) dimensiones.get("columnas");

            for(int i=0; i<filas_int-1; i++){
                listTotales.add(i,0);
                listDefinidos.add(i,0);
            }

            codigos.removeAllViews();
            if (filas_int != 1) {
                matriz = new EditText[filas_int + 1][columnas_int + 1];
                String[] ary = filas.get("cantidades").get(1).toString().substring(1, filas.get("cantidades").get(1).toString().length() - 1).split("]");
                String[] ary2 = Arrays.toString(ary).substring(2, Arrays.toString(ary).length() - 1).split("\\[");
                String[] ary3 = Arrays.toString(ary2).substring(1, Arrays.toString(ary2).length() - 1).split(", , , ");
                TableRow tr1 = new TableRow(MovimientosConsumos.this);
                for (jj = 0; jj < filas_int + 1; jj++) {
                    final TextView c00 = new TextView(MovimientosConsumos.this);
                    c00.setText(columnas.get(String.valueOf(jj)));
                    tr1.addView(c00);

                }
                codigos.addView(tr1);
                for (j = 0; j < 8; j++) {
                    TableRow tr = new TableRow(MovimientosConsumos.this);
                    final TextView c0 = new TextView(MovimientosConsumos.this);
                    c0.setText(nombre_filas[j]);
                    if (j == 7) {
                    } else {
                        tr.addView(c0);
                        for (jj = 0; jj < filas_int; jj++) {

                            String[] ary4 = ary2[j].split(", ");


                            final EditText c00 = new EditText(MovimientosConsumos.this);
                            c00.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                            c00.setText(ary4[jj].substring(0, ary4[jj].length() - 2));
                            c00.setId(Integer.parseInt(j + "" + jj));

                            if (j < 2) {
                                c00.setFocusable(false);
                                c00.setEnabled(false);
                            }

                            if(j==1 && jj<listTotales.size()){
                                listDefinidos.set(jj, Integer.parseInt(c00.getText().toString()) + 15);

                            }

                            if (jj == (filas_int - 1)) {
                                System.out.println("c00    " + c00.getText().toString());
                                c00.setFocusable(false);
                                c00.setEnabled(false);
                            }
                            if((j>1) && jj<listTotales.size()){
                               // int temp = listTotales.get(jj) + Integer.parseInt(c00.getText().toString());
                               // System.out.println("listTotales " +  jj + "   = " + temp);
                                listTotales.set(jj,listTotales.get(jj) + Integer.parseInt(c00.getText().toString()));
                            }


                            tr.addView(c00);
                        }
                        codigos.addView(tr);

                    }


                }


                for (j = 0; j < columnas_int; j++) {


                    for (jj = 0; jj < filas_int; jj++) {


                        int id = getResources().getIdentifier(String.valueOf(Integer.parseInt(j + "" + jj)), "id", getPackageName());
                        final EditText c00 = (EditText) findViewById(id);
                        final int aux = j;
                        final int aux2 = jj;

                        c00.addTextChangedListener(new TextWatcher() {


                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                          int after) {
                                // auxc = c0.getText().toString();

                            }

                            @Override
                            public void onTextChanged(final CharSequence s, int start, int before,
                                                      int count) {
                            }

                            @Override
                            public void afterTextChanged(final Editable s) {
                                //avoid triggering event when text is too short


                                if (s.length() > 0) {
                                    timer.cancel();
                                    timer = new Timer();
                                    timer.schedule(
                                            new TimerTask() {
                                                @Override
                                                public void run() {

                                                    if (c00.getText().toString().equals("")) {
                                                        setText(c00, "0");

                                                    }
                                                    else if (Integer.parseInt(c00.getText().toString()) + listTotales.get(aux2) > listDefinidos.get(aux2)) {
                                                        callToast("Se ha exedido del tamaño permitido");
                                                        setText(c00, "0");

                                                    } else {

                                                        cantidad = c00.getText().toString();
                                                        listTotales.set(aux2, listTotales.get(aux2) + Integer.parseInt(c00.getText().toString()));
                                                        HttpAsyncTask2 a = new HttpAsyncTask2();
                                                        a.execute(server + "/medialuna/spring/produccion/actualizar/cantidad/partidas/" + String.valueOf(Integer.parseInt(String.valueOf(c00.getId()).substring(0, 1))-2) + "/" + String.valueOf(Integer.parseInt(String.valueOf(c00.getId()).substring(1, 2)) + 1) + "/app");
                                                    }
                                                }
                                            },
                                            DELAY
                                    );
                                }


                            }
                        });


                    }
                }


                TableRow tr2 = new TableRow(MovimientosConsumos.this);
                final TextView c0 = new TextView(MovimientosConsumos.this);
                c0.setText("TOTALES   ");
                tr2.addView(c0);
                for (j = 0; j < listTotales.size(); j++) {
                    final TextView c00 = new TextView(MovimientosConsumos.this);
                    c00.setText(listTotales.get(j).toString());
                    tr2.addView(c00);
                }
                codigos.addView(tr2);



            } else {
                TableRow tr = new TableRow(MovimientosConsumos.this);
                final TextView c00 = new TextView(MovimientosConsumos.this);
                c00.setText("No se encontraron resultados.");
                tr.addView(c00);
                codigos.addView(tr);


            }


        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),"Cambio guardado", Toast.LENGTH_SHORT).show();
            listTotales.clear();
            listDefinidos.clear();

            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/produccion/buscar/tallas/app");

        }
    }


    public static String POST2(String url) {



        HashMap<String, String> map = new HashMap<String, String>();
        map.put("@class", HashMap.class.getName());
        map.put("orden", orden);
        map.put("cantidad", cantidad);

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

    private void setText(final EditText text, final String value) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (etn_mov_con.getText().toString().trim().equals("")) {

        } else {

            orden = etn_mov_con.getText().toString();
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/produccion/buscar/tallas/app");



        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (etn_mov_con.getText().toString().trim().equals("")) {

        } else {

            orden = etn_mov_con.getText().toString();
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/produccion/buscar/tallas/app");



        }



    }




    private void callToast(final String value) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
