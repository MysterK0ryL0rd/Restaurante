package com.gm3s.erp.gm3srest.Model;

import java.io.Serializable;

/**
 * Created by usuario on 3/05/16.
 */
public class Cliente  implements Serializable {

    private Integer id;
    private String nombre;
    private String apellido_materno;
    private String apellido_paterno;
    private String tipo_persona;
    private Boolean venta_publico;
    private String rfc;
    private String nombre_corto;
    private String descripcion;
    private Integer agente_id;
    private String agente_nombre;
    private String email;
    private Integer dias_credito;
    private String estatus;
    private String clasificacion;
    private String curp;
    private String pagina_web;
    private String pac_facturacion;


    public Cliente() {
    }


    public Cliente(Integer id, String nombre, String apellido_materno, String apellido_paterno, String tipo_persona, Boolean venta_publico, String rfc, String nombre_corto, String descripcion, Integer agente_id, String agente_nombre, String email, Integer dias_credito, String estatus, String clasificacion, String curp, String pagina_web, String pac_facturacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido_materno = apellido_materno;
        this.apellido_paterno = apellido_paterno;
        this.tipo_persona = tipo_persona;
        this.venta_publico = venta_publico;
        this.rfc = rfc;
        this.nombre_corto = nombre_corto;
        this.descripcion = descripcion;
        this.agente_id = agente_id;
        this.agente_nombre = agente_nombre;
        this.email = email;
        this.dias_credito = dias_credito;
        this.estatus = estatus;
        this.clasificacion = clasificacion;
        this.curp = curp;
        this.pagina_web = pagina_web;
        this.pac_facturacion = pac_facturacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo_persona() {
        return tipo_persona;
    }

    public void setTipo_persona(String tipo_persona) {
        this.tipo_persona = tipo_persona;
    }

    public String getApellido_materno() {
        return apellido_materno;
    }

    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getApellido_paterno() {
        return apellido_paterno;
    }

    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public Boolean getVenta_publico() {
        return venta_publico;
    }

    public void setVenta_publico(Boolean venta_publico) {
        this.venta_publico = venta_publico;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getNombre_corto() {
        return nombre_corto;
    }

    public void setNombre_corto(String nombre_corto) {
        this.nombre_corto = nombre_corto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getAgente_id() {
        return agente_id;
    }

    public void setAgente_id(Integer agente_id) {
        this.agente_id = agente_id;
    }

    public String getAgente_nombre() {
        return agente_nombre;
    }

    public void setAgente_nombre(String agente_nombre) {
        this.agente_nombre = agente_nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDias_credito() {
        return dias_credito;
    }

    public void setDias_credito(Integer dias_credito) {
        this.dias_credito = dias_credito;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getPagina_web() {
        return pagina_web;
    }

    public void setPagina_web(String pagina_web) {
        this.pagina_web = pagina_web;
    }

    public String getPac_facturacion() {
        return pac_facturacion;
    }

    public void setPac_facturacion(String pac_facturacion) {
        this.pac_facturacion = pac_facturacion;
    }


    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido_materno='" + apellido_materno + '\'' +
                ", apellido_paterno='" + apellido_paterno + '\'' +
                ", tipo_persona='" + tipo_persona + '\'' +
                ", venta_publico=" + venta_publico +
                ", rfc='" + rfc + '\'' +
                ", nombre_corto='" + nombre_corto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", agente_id=" + agente_id +
                ", agente_nombre='" + agente_nombre + '\'' +
                ", email='" + email + '\'' +
                ", dias_credito=" + dias_credito +
                ", estatus='" + estatus + '\'' +
                ", clasificacion='" + clasificacion + '\'' +
                ", curp='" + curp + '\'' +
                ", pagina_web='" + pagina_web + '\'' +
                ", pac_facturacion='" + pac_facturacion + '\'' +
                '}';
    }
}
