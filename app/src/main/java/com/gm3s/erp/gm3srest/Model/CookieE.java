package com.gm3s.erp.gm3srest.Model;

import org.apache.http.cookie.Cookie;

import java.util.Date;

/**
 * Created by usuario on 3/11/15.
 */
public class CookieE {
   private String name;
   private String comment;
   private String value;
    private boolean persistent;
    private String domain;
    private String path;
    private String commentURL;
    private Date expiryDate;
    private int[] ports;
    private boolean secure;
    private int version;
    private boolean expired;

   public CookieE(){

    }

    public CookieE(Cookie c){
      this.name = c.getName();
      this.comment = c.getComment();
        this.value = c.getValue();
        this.persistent = c.isPersistent();
        this.domain = c.getDomain();
        this.path = c.getPath();
        this.commentURL = c.getCommentURL();
        this.expiryDate = c.getExpiryDate();
        this.ports = c.getPorts();
        this.secure = c.isSecure();
        this.version = c.getVersion();
       // this.expired = c.getExpiryDate();

    }





    public Cookie returnCookie(Cookie c){

        this.name = c.getName();
        this.comment = c.getComment();
        this.value = c.getValue();
        this.persistent = c.isPersistent();
        this.domain = c.getDomain();
        this.path = c.getPath();
        this.commentURL = c.getCommentURL();
        this.expiryDate = c.getExpiryDate();
        this.ports = c.getPorts();
        this.secure = c.isSecure();
        this.version = c.getVersion();
        //this.expired = c.getExpiryDate();
  return  c;
    }



    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }


    public String getCommentURL() {
        return commentURL;
    }


    public Date getExpiryDate() {
        return expiryDate;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public String getDomain() {
        return domain;
    }


    public String getPath() {
        return path;
    }


    public int[] getPorts() {
        return ports;
    }


    public boolean isSecure() {
        return secure;
    }


    public int getVersion() {
        return version;
    }


    public boolean isExpired(Date date) {
        return false;
    }




    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCommentURL(String commentURL) {
        this.commentURL = commentURL;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setPorts(int[] ports) {
        this.ports = ports;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
