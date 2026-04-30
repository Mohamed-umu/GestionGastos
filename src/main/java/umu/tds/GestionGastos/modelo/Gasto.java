package umu.tds.GestionGastos.modelo;

import java.time.LocalDateTime;

public class Gasto {

    // fecha y hora en la que se realizó el gasto
      private LocalDateTime fecha;

    // La Cuenta asociada al gasto, Personal o una cuenta compartida
    private String cuenta;

    // Categoría principal del gasto
    private String categoria;

    // Subcategoría del gasto
    private String subcategoria;

    // Descripción o nota del gasto
    private String descripcion;

    // Persona que ha pagado el gasto
    private String pagador;

    // Importe del gasto
    private double cantidad;

    // Moneda del gasto, por ejemplo EUR
    private String moneda;

    // Constructor vacío necesario para que Jackson pueda cargar gastos desde JSON
    public Gasto() {
    	
    	
    }

       // Constructor usado para crear un gasto completo
    public Gasto(LocalDateTime fecha, String cuenta, String categoria,
                 String subcategoria, String descripcion,
                 String pagador, double cantidad, String moneda) {

        this.fecha = fecha;
        this.cuenta = cuenta;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.pagador = pagador;
        this.cantidad = cantidad;
        
        this.moneda = moneda;
    }

    // Devuelve la fecha y hora del gasto
    public LocalDateTime getFecha() {
    	
    	
        return fecha;
    }

    // Modifica la fecha y hora del gasto
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Devuelve la cuenta asociada al gasto
    public String getCuenta() {
        return cuenta;
    }

    // Modifica la cuenta asociada al gasto
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    // Devuelve la categoría del gasto
    public String getCategoria() {
        return categoria;
    }

    // Modifica la categoría del gasto
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Devuelve la subcategoría del gasto
    public String getSubcategoria() {
        return subcategoria;
    }

    // Modifica la subcategoría del gasto
    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    // Devuelve la descripción del gasto
    public String getDescripcion() {
        return descripcion;
    }

    // Modifica la descripción del gasto
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Devuelve la persona que pagó el gasto
    public String getPagador() {
        return pagador;
    }

    // Modifica la persona que pagó el gasto
    public void setPagador(String pagador) {
        this.pagador = pagador;
    }

    // Devuelve la cantidad del gasto
    public double getCantidad() {
        return cantidad;
    }

    // Modifica la cantidad del gasto
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    // Devuelve la moneda del gasto
    public String getMoneda() {
        return moneda;
    }

    // Modifica la moneda del gasto
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}