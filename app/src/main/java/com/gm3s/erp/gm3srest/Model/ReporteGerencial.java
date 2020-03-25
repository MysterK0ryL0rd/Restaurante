package com.gm3s.erp.gm3srest.Model;
/**
 * Created by usuario on 10/11/15.
 */
public class ReporteGerencial {
    private int id;
    private int id2;
    private String nombre;
    private String valor;

    public ReporteGerencial() {
    }

    public ReporteGerencial(int id, int id2,String nombre, String valor) {
        this.id = id;
        this.id2 = id2;
        this.nombre = nombre;
        this.valor = valor;
    }

    public ReporteGerencial(int id,int id2, String nombre) {
        this.id2 = id2;
        this.id = id;
        this.nombre = nombre;
        this.valor = "-";

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Objeto: " + id + " " + " " + id2 + nombre + " " + valor;

    }
}
