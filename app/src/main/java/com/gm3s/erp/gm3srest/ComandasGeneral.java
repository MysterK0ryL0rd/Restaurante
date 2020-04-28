package com.gm3s.erp.gm3srest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.gm3s.erp.gm3srest.View.LogIn;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComandasGeneral extends AppCompatActivity {
    String tipo_documento = "";
    TableLayout codigos;
    int selected = 0;
    String id = "";
    int temp;
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;
    LinkedList<HashMap> productos_list = new LinkedList();
    TableLayout prices;
    List<String> nombre_cliente = new ArrayList<String>();
    List<Integer> direccion_cliente = new ArrayList<Integer>();
    List<String> rfc_cliente = new ArrayList<String>();
    List<Integer> id_cliente = new ArrayList<Integer>();
    List<String> nombre_agente = new ArrayList<String>();
    List<String> id_agente = new ArrayList<String>();
    List<Integer> id_moneda = new ArrayList<Integer>();
    String user;
    Map<Integer,Articulo> mapa_articulos = new HashMap<>();
    static Double total_tmp=0.0;
    TextView total;
    static Double impuesto = 0.0;
    boolean esCaja=false;

    private static boolean validacion;


    static private String laSerie = "";
    static private String elCliente = "";
    static private String laBodega = "";
    private String elAgente = "";
    private Integer laDireccion = 0;
    Button escaner_btn_cliente;
    static List<String> tmp = new ArrayList<>();
    Button escaner_btn_fpago;
    Button escaner_btn_serie;




    List<CheckBox> chbx_selected = new ArrayList<>();
    static EditText corto, nombre, sku;


    int cantidad_articulos = 0;
    int counter = 0;
    int first_time = 0;
    static String categoria_busqueda= "nombreCorto";
    boolean formas_estado = false;
    static BigDecimal temporal_resta = new BigDecimal(0.0);
    //Double temporal_resta = 0.0;
    List<Boolean> formas_check =  new ArrayList<Boolean>();
    List<String>  formas_et = new ArrayList<String>();
    static BigDecimal total_temp = new BigDecimal(0.0);
    static LinkedList<HashMap> formasPago_tmp = new LinkedList();
    List<HashMap> formasPago = new ArrayList<HashMap>();
    static boolean editar_precio_permiso  = false;
    static boolean editar_descuento_permiso  = false;
    Integer laMoneda = 0;
    Button guardar,guardarSeparado,cancelar;
    String idDocumento,idMesa;
    String infoDocumento;
    String informacion = "";


    Map<String,String> info = new HashMap<>();
    List<Map<String,String>> orden = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comandas_general);
        pc = new PersistentCookieStore(this);



        info.put("cliente", "");
        info.put("cliente_id","" );
        info.put("agente","" );
        info.put("agente_id", "");
        info.put("serie", "");
        info.put("serie_id", "");
        info.put("f_pago","" );
        info.put("f_pago_id", "");
        info.put("fecha","" );
        info.put("folio", "");
        info.put("subtotal", "");
        info.put("impuestos","" );
        info.put("total", "");
        info.put("no_art","" );

        final Intent intent = getIntent();

        idDocumento = (String)intent.getSerializableExtra("idDocumento");
        idMesa = (String)intent.getSerializableExtra("idMesa");
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");
        user = (String)intent.getSerializableExtra("user");
        impuesto = Double.parseDouble((String)intent.getSerializableExtra("impuesto"));


        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();
        codigos.removeAllViews();




        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        System.out.println("1idDocumento "+idDocumento);


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());


        if (intent.getSerializableExtra("esCaja") != null) {
            esCaja=true;
            informacion = (String) intent.getSerializableExtra("informacion");
            System.out.println("informacion " + informacion);
            String[] parts = informacion.split("producto=");
            String[] parts2 = parts[1].substring(0, parts[1].length() - 34).split("\\}");
            orden.clear();

            ObjectMapper mapper = new ObjectMapper();
            Map totalisimo  = null;
            try {
                totalisimo = mapper.readValue(informacion.toString(), HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String, Object> myMap = new HashMap<String, Object>();
            String s = (String)totalisimo.get("map2");
            String[] pairs2 = s.substring(2, s.length() - 3).split(", ");
            for (int i=0;i<pairs2.length;i++) {
                String pair = pairs2[i];
                String[] keyValue = pair.split("=");
                myMap.put(keyValue[0], keyValue[1]);
            }
            impuesto = Double.parseDouble(myMap.get("impuesto").toString());

            for (int i = 0; i < parts2.length; i++) {
                if (i == 0) {
                    parts2[i] = parts2[i].substring(2);
                    System.out.println("0" + parts2[i]);
                    String[] info = parts2[i].split("\\, ");
                    Map<String,String> articulos = new HashMap<>();
                    for (String tmp : info) {
                        String[] tmp2 = tmp.split("=");
                        articulos.put(tmp2[0], tmp2[1]);

                    }
                    orden.add(articulos);
                }
                if (i > 0) {
                    parts2[i] = parts2[i].substring(3);
                    System.out.println("i" + parts2[i]);
                    String[] info = parts2[i].split("\\, ");
                    Map<String,String> articulos = new HashMap<>();
                    for (String tmp : info) {
                        String[] tmp2 = tmp.split("=");
                        articulos.put(tmp2[0], tmp2[1]);
                        System.out.println(tmp2[0] + " " + tmp2[1]);
                       // orden.add(articulos);
                    }
                    orden.add(articulos);
                }
            }
            for(int i=0; i<orden.size(); i++){
                System.out.println("lista " + orden.get(i));
            }

            crearTablaArticulos();
        }


        if(!idDocumento.equals("null")){
            System.out.println("aqui");
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/documento/obtener/" + tipo_documento + "/" + idDocumento + "/");
        }else{



        }

        guardar = (Button) findViewById(R.id.guardar_pedido);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    alertReferencias();
                    System.out.println("2idDocumento " + idDocumento);
                    guardar.setEnabled(false);
                   // HttpAsyncTask3 a = new HttpAsyncTask3();
                   // a.execute(server + "/medialuna/spring/documento/pedidoRemision/app/" + idDocumento);


            }
        });


        cancelar = (Button) findViewById(R.id.btn_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.getSerializableExtra("esCaja")!=null){
                    Intent intent = new Intent(ComandasGeneral.this, MenuCajas.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    finish();

                }
                else{

                    Intent intent = new Intent(ComandasGeneral.this, MenuMesas.class);
                    intent.putExtra("user", user);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    finish();
                }


            }
        });


        guardarSeparado = (Button) findViewById(R.id.guardar_pedido_separado);
        guardarSeparado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarSeparado.setEnabled(false);
                HttpAsyncTask3 a = new HttpAsyncTask3();
                a.execute(server + "/medialuna/spring/documento/pedidoRemisionporCliente/app/"+idDocumento);
            }
        });


        if(intent.getSerializableExtra("esCaja")!=null) {
            esCaja=true;
           guardar.setText("CONTINUAR");
            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Entro");

                    alertReferencias();

                  //  HttpAsyncTask9 asynk = new HttpAsyncTask9();
                   // asynk.execute(server + "/medialuna/spring/documento/crear/app");

                    /*Intent intent = new Intent(getApplicationContext(), MenuCajas.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.putExtra("menu_tiempos", "true");
                    getApplicationContext().startActivity(intent);
                    finish();*/



                }
            });

            guardarSeparado.setVisibility(View.GONE);
        }



        HttpAsyncTask12 b = new HttpAsyncTask12();
        b.execute(server + "/medialuna/spring/listar/catalogo/1403/");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.salir) {
            Toast.makeText(getApplicationContext(),"Saliendo",Toast.LENGTH_SHORT).show();
            finish();
            pc.clear();
            Intent localIntent = new Intent(this.getApplicationContext(), LogIn.class);
            startActivity(localIntent);
        }
        if (id == R.id.atras) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == 300 && requestCode == 1) {
            Intent intent2 = new Intent();
            intent2.setAction(Intent.ACTION_SEND);
            intent2.setType("text/plain");
            File f = new File(Environment.getExternalStorageDirectory(), "Download/T"+id+".txt");
            intent2.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));


            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent2, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;


                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth") || packageName.equals("com.mediatek.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }

                }

                if (!found) {
                    Toast.makeText(this, "Bluetooth haven been found", Toast.LENGTH_LONG).show();

                } else {
                    intent2.setClassName(packageName, className);
                    startActivity(intent2);
                    //borrarFile(f);
                }
            }

            else{

                Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();

            }


        }




    }








    public void crearTablaArticulos() {//esta si
         total_tmp=0.0;

        TableRow tr1 = new TableRow(this);
        TextView c5111 = new TextView(this);
        c5111.setTextColor(Color.parseColor("#373737"));
        c5111.setText("");
        c5111.setTypeface(null, Typeface.BOLD);
        c5111.setGravity(Gravity.CENTER);
        c5111.setTextSize(25);
        tr1.addView(c5111);

        TextView c511 = new TextView(this);
        c511.setTextColor(Color.parseColor("#373737"));
        if(informacion.equals(""))
            c511.setText("MESA " + idMesa);
        else
            c511.setText("CAJA " + idMesa);
        c511.setTypeface(null, Typeface.BOLD);
        c511.setGravity(Gravity.CENTER);
        c511.setTextSize(25);
        tr1.addView(c511);
        codigos.addView(tr1, 0); //unica

        for (int j = 0; j < orden.size(); j++) {
            TableRow tr = new TableRow(this);

            //final Articulo art = lista_art.get(j);

            final Map<String,String> art =orden.get(j);
            System.out.println(j + " " + art.get("descripcion").toString());

            final TextView c00 = new TextView(this);
     //       c00.setInputType(InputType.TYPE_CLASS_NUMBER);
            c00.setText(art.get("cantidad").toString());
            c00.setGravity(Gravity.RIGHT);
            c00.setTextSize(15);
           // c00.setTextColor(Color.parseColor("#f0f0f0"));
            //c00.setBackgroundColor(Color.parseColor("#373737"));
            cantidad_articulos = cantidad_articulos + 1;
            final String auxc = c00.getText().toString();


            TextView c5 = new TextView(this);
          //  c5.setTextColor(Color.parseColor("#373737"));
            c5.setText(art.get("descripcion").toString());
            c5.setGravity(Gravity.CENTER);
            c5.setTextSize(15);
          //  c5.setBackgroundColor(Color.parseColor("#373737"));

            final EditText c51 = new EditText(this);
            c51.setText(art.get("precio").toString());
            c51.setGravity(Gravity.RIGHT);
            c51.setTextSize(15);
            c51.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if(editar_descuento_permiso==false){
                c51.setEnabled(false);
                c51.setFocusable(false);
            }



            final TextView c2_1 = new TextView(this);
            c2_1.setText(art.get("total").toString());
            c2_1.setGravity(Gravity.LEFT);
            c2_1.setTextSize(15);
           // c2_1.setTextColor(Color.parseColor("#3D6AB3"));
          //  c2_1.setTypeface(null, Typeface.BOLD);
            total_tmp= Helper.formatDouble(total_tmp) + Double.parseDouble(art.get("total"));








            tr.addView(c00);
            tr.addView(c5);

            tr.addView(c51);
            tr.addView(c2_1);






          /*  if (Build.VERSION.SDK_INT >= 16) {
                if ((counter % 2) == 0) {
                    tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                } else {
                    tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                }
            }*/
            System.out.println(" ----------------------- AGREGANDO ---------------------------------------- " + Helper.formatDouble(impuesto));
            codigos.addView(tr, j+1); //unica
        }

        List<String> totales = new ArrayList<>();
        totales.add(" $ " + Helper.formatDouble(total_tmp));
        totales.add(" $ "+Helper.formatDouble(total_tmp * (Helper.formatDouble(impuesto)/100)));
        totales.add(" $ " + Helper.formatDouble(total_tmp +  (Helper.formatDouble(total_tmp) * (Helper.formatDouble(impuesto)/100))));;
        total_tmp = Helper.formatDouble(total_tmp) +  (Helper.formatDouble(total_tmp) * (Helper.formatDouble(impuesto)/100));

        for(int i=0; i<3; i++) {
            TableRow tra = new TableRow(this);
            TextView a = new TextView(this);
            a.setTextColor(Color.parseColor("#373737"));
            a.setText("");
            a.setTypeface(null, Typeface.BOLD);
            a.setGravity(Gravity.CENTER);
            a.setTextSize(25);
            tra.addView(a);

            TextView b = new TextView(this);
            b.setTextColor(Color.parseColor("#373737"));
            b.setText("");
            b.setTypeface(null, Typeface.BOLD);
            b.setGravity(Gravity.CENTER);
            b.setTextSize(25);
            tra.addView(b);

            TextView c = new TextView(this);
            c.setTextColor(Color.parseColor("#373737"));
            c.setText("");
            c.setTypeface(null, Typeface.BOLD);
            c.setGravity(Gravity.CENTER);
            c.setTextSize(25);
            tra.addView(c);

            TextView d = new TextView(this);
            d.setTextColor(Color.parseColor("#3D6AB3"));
            d.setText(totales.get(i));
            d.setTypeface(null, Typeface.BOLD);
            d.setGravity(Gravity.LEFT);
            d.setTextSize(15);
            tra.addView(d);
            codigos.addView(tra, orden.size() + (i+1)); //unica

        }



    }

    private void setText(final TextView text, final String value) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
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
            System.out.println("EL resultado es: " + result);
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



        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("tipoEntidad", "pedidocliente");



        String objectStr = JSONValue.toJSONString(map);


        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");


            System.out.println(" 3 Lo que se envia : " + objectStr.toString());
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

            List<Map<String,Object>> arrayData1 = mapper.readValue(cadena, List.class);

            Map<String,Object> documento = arrayData1.get(0);

            List<Object> infopartidas= (List)documento.get("partidas");
            List<Map<String,Object>> partidas= (List)infopartidas.get(1);
            System.out.println("Impresion " + partidas.size());
            orden.clear();
           for(int i=0; i<partidas.size(); i++){
               Map<String,Object> articulo= (Map)partidas.get(i).get("articulo");

               Map<String,String> orden_aux = new HashMap<>();
               orden_aux.put("cantidad",partidas.get(i).get("cantidad").toString());
               orden_aux.put("descripcion",articulo.get("descripción").toString());
               orden_aux.put("precio", partidas.get(i).get("precio").toString());
               orden_aux.put("total", Double.toString(Double.parseDouble(partidas.get(i).get("cantidad").toString()) * Double.parseDouble(partidas.get(i).get("precio").toString())));
               orden.add(orden_aux);


           }
            crearTablaArticulos();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //String fpago = escaner_etx_fpago.getText().toString();
        // savedInstanceState.putString("Cliente", escaner_txt_cliente.getText().toString());
        // savedInstanceState.putString("Fpago", fpago);
        // savedInstanceState.putString("Serie", escaner_txt_serie.getText().toString());
        // savedInstanceState.putString("Agente", escaner_txt_agente.getText().toString());
        //  savedInstanceState.putString("Fecha", escaner_fecha.getText().toString());
        //  savedInstanceState.putString("Folio", escaner_folio.getText().toString());
        //  savedInstanceState.putString("Noart", escaner_txt_no_art.getText().toString());
       // savedInstanceState.putString("Subtotal", subtotal.getText().toString());
