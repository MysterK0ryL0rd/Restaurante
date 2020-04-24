package com.gm3s.erp.gm3srest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.gm3s.erp.gm3srest.View.Escaner;
import com.gm3s.erp.gm3srest.View.LogIn;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class ComandasParticular  extends AppCompatActivity {
    String tipo_documento = "";

    private final long DELAY3 = 1000; // in ms
    private final long DELAY1 = 1000; // in ms
    private final long DELAY = 2000; // in ms
    private LinearLayout mButton;
    TableLayout codigos;
    int selected = 0;
    String id = "";
    int temp;
    private SharedPreference sharedPreference;
    String server = "";
    static String barcode = "";
    String path = "";
    File file_tmp;
    private static PersistentCookieStore pc;
    // List<Articulo> lista_art = new ArrayList<Articulo>();
    // List<Articulo> lista_articulos = new ArrayList<Articulo>();
    private Timer timer = new Timer();
    LinkedList<HashMap> productos_list = new LinkedList();
    private TextView subtotal;
    private TextView impuestos;
    TableLayout prices;
    // List<Articulo> lista_art2 = new ArrayList<Articulo>();
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


    Map<Integer, Articulo> mapa_articulos = new HashMap<>();

    TextView total;


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
    static String categoria_busqueda = "nombreCorto";
    boolean formas_estado = false;
    static BigDecimal temporal_resta = new BigDecimal(0.0);
    List<Boolean> formas_check = new ArrayList<Boolean>();
    List<String> formas_et = new ArrayList<String>();
    static BigDecimal total_temp = new BigDecimal(0.0);
    LinkedList<HashMap> formasPago_tmp = new LinkedList();
    List<HashMap> formasPago = new ArrayList<HashMap>();
    static boolean editar_precio_permiso = false;
    static boolean editar_descuento_permiso = false;
    Integer laMoneda = 0;
    Button guardar;
    String idDocumento, idMesa, idComensal;
    Double total_double = 0.0;//0.0 ORIGEN

    Map<String, String> info = new HashMap<>();
    List<Map<String, String>> orden = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comandas_particula);
        pc = new PersistentCookieStore(this);


        info.put("cliente", "");
        info.put("cliente_id", "");
        info.put("agente", "");
        info.put("agente_id", "");
        info.put("serie", "");
        info.put("serie_id", "");
        info.put("f_pago", "");
        info.put("f_pago_id", "");
        info.put("fecha", "");
        info.put("folio", "");
        info.put("subtotal", "");
        info.put("impuestos", "");
        info.put("total", "");
        info.put("no_art", "");

        Intent intent = getIntent();

        idMesa = (String) intent.getSerializableExtra("idMesa");
        idComensal = (String) intent.getSerializableExtra("idComensal");

        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();
        codigos.removeAllViews();
        subtotal = (TextView) findViewById(R.id.et_subtotal);
        subtotal.setText("0.0");
        impuestos = (TextView) findViewById(R.id.et_impuestos);
        impuestos.setText("0.0");
        total = (TextView) findViewById(R.id.et_total);
        total.setText("0.0");
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());


        // documento/partidas/consulta/modificar/{idDoc}


        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/app/buscarComandaPersonal/" + idMesa + "/" + idComensal);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Comanda Particular");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComandasParticular.this, PreComanda.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("idMesa", idMesa);
                startActivity(intent);
                finish();

            }
        });

       /* guardar = (Button) findViewById(R.id.guardar_pedido);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask8 a = new HttpAsyncTask8();
                a.execute(server + "/medialuna/spring/documento/modificar/app/" + idDocumento);


            }
        });*/


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
            Toast.makeText(getApplicationContext(), "Saliendo", Toast.LENGTH_SHORT).show();
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
            File f = new File(Environment.getExternalStorageDirectory(), "Download/T" + id + ".txt");
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
            } else {

                Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();

            }


        }


    }


    @SuppressLint("SetTextI18n")
    public void crearTablaArticulos() {//esta si

        for (int j = 0; j < orden.size(); j++) {
            TableRow tr = new TableRow(this);

            //final Articulo art = lista_art.get(j);

            final Map<String, String> art = orden.get(j);


            final EditText c00 = new EditText(this);
            c00.setInputType(InputType.TYPE_CLASS_NUMBER);
            c00.setText(art.get("cantidad").toString());
            c00.setGravity(Gravity.CENTER);
            c00.setTextColor(Color.parseColor("#ffffff"));
            //c00.setBackgroundColor(Color.parseColor("#373737"));
            cantidad_articulos = cantidad_articulos + 1;
            final String auxc = c00.getText().toString();


            TextView c5 = new TextView(this);
            c5.setTextColor(Color.parseColor("#ffffff"));
            c5.setText(art.get("descripcion").toString());
            c5.setGravity(Gravity.CENTER);
            //  c5.setBackgroundColor(Color.parseColor("#373737"));

            final EditText c51 = new EditText(this);
            c51.setText(art.get("precio").toString());
            c51.setGravity(Gravity.RIGHT);
            c51.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (editar_descuento_permiso == false) {
                c51.setEnabled(false);
                c51.setFocusable(false);
            }


            final TextView c2_1 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
            c2_1.setText(art.get("total").toString());
            c2_1.setGravity(Gravity.RIGHT);
            c2_1.setTextSize(25);
            c2_1.setTextColor(Color.parseColor("#3D6AB3"));
            c2_1.setTypeface(null, Typeface.BOLD);

            System.out.println(" 2 Total: " + Helper.formatDouble(total_double));
            tr.addView(c00);
            tr.addView(c5);

            tr.addView(c51);
            tr.addView(c2_1);


            if (Build.VERSION.SDK_INT >= 16) {
                if ((counter % 2) == 0) {
                    tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                } else {
                    tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                }
            }

            codigos.addView(tr, 1); //unica
            TableRow tr1 = new TableRow(this);

            TextView total1 = new TextView(this);
            total1.setTextColor(Color.parseColor("#FFFFFF"));
            total1.setText(Helper.formatBigDec(total_temp).toString());
            total1.setGravity(Gravity.RIGHT);
            tr1.addView(total1);
            codigos.addView(tr1, 1); //unica
            //  c5.setBackgroundColor(Color.parseColor("#373737"));
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


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void convertirDatos(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        Double impuesto = 0.0;
        try {

            List<List<Object>> arrayData1 = mapper.readValue(cadena, List.class);
            System.out.println("arrayData1:" + arrayData1);

            for (int i = 0; i < arrayData1.size(); i++) {

//                List<Object> arrayData2 = mapper.readValue(arrayData1.get(i).get(1).toString(), List.class);
                //System.out.println("for 1: "+ arrayData2);
                for (int j = 1; j < arrayData1.get(i).size(); j++) {
                    //  List<List> arrayData2 = mapper.readValue(arrayData1.get(i).toString().substring(22,arrayData1.get(i).toString().length()-2), List.class);
                    System.out.println("for 2: "+ arrayData1.get(i).get(j).toString());

                    List<Object> infopartidas = (List) arrayData1.get(i).get(j);
                    Double cantidad = (Double)((ArrayList) infopartidas.get(0)).get(1);
                    String descripcion = (String)infopartidas.get(1);
                    Double precio =(Double)((ArrayList) infopartidas.get(2)).get(1);
                    Double total =(Double)((ArrayList) infopartidas.get(3)).get(1);





                    TableRow tr = new TableRow(ComandasParticular.this);


                    TextView c1 = (TextView) ComandasParticular.this.getLayoutInflater().inflate(R.layout.renglones, null);
                    c1.setText(cantidad.toString());

                    TextView c2 = (TextView) ComandasParticular.this.getLayoutInflater().inflate(R.layout.renglones, null);
                    c2.setText(descripcion);

                    TextView c3 = (TextView) ComandasParticular.this.getLayoutInflater().inflate(R.layout.renglones, null);
                    c3.setText(precio.toString());

                    TextView c4 = (TextView) ComandasParticular.this.getLayoutInflater().inflate(R.layout.renglones, null);
                    c4.setText(total.toString());



                    System.out.println(" 1 Total: " + total_double);
                    total_double= Helper.formatDouble(total_double) + Double.parseDouble(total.toString());

                //    total_double=total_double*1.16;
                    tr.addView(c1);
                    tr.addView(c2);
                    tr.addView(c3);
                    tr.addView(c4);


                    if (Build.VERSION.SDK_INT >= 16) {
                        if ((j % 2) == 0) {
                            tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                        } else {
                            tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                        }
                    }


                    codigos.addView(tr);
                }
            }
           /* System.out.println(" 3 Total: " + Helper.formatDouble(total_double));
            setText(subtotal, Helper.formatDouble(total_double).toString());
            setText(impuestos, String.valueOf(Helper.formatDouble(total_double)*0.16));
            setText(total, String.valueOf(Helper.formatDouble(total_double)+(Helper.formatDouble(total_double)*0.16)));*/
            System.out.println(" 3 Total: " + Helper.formatDouble(total_double));
            setText(subtotal, Helper.formatDouble(total_double).toString());
            setText(impuestos, String.format( "%.2f",Helper.formatDouble(total_double) * .16));
            setText(total, String.format( "%.2f",Helper.formatDouble(total_double)+(Helper.formatDouble(total_double))*.16));
            //totales.add(" $ " + Helper.formatDouble(total_tmp));
            //        totales.add(" $ "+Helper.formatDouble(total_tmp * (Helper.formatDouble(impuesto)/100)));
            //        totales.add(" $ " + Helper.formatDouble(total_tmp +  (Helper.formatDouble(total_tmp) * (Helper.formatDouble(impuesto)/100))));;
            //        total_tmp = Helper.formatDouble(total_tmp) +  (Helper.formatDouble(total_tmp) * (Helper.formatDouble(impuesto)/100));


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
            System.out.println("EL resultado es: " + result);
            Intent intent = new Intent(ComandasParticular.this, MenuMesas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            finish();
        }
    }


    public static String POST8(String url) {
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
        savedInstanceState.putString("Subtotal", subtotal.getText().toString());
        savedInstanceState.putString("Impuestos", impuestos.getText().toString());
        savedInstanceState.putString("Total", total.getText().toString());

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

        for (int i = 1; i < codigos.getChildCount(); i++) {

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
        }


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


        total.setText(savedInstanceState.getString("Total"));
        //   escaner_txt_cliente.setText(savedInstanceState.getString("Cliente"));
        //  escaner_etx_fpago.setText(savedInstanceState.getString("Fpago"));
        //  escaner_txt_serie.setText(savedInstanceState.getString("Serie"));
        //  escaner_txt_agente.setText(savedInstanceState.getString("Agente"));
        //  escaner_fecha.setText(savedInstanceState.getString("Fecha"));
        //   escaner_folio.setText(savedInstanceState.getString("Folio"));
        //  escaner_txt_no_art.setText(savedInstanceState.getString("Noart"));
        subtotal.setText(savedInstanceState.getString("Subtotal"));
        impuestos.setText(savedInstanceState.getString("Impuestos"));
        //total.setText("0.0");
        //  escaner_total.setText(savedInstanceState.getString("Total"));


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

}