package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.Mesa;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MenuMesas extends AppCompatActivity {
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    TableLayout prices;
    int counter = 0;
    Integer contador_int = 0;
    Map<Integer,Mesa> mapa_articulos = new HashMap<>();
    List<Mesa> lista_art_temporales = new ArrayList<Mesa>();
    private String m_Text = "";
    List<Articulo> lista_articulos = new ArrayList<Articulo>();
    LinkedList<HashMap> productos_list = new LinkedList();
    Map<String,String> info = new HashMap<>();
    String comensales;
    String id_mesa;
    private Timer timer = new Timer();
    private final long DELAY1 = 30000; // in ms
    String user="";
    private Switch switch_personal;
    private Boolean personal=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        setContentView(R.layout.activity_menu_mesas);


        prices = (TableLayout) findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Menu Mesas");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMesas.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        user = (String)intent.getSerializableExtra("user");
        System.out.println("user " + user);

        switch_personal = (Switch) findViewById(R.id.switch_personal);
        switch_personal.setVisibility(View.VISIBLE);
        //set the switch to ON
        switch_personal.setChecked(false);
        //attach a listener to check for changes in state
        switch_personal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    personal=true;
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/app/buscarMesas/");
                }else{
                    personal=false;
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/app/buscarMesas/");
                }

            }
        });



        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/app/buscarMesas/");
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }
//daniel_dlb@yahoo.com

        @Override
        protected void onPostExecute(String result) {

           System.out.println("El resultado es: " + result);
            convertirDatosMesa(result);
        }
    }


    public static String POST(String url) {
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


    public void crearTablaArticulos2() {

        // String[] colors1 = {"#0084aa","#c2272d","#333333","#3b3251","#ba2c54","#fed353","#0084aa","#7ca755","#333333","#ef812c","#3e8c64","#c2272d"};

        String[] colors1 = {"#41d9aa","#496c8c","#41d9aa","#496c8c","#41d9aa","#496c8c"};
        int[] resources = {R.drawable.tabla_azul1, R.drawable.tabla_verdel1};

        //Drawable[] estilos = {R.drawable.tabla_azul};

        // prices.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //prices.setOrientation(LinearLayout.HORIZONTAL);

        //                android:endColor="#70dd8e"
        //android:centerColor="#71cdc8"
        // android:startColor="#58caee"


        prices.removeAllViews();
        if (lista_art_temporales.isEmpty()) {
            TextView c1 = new TextView(this.getApplicationContext());
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            int sobrante = lista_art_temporales.size() % 2;

            for (int j = 0; j < lista_art_temporales.size()-sobrante; j+=2) {
                // counter = counter +1;
                int x = j ;
                final int y = j  + 1;
              //  final int xx = j +2;
                //      final int yy = j  + 3;

                LinearLayout layout0 = new LinearLayout(getApplicationContext());
                layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout0.setOrientation(LinearLayout.HORIZONTAL);




                LinearLayout layout1 = crearElemento(colors1, x, resources);
                layout0.addView(layout1);

                LinearLayout layout2 = crearElemento(colors1, y, resources);
                layout0.addView(layout2);



                //LinearLayout layout4 = crearElemento(colors1, yy, resources);
                //layout0.addView(layout4);






                prices.addView(layout0);
            }



           if(sobrante != 0){
                LinearLayout layout90 = new LinearLayout(getApplicationContext());
                layout90.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout90.setOrientation(LinearLayout.HORIZONTAL);

                if(sobrante == 1){
                    LinearLayout layout1 = crearElemento(colors1, lista_art_temporales.size()-sobrante, resources);
                    layout90.addView(layout1);
                }


                prices.addView(layout90);
            }






        }

        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Hello");
                        HttpAsyncTask a = new HttpAsyncTask();
                        a.execute(server + "/medialuna/spring/app/buscarMesas/");
                    }
                },
                DELAY1
        );
    }



    public LinearLayout crearElemento(String[] colors, final int x, int[] resources){
        LinearLayout layout2 = new LinearLayout(getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if(scale<2)
            scale=Float.parseFloat("1.8");
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(160 * scale), Math.round(160 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);

        layout2.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if (lista_art_temporales.get(x).getEstadoMesa()) {
                    alertCliente(lista_art_temporales.get(x).getIndexMesa().toString());
                } else {
                    Intent intent = new Intent(MenuMesas.this, PreComanda.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("idMesa", lista_art_temporales.get(x).getIndexMesa().toString());
                    intent.putExtra("tipo_documento", "pedidocliente");
                    if(lista_art_temporales.get(x).getIdDocumento() !=null)
                        intent.putExtra("idDocumento", lista_art_temporales.get(x).getIdDocumento().toString());
                    else
                        intent.putExtra("idDocumento",0);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();

                }
            }
        });

        TextView c31 = new TextView(getApplicationContext());
        c31.setText("");
        c31.setGravity(Gravity.CENTER);
        c31.setTextColor(Color.WHITE);
        c31.setTypeface(null, Typeface.BOLD);
        c31.setTextSize(25);

        TextView c3 = new TextView(getApplicationContext());
        c3.setText("MESA " + lista_art_temporales.get(x).getIndexMesa().toString());
        c3.setGravity(Gravity.CENTER);
        c3.setTextColor(Color.WHITE);
        c3.setTypeface(null, Typeface.BOLD);
        c3.setTextSize(25);


        layout2.addView(c31);
        layout2.addView(c3);

        if(lista_art_temporales.get(x).getEstadoMesa()){
            layout2.setBackgroundResource(R.drawable.tabla_verde);
        }else {
            layout2.setBackgroundResource(R.drawable.tabla_roja);
            String noComen = "?";
            if(lista_art_temporales.get(x).getNoComensales()!=null)
                noComen=lista_art_temporales.get(x).getNoComensales().toString();
            TextView c32 = new TextView(getApplicationContext());
            c32.setText(noComen);
            c32.setGravity(Gravity.CENTER);
            c32.setTextColor(Color.parseColor("#80FFFFFF"));
            c32.setTypeface(null, Typeface.BOLD);
            c32.setTextSize(20);
            c32.setBackgroundColor(Color.parseColor("#804B4B4B"));
            layout2.addView(c32);

            String userName = "?";
            if(lista_art_temporales.get(x).getUserName()!=null)
                userName=lista_art_temporales.get(x).getUserName().toString();
            TextView c33 = new TextView(getApplicationContext());
            c33.setText(userName);
            c33.setGravity(Gravity.CENTER);
            c33.setTextColor(Color.parseColor("#80FFFFFF"));
            c33.setTypeface(null, Typeface.BOLD);
            c33.setTextSize(13);
            c33.setBackgroundColor(Color.parseColor("#804B4B4B"));
            layout2.addView(c33);


            String cifras = "";
            if(lista_art_temporales.get(x).getCifras()!=null) {
                cifras = "T:" + lista_art_temporales.get(x).getCifras().get("total") + " P:" + lista_art_temporales.get(x).getCifras().get("pendiente") + " L:" + lista_art_temporales.get(x).getCifras().get("procesada") + " S:" + lista_art_temporales.get(x).getCifras().get("surtida");
                if((Integer)lista_art_temporales.get(x).getCifras().get("procesada")>0)
                layout2.setBackgroundResource(R.drawable.tabla_amarilla);
            }
            TextView c34 = new TextView(getApplicationContext());
            c34.setText(cifras);
            c34.setGravity(Gravity.CENTER);
            c34.setTextColor(Color.parseColor("#80FFFFFF"));
            c34.setTypeface(null, Typeface.BOLD);
            c34.setTextSize(10);
            c34.setBackgroundColor(Color.parseColor("#804B4B4B"));
            layout2.addView(c34);


        }

        return layout2;
    }


    public void alertCliente(final String idMesa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.clientes_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Comensales");
        builder.setMessage("Inserte el número de comensales");

        Button busclie_btn_aceptar = (Button) dialogView.findViewById(R.id.busclie_btn_aceptar);
        Button busclie_btn_cancelar = (Button) dialogView.findViewById(R.id.busclie_btn_cancelar);


        final EditText busclie_etx_nom = (EditText) dialogView.findViewById(R.id.busclie_etx_nom);



        final AlertDialog dialog = builder.create();

        busclie_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                info.put("impuestos", "0.0");
                info.put("total", "0.0");
                info.put("no_art", "0");
                info.put("counter", String.valueOf(counter));

                id_mesa = idMesa;
                comensales = busclie_etx_nom.getText().toString();

                HttpAsyncTask2 a = new HttpAsyncTask2();
                a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa.toString() + "/" + comensales);


            }
        });

        busclie_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void convertirDatosMesa(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_art_temporales.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);
            user = (String) arrayData.get(0);
            arrayData.remove(0);
           // List<String> arrayD


            // counter = counter + 1;
            for (int i = 0; i < arrayData.size(); i++) {
               // String mesa = (String) arrayData.get(i);
              //  mesa=mesa.substring(24,mesa.length()-2);
                System.out.println("El resultado Mesa: "+ arrayData.get(i));
                 ArrayList<Object> mesaArray= (ArrayList<Object>) arrayData.get(i);
                ArrayList<Object> datos= (ArrayList<Object>) mesaArray.get(1);
             //   ["[Ljava.lang.Object;",[
                //1,1,true

               // ]]
               /* if(datos.size()>4){
                    Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Boolean)datos.get(2),(Integer)datos.get(3),(Integer)datos.get(4));

                        lista_art_temporales.add(mesa);
                }*/

                System.out.println("datos " + datos.size() + " " + datos);
              /*  if(datos.size()==6){
                    String nombre = null;
                    if(datos.get(5)!=null)
                        nombre= (String)datos.get(5);
                        Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Boolean)datos.get(2),(Integer)datos.get(3),(Integer)datos.get(4),nombre);

                        lista_art_temporales.add(mesa);

                }*/

                if(datos.size()==10){
                    String nombre = "";
                    if(datos.get(5)!=null)
                        nombre= (String)datos.get(5);

                    Map<String,Integer> cifras = new HashMap();
                    cifras.put("total",(Integer)((ArrayList)datos.get(6)).get(1));
                    cifras.put("pendiente",(Integer)((ArrayList)datos.get(7)).get(1));
                    cifras.put("procesada", (Integer) ((ArrayList) datos.get(8)).get(1));
                    cifras.put("surtida",(Integer)((ArrayList)datos.get(9)).get(1));
                    Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Boolean)datos.get(2),(Integer)datos.get(3),(Integer)datos.get(4),nombre,cifras);


                    if(personal){
                        if(nombre.contains(user))
                        lista_art_temporales.add(mesa);

                    } else {

                    lista_art_temporales.add(mesa);
                    }

                }

               /* if(datos.size()==5){
                    String nombre = null;


                    Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Boolean)datos.get(2),(Integer)datos.get(3),(Integer)datos.get(4),nombre);

                    lista_art_temporales.add(mesa);

                }
                else {
                Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Boolean)datos.get(2));
                    lista_art_temporales.add(mesa);
                }*/





            }


            Collections.sort(lista_art_temporales, new Comparator<Mesa>(){
                public int compare(Mesa s1, Mesa s2) {
                    return s1.getIndexMesa().compareTo(s2.getIndexMesa());
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

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("---------- " + id_mesa);
            Intent intent = new Intent(MenuMesas.this, MenuComensales.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
            intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
            intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
            intent.putExtra("tipo_documento", "pedidocliente");
            intent.putExtra("idMesa", id_mesa);
            intent.putExtra("comensales", comensales);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();


        }
    }


    public static String POST2(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");

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
}
