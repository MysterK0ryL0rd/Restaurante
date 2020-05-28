package com.gm3s.erp.gm3srest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

@SuppressLint("Registered")
public class ContenedorFragmentosConsultaMenu extends FragmentActivity implements MenuLineasPrincipal.SendMessage{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedorfragmentoconsulta);
    }

    @Override
    public void sendData(String message, String title) {
        MenuArticulosPrincipal f2 = (MenuArticulosPrincipal) getFragmentManager().findFragmentById(R.id.ejemplo2);
        f2.getData(message, title);
    }
}
