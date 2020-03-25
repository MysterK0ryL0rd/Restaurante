package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Model.Serie;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by usuario on 16/12/15.
 */
public class DocumentoBluetooth extends AppCompatActivity {
    String tipo_documento = "";
    private final long DELAY3 = 1000; // in ms
    private final long DELAY1 = 1000; // in ms
    private final long DELAY = 2000; // in ms
    private final long DELAY2 = 500; // in ms
    private LinearLayout mButton;
    TableLayout codigos;
    int selected = 0;
    String id = "";
    int temp;
    private SharedPreference sharedPreference;
    String server = "";
    static String barcode = "";
    static private String laListaPrecio = "";
    String path = "";
    File file_tmp;
    private static PersistentCookieStore pc;
    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Articulo> lista_articulos = new ArrayList<Articulo>();
    private Timer timer = new Timer();
    List<HashMap> productos_list = new LinkedList();
    private TextView subtotal;
    private TextView impuestos;
    TableLayout prices;
    List<Articulo> lista_art2 = new ArrayList<Articulo>();
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

    List<Integer> id_ListaPrecios = new ArrayList<Integer>();
    List<String> nombre_ListaPrecios = new ArrayList<String>();

    List<Boolean> formas_check =  new ArrayList<Boolean>();
    List<String>  formas_et = new ArrayList<String>();
    boolean formas_estado = false;

    static BigDecimal total_temp = new BigDecimal(0.0);
    static BigDecimal temporal_resta = new BigDecimal(0.0);

    TextView total;
    TextView escaner_folio;
    TextView escaner_total, escaner_fecha;
    private static boolean validacion;
    TextView escaner_txt_serie, escaner_txt_agente;
    static TextView escaner_txt_cliente;
    static private String laSerie = "";
    static private String elCliente = "";
    static private String laBodega = "";
    private String elAgente = "";
    private Integer laDireccion = 0;
    Button escaner_btn_cliente;
    static List<String> tmp = new ArrayList<>();
    Button escaner_btn_fpago;
    Button escaner_btn_serie;
    LinearLayout escaner_btn_cambio;
    TextView escaner_etx_fpago;
    Button escaner_btn_agente;
    LinearLayout escaner_btn_salir;
    LinearLayout escaner_btn_articulo;
    LinearLayout escaner_btn_grabar;
    List<CheckBox> chbx_selected = new ArrayList<>();
    static EditText corto, nombre, sku;
    LinearLayout escaner_btn_limp;
    TextView escaner_txt_no_art;
    int cantidad_articulos = 0;
    int counter = 0;
    int first_time = 0;
    static String categoria_busqueda= "codigoSKU";
    static EditText et_escaner;
    boolean blue_on = false;
    List<HashMap> formasPago = new ArrayList<HashMap>();
    boolean pdf_flag=false;

