package com.gm3s.erp.gm3srest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.ListaOrdenServicio;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadPhoto extends AppCompatActivity {
    private static PersistentCookieStore pc;
    Button btpic, btnup;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1, idDocumento, tipo_documento;
    public static String URL = "Paste your URL here";
    String server = "";
    private SharedPreference sharedPreference;
    ImageView imageView;
    List<Map> tiposArchivo = new ArrayList<>();
    RadioGroup tipos_arhivo_rg;
    String idTipoArchivo = "";
    List<String> nombreTiposExistentes = new ArrayList<>();
    boolean disponibles = true;
    int counterDisponibles = 0;
    int counterExistentes = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);


        Intent intent = getIntent();
        idDocumento = (String)intent.getSerializableExtra("idDocumento");
        tipo_documento = (String)intent.getSerializableExtra("tipo_documento");

        URL = server+ "/medialuna/spring/entidad/adjuntos/"+tipo_documento+"/"+idDocumento+"/archivo/app";


        btpic = (Button) findViewById(R.id.cpic);
        btpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickpic();
                btnup.setEnabled(true);
            }
        });

        btnup = (Button) findViewById(R.id.up);
        btnup.setEnabled(false);
        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload();
            }
        });

        imageView = (ImageView) findViewById(R.id.Imageprev);

        tipos_arhivo_rg = (RadioGroup) findViewById(R.id.tipos_arhivo_rg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Capturar Foto Archivo Adjunto");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadPhoto.this, ListaOrdenServicio.class);
                intent.putExtra("tipo_documento", tipo_documento);
                startActivity(intent);
                finish();
            }
        });




        HttpAsyncTask2 a = new HttpAsyncTask2();
        a.execute(server + "/medialuna/spring/entidad/adjuntos/"+idDocumento+"/"+tipo_documento+"/listar/");
    }

    private void upload() {
        if(disponibles==false && idTipoArchivo.equals("")){
            Intent intent = new Intent(UploadPhoto.this, ListaOrdenServicio.class);
            intent.putExtra("tipo_documento", tipo_documento);
            startActivity(intent);
            finish();
        }
        else {
            // Image location URL
            Log.e("path", "----------------" + picturePath);

            // Image
            Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            //ba1 = Base64.encodeBytes(ba);

            Log.e("base64", "-----" + ba1);

            // Upload image to server
            new uploadToServer().execute();
        }
    }

    private void clickpic() {


        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {


            fileUri  = Uri.parse("file:///sdcard/gm3s_aa.jpg");

            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "El dispositivo no cuenta con camara.", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
           // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
           // imageView.setImageBitmap(bitmap);
           // Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
         //   imageFileUri=Uri.parse("file:///sdcard/picture.jpg");






            File file = new File(Environment.getExternalStorageDirectory().getPath(), "gm3s_aa.jpg");
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
              //  bitmap = crupAndScale(bitmap, 300); // if you mind scaling
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] image=stream.toByteArray();

            ba1 = Base64.encodeToString(image,Base64.DEFAULT);




        }
    }

    public class uploadToServer extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd = new ProgressDialog(UploadPhoto.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Cargando imagen a servidor...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String st= "";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            //nameValuePairs.add(new BasicNameValuePair("base64", ba1));
          //  nameValuePairs.add(new BasicNameValuePair("idEntidad", "119"));
            nameValuePairs.add(new BasicNameValuePair("adjunto", ba1));
            nameValuePairs.add(new BasicNameValuePair("tipo", idTipoArchivo));
          //  nameValuePairs.add(new BasicNameValuePair("entidadArchivo", "remisioncliente"));
            nameValuePairs.add(new BasicNameValuePair("contentType", "image/jpeg"));
           // nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
                HttpResponse response = httpclient.execute(httppost);
                 st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return st;

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            System.out.println("result " + result);
            if(result.equals("false")){
                Toast.makeText(getApplication(), "Imagen superior a 2.5MB, favor de intentar de nuevo", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplication(), "Imagen alamacenada con exito.", Toast.LENGTH_LONG).show();

            }
            Intent intent = new Intent(UploadPhoto.this, ListaOrdenServicio.class);
            intent.putExtra("tipo_documento", tipo_documento);
            startActivity(intent);
            finish();
        }
    }

    public static  Bitmap crupAndScale (Bitmap source,int scale){
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result" + result);
            convertirDatostiposArchivo(result);
        }
    }

    public static String GET(String url) {



        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
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


    public void convertirDatostiposArchivo(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            tiposArchivo.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {
                final HashMap tipoArchivo = (HashMap) ((HashMap) arrayData.get(i)).get("tipoArchivo");


                tiposArchivo.add(tipoArchivo);

                if (tipoArchivo.get("nombre").toString().contains("FOTO")) {

                    RadioButton rb = new RadioButton(this);
                    rb.setText(tipoArchivo.get("nombre").toString());
                    // rb.setChecked(true);


                    rb.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            idTipoArchivo = tipoArchivo.get("id").toString();

                        }

                    });
                    tipos_arhivo_rg.addView(rb);


                    if (nombreTiposExistentes.contains(tipoArchivo.get("nombre").toString())) {
                        counterExistentes++;
                        counterDisponibles++;
                        rb.setEnabled(false);

                    } else {
                        counterDisponibles++;
                      ///  idTipoArchivo = tipoArchivo.get("id").toString();
                      //  ((RadioButton) tipos_arhivo_rg.getChildAt(tipos_arhivo_rg.getChildCount() - 1)).setChecked(true);
                    }
                }
            }

            if(counterDisponibles==counterExistentes){
                btpic.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "El documento no cuenta con archivos de tipo foto disponibles", Toast.LENGTH_LONG).show();
                disponibles = false;
            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET2(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result" + result);
            convertirDatosArchivosExistentes(result);
        }
    }

    public void convertirDatosArchivosExistentes(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            nombreTiposExistentes.clear();
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {
                final HashMap tipoArchivo =  ((HashMap) arrayData.get(i));
                 nombreTiposExistentes.add(tipoArchivo.get("nombre").toString());
            }
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/entidad/adjuntos/tiposArchivo/" + tipo_documento);
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static String GET2(String url) {


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
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
}