package com.gm3s.erp.gm3srest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gm3s.erp.gm3srest.Model.Cliente;

public class DescripcionCliente extends AppCompatActivity {

    static private EditText nom_nu_cli, apa_nu_cli, ama_nu_cli, rfc_nu_cli, nco_nu_cli, des_nu_cli, ema_nu_cli, dcre_nu_cli, curp_nu_cli, pweb_nu_cli;
    static private Spinner estatus_sp,pac_sp, clasifi_sp, tipo_persona_sp;
    static private Button agente_btn;
    static private TextView agente_tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);

        Intent intent = getIntent();
        Cliente model = (Cliente) getIntent().getSerializableExtra("Cliente");
        System.out.println("Cliente: " + model);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Cliente");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescripcionCliente.this, MenuCliente.class);
                startActivity(intent);
            }
        });


        nom_nu_cli = (EditText) findViewById(R.id.nom_nu_cli);
        apa_nu_cli = (EditText) findViewById(R.id.apa_nu_cli);
        ama_nu_cli = (EditText) findViewById(R.id.ama_nu_cli);
        rfc_nu_cli = (EditText) findViewById(R.id.rfc_nu_cli);
        nco_nu_cli = (EditText) findViewById(R.id.nco_nu_cli);

        ema_nu_cli = (EditText) findViewById(R.id.ema_nu_cli);

        curp_nu_cli = (EditText) findViewById(R.id.curp_nu_cli);





        agente_tv = (TextView) findViewById(R.id.agente_tv);
        agente_tv.setText(model.getAgente_nombre());

        agente_btn = (Button) findViewById(R.id.agente_btn);
        agente_btn.setVisibility(View.INVISIBLE);


        nom_nu_cli.setEnabled(false);
        nom_nu_cli.setText(model.getNombre());

        apa_nu_cli.setEnabled(false);
        apa_nu_cli.setText(model.getApellido_paterno());

        ama_nu_cli.setEnabled(false);
        ama_nu_cli.setText(model.getApellido_materno());

        rfc_nu_cli.setEnabled(false);
        rfc_nu_cli.setText(model.getRfc());

        nco_nu_cli.setEnabled(false);
        nco_nu_cli.setText(model.getNombre_corto());

        des_nu_cli.setEnabled(false);
        des_nu_cli.setText(model.getDescripcion());

        ema_nu_cli.setEnabled(false);
        ema_nu_cli.setText(model.getEmail());

        dcre_nu_cli.setEnabled(false);
        dcre_nu_cli.setText(model.getDias_credito().toString());

        curp_nu_cli.setEnabled(false);
        curp_nu_cli.setText(model.getCurp());

        pweb_nu_cli.setEnabled(false);
        pweb_nu_cli.setText(model.getPagina_web());


        estatus_sp.setEnabled(false);
        estatus_sp.setPrompt(model.getEstatus());

        pac_sp.setEnabled(false);
        pac_sp.setPrompt(model.getPac_facturacion());

        clasifi_sp.setEnabled(false);
        clasifi_sp.setPrompt(model.getClasificacion());

        tipo_persona_sp.setEnabled(false);
        tipo_persona_sp.setPrompt(model.getTipo_persona());



    }
}
