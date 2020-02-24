package com.smoowy.xpos;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Db extends RealmObject {

    @PrimaryKey
    int id;


    int tipoApalancamiento;
    Boolean hayDecimales;
    String cantidad;
    String porcentajeEntero;
    String referencia;
    String redondeoRef;
    Integer apalancamiento;
    String precioDialogPos;
    String porcentajeDialogPos;
    Boolean seLimpioDIalogPos;
    String precioDialogReferencia;
    String cantidadDialogReferencia;
    Boolean hayDatosDialogReferencia;
    String valorDialogReferencia;
    String cantidadDialogCantidad;
    String porcentajeDialogCantidad;
    Boolean hayDatosDialogCantidad;
    Boolean hayDecimalesDoble;
    String precision;
    String precioDialogPorcentaje;
    String largaDialogPorcentaje;
    String cortaDialogPorcentaje;
    Boolean seLimpioDialogPorcentaje;

    public int getTipoApalancamiento() {
        return tipoApalancamiento;
    }

    public void setTipoApalancamiento(int tipoApalancamiento) {
        this.tipoApalancamiento = tipoApalancamiento;
    }

    public Boolean getHayDecimales() {
        return hayDecimales;
    }

    public void setHayDecimales(Boolean hayDecimales) {
        this.hayDecimales = hayDecimales;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPorcentajeEntero() {
        return porcentajeEntero;
    }

    public void setPorcentajeEntero(String porcentajeEntero) {
        this.porcentajeEntero = porcentajeEntero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getRedondeoRef() {
        return redondeoRef;
    }

    public void setRedondeoRef(String redondeoRef) {
        this.redondeoRef = redondeoRef;
    }

    public Integer getApalancamiento() {
        return apalancamiento;
    }

    public void setApalancamiento(Integer apalancamiento) {
        this.apalancamiento = apalancamiento;
    }

    public String getPrecioDialogPos() {
        return precioDialogPos;
    }

    public void setPrecioDialogPos(String precioDialogPos) {
        this.precioDialogPos = precioDialogPos;
    }

    public String getPorcentajeDialogPos() {
        return porcentajeDialogPos;
    }

    public void setPorcentajeDialogPos(String porcentajeDialogPos) {
        this.porcentajeDialogPos = porcentajeDialogPos;
    }

    public Boolean getSeLimpioDIalogPos() {
        return seLimpioDIalogPos;
    }

    public void setSeLimpioDIalogPos(Boolean seLimpioDIalogPos) {
        this.seLimpioDIalogPos = seLimpioDIalogPos;
    }

    public String getPrecioDialogReferencia() {
        return precioDialogReferencia;
    }

    public void setPrecioDialogReferencia(String precioDialogReferencia) {
        this.precioDialogReferencia = precioDialogReferencia;
    }

    public String getCantidadDialogReferencia() {
        return cantidadDialogReferencia;
    }

    public void setCantidadDialogReferencia(String cantidadDialogReferencia) {
        this.cantidadDialogReferencia = cantidadDialogReferencia;
    }

    public Boolean getHayDatosDialogReferencia() {
        return hayDatosDialogReferencia;
    }

    public void setHayDatosDialogReferencia(Boolean hayDatosDialogReferencia) {
        this.hayDatosDialogReferencia = hayDatosDialogReferencia;
    }

    public String getValorDialogReferencia() {
        return valorDialogReferencia;
    }

    public void setValorDialogReferencia(String valorDialogReferencia) {
        this.valorDialogReferencia = valorDialogReferencia;
    }

    public String getCantidadDialogCantidad() {
        return cantidadDialogCantidad;
    }

    public void setCantidadDialogCantidad(String cantidadDialogCantidad) {
        this.cantidadDialogCantidad = cantidadDialogCantidad;
    }

    public String getPorcentajeDialogCantidad() {
        return porcentajeDialogCantidad;
    }

    public void setPorcentajeDialogCantidad(String porcentajeDialogCantidad) {
        this.porcentajeDialogCantidad = porcentajeDialogCantidad;
    }

    public Boolean getHayDatosDialogCantidad() {
        return hayDatosDialogCantidad;
    }

    public void setHayDatosDialogCantidad(Boolean hayDatosDialogCantidad) {
        this.hayDatosDialogCantidad = hayDatosDialogCantidad;
    }

    public Boolean getHayDecimalesDoble() {
        return hayDecimalesDoble;
    }

    public void setHayDecimalesDoble(Boolean hayDecimalesDoble) {
        this.hayDecimalesDoble = hayDecimalesDoble;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getPrecioDialogPorcentaje() {
        return precioDialogPorcentaje;
    }

    public void setPrecioDialogPorcentaje(String precioDialogPorcentaje) {
        this.precioDialogPorcentaje = precioDialogPorcentaje;
    }

    public String getLargaDialogPorcentaje() {
        return largaDialogPorcentaje;
    }

    public void setLargaDialogPorcentaje(String largaDialogPorcentaje) {
        this.largaDialogPorcentaje = largaDialogPorcentaje;
    }

    public String getCortaDialogPorcentaje() {
        return cortaDialogPorcentaje;
    }

    public void setCortaDialogPorcentaje(String cortaDialogPorcentaje) {
        this.cortaDialogPorcentaje = cortaDialogPorcentaje;
    }
    public Boolean getSeLimpioDialogPorcentaje() {
        return seLimpioDialogPorcentaje;
    }

    public void setSeLimpioDialogPorcentaje(Boolean seLimpioDialogPorcentaje) {
        this.seLimpioDialogPorcentaje = seLimpioDialogPorcentaje;
    }
}
