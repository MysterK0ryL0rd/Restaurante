package com.gm3s.erp.gm3srest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.gm3s.erp.gm3srest.View.Escaner;
import com.gm3s.erp.gm3srest.View.Factura;

public class NuevoMenu extends AppCompatActivity {
    TableLayout tabla_menu, tabla_documentos;
    String[] colores = {"", ""};
    LinearLayout opc_documentos, opc_reportes, opc_abc, opc_otros;
    LinearLayout opc_remision, opc_factura, opc_honorarios, opc_arrendamiento;
    //LinearLayout opc_documentos, opc_reportes, opc_abc, opc_otros;
    //LinearLayout opc_documentos, opc_reportes, opc_abc, opc_otros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        tabla_menu =(TableLayout) findViewById(R.id.tabla_menu);
        tabla_menu.setStretchAllColumns(true);
        tabla_menu.bringToFront();

        tabla_documentos =(TableLayout) findViewById(R.id.tabla_documentos);
        tabla_documentos.setStretchAllColumns(true);
        tabla_documentos.setVisibility(View.INVISIBLE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        opc_documentos =(LinearLayout) findViewById(R.id.opc_documentos);
        opc_documentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                cambiaMenuDocumentos();

            }
        });


        opc_reportes =(LinearLayout) findViewById(R.id.opc_reportes);
        opc_reportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

             //   cambiaMenuReportes();

            }
        });

        opc_abc =(LinearLayout) findViewById(R.id.opc_abc);
        opc_abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

              //  cambiaMenuABC();

            }
        });

        opc_otros =(LinearLayout) findViewById(R.id.opc_otros);
        opc_otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

              //  cambiaMenuOtras();

            }
        });




    }


    public void  cambiaMenuDocumentos() {
        tabla_menu.setVisibility(View.INVISIBLE);
        tabla_documentos.setVisibility(View.VISIBLE);


        opc_remision =(LinearLayout) findViewById(R.id.opc_remision);
        opc_remision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(NuevoMenu.this, Escaner.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        });


        opc_factura =(LinearLayout) findViewById(R.id.opc_factura);
        opc_factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(NuevoMenu.this, Factura.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        });

        opc_honorarios =(LinearLayout) findViewById(R.id.opc_honorarios);
        opc_honorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(NuevoMenu.this, Honorarios.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        });

        opc_arrendamiento =(LinearLayout) findViewById(R.id.opc_arrendamiento);
        opc_arrendamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(NuevoMenu.this, Arrendamiento.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        });

    }
}


