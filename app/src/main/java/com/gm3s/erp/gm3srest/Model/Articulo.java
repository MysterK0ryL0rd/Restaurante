package com.gm3s.erp.gm3srest.Model;

import java.io.Serializable;

/**
 * Created by usuario on 15/12/15.
 */
public class Articulo implements Serializable {
    private Integer id;
    private String descripcion ="";
    private Double existencia;
    private String nombre;
    private String nombreCorto;
    private Double precioBase;
    private Double precioLista;
    private Double impuesto;
    private Integer counter;
    private Integer codigoUsuario;
    private Integer cantidad = 0;
    private String referencia = "";
    private String awsUrl = "";
    private String photo ="";

    public Articulo(Integer id, String descripcion, Double existencia, String nombre, String nombreCorto, Double precioBase, Double precioLista, Double impuesto) {
        this.id = id;
        if(descripcion.length()>10000){
            this.descripcion = descripcion.substring(0,9999) + " ";
        } else {
            this.descripcion = descripcion+ " ";
        }
        this.existencia = existencia;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.precioLista = precioLista;
        this.impuesto = impuesto;
    }

    public Articulo(Integer id, String descripcion, Double existencia, String nombre, String nombreCorto, Double precioBase, Double precioLista, Double impuesto,String referencia, String awsUrl) {
        this.id = id;
        if(descripcion.length()>10000){
            this.descripcion = descripcion.substring(0,9999) + " ";
        } else {
            this.descripcion = descripcion+ " ";
        }
        this.existencia = existencia;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.precioLista = precioLista;
        this.impuesto = impuesto;
        this.referencia = referencia;
        this.awsUrl = awsUrl;
        //this.photo = photo;
    }


    public Articulo(Integer id, String nombreCorto, Double precioBase, Integer codigoUsuario) {
        this.id = id;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.codigoUsuario = codigoUsuario;
    }

    public Articulo(Integer id, String descripcion,Double existencia, String nombre, String nombreCorto, Double precioBase, Double impuesto) {
        this.id = id;
        if(descripcion.length()>10000){
            this.descripcion = descripcion.substring(0,9999) + " ";
        } else {
            this.descripcion = descripcion+ " ";
        }
        this.existencia = existencia;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.impuesto = impuesto;
    }

    public Articulo(Integer id, String descripcion,Double existencia, String nombre, String nombreCorto, Double precioBase, Double impuesto, Integer counter) {
        this.id = id;
        if(descripcion.length()>10000){
            this.descripcion = descripcion.substring(0,9999) + " ";
        } else {
            this.descripcion = descripcion+ " ";
        }
        this.existencia = existencia;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.impuesto = impuesto;
        this.counter = counter;
    }


    public Articulo(Integer id, String descripcion,Double existencia, String nombre, String nombreCorto, Double precioBase, Double impuesto, Integer counter, Integer cantidad) {
        this.id = id;
        if(descripcion.length()>10000){
            this.descripcion = descripcion.substring(0,9999) + " ";
        } else {
            this.descripcion = descripcion+ " ";
        }
        this.existencia = existencia;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.precioBase = precioBase;
        this.impuesto = impuesto;
        this.counter = counter;
        this.cantidad = cantidad;
    }




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getExistencia() {
        return existencia;
    }

    public void setExistencia(Double existencia) {
        this.existencia = existencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    public Double getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(Double precioLista) {
        this.precioLista = precioLista;
    }

    public Double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }


    public Integer getCodigoUsuario() {
        return counter;
    }

    public void setCodigoUsuario(Integer codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }


    public String getAwsUrl() {
        return awsUrl;
    }

    public void setAwsUrl(String awsUrl) {
        this.awsUrl = awsUrl;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", existencia=" + existencia +
                ", nombre='" + nombre + '\'' +
                ", nombreCorto='" + nombreCorto + '\'' +
                ", precioBase=" + precioBase +
                ", precioLista=" + precioLista +
                ", impuesto=" + impuesto +
                ", counter=" + counter +
                ", codigoUsuario=" + codigoUsuario +
                        ", cantidad=" + cantidad +
                        ", extra=" + referencia +
                        ", awsUrl=" + awsUrl;
    }
}
