package com.gm3s.erp.gm3srest.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Model.ReporteGerencial;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstadoResultados extends AppCompatActivity {


    public static ImageView loader;
    private static PersistentCookieStore pc;
    private static boolean validacion;
    List<ReporteGerencial> lista_rg = new ArrayList<ReporteGerencial>();
    TableLayout prices;
    private SharedPreference sharedPreference;
    String server = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_resultados);
        pc = new PersistentCookieStore(this);
        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        prices =(TableLayout)findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Estado de Resultados");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loader.setVisibility(View.VISIBLE);
        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server +"/medialuna/spring/reporte/resultados/0");

        /*HttpGet httppost = new HttpGet(server +"/medialuna/spring/reporte/resultados/0");
        HttpAsynckTaskX asynck1 = new HttpAsynckTaskX(httppost, pc);
        asynck1.execute();
        @Override
        protected void onPostExecute(String result) {
            //  Toast.makeText(getActivity(), "VALIDACION: " + validacion, Toast.LENGTH_LONG).show();
            if (validacion) {
                convertirDatos2(result);
                loader.setVisibility(View.INVISIBLE);
            }

            else {
                Toast.makeText(EstadoResultados.this, "Usuario no valido", Toast.LENGTH_LONG).show();


            }
        }*/


    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
                return POST(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            if (validacion) {
                convertirDatos2(result);
                loader.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(EstadoResultados.this, "Usuario no valido", Toast.LENGTH_LONG).show();
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
            httppost.setHeader("Accept","application/json; text/javascript");
            httppost.setHeader("Content-Type","application/json");
            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null){
                    result = Helper.convertInputStreamToString(inputStream);
                }
                else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {
                    validacion = false;
                } else {
                    validacion = true;
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



    public void convertirDatos2 (String cadena){
        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_rg.clear();
            prices.removeAllViews();
            Map<String,Object> tmp = mapper.readValue(cadena, Map.class);

            List<String> keys = new ArrayList<String>(tmp.keySet());
            List<Object> lista = new ArrayList<Object>(tmp.keySet());
            ArrayList object = (ArrayList) tmp.get(lista.get(1));
            ArrayList array = (ArrayList) object.get(1);

            int y=0;
            for (y=0; y<array.size(); y++) {
                Map mapa1 = (Map) array.get(y);

                List<Object> l = new ArrayList<Object>(mapa1.keySet());
                ReporteGerencial rg = new ReporteGerencial(Integer.parseInt(mapa1.get(l.get(1)).toString()),0,l.get(2).toString());
                lista_rg.add(rg);
                ArrayList object2 = (ArrayList) mapa1.get(l.get(2));


                ArrayList array2 = (ArrayList) object2.get(1);


                int z=0;
                for (z=0; z<array2.size(); z++) {
                    Map mapa2 = (Map) array2.get(z);
                    List<Object> l2 = new ArrayList<Object>(mapa2.keySet());
                    ArrayList object3 = (ArrayList) mapa2.get(l2.get(1));
                    ReporteGerencial rg2 = new ReporteGerencial(Integer.parseInt(mapa1.get(l.get(1)).toString()), Integer.parseInt(mapa2.get(l2.get(3)).toString()) ,"  " + mapa2.get(l2.get(2)).toString(), Helper.numberFormat(BigDecimal.valueOf(Double.parseDouble(object3.get(1).toString())).setScale(2, BigDecimal.ROUND_UP).toString()));
                    lista_rg.add(rg2);
                }

            }
            prices.removeAllViews();

            TableRow tr0 = new TableRow(this);
            TextView c10 = (TextView)this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
            c10.setText("Descripcion");

            TextView c20 = (TextView)this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
            c20.setText("Total");

            tr0.addView(c10);
            tr0.addView(c20);

            prices.addView(tr0);

            for(int j = 0; j < lista_rg.size(); j++) {
                TableRow tr = new TableRow(this);
                TextView c1 = (TextView)this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(lista_rg.get(j).getNombre());

                TextView c2 = (TextView)this.getLayoutInflater().inflate(R.layout.renglones, null);
                c2.setText(lista_rg.get(j).getValor());


                if ((j % 2) == 0) {
                    c1.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                    c2.setBackground(getResources().getDrawable(R.drawable.alt_row_color));

                } else  {
                    c1.setBackground(getResources().getDrawable(R.drawable.row_color));
                    c2.setBackground(getResources().getDrawable(R.drawable.row_color));

                }


                tr.addView(c1);
                tr.addView(c2);

                prices.addView(tr);
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }




}
