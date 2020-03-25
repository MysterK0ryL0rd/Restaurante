package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.Bodega;
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
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NuevoArticulo extends AppCompatActivity {
    int selected = 0;
    String server = "";
    private SharedPreference sharedPreference;
    Button btn_nuevo_art, btn_unidad;
    private static PersistentCookieStore pc;
    Spinner spin_tipo, spin_estatus;
    EditText n_corto_articulo, n_articulo, d_articulo, sku_articulo;
    List<String> nombre_catalogo = new ArrayList<String>();
    List<Integer> ids_catalogo = new ArrayList<Integer>();
    List<String> nombre_lista_precio = new ArrayList<String>();
    List<Integer> ids_lista_precio = new ArrayList<Integer>();
    List<String> nombre_serie = new ArrayList<String>();
    List<Integer> ids_serie = new ArrayList<Integer>();
    int temp = 0;
    static int id_catalogo;
    static int id_articulo = 59021;
    static int id_moneda, id_serie;
    static String precio_articulo;
    static String costo_articulo;
    static List<Bodega> lista_bodegas = new ArrayList<Bodega>();
    CheckBox grav_articulo;
    double impuesto = 0.0;
    List<String> nombre_moneda = new ArrayList<String>();
    List<Integer> ids_moneda = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_articulo);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        btn_nuevo_art = (Button) findViewById(R.id.btn_nuevo_art);
        btn_unidad = (Button) findViewById(R.id.btn_unidad);
        spin_tipo = (Spinner) findViewById(R.id.spin_tipo);
        spin_estatus = (Spinner) findViewById(R.id.spin_estatus);
        spin_estatus.setSelection(((ArrayAdapter)spin_estatus.getAdapter()).getPosition("Compra_Venta"));
        n_corto_articulo = (EditText) findViewById(R.id.n_corto_articulo);
        n_articulo = (EditText) findViewById(R.id.n_articulo);
        d_articulo = (EditText) findViewById(R.id.d_articulo);
        sku_articulo = (EditText) findViewById(R.id.sku_articulo);
        btn_unidad = (Button) findViewById(R.id.btn_unidad);
        grav_articulo = (CheckBox) findViewById(R.id.grav_articulo);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Nuevo Articulo");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_unidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask2 a = new HttpAsyncTask2();
                a.execute(server + "/medialuna/spring/listar/catalogo/1208/");
            }
        });




        btn_nuevo_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText[] editTextArray = {n_corto_articulo, n_articulo, d_articulo, sku_articulo};
                if (Helper.validate(editTextArray) && temp >= 0) {
                   // finish();
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/app/insertarArticulo");

                } else {
                    Toast.makeText(NuevoArticulo.this, "Favor de llenar los campos vacios", Toast.LENGTH_SHORT).show();

                }


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
            System.out.println("El resultado es: " + result);
            id_articulo = Integer.parseInt(result);


            //Here --------



            HttpAsyncTask5 a = new HttpAsyncTask5();
            a.execute(server + "/medialuna/spring/almacen/buscar/bodegas/");




        }
    }


    public String POST(String url) {

        Map mapa_articulo = new HashMap();
        mapa_articulo.put("tipo", spin_tipo.getSelectedItem().toString().toUpperCase());
        mapa_articulo.put("nombreCorto", n_corto_articulo.getText().toString());
        mapa_articulo.put("nombre", n_articulo.getText().toString());
        mapa_articulo.put("descripcion", d_articulo.getText().toString());
        mapa_articulo.put("codigoSKU", sku_articulo.getText().toString());
        mapa_articulo.put("unidad", id_catalogo);
        mapa_articulo.put("estatus", spin_estatus.getSelectedItem().toString().toUpperCase());
        mapa_articulo.put("@class", HashMap.class.getName());
        mapa_articulo.put("gravable", grav_articulo.isChecked());

        JSONObject jsonOBJECT1 = new JSONObject(mapa_articulo);
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("'Content-Type", "text/html; CHARSET=UTF-8");
            StringEntity params = new StringEntity(jsonOBJECT1.toString());
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=UTF-8"));
            httppost.setEntity(params);
            System.out.println("Lo que no se envia --> " + jsonOBJECT1.toString());
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


    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST2(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            convertirDatos2(result);
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


    public void convertirDatos2(String cadena) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoArticulo.this);
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

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        AlertDialog al = builder.create();
        al.show();
    }


    private void build_popupX() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoArticulo.this);
        builder.setTitle("Datos del Catalogo");
        builder.setMessage("Selecciona alguna opcion");

        // TODO Auto-generated method stub


        builder.setPositiveButton("Precios", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                 HttpAsyncTask3 a = new HttpAsyncTask3();
                 a.execute(server + "/medialuna/spring/articulos/listas/disponiblesSinAsignar/" + id_articulo);
            }

        });

            builder.setNegativeButton("Finalizar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
                finish();
            }
        });

        builder.setNeutralButton("Costos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub


                HttpAsyncTask9 a = new HttpAsyncTask9();
                 a.execute(server + "/medialuna/spring/listar/serie/pedidocliente/");



            }
        });
        AlertDialog al = builder.create();
        al.show();
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



        HttpAsyncTask7 a = new HttpAsyncTask7();
        a.execute(server + "/medialuna/spring/regcat/buscar/iva");


        final AlertDialog dialog = builder.create();
        btn_nuevo_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_moneda = ids_moneda.get(sp_nuevo_pre_mon.getSelectedItemPosition());
                id_catalogo = ids_lista_precio.get(sp_nuevo_pre_lista.getSelectedItemPosition());
                precio_articulo = et_nuevo_pre.getText().toString();


                System.out.println("alertPrecioArticulo");
                HttpAsyncTask4 a = new HttpAsyncTask4();
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



    private void setIndex(Spinner spinner){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals("PRECIO BASE")){
                index = i;
            }
        }
        spinner.setSelection(index);
    }

    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST3(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            if(result.equals("[]")){
                Toast.makeText(getApplicationContext(),"Haz añadido todos los precios al articulo", Toast.LENGTH_SHORT).show();
                finish();
                            }else{
                        convertirDatos3(result);
            System.out.println("onPostExecute 3: " + result);

            }

        }
    }


    public static String POST3(String url) {
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


    public void convertirDatos3(String cadena) {
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

            HttpAsyncTask8 b = new HttpAsyncTask8();
            b.execute(server + "/medialuna/spring/catalogos/10007/registros/");


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST4(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Precio Agregado", Toast.LENGTH_SHORT).show();
            System.out.println("onPostExecute 4");
            build_popup2();
         }
    }


    public static String POST4(String url) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("articulo", String.valueOf(id_articulo));
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




    private class HttpAsyncTask5 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST5(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), "Precio Agregado", Toast.LENGTH_SHORT).show();
            convertirDatos5(result);
            System.out.println("onPostExecute 5");


        }
    }


    public static String POST5(String url) {


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



    public void convertirDatos5(String cadena) {
        System.out.println("convertirDatos5: " + cadena);
        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_bodegas.clear();
            ArrayList<HashMap> arrayData = mapper.readValue(cadena, ArrayList.class);
              for (int i = 0; i < arrayData.size(); i++) {
                  HashMap arrayData2 = (HashMap) arrayData.get(i);
                  Bodega b = new Bodega ((Integer) arrayData2.get("idAlmacén"), (Integer ) arrayData2.get("idBodega"));
                  lista_bodegas.add(b);
            }

         //   alertPrecioArticulo();

            HttpAsyncTask6 a = new HttpAsyncTask6();
            a.execute(server + "/medialuna/spring/almacen/bodegas/cambio/");


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
            System.out.println("onPostExecute 6");

            build_popupX();
           // finish();
           // HttpAsyncTask3 a = new HttpAsyncTask3();
           // a.execute(server + "/medialuna/spring/articulos/listas/disponiblesSinAsignar/" + id_articulo);
          }
    }

    public static String POST6(String url) {
        ArrayList<String> cadenas = new ArrayList<>();
        for(int i=0; i<lista_bodegas.size(); i++){
          String cadena = "";
            System.out.println(id_articulo);
            cadena = "1" + "," + id_articulo + "," + lista_bodegas.get(i).getBodega() + "," + lista_bodegas.get(i).getAlmacen();
            cadenas.add(cadena);
        }

        ArrayList<Object> cadenas2 = new ArrayList<>();
        cadenas2.add(ArrayList.class.getName());
        cadenas2.add(cadenas);

        JSONArray arr = new JSONArray();
        arr.add(ArrayList.class.getName());
        arr.add(cadenas);


       // JSONObject jsonOBJECT1 = new JSONObject(arr);



        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");

            StringEntity params = new StringEntity(arr.toString());
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);
            System.out.println("Lo que envia --> " + arr.toString());

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


    private void build_popup2() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoArticulo.this);
        builder.setTitle("Precio");
        builder.setMessage("Deseas agregar mas precios al articulo?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                HttpAsyncTask3 a = new HttpAsyncTask3();
                a.execute(server + "/medialuna/spring/articulos/listas/disponiblesSinAsignar/" + id_articulo);

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
                finish();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }




    private class HttpAsyncTask7 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST7(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("El resultado es: " + result);
            impuesto = Double.parseDouble(result);



        }
    }


    public String POST7(String url) {



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





    private class HttpAsyncTask9 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST9(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

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


    public void convertirDatos9(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HashMap> arrayData = mapper.readValue(cadena, List.class);
            ids_serie.clear();
            nombre_serie.clear();
            for (int i = 0; i < arrayData.size(); i++) {
                //List<Object> arrayData2 = (List<Object>) arrayData.get(i);
                //List<Object> arrayData3 = (List<Object>) arrayData2.get(1);
                ids_serie.add((Integer) arrayData.get(i).get("id"));
                nombre_serie.add((String) arrayData.get(i).get("nombreCorto"));
            }

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

                costo_articulo = et_nuevo_cos.getText().toString();
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

            if(result.equals("[]")){
                //  Toast.makeText(getApplicationContext(),"Haz añadido todos los precios al articulo", Toast.LENGTH_SHORT).show();
                finish();
            }else{
              //  convertirDatos10(result);
                // System.out.println("onPostExecute 3: " + result);

            }

        }
    }


    public static String POST10(String url) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("costo", String.valueOf(costo_articulo));
        map.put("idArticulo", id_articulo);
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


    public void convertirDatos10(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HashMap> arrayData = mapper.readValue(cadena, List.class);
            ids_serie.clear();
            nombre_serie.clear();
            for (int i = 0; i < arrayData.size(); i++) {
                //List<Object> arrayData2 = (List<Object>) arrayData.get(i);
                //List<Object> arrayData3 = (List<Object>) arrayData2.get(1);
                ids_serie.add((Integer) arrayData.get(i).get("id"));
                nombre_serie.add((String) arrayData.get(i).get("nombreCorto"));
            }

            alertCostoArticulo();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    public void convertirDatos4(String cadena) {
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

    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST8(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("El resultado es: " + result);
            //  impuesto = Double.parseDouble(result);

            convertirDatos4(result);

        }
    }


    public String POST8(String url) {



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
