package umu.tds.GestionGastos.alertas;

import java.time.LocalDate;

public class Notificacion {

    // Mensaje que describe la alerta generada
    private String mensaje;

    // fecha en la que se creóo la notificación
    private LocalDate fecha;

    // Constructor vacío ya q Jackson pueda cargar desde JSON
    public Notificacion() {
    }

    // Constructor usado cuando se genera una nueva notificación
    public Notificacion(String mensaje) {
    	
        this.mensaje = mensaje;
        this.fecha = LocalDate.now();
    }

    // Devuelve el mensaje de la notificación
    public String getMensaje() {
    	
    	
        return mensaje;
    }

    // Modificar el mensaje de la notificación
    public void setMensaje(String mensaje) {
    	
    	
        this.mensaje = mensaje;
    }

    // Devuelve la fecha de creación de la notificación
    public LocalDate getFecha() {
        return fecha;
    }

    // para modifica la fecha de la notificación
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}