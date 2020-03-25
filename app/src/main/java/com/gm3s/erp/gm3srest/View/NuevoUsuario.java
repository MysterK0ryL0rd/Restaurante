package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NuevoUsuario extends AppCompatActivity {


   // POST http://localhost:8080/medialuna/spring/listar/tercero/proveedor


   static EditText nu_nombre, nu_usuario, nu_correo, nu_password, nu_password2, nu_maq_et, nu_suc_et;
    RadioButton nu_rb_1_1, nu_rb_1_2, nu_rb_2_1, nu_rb_2_2;
    RadioGroup nu_rg_1, nu_rg_2;
    static Spinner nu_estatus;
    Button nu_maq_bt, nu_suc_bt, nu_roles, nu_guardar, nu_cancelar;
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    String server = "";
    List<String> nombres_maquilero = new ArrayList<String>();
    List<Integer> ids_maquilero = new ArrayList<Integer>();
    List<String> nombres_sucursal = new ArrayList<String>();
    List<Integer> ids_sucursal = new ArrayList<Integer>();

    List<String> nombres_rol = new ArrayList<String>();
    List<Integer> ids_rol = new ArrayList<Integer>();
    static List<Integer> ids_rol2 = new ArrayList<Integer>();

    int selected = 0;
    int temp = 0;
    static int id_maquilero, id_sucursal;
    static int turno_acceso, turno_estricto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Nuevo Usuario");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        nu_maq_bt = (Button) findViewById(R.id.nu_maq_bt);
        nu_suc_bt = (Button) findViewById(R.id.nu_suc_bt);
        nu_roles = (Button) findViewById(R.id.nu_roles);
        nu_guardar = (Button) findViewById(R.id.nu_guardar);
        nu_cancelar = (Button) findViewById(R.id.nu_cancelar);
        nu_estatus = (Spinner) findViewById(R.id.nu_estatus);
        nu_nombre = (EditText) findViewById(R.id.nu_nombre);
        nu_usuario = (EditText) findViewById(R.id.nu_usuario);
        nu_correo = (EditText) findViewById(R.id.nu_correo);
        nu_password = (EditText) findViewById(R.id.nu_password);
        nu_password2 = (EditText) findViewById(R.id.nu_password2);
        nu_maq_et = (EditText) findViewById(R.id.nu_maq_et);
        nu_suc_et = (EditText) findViewById(R.id.nu_suc_et);

        nu_rb_1_1 = (RadioButton) findViewById(R.id.nu_rb_1_1);
        nu_rb_1_2 = (RadioButton) findViewById(R.id.nu_rb_1_2);
        nu_rb_2_1 = (RadioButton) findViewById(R.id.nu_rb_2_1);
        nu_rb_2_2 = (RadioButton) findViewById(R.id.nu_rb_2_2);

        nu_rg_1 = (RadioGroup) findViewById(R.id.nu_rg_1);
        nu_rg_2 = (RadioGroup) findViewById(R.id.nu_rg_2);



        nu_rg_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.nu_rb_1_1) {
                    turno_acceso = 1;
                } else {
                    turno_acceso = 0;
                }
            }

        });


        nu_rg_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.nu_rb_2_1) {
                    turno_estricto = 1;
                } else {
                    turno_estricto = 0;
                }
            }

        });

        nu_maq_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask2 a = new HttpAsyncTask2();
                a.execute(server + "/medialuna/spring/listar/tercero/proveedor");
            }
        });

        nu_suc_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask3 a = new HttpAsyncTask3();
                a.execute(server + "/medialuna/spring/listar/catalogo/30010/");
            }
        });

        nu_roles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask a = new HttpAsyncTask();
                a.execute(server + "/medialuna/spring/editorUsuario/busquedaRoles/app");  //NO
            }
        });

        nu_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask4 a = new HttpAsyncTask4();
                a.execute(server + "/medialuna/spring/editorUsuario/guardarUsuario/app");
            }
        });

        nu_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            System.out.println("Resultado 01: " + result);
            convertirDatos(result);
            Integer i = null;
            String.valueOf(i).toString();

           // convertirDatos(result);
        }
    }


    public static String POST(String url) {

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
            System.out.println("Resultado: " + result);
             convertirDatos2(result);
        }
    }


    public static String POST2(String url) {
        Map mapa_usuario = new HashMap();
        mapa_usuario.put("maquilero", true);
        mapa_usuario.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(mapa_usuario);

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



    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST3(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("Resultado: " + result);
                convertirDatos3(result);
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
            System.out.println("Resultado: " + result);
            finish();
            // convertirDatos(result);
        }
    }


    public static String POST4(String url) {
        Map mapa_usuario = new HashMap();
        mapa_usuario.put("nombre", nu_nombre.getText().toString());
        mapa_usuario.put("username", nu_usuario.getText().toString());
        mapa_usuario.put("email", nu_correo.getText().toString());
        mapa_usuario.put("password", nu_password.getText().toString());
        mapa_usuario.put("usaTurnoAcceso", turno_acceso);
        mapa_usuario.put("usaTurnoEstricto", turno_estricto);
        mapa_usuario.put("estatus", nu_estatus.getSelectedItem().toString());
        mapa_usuario.put("sucursal", id_sucursal);
        mapa_usuario.put("maquilero", id_maquilero);
        mapa_usuario.put("roles", ids_rol2);

        //  "roles":["java.util.ArrayList",[2,121,107,102,64]]


        mapa_usuario.put("@class", HashMap.class.getName());

        JSONObject jsonOBJECT1 = new JSONObject(mapa_usuario);

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




    public void convertirDatos2(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            nombres_maquilero.clear();
            ids_maquilero.clear();

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                    nombres_maquilero.add((String) mapa1.get("nombreCorto"));
                    ids_maquilero.add((Integer) mapa1.get("id"));

            }
            build_popup2();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }



    public void convertirDatos3(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ids_sucursal.clear();
            nombres_sucursal.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);

            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);
                nombres_sucursal.add((String) mapa1.get("nombreCorto"));
                ids_sucursal.add((Integer) mapa1.get("id"));
                System.out.println("Resultado mapa: " + mapa1);

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



    private void build_popup2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoUsuario.this);
        builder.setTitle("Maquilero");
        builder.setSingleChoiceItems(nombres_maquilero.toArray(new String[nombres_maquilero.size()]), 0, new DialogInterface.OnClickListener() {
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
                        id_maquilero = ids_maquilero.get(selected);
                        nu_maq_et.setText(nombres_maquilero.get(selected));
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


    private void build_popup3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoUsuario.this);
        builder.setTitle("Sucursal");
        builder.setSingleChoiceItems(nombres_sucursal.toArray(new String[nombres_sucursal.size()]), 0, new DialogInterface.OnClickListener() {
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
                        id_sucursal = ids_sucursal.get(selected);
                        nu_suc_et.setText(nombres_sucursal.get(selected));
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


    private void build_popup4() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoUsuario.this);
        builder.setTitle("Roles");
        boolean[] isSelectedArray = new boolean[nombres_rol.size()];
        for(int i=0; i<nombres_rol.size(); i++) {
         isSelectedArray[i] = false;
         }
        builder.setMultiChoiceItems(nombres_rol.toArray(new String[nombres_rol.size()]),isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                temp = which;

                if(isChecked==true){

                    ids_rol2.add(ids_rol.get(temp));

                }

                if(isChecked==false) {
                    ids_rol2.remove(ids_rol.get(temp));
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


    public void convertirDatos(String cadena) {
        System.out.println("Resultado 00");
        ObjectMapper mapper = new ObjectMapper();
        try {
            ids_rol.clear();
            nombres_rol.clear();
            Map<Integer, Object> arrayData = mapper.readValue(cadena, Map.class);
            System.out.println("Resultado 000 " + arrayData.keySet());
            List<Integer> array_keys = new ArrayList<Integer>(arrayData.keySet());


            for (int i = 0; i < array_keys.size(); i++) {
                Map mapa1 = (Map)  arrayData.get(array_keys.get(i));
                 nombres_rol.add((String) mapa1.get("nombre"));
                 ids_rol.add((Integer) mapa1.get("id"));
                System.out.println("Resultado 1" + mapa1 );

              //  Map mapa1 = (Map) arrayData.get(i);
              //  System.out.println("Resultado 0");
                // Set set_aux = mapa1.keySet();
              //  while (arrayData.keySet().iterator().hasNext()){
                 //  Map mapa2 = (Map) arrayData.get(arrayData.keySet().iterator());
                   // nombres_rol.add((String) mapa2.get("nombre"));
                    //ids_rol.add((Integer) mapa2.get("id"));
                    //System.out.println("Resultado 1" + mapa2 + "   " + (Integer) arrayData.keySet().iterator()) ;


                   /* String s2 = informacion.get("t1").toString().substring(1, informacion.get("t1").toString().length() - 1);
                    String[] pairs2 = s2.split(", ");
                    for (int i = 0; i < pairs2.length; i++) {
                        String pair = pairs2[i];
                        String[] keyValue = pair.split("=");
                        datos2.put(keyValue[0], String.valueOf(keyValue[1])); //String.valueOf keyValue
                    }*/


                  //  arrayData.keySet().iterator().remove();

                }
           //}

            System.out.println(nombres_rol.size()  + " " + ids_rol.size());
            build_popup4();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

}
