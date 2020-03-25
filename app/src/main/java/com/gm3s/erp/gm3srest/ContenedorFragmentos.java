package com.gm3s.erp.gm3srest;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class ContenedorFragmentos extends FragmentActivity implements MenuLineas.SendMessage{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_fragmentos);
    }

    @Override
    public void sendData(String message, String title) {
        MenuArticulosLineas f2 = (MenuArticulosLineas)getFragmentManager().findFragmentById(R.id.ejemplo2);
        f2.getData(message, title);
    }


}
