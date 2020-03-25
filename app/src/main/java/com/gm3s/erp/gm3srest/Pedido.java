package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.gm3s.erp.gm3srest.Model.Serie;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.Blue;
import com.gm3s.erp.gm3srest.View.CamposAdicionalesOS;
import com.gm3s.erp.gm3srest.View.LogIn;
import com.gm3s.erp.gm3srest.View.MainActivity;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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

public class Pedido extends AppCompatActivity {
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
    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Articulo> lista_articulos = new ArrayList<Articulo>();
    private Timer timer = new Timer();
    LinkedList<HashMap> productos_list = new LinkedList();
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

    Map<Integer,Articulo> mapa_articulos = new HashMap<>();

    TextView total;
    TextView escaner_folio;
    TextView escaner_total, escaner_fecha;
    private static boolean validacion;
    TextView escaner_txt_serie, escaner_txt_agente;
    static TextView escaner_txt_cliente;
    static private String laSerie = "";
    static private String elCliente = "";
    static private String laBodega = "";
    static private String laListaPrecio = "";
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
    static String categoria_busqueda= "nombreCorto";
    boolean formas_estado = false;
    static BigDecimal temporal_resta = new BigDecimal(0.0);
    List<Boolean> formas_check =  new ArrayList<Boolean>();
    List<String>  formas_et = new ArrayList<String>();
    static BigDecimal total_temp = new BigDecimal(0.0);
    LinkedList<HashMap> formasPago_tmp = new LinkedList();
    List<HashMap> formasPago = new ArrayList<HashMap>();
    static boolean editar_precio_permiso  = false;
    static boolean editar_descuento_permiso  = false;
    Integer laMoneda = 0;

    Map<String,String> info = new HashMap<>();

    Button btn_lista_precios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
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
        info.put("subtotal","" );
        info.put("impuestos","" );
        info.put("total","" );
        info.put("no_art","" );

        Intent intent = getIntent();
        String PedidoMapa = (String)intent.getSerializableExtra("PedidoMapa");
        String InfoMapa = (String)intent.getSerializableExtra("InfoMapa");
        String PedidoArt = (String)intent.getSerializableExtra("PedidoArt");

        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");


        codigos = (TableLayout) findViewById(R.id.tabla_codigos);
        codigos.setStretchAllColumns(true);
        codigos.bringToFront();

        subtotal = (TextView) findViewById(R.id.et_subtotal);
        subtotal.setText("0.0");
        info.put("subtotal", "0.0");
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

