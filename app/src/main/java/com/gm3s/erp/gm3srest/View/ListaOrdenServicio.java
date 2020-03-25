package com.gm3s.erp.gm3srest.View;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.InformeCirugia;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.UploadPhoto;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class ListaOrdenServicio extends AppCompatActivity {
    String server = "", tipo_documento;
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    EditText txtFolio_os,fromDateEtxt,toDateEtxt;
    Button btnConsultar_os;
    TableLayout tabla_contenido;
    File file_tmp;
    String id = "";
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    long f_ini;
    long f_fin;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_orden_servicio);

        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        pc = new PersistentCookieStore(this);

        tabla_contenido = (TableLayout) findViewById(R.id.tabla_contenido);
        txtFolio_os = (EditText) findViewById(R.id.txtFolio_os);
        btnConsultar_os = (Button) findViewById(R.id.btnConsultar_os);
        fromDateEtxt = (EditText) findViewById(R.id.tvDate);
        toDateEtxt = (EditText) findViewById(R.id.tvDate2);
        loading = (ProgressBar) findViewById(R.id.loading);


        Intent intent = getIntent();
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Listado Cotizacion");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaOrdenServicio.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"));
        c.setTimeInMillis(c.getTimeInMillis() - 21600000);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        f_ini =  c.getTimeInMillis();
        f_fin = f_ini - 518400000;


        btnConsultar_os.setBackgroundColor(Color.parseColor("#039BE7"));
        btnConsultar_os.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabla_contenido.removeAllViews();
                loading.setVisibility(View.VISIBLE);
                if(txtFolio_os.getText().toString().equals("")){
                HttpAsyncTask1 a = new HttpAsyncTask1();
                    a.execute(server + "/medialuna/spring/documento/catalogo/consulta/"+tipo_documento+"/?fInicial="+f_ini+"&fFinal="+f_fin+"&idSerie=0&idTercero=0&estatus=PENDIENTE");
                }
                else{
                    HttpAsyncTask1 a = new HttpAsyncTask1();
                    a.execute(server + "/medialuna/spring/documento/catalogo/consultaPorFolio/" + tipo_documento + "/" + txtFolio_os.getText().toString() +"?&idSerie=0");
                }
            }
        });


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        fromDateEtxt.setText(dateFormatter.format(c.getTime()));
        toDateEtxt.setText(dateFormatter.format(c.getTime()));
        setDateTimeField();
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


        HttpAsyncTask1 a = new HttpAsyncTask1();
        a.execute(server + "/medialuna/spring/documento/catalogo/consulta/"+tipo_documento+"/?fInicial="+f_ini+"&fFinal="+f_fin+"&idSerie=0&idTercero=0&estatus=PENDIENTE");



    }

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST1(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            convertirDatosCotizaciones(result);
        }
    }

    public static String POST1(String url) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", HashMap.class.getName());
        map.put("idProyecto", 0);


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


    public void convertirDatosCotizaciones(String cadena) {
       ObjectMapper mapper = new ObjectMapper();
        try {
            tabla_contenido.removeAllViews();
            loading.setVisibility(View.GONE);
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {
                final HashMap mapa1 = new HashMap();
                mapa1.put("id", (Integer) ((HashMap) arrayData.get(i)).get("id"));
                mapa1.put("usuario", (String) ((HashMap) arrayData.get(i)).get("usuario"));
                mapa1.put("fechaAlta", (String) ((HashMap) arrayData.get(i)).get("fechaAlta"));
                mapa1.put("serie", (String) ((HashMap) arrayData.get(i)).get("serie"));
                mapa1.put("nombreAgente", (String) ((HashMap) arrayData.get(i)).get("nombreAgente"));
                mapa1.put("nombreTercero", (String) ((HashMap) arrayData.get(i)).get("nombreTercero"));
                mapa1.put("folio", (Integer) ((HashMap) arrayData.get(i)).get("folio"));
                mapa1.put("@class", HashMap.class.getName());

                TableRow tr = new TableRow(ListaOrdenServicio.this);
               // tr.setBackgroundResource(R.drawable.border);

                LinearLayout ly = new LinearLayout(ListaOrdenServicio.this);
                ly.setOrientation(LinearLayout.VERTICAL);

                LinearLayout lyh = new LinearLayout(ListaOrdenServicio.this);
                lyh.setOrientation(LinearLayout.HORIZONTAL);


                TextView a = new TextView(this);
                a.setTextColor(Color.parseColor("#7F7FFF"));
                switch(mapa1.get("folio").toString().length()){
                    case 1:
                        a.setText(mapa1.get("folio").toString() + "     ");
                        break;
                    case 2:
                        a.setText(mapa1.get("folio").toString() + "    ");
                        break;
                    case 3:
                        a.setText(mapa1.get("folio").toString() + "   ");
                        break;
                    case 4:
                        a.setText(mapa1.get("folio").toString() + "  ");
                        break;
                    case 5:
                        a.setText(mapa1.get("folio").toString() + " ");
                        break;
                    default:
                        break;
                }

                a.setGravity(Gravity.CENTER);
                a.setTextSize(Float.parseFloat("30.0"));
                a.setTypeface(null, Typeface.BOLD);

                Button b1 = new Button(this);
                b1.setText("Adjunto");
                b1.setTextColor(Color.parseColor("#2196F3"));
                b1.setTextSize(Float.parseFloat("10.0"));
                b1.setTypeface(null, Typeface.BOLD);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ListaOrdenServicio.this, UploadPhoto.class);
                        intent.putExtra("tipo_documento", tipo_documento);
                        intent.putExtra("idDocumento", mapa1.get("id").toString());
                        startActivity(intent);
                        finish();
                    }
                });


                Button b2 = new Button(this);
                b2.setText("Adicionales");
                b2.setTextColor(Color.parseColor("#2196F3"));
                b2.setTextSize(Float.parseFloat("10.0"));
                b2.setTypeface(null, Typeface.BOLD);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ListaOrdenServicio.this, InformeCirugia.class);
                        intent.putExtra("tipo_documento", tipo_documento);
                        intent.putExtra("idDocumento", mapa1.get("id").toString());
                        intent.putExtra("folio_documento", mapa1.get("folio").toString());
                        startActivity(intent);
                        finish();
                    }
                });


                Button b3 = new Button(this);
                b3.setText("PDF");
                b3.setTextColor(Color.parseColor("#2196F3"));
                b3.setTextSize(Float.parseFloat("10.0"));
                b3.setTypeface(null, Typeface.BOLD);
                b3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        id = mapa1.get("id").toString();
                        HttpAsyncTask10 a = new HttpAsyncTask10();
                        a.execute(server + "/medialuna/spring/documento/reporte/pdf/");
                    }
                });

                lyh.addView(a);
                lyh.addView(b1);
                lyh.addView(b2);
                lyh.addView(b3);

                LinearLayout lyv = new LinearLayout(ListaOrdenServicio.this);
                lyv.setOrientation(LinearLayout.VERTICAL);

                TextView b = new TextView(this);
                b.setText(mapa1.get("fechaAlta").toString());
                b.setGravity(Gravity.LEFT);

                TextView c = new TextView(this);
                c.setText(mapa1.get("nombreTercero").toString());
                c.setGravity(Gravity.LEFT);
                c.setTextSize(Float.parseFloat("10.0"));

                TextView d = new TextView(this);
                d.setText(mapa1.get("nombreAgente").toString());
                d.setGravity(Gravity.LEFT);
                d.setTextSize(Float.parseFloat("10.0"));

                lyv.addView(b);
                lyv.addView(c);
                lyv.addView(d);


                ly.addView(lyh);
                ly.addView(lyv);

                tr.addView(ly);

                if(i%2==0){
                tr.setBackgroundColor(Color.parseColor("#267F7FFF"));
               // a.setTextColor(Color.parseColor("#FFFFFF"));
                }
                tabla_contenido.addView(tr);


            }
            if(arrayData.isEmpty())
                Toast.makeText(getApplicationContext(),"No se encontraron resultados", Toast.LENGTH_SHORT).show();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private class HttpAsyncTask10 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST10(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getApplicationContext(), "Documento Guardado como: " + tipo_documento.substring(0,1).toUpperCase() + id + ".pdf", Toast.LENGTH_LONG).show();

            // Toast.makeText(getApplicationContext(), "Resultado " + result, Toast.LENGTH_LONG).show();

            File pdfFile = new File("/storage/sdcard0/Download/"+tipo_documento.substring(0,1).toUpperCase()+id+".pdf");
            File pdfFile2 = new File("/storage/emulated/0/Download/"+tipo_documento.substring(0,1).toUpperCase()+id+".pdf");

            if(pdfFile.exists())
            {
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try
                {
                    startActivity(pdfIntent);
                }
                catch(ActivityNotFoundException e)
                {
                    Toast.makeText(ListaOrdenServicio.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                }


            }
            else {
                if (pdfFile2.exists()) {
                    Uri path = Uri.fromFile(pdfFile2);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ListaOrdenServicio.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                    }


                }
            }
        }

    }

    public String POST10(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("id", id));
            postParameters.add(new BasicNameValuePair("tipo", tipo_documento));

            httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.addHeader("Accept-Language", "es-MX,es-ES;q=0.9,es;q=0.7,es-AR;q=0.6,es-CL;q=0.4,en-US;q=0.3,en;q=0.1");
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));


            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    createExternalStoragePrivateFile(id, "pdf", inputStream);
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

    public void createExternalStoragePrivateFile(String name, String extension, InputStream is) {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "Download");
        folder.mkdir();
        File file = new File(folder, tipo_documento.substring(0,1).toUpperCase() + name + "." + extension);
        file_tmp = file;

        try {
            Log.d("", "before: " + file.getPath() + " " + file.getTotalSpace() + "  " + file.length());
            FileOutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int bufferLength;
            while ((bufferLength = is.read(buffer)) != -1) {
                os.write(buffer, 0, bufferLength);
            }
            os.close();
            Log.d("", "after: " + file.getPath() + " " + file.getTotalSpace() + "  " + file.length());
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(ListaOrdenServicio.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                f_ini = newDate.getTimeInMillis();


            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        toDatePickerDialog = new DatePickerDialog(ListaOrdenServicio.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 5, 0, 0);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth+1, 0, -1, 0);
                f_fin = newDate.getTimeInMillis();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
