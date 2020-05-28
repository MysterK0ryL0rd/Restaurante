package com.gm3s.erp.gm3srest;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MenuArticulosPrincipal extends Fragment {
    String tipo_documento = "remisioncliente", id_doc= "",idComensal = "";
    static String idLinea;
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    private static boolean validacion;
    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Articulo> lista_art_temporales = new ArrayList<Articulo>();
    TableLayout prices;
    Button ok_button;
    Map<Integer,Articulo> mapa_articulos = new HashMap<>();
    Integer contador_int = 0;
    Map<Integer,String> mapa_articulos2 = new HashMap<>();
    TextView contador, titulo;
    View rootView;
    int counter = 0;
    Integer comensales = 0,  idMesa = 0;
    static Map<String,String> info = new HashMap<>();
    Boolean esCaja= false;
    List<HashMap> formasPago = new ArrayList<HashMap>();
    LinkedList<HashMap> formasPago_tmp = new LinkedList();
    LinkedList<HashMap> productos_list = new LinkedList();
    Integer laMoneda = 0;
    private Integer laDireccion = 0;
    static private String laSerie = "";
    Integer escaner_folio;
    private String elAgente = "";
    static private String elCliente = "";
    String id = "";
    String referenciaGlobal="";
    Integer id_tmp =1;
    String laSerieNombre;
    String user="";
    Map<String,Map> listaArticulos = new HashMap<>();
    Double impuesto=0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.menu_articulos_principal, container, false);
        return rootView;
    }







    @Override
    public void onStart(){
        super.onStart();


        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(rootView.getContext());
        pc = new PersistentCookieStore(getActivity().getApplicationContext());
        prices = (TableLayout) rootView.findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();

        contador = (TextView) rootView.findViewById(R.id.contador);
        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/listar/serie/contar/" + tipo_documento + "/");

        titulo = (TextView) rootView.findViewById(R.id.titulo);

        ok_button = (Button) rootView.findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            }

        }
        );





    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



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
                String nombreS = arrayData.get("nombre").toString();
                laSerie = idS;
                //escaner_txt_serie.setText(nombreS);

                info.put("serie", nombreS);
                info.put("serie_id", laSerie);
                Object bodega1 = (Object) arrayData.get("bodega");

                HashMap<String, Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                HashMap<String, Object> bodega = (HashMap<String, Object>) arrayData.get("bodega");
                String codigoC = cliente.get("id").toString();
                String nombreC = cliente.get("nombre").toString();
                info.put("cliente", nombreC);
                info.put("cliente_id", codigoC);

                if(bodega1 == null){

                }
                else{
                    String codigoB = bodega.get("id").toString();
                    String laBodega = codigoB;
                    info.put("bodega",laBodega);
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


        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        entidad.put("conExistencia", false);
        ArrayList tipo0 = new ArrayList();
        ArrayList tipo = new ArrayList();
        tipo.add("COMPRA_VENTA");
        tipo.add("SERVICIO");
        tipo.add("MATERIA_PRIMA");
        tipo.add("CONSUMO_INTERNO");
        tipo.add("TELA");
        tipo.add("PRODUCCION");
        tipo.add("HABILITACION");
        tipo0.add(ArrayList.class.getName());
        tipo0.add(tipo);
        entidad.put("tipo", tipo0);


        System.out.println("************************************************************************************* ");
        System.out.println(info.toString());
        System.out.println("************************************************************************************* ");

        Map adicionales = new HashMap();
        adicionales.put("@class", HashMap.class.getName());
        adicionales.put("serie", Integer.parseInt(info.get("serie_id")));
        adicionales.put("bodega", Integer.parseInt(info.get("bodega")));
        adicionales.put("lprecio", Integer.parseInt(info.get("cliente_id"))); //el mismo que abajo
        adicionales.put("idTercero",Integer.parseInt(info.get("cliente_id"))); // el mismo que arriba
        adicionales.put("tipoTercero", "cliente");

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        Map pageOrden = new LinkedHashMap();
        pageOrden.put("@class", LinkedHashMap.class.getName());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("entidad", entidad);
        map.put("adicionales", adicionales);
        map.put("pagerFiltros", pagerFiltros);
        map.put("pageOrden", pageOrden);
        map.put("pagerMaxResults", 100);
        map.put("pagerFirstResult", 0);
        map.put("caracteristicas",idLinea);


        String objectStr = JSONValue.toJSONString(map);
        System.out.println("Lo que se envia: " + objectStr);

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
            lista_art_temporales.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);

            // counter = counter + 1;
            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                String aws = "";
                if(mapa1.get("awsUrl") != null)
                    aws = mapa1.get("awsUrl").toString();

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), 0.0, "", aws);

                lista_art_temporales.add(art);


            }
            crearTablaArticulos2();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void crearTablaArticulos2() {

        // String[] colors1 = {"#0084aa","#c2272d","#333333","#3b3251","#ba2c54","#fed353","#0084aa","#7ca755","#333333","#ef812c","#3e8c64","#c2272d"};

        String[] colors1 = {"#41d9aa","#496c8c","#41d9aa","#496c8c","#41d9aa","#496c8c"};
        int[] resources = {R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_verdel, R.drawable.tabla_azul, R.drawable.tabla_verdel, R.drawable.tabla_azul};

        //Drawable[] estilos = {R.drawable.tabla_azul};

        // prices.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //prices.setOrientation(LinearLayout.HORIZONTAL);

        //                android:endColor="#70dd8e"
        //android:centerColor="#71cdc8"
        // android:startColor="#58caee"


        prices.removeAllViews();
        if (lista_art_temporales.isEmpty()) {
            TextView c1 = new TextView(getActivity().getApplicationContext());
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            int sobrante = lista_art_temporales.size() % 3;

            for (int j = 0; j < lista_art_temporales.size()-sobrante; j+=3) {
                // counter = counter +1;
                int x = j ;
                final int y = j  + 1;
                final int xx = j +2;
                //      final int yy = j  + 3;

                LinearLayout layout0 = new LinearLayout(getActivity().getApplicationContext());
                layout0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout0.setOrientation(LinearLayout.HORIZONTAL);




                LinearLayout layout1 = crearElemento(colors1, x, resources);
                layout0.addView(layout1);

                LinearLayout layout2 = crearElemento(colors1, y, resources);
                layout0.addView(layout2);

                LinearLayout layout3 = crearElemento(colors1, xx, resources);
                layout0.addView(layout3);

                //LinearLayout layout4 = crearElemento(colors1, yy, resources);
                //layout0.addView(layout4);






                prices.addView(layout0);
            }



            if(sobrante != 0){
                LinearLayout layout90 = new LinearLayout(getActivity().getApplicationContext());
                layout90.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                layout90.setOrientation(LinearLayout.HORIZONTAL);

                if(sobrante == 1){
                    LinearLayout layout1 = crearElemento(colors1, lista_art_temporales.size()-sobrante, resources);
                    layout90.addView(layout1);
                }
                if(sobrante == 2){
                    LinearLayout layout1 = crearElemento(colors1, lista_art_temporales.size()-sobrante, resources);
                    layout90.addView(layout1);
                    LinearLayout layout2 = crearElemento(colors1, lista_art_temporales.size()-sobrante+1, resources);
                    layout90.addView(layout2);
                }
           /* if(sobrante == 3){
                LinearLayout layout1 = crearElemento(colors1, lista_art_temporales.size()-sobrante, resources);
                layout90.addView(layout1);
                LinearLayout layout2 = crearElemento(colors1, lista_art_temporales.size()-sobrante+1, resources);
                layout90.addView(layout2);
                LinearLayout layout3 = crearElemento(colors1, lista_art_temporales.size()-sobrante+2, resources);
                layout90.addView(layout3);
            }*/
                prices.addView(layout90);
            }






        }
    }


    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = Math.round(px / (metrics.densityDpi / 160f));
        return dp;
    }

    @SuppressLint("ResourceType")
    public LinearLayout crearElemento(String[] colors, final int x, int[] resources){
        LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if(scale<2)
            scale=Float.parseFloat("1.8");
        // Toast.makeText(getActivity().getApplicationContext(), String.valueOf(scale), Toast.LENGTH_LONG).show();
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(180 * scale), Math.round(120 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setGravity(Gravity.CENTER);
        layout2.setBackgroundColor(Color.parseColor(colors[x % 6]));
        layout2.setBackgroundResource(resources[x % 6]);


        LayoutInflater imagen_alert = LayoutInflater.from(getActivity());
        final View ima = imagen_alert.inflate(R.layout.imagenes, null);



        ImageView bt = new ImageView(getActivity().getApplicationContext());


        String path = "";
        if (lista_art_temporales.get(x).getAwsUrl() != null) {
            path = lista_art_temporales.get(x).getAwsUrl();
        }
        if(!path.isEmpty())
            Picasso.with(this.getActivity()).load(path).into(bt);
        bt.setImageResource(R.drawable.signed3);
        bt.setLayoutParams(new TableRow.LayoutParams(Math.round(54 * scale), Math.round(36 * scale)));
        bt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                AlertDialog.Builder info = new AlertDialog.Builder(getActivity());
                info.setView(ima);
                final AlertDialog dialog = info.create();
                final ImageView imagenesComida = (ImageView) ima.findViewById(R.id.imagenes);
                final TextView comentarioComida = (TextView) ima.findViewById(R.id.comentario);
                final TextView platillo = (TextView) ima.findViewById(R.id.platillo);
                String path = "";
                if (lista_art_temporales.get(x).getAwsUrl() != null) {
                    path = lista_art_temporales.get(x).getAwsUrl();
                }
                if(!path.isEmpty())
                    Picasso.with(getContext().getApplicationContext()).load(path).resize(500,500).centerInside().into(imagenesComida);
                String come = lista_art_temporales.get(x).getNombre();
                platillo.setText(come);
                //if(lista_art_temporales.get(x).getDescripcion().length()>10000)
                comentarioComida.setText(lista_art_temporales.get(x).getDescripcion());
                info.setView(ima);
                info.setCancelable(true);
                info.show();
            }
        });
        TextView c4 = new TextView(getActivity().getApplicationContext());
        c4.setTextColor(Color.WHITE);
        c4.setGravity(Gravity.CENTER);
        //c4.setTextSize(25);

        c4.setTypeface(null, Typeface.BOLD);
        c4.setText(lista_art_temporales.get(x).getNombre().toString());
        TextView c6 = new TextView(getActivity().getApplicationContext());
        c6.setText("Precio: " + lista_art_temporales.get(x).getPrecioBase().toString());
        c6.setTextColor(Color.WHITE);
        c6.setGravity(Gravity.CENTER);


        LinearLayout layout00 = new LinearLayout(getActivity().getApplicationContext());
        layout00.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout00.setOrientation(LinearLayout.HORIZONTAL);
        layout00.setGravity(Gravity.CENTER);





        layout2.addView(bt);
        layout2.addView(c4);
        layout2.addView(c6);
        layout2.addView(layout00);

        return layout2;
    }


    public  void  getData(String message, String title){

        System.out.println("1************************************************************************************* ");
        System.out.println(info.toString());
        System.out.println("1************************************************************************************* ");


        titulo.setText(title);
        idLinea = message;
        prices.removeAllViews();
        GifImageView imagen = new GifImageView(getActivity().getApplicationContext());
        imagen.setImageResource(R.drawable.loader);
        prices.addView(imagen);
        HttpAsyncTask7 a = new HttpAsyncTask7();
        a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");
    }



    public void convertirMapa(String cadena){
        System.out.println(" convertirMapa 514 " + cadena);
        System.out.println(cadena);

        ObjectMapper mapper = new ObjectMapper();
        try {
            productos_list.clear();

            // mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            productos_list = mapper.readValue(cadena, LinkedList.class);





            System.out.println("List convertirMapa 528 " + productos_list);





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


        System.out.println(" La cadena de convertirMapa 2 : " + cadena);





        ObjectMapper mapper = new ObjectMapper();
        try {

            info = mapper.readValue(cadena, HashMap.class);
            counter = Integer.parseInt(info.get("counter").toString());
            laSerie = info.get("serie_id");
            laSerieNombre = info.get("serie");
            elCliente = info.get("cliente_id");
            if(info.get("impuesto")!=null)
                impuesto = Double.parseDouble(info.get("impuesto"));
            if(info.get("lprecio")!=null)
                laMoneda= Integer.parseInt(info.get("lprecio"));
            elAgente = info.get("agente_id");
            if(info.get("idDireccion")!=null)
                laDireccion = Integer.parseInt(info.get("idDireccion"));

            System.out.println("info " + info);
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void convertirMapa3(String cadena){


        if (cadena.equals("[]") || cadena.equals("")) {

        }
        else {


/*

            lista_art.clear();
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
                    articulo_map.put(pairs3[0], pairs3[1]);
                }

                System.out.println( " $$$$$$$$$444" + articulo_map);


                Articulo nuevo = new Articulo(Integer.parseInt(articulo_map.get("id").toString()), articulo_map.get("descripcion").toString(), Double.parseDouble(articulo_map.get("existencia").toString()), articulo_map.get("nombre").toString(), articulo_map.get("nombreCorto"), Double.parseDouble(articulo_map.get("precioBase").toString()), Double.parseDouble(articulo_map.get("impuesto").toString()), Integer.parseInt(articulo_map.get("counter").toString()), Integer.parseInt(articulo_map.get("cantidad").toString()));
                lista_art.add(nuevo);
            }

            System.out.println("\n Enviar recibir lista_art " + lista_art);
*/


        }
    }


    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity().getApplicationContext(), "Documento modificado" + result, Toast.LENGTH_LONG).show();
            Intent  i = new Intent(getActivity().getApplicationContext(), MenuMesas.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   i.putExtra("idMesa", lista_art_temporales.get(x).getIndexMesa().toString());
            i.putExtra("tipo_documento", tipo_documento);
            //   i.putExtra("idDocumento", lista_art_temporales.get(x).getIdDocumento().toString());

            startActivity(i);
            getActivity().finish();

        }
    }


    public String POST8(String url)  {
        HashMap t0 = new HashMap();

        Double total_subtotal = 0.0;
        for(int i = 0; i< productos_list.size(); i++){
            System.out.println("la lista: " + productos_list.get(i));
            total_subtotal += Double.parseDouble(productos_list.get(i).get("cantidad").toString())*Double.parseDouble(productos_list.get(i).get("precio").toString()) ;
//            productos_list.get(i).put("idC",productos_list.get(i).get("idC")+"F"+escaner_folio.toString()+"SF"+laSerie);

        }
        t0.put("producto", productos_list);
        t0.put("@class", ArrayList.class.getName());

        HashMap map2 = new HashMap();
        map2.put("id_documento", id_doc);  //id Tercero
        map2.put("entidad", 4);
        map2.put("@class", HashMap.class.getName());

        HashMap totalisimo = new HashMap();
        totalisimo.put("map2",map2.toString());
        totalisimo.put("t0",t0.toString());
        totalisimo.put("@class", HashMap.class.getName());

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
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);

            System.out.println("-----> Lo que se envia: 1" + jsonOBJECT1.toString());
            System.out.println("Lo que se envia: 2" + params.toString());

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



            //------------------------------------------------------------------------------------------------

            JSONArray array = new JSONArray();
            ArrayList tmp = new ArrayList();
            for (int i = 0; i < productos_list.size(); i++) {
                JSONObject jsonOBJECT2 = new JSONObject(productos_list.get(i));
                tmp.add(jsonOBJECT2);
                array.put(jsonOBJECT2);
            }



            Double total_subtotal = 0.0;
            for(int i = 0; i< productos_list.size(); i++){
                System.out.println("la lista: " + productos_list.get(i));
                total_subtotal += Double.parseDouble(productos_list.get(i).get("cantidad").toString())*Double.parseDouble(productos_list.get(i).get("precio").toString()) ;

                if(productos_list.get(i).containsKey("extra")){
                    System.out.println("Si modifica");

                    productos_list.get(i).put("idC","C:"+(id_tmp+1)+" F:"+escaner_folio.toString()+" SF:"+laSerieNombre+" "+referenciaGlobal+ " | "+productos_list.get(i).get("extra").toString()/*+ "|" + "refextra"+refextra*/);
                }else{

                    productos_list.get(i).put("idC","C:"+(id_tmp + 1 )+" F:"+escaner_folio.toString()+" SF:"+laSerieNombre+" "+referenciaGlobal/*+ "|" + "refextra"+refextra*/);

                }

            }


            System.out.println("List convertirMapa 514 c " + productos_list);

            HashMap t0 = new HashMap();
            t0.put("producto", productos_list);
            t0.put("@class", ArrayList.class.getName());



            HashMap t1 = new HashMap();
            t1.put("@class", HashMap.class.getName());
            t1.put("id0",total_subtotal.toString());
            //  totales.add(t1);
            // HashMap t2 = new HashMap();
            //t1.put("@class", HashMap.class.getName());
            t1.put("id1","0.0");
            //  totales.add(t2);
            // HashMap t3 = new HashMap();
            // t1.put("@class", HashMap.class.getName());
            t1.put("id2","0.0");
            // totales.add(t3);
            //  HashMap t4 = new HashMap();
            // t1.put("@class", HashMap.class.getName());
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
            map2.put("referenciaGlobal",referenciaGlobal);
            map2.put("impuesto",impuesto);
            map2.put("@class", HashMap.class.getName());

            HashMap map3 = new HashMap();
            map3.put("formasPago", formasPago_tmp);
            map3.put("@class", ArrayList.class.getName());

            Map totalisimo = new HashMap();
            totalisimo.put("map3",map3.toString());
            totalisimo.put("map2",map2.toString());
            totalisimo.put("t1",t1.toString());
            totalisimo.put("t0",t0.toString());
            totalisimo.put("@class", HashMap.class.getName());

            JSONObject jsonOBJECT1 = new JSONObject(totalisimo);
            //------------------------------------------------------------------------------------------------




            Intent  intent = new Intent(getActivity().getApplicationContext(), ComandasGeneral.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("tipo_documento", tipo_documento);
            intent.putExtra("idDocumento", "null");
            intent.putExtra("idMesa", idMesa.toString());
            intent.putExtra("esCaja", true);
            intent.putExtra("informacion", jsonOBJECT1.toString());
            intent.putExtra("impuesto", impuesto);
            startActivity(intent);
            getActivity().finish();

            //HttpAsyncTask9 a = new HttpAsyncTask9();
            //a.execute(server + "/medialuna/spring/documento/crear/app");



        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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





    private class HttpAsyncTask6 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST6(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("folio " + result);
            escaner_folio = Integer.parseInt(result);
            HttpAsyncTask12 b = new HttpAsyncTask12();
            b.execute(server + "/medialuna/spring/listar/catalogo/1403/");



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




    public void alertReferenciaGlobal(final int x) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity().getApplicationContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.referencia_comida, null);
        builder.setView(dialogView);


        Button busclie_btn_aceptar = (Button) dialogView.findViewById(R.id.btn_test);
        //    Button busclie_btn_cancelar = (Button) dialogView.findViewById(R.id.busclie_btn_cancelar);


        final EditText busclie_etx_nom = (EditText) dialogView.findViewById(R.id.edit_test);

        busclie_etx_nom.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);



        final android.app.AlertDialog dialog = builder.create();

        busclie_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





            }
        });

  /*      busclie_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });*/

        dialog.show();
    }

}
