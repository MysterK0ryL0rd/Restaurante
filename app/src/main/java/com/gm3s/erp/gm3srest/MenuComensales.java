package com.gm3s.erp.gm3srest;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MenuComensales extends AppCompatActivity {

    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    TableLayout prices;
    int counter = 0;
    Integer contador_int = 0;
    Map<Integer,Articulo> mapa_articulos = new HashMap<>();
    List<Articulo> lista_art_temporales = new ArrayList<Articulo>();
    private String m_Text = "";
    String user;

    String tipo_documento = "pedidocliente";
    private final long DELAY3 = 1000; // in ms
    private final long DELAY1 = 1000; // in ms
    private final long DELAY = 2000; // in ms
    private LinearLayout mButton;
    TableLayout codigos;
    int selected = 0;
    String id = "";
    int temp;
    Map<Integer,String> mapa_articulos2 = new HashMap<>();

    static String barcode = "";
    String path = "";
    File file_tmp;

    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Articulo> lista_articulos = new ArrayList<Articulo>();
    private Timer timer = new Timer();
    LinkedList<HashMap> productos_list = new LinkedList();
    private TextView subtotal;
    private TextView impuestos;

    List<Articulo> lista_art2 = new ArrayList<Articulo>();
    List<String> nombre_cliente = new ArrayList<String>();
    List<Integer> direccion_cliente = new ArrayList<Integer>();
    List<String> rfc_cliente = new ArrayList<String>();
    List<Integer> id_cliente = new ArrayList<Integer>();
    List<String> nombre_serie = new ArrayList<String>();
    List<String> id_serie = new ArrayList<String>();
    List<String> nombre_agente = new ArrayList<String>();
    List<String> id_agente = new ArrayList<String>();
    List<Integer> id_bodega = new ArrayList<Integer>();
    List<Integer> id_moneda = new ArrayList<Integer>();




    TextView total;
    Integer escaner_folio;
    TextView escaner_total, escaner_fecha;
    private static boolean validacion;
    //TextView escaner_txt_serie, escaner_txt_agente;
    static TextView escaner_txt_cliente;
    static private String laSerie = "";
    static private String laSerieNombre = "";
    static private String elCliente = "";
    static private String laBodega = "";
    private String elAgente = "";
    private Integer laDireccion = 0;
    Button escaner_btn_cliente,btn_nuevo_cli;
    static List<String> tmp = new ArrayList<>();
    Button btn_finalizar, btn_regresar;
    static EditText corto, nombre, sku;
    LinkedList<HashMap> formasPago_tmp = new LinkedList();
    List<HashMap> formasPago = new ArrayList<HashMap>();
    static boolean editar_precio_permiso  = false;
    static boolean editar_descuento_permiso  = false;
    Integer laMoneda = 0;
    Integer comensales = 0;
    Integer idMesa;
    Map<String,String> info = new HashMap<>();
    Boolean esCaja;
    Double total_double = 0.0;
    String total_tmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        setContentView(R.layout.activity_menu_comensales);

        Intent intent = getIntent();
        comensales = Integer.parseInt((String)intent.getSerializableExtra("comensales"));
        idMesa = Integer.parseInt((String)intent.getSerializableExtra("idMesa"));
        user = (String)intent.getSerializableExtra("user");

        if(getIntent().getSerializableExtra("idMesa") != null){
            idMesa = Integer.parseInt((String)intent.getSerializableExtra("idMesa"));
            HttpAsyncTask2 a = new HttpAsyncTask2();
        }

        if(intent.getSerializableExtra("esCaja")!=null) {
            esCaja = Boolean.getBoolean(intent.getSerializableExtra("esCaja").toString());
        }

        String PedidoMapa = (String) getIntent().getSerializableExtra("PedidoMapa");
        String InfoMapa = (String) getIntent().getSerializableExtra("InfoMapa");
        String PedidoArt = (String) getIntent().getSerializableExtra("PedidoArt");


        convertirMapa(PedidoMapa);
        convertirMapa2(InfoMapa);
        convertirMapa3(PedidoArt);

        if(getIntent().getSerializableExtra("idCliente") != null){
            elCliente =(String)intent.getSerializableExtra("idCliente");
            info.put("cliente_id", elCliente);
        }


        prices = (TableLayout) findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Menu Comensales");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask21 a = new HttpAsyncTask21();
                a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa.toString() + "/0");
            }
        });

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/listar/serie/contar/" + tipo_documento + "/");




        /*HttpAsyncTask13 c = new HttpAsyncTask13();
        c.execute(server + "/medialuna/spring/editorUsuario/permisos/usuario/app");*/


        crearTablaArticulos2();

        System.out.println("El servidor es: " + server);

        btn_finalizar = (Button) findViewById(R.id.btn_finalizar);
        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("List convertirMapa 514 b1" + productos_list);
                //      HttpAsyncTask2 a = new HttpAsyncTask2();
