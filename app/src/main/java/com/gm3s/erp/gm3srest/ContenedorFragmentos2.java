package com.gm3s.erp.gm3srest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ContenedorFragmentos2 extends FragmentActivity implements MenuLineas2.SendMessage{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_fragmentos2);
    }

    @Override
    public void sendData(String message, String title) {
        MenuArticulosLineas2 f2 = (MenuArticulosLineas2)getFragmentManager().findFragmentById(R.id.ejemplo2);
        f2.getData(message, title);
    }


}
