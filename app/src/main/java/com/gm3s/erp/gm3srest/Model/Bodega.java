package com.gm3s.erp.gm3srest.Model;
/**
 * Created by usuario on 4/02/16.
 */
public class Bodega {

   private int bodega;
   private int almacen;

     public Bodega (int bodega, int almacen){
         this.bodega = bodega;
         this.almacen = almacen;
     }

    public int getBodega() {
        return bodega;
    }

    public void setBodega(int bodega) {
        this.bodega = bodega;
    }

    public int getAlmacen() {
        return almacen;
    }

    public void setAlmacen(int almacen) {
        this.almacen = almacen;
    }
}
