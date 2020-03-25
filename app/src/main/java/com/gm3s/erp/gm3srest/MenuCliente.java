package com.gm3s.erp.gm3srest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.Cliente;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MenuCliente extends AppCompatActivity {

    LinearLayout opc_menu_consultar, opc_menu_nuevo;
    static boolean validacion = false;
    List<String> nombre_cliente = new ArrayList<String>();
    List<Integer> id_cliente = new ArrayList<Integer>();
    List<String> nombre_agente = new ArrayList<String>();
    List<String> id_agente = new ArrayList<String>();
    List<Integer> id_moneda = new ArrayList<Integer>();
    List<Integer> direccion_cliente = new ArrayList<Integer>();
    List<String> rfc_cliente = new ArrayList<String>();
    int temp;
    int selected = 0;
    static private String elCliente = "";
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    String server = "";
    static String name;
    List<Cliente> listaClientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);


        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuCliente.this, MainActivity.class);
                startActivity(intent);
            }
        });


        opc_menu_consultar = (LinearLayout) findViewById(R.id.opc_menu_consultar);
        opc_menu_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuCliente.this);
                alertDialog.setTitle("Buscar Cliente");
                alertDialog.setMessage("Ingresa una palabra de busqueda");

                final EditText input = new EditText(MenuCliente.this);
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







        opc_menu_nuevo = (LinearLayout) findViewById(R.id.opc_menu_nuevo);
        opc_menu_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent intent = new Intent(MenuCliente.this, NuevoCliente.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);


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
        System.out.println("Esta es la cadena: " + cadena.substring(0,2000));
        System.out.println(cadena.substring(2000,4000));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuCliente.this);
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
                Intent i = new Intent(MenuCliente.this, DescripcionCliente.class);
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


}
