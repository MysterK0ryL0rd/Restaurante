package com.gm3s.erp.gm3srest;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuLineas extends Fragment {
    private static boolean validacion;
    TableLayout prices;
    String server = "";
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    View rootView;
    SendMessage SM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        rootView =  inflater.inflate(R.layout.activity_menu_lineas, container, false);


        return rootView;
    }


    interface SendMessage{
        public void sendData(String message, String title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            SM = (SendMessage)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException("You need to implemenet send Data method");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(getActivity().getApplicationContext());
        pc = new PersistentCookieStore(getActivity().getApplicationContext());
        prices = (TableLayout) getActivity().findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server +"/medialuna/spring/catalogos/1202/registrosPag/");
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            if (validacion) {
                //System.out.println("Resultado : " + result);
                convertirDatos2(result);
            } else {
                Toast.makeText(getContext(), "Usuario no valido", Toast.LENGTH_LONG).show();
            }
        }
    }


    public static String POST(String url) {
        String result = "";
        InputStream inputStream = null;

        LinkedHashMap pagerOrden = new LinkedHashMap();
        pagerOrden.put("@class", LinkedHashMap.class.getName());

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("activos", true);
        entidad.put("maxResults", 100);
        entidad.put("firstResult", 0);
        entidad.put("pagerFiltros", pagerFiltros);
        entidad.put("pagerOrden", pagerOrden);
        String objectStr = JSONValue.toJSONString(entidad);


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
                if (inputStream != null){
                    result = Helper.convertInputStreamToString(inputStream);
                }
                else
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


    public Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            return m1.get("nombre").compareTo(m2.get("nombre"));
        }
    };

    public void convertirDatos2 (String cadena){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            String[] colors = {

                    "#ee6562",
                    "#496c8c"
            };

            String[] colors2 = {
                    "#f6b231",
                    "#10b2a4",
            };

            List<HashMap<String,String>> nombres = new ArrayList<>();

            for(int i=0; i< (arrayData.size()); i++) {
                 HashMap mapa1 = (HashMap) arrayData.get(i);
                nombres.add(mapa1);
            }



            Collections.sort(nombres, mapComparator);

            for(int i=0; i< (nombres.size()); i++) {
                final Map mapa1 = (Map) nombres.get(i);
                 System.out.println((String) mapa1.get("nombre"));
                TableRow tr =  new TableRow(getActivity().getApplicationContext());
                TextView c1 = (TextView) getActivity().getLayoutInflater().inflate(R.layout.menu_opciones_layout, null);
                c1.setBackgroundColor(Color.parseColor(colors[i % 2]));
                c1.setText((String) mapa1.get("nombre"));
                c1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String idLinea = String.valueOf((Integer) mapa1.get("id"));
                        SM.sendData(idLinea,mapa1.get("nombre").toString());

                    }
                });
                     tr.addView(c1);
                prices.addView(tr);
            }






        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }



}
