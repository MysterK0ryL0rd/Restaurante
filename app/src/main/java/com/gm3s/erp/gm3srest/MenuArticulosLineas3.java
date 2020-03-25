package com.gm3s.erp.gm3srest;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class MenuArticulosLineas3 extends Fragment {
    String tipo_documento = "";
    static String idLinea;
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    private static boolean validacion;
    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Map<String, String>> lista_art_temporales = new ArrayList<Map<String, String>>();
    TableLayout prices;
    Button ok_button;
    Map<Integer, Articulo> mapa_articulos = new HashMap<>();
    Integer contador_int = 0;
    Map<Integer, String> mapa_articulos2 = new HashMap<>();
    TextView contador, titulo;
    View rootView;
    int counter = 0;
    Integer comensales = 0;
    static Map<String, String> info = new HashMap<>();
    private Timer timer = new Timer();
    private final long DELAY1 = 10000; // in ms
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    LinkedList<HashMap> productos_list = new LinkedList();
    private boolean agrupado=false;
    Map<String,Integer>  productos_agrupados = new HashMap<>();
    private Switch switch_agrupar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_menu_articulos_lineas, container, false);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();


        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(rootView.getContext());
        pc = new PersistentCookieStore(getActivity().getApplicationContext());
        prices = (TableLayout) rootView.findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();



        contador = (TextView) rootView.findViewById(R.id.contador);


        titulo = (TextView) rootView.findViewById(R.id.titulo);

        ok_button = (Button) rootView.findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Iterator it = mapa_articulos.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    System.out.println(pair.getKey() + " = " + pair.getKey());

                    mapa_articulos2.put((Integer) pair.getKey(), mapa_articulos.get(pair.getKey()).toString());

                    HashMap tmp = new HashMap();
                    tmp.put("@class", HashMap.class.getName());
                    tmp.put("id", (Integer) pair.getKey());
                    tmp.put("cantidad", mapa_articulos.get(pair.getKey()).getCantidad());
                    tmp.put("counter", mapa_articulos.get(pair.getKey()).getCounter());
                    tmp.put("descuento", 0.0);
                    tmp.put("precio", mapa_articulos.get(pair.getKey()).getPrecioBase());

                    productos_list.add(tmp);
                    lista_art.add(mapa_articulos.get(pair.getKey()));

                    it.remove(); // avoids a ConcurrentModificationException
                }

                System.out.println("\n Enviar lista_art" + lista_art);
                System.out.println("Enviar productos_list " + productos_list);
                System.out.println("Enviar info " + info);
                info.put("counter", String.valueOf(counter));
                Intent i;


                i = new Intent(getActivity().getApplicationContext(), MainActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                lista_art.clear();
                productos_list.clear();
                startActivity(i);
                getActivity().finish();


            }
        });


        switch_agrupar = (Switch) rootView.findViewById(R.id.switch_agrupar);
        switch_agrupar.setVisibility(View.VISIBLE);
        //set the switch to ON
        switch_agrupar.setChecked(false);
        //attach a listener to check for changes in state
        switch_agrupar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    agrupado = true;
                    HttpAsyncTask7 a = new HttpAsyncTask7();
                    a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
                } else {
                    agrupado = false;
                    HttpAsyncTask7 a = new HttpAsyncTask7();
                    a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
                }

            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    private class HttpAsyncTask7 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST7(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("Resultado: " + result);
            convertirDatosArticulo2(result);


        }
    }


    public static String POST7(String url) {


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


    public void convertirDatosArticulo2(String cadena) {


        if(agrupado){

            ObjectMapper mapper = new ObjectMapper();
            try {
                HashMap<String, String> referencia = new HashMap<>();
                List<Object> arrayData1 = mapper.readValue(cadena, List.class);

                productos_agrupados.clear();
                for (int i = 0; i < arrayData1.size(); i++) {
                    List<Object> infopartidas = (List) arrayData1.get(i);
                    List<Object> infoMesa = (List) infopartidas.get(1);
                    Map<String, String> informacion = new HashMap<>();

                    informacion.put("nombre", infoMesa.get(1).toString());
                    informacion.put("cantidad", String.valueOf(((Double) ((ArrayList) infoMesa.get(9)).get(1)).intValue()));
                    informacion.put("estatus", String.valueOf(infoMesa.get(7)));



                    if(productos_agrupados.containsKey(infoMesa.get(1).toString())) {
                        productos_agrupados.put(infoMesa.get(1).toString(), productos_agrupados.get(infoMesa.get(1).toString()).intValue() + ((Double) ((ArrayList) infoMesa.get(9)).get(1)).intValue());
                    }else{
                        productos_agrupados.put(infoMesa.get(1).toString(), ((Double) ((ArrayList) infoMesa.get(9)).get(1)).intValue());


                    }
                    System.out.println("productos_agrupados " +productos_agrupados);

                }




                crearTablaArticulos3();

            } catch (JsonParseException e1) {
                e1.printStackTrace();
            } catch (JsonMappingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }





        }
        else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                HashMap<String, String> referencia = new HashMap<>();
                List<Object> arrayData1 = mapper.readValue(cadena, List.class);
                System.out.println("arrayData1:" + arrayData1);
                lista_art_temporales.clear();
                for (int i = 0; i < arrayData1.size(); i++) {
                    List<Object> infopartidas = (List) arrayData1.get(i);
                    List<Object> infoMesa = (List) infopartidas.get(1);
                    Map<String, String> informacion = new HashMap<>();
                    informacion.put("id_partida", String.valueOf(infoMesa.get(0)));
                    informacion.put("nombre", infoMesa.get(1).toString());
                    informacion.put("referencia", infoMesa.get(2).toString());
                    informacion.put("no_mesa", String.valueOf(infoMesa.get(3)));
                    informacion.put("id_mesa", String.valueOf(infoMesa.get(4)));
                    Integer a = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(0, 2)) * 24 * 60;
                    Integer b = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(2, 4)) * 60;
                    Integer c = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(4, 6));

                    informacion.put("fecha", Integer.toString(a + b + c));
                    informacion.put("orden", String.valueOf(infoMesa.get(6)));
                    informacion.put("estatus", String.valueOf(infoMesa.get(7)));
                    informacion.put("tipodocumento", String.valueOf(infoMesa.get(8)));

                    informacion.put("cantidad", String.valueOf(((Double) ((ArrayList) infoMesa.get(9)).get(1)).intValue()));


                    if (infoMesa.get(2) != null) {

                        if (referencia.containsKey(infoMesa.get(2).toString())) {
                            if (Integer.parseInt(referencia.get(infoMesa.get(2).toString())) > Integer.parseInt(String.valueOf(infoMesa.get(6)))) {
                                referencia.put(infoMesa.get(2).toString(), String.valueOf(infoMesa.get(6)));
                            }

                        } else {
                            referencia.put(infoMesa.get(2).toString(), String.valueOf(infoMesa.get(6)));
                            //lista_art_temporales.add(informacion);
                        }
                    }

                    lista_art_temporales.add(informacion);
                }

                Collections.sort(lista_art_temporales, new Comparator<Map<String, String>>() {
                    public int compare(Map<String, String> one, Map<String, String> two) {
                        Integer a = Integer.parseInt(one.get("fecha"));
                        Integer b = Integer.parseInt(two.get("fecha"));
                        return b.compareTo(a);
                    }
                });


                crearTablaArticulos2();

            } catch (JsonParseException e1) {
                e1.printStackTrace();
            } catch (JsonMappingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }


    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            HttpAsyncTask7 a = new HttpAsyncTask7();
            a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
        }
    }


    public static String POST8(String url) {


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
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


    public void crearTablaArticulos2() {
        if (isAdded()) {
            String[] colors1 = {"#41d9aa", "#496c8c","#60ea79"};
            int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel,R.drawable.tabla_verde2};
            prices.removeAllViews();
            if (lista_art_temporales.isEmpty()) {
                TextView c1 = new TextView(getActivity().getApplicationContext());
                c1.setText("No se encontraron resultados");
                prices.addView(c1);
            } else {

                for (int j = 0; j < lista_art_temporales.size(); j++) {
                    int x = j;


                    LinearLayout layout0 = new LinearLayout(getActivity());
                    layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    layout0.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout layout1 = crearElemento(colors1, x, resources);
                    layout0.addView(layout1);

                    LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    layout2.setOrientation(LinearLayout.VERTICAL);

                    TextView c6 = new TextView(getActivity().getApplicationContext());
                    c6.setText(lista_art_temporales.get(x).get("fecha"));
                    c6.setTextColor(Color.parseColor(colors1[x % 2]));
                    c6.setTextSize(50);
                    c6.setTypeface(null, Typeface.BOLD);
                    c6.setGravity(Gravity.CENTER);


                    TextView c7 = new TextView(getActivity().getApplicationContext());
                    c7.setText("minutos");
                    c7.setTextColor(Color.parseColor(colors1[x % 2]));
                    c7.setTextSize(20);
                    c7.setTypeface(null, Typeface.BOLD);
                    c7.setGravity(Gravity.CENTER);


                    if(Integer.parseInt(lista_art_temporales.get(x).get("fecha"))>300){
                        c6.setTextColor(Color.RED);
                        c7.setTextColor(Color.RED);
                    }

                    layout2.addView(c6);
                    layout2.addView(c7);
                    layout0.addView(layout2);




                    prices.addView(layout0);
                }


            }
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Hello");
                            HttpAsyncTask7 a = new HttpAsyncTask7();
                            a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
                        }
                    },
                    DELAY1
            );
        }
    }



    public void crearTablaArticulos3() {
        if (isAdded()) {
            String[] colors1 = {"#41d9aa", "#496c8c","#60ea79"};
            int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel,R.drawable.tabla_verde2};
            prices.removeAllViews();
            if (productos_agrupados.isEmpty()) {
                TextView c1 = new TextView(getActivity().getApplicationContext());
                c1.setText("No se encontraron resultados");
                prices.addView(c1);
            } else {
                int tmp=0;
                for (Map.Entry<String, Integer> entry : productos_agrupados.entrySet()) {

                    String nombre = entry.getKey();
                    Integer cantidad = entry.getValue();

                    System.out.println("producto: " + productos_agrupados);

                    LinearLayout layout0 = new LinearLayout(getActivity());
                    layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    layout0.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout layout1 = crearElemento2(colors1, tmp, cantidad + " " + nombre, resources);
                    layout0.addView(layout1);

                    LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    layout2.setOrientation(LinearLayout.VERTICAL);




                    layout0.addView(layout2);
                    tmp++;



                    prices.addView(layout0);
                }


            }
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Hello");
                            HttpAsyncTask7 a = new HttpAsyncTask7();
                            a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
                        }
                    },
                    DELAY1
            );
        }
    }

    public LinearLayout crearElemento(String[] colors, final int x, int[] resources) {
        LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if (scale < 2)
            scale = Float.parseFloat("1.8");
        // Toast.makeText(getActivity().getApplicationContext(), String.valueOf(scale), Toast.LENGTH_LONG).show();
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(350 * scale), Math.round(120 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setBackgroundColor(Color.parseColor(colors[x % 2]));
        layout2.setBackgroundResource(resources[x % 2]);



             if (lista_art_temporales.get(x).get("estatus").toString().equals("PROCESADA")) {
                 layout2.setBackgroundColor(Color.parseColor(colors[2]));
                 layout2.setBackgroundResource(resources[2]);

             }


             TextView c2 = new TextView(getActivity().getApplicationContext());
             c2.setText(lista_art_temporales.get(x).get("cantidad") + " " + lista_art_temporales.get(x).get("nombre"));
             c2.setTextColor(Color.WHITE);
             c2.setTextSize(20);
             c2.setTypeface(null, Typeface.BOLD);
             c2.setGravity(Gravity.CENTER);
             layout2.addView(c2);


             TextView c3 = new TextView(getActivity().getApplicationContext());
             c3.setText("R: " + lista_art_temporales.get(x).get("referencia"));
             c3.setTextColor(Color.WHITE);
             c3.setGravity(Gravity.CENTER);
             c3.setTextSize(18);


             layout2.addView(c3);

             layout2.setOnClickListener(new DoubleClickListener() {

                 @Override
                 public void onSingleClick(View v) {
                     Toast.makeText(getActivity().getApplicationContext(), "HOla", Toast.LENGTH_SHORT);
                 }

                 @Override
                 public void onDoubleClick(View v) {
                     HttpAsyncTask8 a = new HttpAsyncTask8();
                     a.execute(server + "/medialuna/spring/documento/cambiarEstatusPedido/" + lista_art_temporales.get(x).get("id_partida") + "/" + lista_art_temporales.get(x).get("tipodocumento"));
                 }
             });




        return layout2;
    }


    public LinearLayout crearElemento2(String[] colors, final int x, String cadena, int[] resources) {
        LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if (scale < 2)
            scale = Float.parseFloat("1.8");
        // Toast.makeText(getActivity().getApplicationContext(), String.valueOf(scale), Toast.LENGTH_LONG).show();
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(350 * scale), Math.round(120 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setBackgroundColor(Color.parseColor(colors[x % 2]));
        layout2.setBackgroundResource(resources[x % 2]);






        TextView c2 = new TextView(getActivity().getApplicationContext());
        c2.setText(cadena);
        c2.setTextColor(Color.WHITE);
        c2.setTextSize(20);
        c2.setTypeface(null, Typeface.BOLD);
        c2.setGravity(Gravity.CENTER);
        layout2.addView(c2);
        System.out.println();

        return layout2;
    }

    public void getData(String message, String title) {
        titulo.setText(title);
        idLinea = message;
        prices.removeAllViews();
        GifImageView imagen = new GifImageView(getActivity().getApplicationContext());
        imagen.setImageResource(R.drawable.loader);
        prices.addView(imagen);
        final Handler mHandler = new Handler();


        // final Handler handler = new Handler();
        //  handler.postDelayed(new Runnable() {
        //      public void run() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("HOLAS");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        HttpAsyncTask7 a = new HttpAsyncTask7();
        a.execute(server + "/medialuna/spring/app/buscarPendientes/" + idLinea);
        // handler.postDelayed(this, 30000); //now is every 2 minutes
        //}
        //     }, 30000); //Every 120000 ms (2 minutes)


    }







    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(v);
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);
        public abstract void onDoubleClick(View v);
    }
}
