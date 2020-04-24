package com.gm3s.erp.gm3srest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.MainActivity;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PedidosPendientes extends AppCompatActivity {

    private static boolean validacion;
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    static List<Integer> lineas = new ArrayList<>();
    List<HashMap<String, String>> lista_art_temporales = new ArrayList<HashMap<String, String>>();
    TableLayout prices;
    private Timer timer = new Timer();
    private final long DELAY1 = 30000; // in ms
    Button ok_button;
    Boolean menu_tiempos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_pendientes);


        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        pc = new PersistentCookieStore(this);


        prices = (TableLayout) findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();


        ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PedidosPendientes.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        if(intent.getSerializableExtra("menu_tiempos") != null){
            menu_tiempos = Boolean.valueOf((String)intent.getSerializableExtra("menu_tiempos"));
        }


        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/catalogos/1202/registrosPag/");
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            if (validacion) {
                //System.out.println("Resultado : " + result);
                convertirDatos2(result);
            } else {
                Toast.makeText(getApplicationContext(), "Usuario no valido", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String POST(String url) {
        String result = "";
        InputStream inputStream = null;

        LinkedHashMap pagerOrden = new LinkedHashMap();
        pagerOrden.put("@class", LinkedHashMap.class.getName());

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("activos", true);
        entidad.put("maxResults", 100);
        entidad.put("firstResult", 0);
        entidad.put("pagerFiltros", pagerFiltros);
        entidad.put("pagerOrden", pagerOrden);
        String objectStr = JSONValue.toJSONString(entidad);


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

    public void convertirDatos2(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);


            for (int i = 0; i < (arrayData.size()); i++) {
                final Map mapa1 = (Map) arrayData.get(i);
                String idLinea = String.valueOf((Integer) mapa1.get("id"));
                lineas.add(Integer.valueOf(idLinea));

            }


            HttpAsyncTask7 a = new HttpAsyncTask7();
            a.execute(server + "/medialuna/spring/app/buscarPendientesGeneral/");



        } catch (IOException e1) {
            e1.printStackTrace();
        }

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



        HashMap t1 = new HashMap();
        //t1.put("@class", HashMap.class.getName());

        for(Integer lin : lineas){
        t1.put(lin,lin);
        }


        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("info", t1.toString());

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
            System.out.println("7 Lo que se envia : " + objectStr.toString());
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

        ObjectMapper mapper = new ObjectMapper();
        try {

            HashMap<String,String> referencia= new HashMap<>();
            List<Object> arrayData1 = mapper.readValue(cadena, List.class);
            System.out.println("arrayData1:" + arrayData1);
            lista_art_temporales.clear();
            for (int i = 0; i < arrayData1.size(); i++) {
                List<Object> infopartidas = (List) arrayData1.get(i);
                List<Object> infoMesa = (List) infopartidas.get(1);
                HashMap<String, String> informacion = new HashMap<>();
                informacion.put("id_partida", String.valueOf(infoMesa.get(0)));
                informacion.put("nombre", infoMesa.get(1).toString());
                informacion.put("referencia", infoMesa.get(2).toString());
                informacion.put("no_mesa", String.valueOf(infoMesa.get(3)));
                informacion.put("id_mesa", String.valueOf(infoMesa.get(4)));

                Integer a = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(0,2))*24*60;
                Integer b = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(2, 4))*60;
                Integer c = Integer.parseInt(String.valueOf(infoMesa.get(5)).substring(4, 6));

                informacion.put("fecha", Integer.toString(a + b + c));
                informacion.put("orden", String.valueOf(infoMesa.get(6)));

                informacion.put("estatus", String.valueOf(infoMesa.get(7)));
                informacion.put("tipodocumento", String.valueOf(infoMesa.get(8)));

                informacion.put("cantidad", String.valueOf(((Double) ((ArrayList) infoMesa.get(9)).get(1)).intValue()));

                if(infoMesa.get(2)!=null) {
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

            if(menu_tiempos) {
                List<HashMap<String, String>> lista_art_temporales2 = new ArrayList<HashMap<String, String>>();
                for (HashMap lista : lista_art_temporales) {
                    System.out.println(lista.get("orden"));
                    System.out.println(lista.get(referencia.get(lista.get("referencia"))));
                    if (lista.get("orden").equals(referencia.get(lista.get("referencia")))) {
                        lista_art_temporales2.add(lista);
                    }

                }

                lista_art_temporales = lista_art_temporales2;
            }

            Collections.sort(lista_art_temporales, new Comparator<HashMap<String, String>>() {
                public int compare(HashMap<String, String> one, HashMap<String, String> two) {
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

    @SuppressLint("SetTextI18n")
    public void crearTablaArticulos2() {
        String[] colors1 = {"#41d9aa", "#496c8c","#7BDC6F"};
        int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel,R.drawable.tabla_verde2};
        prices.removeAllViews();
        if (lista_art_temporales.isEmpty()) {
            TextView c1 = new TextView(getApplicationContext());
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {


            for (int j = 0; j < lista_art_temporales.size(); j ++) {
                int x = j;


                LinearLayout layout0 = new LinearLayout(getApplicationContext());
                layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout0.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout layout1 = crearElemento(colors1, x, resources);
                layout0.addView(layout1);

                LinearLayout layout2 = new LinearLayout(getApplicationContext());
                layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout2.setOrientation(LinearLayout.VERTICAL);

                TextView c6 = new TextView(getApplicationContext());
                c6.setText(lista_art_temporales.get(x).get("fecha"));
                c6.setTextColor(Color.parseColor(colors1[x % 2]));
                c6.setTextSize(50);
                c6.setTypeface(null, Typeface.BOLD);
                c6.setGravity(Gravity.CENTER);


                TextView c7 = new TextView(getApplicationContext());
                c7.setText("minutos");
                c7.setTextColor(Color.parseColor(colors1[x % 2]));
                c7.setTextSize(20);
                c7.setTypeface(null, Typeface.BOLD);
                c7.setGravity(Gravity.CENTER);

                TextView c8 = new TextView(getApplicationContext());
                String refextra = getIntent().getStringExtra("refextra");
                c8.setText("Referencia Extra " + refextra);
                c8.setTextColor(Color.parseColor(colors1[x % 2]));
                c8.setTextSize(20);
                c8.setTypeface(null, Typeface.BOLD);
                c8.setGravity(Gravity.CENTER);




                if(Integer.parseInt(lista_art_temporales.get(x).get("fecha"))>300){
                    c6.setTextColor(Color.RED);
                    c7.setTextColor(Color.RED);
                    c8.setTextColor(Color.RED);
                }

                layout2.addView(c6);
                layout2.addView(c7);
                layout2.addView(c8);
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
                        a.execute(server + "/medialuna/spring/app/buscarPendientesGeneral/");
                    }
                },
                DELAY1
        );
    }

    public LinearLayout crearElemento(String[] colors, final int x, int[] resources) {
        LinearLayout layout2 = new LinearLayout(getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if (scale < 2)
            scale = Float.parseFloat("1.8");
        // Toast.makeText(getActivity().getApplicationContext(), String.valueOf(scale), Toast.LENGTH_LONG).show();
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(275 * scale), Math.round(130 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setBackgroundColor(Color.parseColor(colors[x % 2]));
        layout2.setBackgroundResource(resources[x % 2]);


        if(lista_art_temporales.get(x).get("estatus").toString().equals("PROCESADA")){
            layout2.setBackgroundColor(Color.parseColor(colors[2]));
            layout2.setBackgroundResource(resources[2]);

        }

        else{
            layout2.setBackgroundColor(Color.parseColor(colors[1]));
            layout2.setBackgroundResource(resources[1]);

        }

        TextView c2 = new TextView(getApplicationContext());
        c2.setText(lista_art_temporales.get(x).get("cantidad")+ " " +lista_art_temporales.get(x).get("nombre"));
        c2.setTextColor(Color.WHITE);
        c2.setTextSize(25);
        c2.setTypeface(null, Typeface.BOLD);
        c2.setGravity(Gravity.CENTER);

        TextView c3 = new TextView(getApplicationContext());
        String[] temp = lista_art_temporales.get(x).get("referencia").split("\\|");
        c3.setText("Ref: " + temp[0]);
        c3.setTextColor(Color.WHITE);
        c3.setGravity(Gravity.CENTER);
        c3.setTextSize(17);




        layout2.addView(c2);
        layout2.addView(c3);


        layout2.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {
                Toast.makeText(getApplicationContext(),"HOla",Toast.LENGTH_SHORT);
            }

            @Override
            public void onDoubleClick(View v) {
                HttpAsyncTask8 a = new HttpAsyncTask8();
                a.execute(server + "/medialuna/spring/documento/cambiarEstatusPedido/" + lista_art_temporales.get(x).get("id_partida")+"/"+lista_art_temporales.get(x).get("tipodocumento"));
            }
        });

        return layout2;
    }


    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            HttpAsyncTask7 a = new HttpAsyncTask7();
            a.execute(server + "/medialuna/spring/app/buscarPendientesGeneral/");
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
