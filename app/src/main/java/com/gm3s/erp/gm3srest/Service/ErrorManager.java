package com.gm3s.erp.gm3srest.Service;

/**
 * Created by usuario on 28/01/16.
 */
public class ErrorManager {


    public String error(String cadena){
        if(cadena.contains("Clave de Usuario:")){
            cadena = "Sesison Terminada, Favor de ingresar de nuevo.";
            // HTTP Status 404
        }

        if(cadena.contains("Campos duplicados en otro(s) registro(s):")){
            cadena = "Campos duplicados en otro(s) registro(s).";
        }
        return cadena;


    }


}
