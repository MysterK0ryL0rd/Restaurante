package com.gm3s.erp.gm3srest.Model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by usuario on 27/10/16.
 */
public class Mesa implements Serializable {
    private Integer empresaMesa;
    private Integer indexMesa;
    private Boolean estadoMesa;
    private Integer idDocumento;
    private Integer noComensales;
    private String userName;
    private Map<String,Integer> cifras;

    public Mesa(Integer empresaMesa, Integer indexMesa, Boolean estadoMesa,Integer idDocumento,Integer noComensales) {
        this.empresaMesa = empresaMesa;
        this.indexMesa = indexMesa;
        this.estadoMesa = estadoMesa;
        this.idDocumento = idDocumento;
        this.noComensales = noComensales;
    }
    public Mesa(Integer empresaMesa, Integer indexMesa, Boolean estadoMesa,Integer idDocumento,Integer noComensales, String userName) {
        this.empresaMesa = empresaMesa;
        this.indexMesa = indexMesa;
        this.estadoMesa = estadoMesa;
        this.idDocumento = idDocumento;
        this.noComensales = noComensales;
        this.userName = userName;
    }

    public Mesa(Integer empresaMesa, Integer indexMesa, Boolean estadoMesa,Integer idDocumento,Integer noComensales, String userName, Map cifras) {
        this.empresaMesa = empresaMesa;
        this.indexMesa = indexMesa;
        this.estadoMesa = estadoMesa;
        this.idDocumento = idDocumento;
        this.noComensales = noComensales;
        this.userName = userName;
        this.cifras=cifras;
    }
    public Mesa(Integer empresaMesa, Integer indexMesa, Boolean estadoMesa) {
        this.empresaMesa = empresaMesa;
        this.indexMesa = indexMesa;
        this.estadoMesa = estadoMesa;
    }

    public Mesa(Integer empresaMesa, Integer indexMesa, Integer idDocumento) {
        this.empresaMesa = empresaMesa;
        this.indexMesa = indexMesa;
        this.idDocumento = idDocumento;
    }

    public Integer getEmpresaMesa() {
        return empresaMesa;
    }

    public void setEmpresaMesa(Integer empresaMesa) {
        this.empresaMesa = empresaMesa;
    }

    public Integer getIndexMesa() {
        return indexMesa;
    }

    public void setIndexMesa(Integer indexMesa) {
        this.indexMesa = indexMesa;
    }

    public Boolean getEstadoMesa() {
        return estadoMesa;
    }

    public void setEstadoMesa(Boolean estadoMesa) {
        this.estadoMesa = estadoMesa;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Integer getNoComensales() {
        return noComensales;
    }

    public void setNoComensales(Integer noComensales) {
        this.noComensales = noComensales;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Integer> getCifras() {
        return cifras;
    }

    public void setCifras(Map<String, Integer> cifras) {
        this.cifras = cifras;
    }
}
