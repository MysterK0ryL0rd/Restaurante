package com.gm3s.erp.gm3srest.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Model.SharedPreference;

import java.util.Arrays;
import java.util.List;

public class Configuracion extends AppCompatActivity {
    private SharedPreference sharedPreference;
    List<String> servers = Arrays.asList("https://pyme.gm3s-erp.com:6065", "https://oggi.gm3s-erp.com:5050", /*latitude*/"http://192.168.0.14:8080", "https://mi2.gm3s-erp.com:6068", "https://alyp.gm3s-erp.com:6070", "https://azul.gm3s-erp.com:3030", "https://diac.gm3s-erp.com:6073", "https://oggi.gm3s-erp.com:5555", "https://tiendas.gm3s-erp.com:8989", "https://ufra.gm3s-erp.com:9590", "https://ufra.gm3s-erp.com:9591", "https://gm3s.gm3s-erp.com:8383","https://ffmm.gm3s-erp.com:5251");
    int server = -1;
    private Button s1;
    private Button s2;
    private Button s3;
    private Button s4;
    private Button s5;
    private Button s6;
    private Button s7;
    private Button s8;
    private Button s9;
    private Button s10;
    private Button s11;
    private Button s12;
    private Button s13;
    /*"http://192.168.0.3:8080","http://192.168.0.2:8080",*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Configuracion de Servidor");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreference = new SharedPreference();
        s1 = (Button) findViewById(R.id.server1);
        s2 = (Button) findViewById(R.id.server2);
        s3 = (Button) findViewById(R.id.server3);
        s4 = (Button) findViewById(R.id.server4);
        s5 = (Button) findViewById(R.id.server5);
        s6 = (Button) findViewById(R.id.server6);
        s7 = (Button) findViewById(R.id.server7);
        s8 = (Button) findViewById(R.id.server8);
        s9 = (Button) findViewById(R.id.server9);
        s10 = (Button) findViewById(R.id.server10);
        s11 = (Button) findViewById(R.id.server11);
        s12 = (Button) findViewById(R.id.server12);
        s13 = (Button) findViewById(R.id.server13);
        s1.setOnClickListener(onClickListener);
        s2.setOnClickListener(onClickListener);
        s3.setOnClickListener(onClickListener);
        s4.setOnClickListener(onClickListener);
        s5.setOnClickListener(onClickListener);
        s6.setOnClickListener(onClickListener);
        s7.setOnClickListener(onClickListener);
        s8.setOnClickListener(onClickListener);
        s9.setOnClickListener(onClickListener);
        s10.setOnClickListener(onClickListener);
        s11.setOnClickListener(onClickListener);
        s12.setOnClickListener(onClickListener);
        s13.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.server1:
                    server = 0;
                    break;
                case R.id.server2:
                    server = 1;
                    break;
                case R.id.server3:
                    server = 2;
                    break;
                case R.id.server4:
                    server = 3;
                    break;
                case R.id.server5:
                    server = 4;
                    break;
                case R.id.server6:
                    server = 5;
                    break;
                case R.id.server7:
                    server = 6;
                    break;
                case R.id.server8:
                    server = 7;
                    break;
                case R.id.server9:
                    server = 8;
                    break;
                case R.id.server10:
                    server = 9;
                    break;
                case R.id.server11:
                    server = 10;
                    break;
                case R.id.server12:
                    server = 11;
                    break;
                case R.id.server13:
                    server = 12;
                    break;
            }

            sharedPreference.clearSharedPreference(getApplicationContext());
         sharedPreference.save(getApplicationContext(),servers.get(server));
         //   sharedPreference.save("r",servers.get(server));
            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
          //  SharedPreferences.Editor editor = sharedPreferences.edit();
         //   editor.putString("server", servers.get(server));
           // editor.commit();
            Toast.makeText(getApplicationContext(),"Servidor elegido: " + servers.get(server),Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Servidor almacenado",Toast.LENGTH_SHORT).show();
             Intent localIntent = new Intent(Configuracion.this.getApplicationContext(), LogIn.class);
             startActivity(localIntent);
        }
    };




}
