package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Model.Serie;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class CorteCaja extends AppCompatActivity {


    private static PersistentCookieStore pc;
    private SharedPreference sharedPreference;
    String server = "";
    TableLayout prices;
    TableLayout tabla_totales;
    BigDecimal total= new BigDecimal(0.0);
    long f_ini, f_fin;
    int selected = 0;
    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    TextView corte_caja_total;
    TextView corte_caja_efe;
    TextView corte_caja_tdeb;
    TextView corte_caja_tcre;
    TextView corte_caja_che;
    TextView corte_caja_tran;
    TextView corte_caja_cre;
    TextView corte_caja_noid;
    Map<String,Integer> proporciones = new HashMap<>();
    List<String> tipos_pago = new ArrayList<>();
    static Long rango_maximo = new Long (5259492000L);

    private Button consultar, limpiar_corte;
    static private String laSerie = "";
    Button escaner_btn_serie;
    static boolean validacion;
    int temp;
    TextView escaner_txt_serie;
    List<String> nombre_serie = new ArrayList<String>();
    List<String> id_serie = new ArrayList<String>();
    int counter =0;
    List<BigDecimal> totales = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corte_caja);




        pc = new PersistentCookieStore(this);

        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Corte de Caja");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        totales.add(0, new BigDecimal(0.0));
        totales.add(1, new BigDecimal(0.0));
        totales.add(2, new BigDecimal(0.0));
        totales.add(3,new BigDecimal(0.0));
        totales.add(4, new BigDecimal(0.0));
        totales.add(5, new BigDecimal(0.0));
        totales.add(6,new BigDecimal(0.0));
        totales.add(7,new BigDecimal(0.0));

        prices =(TableLayout) findViewById(R.id.tabla_codigos);
        prices.setStretchAllColumns(true);
        prices.bringToFront();

        tabla_totales =(TableLayout) findViewById(R.id.tabla_totales);
        tabla_totales.setStretchAllColumns(true);
        tabla_totales.bringToFront();


        fromDateEtxt = (EditText) findViewById(R.id.tvDate);
        toDateEtxt = (EditText) findViewById(R.id.tvDate2);
        corte_caja_total = (TextView) findViewById(R.id.corte_caja_total);

        limpiar_corte = (Button) findViewById(R.id.limpiar_corte);
        consultar = (Button) findViewById(R.id.consultar);
        escaner_txt_serie = (TextView) findViewById(R.id.escaner_txt_serie);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

   //     Toast.makeText(getApplicationContext(), "Zona Horaria: " + TimeZone.getDefault(), Toast.LENGTH_SHORT).show();

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"));
        c.setTimeInMillis(c.getTimeInMillis() - 21600000);
        c.set(Calendar.HOUR_OF_DAY,6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        f_ini =  c.getTimeInMillis();


        fromDateEtxt.setText(dateFormatter.format(c.getTime()));
        toDateEtxt.setText(dateFormatter.format(c.getTime()));

        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - 1);
        f_fin =  c.getTimeInMillis();






        System.out.println("fromDateEtxt: " + f_ini +  "toDateExt: " + f_fin);

        HttpAsyncTask3 b = new HttpAsyncTask3();
        b.execute(server + "/medialuna/spring/listar/catalogo/1403/");


        setDateTimeField();

        fromDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //    Helper.hideSoftKeyboard(CorteCaja.this);
                fromDatePickerDialog.show();

            }
        });

        toDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //   Helper.hideSoftKeyboard(CorteCaja.this);
                toDatePickerDialog.show();

            }
        });


        escaner_btn_serie = (Button) findViewById(R.id.escaner_btn_serie);
        escaner_btn_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask2 a = new HttpAsyncTask2();
                a.execute(server + "/medialuna/spring/listar/serie/");
            }
        });


        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Helper.isConnected3(CorteCaja.this)) {
                    if(fromDateEtxt.getText().toString().equals("") || toDateEtxt.getText().toString().equals("")){
                        Toast.makeText(CorteCaja.this, "Favor de validar la(s) fecha(s)", Toast.LENGTH_LONG).show();
                    }
                    else{

                        System.out.println("--- fromDateEtxt: " + f_ini +  "toDateExt: " + f_fin);

                        Long rango= f_fin - f_ini;
                        if(rango.compareTo(rango_maximo)<0){
                        if(laSerie.equals("")){
                        HttpAsyncTask a = new HttpAsyncTask();
                        a.execute(server + "/medialuna/spring/documentos/desglose/buscar?fInicial=" + f_ini + "&fFinal=" + f_fin);}
                        else{
                      HttpAsyncTask a = new HttpAsyncTask();
                      a.execute(server + "/medialuna/spring/documentos/desglose/buscar?fInicial=" + f_ini + "&fFinal=" + f_fin + "&idSerie=" + laSerie);

                        }}
                        else{
                            Toast.makeText(CorteCaja.this, "Favor de elegir un rago menor de fechas (1 mes)", Toast.LENGTH_LONG).show();

                        }

                    }

                }else {
                    Toast.makeText(CorteCaja.this, "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent(CorteCaja.this, LogIn.class);
                    startActivity(localIntent);
                }


            }
        });



        limpiar_corte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                limpiar();

            }
        });


    }


    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

                convertirDatos4(result);

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
                    validacion = false;
                } else {
                    validacion = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public void convertirDatos4(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_serie.clear();
            id_serie.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
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


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            convertirDatos3(result);


            //int seconds = c.get(Calendar.SECOND);
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
          //  StringEntity params = new StringEntity(objectStr);
          //  params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
         //   httppost.setEntity(params);
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
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public void convertirDatos3 (String cadena){
        System.out.println("La cadena es: "  + cadena) ;

        ObjectMapper mapper = new ObjectMapper();
        try {

          limpiar();

            List<HashMap> arrayData = mapper.readValue(cadena, List.class);

               System.out.println("Objeto: TAM" + arrayData.size());

            for(int i = 0; i < arrayData.size(); i++) {  //iterator

                //   System.out.println("Objeto: TAM" + arrayData.get(i));
                if (i < 1500) {

                    System.out.println(" " + i);
                    TableRow tr = new TableRow(CorteCaja.this);
                    TextView c0 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c0.setText(arrayData.get(i).get("folio").toString());

                    TextView c1 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c1.setText(arrayData.get(i).get("fechaDoc").toString());

                    TextView c2 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c2.setText(arrayData.get(i).get("total").toString());


                    TextView c3 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c3.setText(arrayData.get(i).get("usuario").toString());


                    TextView c4 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c4.setText(arrayData.get(i).get("cliente").toString().substring(0, 5));

                    TextView c5 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c5.setText(arrayData.get(i).get("tipoDoc").toString());

                    if(proporciones.containsKey(arrayData.get(i).get("tipoDoc").toString())){
                        proporciones.put(arrayData.get(i).get("tipoDoc").toString(),proporciones.get(arrayData.get(i).get("tipoDoc"))+1);
                    }
                    else{
                        proporciones.put(arrayData.get(i).get("tipoDoc").toString(),1);
                    }

                    TextView c6 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);

                    c6.setText(arrayData.get(i).get("estatus").toString());

                    if (Build.VERSION.SDK_INT >= 16) {

                        if ((i % 2) == 0) {
                            c0.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c1.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c2.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c3.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c4.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c5.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                            c6.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                        } else {
                            c0.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c1.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c2.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c3.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c4.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c5.setBackground(getResources().getDrawable(R.drawable.row_color));
                            c6.setBackground(getResources().getDrawable(R.drawable.row_color));
                        }
                    }

                    tr.addView(c0);
                    tr.addView(c1);
                    tr.addView(c2);
                    tr.addView(c3);
                    tr.addView(c4);
                    tr.addView(c5);
                    tr.addView(c6);


                    prices.addView(tr);





                }

                if (arrayData.get(i).get("estatus").toString().equalsIgnoreCase("CANCELADO")) {


                } else {
                    if (arrayData.get(i).get("remisionFacturada").toString().equals("false")) {
                        total = total.add(new BigDecimal(Double.parseDouble(arrayData.get(i).get("total").toString())));
                        setTotales((arrayData.get(i).get("formasDePago")));

                    }


                }



            }


            corte_caja_total.setText(Helper.numberFormat(Helper.formatBigDec(total).toString()));


            for(int i=0; i<tipos_pago.size(); i++) {
                TableRow tr = new TableRow(CorteCaja.this);
                TextView c0 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c0.setText(tipos_pago.get(i));
//                System.out.println(tipos_pago.get(i) + "/" + Helper.numberFormat(Helper.formatBigDec(totales.get(i)).toString()));
                TextView c1 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(Helper.numberFormat(Helper.formatBigDec(totales.get(i)).toString()));
                //System.out.println(Helper.numberFormat(Helper.formatBigDec(totales.get(i)).toString()));

                if (Build.VERSION.SDK_INT >= 16) {

                    if ((i % 2) == 0) {
                        c0.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                        c1.setBackground(getResources().getDrawable(R.drawable.alt_row_color));

                    } else {
                        c0.setBackground(getResources().getDrawable(R.drawable.row_color));
                        c1.setBackground(getResources().getDrawable(R.drawable.row_color));

                    }
                }

                tr.addView(c0);
                tr.addView(c1);
                tabla_totales.addView(tr);
            }

            int aux=1;

            for (Map.Entry<String, Integer> entry : proporciones.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                TableRow tr = new TableRow(CorteCaja.this);
                TextView c0 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);
                key = key.replace("cliente", " Cliente");
                c0.setText(key.substring(0,1).toUpperCase()+key.substring(1));
                // System.out.println(tipos_pago.get(i) + "/" + Helper.numberFormat(Helper.formatBigDec(totales.get(i)).toString()));
                TextView c1 = (TextView) CorteCaja.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(value.toString());
                //System.out.println(Helper.numberFormat(Helper.formatBigDec(totales.get(i)).toString()));

                if (Build.VERSION.SDK_INT >= 16) {

                    c0.setBackground(getResources().getDrawable(R.drawable.row_color));
                    c1.setBackground(getResources().getDrawable(R.drawable.row_color));


                }

                tr.addView(c0);
                tr.addView(c1);
                tabla_totales.addView(tr,aux);
                aux++;

            }





        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST3(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            convertirDatos5(result);
        }
    }

    public static String POST3(String url) {

        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            //  StringEntity params = new StringEntity(objectStr);
            //  params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //   httppost.setEntity(params);
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
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(CorteCaja.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                f_ini = newDate.getTimeInMillis();


            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        toDatePickerDialog = new DatePickerDialog(CorteCaja.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 5, 0, 0);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth+1, 0, -1, 0);
                f_fin = newDate.getTimeInMillis();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void build_popup() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(CorteCaja.this);
        builder.setTitle("Serie");

        builder.setSingleChoiceItems(nombre_serie.toArray(new String[nombre_serie.size()]), 0, new DialogInterface.OnClickListener() {

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
                        laSerie = id_serie.get(selected);
                        escaner_txt_serie.setText(nombre_serie.get(selected));

                        break;
                    //   case 0:text = "Bad";break;

                    //  case 1:text = "Good";break;

                    // case 2:text = "Very Good";break;

                    //   case 3:text = "Average";break;

                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie + " " + nombre_serie.get(selected) + " " + selected, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie, Toast.LENGTH_LONG).show();

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


    public void setTotales(Object formas) {


        ArrayList<Object> array = (ArrayList) formas;
        ArrayList<Object> array2 = (ArrayList<Object>) array.get(1);
        for (int i = 0; i < array2.size(); i++) {
            HashMap<String, Object> array3 = (HashMap<String, Object>) array2.get(i);
            for (int j = 0; j < tipos_pago.size(); j++) {
                if (array3.get("nombre").toString().equals(tipos_pago.get(j).toString())) {
                    totales.set(j, totales.get(j).add(new BigDecimal(Double.parseDouble(array3.get("valor").toString()))));
                }
            }

        }
    }



    public String convertirDatos5(String cadena) {
        String id = "";
        ObjectMapper mapper = new ObjectMapper();
        try {

            //System.out.println("arrayData1" + cadena);
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i=0; i<arrayData.size(); i++){
            HashMap<String,Object> my_map = (HashMap<String,Object>)arrayData.get(i);
             //   System.out.println("arrayData" + my_map.get("nombre").toString());
                tipos_pago.add(i,my_map.get("nombre").toString());
                System.out.println("Tipo Pago:  " + my_map.get("nombre").toString());
            }


            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/documentos/desglose/buscar?fInicial=" + f_ini + "&fFinal=" + f_fin);

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return id;
    }



    public void limpiar(){

        totales.clear();
        totales.add(0, new BigDecimal(0.0));
        totales.add(1, new BigDecimal(0.0));
        totales.add(2,new BigDecimal(0.0));
        totales.add(3,new BigDecimal(0.0));
        totales.add(4,new BigDecimal(0.0));
        totales.add(5,new BigDecimal(0.0));
        totales.add(6, new BigDecimal(0.0));
        totales.add(7, new BigDecimal(0.0));


     //   System.out.println("Vistas  "+ prices.getChildCount());

        if(prices.getChildCount()>1) {
            prices.removeViews(1, prices.getChildCount()-1);
        }
     //   else{
      //   prices.removeViews(1, prices.getChildCount());
      //}

        total=  new BigDecimal(0.0);
        corte_caja_total.setText(Helper.numberFormat(Helper.formatBigDec(total).toString()));


        if(tabla_totales.getChildCount()>2) {
            tabla_totales.removeViews(2, tabla_totales.getChildCount()-2);
            //corte_caja_total.setText(Helper.numberFormat(Helper.formatBigDec(total).toString()));
        }

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
            Intent localIntent = new Intent(CorteCaja.this.getApplicationContext(), LogIn.class);
            startActivity(localIntent);
        }
        if (id == R.id.atras) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("F_INICIAL", String.valueOf(f_ini));
        savedInstanceState.putString("F_FINAL", String.valueOf(f_fin));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
         f_ini = Long.parseLong(savedInstanceState.getString("F_INICIAL"));
         f_fin = Long.parseLong(savedInstanceState.getString("F_FINAL"));
         fromDateEtxt.setText(dateFormatter.format(f_ini));
         toDateEtxt.setText(dateFormatter.format(f_fin));

    }

}
