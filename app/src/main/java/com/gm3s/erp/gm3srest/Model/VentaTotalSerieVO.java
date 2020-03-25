package com.gm3s.erp.gm3srest.Model;

/**
 * Created by usuario on 10/11/15.
 */
        import  java.math.BigDecimal;


//@I18n
public class VentaTotalSerieVO {

    private Integer id;
    private String nombre;
    private String nombreCorto;
    private BigDecimal subtotalTotal = new BigDecimal("0");
    private BigDecimal ivaTotal = new BigDecimal("0");
    private BigDecimal retencionesTotal = new BigDecimal("0");
    private BigDecimal totalTotal = new BigDecimal("0");
    private BigDecimal subtotalFactura = new BigDecimal("0");
    private BigDecimal ivaFactura = new BigDecimal("0");
    private BigDecimal retencionesFactura = new BigDecimal("0");
    private BigDecimal totalFactura = new BigDecimal("0");
    private BigDecimal subtotalRemision = new BigDecimal("0");
    private BigDecimal ivaRemision = new BigDecimal("0");
    private BigDecimal retencionesRemision = new BigDecimal("0");
    private BigDecimal totalRemision = new BigDecimal("0");
    private BigDecimal subtotalNotaCredito = new BigDecimal("0");
    private BigDecimal ivaNotaCredito = new BigDecimal("0");
    private BigDecimal retencionesNotaCredito = new BigDecimal("0");
    private BigDecimal totalNotaCredito = new BigDecimal("0");

    public VentaTotalSerieVO() {
    }

    public VentaTotalSerieVO(String nombre, String nombreCorto, BigDecimal totalTotal, BigDecimal subtotalTotal, BigDecimal ivaTotal, BigDecimal subtotalFactura, BigDecimal ivaFactura, BigDecimal totalFactura, BigDecimal totalRemision, BigDecimal subtotalNotaCredito, BigDecimal ivaNotaCredito, BigDecimal totalNotaCredito) {
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.totalTotal = totalTotal;
        this.subtotalTotal = subtotalTotal;
        this.ivaTotal = ivaTotal;
        this.totalTotal = totalTotal;
        this.subtotalFactura = subtotalFactura;
        this.ivaFactura = ivaFactura;
        this.totalFactura = totalFactura;
        this.totalRemision = totalRemision;
        this.subtotalNotaCredito = subtotalNotaCredito;
        this.ivaNotaCredito = ivaNotaCredito;
        this.totalNotaCredito = totalNotaCredito;
         }

    //@CampoOculto
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

   // @ViewOrden(orden=1)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    //@ViewOrden(orden=2)
    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    //@ViewOrden(orden = 3)
    //@FormatoNúmero(Formato.MONEDA)
    public BigDecimal getSubtotalTotal() {
        return subtotalTotal;
    }

    public void setSubtotalTotal(BigDecimal subtotalTotal) {
        this.subtotalTotal = subtotalTotal;
    }

  //  @ViewOrden(orden = 4)
  //  @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getIvaTotal() {
        return ivaTotal;
    }

    public void setIvaTotal(BigDecimal ivaTotal) {
        this.ivaTotal = ivaTotal;
    }

   // @ViewOrden(orden = 5)
    //@FormatoNúmero(Formato.MONEDA)
    public BigDecimal getRetencionesTotal() {
        return retencionesTotal;
    }

    public void setRetencionesTotal(BigDecimal retencionesTotal) {
        this.retencionesTotal = retencionesTotal;
    }

   // @ViewOrden(orden = 6)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getTotalTotal() {
        return totalTotal;
    }

    public void setTotalTotal(BigDecimal totalTotal) {
        this.totalTotal = totalTotal;
    }

   // @ViewOrden(orden = 7)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getSubtotalFactura() {
        return subtotalFactura;
    }

    public void setSubtotalFactura(BigDecimal subtotalFactura) {
        this.subtotalFactura = subtotalFactura;
    }

   // @ViewOrden(orden = 8)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getIvaFactura() {
        return ivaFactura;
    }

    public void setIvaFactura(BigDecimal ivaFactura) {
        this.ivaFactura = ivaFactura;
    }

    //@ViewOrden(orden = 9)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getRetencionesFactura() {
        return retencionesFactura;
    }

    public void setRetencionesFactura(BigDecimal retencionesFactura) {
        this.retencionesFactura = retencionesFactura;
    }

    //@ViewOrden(orden = 10)
    //@FormatoNúmero(Formato.MONEDA)
    public BigDecimal getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(BigDecimal totalFactura) {
        this.totalFactura = totalFactura;
    }

    //@ViewOrden(orden = 11)
    //@FormatoNúmero(Formato.MONEDA)
    public BigDecimal getSubtotalRemision() {
        return subtotalRemision;
    }

    public void setSubtotalRemision(BigDecimal subtotalRemision) {
        this.subtotalRemision = subtotalRemision;
    }

   // @ViewOrden(orden = 12)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getIvaRemision() {
        return ivaRemision;
    }

    public void setIvaRemision(BigDecimal ivaRemision) {
        this.ivaRemision = ivaRemision;
    }

   // @ViewOrden(orden = 13)
  //  @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getRetencionesRemision() {
        return retencionesRemision;
    }

    public void setRetencionesRemision(BigDecimal retencionesRemision) {
        this.retencionesRemision = retencionesRemision;
    }

    //@ViewOrden(orden = 14)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getTotalRemision() {
        return totalRemision;
    }

    public void setTotalRemision(BigDecimal totalRemision) {
        this.totalRemision = totalRemision;
    }

   // @ViewOrden(orden = 15)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getSubtotalNotaCredito() {
        return subtotalNotaCredito;
    }

    public void setSubtotalNotaCredito(BigDecimal subtotalNotaCredito) {
        this.subtotalNotaCredito = subtotalNotaCredito;
    }

 //   @ViewOrden(orden = 16)
//    @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getIvaNotaCredito() {
        return ivaNotaCredito;
    }

    public void setIvaNotaCredito(BigDecimal ivaNotaCredito) {
        this.ivaNotaCredito = ivaNotaCredito;
    }

    //@ViewOrden(orden = 17)
    //@FormatoNúmero(Formato.MONEDA)
    public BigDecimal getRetencionesNotaCredito() {
        return retencionesNotaCredito;
    }

    public void setRetencionesNotaCredito(BigDecimal retencionesNotaCredito) {
        this.retencionesNotaCredito = retencionesNotaCredito;
    }

    //@ViewOrden(orden = 18)
   // @FormatoNúmero(Formato.MONEDA)
    public BigDecimal getTotalNotaCredito() {
        return totalNotaCredito;
    }

    public void setTotalNotaCredito(BigDecimal totalNotaCredito) {
        this.totalNotaCredito = totalNotaCredito;
    }


    @Override
    public String toString() {
        return "Objeto : " + nombre + "   " + nombreCorto + "   " + totalTotal + "  " + subtotalTotal + "  " + ivaTotal + "  " + subtotalFactura + "  " + ivaFactura + "  " + totalFactura + "  " + totalRemision + "  " + subtotalNotaCredito + "  " + ivaNotaCredito + "  " + totalNotaCredito;
    }
}
