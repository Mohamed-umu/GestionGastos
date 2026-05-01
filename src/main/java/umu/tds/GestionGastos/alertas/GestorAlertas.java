package umu.tds.GestionGastos.alertas;

import java.util.ArrayList;
import java.util.List;

import umu.tds.GestionGastos.modelo.Gasto;

public class GestorAlertas {

    // Lista de alertas configuradas por el usuario
    private List<EstrategiaAlerta> alertas;

    // Historial de notificaciones generadas por las alertas
    private List<Notificacion> historial;

    // Constructor q inicializa las listas vacías
    public GestorAlertas() {
    	
    	
        this.alertas = new ArrayList<>();
        this.historial = new ArrayList<>();
    }

    // Añade una nueva alerta al sistema
    public void añadirAlerta(EstrategiaAlerta alerta) {
    	
    	
        alertas.add(alerta);
    }

   //Elimina una alerta según su posición en la lista
    public void eliminarAlerta(int index) {
    	
    	
    	
        if (index >= 0 && index < alertas.size()) {
            alertas.remove(index);
        }
    }

      //Devuelve la lista de alertas activas
    public List<EstrategiaAlerta> getAlertas() {
    	
    	
    	
        return alertas;
    }

    // Comprueba todas las alertas sobre la lista actual de gastos
    public void comprobarAlertas(List<Gasto> gastos) {

        for (EstrategiaAlerta alerta : alertas) {

            // Si una alerta se cumple, se genera una notificación
            if (alerta.comprobar(gastos)) {

                String mensaje = alerta.getMensaje();

                // Cada vez que se supera el límite, se crea una notificación nueva
                historial.add(new Notificacion(mensaje));
            }
        }
    }

    // Devuelve el historial de notificaciones
    public List<Notificacion> getHistorial() {
    	
    	
        return historial;
    }

    // Elimina todas las alertas activas
    public void limpiarAlertas() {
    	
    	
        alertas.clear();
    }

    // Sustituye el historial actual por uno cargado desde persistencia
    public void setHistorial(List<Notificacion> historial) {
    	
    	
        this.historial = new ArrayList<>(historial);
    }
}