        escaner_btn_fpago = (Button) findViewById(R.id.escaner_btn_fpago);
        btn_lista_precios = (Button) findViewById(R.id.btn_lista_precios);
        btn_lista_precios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpListaPrecio();
            }
        });

        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Documento - Lista de Articulos");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pedido.this,MainActivity.class);
                startActivity(intent);
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        escaner_fecha = (TextView) findViewById(R.id.escaner_fecha);
        escaner_fecha.setText(escaner_fecha.getText() + " " + currentDateandTime);
        info.put("fecha", escaner_fecha.getText() + " " + currentDateandTime);


        escaner_folio = (TextView) findViewById(R.id.escaner_folio);
        escaner_folio.setText("");

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/listar/serie/contar/"+tipo_documento+"/");

        HttpAsyncTask12 b = new HttpAsyncTask12();
        b.execute(server + "/medialuna/spring/listar/catalogo/1403/");

        HttpAsyncTask13 c = new HttpAsyncTask13();
        c.execute(server + "/medialuna/spring/editorUsuario/permisos/usuario/app");


        HttpAsyncTask14 d = new HttpAsyncTask14();
        d.execute(server + "/medialuna/spring/app/buscarListaPrecios");

        mButton = (LinearLayout) findViewById(R.id.escaner_btn_escanear);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comprobarDatos() == true) {


                    info.put("impuestos", impuestos.getText().toString());
                    info.put("total", total.getText().toString());
                    info.put("no_art", escaner_txt_no_art.getText().toString());
                    info.put("counter", String.valueOf(counter));


                    Intent intent = new Intent(Pedido.this, ContenedorFragmentos.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    System.out.println("Aqui esta el pedido : " + productos_list);
                    System.out.println("lista_art: " + lista_articulos);

                    intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_articulos));
                    intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
                    intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                    intent.putExtra("tipo_documento", tipo_documento);
                    intent.putExtra("comensales", "0");
                    startActivity(intent);
                    finish();




                } else {
                    Toast.makeText(Pedido.this, "Seleccione una Serie", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(Pedido.this, "Presiono limpiar", Toast.LENGTH_SHORT).show();

                counter = 0;
                cantidad_articulos = 0;
                subtotal.setText("0.0");
                info.put("subtotal", "0.0");
                impuestos.setText("0.0");
                total.setText("0.0");
                escaner_total.setText("0.0");
                escaner_txt_no_art.setText("0");
                while (codigos.getChildCount() != 1) {
                    codigos.removeViewAt(codigos.getChildCount() - 1);
                }
                productos_list.clear();
                lista_articulos.clear();
                lista_art.clear();
                escaner_btn_limp.setEnabled(false);


            }
        });


        System.out.println("Enviar Recibe lista_art" + PedidoArt);
        System.out.println("Enviar Recibe productos_list " + PedidoMapa);
        System.out.println("Enviar Recibe info " + InfoMapa);


        convertirMapa(PedidoMapa);
        convertirMapa2(InfoMapa);
        convertirMapa3(PedidoArt);


        escaner_btn_articulo = (LinearLayout) findViewById(R.id.escaner_btn_articulo);
        escaner_btn_articulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarDatos() == true) {
                    alertArticulo();
                } else {


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
                Toast.makeText(Pedido.this, tipo_documento, Toast.LENGTH_LONG).show();
                if (laSerie.equals("")) {
                    Toast.makeText(Pedido.this, "Favor de elegir Serie ", Toast.LENGTH_LONG).show();

                } else if (elCliente.equals("") || elAgente.equals("")) {
                    Toast.makeText(Pedido.this, "Favor de elegir Cliente/Agente ", Toast.LENGTH_LONG).show();

                } else {

                    if (cantidad_articulos > 0) {
                        HttpAsyncTask8 a = new HttpAsyncTask8();
                        a.execute(server + "/medialuna/spring/documento/crear/app");
                    } else {
                        Toast.makeText(Pedido.this, "Documento vacio", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


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
            Intent localIntent = new Intent(Pedido.this.getApplicationContext(), LogIn.class);
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

            final IntentIntegrator integrator = new IntentIntegrator(Pedido.this);
            integrator.setCaptureLayout(R.layout.custom_capture_layout);
            integrator.initiateScan();

            if(categoria_busqueda.equals("nombreCorto")){
                if (result.equals("[]") || result.equals("")) {
                    categoria_busqueda = "codigoSKU";
                    Toast.makeText(getApplicationContext(), "Codigo NO encontrado en NombreCorto", Toast.LENGTH_SHORT).show();
                    HttpAsyncTask3 a = new HttpAsyncTask3();
                    a.execute(server + "/medialuna/spring/listar/entidad/filtro/documentoArticulosCaracteristicas/");
                } else {
                    categoria_busqueda = "nombreCorto";
                    Toast.makeText(getApplicationContext(), "Codigo encontrado", Toast.LENGTH_SHORT).show();
                    convertirDatosArticulo(result);
                }


            }


            else{
                if (result.equals("[]") || result.equals("")) {
                    categoria_busqueda = "nombreCorto";
                    Toast.makeText(getApplicationContext(), "Codigo NO encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    categoria_busqueda = "nombreCorto";
                    Toast.makeText(getApplicationContext(), "Codigo encontrado", Toast.LENGTH_SHORT).show();
                    convertirDatosArticulo(result);
                }


            }








        }
    }


    public static String POST3(String url) {

        if(laListaPrecio=="")
            laListaPrecio=elCliente;

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName()); //Solo busca con enteros
        entidad.put(categoria_busqueda, barcode); //nombreCorto
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
        adicionales.put("lprecio", Integer.parseInt(laListaPrecio));//elCliente
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

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), String.valueOf((String)mapa1.get("nombreCorto")), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), counter, 1);
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
            c00.setText(art.getCantidad().toString());
            c00.setGravity(Gravity.CENTER);
            c00.setTextColor(Color.parseColor("#ffffff"));
            c00.setBackgroundColor(Color.parseColor("#373737"));
            cantidad_articulos = cantidad_articulos + 1;
            final String auxc = c00.getText().toString();


            TextView c5 = new TextView(this);
            c5.setTextColor(Color.parseColor("#ffffff"));
            c5.setText(lista_art.get(j).getDescripcion().toString());
            c5.setGravity(Gravity.CENTER);
            c5.setBackgroundColor(Color.parseColor("#373737"));

            final EditText c51 = new EditText(this);
            c51.setText("0.0");
            c51.setGravity(Gravity.RIGHT);
            c51.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if(editar_descuento_permiso==false){
                c51.setEnabled(false);
                c51.setFocusable(false);
            }

            final EditText c6 = new EditText(this);
            c6.setText(Helper.formatDouble(lista_art.get(j).getPrecioBase()).toString());
            c6.setGravity(Gravity.RIGHT);
            if(editar_precio_permiso==false){
                c6.setEnabled(false);
                c6.setFocusable(false);
            }

            final TextView c2_1 = (TextView) this.getLayoutInflater().inflate(R.layout.renglones, null);
            c2_1.setText(Helper.formatDouble(lista_art.get(j).getPrecioBase()).toString());
            c2_1.setGravity(Gravity.RIGHT);
            c2_1.setTextSize(25);
            c2_1.setTextColor(Color.parseColor("#3D6AB3"));
            c2_1.setTypeface(null, Typeface.BOLD);


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
                                        info.put("subtotal", Helper.formatDouble(total_double2).toString());
                                        setText(total, String.valueOf(Helper.formatDouble((Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                        setText(escaner_total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                        cantidad_articulos = cantidad_articulos + Integer.parseInt(c00.getText().toString()) - Integer.valueOf(cantidad_tmp.intValue());
                                        setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));


                                        for (int i = 0; i < productos_list.size(); i++) {
                                            if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                lista_articulos.get(art.getCounter()).setCantidad(Integer.parseInt(c00.getText().toString()));
                                                productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));

                                                System.out.println("\n-===============================================================0");
                                                System.out.println("\n*******************************************************************");
                                                lista_articulos.get(i).setCantidad(Integer.parseInt(c00.getText().toString()));
                                                productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));
                                                System.out.println("\n*******************************************************************");
                                                System.out.println("\n*******************************************************************");

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
                                        } else {

                                            Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                            Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString())) / 100)));
                                            setText(c2_1, Helper.formatDouble(aux31).toString());
                                            Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) - tem_total + aux31));
                                            setText(subtotal, String.valueOf(subtotal_double));
                                            info.put("subtotal", String.valueOf(subtotal_double));

                                            Double impuesto_double = Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto()) / 100)) + (aux31 * ((art.getImpuesto()) / 100)));
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
                                            info.put("subtotal", String.valueOf(subtotal_double));

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
            info.put("subtotal", String.valueOf(Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) + total_double)));

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
                if ((counter % 2) == 0) {
                    tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                } else {
                    tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                }
            }

            codigos.addView(tr, 1); //unica
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

                info.put("serie", nombreS);
                info.put("serie_id", laSerie);

                Object cliente1 = (Object) arrayData.get("cliente");
                Object bodega1 = (Object) arrayData.get("bodega");
                HttpAsyncTask6 c = new HttpAsyncTask6();
                c.execute(server + "/medialuna/spring/documento/obtener/folio/" + idS + "/" + tipo_documento+"/");

                HashMap<String, Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                HashMap<String, Object> bodega = (HashMap<String, Object>) arrayData.get("bodega");


                if (cliente1 == null) {

                } else {
                    //HashMap<String,Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                    String codigoC = cliente.get("id").toString();
                    String nombreC = cliente.get("nombre").toString();

                    HashMap<String, Object> agente = (HashMap<String, Object>) cliente.get("agente");


                    String codigoA = agente.get("id").toString();
                    String nombreA = agente.get("nombre").toString();

                    System.out.println(" Esto es la informacion del cliente: 1 " + arrayData0.keySet());
                    System.out.println(" Esto es la informacion del cliente: 2 " + arrayData.keySet());
                    System.out.println(" Esto es la informacion del cliente: 3 " + cliente.keySet());
                    System.out.println(" Esto es la informacion del cliente: 4 " + cliente.get("direcciones").toString());
                    System.out.println(" Esto es la informacion del cliente: 5 " + cliente.get("direccion").toString());
                    List<Object> direcciones = (List) cliente.get("direcciones");
                    System.out.println(" Esto es la informacion del cliente: 6 " + cliente.get("direccion").toString());
                    List<HashMap> direccionimpl = (List) direcciones.get(1);
                    System.out.println(" Esto es la informacion del cliente: 7 " + cliente.get("direccion").toString());
                    HashMap direccion_impl_0 = direccionimpl.get(0);
                    System.out.println(" Esto es la informacion del cliente: 8 " + cliente.get("direccion").toString());
                    laDireccion = (Integer) direccion_impl_0.get("id");
                    System.out.println(" Esto es la informacion del cliente: 9 " + cliente.get("direccion").toString());
                    System.out.println(" Esto es la informacion del cliente: 10 " + laDireccion);
                    elAgente = codigoA;
                    elCliente = codigoC;
                    escaner_txt_cliente.setText(nombreC);
                    info.put("cliente", nombreC);
                    info.put("cliente_id", codigoC);

                    escaner_txt_agente.setText(nombreA);
                    info.put("agente", nombreA);
                    info.put("agente_id", codigoA);

                    if (cliente.containsKey("moneda") && cliente.get("moneda") != null) {
                        Map mapa2 = (Map) cliente.get("moneda");
                        laMoneda = (Integer) mapa2.get("id");
                    }
                }

                if(bodega1 == null){}
                else{
                    String codigoB = bodega.get("id").toString();
                    laBodega = codigoB;
                    info.put("bodega",laBodega);
                }


                if (Integer.parseInt(arrayData0.get("total").toString()) >= 1 && laSerie.equals("")) {//==
                    laSerie = idS;
                    escaner_txt_serie.setText(nombreS);

                    info.put("serie", nombreS);
                    info.put("serie_id", laSerie);

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
                    Toast.makeText(Pedido.this, "Favor de ingresar mayor caracteres", Toast.LENGTH_SHORT).show();
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
                // mapa10.get(0);

                System.out.println("Direcciones "  + direccion_cliente.get(direccion_cliente.size()-1));


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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);
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

                        info.put("cliente", nombre_cliente.get(selected));
                        info.put("cliente_id", elCliente);

                        info.put("agente",nombre_agente.get(selected) );
                        info.put("agente_id", elAgente);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);

        LayoutInflater inflater = Pedido.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.referencias2, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);


        final TableLayout tabla_efectivo = (TableLayout) dialogView.findViewById(R.id.tabla_efectivo);
        final TableLayout tabla_credito = (TableLayout) dialogView.findViewById(R.id.tabla_credito);


        final TextView total_tv = (TextView) dialogView.findViewById(R.id.total_textView);
        // EditText efectivo_et = (EditText) dialogView.findViewById(R.id.referencias_etx_efectivo);

        // efectivo_et.setText(total.getText());
        total_tv.setText(total.getText());

        final ArrayList<CheckBox> check_array_formas2 = new ArrayList<>();
        final ArrayList<EditText> etx_array_formas2 = new ArrayList<>();

        for(int i=0; i<formasPago.size(); i++) {


            TableRow tr = new TableRow(this);

            final CheckBox c00 = new CheckBox(this);
            c00.setText(formasPago.get(i).get("nombre").toString());
            c00.setTag("referencias_cbx_" + formasPago.get(i).get("nombre").toString().toLowerCase());
            c00.setTextColor(Color.WHITE);
            check_array_formas2.add(c00);


            final EditText c51 = new EditText(this);
            c51.setTag("referencias_etx_" + formasPago.get(i).get("nombre").toString().toLowerCase());
            etx_array_formas2.add(c51);
            //c51.setBackground((Integer)R.drawable.edit_text_style3);

            if (Build.VERSION.SDK_INT >= 16) {
                c51.setBackground(getResources().getDrawable(R.drawable.edit_text_style3));
                }








            tr.addView(c00);
            tr.addView(c51);


            if(formasPago.get(i).get("nombre").toString().toLowerCase().equals("efectivo")) {
                c51.setText(total.getText());
                c00.setChecked(true);
            }

            if(formasPago.get(i).get("credito").toString().equals("true")){
                tabla_credito.addView(tr);
            }
            else
            {
                tabla_efectivo.addView(tr);
            }

        }















        /*for(int i=0; i<etx_array_formas.size(); i++){
            etx_array_formas.get(i).setText("");
            etx_array_formas.get(i).setEnabled(false);
            etx_array_formas.get(i).setFocusable(false);
            etx_array_formas.get(i).setClickable(false);

        }*/



        temporal_resta = BigDecimal.valueOf(0.0);

        for (int i=0; i<check_array_formas2.size(); i++){
            final int aux = i;
            check_array_formas2.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        temporal_resta = BigDecimal.valueOf(0.0);
                        for (int j = 0; j < etx_array_formas2.size(); j++) {
                            if ((etx_array_formas2.get(j).isEnabled() == false) || (etx_array_formas2.get(j).getText().toString().equals("")) || (check_array_formas2.get(j).isChecked()== false)) {
                            } else {
                                temporal_resta = temporal_resta.add(new BigDecimal(etx_array_formas2.get(j).getText().toString()));
                            }
                        }


                        Toast.makeText(getApplicationContext(), " Temporal Resta" + temporal_resta, Toast.LENGTH_SHORT).show();

                        etx_array_formas2.get(aux).setText(String.valueOf((new BigDecimal(total.getText().toString())).subtract(temporal_resta)));
                        etx_array_formas2.get(aux).setEnabled(true);
                        etx_array_formas2.get(aux).setFocusable(true);
                        etx_array_formas2.get(aux).setClickable(true);
                    } else {
                        etx_array_formas2.get(aux).setText("");
                        //    etx_array_formas.get(aux).setEnabled(false);
                        //  etx_array_formas.get(aux).setFocusable(false);
                        // etx_array_formas.get(aux).setClickable(false);

                    }

                }
            });
        }


        Button referencias_btn_cancelar = (Button) dialogView.findViewById(R.id.referencias_btn_cancelar);


        Button referencias_btn_aceptar = (Button) dialogView.findViewById(R.id.referencias_btn_aceptar);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();


        if(formas_estado){
            for(int i=0; i<check_array_formas2.size(); i++) {
                check_array_formas2.get(i).setChecked(formas_check.get(i));
                etx_array_formas2.get(i).setText(formas_et.get(i));
            }
        }


        referencias_btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                total_temp = BigDecimal.valueOf(0.0);

                /*if (referencias_cbx_efectivo.isChecked() || referencias_cbx_tarjeta.isChecked() || referencias_cbx_vales.isChecked()) {
                    escaner_etx_fpago.setText("EFECTIVO");
                }

                if (referencias_cbx_cheque.isChecked() || referencias_cbx_transferencia.isChecked() || referencias_cbx_credito.isChecked() || referencias_cbx_otro.isChecked()) {
                    escaner_etx_fpago.setText("CREDITO");
                    escaner_btn_cambio.setEnabled(false);
                }*/

                formasPago_tmp.clear();
                formas_check.clear();
                formas_et.clear();

                for(int i=0; i<check_array_formas2.size(); i++){
                    System.out.println("      ----> " + 1);
                    System.out.println("CheckBox " + "Elemento " + i + "   estado " + check_array_formas2.get(i).isChecked());
                    formas_check.add(check_array_formas2.get(i).isChecked());
                    formas_et.add(etx_array_formas2.get(i).getText().toString());
                    if(check_array_formas2.get(i).isChecked()){
                        System.out.println("      ----> " + 2);
                        for(int j=0; j<formasPago.size(); j++){
                            System.out.println("      ----> " + 3 + j);
                            // System.out.println("CheckBox " + formasPago.get(j).get("nombre").toString() + "        " + check_array_formas.get(i).getText().toString().toUpperCase());

                             System.out.println("-->" + formasPago.get(j).get("nombre").toString() + "        " + check_array_formas2.get(i).getText().toString().toUpperCase());
                            if(formasPago.get(j).get("nombre").toString().equals(check_array_formas2.get(i).getText().toString().toUpperCase())){
                                System.out.println("      ----> " + 4);
                                HashMap tmp = formasPago.get(j);
                                System.out.println("CheckBox " + "Edit TExt"  + etx_array_formas2.get(i).getText().toString());
                                tmp.put("valor", etx_array_formas2.get(i).getText().toString());
                                formasPago_tmp.add(tmp);

                                System.out.println("CheckBox " + "Valor: " + etx_array_formas2.get(i).getText().toString());
                                total_temp = total_temp.add(new BigDecimal(etx_array_formas2.get(i).getText().toString()));


                            }

                        }


                    }



                }


                if(total_temp.toString().equals(total.getText().toString())){
                    formas_estado = true;
                    dialog.cancel();

                }
                else {

                    System.out.println("      ----> " +  total_temp  + "   ----> " + total.getText().toString());
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
        // Display the custom alert dialog on interface
        dialog.show();
    }


    private void build_popup() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);
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


                        info.put("serie", nombre_serie.get(selected));
                        info.put("serie_id", laSerie);

                        break;
                    //   case 0:text = "Bad";break;

                    //  case 1:text = "Good";break;

                    // case 2:text = "Very Good";break;

                    //   case 3:text = "Average";break;

                }
                Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie + " " + nombre_serie.get(selected) + " " + selected, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Seleccionaste " + laSerie, Toast.LENGTH_LONG).show();
                HttpAsyncTask6 a = new HttpAsyncTask6();
                a.execute(server + "/medialuna/spring/documento/obtener/folio/" + laSerie + "/"+tipo_documento+"/");


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
                Serie serietmp = new Serie((Integer) mapa1.get("id"), (Integer) mapa1.get("códigoUsuario"), (String) mapa1.get("nombreCorto"), (String) mapa1.get("nombre"), (Integer) bodega.get("id"));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);

        LayoutInflater inflater = Pedido.this.getLayoutInflater();
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

                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Pedido.this);
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

                    Toast.makeText(Pedido.this, "Cantidad menor al total", Toast.LENGTH_SHORT);

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
            //setText(escaner_folio,result);
            // System.out.println("Resultado2: " + result);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);
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


                        info.put("agente", nombre_agente.get(selected));
                        info.put("agente_id", elAgente);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);

        LayoutInflater inflater = Pedido.this.getLayoutInflater();
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
                prices.removeAllViews();

                if (corto.getText().toString().equals("") && nombre.getText().toString().equals("") && sku.getText().toString().equals("")) {

                } else {
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
        adicionales.put("bodega",Integer.parseInt(laBodega));
        adicionales.put("lprecio", Integer.parseInt(laListaPrecio)); //elCliente
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


                TableRow tr = new TableRow(Pedido.this);
                tr.setBackgroundResource(R.drawable.border);

                // android:baselineAligned="false"
                //   tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final Articulo art = lista_art.get(i);

                final EditText c00 = new EditText(Pedido.this);
                c00.setInputType(InputType.TYPE_CLASS_NUMBER);
                c00.setText(art.getCantidad().toString());
                c00.setGravity(Gravity.CENTER);
                //  c00.setTextColor(Color.parseColor("#ffffff"));
                //  c00.setBackgroundColor(Color.parseColor("#373737"));
                cantidad_articulos = cantidad_articulos + 1;

                final String auxc = c00.getText().toString();


                TextView c5 = new TextView(this);
                //c5.setTextColor(Color.parseColor("#ffffff"));
                c5.setText(art.getDescripcion().toString());
                c5.setGravity(Gravity.CENTER);
                //  c5.setBackgroundColor(Color.parseColor("#373737"));

                final EditText c51 = new EditText(this);
                c51.setText("0.0");
                c51.setGravity(Gravity.RIGHT);
                c51.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if(editar_descuento_permiso==false){
                    c51.setEnabled(false);
                    c51.setFocusable(false);
                }

                final EditText c6 = new EditText(this);
                c6.setText(Helper.formatDouble(art.getPrecioBase()).toString());
                c6.setGravity(Gravity.RIGHT);
                c6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if(editar_precio_permiso==false){
                    c6.setEnabled(false);
                    c6.setFocusable(false);
                }

                final TextView c2_1 = new TextView(this);
                c2_1.setText(Helper.formatDouble(art.getPrecioBase()).toString());
                c2_1.setGravity(Gravity.RIGHT);
                c2_1.setTextSize(25);
                c2_1.setTextColor(Color.parseColor("#3D6AB3"));
                c2_1.setTypeface(null, Typeface.BOLD);
                // c2_1.setBackgroundColor(Color.parseColor("#373737"));
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
                                            info.put("subtotal", Helper.formatDouble(total_double2).toString());
                                            setText(total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));
                                            setText(escaner_total, String.valueOf((Helper.formatDouble(Double.parseDouble(total.getText().toString()) - (aux3 * ((art.getImpuesto() / 100) + 1)) + (total_double * ((art.getImpuesto() / 100) + 1))))));

                                            cantidad_articulos = cantidad_articulos + Integer.parseInt(c00.getText().toString()) - Integer.valueOf(cantidad_tmp.intValue());
                                            setText(escaner_txt_no_art, String.valueOf(cantidad_articulos));
                                            System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 1");

                                            for (int i = 0; i < productos_list.size(); i++) {
                                                System.out.println("\n!!!   "   + productos_list.get(i).get("counter")   + "     "  +  art.getCounter() + " !!!!");

                                                if (productos_list.get(i).get("counter").equals(art.getCounter())) {
                                                    System.out.println("\n*******************************************************************");
                                                    System.out.println("\n*******************************************************************");
                                                    lista_articulos.get(i).setCantidad(Integer.parseInt(c00.getText().toString()));
                                                    productos_list.get(i).put("cantidad", Integer.parseInt(c00.getText().toString()));
                                                    System.out.println("\n*******************************************************************");
                                                    System.out.println("\n*******************************************************************");

                                                }
                                            }
                                            System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 2");

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
                                            } else {

                                                Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                                Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString())) / 100)));
                                                setText(c2_1, Helper.formatDouble(aux31).toString());
                                                Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) - tem_total + aux31));
                                                setText(subtotal, String.valueOf(subtotal_double));
                                                info.put("subtotal", String.valueOf(subtotal_double));

                                                Double impuesto_double = Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto()) / 100)) + (aux31 * ((art.getImpuesto()) / 100)));
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
                                                info.put("subtotal", String.valueOf(subtotal_double));

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
                info.put("subtotal", String.valueOf(Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) + total_double)));

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
                    if ((counter % 2) == 0) {
                        tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


                    } else {
                        tr.setBackground(getResources().getDrawable(R.drawable.row_color));

                    }
                }
                codigos.addView(tr);
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

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("descripción"), Double.parseDouble(mapa1.get("existencia").toString()), (String) mapa1.get("nombre"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Double.parseDouble(mapa1.get("impuesto").toString()), counter,1);

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
            TextView c1 = new TextView(Pedido.this);
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            for (int j = 0; j < lista_art.size(); j++) {
                // counter = counter +1;

                TableRow tr = new TableRow(Pedido.this);
                CheckBox c0 = new CheckBox(Pedido.this);
                c0.setId(R.id.select_art + j);
                chbx_selected.add(c0);

                TextView c1 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c1.setText(lista_art.get(j).getId().toString());

                TextView c2 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c2.setText(lista_art.get(j).getDescripcion().toString());

                TextView c3 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones, null);
                c3.setText(lista_art.get(j).getExistencia().toString());

                TextView c4 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c4.setText(lista_art.get(j).getNombre().toString());

                TextView c5 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                System.out.println("Nombre Corto"  + lista_art.get(j).getNombreCorto().toString());

                c5.setText(String.valueOf(lista_art.get(j).getNombreCorto().toString()));

                TextView c6 = (TextView) Pedido.this.getLayoutInflater().inflate(R.layout.renglones, null);
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

            //  Intent intent = getIntent();
            //  finish();
            //  startActivity(intent);

            id = convertirDatosDocumento(result);
            System.out.println("Por aqui va el id: " + id);

            if(tipo_documento.equals("cotizacioncliente")){
               Intent intent = new Intent(Pedido.this, CamposAdicionalesOS.class);
                intent.putExtra("idDocumento", id+"");
                intent.putExtra("tipo_documento", tipo_documento);
                startActivity(intent);
                finish();
            }else{
            alertRemisionCreada();
            }

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



     /*   String result = " "" ";
        for (int i = 0; i < list.size(); i++) {
            result += " " + list.get(i);
        }*/



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
        //  totales.add(t1);
        // HashMap t2 = new HashMap();
        //t1.put("@class", HashMap.class.getName());
        t1.put("id1","0.0");
        //  totales.add(t2);
        // HashMap t3 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id2","0.0");
        // totales.add(t3);
        //  HashMap t4 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id3","0.0");
        // totales.add(t4);
        // HashMap t5 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id4",subtotal.getText().toString());
        //  totales.add(t5);
        // HashMap t6 = new HashMap();
        //t1.put("@class", HashMap.class.getName());
        t1.put("id5","0.0");
        //   totales.add(t6);
        // HashMap t7 = new HashMap();
        //t1.put("@class", HashMap.class.getName());
        t1.put("id6","0.0");
        //  totales.add(t7);
        // HashMap t8 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id7",subtotal.getText().toString());
        //   totales.add(t8);
        // HashMap t9 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id8",impuestos.getText().toString());
        // totales.add(t9);
        // HashMap t10 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id9","0.0");
        // totales.add(t10);
        //HashMap t11 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id10","0.0");
        //totales.add(t11);
        //  HashMap t12 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id11","0.0");
        //totales.add(t12);
        //HashMap t13 = new HashMap();
        // t1.put("@class", HashMap.class.getName());
        t1.put("id12", total.getText().toString());
        //totales.add(t13);

        //ArrayList totales_tmp = new ArrayList();
        //for (int i = 0; i < totales.size(); i++) {
        //    JSONObject jsonOBJECT2 = new JSONObject(totales.get(i));
        //    totales_tmp.add(jsonOBJECT2);
        // }

        if(formasPago_tmp.size()==1){
            formasPago_tmp.get(0).put("valor", total.getText().toString());
        }
        HashMap map2 = new HashMap();
        map2.put("id_direccion", laDireccion);
        map2.put("folio", escaner_folio.getText().toString());
        map2.put("tercero", elCliente);  //id Tercero
        map2.put("agente", Integer.parseInt(elAgente));
        map2.put("categoria", laSerie);
        map2.put("serie", laSerie);
        map2.put("entidad", buscarEntidad(tipo_documento));
        map2.put("moneda", laMoneda);
        map2.put("@class", HashMap.class.getName());

        HashMap map3 = new HashMap();
        map3.put("formasPago", formasPago_tmp);
        map3.put("@class", ArrayList.class.getName());

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

        System.out.println("Lo que se envia0: " + jsonOBJECT1);
        System.out.println("Lo que se envia1: " + jsonOBJECT1.toString());

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

            //StringEntity params2 = new StringEntity(jsonOBJECT2.toString());
            // params2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //  httppost.setEntity(params2);

            //StringEntity params3 = new StringEntity(jsonOBJECT3.toString());
            //params3.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            // httppost.setEntity(params3);

            System.out.println("Lo que se envia: " + jsonOBJECT1.toString());
            //  System.out.println("Lo que se envia: " + jsonOBJECT2.toString());
            //   System.out.println("Lo que se envia: " + jsonOBJECT3.toString());
            System.out.println("Lo que se envia: " + params.toString());

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
        productos_list = (LinkedList<HashMap>) contenido_map.get("productos_list");
        System.out.println("On Restore producots_list: " + productos_list);
        System.out.println("On Restore: escaner_etx_fpago: " + escaner_etx_fpago.getText().toString());

    }


    public void onCheckboxClicked2(HashMap<String, Object> contenido) {
        List<Articulo> lista_art_tmp = (List<Articulo>) contenido.get("lista_art");
        List<Articulo> lista_art2_tmp = (List<Articulo>) contenido.get("lista_art2");
        LinkedList<HashMap> productos_list_tmp = (LinkedList<HashMap>) contenido.get("productos_list");


        if (lista_art_tmp.isEmpty() && lista_art2_tmp.isEmpty() && productos_list_tmp.isEmpty()) {

        } else {

            for (int i = 0; i < lista_art2_tmp.size(); i++) {


                escaner_btn_limp.setEnabled(true);

                lista_art2.add(lista_art2_tmp.get(i));  //???????


                TableRow tr = new TableRow(Pedido.this);
                tr.setBackgroundResource(R.drawable.border);

                // android:baselineAligned="false"
                //   tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final Articulo art = lista_art2_tmp.get(i); //?????????

                final EditText c00 = new EditText(Pedido.this);
                c00.setInputType(InputType.TYPE_CLASS_NUMBER);
                c00.setText(productos_list_tmp.get(i).get("cantidad").toString());
                c00.setGravity(Gravity.CENTER);
                //c00.setTextColor(Color.parseColor("#ffffff"));
                //c00.setBackgroundColor(Color.parseColor("#373737"));
                cantidad_articulos = cantidad_articulos + Integer.parseInt(productos_list_tmp.get(i).get("cantidad").toString());

                final String auxc = c00.getText().toString();


                TextView c5 = new TextView(this);
                // c5.setTextColor(Color.parseColor("#ffffff"));
                c5.setText(art.getDescripcion());
                c5.setGravity(Gravity.CENTER);
                // c5.setBackgroundColor(Color.parseColor("#373737"));

                final EditText c51 = new EditText(this);
                c51.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                c51.setText(productos_list_tmp.get(i).get("precio").toString());
                c51.setGravity(Gravity.RIGHT);
                c51.setSelectAllOnFocus(true);
                if(editar_precio_permiso==false){
                    c51.setEnabled(false);
                    c51.setFocusable(false);
                }

                final EditText c6 = new EditText(this);
                c6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                c6.setText(productos_list_tmp.get(i).get("descuento").toString());
                c6.setGravity(Gravity.RIGHT);
                c6.setSelectAllOnFocus(true);
                if(editar_descuento_permiso==false){
                    c6.setEnabled(false);
                    c6.setFocusable(false);
                }

                final TextView c2_1 = new TextView(this);
                Double total_tmp = 0.0;
                Integer tmp_int = (Integer) productos_list_tmp.get(i).get("cantidad");
                total_tmp = Double.parseDouble(tmp_int.toString());
                total_tmp = ((Double)productos_list_tmp.get(i).get("precio")) * total_tmp;
                total_tmp = total_tmp * (1-((Double)productos_list_tmp.get(i).get("descuento"))/100);
                c2_1.setText(Helper.formatDouble(total_tmp).toString());
                c2_1.setTextSize(25);
                c2_1.setTextColor(Color.parseColor("#3D6AB3"));
                c2_1.setTypeface(null, Typeface.BOLD);
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
                                                    System.out.println("\n*------------------------------------------------------------------");
                                                    System.out.println("\n*******************************************************************");
                                                    lista_articulos.get(i).setCantidad(Integer.parseInt(c00.getText().toString()));

                                                    System.out.println("\n*******************************************************************");
                                                    System.out.println("\n*******************************************************************");
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


                codigos.addView(tr);


            }
        }


    }







    public String createExternalStoragePrivateFile2(String cadena, String id)  {
        cadena=cadena.substring(8,cadena.length()-8);
        String[] stringArray = cadena.split("\\\\");
        for(int i=0; i<stringArray.length;i++){
            stringArray[i] = stringArray[i].substring(1) +  "\r\n";
            System.out.println("[" + i + "]    " + stringArray[i]);


        }


        System.out.println(cadena.substring(0,45));
        System.out.println(cadena.substring(46,90));
        System.out.println(cadena.substring(91,135));
        System.out.println(cadena.substring(136,180));
        System.out.println(cadena.substring(181,225));
        System.out.println(cadena.substring(226,270));
        System.out.println(cadena.substring(271,315));
        System.out.println(cadena.substring(316,360));
        System.out.println(cadena.substring(361, 405));
        System.out.println(cadena.substring(451, 495));
        System.out.println(cadena.substring(496, 540));
        System.out.println(cadena.substring(541,585));
        System.out.println(cadena.substring(586,630));
        System.out.println(cadena.substring(631,675));
        System.out.println(cadena.substring(676,720));
        System.out.println(cadena.substring(721,765));
        System.out.println(cadena.substring(766,810));
        System.out.println(cadena.substring(811,855));
        System.out.println(cadena.substring(856,900));
        System.out.println(cadena.substring(901,945));
        System.out.println(cadena.substring(946,cadena.length()-1));

        int horizontal = 30;
        cadena = cadena.substring(0,cadena.length()-176);
        String resultado = "";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        //System.out.println("********** 2");
        File folder = new File(extStorageDirectory, "Download");
     //   System.out.println("**********");
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

                       // System.out.println(cadena.substring(i+1, i+43));



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


            Intent intent = new Intent(Pedido.this, Blue.class);

            //result = result.replace(System.getProperty("line.separator"), "hola");
           // System.out.println("Impresion");
          //  System.out.println(result);
            intent.putExtra("Ticket", result);
            startActivity(intent);


           /* Toast.makeText(getApplicationContext(), "Ticket guardado como TR" + id +".txt", Toast.LENGTH_LONG).show();



            File pdfFile = new File("/storage/sdcard0/Download/TR"+id+".txt");
            if(pdfFile.exists())
            {
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "text/plain");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try
                {
                    startActivity(pdfIntent);
                }
                catch(ActivityNotFoundException e)
                {
                    Toast.makeText(Escaner3.this, "No Application available to view txt", Toast.LENGTH_LONG).show();
                }
            }
*/

            //createExternalStoragePrivateFile(id, "pdf", );
          /*  Uri path = Uri.fromFile(file_tmp);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(Escaner.this, "No Application available to view",
                        Toast.LENGTH_SHORT).show();
            }*/


        }
    }

    public String POST9(String url) {
        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("idDocumento", id));
            postParameters.add(new BasicNameValuePair("tipoDocumento", tipo_documento));

            System.out.println("Aqui esta el id "  + id);

            httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));


            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                 //    String resultado =  createExternalStoragePrivateFile2(result, id);
                  //  result = resultado;
                    // id =convertirDatosDocumento(result);
                    // createExternalStoragePrivateFile("T" + id, "txt", inputStream);

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



    private class HttpAsyncTask10 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST10(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Documento Guardado como: " + tipo_documento.substring(0,1).toUpperCase() + id + ".pdf", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(Pedido.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(Pedido.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "Estado de envio: " + result, Toast.LENGTH_LONG).show();
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
        return id;
    }






    public String convertirDatosDocumentoDireccion(String cadena) {
        String id = "";
        ObjectMapper mapper = new ObjectMapper();
        try {

            System.out.println("arrayData1" + cadena);
            List<HashMap> arrayData = mapper.readValue(cadena, List.class);
            System.out.println("arrayData" + arrayData);
            //         id = String.valueOf((Integer) arrayData.get("id"));

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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);

        LayoutInflater inflater = Pedido.this.getLayoutInflater();
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
                intent.putExtra("PedidoMapa", "");
                intent.putExtra("InfoMapa", "");
                intent.putExtra("PedidoArt", "");
                intent.putExtra("tipo_documento", tipo_documento);
                startActivity(intent);
                finish();
            }
        });

        bluetooth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask9 a = new HttpAsyncTask9();
               // if(tipo_documento.equals("remisioncliente")) {
                a.execute(server + "/medialuna/spring/tickets/ticketAppDefault/"+id+"/"+tipo_documento);
               // }
               // else{
               //     a.execute(server + "/medialuna/spring/tickets/ticketFacturaDefault");
               // }
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

        System.out.println("**** Datos " + laSerie);
        System.out.println("**** Datos " + elCliente);
        System.out.println("**** Datos " + laDireccion);
        System.out.println("**** Datos " + elAgente);

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
        System.out.println("-----> " + "Formas Pago: " + cadena);
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











    public void onCheckboxClicked20(List<Articulo> mapa) {
        System.out.println("El ar entro a CheckBox");

        escaner_btn_limp.setEnabled(true);


       for(int i=0; i<mapa.size(); i++) {


            counter = counter + 1;
//                mapa.get(i).setCounter(counter);


            lista_art2.add(mapa.get(i));
          //  lista_articulos.add(mapa.get(i));


            TableRow tr = new TableRow(Pedido.this);
            tr.setBackgroundResource(R.drawable.border);

            // android:baselineAligned="false"
            //   tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));


            System.out.println("El articulo esta aqui:  " +mapa.get(i));

            final Articulo art = mapa.get(i);

            final EditText c00 = new EditText(Pedido.this);
            c00.setInputType(InputType.TYPE_CLASS_NUMBER);
            c00.setText(art.getCantidad().toString());
            c00.setGravity(Gravity.CENTER);
            //  c00.setTextColor(Color.parseColor("#ffffff"));
            //  c00.setBackgroundColor(Color.parseColor("#373737"));
            cantidad_articulos = cantidad_articulos + 1;

            final String auxc = c00.getText().toString();


            TextView c5 = new TextView(this);
            //c5.setTextColor(Color.parseColor("#ffffff"));
            c5.setText(art.getDescripcion().toString());
            c5.setGravity(Gravity.CENTER);
            //  c5.setBackgroundColor(Color.parseColor("#373737"));

            final EditText c51 = new EditText(this);
            c51.setText("0.0");
            c51.setGravity(Gravity.RIGHT);
            c51.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (editar_descuento_permiso == false) {
                c51.setEnabled(false);
                c51.setFocusable(false);
            }

            final EditText c6 = new EditText(this);
            c6.setText(Helper.formatDouble(art.getPrecioBase()).toString());
            c6.setGravity(Gravity.RIGHT);
            c6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (editar_precio_permiso == false) {
                c6.setEnabled(false);
                c6.setFocusable(false);
            }

            final TextView c2_1 = new TextView(this);
            c2_1.setText(Helper.formatDouble(art.getPrecioBase()*art.getCantidad()).toString());
            c2_1.setGravity(Gravity.RIGHT);
            c2_1.setTextSize(25);
            c2_1.setTextColor(Color.parseColor("#3D6AB3"));
            c2_1.setTypeface(null, Typeface.BOLD);
            // c2_1.setBackgroundColor(Color.parseColor("#373737"));
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
                                                System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                                System.out.println("\n*******************************************************************");
                                                lista_articulos.get(i).setCantidad(Integer.parseInt(c00.getText().toString()));
                                                System.out.println("\n*******************************************************************");
                                                System.out.println("\n*******************************************************************");
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
                                        } else {

                                            Double tem_total = Double.parseDouble(c2_1.getText().toString());
                                            Double aux31 = Double.parseDouble(c00.getText().toString()) * ((Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString())) / 100)));
                                            setText(c2_1, Helper.formatDouble(aux31).toString());
                                            Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) - tem_total + aux31));
                                            setText(subtotal, String.valueOf(subtotal_double));

                                            Double impuesto_double = Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto()) / 100)) + (aux31 * ((art.getImpuesto()) / 100)));
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
                                            Double aux3 = Double.parseDouble(c00.getText().toString()) * Double.parseDouble(c6.getText().toString()) * ((100 - Double.parseDouble(c51.getText().toString())) / 100);
                                            setText(c2_1, Helper.formatDouble(aux3).toString());

                                            Double subtotal_double = (Helper.formatDouble(Double.parseDouble(subtotal.getText().toString()) - tem_total + aux3));
                                            setText(subtotal, String.valueOf(subtotal_double));

                                            Double impuesto_double = Helper.formatDouble(Double.parseDouble(impuestos.getText().toString()) - (tem_total * ((art.getImpuesto()) / 100)) + (aux3 * ((art.getImpuesto()) / 100)));
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
            tmp.put("cantidad", art.getCantidad());
            tmp.put("counter", counter);
            tmp.put("descuento", 0.0);
            tmp.put("precio", art.getPrecioBase());



            System.out.println("Aqui esta la product " + productos_list);


           if (Build.VERSION.SDK_INT >= 16) {
               if ((counter % 2) == 0) {
                   tr.setBackground(getResources().getDrawable(R.drawable.alt_row_color));


               } else {
                   tr.setBackground(getResources().getDrawable(R.drawable.row_color));

               }
           }


            codigos.addView(tr);


        }



        //escaner_txt_no_art.setText(String.valueOf(cantidad_articulos));
    }


    public void convertirMapa2(String cadena) {


        info.put("f_pago", "");
        info.put("f_pago_id", "");


        info.put("folio", "");
        info.put("counter", String.valueOf(counter));


        System.out.println(" La cadena de convertirMapa 2 :" + cadena + ":");


        if (cadena.equals("")) {
        }
        else{
            ObjectMapper mapper = new ObjectMapper();
            try {

                info = mapper.readValue(cadena, HashMap.class);
                counter = Integer.parseInt(info.get("counter").toString());

                System.out.println(" La cadena de convertirMapa 3 :" + info + ":");

            } catch (JsonParseException e1) {
                e1.printStackTrace();
            } catch (JsonMappingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void convertirMapa3(String cadena){

        if (cadena.equals("[]") || cadena.equals("")) {

        }
        else {
            System.out.println("Esta es la cadena: " + cadena);


           // lista_art.clear();
            lista_articulos.clear();
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
                //lista_art.add(nuevo);
                lista_articulos.add(nuevo);
            }
            System.out.println( " $$$$$$$$$444" + lista_articulos.size());

            onCheckboxClicked20(lista_articulos);
           //onCheckboxClicked20(lista_art);
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
        if(nombre_entidad.equals("cotizacioncliente")){
            return 5;
        }
        else {
        return  10;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Pedido.this);
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
