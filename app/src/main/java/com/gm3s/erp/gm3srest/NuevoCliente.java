package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
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
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NuevoCliente extends AppCompatActivity {
    String server = "";
    Button btn_nuevo_cliente;
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    static int orden;
    static int codigoUsuario;
    static private EditText nom_nu_cli, apa_nu_cli, ama_nu_cli, rfc_nu_cli, nco_nu_cli, des_nu_cli, ema_nu_cli, dcre_nu_cli, curp_nu_cli, pweb_nu_cli, colonia_cli, calle_cli, noe_cli;
    static private EditText pais_cli, noi_cli, del_cli, ciu_cli, est_cli, cp_cli;
    static private Spinner estatus_sp,pac_sp, clasifi_sp, tipo_persona_sp;
    static private Button agente_btn, cp_button;
    static private TextView agente_tv;
    List<String> nombres_agente = new ArrayList<String>();
    List<String> nombres_cliente = new ArrayList<String>();
    List<Integer> ids_agente = new ArrayList<Integer>();
    int selected = 0;
    int temp = 0;
    int selected2 = 0;
    int temp2 = 0;
    static int id_agente = 0;
    int comensales, idMesa;
    LinkedList<HashMap> productos_list = new LinkedList();
    Map<String,String> info = new HashMap<>();
    int counter = 0;
    List<Articulo> lista_art = new ArrayList<Articulo>();
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        HttpAsyncTask2 a = new HttpAsyncTask2();
        a.execute(server + "/medialuna/spring/util/genericos/orden/");

        HttpAsyncTask3 b = new HttpAsyncTask3();
        b.execute(server + "/medialuna/spring/util/genericos/codigoUsuario/");

        Intent intent = getIntent();

        if(intent.getSerializableExtra("comensales")!=null) {
            comensales = Integer.parseInt(intent.getSerializableExtra("comensales").toString());
            flag=true;
        }
        if(intent.getSerializableExtra("idMesa")!=null) {
        idMesa = Integer.parseInt(intent.getSerializableExtra("idMesa").toString());}
        String PedidoMapa = (String) getIntent().getSerializableExtra("PedidoMapa");
        String InfoMapa = (String) getIntent().getSerializableExtra("InfoMapa");
        String PedidoArt = (String) getIntent().getSerializableExtra("PedidoArt");

        convertirMapa(PedidoMapa);
        convertirMapa2(InfoMapa);
        convertirMapa3(PedidoArt);

        nom_nu_cli = (EditText) findViewById(R.id.nom_nu_cli);
        apa_nu_cli = (EditText) findViewById(R.id.apa_nu_cli);
        ama_nu_cli = (EditText) findViewById(R.id.ama_nu_cli);
        rfc_nu_cli = (EditText) findViewById(R.id.rfc_nu_cli);
        nco_nu_cli = (EditText) findViewById(R.id.nco_nu_cli);

        ema_nu_cli = (EditText) findViewById(R.id.ema_nu_cli);

        curp_nu_cli = (EditText) findViewById(R.id.curp_nu_cli);



        colonia_cli = (EditText) findViewById(R.id.colonia_cli);
        calle_cli = (EditText) findViewById(R.id.calle_cli);
        noe_cli = (EditText) findViewById(R.id.noe_cli);
        noi_cli = (EditText) findViewById(R.id.noi_cli);
        pais_cli = (EditText) findViewById(R.id.pais_cli);
        del_cli = (EditText) findViewById(R.id.del_cli);
        ciu_cli = (EditText) findViewById(R.id.ciu_cli);
        est_cli = (EditText) findViewById(R.id.est_cli);
        cp_cli = (EditText) findViewById(R.id.cp_cli);



        agente_tv = (TextView) findViewById(R.id.agente_tv);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Nuevo Cliente");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoCliente.this, MenuCliente.class);
                startActivity(intent);
            }
        });

        btn_nuevo_cliente = (Button) findViewById(R.id.btn_nuevo_cliente);
        btn_nuevo_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id_agente == 0 || nom_nu_cli.getText().toString().equals("") || nco_nu_cli.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Faltan campos por llenar", Toast.LENGTH_SHORT).show();
                } else {
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/util/genericos/alta/app");

                }


            }
        });


        agente_btn = (Button) findViewById(R.id.agente_btn);
        agente_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/listar/agente/VENDEDOR/");
            }
        });



        cp_button = (Button) findViewById(R.id.cp_button);
        cp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask5 a = new HttpAsyncTask5();
                a.execute(server + "/medialuna/spring/codigoPostal/buscar/"+cp_cli.getText().toString());
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

            if(result.contains("Error")){
                Toast.makeText(getApplicationContext(),"Usuario NO Creado", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Usuario Creado", Toast.LENGTH_SHORT).show();
                convertirDatos2(result);
            }


        }
    }


    public static String POST(String url) {





        Map mapa_cliente= new HashMap();
        // mapa_cliente.put("@class", "mx.mgsoftware.erp.entidades.ClienteImpl");
          mapa_cliente.put("agente", id_agente);
        //  mapa_cliente.put("aparta", false);
        //   mapa_cliente.put("bodegaApartado", null);
        //  mapa_cliente.put("codigoEANCliente", null);
        //  mapa_cliente.put("codigoEANProveedor", null);
       mapa_cliente.put("comentario", "");
        // mapa_cliente.put("consignacion", false);
         mapa_cliente.put("curp", curp_nu_cli.getText().toString());
       mapa_cliente.put("codigoUsuario",codigoUsuario+1);
        mapa_cliente.put("descripcion",nom_nu_cli.getText().toString()+ " "+ apa_nu_cli.getText().toString());
        mapa_cliente.put("diasCredito", "1");
        mapa_cliente.put("estatus", "ACTIVO");
        mapa_cliente.put("limiteCredito", "1");
        mapa_cliente.put("mail", ema_nu_cli.getText().toString());
        mapa_cliente.put("materno", ama_nu_cli.getText().toString());
        //    mapa_cliente.put("moneda", 259014);
        mapa_cliente.put("nombre", nom_nu_cli.getText().toString());
        mapa_cliente.put("nombreCorto", nco_nu_cli.getText().toString());
       // mapa_cliente.put("numeroProveedor", null);
        mapa_cliente.put("orden", orden + 1);
        mapa_cliente.put("pacFacturacionEnum", "DEFAULT");
       mapa_cliente.put("paterno", apa_nu_cli.getText().toString());
        mapa_cliente.put("rfc", rfc_nu_cli.getText().toString());
      //  mapa_cliente.put("tiempoCaptura", 112);
        mapa_cliente.put("tipoClasificacion", "Nacional");
        mapa_cliente.put("tipoPersonaFiscal", "Fisica");
       // mapa_cliente.put("usaColoresTallas", false);
      mapa_cliente.put("ventaPublico", false);
      mapa_cliente.put("web", "");


        try {
            mapa_cliente.put("calle", reemplazarAcentos(URLEncoder.encode(calle_cli.getText().toString(), "utf-8")));
            mapa_cliente.put("ciudad", reemplazarAcentos(URLEncoder.encode(ciu_cli.getText().toString(), "utf-8")));
            mapa_cliente.put("colonia", reemplazarAcentos(URLEncoder.encode(colonia_cli.getText().toString(), "utf-8")));
            mapa_cliente.put("cp", cp_cli.getText().toString());
            mapa_cliente.put("delegacion", reemplazarAcentos(URLEncoder.encode(del_cli.getText().toString(), "utf-8")));
            mapa_cliente.put("estado", reemplazarAcentos(URLEncoder.encode(est_cli.getText().toString(), "utf-8")));
            mapa_cliente.put("noe", noe_cli.getText().toString());
            mapa_cliente.put("noi", noi_cli.getText().toString());
            mapa_cliente.put("pais",reemplazarAcentos(URLEncoder.encode(pais_cli.getText().toString(), "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }





        mapa_cliente.put("@class", HashMap.class.getName());
        JSONObject jsonOBJECT1 = new JSONObject(mapa_cliente);


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(jsonOBJECT1.toString());
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=UTF-8"));
            httppost.setEntity(params);
            System.out.println("Lo que no se envia --> " + jsonOBJECT1.toString());
            System.out.println("La cookie que se envia --> " + pc.getCookies().get(0).toString());
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

            System.out.println("Resultado de consulta series: " + result);
            orden = Integer.parseInt(result);


        }
    }


    public static String POST2(String url) {

        Map mapa_cliente= new HashMap();
        mapa_cliente.put("@class", "mx.mgsoftware.erp.entidades.ClienteImpl");
        mapa_cliente.put("id","");
        JSONObject jsonOBJECT1 = new JSONObject(mapa_cliente);


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
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



    public void convertirDatos(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {
           // lista_art.clear();

            HashMap<String,Object> arrayData = mapper.readValue(cadena, HashMap.class);
            del_cli.setText(arrayData.get("municipio").toString());
            ciu_cli.setText(arrayData.get("ciudad").toString());
            est_cli.setText(arrayData.get("estado").toString());

            List colonias = (List) arrayData.get("colonias");
            List colonias2 =  (List) colonias.get(1);
            nombres_cliente.clear();
            for(int i=0; i<colonias2.size(); i++){
                HashMap<String, Object> colonia = (HashMap)colonias2.get(i);
                nombres_cliente.add(colonia.get("nombreAsentamiento").toString());
               // colonia_cli.setText(colonia_cli.getText() + colonia.get("nombreAsentamiento").toString());

            }
            if(nombres_cliente.size()>1){
                build_popup2();
            }
            else{
                colonia_cli.setText(nombres_cliente.get(0));
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
            // lista_art.clear();

            HashMap<String,Object> arrayData = mapper.readValue(cadena, HashMap.class);

            Toast.makeText(getApplicationContext(),    arrayData.get("id").toString(), Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(NuevoCliente.this, MenuComensales.class);
            intent.putExtra("idCliente", arrayData.get("id").toString());
            intent.putExtra("comensales", String.valueOf(comensales));
            intent.putExtra("idMesa", String.valueOf(idMesa));
            intent.putExtra("PedidoArt", JSONValue.toJSONString(lista_art));
            intent.putExtra("PedidoMapa", JSONValue.toJSONString(productos_list));
            intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void build_popup2() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoCliente.this);
        builder.setTitle("Cliente");
        builder.setSingleChoiceItems(nombres_cliente.toArray(new String[nombres_cliente.size()]), 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp2 = which;
                // TODO Auto-generated method stub
            }


        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                selected2 = temp2;

                switch (selected2) {

                    default:
                        colonia_cli.setText(nombres_cliente.get(selected2));


                        break;
                }
                //Toast.makeText(getApplicationContext(), "Seleccionaste " + elCliente + " " + nombre_cliente.get(selected), Toast.LENGTH_LONG).show();

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

            System.out.println("Resultado de consulta series: " + result);
            convertirDatos(result);
          //  orden = Integer.parseInt(result);


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







    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST3(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            System.out.println("Resultado de consulta series 3 : " + result);
            codigoUsuario = Integer.parseInt(result);
            System.out.println("codigoUsuario" + codigoUsuario);

        }
    }


    public static String POST3(String url) {

        Map mapa_cliente= new HashMap();
        mapa_cliente.put("@class", "mx.mgsoftware.erp.entidades.ClienteImpl");
        mapa_cliente.put("id", "");
        JSONObject jsonOBJECT1 = new JSONObject(mapa_cliente);

        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
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



    private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST4(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            convertirDatos4(result);
            //setText(escaner_folio,result);
            // System.out.println("Resultado2: " + result);
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


    public void convertirDatos4(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombres_agente.clear();
            ids_agente.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                //Serie serietmp = new Serie((Integer) mapa1.get("id"), (Integer) mapa1.get("códigoUsuario"), (String) mapa1.get("nombreCorto"), (String) mapa1.get("nombre"));
                nombres_agente.add((String) mapa1.get("nombre"));
                ids_agente.add(((Integer) mapa1.get("id")));

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
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoCliente.this);
        builder.setTitle("Agente");
        builder.setSingleChoiceItems(nombres_agente.toArray(new String[nombres_agente.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp = which;
            }

        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = temp;
                switch (selected) {
                    default:
                        id_agente = ids_agente.get(selected);
                        agente_tv.setText(String.valueOf(id_agente));
                        break;
                }
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

   public static String reemplazarAcentos(String palabra){
        palabra.replaceAll("\\%C3\\%89", "E");
        palabra.replaceAll("\\%C3\\%8D", "I");
        palabra.replaceAll("\\%C3\\%81", "A");
       palabra.replaceAll("\\%C3\\%9A", "U");
       palabra.replaceAll("\\%C3\\%93", "O");
       palabra.replaceAll("\\+", " ");
        return palabra;

    }

    public void convertirMapa(String cadena){
        ObjectMapper mapper = new ObjectMapper();
        try {
            productos_list.clear();
            productos_list = mapper.readValue(cadena, LinkedList.class);
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    public void convertirMapa2(String cadena) {
        info.put("f_pago", "");
        info.put("f_pago_id", "");
        info.put("folio", "");
        ObjectMapper mapper = new ObjectMapper();
        try {
            info = mapper.readValue(cadena, HashMap.class);
            counter = Integer.parseInt(info.get("counter").toString());
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void convertirMapa3(String cadena){
        if (cadena.equals("[]") || cadena.equals("")) {
        }
        else {
            lista_art.clear();
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
                Articulo nuevo = new Articulo(Integer.parseInt(articulo_map.get("id").toString()), articulo_map.get("descripcion").toString(), Double.parseDouble(articulo_map.get("existencia").toString()), articulo_map.get("nombre").toString(), articulo_map.get("nombreCorto"), Double.parseDouble(articulo_map.get("precioBase").toString()), Double.parseDouble(articulo_map.get("impuesto").toString()), Integer.parseInt(articulo_map.get("counter").toString()), Integer.parseInt(articulo_map.get("cantidad").toString()));
                lista_art.add(nuevo);
            }
        }
    }
}
