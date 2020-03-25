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

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.ReporteGerencial;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReporteGerencialActividad  extends AppCompatActivity {

    public static ImageView loader;
    private static PersistentCookieStore pc;
    private static boolean validacion;
    List<ReporteGerencial> lista_rg = new ArrayList<ReporteGerencial>();
    TableLayout prices;
    String server = "";
    private SharedPreference sharedPreference;
    boolean white_back = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_gerencial);
        pc = new PersistentCookieStore(this);
        prices =(TableLayout) findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        loader = (ImageView)  findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Reporte Gerencial");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server +"/medialuna/spring/reporte/gerencial/");



    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getActivity(), "VALIDACION: " + validacion, Toast.LENGTH_LONG).show();
            if (validacion) {
                convertirDatos(result);
                loader.setVisibility(View.INVISIBLE);
            }
            else {
                Toast.makeText(ReporteGerencialActividad.this, "Usuario no valido", Toast.LENGTH_LONG).show();


            }
        }
    }


        public String POST(String url) {
            String result = "";
            InputStream inputStream = null;
            try {

                DefaultHttpClient httpclient = new DefaultHttpClient();
                httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
                HttpGet httppost = new HttpGet(url);
                httppost.setHeader("Accept", "application/json; text/javascript");
                httppost.setHeader("Content-Type", "application/json");
                String json = "";
                try {

                    HttpResponse httpResponse = httpclient.execute(httppost);
                    inputStream = httpResponse.getEntity().getContent();
                    if (inputStream != null) {
                        result = Helper.convertInputStreamToString(inputStream);
                    } else
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




        public void convertirDatos(String cadena) {

            ObjectMapper mapper = new ObjectMapper();

            try {
                lista_rg.clear();
                prices.removeAllViews();

                List<Map<String, Object>> arrayData = mapper.readValue(cadena, List.class);
                int i;
                for (i = 0; i < arrayData.size(); i++) {
                    Map<String, Object> tmp = arrayData.get(i);
                    List<String> keys = new ArrayList<String>(tmp.keySet());
                    if (keys.size() == 3) {
                        List<Object> lista = new ArrayList<Object>(tmp.keySet());
                        System.out.println("tmp.get(lista.get(1)).toString()   " + tmp.get(lista.get(1)).toString());
                        System.out.println("keys.get(2)  " + keys.get(2));
                     //   System.out.println();
                        //(tmp.get(lista.get(1)).toString()), 0, keys.get(2)
                        ReporteGerencial rg = new ReporteGerencial(Integer.parseInt(tmp.get(lista.get(1)).toString()), 0, keys.get(2));
                        lista_rg.add(rg);
                        ArrayList object = (ArrayList) tmp.get(lista.get(2));
                        ArrayList array = (ArrayList) object.get(1);
                         int y = 0;
                        for (y = 0; y < array.size(); y++) {
                            Map mapa1 = (Map) array.get(y);
                            List<Object> l = new ArrayList<Object>(mapa1.keySet());
                            ArrayList object2 = (ArrayList) mapa1.get(l.get(1));
                            ReporteGerencial rg2 = new ReporteGerencial(Integer.parseInt(tmp.get(lista.get(1)).toString()), Integer.parseInt(mapa1.get(l.get(3)).toString()), "    " + mapa1.get(l.get(2)).toString(), Helper.numberFormat(BigDecimal.valueOf(Double.parseDouble(object2.get(1).toString())).setScale(2, BigDecimal.ROUND_UP).toString()));
                            lista_rg.add(rg2);
                        }
                    } else {
                        List<Object> lista = new ArrayList<Object>(tmp.keySet());
                        ArrayList object3 = (ArrayList) tmp.get(lista.get(1));
                        ReporteGerencial rg = new ReporteGerencial(Integer.parseInt(tmp.get(lista.get(2)).toString()), 0, keys.get(3));
                        lista_rg.add(rg);
                        ArrayList object = (ArrayList) tmp.get(lista.get(3));
                        ArrayList array = (ArrayList) object.get(1);
                        int y = 0;
                        for (y = 0; y < array.size(); y++) {
                            Map mapa1 = (Map) array.get(y);
                            List<Object> l = new ArrayList<Object>(mapa1.keySet());
                            ArrayList object2 = (ArrayList) mapa1.get(l.get(1));
                            ReporteGerencial rg2 = new ReporteGerencial(Integer.parseInt(tmp.get(lista.get(2)).toString()), y + 1, "    " + mapa1.get(l.get(2)).toString(),Helper.numberFormat(BigDecimal.valueOf(Double.parseDouble(object2.get(1).toString())).setScale(2, BigDecimal.ROUND_UP).toString()));
                            lista_rg.add(rg2);
                          }

                    }
                }


                Collections.sort(lista_rg, new Comparator<ReporteGerencial>() {
                    @Override
                    public int compare(ReporteGerencial p1, ReporteGerencial p2) {
                        return p1.getId() - p2.getId();
                    }

                });


                TableRow tr0 = new TableRow(this);
                TextView c10 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
                c10.setText("Descripcion");

                TextView c20 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
                c20.setText("Total");



                tr0.addView(c10);
                tr0.addView(c20);

                prices.addView(tr0);

                for (int j = 0; j < lista_rg.size(); j++) {
                    TableRow tr = new TableRow(this);

                   /* if(lista_rg.get(j).getNombre().length()>21){
                        LinearLayout l1 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.renglones, null);
                        l1.setOrientation(LinearLayout.VERTICAL);
                         // l1.setbo

                        TextView c1 = new TextView(getActivity());
                        c1.setText(lista_rg.get(j).getNombre().substring(0,21));


                        TextView c2 = new TextView(getActivity());
                        c2.setText(lista_rg.get(j).getNombre().substring(21));
                 POR VENCER 91 O MAS
                 BODEGA DE TELAS PENDIENTE

                        l1.addView(c1);
                        l1.addView(c2);
                        tr.addView(l1);
                    }
                    else{*/
                        TextView c1 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
                       if(lista_rg.get(j).getNombre().length()>22){
                           c1.setText(lista_rg.get(j).getNombre().substring(0,22));
                       }else{
                           c1.setText(lista_rg.get(j).getNombre());
                       }



                    //}





                    TextView c2 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
                    c2.setText(lista_rg.get(j).getValor());



                    if (white_back == true) {
                        c1.setBackground(getResources().getDrawable(
                                R.drawable.alt_row_color));
                        c2.setBackground(getResources().getDrawable(
                                R.drawable.alt_row_color));
                        white_back = false;
                    } else if(white_back == false) {
                        c1.setBackground(getResources().getDrawable(
                                R.drawable.row_color));
                        c2.setBackground(getResources().getDrawable(
                                R.drawable.row_color));
                        white_back = true;
                    }
                    tr.addView(c1);
                    tr.addView(c2);
                    prices.addView(tr);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

