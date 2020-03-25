package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.Cliente;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.Serie;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ReporteVentasNetas extends AppCompatActivity {

    TableLayout prices;
    public static ImageView loader;
    private static PersistentCookieStore pc;
    private Button consultar, btn_serie, btn_agente, btn_sucursal, btn_articulo, btn_cliente;
    String server = "";
    private SharedPreference sharedPreference;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private SimpleDateFormat dateFormatter;
    long f_ini, f_fin;
    private static boolean validacion;
    RadioGroup radioGroup;
    int opc = 1;
    List<String> nombre_serie = new ArrayList<String>();
    List<String> id_serie = new ArrayList<String>();
    int temp;
    static EditText corto, nombre, sku;
    static ArrayList id_serie2 = new ArrayList();
    List<CheckBox> chbx_selected = new ArrayList<>();
    List<Object> articulos = new ArrayList<>();
    List<Object> agentes = new ArrayList<>();
    static List<Object> series = new ArrayList<>();
    List<Object> terceros = new ArrayList<>();
    int selected = 0;
    List<String> nombre_cliente = new ArrayList<String>();
    List<Integer> id_cliente = new ArrayList<Integer>();
    List<String> nombre_agente = new ArrayList<String>();
    List<String> id_agente = new ArrayList<String>();
    List<Integer> id_moneda = new ArrayList<Integer>();
    List<Integer> direccion_cliente = new ArrayList<Integer>();
    List<String> rfc_cliente = new ArrayList<String>();
    List<Cliente> listaClientes = new ArrayList<>();
    static String name;
    static private String elCliente = "";
    static private String laSerie = "";
    static private String laBodega = "";
    List<Articulo> lista_art = new ArrayList<Articulo>();

    static ArrayList serie_ids = new ArrayList();
    static ArrayList cliente_ids = new ArrayList();
    static ArrayList agente_ids = new ArrayList();
    static ArrayList articulo_ids = new ArrayList();
    static Map map = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_ventas_netas);
        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        pc = new PersistentCookieStore(this);
        prices = (TableLayout) findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        fromDateEtxt = (EditText) findViewById(R.id.tvDate);
        toDateEtxt = (EditText) findViewById(R.id.tvDate2);
        consultar = (Button) findViewById(R.id.consultar);
        final EditText[] a = {fromDateEtxt, toDateEtxt};
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        series.add(ArrayList.class.getName());


        map.put("agentes", 0);
        map.put("articulos", 0);
        map.put("series", 0);  //id Tercero
        map.put("terceros", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Reporte de Ventas Netas");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setDateTimeField();


        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"));
        c.setTimeInMillis(c.getTimeInMillis() - 21600000);
        f_fin = c.getTimeInMillis();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
        f_ini = c.getTimeInMillis();


        fromDateEtxt.setText(dateFormatter.format(f_ini));
        toDateEtxt.setText(dateFormatter.format(f_fin));


        fromDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                fromDatePickerDialog.show();

            }
        });

        toDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                toDatePickerDialog.show();

            }
        });


        btn_serie = (Button) findViewById(R.id.btn_serie);
        btn_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/listar/serie/");


            }
        });

        btn_agente = (Button) findViewById(R.id.btn_agente);
        btn_agente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/listar/agente/");


            }
        });


        btn_articulo = (Button) findViewById(R.id.btn_articulo);
        btn_articulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArticulo();


            }
        });

        btn_cliente = (Button) findViewById(R.id.btn_cliente);
        btn_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReporteVentasNetas.this);
                alertDialog.setTitle("Buscar Cliente");
                alertDialog.setMessage("Ingresa una palabra de busqueda");

                final EditText input = new EditText(ReporteVentasNetas.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                // alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                name = input.getText().toString();

                                HttpAsyncTask2 a = new HttpAsyncTask2();
                                a.execute(server + "/medialuna/spring/listar/pagina/tercero/cliente/");
                                dialog.cancel();
                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }

        });


        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Helper.isConnected3(ReporteVentasNetas.this)) {
                    if (fromDateEtxt.getText().toString().equals("") || toDateEtxt.getText().toString().equals("")) {
                        Toast.makeText(ReporteVentasNetas.this, "Favor de validar la(s) fecha(s)", Toast.LENGTH_LONG).show();
                    } else {
                        prices.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.VISIBLE);
                        System.out.println(" Las fechas son: " + f_ini + "   " + f_fin);
                        HttpAsyncTask a = new HttpAsyncTask();
                        //a.execute(server + "/medialuna/spring/documento/buscar/total/venta/serie/" + f_ini + "/" + f_fin);
                        a.execute(server + "/medialuna/spring/documento/buscar/total/ventasNetas/" + f_ini + "/" + f_fin + "/" + opc);
                    }

                } else {
                    Toast.makeText(ReporteVentasNetas.this, "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent(ReporteVentasNetas.this, LogIn.class);
                    startActivity(localIntent);
                }


            }
        });


        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radioButton1) {
                    opc = 1;
                } else if (checkedId == R.id.radioButton2) {
                    opc = 2;
                } else if (checkedId == R.id.radioButton3) {
                    opc = 3;
                } else if (checkedId == R.id.radioButton4) {
                    opc = 4;
                } else if (checkedId == R.id.radioButton5) {
                    opc = 5;
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
            //  Toast.makeText(getActivity(), "VALIDACION: " + validacion, Toast.LENGTH_LONG).show();

            System.out.println("El resultado es: " + result);

            if (result.equals("[]")) {
                loader.setVisibility(View.INVISIBLE);
            } else {
                prices.setVisibility(View.VISIBLE);
                convertirDatos3(result);
                loader.setVisibility(View.INVISIBLE);


            }
        }
    }


    public static String POST(String url) {



        if(serie_ids.size()>0){
            ArrayList tipo0 = new ArrayList();
            tipo0.add(ArrayList.class.getName());
            tipo0.add(serie_ids);
            map.put("series", tipo0);
        }

        if(cliente_ids.size()>0){
            ArrayList tipo0 = new ArrayList();
            tipo0.add(ArrayList.class.getName());
            tipo0.add(serie_ids);
            map.put("terceros", tipo0);
        }

        if(agente_ids.size()>0){
            ArrayList tipo0 = new ArrayList();
            tipo0.add(ArrayList.class.getName());
            tipo0.add(serie_ids);
            map.put("agentes", tipo0);
        }

        if(articulo_ids.size()>0){
            ArrayList tipo0 = new ArrayList();
            tipo0.add(ArrayList.class.getName());
            tipo0.add(serie_ids);
            map.put("articulos", tipo0);
        }




        map.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(map);

        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
           /* Map<String,Object> b = new HashMap();
            b.put("@class", Map.class);
            JSONObject obj = new JSONObject(b);
            ObjectMapper c = new ObjectMapper();
            String json2 = c.writeValueAsString(b);
            StringEntity params =new StringEntity(json2);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(params);*/

            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");

            StringEntity params = new StringEntity(jsonOBJECT1.toString());
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
                    System.out.println("\nNO se  pudo ejecutar la funcion");
                } else {
                    validacion = true;
                    System.out.println("\nFUncion ejecutada");
                    System.out.println("RESULTADO post: " + result);
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
            List<BigDecimal> cantidades = new ArrayList<>();
            cantidades.add(new BigDecimal(0));
            cantidades.add(new BigDecimal(0));
            cantidades.add(new BigDecimal(0));
            cantidades.add(new BigDecimal(0));
            cantidades.add(new BigDecimal(0));
            cantidades.add(new BigDecimal(0));


            prices.removeAllViews();
            String[] valores = {"Nombre", "Total Factura", "Total Remision", "Total Nota Credito", "Venta Total", "Total Piezas", "Total Servicios"};
            TableRow tr0 = new TableRow(ReporteVentasNetas.this);
            for (int i = 0; i < valores.length; i++) {
                TextView c00 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
                c00.setText(valores[i]);
                tr0.addView(c00);
            }
            prices.addView(tr0);

            for (int x = 0; x < arrayData.size(); x++) {
                Map mapa1 = (Map) arrayData.get(x);
                TableRow tr = new TableRow(ReporteVentasNetas.this);
                TextView c0 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                if (mapa1.get("nombre").toString().length() > 20) {
                    c0.setText((String) mapa1.get("nombre").toString().substring(0, 20));
                } else {
                    c0.setText((String) mapa1.get("nombre").toString());
                }


                TextView c1 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c1.setText(Helper.numberFormat(Helper.formatBigDec(new BigDecimal(mapa1.get("totalFactura").toString())).toString()));
                cantidades.set(0, cantidades.get(0).add(new BigDecimal(mapa1.get("totalFactura").toString())));

                TextView c2 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c2.setText(Helper.numberFormat(Helper.formatBigDec(new BigDecimal(mapa1.get("totalRemision").toString())).toString()));
                cantidades.set(1, cantidades.get(1).add(new BigDecimal(mapa1.get("totalRemision").toString())));

                TextView c3 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c3.setText(Helper.numberFormat(Helper.formatBigDec(new BigDecimal(mapa1.get("totalNotaCredito").toString())).toString()));
                cantidades.set(2, cantidades.get(2).add(new BigDecimal(mapa1.get("totalNotaCredito").toString())));

                TextView c4 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c4.setText(Helper.numberFormat(Helper.formatBigDec(new BigDecimal(mapa1.get("totalTotal").toString())).toString()));
                cantidades.set(3, cantidades.get(3).add(new BigDecimal(mapa1.get("totalTotal").toString())));

                TextView c5 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c5.setText(Helper.formatBigDec(new BigDecimal(mapa1.get("pzasTotal").toString())).toString());
                cantidades.set(4, cantidades.get(4).add(new BigDecimal(mapa1.get("pzasTotal").toString())));

                TextView c6 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c6.setText(Helper.formatBigDec(new BigDecimal(mapa1.get("serviciosTotal").toString())).toString());
                cantidades.set(5, cantidades.get(5).add(new BigDecimal(mapa1.get("serviciosTotal").toString())));


                tr.addView(c0);
                tr.addView(c1);
                tr.addView(c2);
                tr.addView(c3);
                tr.addView(c4);
                tr.addView(c5);
                tr.addView(c6);
                prices.addView(tr);

            }

            TableRow tr1 = new TableRow(ReporteVentasNetas.this);
            TextView c0 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
            c0.setText("");
            tr1.addView(c0);
            for (int i = 0; i < valores.length - 1; i++) {
                TextView c00 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
                c00.setText(Helper.numberFormat(Helper.formatBigDec(cantidades.get(i)).toString()));
                tr1.addView(c00);
            }
            prices.addView(tr1);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(ReporteVentasNetas.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("Fecha Inicial: " + dateFormatter.format(newDate.getTime()));
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth, 6, 0, 0);
                f_ini = newDate.getTimeInMillis();
                System.out.println("Inicial " + f_ini);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        toDatePickerDialog = new DatePickerDialog(ReporteVentasNetas.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth + 1, 6, -1, 0);
                f_fin = newDate.getTimeInMillis();
                System.out.println("Final " + f_fin);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST4(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            if (validacion) {
                convertirDatos4(result);
            } else {
                //  Toast.makeText(getActivity(), "Usuario no valido", Toast.LENGTH_LONG).show();


            }
        }
    }


    public static String POST4(String url) {
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


    public void convertirDatos4(String cadena) {
        System.out.println("La cadena es: " + cadena);

        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_serie.clear();
            id_serie.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                //HashMap<String, Object> bodega = (HashMap<String, Object>)mapa1.get("bodega");
                Serie serietmp = new Serie((Integer) mapa1.get("id"), (Integer) mapa1.get("códigoUsuario"), (String) mapa1.get("nombreCorto"), (String) mapa1.get("nombre"));
                nombre_serie.add(serietmp.getNombre());
                id_serie.add(String.valueOf(serietmp.getId()));


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
        AlertDialog.Builder builder = new AlertDialog.Builder(ReporteVentasNetas.this);
        builder.setTitle("Serie");
        boolean[] isSelectedArray = new boolean[nombre_serie.size()];
        for (int i = 0; i < nombre_serie.size(); i++) {
            isSelectedArray[i] = false;
        }


        builder.setMultiChoiceItems(nombre_serie.toArray(new String[nombre_serie.size()]), isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                temp = which;

                if (isChecked == true) {

                    id_serie2.add(Integer.parseInt(id_serie.get(temp)));
                    serie_ids.add(Integer.parseInt(id_serie.get(temp)));

                }

                if (isChecked == false) {
                    id_serie2.remove(id_serie.get(temp));
                    System.out.println("Aqui --------> ");

                }


            }

        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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



    private void build_popupAgente() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ReporteVentasNetas.this);
        builder.setTitle("Agente");
        boolean[] isSelectedArray = new boolean[nombre_agente.size()];
        for(int i=0; i<nombre_agente.size(); i++) {
            isSelectedArray[i] = false;
        }



        builder.setMultiChoiceItems(nombre_agente.toArray(new String[nombre_agente.size()]), isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                temp = which;

                if (isChecked == true) {

                    agente_ids.add(Integer.parseInt(id_serie.get(temp)));

                }

                if (isChecked == false) {
                    agente_ids.remove(Integer.parseInt(id_serie.get(temp)));
                    System.out.println("Aqui --------> ");

                }


            }

        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
        HashMap map = new HashMap();
        HashMap map1 = new HashMap();
        map1.put("@class", HashMap.class.getName());
        if (name.length() <= 1) {
        } else {
            map1.put("nombre", name);
        }
        ArrayList al = new ArrayList();
        al.add(ArrayList.class.getName());
        ArrayList al1 = new ArrayList();
        al1.add("ACTIVO");
        al.add(new JSONArray(al1));
        map1.put("estatus", new JSONArray(al));
        map.put("pagerFiltros", new JSONObject(map1));
        Map map2 = new LinkedHashMap();
        map2.put("@class", LinkedHashMap.class.getName());
        map.put("pagerOrden", new JSONObject(map2));
        map.put("firstResult", 0);
        map.put("maxResults", 10);
        map.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(map);


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
        System.out.println("Esta es la cadena: " + cadena.substring(0, 2000));
        System.out.println(cadena.substring(2000, 4000));
        System.out.println(cadena.substring(4000));
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            id_cliente.clear();
            nombre_cliente.clear();
            direccion_cliente.clear();
            rfc_cliente.clear();
            nombre_agente.clear();
            id_agente.clear();
            id_moneda.clear();

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                Cliente cliente_tmp = new Cliente();
                cliente_tmp.setId((Integer) mapa1.get("id"));
                cliente_tmp.setNombre((String) mapa1.get("nombre"));
                cliente_tmp.setRfc((String) mapa1.get("rfc"));
                cliente_tmp.setApellido_materno((String) mapa1.get("materno"));
                cliente_tmp.setApellido_paterno((String) mapa1.get("paterno"));
                cliente_tmp.setClasificacion((String) mapa1.get("tipoClasificacion"));
                cliente_tmp.setCurp((String) mapa1.get("curp"));
                cliente_tmp.setDescripcion((String) mapa1.get("descripción"));
                cliente_tmp.setDias_credito((Integer) mapa1.get("díasCrédito"));
                cliente_tmp.setEmail((String) mapa1.get("mail"));
                cliente_tmp.setVenta_publico((Boolean) mapa1.get("ventaPublico"));
                cliente_tmp.setPagina_web((String) mapa1.get("web"));
                cliente_tmp.setPac_facturacion((String) mapa1.get("pacFacturacionEnum"));
                cliente_tmp.setNombre_corto((String) mapa1.get("nombreCorto"));
                cliente_tmp.setTipo_persona((String) mapa1.get("tipoPersonaFiscal"));


                id_cliente.add((Integer) mapa1.get("id"));
                nombre_cliente.add((String) mapa1.get("nombre"));
                rfc_cliente.add((String) mapa1.get("rfc"));

                List<Object> direcciones = (List) mapa1.get("direcciones");
                List<HashMap> mapa10 = (List) direcciones.get(1);
                direccion_cliente.add((Integer) mapa10.get(0).get("id"));
                if (mapa1.containsKey("agente") && mapa1.get("agente") != null) {
                    Map mapa2 = (Map) mapa1.get("agente");

                    if (mapa2.containsKey("nombre")) {
                        nombre_agente.add((String) mapa2.get("nombre"));
                        cliente_tmp.setAgente_nombre(((String) mapa2.get("nombre")));
                    } else {
                        nombre_agente.add("");
                    }
                    if (mapa2.containsKey("id")) {
                        id_agente.add(String.valueOf((Integer) mapa2.get("id")));
                        cliente_tmp.setAgente_id(((Integer) mapa2.get("id")));
                    } else {
                        id_agente.add("");
                    }
                } else {
                    nombre_agente.add("");
                    id_agente.add("");
                }


                if (mapa1.containsKey("moneda") && mapa1.get("moneda") != null) {
                    Map mapa2 = (Map) mapa1.get("moneda");
                    id_moneda.add((Integer) mapa2.get("id"));
                }
                else{
                    id_moneda.add(0);
                }

                listaClientes.add(cliente_tmp);
            }
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (id_cliente.size() > 0) {
            build_popup2();
        }

    }


    private void build_popup2() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ReporteVentasNetas.this);
        builder.setTitle("Cliente");
        builder.setSingleChoiceItems(nombre_cliente.toArray(new String[nombre_cliente.size()]), 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp = which;
                // TODO Auto-generated method stub
            }


        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                selected = temp;

                switch (selected) {

                    default:
                        // escaner_txt_cliente.setText(nombre_cliente.get(selected));
                        elCliente = id_cliente.get(selected).toString();
                        //elAgente = id_agente.get(selected).toString();
                        //  escaner_txt_agente.setText(nombre_agente.get(selected));
                        //laDireccion = direccion_cliente.get(selected);
                        //laMoneda = id_moneda.get(selected);

                        break;
                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + elCliente + " " + nombre_cliente.get(selected), Toast.LENGTH_LONG).show();

                Cliente cliente = listaClientes.get(selected);
                Intent i = new Intent(ReporteVentasNetas.this, DescripcionCliente.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("Cliente", cliente);
                startActivity(i);
                finish();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //escaner_txt_cliente.setText("");
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }


    private class HttpAsyncTask5 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST5(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("Articulos: " + result);
           // convertirDatos(result);
        }
    }


    public static String POST5(String url) {

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


    public void alertArticulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReporteVentasNetas.this);

        LayoutInflater inflater = ReporteVentasNetas.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.busqueda_articulos, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = (Button) dialogView.findViewById(R.id.agregar);
        //   btn_positive.setEnabled(false);
        Button btn_negative = (Button) dialogView.findViewById(R.id.buscar_art);


        nombre = (EditText) dialogView.findViewById(R.id.nombre_et);
        corto = (EditText) dialogView.findViewById(R.id.corto_et);
        sku = (EditText) dialogView.findViewById(R.id.sku_et);

        prices = (TableLayout) dialogView.findViewById(R.id.tabla_prueba);
        prices.removeAllViews();
        prices.setStretchAllColumns(true);
        prices.bringToFront();

        // Create the alert dialog
        chbx_selected.clear();
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onCheckboxClicked();

                dialog.cancel();
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prices.removeAllViews();

                if (corto.getText().toString().equals("") && nombre.getText().toString().equals("") && sku.getText().toString().equals("")) {

                } else {
                    try{
                        String encodedurl = "";
                   // HttpAsyncTask7 a = new HttpAsyncTask7();
                  //  a.execute(server + "/medialuna/spring/listar/entidad/filtro/"+ URLEncoder.encode("selectorArtículosInventario", "utf-8") + "/";);}
                        encodedurl = server + "/medialuna/spring/listar/entidad/filtro/"+ URLEncoder.encode("selectorArtículosInventario", "utf-8") + "/";
                        //encodedurl = server + "/medialuna/spring/listar/entidad/filtro/count/"+ URLEncoder.encode("selectorArtículosInventario", "utf-8") + "/";
                        System.out.println(" El url  : " + encodedurl);
                    //HttpAsyncTask8 a = new HttpAsyncTask8();
                       //  a.execute(encodedurl);
                    HttpAsyncTask7 a = new HttpAsyncTask7();
                    a.execute(encodedurl);

                }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        // Display the custom alert dialog on interface
        dialog.show();
    }


    private class HttpAsyncTask7 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST7(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("Articulos: " + result);
            convertirDatosArticulo2(result);

        }
    }


    public static String POST7(String url) {


        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        agregarCamposEntidad(corto.getText().toString(), entidad, "nombreCorto");
        agregarCamposEntidad(nombre.getText().toString(), entidad, "nombre");
        agregarCamposEntidad(sku.getText().toString(), entidad, "codigoSKU");
        entidad.put("conExistencia", false);
        ArrayList tipo0 = new ArrayList();
        ArrayList tipo = new ArrayList();
        tipo.add("COMPRA_VENTA");
        tipo.add("MATERIA_PRIMA");
        tipo.add("CONSUMO_INTERNO");
        tipo.add("TELA");
        tipo.add("PRODUCCION");
        tipo.add("HABILITACION");
        tipo0.add(ArrayList.class.getName());
        tipo0.add(tipo);
        entidad.put("tipo", tipo0);

        Map adicionales = new HashMap();
        adicionales.put("@class", HashMap.class.getName());
        adicionales.put("tipoTercero", "ARTICULO");

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
        map.put("pagerMaxResults", 10);
        map.put("pagerFirstResult", 0);


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
            System.out.println("Lo que se envia: " + objectStr + " " + map);

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


    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.print("El resultado de articulos: " + result);

        }
    }


    public static String POST8(String url) {
        System.out.print("Entro al Post");

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        entidad.put("conExistencia", false);
        ArrayList tipo0 = new ArrayList();
        ArrayList tipo = new ArrayList();
        tipo.add("COMPRA_VENTA");
        tipo.add("MATERIA_PRIMA");
        tipo.add("CONSUMO_INTERNO");
        tipo.add("TELA");
        tipo.add("PRODUCCION");
        tipo.add("HABILITACION");
        tipo0.add(ArrayList.class.getName());
        tipo0.add(tipo);
        entidad.put("tipo", tipo0);

        Map adicionales = new HashMap();
        adicionales.put("@class", HashMap.class.getName());
        adicionales.put("tipoTercero", "ARTICULO");

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        Map pageOrden = new LinkedHashMap();
        pageOrden.put("@class", LinkedHashMap.class.getName());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("entidad", entidad);
        map.put("adicionales", adicionales);
        map.put("pagerFiltros", pagerFiltros);



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

    static void agregarCamposEntidad(String campo, Map entidad, String key) {

        if (campo.equals("")) {
        } else {
            entidad.put(key, campo);
        }
    }


    public void convertirDatosArticulo2(String cadena) {

        System.out.print("El resultado de articulos: " + cadena);

        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_art.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);

            // counter = counter + 1;
            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()),0.0);

                lista_art.add(art);

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
        chbx_selected.clear();

        if (lista_art.isEmpty()) {
            TextView c1 = new TextView(ReporteVentasNetas.this);
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            for (int j = 0; j < lista_art.size(); j++) {
                // counter = counter +1;

                TableRow tr = new TableRow(ReporteVentasNetas.this);
                CheckBox c0 = new CheckBox(ReporteVentasNetas.this);
                c0.setId(R.id.select_art + j);
                chbx_selected.add(c0);

                TextView c1 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(lista_art.get(j).getId().toString());

                TextView c2 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c2.setText(lista_art.get(j).getDescripcion().toString());

                TextView c3 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c3.setText(lista_art.get(j).getExistencia().toString());

                TextView c4 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c4.setText(lista_art.get(j).getNombre().toString());
                System.out.println("Nombre:-->" + lista_art.get(j).getNombre().toString());

                TextView c5 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                System.out.println("Nombre Corto:-->"  + lista_art.get(j).getNombreCorto().toString());

                c5.setText(String.valueOf(lista_art.get(j).getNombreCorto().toString()));

                TextView c6 = (TextView) ReporteVentasNetas.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c6.setText(lista_art.get(j).getPrecioBase().toString());


                tr.addView(c0);
                tr.addView(c1);
                tr.addView(c2);
                tr.addView(c3);
                tr.addView(c4);
                tr.addView(c5);
                tr.addView(c6);


                if (Build.VERSION.SDK_INT >= 16) {
                    if ((j % 2) == 0) {
                        tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                    } else {
                        tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                    }
                }
                prices.addView(tr);
            }
        }
    }

}