//                a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa.toString());


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
                    tmp.put("idC", mapa_articulos.get(pair.getKey()).getPrecioBase());

                    productos_list.add(tmp);
                    lista_art.add(mapa_articulos.get(pair.getKey()));

                    it.remove(); // avoids a ConcurrentModificationException
                }
                System.out.println("List convertirMapa 514 b2" + productos_list);
                System.out.println("\n Enviar lista_art" + lista_art);
                System.out.println("Enviar productos_list " + productos_list);
                System.out.println("Enviar info " + info);
                info.put("counter", String.valueOf(counter));



                Toast.makeText(getApplicationContext(), tipo_documento, Toast.LENGTH_LONG).show();
                if (laSerie.equals("")) {
                    Toast.makeText(getApplicationContext(), "Favor de elegir Serie ", Toast.LENGTH_LONG).show();

                } else if (elCliente.equals("") || elAgente.equals("")) {
                    Toast.makeText(getApplicationContext(), "Favor de elegir Cliente/Agente ", Toast.LENGTH_LONG).show();

                } else {
                    System.out.println("List convertirMapa 514 b3" + productos_list);

                    HttpAsyncTask12 b = new HttpAsyncTask12();
                    b.execute(server + "/medialuna/spring/listar/catalogo/1403/");
                }


            }
        });


        btn_regresar = (Button) findViewById(R.id.btn_regresar);
        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask21 a = new HttpAsyncTask21();
                a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa.toString() + "/0");
            }
        });

        btn_nuevo_cli = (Button) findViewById(R.id.btn_nuevo_cli);
        btn_nuevo_cli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NuevoCliente.class);
                intent.putExtra("comensales", comensales);
                intent.putExtra("idMesa", idMesa);
                intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
                intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                intent.putExtra("tipo_documento", tipo_documento);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();


            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HttpAsyncTask21 a = new HttpAsyncTask21();
        a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa.toString() + "/0");
    }

    public void crearTablaArticulos2() {

        String[] colors1 = {"#41d9aa","#496c8c","#41d9aa","#496c8c","#41d9aa","#496c8c"};
        int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_verdel, R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_azul};


        prices.removeAllViews();

            int sobrante = comensales % 2;

            for (int j = 0; j < comensales-sobrante; j+=2) {
                // counter = counter +1;
                int x = j ;
                final int y = j  + 1;

                LinearLayout layout0 = new LinearLayout(getApplicationContext());
                layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout0.setOrientation(LinearLayout.HORIZONTAL);


                LinearLayout layout1 = crearElemento(colors1, x, resources);
                layout0.addView(layout1);

                LinearLayout layout2 = crearElemento(colors1, y, resources);
                layout0.addView(layout2);


                prices.addView(layout0);
            }



            if(sobrante != 0){
                LinearLayout layout90 = new LinearLayout(getApplicationContext());
                layout90.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout90.setOrientation(LinearLayout.HORIZONTAL);

                if(sobrante == 1){
                    LinearLayout layout1 = crearElemento(colors1, comensales-sobrante, resources);
                    layout90.addView(layout1);
                }

                prices.addView(layout90);
            }







    }


    public LinearLayout crearElemento(String[] colors, final int x, int[] resources){
        LinearLayout layout2 = new LinearLayout(getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if(scale<2)
            scale=Float.parseFloat("1.8");
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(160 * scale), Math.round(160 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setBackgroundColor(Color.parseColor(colors[x % 6]));
        layout2.setBackgroundResource(resources[x % 6]);

        System.out.println("productos_list" + JSONValue.toJSONString(productos_list));
        final ImageView c1 = new ImageView(getApplicationContext());
        if((x%2)==0){
            c1.setImageResource(R.drawable.comen1);
            if(JSONValue.toJSONString(productos_list).contains(" C:"+(x+1))){
                c1.setImageResource(R.drawable.comen4);
            }
        }else {
            c1.setImageResource(R.drawable.comen2);
            if(JSONValue.toJSONString(productos_list).contains(" C:"+(x+1))){
                c1.setImageResource(R.drawable.comen3);
            }
        }



        c1.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                if (comprobarDatos() == true) {


                    info.put("impuestos", "0.0");
                    info.put("total", "0.0");
                    info.put("no_art", "0");
                    info.put("counter", String.valueOf(counter));


                    Intent intent = new Intent(MenuComensales.this, ContenedorFragmentos2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    System.out.println("Aqui esta el pedido : " + productos_list); //Products list es la lista de ids de articulos para el sistema
                    System.out.println("lista_art: " + lista_articulos);//Lista de articulos es la lista
                    intent.putExtra("user", user);
                    intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
                    intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                    intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                    intent.putExtra("tipo_documento", tipo_documento);
                    intent.putExtra("comensales", comensales.toString());
                    intent.putExtra("idComensal", "M:" + idMesa + " C:" + (x+1));//------------------------------------------------------------------------"
                    intent.putExtra("idMesa", idMesa.toString());
                    startActivity(intent);
                    finish();
                    Toast.makeText(MenuComensales.this, idMesa + "" + x, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MenuComensales.this, "Comprobar datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView c3 = new TextView(getApplicationContext());
        c3.setText("Comensal No.: " + x + 1);
        c3.setGravity(Gravity.CENTER);
        c3.setTextColor(Color.WHITE);








        layout2.addView(c1);
        layout2.addView(c3);


        return layout2;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            if (validacion) {
                convertirDatos(result);
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
            Map<String, Object> arrayData0 = mapper.readValue(cadena, HashMap.class);




            if(arrayData0.containsKey("serie")) {
                HashMap<String, Object> arrayData = (HashMap<String, Object>) arrayData0.get("serie");
                String idS = arrayData.get("id").toString();
                laSerieNombre = arrayData.get("nombre").toString();
                laSerie = idS;
                //escaner_txt_serie.setText(nombreS);

                info.put("serie", laSerieNombre);
                info.put("serie_id", laSerie);

                System.out.println("impuesto " + arrayData.get("impuesto"));
                if(arrayData.get("impuesto")!=null){
                    HashMap<String, Object> arrayData1 = (HashMap<String, Object>) arrayData.get("impuesto");
                    info.put("impuesto",  arrayData1.get("impuesto").toString());

                }

                Object cliente1 = (Object) arrayData.get("cliente");
                Object bodega1 = (Object) arrayData.get("bodega");
                HttpAsyncTask6 c = new HttpAsyncTask6();
                c.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/" + tipo_documento+"/");

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
                        System.out.println("info si tiene moneda" + laMoneda);
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

                    info.put("serie", laSerieNombre);
                    info.put("serie_id", laSerie);

                    HttpAsyncTask6 b = new HttpAsyncTask6();
                    b.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/" + tipo_documento + "/");



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


    private class HttpAsyncTask12 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST12(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            //  Toast.makeText(getApplicationContext(), "Resultado 12 : " + result, Toast.LENGTH_LONG).show();
            convertirDatosFormasPago(result);
        }
    }

    public String POST12(String url) {
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

                    // createExternalStoragePrivateFile(id, "pdf", inputStream);
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


    private class HttpAsyncTask13 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST13(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getApplicationContext(), "Estado de envio: " + result, Toast.LENGTH_LONG).show();
            convertirDatosPermiso(result);
        }
    }

    public String POST13(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {

                    // createExternalStoragePrivateFile(id, "pdf", inputStream);
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

    public void convertirDatosFormasPago(String cadena) {
        System.out.println("-----> " + "Formas Pago: " + cadena);
        ObjectMapper mapper = new ObjectMapper();
        try {
            formasPago.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);


            for (int i = 0; i < arrayData.size(); i++) {
                HashMap mapa1 = new HashMap();
                mapa1.put("id",(Integer) ((HashMap) arrayData.get(i)).get("id"));
                mapa1.put("nombre", (String) ((HashMap) arrayData.get(i)).get("nombre"));
                mapa1.put("credito", (Boolean) ((HashMap) arrayData.get(i)).get("crédito"));
                mapa1.put("@class", HashMap.class.getName());
                formasPago.add(mapa1);
                if(mapa1.get("nombre").toString().toUpperCase().contains("EFECTIVO")){
                    formasPago_tmp.add(mapa1);
                }
            }
            System.out.println("-----> " + "Mapa: " + Arrays.asList(formasPago));
            if (verificarDatosPedido()){
                HttpAsyncTask8 a = new HttpAsyncTask8();
                a.execute(server + "/medialuna/spring/documento/crear/app");
            }else
                Toast.makeText(getApplicationContext(), "No existe orden para comensal", Toast.LENGTH_LONG).show();


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public boolean verificarDatosPedido() {
        boolean validar = false;
        if (productos_list.size()>0)
            validar = true;
        return validar;
    }




    public void convertirDatosPermiso(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Boolean> arrayData = mapper.readValue(cadena, HashMap.class);
            if(arrayData.containsKey("general.modDescuentoPartida")){
                editar_descuento_permiso = arrayData.get("general.modDescuentoPartida");
            }
            if(arrayData.containsKey("general.modPrecio")){
                editar_precio_permiso = arrayData.get("general.modPrecio");

            }


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void cerrarCuenta(){

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
        Intent i = new Intent(getApplicationContext(), Pedido.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("PedidoArt", JSONValue.toJSONString(lista_art));
        i.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
        i.putExtra("InfoMapa", JSONValue.toJSONString(info));
        i.putExtra("tipo_documento", tipo_documento);
        lista_art.clear();
        productos_list.clear();
        startActivity(i);
        finish();
    }

    public void convertirMapa(String cadena){
        System.out.println(" convertirMapa 514 a" + cadena);
        System.out.println(cadena);

        ObjectMapper mapper = new ObjectMapper();
        try {
            productos_list.clear();

            // mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            productos_list = mapper.readValue(cadena, LinkedList.class);





            System.out.println("List convertirMapa 514 b" + productos_list);




          /*  if(arrayData.size()>0) {

                System.out.println(" convertirMapa 535");

                Iterator it = arrayData.entrySet().iterator();

                while (it.hasNext()) {
                    System.out.println(" convertirMapa 540");
                    Map.Entry pair = (Map.Entry) it.next();
                    System.out.println(arrayData.get(pair.getKey()));


                    String[] pairs = arrayData.get(pair.getKey()).split(",");
                    for(int i=0; i<pairs.length; i++){

                        String[] pairs2 = pairs[i].split("=");
                        pairs[i] = pairs2[1];
                    }

                    Articulo elarticulo = new Articulo(Integer.parseInt(pairs[0]),pairs[1], Double.parseDouble(pairs[2]), pairs[3], pairs[4], Double.parseDouble(pairs[5]), Double.parseDouble(pairs[7]), Integer.parseInt(pairs[8]), Integer.parseInt(pairs[10]));
                    System.out.println("El ar Put");
                    mapa_articulos2.put(Integer.parseInt(pair.getKey().toString()), elarticulo.toString());
                    it.remove();
                }



            }*/
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    public void convertirMapa2(String cadena) {


        info.put("f_pago", "");
        info.put("f_pago_id", "");


        info.put("folio", "");


        System.out.println(" La cadena de convertirMapa 21 : " + cadena);





        ObjectMapper mapper = new ObjectMapper();
        try {

            info = mapper.readValue(cadena, HashMap.class);
            counter = Integer.parseInt(info.get("counter").toString());



        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void convertirMapa3(String cadena){
        System.out.println("La cadena: " + cadena);

        if (cadena.equals("[]") || cadena.equals("")) {

        }
        else {



           // lista_art.clear();
            cadena = cadena.substring(1, cadena.length() - 1);

            String[] pairs = cadena.split(",id"); //Division por articulo

            if(pairs.length>=1){
                for (int i=1; i<pairs.length;i++){
                    pairs[i] = "id" + pairs[i];
                }
            }


            for (int i = 0; i < pairs.length; i++) {
                String[] pairs2 = pairs[i].split(", "); //Division por atributo

                Map<String, String> articulo_map = new HashMap<>();

                for (int j = 0; j < pairs2.length; j++) {
                    String[] pairs3 = pairs2[j].split("=");
                    if(pairs3.length > 1)
                        articulo_map.put(pairs3[0], pairs3[1]);
                }

                System.out.println( " $$$$$$$$$444" + articulo_map);


                Articulo nuevo = new Articulo(Integer.parseInt(articulo_map.get("id").toString()), articulo_map.get("descripcion").toString(), Double.parseDouble(articulo_map.get("existencia").toString()), articulo_map.get("nombre").toString(), articulo_map.get("nombreCorto"), Double.parseDouble(articulo_map.get("precioBase").toString()), Double.parseDouble(articulo_map.get("impuesto").toString()), Integer.parseInt(articulo_map.get("counter").toString()), Integer.parseInt(articulo_map.get("cantidad").toString()));
                lista_art.add(nuevo);
            }

            System.out.println("\n Enviar recibir lista_art " + lista_art);

            //System.out.println("List convertirMapa3 2" + helper);
            //   onCheckboxClicked20(lista_art);

                /*  if(arrayData.size()>0) {

                System.out.println(" convertirMapa 535");

                Iterator it = arrayData.entrySet().iterator();

                while (it.hasNext()) {
                    System.out.println(" convertirMapa 540");
                    Map.Entry pair = (Map.Entry) it.next();
                    System.out.println(arrayData.get(pair.getKey()));


                    String[] pairs = arrayData.get(pair.getKey()).split(",");
                    for(int i=0; i<pairs.length; i++){

                        String[] pairs2 = pairs[i].split("=");
                        pairs[i] = pairs2[1];
                    }

                    Articulo elarticulo = new Articulo(Integer.parseInt(pairs[0]),pairs[1], Double.parseDouble(pairs[2]), pairs[3], pairs[4], Double.parseDouble(pairs[5]), Double.parseDouble(pairs[7]), Integer.parseInt(pairs[8]), Integer.parseInt(pairs[10]));
                    System.out.println("El ar Put");
                    mapa_articulos2.put(Integer.parseInt(pair.getKey().toString()), elarticulo.toString());
                    it.remove();
                }



            }*/

        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }
//daniel_dlb@yahoo.com

        @Override
        protected void onPostExecute(String result) {


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

    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Documento creado" + result, Toast.LENGTH_LONG).show();

            //  Intent intent = getIntent();
            //  finish();
            //  startActivity(intent);

            id = convertirDatosDocumento(result);
            System.out.println("Por aqui va el id: " + id);



            HttpAsyncTask14 a = new HttpAsyncTask14();
            a.execute(server + "/medialuna/spring/app/asignarReferencia/" + idMesa.toString()+"/"+id);

            //documentoPopUp(id);
          //  alertRemisionCreada();

        }
    }


    public String POST8(String url)  {
        JSONArray array = new JSONArray();
        ArrayList tmp = new ArrayList();
        for (int i = 0; i < productos_list.size(); i++) {
            JSONObject jsonOBJECT2 = new JSONObject(productos_list.get(i));
            tmp.add(jsonOBJECT2);
            array.put(jsonOBJECT2);
        }

        Double total_subtotal = 0.0;
        for(int i = 0; i< productos_list.size(); i++){
            total_subtotal += Double.parseDouble(productos_list.get(i).get("cantidad").toString())*Double.parseDouble(productos_list.get(i).get("precio").toString()) ;
            /*if(productos_list.get(i).containsKey("extra")){
                if (productos_list.get(i).get("extra") != null) {
                    String refextra = getIntent().getExtras().getString("extra");
                    productos_list.get(i).put("idC", productos_list.get(i).get("idC") + " F:" + escaner_folio.toString() + " SF:" + laSerieNombre + "|" + productos_list.get(i).get("extra").toString() + "|" + refextra);
                    productos_list.get(i).remove("extra");
                }
                else{
                    String refextra = getIntent().getExtras().getString("extra");
                    productos_list.get(i).put("idC","PENDIENTE|"+productos_list.get(i).get("idC")+" F:"+escaner_folio.toString()+" SF:"+laSerieNombre+ "|" + refextra);

                }
            }else{
                String refextra = getIntent().getExtras().getString("extra");
                productos_list.get(i).put("idC","PENDIENTE|"+productos_list.get(i).get("idC")+" F:"+escaner_folio.toString()+" SF:"+laSerieNombre+ "|" + refextra);

            }*/

            if(productos_list.get(i).get("extra") != null){
                productos_list.get(i).put("idC","PENDIENTE|"+productos_list.get(i).get("idC") + " F:" + escaner_folio.toString() + " SF:" + laSerieNombre + "|" + productos_list.get(i).get("extra").toString());
                productos_list.get(i).remove("extra");
            }else {
                productos_list.get(i).put("idC", "PENDIENTE|"+productos_list.get(i).get("idC") + " F:" + escaner_folio.toString() + " SF:" + laSerieNombre);
                productos_list.get(i).remove("extra");
            }
        }


        HashMap t0 = new HashMap();
        t0.put("producto", productos_list);
        t0.put("@class", ArrayList.class.getName());



        HashMap t1 = new HashMap();
        t1.put("@class", HashMap.class.getName());
        t1.put("id0",total_subtotal.toString());
        t1.put("id1","0.0");
        t1.put("id2","0.0");
        t1.put("id3","0.0");
        // totales.add(t4);
        // HashMap t5 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id4",total_subtotal.toString());
        //  totales.add(t5);
        // HashMap t6 = new HashMap();
        //t1.put("@class", HashMap.class.getName());
        t1.put("id5","0.0");
        //   totales.add(t6);
        // HashMap t7 = new HashMap();
        //t1.put("@class", HashMap.class.getName());
        t1.put("id6","0.0");
        //  totales.add(t7);
        // HashMap t8 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id7",total_subtotal.toString());
        //   totales.add(t8);
        // HashMap t9 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id8","0.0");
        // totales.add(t9);
        // HashMap t10 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id9","0.0");
        // totales.add(t10);
        //HashMap t11 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id10","0.0");
        //totales.add(t11);
        //  HashMap t12 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id11","0.0");
        //totales.add(t12);
        //HashMap t13 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id12", total_subtotal.toString());
        //totales.add(t13);

        //ArrayList totales_tmp = new ArrayList();
        //for (int i = 0; i < totales.size(); i++) {
        //    JSONObject jsonOBJECT2 = new JSONObject(totales.get(i));
        //    totales_tmp.add(jsonOBJECT2);
        // }

        if(formasPago_tmp.size()==1){
            formasPago_tmp.get(0).put("valor", total_subtotal.toString());
        }
        HashMap map2 = new HashMap();
        map2.put("id_direccion", laDireccion);
        map2.put("folio", escaner_folio.toString());
        map2.put("tercero", elCliente);  //id Tercero
        map2.put("agente", Integer.parseInt(elAgente));
        map2.put("categoria", laSerie);
        map2.put("serie", laSerie);
        map2.put("entidad", buscarEntidad(tipo_documento));
        map2.put("moneda", laMoneda);
        map2.put("@class", HashMap.class.getName());

        HashMap map3 = new HashMap();
        map3.put("@class", ArrayList.class.getName());
        map3.put("formasPago", formasPago_tmp);


        Map totalisimo = new HashMap();
        totalisimo.put("map3",map3.toString());
        totalisimo.put("map2",map2.toString());
        totalisimo.put("t1",t1.toString());
        totalisimo.put("t0",t0.toString());
        totalisimo.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(totalisimo);
        //  JSONObject jsonOBJECT2 = new JSONObject(t1);
        //  JSONObject jsonOBJECT3 = new JSONObject(t0);
        // try {
        //      jsonOBJECT3.put("producto", array);
        //      jsonOBJECT3.put("@class", HashMap.class.getName());
        //  } catch (JSONException e) {
        //    e.printStackTrace();
        //  }

        System.out.println("Lo que se envia0: " + jsonOBJECT1);
        System.out.println("Lo que se envia1: " + jsonOBJECT1.toString());

        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");

            StringEntity params = new StringEntity(jsonOBJECT1.toString());
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);

            //StringEntity params2 = new StringEntity(jsonOBJECT2.toString());
            // params2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //  httppost.setEntity(params2);

            //StringEntity params3 = new StringEntity(jsonOBJECT3.toString());
            //params3.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            // httppost.setEntity(params3);

            System.out.println("Lo que se envia: " + jsonOBJECT1.toString());
            //  System.out.println("Lo que se envia: " + jsonOBJECT2.toString());
            //   System.out.println("Lo que se envia: " + jsonOBJECT3.toString());
            System.out.println("Lo que se envia: " + params.toString());

            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                    System.out.println("Y por aqui el result: " + result);
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

    public boolean comprobarDatosMenu(){
        boolean valor = false;
        if(productos_list.size()>0)
            valor = true;
        return valor;
    }

    public int buscarEntidad(String nombre_entidad){
        if(nombre_entidad.equals("remisioncliente")){
            return 0;
        }
        if(nombre_entidad.equals("facturacliente")){
            return 1;
        }
        if(nombre_entidad.equals("honorarioscliente")){
            return 2;
        }
        if(nombre_entidad.equals("arrendamientocliente")){
            return 3;
        }
        if(nombre_entidad.equals("pedidocliente")){
            return 4;
        }
        else {
            return  0;
        }
    }

    public String convertirDatosDocumento(String cadena) {
        String id = "";
        ObjectMapper mapper = new ObjectMapper();
        try {

            System.out.println("arrayData1" + cadena);
            HashMap arrayData = mapper.readValue(cadena, HashMap.class);
            System.out.println("arrayData" + arrayData);
            id = String.valueOf((Integer) arrayData.get("id"));

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return id;
    }


    private class HttpAsyncTask14 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST14(urls[0]);

        }
//daniel_dlb@yahoo.com

        @Override
        protected void onPostExecute(String result) {

            lista_art.clear();
            productos_list.clear();
            Intent intent = new Intent(getApplicationContext(),MenuMesas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("user", user);
            getApplicationContext().startActivity(intent);
            finish();

        }
    }


    public static String POST14(String url) {
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

    private class HttpAsyncTask21 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST21(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(MenuComensales.this, MenuMesas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();


        }
    }


    public static String POST21(String url) {
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

}
