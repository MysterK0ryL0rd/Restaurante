package com.gm3s.erp.gm3srest;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MenuArticulosLineas extends Fragment {
    String tipo_documento = "";
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
    Integer comensales = 0;
    static Map<String,String> info = new HashMap<>();
    boolean flag=false;


    LinkedList<HashMap> productos_list = new LinkedList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       rootView =  inflater.inflate(R.layout.activity_menu_articulos_lineas, container, false);

        return rootView;
    }







    @Override
    public void onStart(){
        super.onStart();

        Intent intent = getActivity().getIntent();
        String PedidoMapa = (String) getActivity().getIntent().getSerializableExtra("PedidoMapa");
        String InfoMapa = (String) getActivity().getIntent().getSerializableExtra("InfoMapa");
        String PedidoArt = (String)intent.getSerializableExtra("PedidoArt");
        comensales = Integer.parseInt((String)intent.getSerializableExtra("comensales"));
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");

        convertirMapa(PedidoMapa);
        convertirMapa2(InfoMapa);
        convertirMapa3(PedidoArt);

        System.out.println("Contenido de PedidoMapa: " + PedidoMapa);
        System.out.println("Contenido de InfoMapa: " + InfoMapa);



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
                if (comensales == 0) {
                    i = new Intent(getActivity().getApplicationContext(), Pedido.class);
                } else {
                    i = new Intent(getActivity().getApplicationContext(), MenuComensales.class);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("PedidoArt", JSONValue.toJSONString(lista_art));
                i.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                i.putExtra("InfoMapa", JSONValue.toJSONString(info));
                i.putExtra("tipo_documento", tipo_documento);
                i.putExtra("comensales", comensales.toString());
                lista_art.clear();
                productos_list.clear();
                startActivity(i);
                getActivity().finish();


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

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), 0);

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

    public LinearLayout crearElemento(String[] colors, final int x, int[] resources){
        LinearLayout layout2 = new LinearLayout(getActivity().getApplicationContext());
        layout2.setPadding(20, 20, 20, 20);
        float scale = getResources().getDisplayMetrics().density;//3.0cel    1.2tablet
        if(scale<2)
            scale=Float.parseFloat("1.8");
       // Toast.makeText(getActivity().getApplicationContext(), String.valueOf(scale), Toast.LENGTH_LONG).show();
        layout2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(160 * scale), Math.round(160 * scale))); //500 500 cel
        layout2.setOrientation(LinearLayout.VERTICAL);
        //layout2.setBackground();

        layout2.setBackgroundColor(Color.parseColor(colors[x % 6]));
        layout2.setBackgroundResource(resources[x % 6]);






       // ImageView c0 = new ImageView(getActivity().getApplicationContext());
       // Picasso.with(getActivity().getApplicationContext()).load("https://dl.dropboxusercontent.com/u/44047420/bolis/"+lista_art_temporales.get(x).getId().toString()+".jpg").into(c0);
       // layout2.setBackground(c0.getDrawable());


        ImageView c1 = new ImageView(getActivity().getApplicationContext());
        c1.setImageResource(R.drawable.signed3);
        TextView c3 = new TextView(getActivity().getApplicationContext());
        c3.setText("Existencia: " + lista_art_temporales.get(x).getExistencia().toString());
        c3.setGravity(Gravity.CENTER);
        c3.setTextColor(Color.WHITE);
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

        final EditText c8 = new EditText(getActivity().getApplicationContext());
        c8.setText("0");
        c8.setEnabled(false);
        c8.setTextSize(30);
        c8.setTextColor(Color.WHITE);

        Button c7 = new Button(getActivity().getApplicationContext());
        c7.setText("-");
        c7.setLayoutParams(new TableRow.LayoutParams(Math.round(54 * scale), Math.round(36*scale))); //cel  para tablet para tablet la mitad
        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getActivity().getApplicationContext(), x + "   " + lista_art_temporales.get(x).getId() + " " + lista_art_temporales.get(x).getNombre(), Toast.LENGTH_SHORT).show();
                //lista_articulos.add(lista_art(jj));
                if (Integer.parseInt(c8.getText().toString()) > 0) {
                    c8.setText(String.valueOf(Integer.parseInt(c8.getText().toString()) - 1));
                    if (mapa_articulos.containsKey(lista_art_temporales.get(x).getId())) {
                        lista_art_temporales.get(x).setCantidad(Integer.parseInt(c8.getText().toString()));

                        mapa_articulos.put(lista_art_temporales.get(x).getId(), lista_art_temporales.get(x));

                        contador_int = contador_int - 1;
                        //contador.setText(contador_int.toString());

                        if (mapa_articulos.get(lista_art_temporales.get(x).getId()).getCantidad() == 0) {
                            mapa_articulos.remove(lista_art_temporales.get(x).getId());
                            counter--;
                        }

                    }
                }

            }
        });

        Button c9 = new Button(getActivity().getApplicationContext());
        c9.setText("+");
        c9.setLayoutParams(new TableRow.LayoutParams(Math.round(54 * scale), Math.round(36 * scale)));
        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity().getApplicationContext(), x + " " + lista_art_temporales.get(x).getId() + " " + lista_art_temporales.get(x).getNombre(), Toast.LENGTH_SHORT).show();
                //lista_articulos.add(lista_art(jj));
                c8.setText(String.valueOf(Integer.parseInt(c8.getText().toString()) + 1));
                lista_art_temporales.get(x).setCantidad(Integer.parseInt(c8.getText().toString()));
                if(mapa_articulos.containsKey(lista_art_temporales.get(x).getId())){

                }
                else{
                    counter++;
                }
                lista_art_temporales.get(x).setCounter(counter);
                mapa_articulos.put(lista_art_temporales.get(x).getId(), lista_art_temporales.get(x));



                contador_int = contador_int + 1;
                contador.setText(contador_int.toString());


            }
        });

        layout00.addView(c7);
        layout00.addView(c8);
        layout00.addView(c9);

        layout2.addView(c1);
        layout2.addView(c4);
        layout2.addView(c6);
        layout2.addView(c3);
        layout2.addView(layout00);

        return layout2;
    }


    public  void  getData(String message, String title){
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


        System.out.println(" La cadena de convertirMapa 2 : " + cadena);





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


        if (cadena.equals("[]") || cadena.equals("")) {

        }
        else {



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

    }
