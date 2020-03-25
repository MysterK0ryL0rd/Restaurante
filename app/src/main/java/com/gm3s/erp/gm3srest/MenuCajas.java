package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import android.widget.Toast;

public class MenuCajas extends AppCompatActivity {
        private SharedPreference sharedPreference;
        String server = "";
        private static PersistentCookieStore pc;
        TableLayout prices;
        int counter = 0;
        Map<Integer,Mesa> mapa_articulos = new HashMap<>();
        List<Mesa> lista_art_temporales = new ArrayList<Mesa>();
        private String m_Text = "";
        List<Articulo> lista_articulos = new ArrayList<Articulo>();
        LinkedList<HashMap> productos_list = new LinkedList();
        Map<String,String> info = new HashMap<>();
        String comensales;
        String id_mesa;
    static private String laSerie = "";
    static private String elCliente = "";
    static private String laBodega = "";
    private String elAgente = "";
    private Integer laDireccion = 0;
    Integer laMoneda = 0;
    Integer escaner_folio;
    int x_tmp;
    String referencia="";

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
            toolbar.setSubtitle("Menu Cajas");
            toolbar.setNavigationIcon(R.drawable.arrow_left);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuCajas.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            System.out.println("El servidor es: " + server);
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/app/buscarCajas/");
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
            //if (isAdded()) {
                // String[] colors1 = {"#0084aa","#c2272d","#333333","#3b3251","#ba2c54","#fed353","#0084aa","#7ca755","#333333","#ef812c","#3e8c64","#c2272d"};

