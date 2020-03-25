package com.gm3s.erp.gm3srest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ContenedorFragmentos3 extends FragmentActivity implements MenuLineas3.SendMessage{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_fragmentos3);
    }

    @Override
    public void sendData(String message, String title) {
        MenuArticulosLineas3 f2 = (MenuArticulosLineas3)getFragmentManager().findFragmentById(R.id.ejemplo2);
        f2.getData(message, title);
    }


}
