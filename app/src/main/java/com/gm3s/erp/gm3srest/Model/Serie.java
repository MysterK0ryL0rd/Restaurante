package com.gm3s.erp.gm3srest.Model;

/**
 * Created by usuario on 3/12/15.
 */
public class Serie {
    private int id;
    private int codigo;
    private String nombreCorto;
    private String nombre;
    private Integer bodega;

    public Serie(int id, int codigo, String nombreCorto, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombreCorto = nombreCorto;
        this.nombre = nombre;

    }

    public Serie(int id, int codigo, String nombreCorto, String nombre, Integer bodega) {
        this.id = id;
        this.codigo = codigo;
        this.nombreCorto = nombreCorto;
        this.nombre = nombre;
        this.bodega = bodega;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getBodega() {
        return bodega;
    }

    public void setBodega(Integer bodega) {
        this.bodega = bodega;
    }


    @Override
    public String toString() {
        return "Serie{" +
                "id=" + id +
                ", codigo=" + codigo +
                ", nombreCorto='" + nombreCorto + '\'' +
                ", nombre='" + nombre + '\'' +
                ", bodega='" + bodega + '\'' +
                '}';
    }
}