//        savedInstanceState.putString("Impuestos", impuestos.getText().toString());
//        savedInstanceState.putString("Total", total.getText().toString());

        savedInstanceState.putString("IdSerie", laSerie);
        savedInstanceState.putString("IdCliente", elCliente);
        savedInstanceState.putString("IdAgente", elAgente);
        savedInstanceState.putString("IdDireccion", laDireccion.toString());
        savedInstanceState.putInt("Counter", counter);

        first_time = 1;
        savedInstanceState.putInt("Ftime", first_time);
        //savedInstanceState.putParcelable("Codigos", codigos);
        //savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
        //   final TableLayout codigos2 = (TableLayout) findViewById(R.id.tabla_codigos);
        //  codigos.setStretchAllColumns(true);
        // codigos.bringToFront();

        List<List<String>> contenido = new ArrayList<>();

       /* for (int i = 1; i < codigos.getChildCount(); i++) {

            TableRow row = (TableRow) codigos.getChildAt(i);
            EditText t1 = (EditText) row.getChildAt(0);
            TextView t2 = (TextView) row.getChildAt(1);
            TextView t3 = (TextView) row.getChildAt(2);
            TextView t4 = (TextView) row.getChildAt(3);
            TextView t5 = (TextView) row.getChildAt(4);

            List<String> contenido_tmp = new ArrayList<>();
            contenido_tmp.add(t1.getText().toString());
            contenido_tmp.add(t2.getText().toString());
            contenido_tmp.add(t3.getText().toString());
            contenido_tmp.add(t4.getText().toString());
            contenido_tmp.add(t5.getText().toString());

            contenido.add(contenido_tmp);
        }*/


        HashMap<String, Object> contenido_map = new HashMap<String, Object>();

        contenido_map.put("productos_list", productos_list);
        System.out.println("On Save: productos_list: " + productos_list);
        savedInstanceState.putSerializable("contenido", contenido_map);
        // System.out.println("On Save: escaner_etx_fpago: " + fpago);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        //savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
        super.onRestoreInstanceState(savedInstanceState);


        laSerie = savedInstanceState.getString("IdSerie");
        elCliente = savedInstanceState.getString("IdCliente");
        elAgente = savedInstanceState.getString("IdAgente");
        laDireccion = Integer.parseInt(savedInstanceState.getString("IdDireccion"));

        first_time = savedInstanceState.getInt("Ftime");
        counter = savedInstanceState.getInt("Counter");
        HashMap<String, Object> contenido_map = (HashMap<String, Object>) savedInstanceState.getSerializable("contenido");



        productos_list = (LinkedList<HashMap>) contenido_map.get("productos_list");
        System.out.println("On Restore producots_list: " + productos_list);
        //  System.out.println("On Restore: escaner_etx_fpago: " + escaner_etx_fpago.getText().toString());

    }



    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST3(urls[0]);

        }

        @Override
        protected void onPostExecute(String result) {

            HttpAsyncTask2 a = new HttpAsyncTask2();
            a.execute(server + "/medialuna/spring/app/cambiarEstadoMesa/" + idMesa + "/0");



        }
    }


    public static String POST3(String url) {
           if(formasPago_tmp.size()==1){
            formasPago_tmp.get(0).put("valor", Helper.formatDouble(total_tmp).toString());
        }

        HashMap map3 = new HashMap();
        map3.put("formasPago", formasPago_tmp);
        map3.put("@class", ArrayList.class.getName());


        Map totalisimo = new HashMap();
        totalisimo.put("map3",map3.toString());
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
//daniel_dlb@yahoo.com

        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(ComandasGeneral.this, MenuMesas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("user", user);
            getApplicationContext().startActivity(intent);
            finish();

        //    Intent intent = new Intent(ComandasGeneral.this, MenuMesas.class);
         //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    getApplicationContext().startActivity(intent);
         //   finish();

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








    private class HttpAsyncTask9 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST9(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Documento creado" + result, Toast.LENGTH_LONG).show();

            //  Intent intent = getIntent();
            //  finish();
            //  startActivity(intent);

            id = convertirDatosDocumento(result);
            System.out.println("Por aqui va el id: " + id);



         //   HttpAsyncTask14 a = new HttpAsyncTask14();
         //   a.execute(server + "/medialuna/spring/app/asignarReferenciaCaja/" + idMesa.toString() + "/" + id);



        }
    }


    public String POST9(String url)  {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");


            if(formasPago_tmp.size()==1){
                formasPago_tmp.get(0).put("valor", Helper.formatDouble(total_tmp).toString());
            }
            ObjectMapper mapper = new ObjectMapper();
            Map totalisimo  = mapper.readValue(informacion.toString(), HashMap.class);
            Map<String, Object> myMap = new HashMap<String, Object>();
            String s = (String)totalisimo.get("map3");
            System.out.println("s " + s);
            String[] pairs = s.split("formasPago=");
            String[] pairs2 = pairs[1].substring(2,pairs[1].length()-3).split(", ");
            for (int i=0;i<pairs2.length;i++) {
                String pair = pairs2[i];
                String[] keyValue = pair.split("=");
                myMap.put(keyValue[0], keyValue[1]);
            }

            System.out.println("myMap " + myMap);
            //HashMap map3 = (HashMap) totalisimo.get("map3");
           myMap.put("formasPago",formasPago_tmp);
            System.out.println("totalisimo " + totalisimo.keySet());


            HashMap map3 = new HashMap();
            map3.put("formasPago", formasPago_tmp);
            map3.put("@class", ArrayList.class.getName());

            totalisimo.put("map3",map3.toString());
            JSONObject jsonOBJECT1 = new JSONObject(totalisimo);
            StringEntity params = new StringEntity(jsonOBJECT1.toString());
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);

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

        Intent intent = new Intent(getApplicationContext(), MenuCajas.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.putExtra("menu_tiempos", "true");
                    getApplicationContext().startActivity(intent);
                    finish();
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

            Intent intent = new Intent(ComandasGeneral.this, MenuCajas.class);
            intent.putExtra("user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

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

    @SuppressLint("SetTextI18n")
    public void alertReferencias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComandasGeneral.this);

        LayoutInflater inflater = ComandasGeneral.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.referencias2, null);
        builder.setView(dialogView);

        final TableLayout tabla_efectivo = (TableLayout) dialogView.findViewById(R.id.tabla_efectivo);
        final TableLayout tabla_credito = (TableLayout) dialogView.findViewById(R.id.tabla_credito);

        final TextView total_tv = (TextView) dialogView.findViewById(R.id.total_textView);
        total_tv.setText(Helper.formatDouble(total_tmp).toString());

        final ArrayList < CheckBox > check_array_formas2 = new ArrayList < > ();
        final ArrayList < EditText > etx_array_formas2 = new ArrayList < > ();

        for (int i = 0; i < formasPago.size(); i++) {


            TableRow tr = new TableRow(this);

            final CheckBox c00 = new CheckBox(this);
            c00.setText(formasPago.get(i).get("nombre").toString());
            c00.setTag("referencias_cbx_" + formasPago.get(i).get("nombre").toString().toLowerCase());
            c00.setTextColor(Color.WHITE);
            check_array_formas2.add(c00);


            final EditText c51 = new EditText(this);
            c51.setTag("referencias_etx_" + formasPago.get(i).get("nombre").toString().toLowerCase());
            etx_array_formas2.add(c51);
            if (Build.VERSION.SDK_INT >= 16) {
                c51.setBackground(getResources().getDrawable(R.drawable.edit_text_style3));
            }
            tr.addView(c00);
            tr.addView(c51);
            if (formasPago.get(i).get("nombre").toString().toLowerCase().equals("efectivo")) {
                c51.setText(Helper.formatDouble(total_tmp).toString());
                c00.setChecked(true);
            }
            if (formasPago.get(i).get("credito")!=null && formasPago.get(i).get("credito").toString().equals("true")) {
                tabla_credito.addView(tr);
            } else {
                tabla_efectivo.addView(tr);
            }
        }
            //temporal_resta =
        temporal_resta = BigDecimal.valueOf(0.0);
        for (int i = 0; i < check_array_formas2.size(); i++) {
            final int aux = i;
            check_array_formas2.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        temporal_resta = BigDecimal.valueOf(0.0);
                        for (int j = 0; j < etx_array_formas2.size(); j++) {
                            if ((!etx_array_formas2.get(j).isEnabled()) || (etx_array_formas2.get(j).getText().toString().equals("")) || (!check_array_formas2.get(j).isChecked())) {} else {
                                temporal_resta = temporal_resta.add(new BigDecimal(etx_array_formas2.get(j).getText().toString()));
                            }
                        }
                        Toast.makeText(getApplicationContext(), " Temporal Resta " + Helper.formatBigDec(temporal_resta), Toast.LENGTH_SHORT).show();

                        etx_array_formas2.get(aux).setText(String.valueOf((new BigDecimal(Helper.formatDouble(total_tmp).toString())).subtract(Helper.formatBigDec(temporal_resta))));
                        etx_array_formas2.get(aux).setEnabled(true);
                        // etx_array_formas2.get(aux).setFocusable(true);
                        etx_array_formas2.get(aux).setClickable(true);
                    } else {
                        etx_array_formas2.get(aux).setText("");
                    }

                }
            });
        }
        Button referencias_btn_cancelar = (Button) dialogView.findViewById(R.id.referencias_btn_cancelar);


        Button referencias_btn_aceptar = (Button) dialogView.findViewById(R.id.referencias_btn_aceptar);

        final AlertDialog dialog = builder.create();


        if (formas_estado) {
            for (int i = 0; i < check_array_formas2.size(); i++) {
                check_array_formas2.get(i).setChecked(formas_check.get(i));
                etx_array_formas2.get(i).setText(formas_et.get(i));
            }
        }
        referencias_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_temp = BigDecimal.valueOf(0.0);
                formasPago_tmp.clear();
                formas_check.clear();
                formas_et.clear();
                for (int i = 0; i < check_array_formas2.size(); i++) {
                    System.out.println("      ----> " + 1);
                    System.out.println("CheckBox " + "Elemento " + i + "   estado " + check_array_formas2.get(i).isChecked());
                    formas_check.add(check_array_formas2.get(i).isChecked());
                    formas_et.add(etx_array_formas2.get(i).getText().toString());
                    if (check_array_formas2.get(i).isChecked()) {
                        System.out.println("      ----> " + 2);
                        for (int j = 0; j < formasPago.size(); j++) {
                            System.out.println("      ----> " + 3 + j);

                            System.out.println("-->" + formasPago.get(j).get("nombre").toString() + "        " + check_array_formas2.get(i).getText().toString().toUpperCase());
                            if (formasPago.get(j).get("nombre").toString().equals(check_array_formas2.get(i).getText().toString().toUpperCase())) {
                                System.out.println("      ----> " + 4);
                                HashMap tmp = formasPago.get(j);
                                System.out.println("CheckBox " + "Edit TExt" + etx_array_formas2.get(i).getText().toString());
                                tmp.put("valor", etx_array_formas2.get(i).getText().toString());
                                formasPago_tmp.add(tmp);

                                System.out.println("CheckBox " + "Valor: " + etx_array_formas2.get(i).getText().toString());
                                total_temp = Helper.formatBigDec(total_temp).add(new BigDecimal(etx_array_formas2.get(i).getText().toString()));
                            }
                        }
                    }
                }
                if (verificarCoincidenciaPrecios()) {
                    formas_estado = true;
                    dialog.cancel();
                } else {
                    System.out.println("      ----> " + Helper.formatBigDec(total_temp) + "   ----> " + Helper.formatDouble(total_tmp).toString());
                    Toast.makeText(getApplicationContext(), "Favor de verificar los totales", Toast.LENGTH_SHORT).show();
                }


                if(esCaja){
                HttpAsyncTask9 asynk = new HttpAsyncTask9();
                asynk.execute(server + "/medialuna/spring/documento/crear/app");}
                else{
                    HttpAsyncTask3 a = new HttpAsyncTask3();
                    a.execute(server + "/medialuna/spring/documento/pedidoRemision/app/" + idDocumento);

                }
            }



        });
        referencias_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    @SuppressLint("DefaultLocale")
    public boolean verificarCoincidenciaPrecios() {
        boolean validar = false;
        total_tmp = Double.valueOf(String.format( "%.2f", Helper.formatDouble(total_tmp)));
        if (Helper.formatBigDec(total_temp).toString().equals(Helper.formatDouble(total_tmp).toString()))
            validar = true;
        return validar;
    }



    public void convertirDatosFormasPago(String cadena) {

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




        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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

}
