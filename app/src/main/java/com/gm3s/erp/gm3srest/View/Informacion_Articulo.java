package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Informacion_Articulo extends AppCompatActivity {
    static Integer idArticulo = 0, codigoUsuario = 0;
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    String server = "";
    int selected = 0;
    static boolean validacion;
    static Spinner spin_tipo, spin_estatus;
    static TextView tv_unidad;
    HashMap<String, Object> articulo = new HashMap<>();
    static EditText n_corto_articulo, n_articulo, d_articulo, sku_articulo, costo_articulo, precioBase_articulo;
    static CheckBox grav_articulo;
    Button btn_nuevo_art, info_art_costos_btn, btn_unidad, btn_costos;
    List<HashMap<String, Object>> precios_x_art = new ArrayList<>();
    List<String> nombre_lista_precio = new ArrayList<String>();
    List<Integer> ids_lista_precio = new ArrayList<Integer>();
    static int id_catalogo;
    double impuesto = 0.0;
    static String precio_articulo;
    static int id_moneda;
    List<String> nombre_catalogo = new ArrayList<String>();
    List<Integer> ids_catalogo = new ArrayList<Integer>();
    int temp = 0;
    List<String> nombre_serie = new ArrayList<String>();
    List<Integer> ids_serie = new ArrayList<Integer>();
    static int id_serie;
    static String costo_articulo_string;
    List<String> nombre_moneda = new ArrayList<String>();
    List<Integer> ids_moneda = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion__articulo);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        spin_tipo = (Spinner) findViewById(R.id.spin_tipo);
        spin_estatus = (Spinner) findViewById(R.id.spin_estatus);
        n_corto_articulo = (EditText) findViewById(R.id.n_corto_articulo);
        n_articulo = (EditText) findViewById(R.id.n_articulo);
        d_articulo = (EditText) findViewById(R.id.d_articulo);
        sku_articulo = (EditText) findViewById(R.id.sku_articulo);
        costo_articulo = (EditText) findViewById(R.id.costo_articulo);
        precioBase_articulo = (EditText) findViewById(R.id.precioBase_articulo);
        grav_articulo = (CheckBox) findViewById(R.id.grav_articulo);
        btn_nuevo_art = (Button) findViewById(R.id.btn_nuevo_art);
        info_art_costos_btn = (Button) findViewById(R.id.info_art_costos_btn);
        btn_unidad = (Button) findViewById(R.id.btn_unidad);
        btn_costos = (Button) findViewById(R.id.btn_costos);
        tv_unidad = (TextView) findViewById(R.id.tv_unidad);

        Intent intent = getIntent();
        idArticulo = Integer.parseInt(intent.getExtras().getString("idArticulo"));
        codigoUsuario = Integer.parseInt(intent.getExtras().getString("codigoUsuario"));
        Integer opcion =  Integer.parseInt(intent.getExtras().getString("opcion")); //O Modificar, 1 Borrar


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Informacion Articulo");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/util/genericos/busquedaArticulosApp/");

        btn_unidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask8 a = new HttpAsyncTask8();
                a.execute(server + "/medialuna/spring/listar/catalogo/1208/");
            }
        });



        btn_costos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask9 a = new HttpAsyncTask9();
                a.execute(server + "/medialuna/spring/articulos/buscarSerieDefault");
            }
        });





        btn_nuevo_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    HttpAsyncTask2 a = new HttpAsyncTask2();
                    a.execute(server + "/medialuna/spring/app/modificarArticulo");
                }
       });


        info_art_costos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask3 a = new HttpAsyncTask3();
                a.execute(server + "/medialuna/spring/app/buscarCostosArticulo");
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

            System.out.println("Result: " + result);
            convertirDatos(result);
        }
    }


    public static String POST(String url) {
        LinkedHashMap pagerOrden = new LinkedHashMap();
        pagerOrden.put("@class", LinkedHashMap.class.getName());

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());
        pagerFiltros.put("id", idArticulo);


        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("activos", true);
        entidad.put("maxResults", 1);
        entidad.put("firstResult", 0);
        entidad.put("pagerFiltros", pagerFiltros);
        entidad.put("pagerOrden", pagerOrden);


        String objectStr = JSONValue.toJSONString(entidad);


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



    public void convertirDatos(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                  vaciarDatos(mapa1);
               }


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void vaciarDatos(Map mapa1) {
        articulo.put("id", mapa1.get("id"));


        if (mapa1.containsKey("tipo")) {
            articulo.put("tipo", mapa1.get("tipo"));
            String tipo = ((String) articulo.get("tipo")).substring(0, 1) + ((String) articulo.get("tipo")).substring(1).toLowerCase();
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_articulo, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_tipo.setAdapter(adapter);
            if (!tipo.equals(null)) {
                int spinnerPosition = adapter.getPosition(tipo);
                spin_tipo.setSelection(spinnerPosition);
            }
        }
        if (mapa1.containsKey("nombreCorto")) {
            articulo.put("nombreCorto", mapa1.get("nombreCorto"));
            String nombreCorto = (String) articulo.get("nombreCorto");
            n_corto_articulo.setText(nombreCorto);
        }
        if (mapa1.containsKey("nombre")) {
            articulo.put("nombre", mapa1.get("nombre"));
            String nombre = (String) articulo.get("nombre");
            n_articulo.setText(nombre);
        }
        if (mapa1.containsKey("descripción")) {
            articulo.put("descripción", mapa1.get("descripción"));
            String descripcion = (String) articulo.get("descripción");
            d_articulo.setText(descripcion);
        }
        if (mapa1.containsKey("gravable")) {
            articulo.put("gravable", mapa1.get("gravable"));
            boolean gravable = (Boolean) articulo.get("gravable");
            grav_articulo.setChecked(gravable);
        }
        if (mapa1.containsKey("codigoSKU") && mapa1.get("codigoSKU")!=null) {
            articulo.put("codigoSKU", mapa1.get("codigoSKU"));
            String codigoSKU = (String) articulo.get("codigoSKU");
            sku_articulo.setText(codigoSKU);
        }
        if (mapa1.containsKey("estatus")) {
            articulo.put("estatus", mapa1.get("estatus"));
            String estatus = ((String) articulo.get("estatus")).substring(0, 1) + ((String) articulo.get("estatus")).substring(1).toLowerCase();
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.estatus_articulo, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_estatus.setAdapter(adapter2);
            if (!estatus.equals(null)) {
                int spinnerPosition2 = adapter2.getPosition(estatus);
                spin_estatus.setSelection(spinnerPosition2);
            }
        }
        if (mapa1.get("unidad")!=null) {
            Map mapa2 = (Map) mapa1.get("unidad");
            articulo.put("unidad", mapa2.get("nombre"));
            id_catalogo = (Integer) mapa2.get("id");
            String unidad = ((String) articulo.get("unidad")).substring(0, 1) + ((String) articulo.get("unidad")).substring(1).toLowerCase();
            tv_unidad.setText(unidad);

        }
        if (mapa1.containsKey("costo") ) {
            articulo.put("costo", mapa1.get("costo"));
            Double costo = (Double) articulo.get("costo");
            costo_articulo.setText(costo.toString());
        }

        if (mapa1.containsKey("precioBase") ) {
            articulo.put("precioBase", mapa1.get("precioBase"));
            Double precioBase = (Double) articulo.get("precioBase");
            precioBase_articulo.setText(precioBase.toString());
        }
    }



    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result: " + result);
            finish();

        }
    }


    public static String POST2(String url) {


        Map mapa_articulo = new HashMap();
        mapa_articulo.put("tipo", spin_tipo.getSelectedItem().toString().toUpperCase());
        mapa_articulo.put("nombreCorto", n_corto_articulo.getText().toString());
        mapa_articulo.put("nombre", n_articulo.getText().toString());
        mapa_articulo.put("descripcion", d_articulo.getText().toString());
        mapa_articulo.put("codigoSKU", sku_articulo.getText().toString());
        mapa_articulo.put("unidad", id_catalogo);
        mapa_articulo.put("id", idArticulo);
        mapa_articulo.put("estatus", spin_estatus.getSelectedItem().toString().toUpperCase());
        mapa_articulo.put("@class", HashMap.class.getName());
        mapa_articulo.put("gravable", grav_articulo.isChecked());


        String objectStr = JSONValue.toJSONString(mapa_articulo);


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
            System.out.println("Lo que se envia: " + objectStr.toString());
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




    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST3(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result: " + result);
            convertirDatos3(result);
        }
    }


    public static String POST3(String url) {

         Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("id", idArticulo);



        String objectStr = JSONValue.toJSONString(entidad);


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type","application/json; charset=UTF-8");
            StringEntity params = new StringEntity(objectStr);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json; charset=UTF-8"));
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


    public void convertirDatos3(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {


            List<Object> arrayData = mapper.readValue(cadena, List.class);
            precios_x_art.clear();

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                Map mapa2 = (Map) mapa1.get("moneda");
                Map mapa3 = (Map) mapa1.get("lista");
                System.out.println((Integer) mapa1.get("id") + " " + mapa3.get("nombre").toString() + " " + Double.parseDouble(mapa1.get("precio").toString()) + " " + mapa2.get("nombreCorto").toString());
                HashMap<String, Object> precio_temp = new HashMap<>();
                precio_temp.put("id",(Integer) mapa1.get("id"));
                precio_temp.put("nombre",mapa3.get("nombre").toString());
                precio_temp.put("precio",Double.parseDouble(mapa1.get("precio").toString()));
                precio_temp.put("nombreCorto",mapa2.get("nombreCorto").toString());
                precios_x_art.add(precio_temp);


            }
            alertListaPrecioArticulo();


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void alertListaPrecioArticulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Informacion_Articulo.this);

        LayoutInflater inflater = Informacion_Articulo.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.lista_precios_articulo, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        Button lis_pre_a_pre = (Button) dialogView.findViewById(R.id.lis_pre_a_pre);
        TableLayout costos;
        costos = (TableLayout) dialogView.findViewById(R.id.tabla_precios);
        costos.removeAllViews();
        costos.setStretchAllColumns(true);
        costos.bringToFront();

        // Create the alert dialog
        final AlertDialog dialog = builder.create();


        lis_pre_a_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                HttpAsyncTask5 a = new HttpAsyncTask5();
                a.execute(server + "/medialuna/spring/articulos/listas/disponiblesSinAsignar/" + idArticulo);

            }
        });




        if (precios_x_art.isEmpty()) {

        } else {
            for (int j = 0; j < precios_x_art.size(); j++) {

                final HashMap<String, Object> precio_x_art = precios_x_art.get(j);

                TableRow tr = new TableRow(Informacion_Articulo.this);

                TextView c2 = (TextView) Informacion_Articulo.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c2.setText(precios_x_art.get(j).get("nombre").toString());

                TextView c3 = (TextView) Informacion_Articulo.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c3.setText(precios_x_art.get(j).get("precio").toString());

                TextView c31 = (TextView) Informacion_Articulo.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c31.setText(precios_x_art.get(j).get("nombreCorto").toString());

                ImageView c4 = new ImageView(Informacion_Articulo.this);
                c4.setImageResource(R.drawable.edit_icon);
                c4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertPrecioArticulo(precio_x_art);
                        dialog.cancel();


                    }
                });


                tr.addView(c2);
                tr.addView(c3);
                tr.addView(c31);
                tr.addView(c4);

                costos.addView(tr);
            }
        }





            // Display the custom alert dialog on interface
            dialog.show();

        }



    public void alertPrecioArticulo(final HashMap<String, Object> informacion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.editar_precio_art, null);
        builder.setView(dialogView);
        final TextView tv_nuevo_pre_lista = (TextView) dialogView.findViewById(R.id.tv_nuevo_pre_lista);
        final EditText et_nuevo_pre = (EditText) dialogView.findViewById(R.id.et_nuevo_pre);
        Button btn_nuevo_pre = (Button) dialogView.findViewById(R.id.btn_nuevo_pre);



        et_nuevo_pre.setText(((Double) informacion.get("precio")).toString());
        tv_nuevo_pre_lista.setText((String) informacion.get("nombre"));



        final AlertDialog dialog = builder.create();
        btn_nuevo_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(informacion.get("nombre").toString().equals("PRECIO BASE")){
                    precioBase_articulo.setText(et_nuevo_pre.getText().toString());

                }

                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/articulos/modifica2/precio/" + Integer.parseInt(informacion.get("id").toString()) + "/" + Double.parseDouble(et_nuevo_pre.getText().toString()) + "/");
                dialog.cancel();

            }
        });








        dialog.show();
    }



    private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST4(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result: " + result);
            HttpAsyncTask3 a = new HttpAsyncTask3();
            a.execute(server + "/medialuna/spring/app/buscarCostosArticulo");

        }
    }


    public static String POST4(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
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



    private class HttpAsyncTask5 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST5(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            if(result.equals("[]")){
                Toast.makeText(getApplicationContext(), "Haz añadido todos los precios al articulo", Toast.LENGTH_SHORT).show();
                HttpAsyncTask3 a = new HttpAsyncTask3();
                a.execute(server + "/medialuna/spring/app/buscarCostosArticulo");


            }else{
                convertirDatos5(result);
                System.out.println("onPostExecute 3: " + result);

            }

        }
    }


    public static String POST5(String url) {
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


    public void convertirDatos5(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            ids_lista_precio.clear();
            nombre_lista_precio.clear();
            for (int i = 0; i < arrayData.size(); i++) {
                List<Object> arrayData2 = (List<Object>) arrayData.get(i);
                List<Object> arrayData3 = (List<Object>) arrayData2.get(1);
                ids_lista_precio.add((Integer) arrayData3.get(0));
                nombre_lista_precio.add((String) arrayData3.get(1));
            }

            HttpAsyncTask11 b = new HttpAsyncTask11();
            b.execute(server + "/medialuna/spring/catalogos/10007/registros/");

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }



    public void alertPrecioArticulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.nuevo_precio_art, null);
        builder.setView(dialogView);
        final Spinner sp_nuevo_pre_lista = (Spinner) dialogView.findViewById(R.id.sp_nuevo_pre_lista);
        final Spinner sp_nuevo_pre_mon = (Spinner) dialogView.findViewById(R.id.sp_nuevo_pre_mon);
        final EditText et_nuevo_pre = (EditText) dialogView.findViewById(R.id.et_nuevo_pre);
        Button btn_nuevo_pre = (Button) dialogView.findViewById(R.id.btn_nuevo_pre);
        Button btn_nuevo_pre_calc_iva = (Button) dialogView.findViewById(R.id.btn_nuevo_pre_calc_iva);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombre_lista_precio);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_nuevo_pre_lista.setAdapter(spinnerAdapter);
        setIndex(sp_nuevo_pre_lista);
        spinnerAdapter.notifyDataSetChanged();



        HttpAsyncTask6 a = new HttpAsyncTask6();
        a.execute(server + "/medialuna/spring/regcat/buscar/iva");


        final AlertDialog dialog = builder.create();
        btn_nuevo_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_moneda = ids_moneda.get(sp_nuevo_pre_mon.getSelectedItemPosition());
                id_catalogo = ids_lista_precio.get(sp_nuevo_pre_lista.getSelectedItemPosition());
                precio_articulo = et_nuevo_pre.getText().toString();


                if(nombre_lista_precio.get(sp_nuevo_pre_lista.getSelectedItemPosition()).equals("PRECIO BASE")){
                    precioBase_articulo.setText(et_nuevo_pre.getText().toString());

                }

                System.out.println("alertPrecioArticulo");
                HttpAsyncTask7 a = new HttpAsyncTask7();
                a.execute(server + "/medialuna/spring/articulos/precios/agregar/");
                dialog.cancel();

            }
        });


        btn_nuevo_pre_calc_iva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nuevo_pre.getText().toString().equals("")){

                }
                else{
                    Double temp = Double.parseDouble(et_nuevo_pre.getText().toString());
                    temp = temp * (1-(impuesto/100));
                    et_nuevo_pre.setText(temp.toString());


                }

            }
        });


        dialog.show();
    }

    private class HttpAsyncTask6 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST6(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("El resultado es: " + result);
            impuesto = Double.parseDouble(result);



        }
    }


    public String POST6(String url) {



        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("'Content-Type", "text/html; CHARSET=UTF-8");
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


    private class HttpAsyncTask7 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST7(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Precio Agregado", Toast.LENGTH_SHORT).show();
            System.out.println("onPostExecute 4");
            HttpAsyncTask3 a = new HttpAsyncTask3();
            a.execute(server + "/medialuna/spring/app/buscarCostosArticulo");
        }
    }


    public static String POST7(String url) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("articulo", String.valueOf(idArticulo));
        map.put("lista", String.valueOf(id_catalogo)); //ID PRECIO
        map.put("moneda", String.valueOf(id_moneda)); //ID MONEDA
        map.put("precio", precio_articulo);


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
            convertirDatos8(result);
        }
    }


    public static String POST8(String url) {
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

    public void convertirDatos8(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_catalogo.clear();
            ids_catalogo.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                if (mapa1.get("estatus").toString().equals("ACTIVO")) {
                    nombre_catalogo.add((String) mapa1.get("nombre"));
                    ids_catalogo.add((Integer) mapa1.get("id"));
                }
            }
            build_popup();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    private void build_popup() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(Informacion_Articulo.this);
        builder.setTitle("Catalogo");
        builder.setSingleChoiceItems(nombre_catalogo.toArray(new String[nombre_catalogo.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                temp = which;
            }

        });
        // TODO Auto-generated method stub


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = temp;
                switch (selected) {
                    default:
                        id_catalogo = ids_catalogo.get(selected);
                        tv_unidad.setText(nombre_catalogo.get(selected));
                        // laSerie = id_serie.get(selected);
                        // escaner_txt_serie.setText(nombre_serie.get(selected));
                        break;
                    //   case 0:text = "Bad";break;
                    //  case 1:text = "Good";break;
                    // case 2:text = "Very Good";break;
                    //   case 3:text = "Average";break;
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }

    private void setIndex(Spinner spinner){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals("PRECIO BASE")){
                index = i;
            }
        }
        spinner.setSelection(index);
    }

    private class HttpAsyncTask9 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST9(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            System.out.println("Resultado de consulta series: " + result);

            if(result.equals("[]")){
                //  Toast.makeText(getApplicationContext(),"Haz añadido todos los precios al articulo", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                convertirDatos9(result);
                // System.out.println("onPostExecute 3: " + result);

            }

        }
    }


    public static String POST9(String url) {
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


    public void convertirDatos9(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {

           HashMap arrayData = mapper.readValue(cadena, HashMap.class);
            ids_serie.clear();
            nombre_serie.clear();

                ids_serie.add((Integer) arrayData.get("id"));
                nombre_serie.add((String) arrayData.get("nombreCorto"));


            alertCostoArticulo();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    public void alertCostoArticulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.nuevo_costo_art, null);
        builder.setView(dialogView);
        final Spinner sp_nuevo_pre_serie = (Spinner) dialogView.findViewById(R.id.sp_nuevo_pre_serie);

        final EditText et_nuevo_cos = (EditText) dialogView.findViewById(R.id.et_nuevo_cos);
        Button btn_nuevo_pre = (Button) dialogView.findViewById(R.id.btn_nuevo_cos);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombre_serie);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_nuevo_pre_serie.setAdapter(spinnerAdapter);
        setIndex(sp_nuevo_pre_serie);
        spinnerAdapter.notifyDataSetChanged();






        final AlertDialog dialog = builder.create();
        btn_nuevo_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                costo_articulo_string = et_nuevo_cos.getText().toString();
                id_serie = ids_serie.get(sp_nuevo_pre_serie.getSelectedItemPosition());


                HttpAsyncTask10 a = new HttpAsyncTask10();
                a.execute(server + "/medialuna/spring/articulos/insertar/costeo/manual/app");
                dialog.cancel();

            }
        });




        dialog.show();
    }


    private class HttpAsyncTask10 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST10(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            setText(costo_articulo,costo_articulo_string);


        }
    }


    public static String POST10(String url) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("costo", String.valueOf(costo_articulo_string));
        map.put("idArticulo", idArticulo);
        map.put("idSerie", id_serie);


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


    private void setText(final TextView text, final String value) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }




    public void convertirDatos11(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_moneda.clear();
            ids_moneda.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                if (mapa1.get("estatus").toString().equals("ACTIVO")) {
                    nombre_moneda.add((String) mapa1.get("nombreCorto"));
                    ids_moneda.add((Integer) mapa1.get("id"));
                }
            }

            alertPrecioArticulo();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    private class HttpAsyncTask11 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST11(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("El resultado es: " + result);
            //  impuesto = Double.parseDouble(result);

            convertirDatos11(result);

        }
    }


    public String POST11(String url) {



        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("'Content-Type", "text/html; CHARSET=UTF-8");
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


