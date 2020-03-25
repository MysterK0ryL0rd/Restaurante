package com.gm3s.erp.gm3srest.Model;

/**
 * Created by usuario on 22/10/15.
 */
public class Usuario {
 private String user;
 private String compania;
 private String password;
 private String code;
 private String email;

    public Usuario() {
    }

    public Usuario(String user, String compania, String password, String code, String email) {
        this.user = user;
        this.compania = compania;
        this.password = password;
        this.code = code;
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