                String[] colors1 = {"#41d9aa", "#496c8c", "#60ea79"};
                int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_verde2};

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

                    for (int j = 0; j < lista_art_temporales.size() - sobrante; j += 2) {
                        // counter = counter +1;
                        int x = j;
                        final int y = j + 1;
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


                    if (sobrante != 0) {
                        LinearLayout layout90 = new LinearLayout(getApplicationContext());
                        layout90.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        layout90.setOrientation(LinearLayout.HORIZONTAL);

                        if (sobrante == 1) {
                            LinearLayout layout1 = crearElemento(colors1, lista_art_temporales.size() - sobrante, resources);
                            layout90.addView(layout1);
                        }


                        prices.addView(layout90);
                    }


                }
            }
     //   }


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



                        alertReferenciaGlobal(x);



                }
            });

            TextView c31 = new TextView(getApplicationContext());
            c31.setText("");
            c31.setGravity(Gravity.CENTER);
            c31.setTextColor(Color.WHITE);
            c31.setTypeface(null, Typeface.BOLD);
            c31.setTextSize(25);

            TextView c3 = new TextView(getApplicationContext());
            c3.setText("CAJA " + lista_art_temporales.get(x).getIndexMesa().toString());
            c3.setGravity(Gravity.CENTER);
            c3.setTextColor(Color.WHITE);
            c3.setTypeface(null, Typeface.BOLD);
            c3.setTextSize(25);


            layout2.addView(c31);
            layout2.addView(c3);


                layout2.setBackgroundResource(R.drawable.tabla_verde);


            return layout2;
        }




        public void convertirDatosMesa(String cadena) {

            ObjectMapper mapper = new ObjectMapper();
            try {
                lista_art_temporales.clear();

                List<Object> arrayData = mapper.readValue(cadena, List.class);

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

                        Mesa mesa = new Mesa((Integer)datos.get(0),(Integer)datos.get(1),(Integer)datos.get(2));
                        lista_art_temporales.add(mesa);






                }


                Collections.sort(lista_art_temporales, new Comparator<Mesa>() {
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

            HttpAsyncTask1 a = new HttpAsyncTask1();
            a.execute(server + "/medialuna/spring/listar/serie/contar/remisioncliente/");
        }

        private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {

                return POST2(urls[0]);

            }


            @Override
            protected void onPostExecute(String result) {
                System.out.println("---------- " + id_mesa);
                Intent intent = new Intent(MenuCajas.this, MenuComensales.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
                intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                intent.putExtra("tipo_documento", "remisioncliente");
                intent.putExtra("idMesa", id_mesa);
                intent.putExtra("comensales", comensales);
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

    public boolean comprobarDatos() {

        System.out.println("**** Datos " + laSerie);
        System.out.println("**** Datos " + elCliente);
        System.out.println("**** Datos " + laDireccion);
        System.out.println("**** Datos " + elAgente);

        boolean flag = true;
        if (laSerie.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir la Serie", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if (elCliente.equals("") || laDireccion.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir el Cliente", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if (elAgente.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir el Agente", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return  flag;

    }

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST1(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {


                convertirDatos(result);

        }
    }


    public static String POST1(String url) {
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
        System.out.println("cadena: " + cadena);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> arrayData0 = mapper.readValue(cadena, HashMap.class);




            if(arrayData0.containsKey("serie")) {
                HashMap<String, Object> arrayData = (HashMap<String, Object>) arrayData0.get("serie");
                String idS = arrayData.get("id").toString();
                String nombreS = arrayData.get("nombre").toString();
                laSerie = idS;
                //escaner_txt_serie.setText(nombreS);

                info.put("serie", nombreS);
                info.put("serie_id", laSerie);


                System.out.println("impuesto " + arrayData.get("impuesto"));
                if(arrayData.get("impuesto")!=null){
                    HashMap<String, Object> arrayData1 = (HashMap<String, Object>) arrayData.get("impuesto");
                    info.put("impuesto",  arrayData1.get("impuesto").toString());

                }



                Object cliente1 = (Object) arrayData.get("cliente");
                Object bodega1 = (Object) arrayData.get("bodega");
                HttpAsyncTask6 c = new HttpAsyncTask6();
                c.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/" + "remisioncliente"+"/");

                HashMap<String, Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                HashMap<String, Object> bodega = (HashMap<String, Object>) arrayData.get("bodega");


                if (cliente1 == null) {

                } else {
                    //HashMap<String,Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                    String codigoC = cliente.get("id").toString();
                    String nombreC = cliente.get("nombre").toString();

                    HashMap<String, Object> agente = (HashMap<String, Object>) cliente.get("agente");


                    String codigoA = agente.get("id").toString();
                    String nombreA = agente.get("nombre").toString();

                    System.out.println(" Esto es la informacion del cliente: 1 " + arrayData0.keySet());
                    System.out.println(" Esto es la informacion del cliente: 2 " + arrayData.keySet());
                    System.out.println(" Esto es la informacion del cliente: 3 " + cliente.keySet());
                    System.out.println(" Esto es la informacion del cliente: 4 " + cliente.get("direcciones").toString());
                    System.out.println(" Esto es la informacion del cliente: 5 " + cliente.get("direccion").toString());
                    List<Object> direcciones = (List) cliente.get("direcciones");
                    System.out.println(" Esto es la informacion del cliente: 6 " + cliente.get("direccion").toString());
                    List<HashMap> direccionimpl = (List) direcciones.get(1);
                    System.out.println(" Esto es la informacion del cliente: 7 " + cliente.get("direccion").toString());
                    HashMap direccion_impl_0 = direccionimpl.get(0);
                    System.out.println(" Esto es la informacion del cliente: 8 " + cliente.get("direccion").toString());
                    laDireccion = (Integer) direccion_impl_0.get("id");
                    info.put("idDireccion",laDireccion.toString());
                    System.out.println(" Esto es la informacion del cliente: 9 " + cliente.get("direccion").toString());
                    System.out.println(" Esto es la informacion del cliente: 10 " + laDireccion);
                    elAgente = codigoA;
                    if(elCliente.equals(""))
                        elCliente = codigoC;
//                    escaner_txt_cliente.setText(nombreC);
                    info.put("cliente", nombreC);
                    info.put("cliente_id", elCliente);

                    //escaner_txt_agente.setText(nombreA);
                    info.put("agente", nombreA);
                    info.put("agente_id", codigoA);

                    if (cliente.containsKey("moneda") && cliente.get("moneda") != null) {
                        Map mapa2 = (Map) cliente.get("moneda");
                        laMoneda = (Integer) mapa2.get("id");
                        info.put("lprecio",laMoneda.toString());
                    }
                }

                if(bodega1 == null){}
                else{
                    String codigoB = bodega.get("id").toString();
                    laBodega = codigoB;
                    info.put("bodega",laBodega);
                }


                if (Integer.parseInt(arrayData0.get("total").toString()) >= 1 && laSerie.equals("")) {//==
                    laSerie = idS;
                    //escaner_txt_serie.setText(nombreS);

                    info.put("serie", nombreS);
                    info.put("serie_id", laSerie);

                    HttpAsyncTask6 b = new HttpAsyncTask6();
                    b.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/"+"remisioncliente"+"/");

                }
            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    private class HttpAsyncTask6 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST6(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            escaner_folio = Integer.parseInt(result);
            //    setText(escaner_folio, result);



        }
    }


    public static String POST6(String url) {
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

    public void alertReferenciaGlobal(final int x) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.clientes_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Cliente");
        builder.setMessage("Inserte el nombre del cliente");


        Button busclie_btn_aceptar = (Button) dialogView.findViewById(R.id.busclie_btn_aceptar);
        Button busclie_btn_cancelar = (Button) dialogView.findViewById(R.id.busclie_btn_cancelar);


        final EditText busclie_etx_nom = (EditText) dialogView.findViewById(R.id.busclie_etx_nom);

        busclie_etx_nom.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        ;

        final AlertDialog dialog = builder.create();

        busclie_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id_mesa = lista_art_temporales.get(x).getIndexMesa().toString();
                referencia= busclie_etx_nom.getText().toString();
                x_tmp=x;

                info.put("impuestos", "0.0");
                info.put("total", "0.0");
                info.put("no_art", "0");
                info.put("counter", String.valueOf(counter));


                comensales = "1";

                //HttpAsyncTask2 a = new HttpAsyncTask2();
                // a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + id_mesa + "/" + "1");
                if (comprobarDatos() == true) {

                    info.put("impuestos", "0.0");
                    info.put("total", "0.0");
                    info.put("no_art", "0");
                    info.put("counter", String.valueOf(counter));


                    Intent intent = new Intent(MenuCajas.this, ContenedorFragmentos2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    System.out.println("Aqui esta el pedido : " + productos_list); //Products list es la lista de ids de articulos para el sistema
                    System.out.println("lista_art: " + lista_articulos);//Lista de articulos es la lista

                    intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
                    intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                    intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                    intent.putExtra("tipo_documento", "remisioncliente");
                    intent.putExtra("comensales", comensales.toString());
                    intent.putExtra("idComensal", "M"+id_mesa + "C" + x_tmp);//------------------------------------------------------------------------"
                    intent.putExtra("idMesa", id_mesa.toString());
                    intent.putExtra("esCaja", referencia);
                    startActivity(intent);
                    finish();
                    Toast.makeText(MenuCajas.this, id_mesa + "" + x_tmp, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MenuCajas.this, "Comprobar datos", Toast.LENGTH_SHORT).show();
                }





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




}
