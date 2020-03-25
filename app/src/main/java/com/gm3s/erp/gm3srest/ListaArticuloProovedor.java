package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.MainActivity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.message.BasicHeader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ListaArticuloProovedor extends AppCompatActivity {

    private static PersistentCookieStore pc;
    Button escaner_btn_proo, btn_lista_buscar, btn_lista_borrar;
    int temp = 0;
    static List<String> nombres_proovedores = new ArrayList<String>();
    static List<Integer> ids_proovedores = new ArrayList<Integer>();
    static List<Integer> ids_proovedores2 = new ArrayList<Integer>();
    private SharedPreference sharedPreference;
    String server = "";
    static int id_proovedor;
    static EditText escaner_etx_pro;
    List<HashMap> lista_articulos = new LinkedList<>();
    TableLayout codigos;
    int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_articulo_proovedor);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        escaner_etx_pro = (EditText) findViewById(R.id.escaner_etx_pro);
        escaner_etx_pro.setSelectAllOnFocus(true);

        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Lista Articulos por Proveedor");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaArticuloProovedor.this, MainActivity.class);
                startActivity(intent);
            }
        });


        /*escaner_btn_proo = (Button) findViewById(R.id.escaner_btn_proo);
        escaner_btn_proo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask a = new HttpAsyncTask();
                a.execute(server + "/medialuna/spring/util/genericos/listarPag/");
            }
        });*/



        btn_lista_borrar = (Button) findViewById(R.id.btn_lista_borrar);
        btn_lista_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            escaner_etx_pro.setText("");


            }
        });
        btn_lista_buscar = (Button) findViewById(R.id.btn_lista_buscar);
        btn_lista_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask1 a = new HttpAsyncTask1();
                a.execute(server + "/medialuna/spring/listar/registros/autocomplete");



            }
        });
    }


    public void alertProovedor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListaArticuloProovedor.this);
        builder.setTitle("ProveEdores");

        builder.setSingleChoiceItems(nombres_proovedores.toArray(new String[nombres_proovedores.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                temp = which;


            }

        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) { //<--------------------------------------BUG

                selected = temp;

                System.out.println("Selecred:--> " + selected);
                System.out.println("Temp:--> " + temp);
                System.out.println("Wich:--> " + which);

                System.out.println("ids_proovedores:--> " + ids_proovedores.size());
                System.out.println("nombres_proovedores:--> " + nombres_proovedores.size());

                id_proovedor = ids_proovedores.get(selected);//PELVER  KOH
                escaner_etx_pro.setText(nombres_proovedores.get(selected));
                HttpAsyncTask2 a = new HttpAsyncTask2();
                System.out.println("Id proovedor: " + id_proovedor);
                a.execute(server + "/medialuna/spring/costoProveedor/consultarCostos?idArticulo=0&idProveedor="+id_proovedor);
                dialog.cancel();
                temp = 0;
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
           System.out.println("Proovedores: " +  result);
            convertirDatos(result);
        }
    }


    public static String POST(String url) {

        Map registro = new HashMap();
        registro.put("@class", "mx.mgsoftware.erp.entidades.ProveedorImpl");
        registro.put("id", "");

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        Map pageOrden = new LinkedHashMap();
        pageOrden.put("@class", LinkedHashMap.class.getName());

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("activos", true);
        map.put("registro", registro);
        map.put("pagerFiltros", pagerFiltros);
        map.put("pageOrden", pageOrden);
        map.put("maxResults", 200);
        map.put("firstResult", 0);

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

    public void convertirDatos(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HashMap> arrayData = mapper.readValue(cadena, List.class);
            ids_proovedores.clear();
            nombres_proovedores.clear();
            for (int i = 0; i < arrayData.size(); i++) {
                ids_proovedores.add((Integer) arrayData.get(i).get("id"));
                nombres_proovedores.add((String) arrayData.get(i).get("nombre"));
            }
            alertProovedor();
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
            System.out.println("Proovedores: here " +  result);
            convertirDatos2(result);
        }
    }


    public static String POST2(String url) {



        HashMap<String, Object> map = null;


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
            System.out.println("Lo que se envia : " + objectStr.toString());
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



    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST1(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("Proovedores: " +  result);
            convertirDatos1(result);

        }
    }




    public static String POST1(String url) {

        Map nombre = new HashMap();
        nombre.put("@class", HashMap.class.getCanonicalName());
        nombre.put("claseEntidad", "mx.mgsoftware.erp.entidades.ProveedorImpl");
        nombre.put("maxResults", 20);
        nombre.put("cadenaBusqueda", escaner_etx_pro.getText().toString());







        String objectStr = JSONValue.toJSONString(nombre);

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
            System.out.println("Params: " + EntityUtils.toString(params));







           /* HttpURLConnection urlConnection = null;
            URL url2 = new URL(url);
            urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.setDoOutput(true);


//            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept", "application/json; text/javascript");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            String cookieString = "Name=" +  pc.getCookies().get(0).getName() + ";" +
                                  " Value=" + pc.getCookies().get(0).getValue() + ";" ;
            urlConnection.setRequestProperty("Cookie", cookieString);*/

           // inputStream = urlConnection.getInputStream();

            /*String parameters = EntityUtils.toString(params);
            urlConnection.setFixedLengthStreamingMode(parameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parameters);
            out.close();
*/



           // urlConnection.connect();





            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
               // inputStream = urlConnection.getInputStream();
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



    public void convertirDatos1(String cadena) {
        System.out.println(" La cadena de proveedores: " + cadena);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<LinkedHashMap> arrayData0 = mapper.readValue(cadena, List.class);

            ids_proovedores.clear();
            nombres_proovedores.clear();
            for (int i = 0; i < arrayData0.size(); i++) {
                LinkedHashMap provedoor = arrayData0.get(i);
                   ids_proovedores.add((Integer) provedoor.get("0"));
                    nombres_proovedores.add((String) provedoor.get("1"));
                }
            if(!ids_proovedores.isEmpty()) {
              alertProovedor();
            }
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }



    public void convertirDatos2(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HashMap> arrayData0 = mapper.readValue(cadena, List.class);

            lista_articulos.clear();
            for (int i = 0; i < arrayData0.size(); i++) {
                HashMap<String, String> articulo = new HashMap<>();
                articulo.put("id", arrayData0.get(i).get("id").toString());
                articulo.put("costo", arrayData0.get(i).get("costo").toString().substring(0, arrayData0.get(i).get("costo").toString().indexOf('.')+2));
                //System.out.println("Aqui esta el punto " + arrayData0.get(i).get("costo").toString().indexOf('.'));


                if(arrayData0.get(i).get("skuArticulo") == null){
                articulo.put("skuArticulo", "");}
                else{
                    articulo.put("skuArticulo", arrayData0.get(i).get("skuArticulo").toString());
                }
                articulo.put("nombreCortoArticulo", arrayData0.get(i).get("nombreCortoArticulo").toString());
                articulo.put("utilidad", arrayData0.get(i).get("utilidad").toString());
                articulo.put("precioBase", arrayData0.get(i).get("precioBase").toString());
                lista_articulos.add(articulo);
            }
            if(!lista_articulos.isEmpty()) {
                crearTablaArticulos();
            }

            else{
                while (codigos.getChildCount() != 1) {
                    codigos.removeViewAt(codigos.getChildCount() - 1);
                }
                TableRow tr = new TableRow(this);
                TextView c1 = new TextView(this);
                c1.setText("No se encontraron resultados.");
                tr.addView(c1);
                tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                codigos.addView(tr); // unica
            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    public void crearTablaArticulos() {


        System.out.println("Current Views: " + codigos.getChildCount());

        while (codigos.getChildCount() != 1) {
            codigos.removeViewAt(codigos.getChildCount() - 1);
        }

        for (int j = 0; j < lista_articulos.size(); j++) {
            TableRow tr = new TableRow(this);

            TextView c1 = new TextView(this);
            c1.setText(lista_articulos.get(j).get("nombreCortoArticulo").toString());

            TextView c2 = new TextView(this);
            c2.setText(lista_articulos.get(j).get("costo").toString());

            TextView c3 = new TextView(this);
            c3.setText(lista_articulos.get(j).get("precioBase").toString());

            TextView c4 = new TextView(this);
            c4.setText(Helper.formatBigDec(new BigDecimal(lista_articulos.get(j).get("utilidad").toString())).toString());

            TextView c5 = new TextView(this);
            c5.setText(lista_articulos.get(j).get("skuArticulo").toString());


            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);
            tr.addView(c4);
            tr.addView(c5);


            if ((j % 2) == 0) {
                tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
            } else  {
                tr.setBackground(getResources().getDrawable(R.drawable.row_color));
            }

            codigos.addView(tr); // unica
        }

    }

}
