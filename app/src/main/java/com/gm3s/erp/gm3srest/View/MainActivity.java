package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Arrendamiento;
import com.gm3s.erp.gm3srest.AsignacionDeStacks;
import com.gm3s.erp.gm3srest.BusquedaInventario;
import com.gm3s.erp.gm3srest.ConsultarStacks;
import com.gm3s.erp.gm3srest.ConsultarUbicacionRack;
import com.gm3s.erp.gm3srest.ContenedorFragmentos2;
import com.gm3s.erp.gm3srest.ContenedorFragmentos3;
import com.gm3s.erp.gm3srest.ContenedorFragmentosConsultaMenu;
import com.gm3s.erp.gm3srest.Honorarios;
import com.gm3s.erp.gm3srest.ListaArticuloProovedor;
import com.gm3s.erp.gm3srest.MenuCajas;
import com.gm3s.erp.gm3srest.MenuCliente;
import com.gm3s.erp.gm3srest.MenuMesas;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.MovimientosConsumos;
import com.gm3s.erp.gm3srest.Pedido;
import com.gm3s.erp.gm3srest.PedidosPendientes;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.UploadPhoto;
import com.gm3s.erp.gm3srest.Util.Constantes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static PersistentCookieStore pc;
    static String server = "";
    String user = "";
    String company = "";
    private SharedPreference sharedPreference;
    NavigationView navigationView;
    ImageView img;
    Bitmap bitmap;
    Drawable d;
    boolean supervisor_calidad = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_activity_full_content);

        //CookieManager cookieManager = new CookieManager();
        //CookieHandler.setDefault(cookieManager);

        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        company = intent.getStringExtra("usercompany");
        System.out.println("El recibido es : " + user + company);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        server = sharedPreference.getValue(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        verificarPermisos();
        cargaImagen();

        //Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            Intent localIntent = new Intent(MainActivity.this.getApplicationContext(), LogIn.class);
            startActivity(localIntent);



        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (Helper.isConnected3(getApplicationContext())) {

            if (id == R.id.nav_opc1_1) {
                Intent intent = new Intent(this, TotalesSerie2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_1) {
                Intent intent = new Intent(this, ReporteGerencialActividad.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_2) {
                Intent intent = new Intent(this, EstadoResultados.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }




            if (id == R.id.nav_opc1_2) {
                alertInputChooserF();
            }

            if (id == R.id.nav_opc1_3) {
                alertInputChooser();

            }

            if (id == R.id.nav_opc1_0) {
                alertInputChooserP();

            }

            if (id == R.id.nav_opc1_5) {
                Intent intent = new Intent(this, Honorarios.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

            if (id == R.id.nav_opc1_6) {
                Intent intent = new Intent(this, Arrendamiento.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

           

            if (id == R.id.nav_opc2_2) {
                Intent intent = new Intent(this, ListaArticuloProovedor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

            if (id == R.id.nav_opc2_3) {
                Intent intent = new Intent(this, BusquedaInventario.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }


            if (id == R.id.nav_opc1_4) {
                Intent intent = new Intent(this, CorteCaja.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }



            if (id == R.id.nav_opc2_1) {
                Intent intent = new Intent(this, NuevoArticulo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_3){
                Intent intent = new Intent(this, NuevoUsuario.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                }


            if (id == R.id.nav_opc2){
                Intent intent = new Intent(this, Busqueda_Articulo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc3){
                Intent intent = new Intent(this, ReporteExistencias.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc6_1) {
                    Intent intent = new Intent(this, MovimientosConsumos.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }

            if (id == R.id.nav_opc6_2) {
                Intent intent = new Intent(this, AsignacionDeStacks.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc6_3) {
                Intent intent = new Intent(this, ConsultarStacks.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc6_4) {
                Intent intent = new Intent(this, ConsultarUbicacionRack.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_4){
                Intent intent = new Intent(this, MenuCliente.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_5){
                Intent intent = new Intent(MainActivity.this, Pedido.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PedidoMapa", "");
                intent.putExtra("InfoMapa", "");
                intent.putExtra("PedidoArt", "");
                intent.putExtra("tipo_documento", Constantes.Documentos.COTIZACION_CLIENTE);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_6){
                Intent intent = new Intent(this, ListaOrdenServicio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.COTIZACION_CLIENTE);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc4_5){
                Intent intent = new Intent(this, ConsultarStacks.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc5_2){
                Intent intent = new Intent(this, MenuMesas.class);
                System.out.println("user1 " +user);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc5_1){
                Intent intent = new Intent(this, ContenedorFragmentosConsultaMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc5_3){
                Intent intent = new Intent(this, ContenedorFragmentos3.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            if (id == R.id.nav_opc5_4){
                Intent intent = new Intent(this, PedidosPendientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            if (id == R.id.nav_opc5_5){
                Intent intent = new Intent(this, PedidosPendientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("menu_tiempos", "true");
                getApplicationContext().startActivity(intent);
            }
            if (id == R.id.nav_opc5_6){
                Intent intent = new Intent(this, MenuCajas.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startActivity(intent);
            }

            if (id == R.id.nav_opc5_8){
               Intent intent = new Intent(this, UploadPhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
        }

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void verificarPermisos(){



      //  navigationView.getMenu().findItem(R.id.menu_ad_ventas).setVisible(true);
      //  navigationView.getMenu().findItem(R.id.menu_ad_gerencial).setVisible(true);

        /*if(user.equals("administrador")){
            navigationView.getMenu().findItem(R.id.menu_ad_ventas).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_ad_gerencial).setVisible(true);
        }
        else {*/
        //new HttpAsyncTask().execute(server + "/medialuna/spring/editorUsuario/busquedaRolesUsuario/app");


    }

    public void cargaImagen(){
        HttpAsyncTask2 a = new HttpAsyncTask2();
        a.execute(server + "/medialuna/spring/abc/empresa/imagen/LOGO/_/");
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
          System.out.println("Resultado de permisos: " + result);
            if (result.equals("[{\"@class\":\"mx.mgsoftware.erp.seguridad.Rol\",\"id\":84,\"nombre\":\"ROLE_OGGI_SUPERVISOR DE CALIDAD\"},{\"@class\":\"mx.mgsoftware.erp.seguridad.Rol\",\"id\":2,\"nombre\":\"ROLE_USER\"}]")){




                navigationView.getMenu().findItem(R.id.nav_opc2_2).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc3).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc2).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc4_3).setVisible(false);

                navigationView.getMenu().findItem(R.id.nav_opc2_1).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc1_4).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc2_3).setVisible(false);

                navigationView.getMenu().findItem(R.id.nav_opc1_2).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc1_3).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc1_1).setVisible(false);

                navigationView.getMenu().findItem(R.id.nav_opc4_1).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_opc4_2).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav1).setVisible(false);


            }

        }
    }



    public static String POST(String url) {
        String result = "";
        System.out.println("Servidor: " + server);
        try {
            InputStream inputStream = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);

            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                System.out.println("\n 1,- : " + httpResponse.getStatusLine().getStatusCode());

                if (inputStream != null)
                    result = Helper.convertInputStreamToString(inputStream);

                else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {
                    System.out.println("\nNO se  pudo ejecutar la funcion");
                } else {
                    System.out.println("\nFUncion ejecutada");
                }

                System.out.println("\n El resultado es -------> " + result);

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
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            getSupportActionBar().setIcon(d);
           // cargarImagenArticulos();

        }
    }





    public String POST2(String url) {
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
                HttpEntity entity = httpResponse.getEntity();
                BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                InputStream input = b_entity.getContent();
                bitmap = BitmapFactory.decodeStream(input);
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
            Toast.makeText(getApplicationContext(), "Finalizado" + result, Toast.LENGTH_LONG).show();

        }
    }






    public String POST8(String url) {



        Map<String, Object> map2 = new HashMap();
        map2.put("id","66563");
        map2.put("tipo","remisioncliente");
        map2.put("@class",Map.class.getName());


     JSONObject jsonOBJECT1 = new JSONObject(map2);

        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("id", "66563"));
            postParameters.add(new BasicNameValuePair("tipo", "remisioncliente"));

            httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));


            String json = "";
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    //result = Helper.convertInputStreamToString(inputStream);
                   createExternalStoragePrivateFile("T66563","pdf",inputStream);

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


   public void imprimir(){
       HttpAsyncTask8 a = new HttpAsyncTask8();
       a.execute(server + "/medialuna/spring/documento/reporte/ticket/");
       //ModelAndView a;
   }


    public void createExternalStoragePrivateFile(String name, String extension, InputStream is) {
        // Create a path where we will place our private file on external
        // storage.

        System.out.println("********** 1");
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        System.out.println("********** 2");
        File folder = new File(extStorageDirectory, "Download");
        System.out.println("**********");
        folder.mkdir();
        System.out.println("********** 3");
        File file = new File (folder, name + "." + extension);
        System.out.println("********** 4");

        try {
            System.out.println("********** 5");
            Log.d("", "before: " + file.getPath() +
                    " " + file.getTotalSpace() + "  " + file.length());
            System.out.println("********** 6");
            FileOutputStream os = new FileOutputStream(file);
            System.out.println("********** 7");



            // create a buffer...
            byte[] buffer = new byte[8192];
            System.out.println("********** 7.1");
            int bufferLength;
            System.out.println("********** 7.2");

            System.out.println("********** buffer" + buffer.length);
            System.out.println("********** is.read(buffer)" + is.read(buffer));
            while ((bufferLength = is.read(buffer)) != -1) {
                System.out.println("********** 7.3");
                os.write(buffer, 0, bufferLength);
                System.out.println("********** 7.3-");
            }
            System.out.println("********** 7.4");
            os.close();

  
            System.out.println("********** 8");
            Log.d("", "after: " + file.getPath() +
                    " " + file.getTotalSpace() + "  " + file.length());
        } catch (IOException e) {
            System.out.println("********** 9");
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }






    public void alertInputChooser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Selecciona el modo de captura");
        builder.setMessage("");
        builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Choose Camara");
                Intent intent = new Intent(MainActivity.this, DocumentoCamara.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.REMISION_CLIENTE);
                getApplicationContext().startActivity(intent);

            }

        });


        builder.setNeutralButton("Articulos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //  if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                Intent intent = new Intent(MainActivity.this, Pedido.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PedidoMapa", "");
                intent.putExtra("InfoMapa", "");
                intent.putExtra("PedidoArt", "");
                intent.putExtra("tipo_documento", Constantes.Documentos.REMISION_CLIENTE);
                getApplicationContext().startActivity(intent);
                //   } //else {
                //      Toast.makeText(MainActivity.this, "Opcion no disponible para este dispositivo", Toast.LENGTH_LONG).show();
                // }


            }

        });

        builder.setNegativeButton("Escaner", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.out.println("Choose Scaner");
                Intent intent = new Intent(MainActivity.this, DocumentoBluetooth.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.REMISION_CLIENTE);
                getApplicationContext().startActivity(intent);
            }
        });

        AlertDialog al = builder.create();
        al.show();

    }


    public void alertInputChooserF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Modo de captura");
        builder.setMessage("Selecciona el modo de captura");
        builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Choose Camara");
                Intent intent = new Intent(MainActivity.this, DocumentoCamara.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.FACTURA_CLIENTE);
                getApplicationContext().startActivity(intent);

            }

        });

        builder.setNeutralButton("Articulos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

              //  if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                    Intent intent = new Intent(MainActivity.this, Pedido.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PedidoMapa", "");
                    intent.putExtra("InfoMapa", "");
                    intent.putExtra("PedidoArt", "");
                    intent.putExtra("tipo_documento", Constantes.Documentos.FACTURA_CLIENTE);
                    getApplicationContext().startActivity(intent);
             //   } else {
               //     Toast.makeText(MainActivity.this, "Opcion no disponible para este dispositivo", Toast.LENGTH_LONG).show();
               // }


            }

        });


        builder.setNegativeButton("Escaner", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.out.println("Choose Scaner");
                Intent intent = new Intent(MainActivity.this, DocumentoBluetooth.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.FACTURA_CLIENTE);
                getApplicationContext().startActivity(intent);
            }
        });

        AlertDialog al = builder.create();
        al.show();

    }


    public void alertInputChooserP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Modo de captura");
        builder.setMessage("Selecciona el modo de captura");
        builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Choose Camara");
                Intent intent = new Intent(MainActivity.this, DocumentoCamara.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.PEDIDO_CLIENTE);
                getApplicationContext().startActivity(intent);

            }

        });

        builder.setNeutralButton("Articulos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

               // if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                    Intent intent = new Intent(MainActivity.this, Pedido.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PedidoMapa", "");
                    intent.putExtra("InfoMapa", "");
                    intent.putExtra("PedidoArt", "");
                    intent.putExtra("tipo_documento", Constantes.Documentos.PEDIDO_CLIENTE);
                    getApplicationContext().startActivity(intent);
             //   } else {
               //     Toast.makeText(MainActivity.this, "Opcion no disponible para este dispositivo", Toast.LENGTH_LONG).show();
               // }


            }

        });


        builder.setNegativeButton("Escaner", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.out.println("Choose Scaner");
                Intent intent = new Intent(MainActivity.this, DocumentoBluetooth.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tipo_documento", Constantes.Documentos.PEDIDO_CLIENTE);
                getApplicationContext().startActivity(intent);
            }
        });

        AlertDialog al = builder.create();
        al.show();

    }

    public void cargarImagenArticulos(){
        HttpAsyncTask7 a = new HttpAsyncTask7();
        a.execute(server + "/medialuna/spring/app/buscarArticulosImagenes/");

    }

    private class HttpAsyncTask7 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST7(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("Resultado: " + result);
            convertirDatosArticulo(result);

        }
    }


    public static String POST7(String url) {




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

    public void convertirDatosArticulo(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            for(int i=0; i<cadena.length();i+=100){
                System.out.println(cadena.substring(i,i+100));


            }

            List<List<Object>> arrayData = mapper.readValue(cadena, List.class);



            // counter = counter + 1;
            for (int i = 0; i < arrayData.size(); i++) {
                List<ArrayList> datos = (List)arrayData.get(i);
                ArrayList<String> datos1 = (ArrayList) datos.get(1);
                String url = (String) datos1.get(1);

                System.out.println(url);

            }
           // crearTablaArticulos2();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