    LinkedList<HashMap> formasPago_tmp = new LinkedList();
    static boolean editar_precio_permiso  = false;
    static boolean editar_descuento_permiso  = false;
    Integer laMoneda = 0;
    byte[] data = "data".getBytes();
    byte[] ticketBytes;
    Button btn_lista_precios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.escaner2);
        pc = new PersistentCookieStore(this);



        Intent intent = getIntent();
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");

        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();

        subtotal = (TextView) findViewById(R.id.et_subtotal);
        subtotal.setText("0.0");
        impuestos = (TextView) findViewById(R.id.et_impuestos);
        impuestos.setText("0.0");
        total = (TextView) findViewById(R.id.et_total);
        total.setText("0.0");
        escaner_total = (TextView) findViewById(R.id.escaner_total);
        escaner_total.setText("0.0");
        escaner_txt_no_art = (TextView) findViewById(R.id.escaner_txt_no_art);
        escaner_txt_no_art.setText("0");


        escaner_txt_serie = (TextView) findViewById(R.id.escaner_txt_serie);
        escaner_txt_cliente = (TextView) findViewById(R.id.escaner_txt_cliente);
        escaner_txt_agente = (TextView) findViewById(R.id.escaner_txt_agente);
        escaner_etx_fpago = (TextView) findViewById(R.id.escaner_etx_fpago);

        et_escaner = (EditText) findViewById(R.id.et_escaner);
        et_escaner.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {


                if (s.length() > 3) {
                    System.out.println(" ***** 0");
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    System.out.println(" ***** 1");


                                    System.out.println(" ***** 2");
                                    HttpAsyncTask3 a = new HttpAsyncTask3();
                                    a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");


                                }
                            },
                            DELAY2
                    );

                }


            }
        });







        /*btn_lista_precios = (Button) findViewById(R.id.btn_lista_precios);
        btn_lista_precios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpListaPrecio();
            }
        });*/


        escaner_btn_fpago = (Button) findViewById(R.id.escaner_btn_fpago);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Documento Usa BT");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentoBluetooth.this,MainActivity.class);
                startActivity(intent);
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        escaner_fecha = (TextView) findViewById(R.id.escaner_fecha);
        escaner_fecha.setText(escaner_fecha.getText() + " " + currentDateandTime);


        escaner_folio = (TextView) findViewById(R.id.escaner_folio);
        escaner_folio.setText("");

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/listar/serie/contar/"+tipo_documento+"/");

        HttpAsyncTask13 c = new HttpAsyncTask13();
        c.execute(server + "/medialuna/spring/editorUsuario/permisos/usuario/app");

        HttpAsyncTask12 b = new HttpAsyncTask12();
        b.execute(server + "/medialuna/spring/listar/catalogo/1403/");

        HttpAsyncTask14 d = new HttpAsyncTask14();
        d.execute(server + "/medialuna/spring/app/buscarListaPrecios");

        mButton = (LinearLayout) findViewById(R.id.escaner_btn_escanear);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comprobarDatos() == true) {

                    if(blue_on == false){
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentOpenBluetoothSettings);
                        // mButton.setImageResource(R.drawable.b2);
                        blue_on = true;
                        et_escaner.setFocusable(true);
                        Toast.makeText(getApplicationContext(), "  " + et_escaner.isFocusable() ,Toast.LENGTH_LONG).show();

                    }
                    else{
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentOpenBluetoothSettings);
                        //  mButton.setImageResource(R.drawable.b1);
                        blue_on = false;
                        et_escaner.setFocusable(false);
                        Toast.makeText(getApplicationContext(), "  OJO " + et_escaner.isFocusable() ,Toast.LENGTH_LONG).show();

                    }



                } else {
                    Toast.makeText(DocumentoBluetooth.this, "Seleccione una Serie", Toast.LENGTH_SHORT).show();
                }
            }
        });


        escaner_btn_fpago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertReferencias();
            }
        });


        escaner_btn_cliente = (Button) findViewById(R.id.escaner_btn_cliente);
        escaner_btn_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCliente();


            }
        });


        escaner_btn_limp = (LinearLayout) findViewById(R.id.escaner_btn_limp);
        escaner_btn_limp.setEnabled(false);
        escaner_btn_limp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                cantidad_articulos = 0;
                subtotal.setText("0.0");
                impuestos.setText("0.0");
                total.setText("0.0");
                escaner_total.setText("0.0");
                escaner_txt_no_art.setText("0");
                while (codigos.getChildCount() != 1) {
                    codigos.removeViewAt(codigos.getChildCount() - 1);
                }
                productos_list.clear();
                lista_articulos.clear();
                escaner_btn_limp.setEnabled(false);


            }
        });


        escaner_btn_articulo = (LinearLayout) findViewById(R.id.escaner_btn_articulo);
        escaner_btn_articulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarDatos() == true) {
                    alertArticulo();
                } else {

                    Toast.makeText(DocumentoBluetooth.this, "Seleccione una Serie", Toast.LENGTH_SHORT).show();
                }


            }
        });


        escaner_btn_salir = (LinearLayout) findViewById(R.id.escaner_btn_salir);
        escaner_btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });



        escaner_btn_cambio = (LinearLayout) findViewById(R.id.escaner_btn_cambio);
        escaner_btn_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCambio();

            }
        });


        escaner_btn_serie = (Button) findViewById(R.id.escaner_btn_serie);
        escaner_btn_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/listar/serie/"+tipo_documento+"/");
            }
        });

        escaner_btn_agente = (Button) findViewById(R.id.escaner_btn_agente);
        escaner_btn_agente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpAsyncTask5 a = new HttpAsyncTask5();
                a.execute(server + "/medialuna/spring/listar/agente/VENDEDOR/");
            }
        });


        escaner_btn_grabar = (LinearLayout) findViewById(R.id.escaner_btn_grabar);
        escaner_btn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("SI ENTRO AL BOTON");

                if (laSerie.equals("")) {
                    Toast.makeText(DocumentoBluetooth.this, "Favor de elegir Serie ", Toast.LENGTH_LONG).show();

                } else if (elCliente.equals("") || elAgente.equals("")) {
                    Toast.makeText(DocumentoBluetooth.this, "Favor de elegir Cliente/Agente ", Toast.LENGTH_LONG).show();

                } else {

                    if (cantidad_articulos > 0) {
                        HttpAsyncTask8 a = new HttpAsyncTask8();
                        a.execute(server + "/medialuna/spring/documento/crear/app");
                    } else {
                        Toast.makeText(DocumentoBluetooth.this, "Documento Vacio", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ocultarTeclado();
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
            Intent localIntent = new Intent(DocumentoBluetooth.this.getApplicationContext(), LogIn.class);
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

        else{
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            if (resultCode == RESULT_OK) {
                if (scanResult != null) {
                    String re = scanResult.getContents();
                    barcode = re;

                    HttpAsyncTask3 a = new HttpAsyncTask3();
                    a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");


                    Toast.makeText(this, "Codigo:" + re, Toast.LENGTH_LONG).show();
                    Log.d("code", re);
                    //  TableRow tr = new TableRow(this);
                    //TextView c0 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
                    //c0.setText(re);
                    //  tr.addView(c0);
                    // codigos.addView(tr);


                }
            } else if (resultCode == RESULT_CANCELED) {
                // Toast.makeText(this, "Finalizado", Toast.LENGTH_LONG).show();
            }


            // else continue with any other code you need in the method


        }


    }


    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST3(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            //   Toast.makeText(getApplicationContext(), "onPostExecute[" + result + "]", Toast.LENGTH_SHORT).show();

            //  final IntentIntegrator integrator = new IntentIntegrator(Escaner.this);
            // integrator.setCaptureLayout(R.layout.custom_capture_layout);
            //  integrator.initiateScan();

            /*if(categoria_busqueda.equals("nombreCorto")){
                if (result.equals("[]") || result.equals("")) {
                    categoria_busqueda = "codigoSKU";
                    Toast.makeText(getApplicationContext(), "Codigo NO encontrado en NombreCorto", Toast.LENGTH_SHORT).show();
                    HttpAsyncTask3 a = new HttpAsyncTask3();
                    a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");
                } else {
                    categoria_busqueda = "nombreCorto";
                    Toast.makeText(getApplicationContext(), "Codigo encontrado", Toast.LENGTH_SHORT).show();
                    convertirDatosArticulo(result);
                    et_escaner.setText("");
                }


            }*/


            // else{
            if (result.equals("[]") || result.equals("")) {
                //      categoria_busqueda = "nombreCorto";
                Toast.makeText(getApplicationContext(), "Codigo NO encontrado", Toast.LENGTH_SHORT).show();
                et_escaner.setText("");
                et_escaner.requestFocus();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                mp.start();
            } else {
                // categoria_busqueda = "nombreCorto";
                Toast.makeText(getApplicationContext(), "Codigo encontrado", Toast.LENGTH_SHORT).show();
                convertirDatosArticulo(result);
                et_escaner.setText("");
                et_escaner.requestFocus();
            }


            //    }








        }
    }


    public static String POST3(String url) {
        if(laListaPrecio=="")
            laListaPrecio=elCliente;

        System.out.println("et-escaner " + et_escaner.getText().toString());
        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        if(et_escaner.getText().toString().contains("\n")){
            System.out.println("-->"+ et_escaner.getText().toString()+"<--");
            entidad.put(categoria_busqueda, et_escaner.getText().toString().substring(0, et_escaner.getText().toString().length()-1)); //nombreCorto
        }
        else{
            System.out.println("+++"+ et_escaner.getText().toString()+"+++");
            entidad.put(categoria_busqueda, et_escaner.getText().toString()); //nombreCorto
        }
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

        Map adicionales = new HashMap();
        adicionales.put("@class", HashMap.class.getName());
        adicionales.put("serie", Integer.parseInt(laSerie));
        adicionales.put("bodega",Integer.parseInt(laBodega));
        adicionales.put("lprecio", Integer.parseInt(laListaPrecio));
        adicionales.put("idTercero", Integer.parseInt(elCliente));
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
        map.put("pagerMaxResults", 1);
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






    public void convertirDatosArticulo(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_art.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);


            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), counter);
                lista_art.add(art);


                counter++;
                art.setCounter(counter);
                lista_articulos.add(art);


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


    public void crearTablaArticulos() {

        for (int j = 0; j < lista_art.size(); j++) {
            TableRow tr = new TableRow(this);
            escaner_btn_limp.setEnabled(true);
            final Articulo art = lista_art.get(j);

            final EditText c00 = new EditText(this);
            c00.setInputType(InputType.TYPE_CLASS_NUMBER);
            c00.setText("1");
            c00.setGravity(Gravity.RIGHT);
            //c00.setTextColor(Color.parseColor("#ffffff"));
            //c00.setBackgroundColor(Color.parseColor("#373737"));
            cantidad_articulos = cantidad_articulos + 1;
            final String auxc = c00.getText().toString();


            TextView c5 = new TextView(this);
            //c5.setTextColor(Color.parseColor("#ffffff"));
            c5.setText(lista_art.get(j).getDescripcion().toString());
            c5.setGravity(Gravity.CENTER);
            //c5.setBackgroundColor(Color.parseColor("#373737"));


            final EditText c51 = new EditText(this);
            c51.setText("0.0");
            c51.setGravity(Gravity.RIGHT);
            c51.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            c51.setSelectAllOnFocus(true);
            if (editar_descuento_permiso == false) {
                c51.setEnabled(false);
               // c51.setFocusable(false);
            }

            final EditText c6 = new EditText(this);
            c6.setText(Helper.formatDouble(lista_art.get(j).getPrecioBase()).toString());
            c6.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            c6.setSelectAllOnFocus(true);
            c6.setGravity(Gravity.RIGHT);
            if (editar_precio_permiso == false) {
                c6.setEnabled(false);
              //  c6.setFocusable(false);
            }

            final TextView c2_1 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
            c2_1.setText(Helper.formatDouble(lista_art.get(j).getPrecioBase()).toString());
            c2_1.setTextSize(25);
            c2_1.setGravity(Gravity.RIGHT);
            c2_1.setTextColor(Color.parseColor("#3D6AB3"));
            c2_1.setTypeface(null, Typeface.BOLD);
            if (Build.VERSION.SDK_INT >= 16) {

            if ((j % 2) == 0) {
                c00.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                c5.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                c51.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                c6.setBackground(getResources().getDrawable(R.drawable.alt_row_color));
                c2_1.setBackground(getResources().getDrawable(R.drawable.alt_row_color));

            } else {
                c00.setBackground(getResources().getDrawable(R.drawable.row_color));
                c5.setBackground(getResources().getDrawable(R.drawable.row_color));
                c51.setBackground(getResources().getDrawable(R.drawable.row_color));
                c6.setBackground(getResources().getDrawable(R.drawable.row_color));
                c2_1.setBackground(getResources().getDrawable(R.drawable.row_color));
            }
        }

            c00.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // auxc = c0.getText().toString();

                }

                @Override
                public void onTextChanged(final CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    //avoid triggering event when text is too short


                    if (s.length() > 0) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {


                                        if (c00.getText().toString().equals("0")) { //O o menor a 0
                                            setText(c00, "1");
                                        }
                                        Double cantidad_tmp;
                                        Double aux3 = Double.parseDouble(c2_1.getText().toString());
                                        cantidad_tmp = aux3 / Double.parseDouble(c6.getText().toString());
                                        Double total_double = (Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString())); //cantidad por articulo
                                        Double total_double2 = Double.parseDouble(subtotal.getText().toString()) - (Double.parseDouble(c2_1.getText().toString())) + total_double; //subtotal
                                        Double total_double3 = (-(Double.parseDouble(c2_1.getText().toString())) * (art.getImpuesto() / 100)) + (total_double * (art.getImpuesto() / 100));
                                        setText(impuestos, String.valueOf(Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) + total_double3)));
                                        setText(c2_1, Helper.formatDouble(total_double).toString());
                                        setText(subtotal, Helper.formatDouble(total_double2).toString());
                                        setText(total, String.valueOf(Helper.formatDouble((Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                        setText(escaner_total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                        cantidad_articulos = cantidad_articulos + Integer.parseInt(c00.getText().toString()) - Integer.valueOf(cantidad_tmp.intValue());
                                        setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));


                                        for (int i = 0; i < productos_list.size(); i++) {
                                            if (productos_list.get(i).get("counter").equals(art.getCounter())) {

                                                productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));

                                            }
                                        }

                                    }
                                },
                                DELAY1
                        );
                    }


                }
            });

            c51.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // auxc = c0.getText().toString();

                }

                @Override
                public void onTextChanged(final CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    //avoid triggering event when text is too short


                    if (s.length() > 0) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {

                                        if (c51.getText().toString().equals("")) { //O nada
                                            setText(c51, "0.0");
                                        }
                                        else {

                                            Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                            Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString()))/ 100)));
                                            setText(c2_1, Helper.formatDouble(aux31).toString());
                                            Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString())- tem_total + aux31));
                                            setText(subtotal, String.valueOf(subtotal_double));

                                            Double impuesto_double =  Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto())/100)) + (aux31 * ((art.getImpuesto())/100)));
                                            setText(impuestos, String.valueOf(impuesto_double));

                                            setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                            setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));


                                            for (int i = 0; i < productos_list.size(); i++) {
                                                if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                    productos_list.get(i).put("descuento", Double.parseDouble(c51.getText().toString()));

                                                }
                                            }


                                        }
                                    }
                                },
                                DELAY3
                        );
                    }


                }
            });


            c6.addTextChangedListener(new TextWatcher() {//<-Hereeeeeeeeeeeeeeeeeeeeeeeeee





                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    setText(c6, "0.0");

                }

                @Override
                public void onTextChanged(final CharSequence s, int start, int before,
                                          int count) {
                    // setText(c6, "0.01");
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    //avoid triggering event when text is too short

                    System.out.println("Here>" + c6.getText().toString() + "<");

                    if (s.length() > 0) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {

                                        if (c6.getText().toString().equals("")) { //O nada
                                            setText(c6, "0.0");
                                        }
                                        else {

                                            Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                            Double aux3 = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString()) * (( 100- Double.parseDouble(c51.getText().toString()))/100);
                                            setText(c2_1, Helper.formatDouble(aux3).toString());

                                            Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString())- tem_total + aux3));
                                            setText(subtotal,String.valueOf(subtotal_double));

                                            Double impuesto_double =  Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto())/100)) + (aux3 * ((art.getImpuesto())/100)));
                                            setText(impuestos, String.valueOf(impuesto_double));

                                            setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                            setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));

                                            for (int i = 0; i < productos_list.size(); i++) {
                                                if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                    productos_list.get(i).put("precio", Double.parseDouble(c6.getText().toString()));

                                                }
                                            }

                                        }
                                    }
                                },
                                DELAY3
                        );
                    }


                }
            });

            tr.addView(c00);
            tr.addView(c5);
            tr.addView(c6);
            tr.addView(c51);
            tr.addView(c2_1);


            Double total_double = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString());
            setText(subtotal, String.valueOf(Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) + total_double)));

            Double total_double2 = total_double * (art.getImpuesto() / 100);
            setText(impuestos, String.valueOf(Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) + total_double2)));


            setText(total, String.valueOf(Helper.formatDouble(Double.parseDouble(total.getText().toString()) + total_double + total_double2)));
            setText(escaner_total, total.getText().toString());
            setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));

            HashMap tmp = new HashMap();
            tmp.put("@class", HashMap.class.getName());
            tmp.put("id", art.getId());
            tmp.put("cantidad", 1);
            tmp.put("counter", counter);
            tmp.put("descuento", 0.0);
            tmp.put("precio", art.getPrecioBase());

            productos_list.add(tmp);

            if (Build.VERSION.SDK_INT >= 16) {
                if ((j % 2) == 0) {
                    tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                } else {
                    tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                }
            }


            codigos.addView(tr,1); // unica
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
//daniel_dlb@yahoo.com

        @Override
        protected void onPostExecute(String result) {

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


    public void convertirDatos(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> arrayData0 = mapper.readValue(cadena, HashMap.class);




            if(arrayData0.containsKey("serie")) {
                HashMap<String, Object> arrayData = (HashMap<String, Object>) arrayData0.get("serie");
                String idS = arrayData.get("id").toString();
                String nombreS = arrayData.get("nombre").toString();
                laSerie = idS;
                escaner_txt_serie.setText(nombreS);
                Object cliente1 = (Object) arrayData.get("cliente");
                Object bodega1 = (Object) arrayData.get("bodega");

                HttpAsyncTask6 c = new HttpAsyncTask6();
                c.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/"+tipo_documento+"/");

                HashMap<String, Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                HashMap<String, Object> bodega = (HashMap<String, Object>) arrayData.get("bodega");

                if (cliente1 == null) {

                } else {
                    //HashMap<String,Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                    String codigoC = cliente.get("id").toString();
                    String nombreC = cliente.get("nombre").toString();

                    if (cliente.containsKey("agente") && cliente.get("agente")!=null) {
                        System.out.println("Agente "  + cliente.get("agente").toString());
                        HashMap<String, Object> agente = (HashMap<String, Object>) cliente.get("agente");
                        if(agente.get("id")!=null){
                        String codigoA = agente.get("id").toString();
                        String nombreA = agente.get("nombre").toString();
                        elAgente = codigoA;
                        escaner_txt_agente.setText(nombreA);}
                    }


                    List<Object> direcciones = (List) cliente.get("direcciones");
                    List<HashMap> direccionimpl = (List) direcciones.get(1);
                    HashMap direccion_impl_0 = direccionimpl.get(0);
                    laDireccion = (Integer) direccion_impl_0.get("id");

                    elCliente = codigoC;
                    escaner_txt_cliente.setText(nombreC);



                    if (cliente.containsKey("moneda") && cliente.get("moneda") != null) {
                        Map mapa2 = (Map) cliente.get("moneda");
                        laMoneda = (Integer) mapa2.get("id");
                    }
                }

                if(bodega1 == null){}
                else{
                    String codigoB = bodega.get("id").toString();
                    laBodega = codigoB;

                }




                if (Integer.parseInt(arrayData0.get("total").toString()) >= 1 && laSerie.equals("")) {//==
                    laSerie = idS;
                    escaner_txt_serie.setText(nombreS);
                    HttpAsyncTask6 b = new HttpAsyncTask6();
                    b.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/"+tipo_documento+"/");

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


    private class HttpAsyncTask6 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST6(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("***************************** ******************************** ***************************************    " + result);
            setText(escaner_folio, result);



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


    public void alertCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.busqueda_cliente, null);
        builder.setView(dialogView);

        Button busclie_btn_aceptar = (Button) dialogView.findViewById(R.id.busclie_btn_aceptar);
        Button busclie_btn_cancelar = (Button) dialogView.findViewById(R.id.busclie_btn_cancelar);


        final EditText busclie_etx_nom = (EditText) dialogView.findViewById(R.id.busclie_etx_nom);
        final EditText busclie_etx_app = (EditText) dialogView.findViewById(R.id.busclie_etx_app);
        final EditText busclie_etx_apm = (EditText) dialogView.findViewById(R.id.busclie_etx_apm);
        final EditText busclie_etx_rfc = (EditText) dialogView.findViewById(R.id.busclie_etx_rfc);


        final AlertDialog dialog = builder.create();

        busclie_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp.clear();
                tmp.add(busclie_etx_nom.getText().toString());
                tmp.add(busclie_etx_app.getText().toString());
                tmp.add(busclie_etx_apm.getText().toString());
                tmp.add(busclie_etx_rfc.getText().toString());
                if (tmp.get(0).length() <= 1 && tmp.get(1).length() <= 1 && tmp.get(2).length() <= 1 && tmp.get(3).length() <= 1) {
                    Toast.makeText(DocumentoBluetooth.this, "Favor de ingresar mayor caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    HttpAsyncTask2 a = new HttpAsyncTask2();
                    a.execute(server + "/medialuna/spring/listar/pagina/tercero/cliente/");
                    dialog.cancel();
                }
            }
        });

        busclie_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
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
        if (tmp.get(0).length() <= 1) {
        } else {
            map1.put("nombre", tmp.get(0));
        }
        if (tmp.get(1).length() <= 1) {
        } else {
            map1.put("paterno", tmp.get(1));
        }
        if (tmp.get(2).length() <= 1) {
        } else {
            map1.put("materno", tmp.get(2));
        }
        if (tmp.get(3).length() <= 1) {
        } else {
            map1.put("rfc", tmp.get(3));
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
                    } else {
                        nombre_agente.add("");
                    }
                    if (mapa2.containsKey("id")) {
                        id_agente.add(String.valueOf((Integer) mapa2.get("id")));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);
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
                        escaner_txt_cliente.setText(nombre_cliente.get(selected));
                        elCliente = id_cliente.get(selected).toString();
                        elAgente = id_agente.get(selected).toString();
                        escaner_txt_agente.setText(nombre_agente.get(selected));
                        laDireccion = direccion_cliente.get(selected);
                        laMoneda = id_moneda.get(selected);

                        break;
                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + elCliente + " " + nombre_cliente.get(selected), Toast.LENGTH_LONG).show();

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

    public void alertReferencias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);

        LayoutInflater inflater = DocumentoBluetooth.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.referencias2, null);
        builder.setView(dialogView);

        final TableLayout tabla_efectivo = (TableLayout) dialogView.findViewById(R.id.tabla_efectivo);
        final TableLayout tabla_credito = (TableLayout) dialogView.findViewById(R.id.tabla_credito);

        final TextView total_tv = (TextView) dialogView.findViewById(R.id.total_textView);
        total_tv.setText(total.getText());

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
                c51.setText(total.getText());
                c00.setChecked(true);
            }
            if (formasPago.get(i).get("credito").toString().equals("true")) {
                tabla_credito.addView(tr);
            } else {
                tabla_efectivo.addView(tr);
            }
        }
        temporal_resta = BigDecimal.valueOf(0.0);
        for (int i = 0; i < check_array_formas2.size(); i++) {
            final int aux = i;
            check_array_formas2.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        temporal_resta = BigDecimal.valueOf(0.0);
                        for (int j = 0; j < etx_array_formas2.size(); j++) {
                            if ((etx_array_formas2.get(j).isEnabled() == false) || (etx_array_formas2.get(j).getText().toString().equals("")) || (check_array_formas2.get(j).isChecked() == false)) {} else {
                                temporal_resta = temporal_resta.add(new BigDecimal(etx_array_formas2.get(j).getText().toString()));
                            }
                        }
                        Toast.makeText(getApplicationContext(), " Temporal Resta" + temporal_resta, Toast.LENGTH_SHORT).show();

                        etx_array_formas2.get(aux).setText(String.valueOf((new BigDecimal(total.getText().toString())).subtract(temporal_resta)));
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
                                total_temp = total_temp.add(new BigDecimal(etx_array_formas2.get(i).getText().toString()));
                            }
                        }
                    }
                }
                if (total_temp.toString().equals(total.getText().toString())) {
                    formas_estado = true;
                    dialog.cancel();
                } else {
                    System.out.println("      ----> " + total_temp + "   ----> " + total.getText().toString());
                    Toast.makeText(getApplicationContext(), "Favor de verificar los totales", Toast.LENGTH_SHORT).show();
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


    private void build_popup() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);
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
                        laBodega = id_bodega.get(selected).toString();
                        escaner_txt_serie.setText(nombre_serie.get(selected));

                        break;
                    //   case 0:text = "Bad";break;

                    //  case 1:text = "Good";break;

                    // case 2:text = "Very Good";break;

                    //   case 3:text = "Average";break;

                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie + " " + nombre_serie.get(selected) + " " + selected, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie, Toast.LENGTH_LONG).show();
                HttpAsyncTask6 a = new HttpAsyncTask6();
                a.execute(server + "/medialuna/spring/documento/obtener/folio/" + laSerie + "/" + tipo_documento + "/");


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
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_serie.clear();
            id_serie.clear();
            id_bodega.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                HashMap<String, Object> bodega = (HashMap<String, Object>)mapa1.get("bodega");
                Serie serietmp = new Serie((Integer) mapa1.get("id"), (Integer) mapa1.get("códigoUsuario"), (String) mapa1.get("nombreCorto"), (String) mapa1.get("nombre"),(Integer) bodega.get("id"));
                nombre_serie.add(serietmp.getNombre());
                id_serie.add(String.valueOf(serietmp.getId()));
                id_bodega.add(serietmp.getBodega());

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


    public void alertCambio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);

        LayoutInflater inflater = DocumentoBluetooth.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cambio, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        final TextView cambio_txv_total = (TextView) dialogView.findViewById(R.id.cambio_txv_total);
        final EditText cambio_etx_cambio = (EditText) dialogView.findViewById(R.id.cambio_etx_cambio);

        cambio_txv_total.setText(escaner_total.getText().toString());

        Button cambio_btn_cancelar = (Button) dialogView.findViewById(R.id.cambio_btn_cancelar);


        Button cambio_btn_generar = (Button) dialogView.findViewById(R.id.cambio_btn_generar);


        if (cambio_txv_total.getText().toString().equals("0.0")) {
            cambio_btn_generar.setEnabled(false);
            cambio_etx_cambio.setEnabled(false);

        }
        // Create the alert dialog
        final AlertDialog dialog = builder.create();


        cambio_btn_generar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Double.parseDouble(cambio_etx_cambio.getText().toString()) >= Double.parseDouble(cambio_txv_total.getText().toString())) {
                    Double cambio = 0.0;


                    cambio = Double.parseDouble(cambio_etx_cambio.getText().toString()) - Double.parseDouble(cambio_txv_total.getText().toString());


                    dialog.cancel();


                    final AlertDialog alert;

                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(DocumentoBluetooth.this);
                    alert = dialog2.create();

                    alert.setMessage("Cambio:" + cambio.toString());

                    alert.setButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            alert.cancel();
                        }
                    });


                    alert.show();

                } else {

                    Toast.makeText(DocumentoBluetooth.this, "Cantidad menor al total", Toast.LENGTH_SHORT);

                }
            }
        });


        cambio_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        // Display the custom alert dialog on interface
        dialog.show();
    }


    private class HttpAsyncTask5 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST5(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            convertirDatos5(result);
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


    public void convertirDatos5(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombre_agente.clear();
            id_agente.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                //Serie serietmp = new Serie((Integer) mapa1.get("id"), (Integer) mapa1.get("códigoUsuario"), (String) mapa1.get("nombreCorto"), (String) mapa1.get("nombre"));
                nombre_agente.add((String) mapa1.get("nombre"));
                id_agente.add(String.valueOf((Integer) mapa1.get("id")));

            }

            build_popup3();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    private void build_popup3() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);
        builder.setTitle("Agente");
        builder.setSingleChoiceItems(nombre_agente.toArray(new String[nombre_agente.size()]), 0, new DialogInterface.OnClickListener() {

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
                        elAgente = id_agente.get(selected);
                        escaner_txt_agente.setText(nombre_agente.get(selected));
                        break;
                    //   case 0:text = "Bad";break;

                    //  case 1:text = "Good";break;

                    // case 2:text = "Very Good";break;

                    //   case 3:text = "Average";break;

                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + elAgente + " " + nombre_agente.get(selected), Toast.LENGTH_LONG).show();
                dialog.cancel();


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


    public void alertArticulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);

        LayoutInflater inflater = DocumentoBluetooth.this.getLayoutInflater();
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
                onCheckboxClicked();

                dialog.cancel();
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Segun se oculta", Toast.LENGTH_SHORT).show();
               // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
               // imm.hideSoftInputFromWindow(nombre.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                prices.removeAllViews();

                if (corto.getText().toString().equals("") && nombre.getText().toString().equals("") && sku.getText().toString().equals("")) {

                } else {

                   /// Toast.makeText(getApplicationContext(), "Segun se oculta", Toast.LENGTH_SHORT).show();


                    HttpAsyncTask7 a = new HttpAsyncTask7();
                    a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");
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
            convertirDatosArticulo2(result);

        }
    }


    public static String POST7(String url) {

        if(laListaPrecio=="")
            laListaPrecio=elCliente;

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        agregarCamposEntidad(corto.getText().toString(), entidad, "nombreCorto");
        agregarCamposEntidad(nombre.getText().toString(), entidad, "nombre");
        agregarCamposEntidad(sku.getText().toString(), entidad, "codigoSKU");
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

        Map adicionales = new HashMap();
        adicionales.put("@class", HashMap.class.getName());
        adicionales.put("serie", Integer.parseInt(laSerie));
        adicionales.put("bodega", Integer.parseInt(laBodega));
        adicionales.put("lprecio", Integer.parseInt(laListaPrecio));
        adicionales.put("idTercero", Integer.parseInt(elCliente));
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
            System.out.println("7 Lo que se envia : " + objectStr.toString());
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


    public void onCheckboxClicked() {


        for (int i = 0; i < chbx_selected.size(); i++) {

            if (chbx_selected.get(i).isChecked()) {
                escaner_btn_limp.setEnabled(true);

                counter = counter + 1;
                lista_art.get(i).setCounter(counter);


                lista_art2.add(lista_art.get(i));
                lista_articulos.add(lista_art.get(i));


                TableRow tr = new TableRow(DocumentoBluetooth.this);
                // tr.setBackgroundResource(R.drawable.border);

                // android:baselineAligned="false"
                //   tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final Articulo art = lista_art.get(i);

                final EditText c00 = new EditText(DocumentoBluetooth.this);
                c00.setInputType(InputType.TYPE_CLASS_NUMBER);
                c00.setText("1");
                c00.setGravity(Gravity.CENTER);
                // c00.setTextColor(Color.parseColor("#ffffff"));
                //  c00.setBackgroundColor(Color.parseColor("#373737"));
                cantidad_articulos = cantidad_articulos + 1;

                final String auxc = c00.getText().toString();


                TextView c5 = new TextView(this);
                // c5.setTextColor(Color.parseColor("#ffffff"));
                c5.setText(art.getDescripcion().toString());
                c5.setGravity(Gravity.CENTER);
                // c5.setBackgroundColor(Color.parseColor("#373737"));

                final EditText c51 = new EditText(DocumentoBluetooth.this);
                c51.setText("0.0");
                c51.setGravity(Gravity.RIGHT);
                c51.setSelectAllOnFocus(true);
                c51.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if(editar_descuento_permiso==false){
                    c51.setEnabled(false);
                   // c51.setFocusable(false);
                }


                final EditText c6 = new EditText(this);
                c6.setText(Helper.formatDouble(art.getPrecioBase()).toString());
                c6.setGravity(Gravity.RIGHT);
                // c6.setBackgroundColor(Color.RED);
                //    c6.setTextColor(Color.parseColor("#ffffff"));
                c6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                c6.setSelectAllOnFocus(true);
                if(editar_precio_permiso==false){
                    c6.setEnabled(false);
                   // c6.setFocusable(false);
                }

                final TextView c2_1 = new TextView(this);
                c2_1.setTextSize(25);
                c2_1.setTextColor(Color.parseColor("#3D6AB3"));
                c2_1.setTypeface(null, Typeface.BOLD);
                c2_1.setText(Helper.formatDouble(art.getPrecioBase()).toString());
                c2_1.setGravity(Gravity.RIGHT);
                //  c2_1.setBackgroundColor(Color.parseColor("#373737"));
                //  c2_1.setTextColor(Color.parseColor("#ffffff"));




                c00.addTextChangedListener(new TextWatcher() {


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {


                                            if (c00.getText().toString().equals("0")) { //O nada
                                                setText(c00, "1");
                                            }

                                            Double cantidad_tmp;
                                            Double aux3 = Double.parseDouble(c2_1.getText().toString());
                                            cantidad_tmp = aux3 / Double.parseDouble(c6.getText().toString());
                                            Double total_double = (Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString())); //cantidad por articulo
                                            Double total_double2 = Double.parseDouble(subtotal.getText().toString()) - (Double.parseDouble(c2_1.getText().toString())) + total_double; //subtotal
                                            Double total_double3 = (-(Double.parseDouble(c2_1.getText().toString())) * (art.getImpuesto() / 100)) + (total_double * (art.getImpuesto() / 100));
                                            setText(impuestos, String.valueOf(Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) + total_double3)));
                                            setText(c2_1, Helper.formatDouble(total_double).toString());
                                            setText(subtotal, Helper.formatDouble(total_double2).toString());
                                            setText(total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                            setText(escaner_total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));

                                            cantidad_articulos = cantidad_articulos + Integer.parseInt(c00.getText().toString()) - Integer.valueOf(cantidad_tmp.intValue());
                                            setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));


                                            for (int i = 0; i < productos_list.size(); i++) {
                                                if (productos_list.get(i).get("counter").equals(art.getCounter())) {

                                                    productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));

                                                }
                                            }

                                        }
                                    },
                                    DELAY1
                            );
                        }


                    }
                });

                c51.addTextChangedListener(new TextWatcher() {


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // auxc = c0.getText().toString();

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short


                        if(s.length()==0){
                            System.out.println("  ############################################################################################################################## ###########################");
                        }


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {

                                            if (c51.getText().toString().equals("")) { //O nada
                                                setText(c51, "0.0");
                                            }
                                            else {

                                                Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                                Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString()))/ 100)));
                                                setText(c2_1, Helper.formatDouble(aux31).toString());
                                                Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString())- tem_total + aux31));
                                                setText(subtotal, String.valueOf(subtotal_double));

                                                Double impuesto_double =  Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto())/100)) + (aux31 * ((art.getImpuesto())/100)));
                                                setText(impuestos, String.valueOf(impuesto_double));

                                                setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                                setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));


                                                for (int i = 0; i < productos_list.size(); i++) {
                                                    if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                        productos_list.get(i).put("descuento", Double.parseDouble(c51.getText().toString()));

                                                    }
                                                }


                                            }
                                        }
                                    },
                                    DELAY3
                            );
                        }


                    }
                });




                c6.addTextChangedListener(new TextWatcher() {//<-Hereeeeeeeeeeeeeeeeeeeeeeeeee


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // auxc = c0.getText().toString();

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short
                        //   System.out.println("Here>" + c6.getText().toString() + "<");

                        // if (s.length() == 0) {
                        //       setText(c6, "0.0");
                        //   }


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {

                                            if (c6.getText().toString().equals("")) { //O nada
                                                setText(c6, "0.0");
                                            }
                                            else {

                                                Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                                Double aux3 = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString()) * (( 100- Double.parseDouble(c51.getText().toString()))/100);
                                                setText(c2_1, Helper.formatDouble(aux3).toString());

                                                Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString())- tem_total + aux3));
                                                setText(subtotal,String.valueOf(subtotal_double));

                                                Double impuesto_double =  Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto())/100)) + (aux3 * ((art.getImpuesto())/100)));
                                                setText(impuestos, String.valueOf(impuesto_double));

                                                setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                                setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));

                                                for (int i = 0; i < productos_list.size(); i++) {
                                                    if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                        productos_list.get(i).put("precio", Double.parseDouble(c6.getText().toString()));

                                                    }
                                                }

                                            }
                                        }
                                    },
                                    DELAY3
                            );
                        }



                    }
                });

                tr.addView(c00);
                tr.addView(c5);
                tr.addView(c6);
                tr.addView(c51);
                tr.addView(c2_1);

                Double total_double = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString());
                setText(subtotal, String.valueOf(Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) + total_double)));

                Double total_double2 = total_double * (art.getImpuesto() / 100);
                setText(impuestos, String.valueOf(Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) + total_double2)));


                setText(total, String.valueOf(Helper.formatDouble(Double.parseDouble(total.getText().toString()) + total_double + total_double2)));
                setText(escaner_total, total.getText().toString());
                setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));

                HashMap tmp = new HashMap();
                tmp.put("@class", HashMap.class.getName());
                tmp.put("id", art.getId());
                tmp.put("cantidad", 1);
                tmp.put("counter", counter);
                tmp.put("descuento", 0.0);
                tmp.put("precio", art.getPrecioBase());

                productos_list.add(tmp);

                if (Build.VERSION.SDK_INT >= 16) {
                    if ((i % 2) == 0) {
                        tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                    } else {
                        tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                    }
                }


                codigos.addView(tr,1);
            }


        }

        escaner_txt_no_art.setText(String.valueOf(cantidad_articulos));
    }


    public void convertirDatosArticulo2(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            lista_art.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);

            // counter = counter + 1;
            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), counter);

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
       // ocultarTeclado();
        /*View view = this.getCurrentFocus();
        view.clearFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/
        //onBackPressed();
        if(!corto.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "corto", Toast.LENGTH_SHORT).show();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm2.hideSoftInputFromWindow(corto.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        }

        if(!nombre.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "nombre", Toast.LENGTH_SHORT).show();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm2.hideSoftInputFromWindow(nombre.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        }

        if(!sku.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "sku", Toast.LENGTH_SHORT).show();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm2.hideSoftInputFromWindow(sku.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        }

        if (lista_art.isEmpty()) {
            TextView c1 = new TextView(DocumentoBluetooth.this);
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            for (int j = 0; j < lista_art.size(); j++) {
                // counter = counter +1;

                TableRow tr = new TableRow(DocumentoBluetooth.this);
                CheckBox c0 = new CheckBox(DocumentoBluetooth.this);
                c0.setId(R.id.select_art + j);
                chbx_selected.add(c0);

                TextView c1 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(lista_art.get(j).getId().toString());

                TextView c2 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c2.setText(lista_art.get(j).getDescripcion().toString());

                TextView c3 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c3.setText(lista_art.get(j).getExistencia().toString());

                TextView c4 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c4.setText(lista_art.get(j).getNombre().toString());

                TextView c5 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c5.setText(lista_art.get(j).getNombreCorto().toString());

                TextView c6 = (TextView) DocumentoBluetooth.this.getLayoutInflater().inflate(R.layout.renglones, null);
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


    private class HttpAsyncTask8 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST8(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Documento creado" + result, Toast.LENGTH_LONG).show();
            id = convertirDatosDocumento(result);
            alertRemisionCreada();

        }
    }


    public String POST8(String url)  {
        JSONArray array = new JSONArray();
        ArrayList tmp = new ArrayList();
        for (int i = 0; i < productos_list.size(); i++) {
            JSONObject jsonOBJECT2 = new JSONObject(productos_list.get(i));
            tmp.add(jsonOBJECT2);
            array.put(jsonOBJECT2);
        }



        String array_string = "";
        for(int i = 0; i< productos_list.size(); i++){
            array_string += productos_list.get(i).toString();

        }



        HashMap t0 = new HashMap();
        t0.put("producto", productos_list);
        t0.put("@class", ArrayList.class.getName());



        HashMap t1 = new HashMap();
        t1.put("@class", HashMap.class.getName());
        t1.put("id0",subtotal.getText().toString());
        t1.put("id1","0.0");
        t1.put("id2","0.0");
        t1.put("id3","0.0");
        t1.put("id4",subtotal.getText().toString());
        t1.put("id5","0.0");
        t1.put("id6","0.0");
        t1.put("id7",subtotal.getText().toString());
        t1.put("id8",impuestos.getText().toString());
        t1.put("id9","0.0");
        t1.put("id10","0.0");
        t1.put("id11","0.0");
        t1.put("id12", total.getText().toString());
        if(formasPago_tmp.size()==1){
            formasPago_tmp.get(0).put("valor", total.getText().toString());
        }

        HashMap map3 = new HashMap();
        map3.put("formasPago", formasPago_tmp);
        map3.put("@class", ArrayList.class.getName());

        HashMap map2 = new HashMap();
        map2.put("id_direccion", laDireccion);
        map2.put("folio", escaner_folio.getText().toString());
        map2.put("tercero", elCliente);  //id Tercero
        map2.put("agente", Integer.parseInt(elAgente));
        map2.put("categoria",laSerie);
        map2.put("serie", laSerie);
        map2.put("entidad", buscarEntidad(tipo_documento));
        map2.put("moneda", laMoneda);
        map2.put("@class", HashMap.class.getName());


        Map totalisimo = new HashMap();
        totalisimo.put("map3",map3.toString());
        totalisimo.put("map2",map2.toString());
        totalisimo.put("t1",t1.toString());
        totalisimo.put("t0",t0.toString());
        totalisimo.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(totalisimo);
        //  JSONObject jsonOBJECT2 = new JSONObject(t1);
        //  JSONObject jsonOBJECT3 = new JSONObject(t0);
        // try {
        //      jsonOBJECT3.put("producto", array);
        //      jsonOBJECT3.put("@class", HashMap.class.getName());
        //  } catch (JSONException e) {
        //    e.printStackTrace();
        //  }

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String fpago = escaner_etx_fpago.getText().toString();
        savedInstanceState.putString("Cliente", escaner_txt_cliente.getText().toString());
        savedInstanceState.putString("Fpago", fpago);
        savedInstanceState.putString("Serie", escaner_txt_serie.getText().toString());
        savedInstanceState.putString("Agente", escaner_txt_agente.getText().toString());
        savedInstanceState.putString("Fecha", escaner_fecha.getText().toString());
        savedInstanceState.putString("Folio", escaner_folio.getText().toString());
        savedInstanceState.putString("Noart", escaner_txt_no_art.getText().toString());
        savedInstanceState.putString("Subtotal", subtotal.getText().toString());
        savedInstanceState.putString("Impuestos", impuestos.getText().toString());
        savedInstanceState.putString("Total", total.getText().toString());

        savedInstanceState.putString("IdSerie", laSerie);
        savedInstanceState.putString("IdCliente", elCliente);
        savedInstanceState.putString("IdAgente", elAgente);
        savedInstanceState.putString("IdDireccion", laDireccion.toString());
        savedInstanceState.putInt("Counter", counter);
        savedInstanceState.putString("pdf_flag", String.valueOf(pdf_flag));

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
        contenido_map.put("lista_art", lista_art);
        contenido_map.put("lista_art2", lista_articulos);
        contenido_map.put("productos_list", productos_list);
        System.out.println("On Save: productos_list: " + productos_list);
        savedInstanceState.putSerializable("contenido", contenido_map);
        System.out.println("On Save: escaner_etx_fpago: " + fpago);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        //savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
        super.onRestoreInstanceState(savedInstanceState);


        total.setText(savedInstanceState.getString("Total"));
        escaner_txt_cliente.setText(savedInstanceState.getString("Cliente"));
        escaner_etx_fpago.setText(savedInstanceState.getString("Fpago"));
        escaner_txt_serie.setText(savedInstanceState.getString("Serie"));
        escaner_txt_agente.setText(savedInstanceState.getString("Agente"));
        escaner_fecha.setText(savedInstanceState.getString("Fecha"));
        escaner_folio.setText(savedInstanceState.getString("Folio"));
        escaner_txt_no_art.setText(savedInstanceState.getString("Noart"));
        subtotal.setText(savedInstanceState.getString("Subtotal"));
        impuestos.setText(savedInstanceState.getString("Impuestos"));
        //total.setText("0.0");
        escaner_total.setText(savedInstanceState.getString("Total"));


        laSerie = savedInstanceState.getString("IdSerie");
        elCliente = savedInstanceState.getString("IdCliente");
        elAgente = savedInstanceState.getString("IdAgente");
        laDireccion = Integer.parseInt(savedInstanceState.getString("IdDireccion"));

        first_time = savedInstanceState.getInt("Ftime");
        counter = savedInstanceState.getInt("Counter");
        HashMap<String, Object> contenido_map = (HashMap<String, Object>) savedInstanceState.getSerializable("contenido");

        onCheckboxClicked2(contenido_map);

        lista_art = (List<Articulo>) contenido_map.get("lista_art");
        lista_articulos = (List<Articulo>) contenido_map.get("lista_art2");
        productos_list = (List<HashMap>) contenido_map.get("productos_list");
        System.out.println("On Restore producots_list: " + productos_list);
        System.out.println("On Restore: escaner_etx_fpago: " + escaner_etx_fpago.getText().toString());
        pdf_flag =   Boolean.valueOf(savedInstanceState.getString("pdf_flag"));

        if(pdf_flag){
            alertRemisionCreada();

        }

    }


    public void onCheckboxClicked2(HashMap<String, Object> contenido) {
        List<Articulo> lista_art_tmp = (List<Articulo>) contenido.get("lista_art");
        List<Articulo> lista_art2_tmp = (List<Articulo>) contenido.get("lista_art2");
        List<HashMap> productos_list_tmp = (List<HashMap>) contenido.get("productos_list");//Error aqui


        if (lista_art_tmp.isEmpty() && lista_art2_tmp.isEmpty() && productos_list_tmp.isEmpty()) {

        } else {

            for (int i = 0; i < lista_art2_tmp.size(); i++) {


                escaner_btn_limp.setEnabled(true);

                lista_art2.add(lista_art2_tmp.get(i));  //???????


                TableRow tr = new TableRow(DocumentoBluetooth.this);
                tr.setBackgroundResource(R.drawable.border);

                // android:baselineAligned="false"
                //   tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final Articulo art = lista_art2_tmp.get(i); //?????????

                final EditText c00 = new EditText(DocumentoBluetooth.this);
                c00.setInputType(InputType.TYPE_CLASS_NUMBER);
                c00.setText(productos_list_tmp.get(i).get("cantidad").toString());
                c00.setGravity(Gravity.CENTER);
                //  c00.setTextColor(Color.parseColor("#ffffff"));
                // c00.setBackgroundColor(Color.parseColor("#373737"));
                cantidad_articulos = cantidad_articulos + Integer.parseInt(productos_list_tmp.get(i).get("cantidad").toString());

                final String auxc = c00.getText().toString();


                TextView c5 = new TextView(this);
                c5.setText(art.getDescripcion());
                c5.setGravity(Gravity.CENTER);

                final EditText c51 = new EditText(this);
                c51.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                c51.setText(productos_list_tmp.get(i).get("precio").toString());
                c51.setGravity(Gravity.RIGHT);
                c51.setSelectAllOnFocus(true);
                if(editar_precio_permiso==false){
                    c51.setEnabled(false);
                   // c51.setFocusable(false);
                }

                final EditText c6 = new EditText(this);
                c6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                c6.setText(productos_list_tmp.get(i).get("descuento").toString());
                c6.setGravity(Gravity.RIGHT);
                c6.setSelectAllOnFocus(true);
                if(editar_descuento_permiso==false){
                    c6.setEnabled(false);
                 //   c6.setFocusable(false);
                }

                final TextView c2_1 = new TextView(this);
                Double total_tmp = 0.0;
                Integer tmp_int = (Integer) productos_list_tmp.get(i).get("cantidad");
                total_tmp = Double.parseDouble(tmp_int.toString());
                total_tmp = ((Double)productos_list_tmp.get(i).get("precio")) * total_tmp;
                total_tmp = total_tmp * (1-((Double)productos_list_tmp.get(i).get("descuento"))/100);
                c2_1.setText(Helper.formatDouble(total_tmp).toString());
                c2_1.setGravity(Gravity.RIGHT);

                c00.addTextChangedListener(new TextWatcher() {


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {


                                            if (c00.getText().toString().equals("0")) { //O nada
                                                setText(c00, "1");
                                            }

                                            Double cantidad_tmp = 0.0;
                                            Double aux3 = Double.parseDouble(c2_1.getText().toString());
                                            cantidad_tmp = aux3 / Double.parseDouble(c51.getText().toString());
                                            Double total_double = (Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c51.getText().toString())); //cantidad por articulo

                                            Double total_double2 = Double.parseDouble(subtotal.getText().toString()) - (Double.parseDouble(c51.getText().toString())) + total_double; //subtotal
                                            Double total_double3 = (-(Double.parseDouble(c51.getText().toString())) * (art.getImpuesto() / 100)) + (total_double * (art.getImpuesto() / 100));

                                            System.out.println("Datos: " + "    cantidad_tmp " + cantidad_tmp + " aux3 " + aux3 + "      total_double " + total_double + "     total_double2 " + total_double2 + "       total_double3 " + total_double3);

                                            setText(impuestos, String.valueOf(Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) + total_double3)));
                                            setText(c2_1, Helper.formatDouble(total_double).toString());
                                            setText(subtotal, Helper.formatDouble(total_double2).toString());
                                            setText(total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                            setText(escaner_total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));

                                            System.out.println("prod cantidad_articulos 1:" + cantidad_articulos);
                                            cantidad_articulos = cantidad_articulos + Integer.parseInt(c00.getText().toString()) - Integer.valueOf(cantidad_tmp.intValue());
                                            System.out.println("prod cantidad_articulos 2:" + cantidad_articulos);
                                            setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));


                                            for (int i = 0; i < productos_list.size(); i++) {
                                                if (productos_list.get(i).get("counter").equals(art.getCounter())) {

                                                    productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));

                                                }
                                            }
                                        }


                                    },
                                    DELAY1
                            );
                        }

                    }

                });


                c6.addTextChangedListener(new TextWatcher() {


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // auxc = c0.getText().toString();

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {

                                            if (c6.getText().toString().equals("")) { //O nada
                                                setText(c6, "0.0");
                                            } else {

                                                Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                                Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c51.getText().toString()) * ((100 - Double.parseDouble(c6.getText().toString())) / 100)));
                                                setText(c2_1, Helper.formatDouble(aux31).toString());
                                                Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) - tem_total + aux31));
                                                setText(subtotal, String.valueOf(subtotal_double));

                                                Double impuesto_double = Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto()) / 100)) + (aux31 * ((art.getImpuesto()) / 100)));
                                                setText(impuestos, String.valueOf(impuesto_double));

                                                setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                                setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));


                                                for (int i = 0; i < productos_list.size(); i++) {
                                                    if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                        productos_list.get(i).put("descuento", Double.parseDouble(c6.getText().toString()));

                                                    }
                                                }


                                            }
                                        }
                                    },
                                    DELAY3
                            );
                        }


                    }
                });




                c51.addTextChangedListener(new TextWatcher() {//<-Hereeeeeeeeeeeeeeeeeeeeeeeeee


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // auxc = c0.getText().toString();

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        //avoid triggering event when text is too short
                        //   System.out.println("Here>" + c6.getText().toString() + "<");

                        // if (s.length() == 0) {
                        //       setText(c6, "0.0");
                        //   }


                        if (s.length() > 0) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {

                                            if (c51.getText().toString().equals("")) { //O nada
                                                setText(c51, "0.0");
                                            }
                                            else {

                                                Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                                Double aux3 = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c51.getText().toString()) * (( 100- Double.parseDouble(c6.getText().toString()))/100);
                                                setText(c2_1, Helper.formatDouble(aux3).toString());

                                                Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString())- tem_total + aux3));
                                                setText(subtotal,String.valueOf(subtotal_double));

                                                Double impuesto_double =  Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto())/100)) + (aux3 * ((art.getImpuesto())/100)));
                                                setText(impuestos, String.valueOf(impuesto_double));

                                                setText(total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));
                                                setText(escaner_total, String.valueOf((Helper.formatDouble(subtotal_double + impuesto_double))));

                                                for (int i = 0; i < productos_list.size(); i++) {
                                                    if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                        productos_list.get(i).put("precio", Double.parseDouble(c51.getText().toString()));

                                                    }
                                                }

                                            }
                                        }
                                    },
                                    DELAY3
                            );
                        }



                    }
                });

                tr.addView(c00);
                tr.addView(c5);
                tr.addView(c51);
                tr.addView(c6);
                tr.addView(c2_1);

                if (Build.VERSION.SDK_INT >= 16) {
                    if ((i % 2) == 0) {
                        tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                    } else {
                        tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                    }
                }


                codigos.addView(tr,1);


            }
        }


    }







    public String createExternalStoragePrivateFile2(String cadena, String id)  {
        cadena= cadena.substring(8,cadena.length()-8);
        String resultado="";
        String[] cadenas = cadena.split("\\\\n");

        for(String cadena1: cadenas){
            System.out.println(cadena1);
            resultado=resultado+cadena1;

        }






      /*  String resultado ="";
       // cadena = cadena.replaceAll("\\\\n", "").replaceAll("\\\\r", "");
        cadena= cadena.substring(8,cadena.length()-8);
      //  cadena.replaceAll("\\ \\n","\r\n");
        int j=0;
        int k=8;
        int l=42;
        int tmp2=0;
        System.out.println(cadena.substring(8,42));//35
        System.out.println(cadena.substring(50,84));//35
        System.out.println(cadena.substring(92 ,126));

/*       System.out.println(cadena.substring(317, 176));
        System.out.println(cadena.substring(356, 176));
        System.out.println(cadena.substring(399, 176));
        System.out.println(cadena.substring(442, 176));
        System.out.println(cadena.substring(485, 176));
        System.out.println(cadena.substring(528, 176));*/
       /* System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));
        System.out.println(cadena.substring(j+k, j+35+k));*/



   /*  while(cadena.length()>l){
             //tmp2=(k+(l*j)+j)+42;
            if(cadena.length()>=l){
                System.out.println(cadena.substring(k,l));
               // resultado=resultado+cadena.substring(j+k, j+35+k);
                k=l+8;
                l=k+34;
            }
            else{
                System.out.println(cadena.substring(k, cadena.length()));
               // resultado=resultado+cadena.substring(j+k, cadena.length());
            }

         //  j++;
        }



       /* for (int i=0; i<cadena.length(); i++){
            System.out.println(cadena.substring((i*43)+6,((i+1)*43)-2));
        }*/


        /*int horizontal = 30;
        cadena = cadena.substring(0,cadena.length()-176);
        String resultado = "";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        System.out.println("********** 2");
        File folder = new File(extStorageDirectory, "Download");
        System.out.println("**********");
        folder.mkdir();
        File file = new File(folder, "T"+ tipo_documento.substring(0,1).toUpperCase() + id + ".txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter osw;
            try {
                for(int i=0; i<cadena.length(); i+=44){
                    if(cadena.length()-i>43){

                        String cadena_tmp = cadena.substring(i + 8, i + 43);
                        // resultado= resultado + "TEXT 4 0 30 40 " + cadena_tmp;
                        String temporal = cadena_tmp + "\r\n";
                        horizontal = horizontal + 30;
                        resultado = resultado + temporal;
                           /*"! 0 20 20 21 1\r\n"
                                   + "TEXT 0 3 20 20 " + cadena_tmp
                                   + "FORM\r\n"
                                   + "PRINT\r\n";*/

                     //   System.out.println(cadena.substring(i+8, i+43));



                      /*  if(i==0){
                            stream.write(cadena_tmp.substring(10).getBytes());
                            //stream.flush();

                        }
                        else {


                            stream.write(cadena_tmp.getBytes());
                            // stream.flush();
                        }
                        // stream.write(System.getProperty("line.separator").getBytes());
                        //   osw = new OutputStreamWriter(stream);
                        //  osw.append("\n");
                        //   osw.flush();
                        //  osw.close();
                    }
                    else{
                        //System.out.println("+" + cadena.substring(i+1,cadena.length()-1));
                    }

                }


            } finally {
                stream.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
       /* int horizontal = 30;
        cadena = cadena.substring(0,cadena.length()-176);
        String resultado = "";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        System.out.println("********** 2");
        File folder = new File(extStorageDirectory, "Download");
        System.out.println("**********");
        folder.mkdir();
        File file = new File(folder, "T"+ tipo_documento.substring(0,1).toUpperCase() + id + ".txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter osw;
            try {
                for(int i=0; i<cadena.length(); i+=44){
                    if(cadena.length()-i>43){

                        String cadena_tmp = cadena.substring(i + 1, i + 43);
                        // resultado= resultado + "TEXT 4 0 30 40 " + cadena_tmp;
                        String temporal = cadena_tmp + "\r\n";
                        horizontal = horizontal + 30;
                        resultado = resultado + temporal;
                           /*"! 0 20 20 21 1\r\n"
                                   + "TEXT 0 3 20 20 " + cadena_tmp
                                   + "FORM\r\n"
                                   + "PRINT\r\n";*/

                      /*  System.out.println(cadena.substring(i+1, i+43));



                        if(i==0){
                            stream.write(cadena_tmp.substring(10).getBytes());
                            //stream.flush();

                        }
                        else {


                            stream.write(cadena_tmp.getBytes());
                            // stream.flush();
                        }
                        // stream.write(System.getProperty("line.separator").getBytes());
                        //   osw = new OutputStreamWriter(stream);
                        //  osw.append("\n");
                        //   osw.flush();
                        //  osw.close();
                    }
                    else{
                        //System.out.println("+" + cadena.substring(i+1,cadena.length()-1));
                    }

                }


            } finally {
                stream.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return  resultado;
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


    private class HttpAsyncTask9 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST9(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            Intent intent = new Intent(DocumentoBluetooth.this, Blue.class);

            //result = result.replace(System.getProperty("line.separator"), "hola");
            System.out.println("Impresion: " + result);
            System.out.println(result);
            intent.putExtra("Ticket", ticketBytes);
            intent.putExtra("data", result);
            startActivity(intent);




        }
    }

    public String POST9(String url) {
        String result="";
        InputStream inputStream = null;
        try {



            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);




            System.out.println("Aqui esta el id " + id);

            httppost.setHeader("Content-Type", "application/json");
            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {

                    result  = Helper.convertInputStreamToString(inputStream);
                    String[] texto = result.split("<imagen>");

                    if (texto.length > 1) {
                        ticketBytes = new byte[texto[0].length()];
                        ticketBytes = (texto[0]).getBytes();
                        for (int i = 1; i < texto.length; i = i + 2) {
                            ticketBytes = Helper.concatBytes(ticketBytes, Base64.decode(texto[i], Base64.DEFAULT));
                            ticketBytes = Helper.concatBytes(ticketBytes, texto[i + 1].getBytes());
                        }
                    } else {
                        ticketBytes = new byte[result.length()];
                        ticketBytes = result.getBytes();
                    }

                }else
                    ticketBytes = "No informacion de ticket".getBytes();
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



    private class HttpAsyncTask10 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST10(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Documento Guardado como: "+tipo_documento.substring(0,1).toUpperCase() + id + ".pdf", Toast.LENGTH_LONG).show();
            System.out.println("El resultado es: " + result);
            Toast.makeText(getApplicationContext(), "Resultado " + result, Toast.LENGTH_LONG).show();

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
                    Toast.makeText(DocumentoBluetooth.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                }


            }
            else {
                if (pdfFile2.exists()) {
                    Uri path = Uri.fromFile(pdfFile2);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdf_flag = true;
                    try {
                        startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(DocumentoBluetooth.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                    }


                }
            }
        }

    }

    public String POST10(String url) {
        Map<String, Object> map2 = new HashMap();
        map2.put("id", id);
        map2.put("tipo", tipo_documento);
        map2.put("@class", Map.class.getName());


        JSONObject jsonOBJECT1 = new JSONObject(map2);

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
                    // result = Helper.convertInputStreamToString(inputStream);

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



    private class HttpAsyncTask11 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST11(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            //   Toast.makeText(getApplicationContext(), "Estado de envio: " + result, Toast.LENGTH_LONG).show();
        }
    }

    public String POST11(String url) {
        Map<String, Object> map2 = new HashMap();
        map2.put("id", id);
        map2.put("tipo", tipo_documento);
        map2.put("@class", Map.class.getName());


        JSONObject jsonOBJECT1 = new JSONObject(map2);

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


    private class HttpAsyncTask13 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST13(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getApplicationContext(), "Estado de envio: " + result, Toast.LENGTH_LONG).show();
            convertirDatosPermiso(result);
        }
    }

    public String POST13(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
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


    static void agregarCamposEntidad(String campo, Map entidad, String key) {

        if (campo.equals("")) {
        } else {
            entidad.put(key, campo);
        }
    }


    public String convertirDatosDocumento(String cadena) {
        String id = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap arrayData = mapper.readValue(cadena, HashMap.class);
            id = String.valueOf((Integer) arrayData.get("id"));

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return id;
    }

    public void convertirDatosPermiso(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Boolean> arrayData = mapper.readValue(cadena, HashMap.class);
            if(arrayData.containsKey("general.modDescuentoPartida")){
                editar_descuento_permiso = arrayData.get("general.modDescuentoPartida");
            }
            if(arrayData.containsKey("general.modPrecio")){
                editar_precio_permiso = arrayData.get("general.modPrecio");

            }


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public String createExternalStoragePrivateFile2(String cadena, String id, byte[] data, int inicio) {

        cadena = cadena.substring(inicio, cadena.length() - 176);


        cadena = cadena.replaceAll("\\\\n","\r\n");
        cadena = cadena + "\r\n";
        System.out.println("Factura: " + cadena + " }");

        return cadena;


      /*  int horizontal = 30;
        cadena = cadena.substring(0,cadena.length()-176);
        String resultado = "";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        System.out.println("********** 2");
        File folder = new File(extStorageDirectory, "Download");
        System.out.println("**********");
        folder.mkdir();
        File file = new File(folder, "TR" + id + ".txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter osw;
            try {
                for(int i=0; i<cadena.length(); i+=44){
                    if(cadena.length()-i>43){

                        String cadena_tmp = cadena.substring(i + 1, i + 43);
                        // resultado= resultado + "TEXT 4 0 30 40 " + cadena_tmp;
                        String temporal = cadena_tmp + "\r\n";
                        horizontal = horizontal + 30;
                        resultado = resultado + temporal;
                           /*"! 0 20 20 21 1\r\n"
                                   + "TEXT 0 3 20 20 " + cadena_tmp
                                   + "FORM\r\n"
                                   + "PRINT\r\n";*/

                       /* System.out.println(cadena.substring(i+1, i+43));



                        if(i==0){
                            stream.write(cadena_tmp.substring(10).getBytes());
                            //stream.flush();

                        }
                        else {


                            stream.write(cadena_tmp.getBytes());
                            // stream.flush();
                        }
                        // stream.write(System.getProperty("line.separator").getBytes());
                        //   osw = new OutputStreamWriter(stream);
                        //  osw.append("\n");
                        //   osw.flush();
                        //  osw.close();
                    }
                    else{
                        //System.out.println("+" + cadena.substring(i+1,cadena.length()-1));
                    }

                }


            } finally {
                stream.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultado;*/
    }



    public String convertirDatosDocumentoDireccion(String cadena) {
        String id = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HashMap> arrayData = mapper.readValue(cadena, List.class);
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return id;
    }


    public void alertRemisionCreada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);

        LayoutInflater inflater = DocumentoBluetooth.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.remision_creada, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);


        final Button email_btn = (Button) dialogView.findViewById(R.id.rem_correo);
        Button remision_btn = (Button) dialogView.findViewById(R.id.rem_pdf);
        final Button ticket_btn = (Button) dialogView.findViewById(R.id.rem_blue);
        Button salir_btn = (Button) dialogView.findViewById(R.id.rem_salir);
        Button bluetooth_btn = (Button) dialogView.findViewById(R.id.rem_txt);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();


        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask11 a = new HttpAsyncTask11();
                a.execute(server + "/medialuna/spring/documento/reporte/mail");
            }
        });


        remision_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask10 a = new HttpAsyncTask10();
                a.execute(server + "/medialuna/spring/documento/reporte/pdf/");
            }
        });


        ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendViaBluuetooth();
            }
        });


        salir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = getIntent();
                intent.putExtra("tipo_documento", tipo_documento);
                startActivity(intent);
                finish();
            }
        });

        bluetooth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentoBluetooth.this, tipo_documento, Toast.LENGTH_LONG).show();
                HttpAsyncTask9 a = new HttpAsyncTask9();
               // if(tipo_documento.equals("remisioncliente")) {
                    a.execute(server + "/medialuna/spring/tickets/ticketAppDefault/"+id+"/"+tipo_documento);
               // }
               /* else{
                    a.execute(server + "/medialuna/spring/tickets/ticketFacturaDefault");
                }*/
                ticket_btn.setEnabled(true);
            }
        });


        dialog.show();


    }



    public void sendViaBluuetooth(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null){
            Toast.makeText(this, "Bluettoth is not supported on this device", Toast.LENGTH_LONG).show();
        }
        else{
            enableBluetooth();
        }
    }

    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoveryIntent, 1);
    }




    public void borrarFile(File f){

        f.delete();
    }


    public boolean comprobarDatos() {
        boolean flag = true;
        if (laSerie.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir la Serie", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if (elCliente.equals("") || laDireccion.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir el Cliente", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if (elAgente.equals("")) {
            Toast.makeText(getApplicationContext(), "Favor de elegir el Agente", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return  flag;

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

    public void ocultarTeclado(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_escaner.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        //inputMethodManager.hideSoftInputFromWindow(editTextBuscar.getWindowToken(), 0);
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

    private class HttpAsyncTask14 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST14(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("result " + result);
            if(!result.equals("null"))
                convertirDatosListaPrecios(result);
        }
    }

    public String POST14(String url) {
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

    private void popUpListaPrecio() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentoBluetooth.this);
        builder.setTitle("Lista de Precios");
        builder.setSingleChoiceItems(nombre_ListaPrecios.toArray(new String[nombre_ListaPrecios.size()]), 0, new DialogInterface.OnClickListener() {

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

                        laListaPrecio = id_ListaPrecios.get(selected).toString();

                        break;
                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + laListaPrecio + " " + nombre_ListaPrecios.get(selected), Toast.LENGTH_LONG).show();

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


    public void convertirDatosListaPrecios(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            id_ListaPrecios.clear();
            nombre_ListaPrecios.clear();


            for (int i = 0; i < arrayData.size(); i++) {
                System.out.println(arrayData.get(i));
                Map mapa1 = (Map) arrayData.get(i);
                System.out.println((Integer) mapa1.get("id") + (String) mapa1.get("nombre"));
                id_ListaPrecios.add((Integer) mapa1.get("id"));
                nombre_ListaPrecios.add((String) mapa1.get("nombre"));



            }
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (id_ListaPrecios.size() > 0) {
            btn_lista_precios.setVisibility(View.VISIBLE);

        }

    }
}