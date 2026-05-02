package umu.tds.GestionGastos.compartidas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import umu.tds.GestionGastos.modelo.Gasto;

public class CuentaCompartida {

    //nombre identificativo de la cuenta compartida
    private String nombre;

       //personas que forman parte de la cuenta
    private List<String> personas;

     // Saldo pendiente de cada persona
    private Map<String, Double> saldos;

     //porcentaje de gasto asumido por cada persona
    private Map<String, Double> porcentajes;

      // Constructor vacío necesario para Jackson
    public CuentaCompartida() {
        this.personas = new ArrayList<>();
        this.saldos = new HashMap<>();
        this.porcentajes = new HashMap<>();
    }

    // constrructor usado al crear una cuenta compartida nueva
    public CuentaCompartida(String nombre, List<String> personas) {
    	
    	
    	
        this.nombre = nombre;
        this.personas = new ArrayList<>(personas);
        this.saldos = new HashMap<>();
        this.porcentajes = new HashMap<>();

        // Inicialmente todas las personas tienen saldo 0
        for (String p : personas) {
            saldos.put(p, 0.0);
        }
    }

    public void añadirGastoEquitativo(double cantidad, String pagador) {
    	
    	
    	
    	

        // El pagador debe pertenecer a la cuenta compartida
        if (!personas.contains(pagador)) {
            throw new IllegalArgumentException("El pagador no pertenece a la cuenta");
        }

        // Cada persona asume la misma parte del gasto
        double parte = cantidad / personas.size();

        // Primero se resta a todos la parte que deben asumir
        for (String persona : personas) {
        	
        	
            saldos.put(persona, saldos.get(persona) - parte);
        }

        // Después se suma al pagador el total que ha pagado
        saldos.put(pagador, saldos.get(pagador) + cantidad);
    }

    public void añadirGastoPorcentual(double cantidad, String pagador) {

        // El pagador debe pertenecer a la cuenta compartida
        if (!personas.contains(pagador)) {
            throw new IllegalArgumentException("El pagador no pertenece a la cuenta");
        }

          // Comprobar que los porcentajes están bien configurados
        validarPorcentajes();

        // Cada persona asume la parte correspondiente a su porcentaje
        for (String persona : personas) {
        	
        	
            double porcentaje = porcentajes.get(persona);
            double parte = cantidad * porcentaje / 100.0;

            saldos.put(persona, saldos.get(persona) - parte);
        }

        //El pagador recupera el importe completo que ha adelantado
        saldos.put(pagador, saldos.get(pagador) + cantidad);
    }

    public void configurarPorcentajes(Map<String, Double> porcentajes) {
    	
    	
        //guardar el reparto porcentual definido por el usuario
        this.porcentajes = porcentajes;

         // Validar que el reparto sea correcto
        validarPorcentajes();
    }

    private void validarPorcentajes() {

        // Debe existir un mapa de porcentajes
        if (porcentajes == null || porcentajes.isEmpty()) {
        	
        	
            throw new IllegalArgumentException("No hay porcentajes configurados");
        }

        double total = 0;

        // Todas las personas deben tener un porcentaje asignado
        for (String persona : personas) {
        	

            if (!porcentajes.containsKey(persona)) {
            	
            	
                throw new IllegalArgumentException("Falta porcentaje para " + persona);
            }

            
            total += porcentajes.get(persona);
        }

        // La suma total de porcentajes debe ser 100
        if (Math.abs(total - 100.0) > 0.01) {
        	
        	
            throw new IllegalArgumentException("La suma de porcentajes debe ser 100%");
        }
    }

    public void reiniciarSaldos() {
    	
    	
        // Borra los saldos actuales
        saldos.clear();

        // Vuelve a poner todos los saldos a 0
        for (String persona : personas) {
            saldos.put(persona, 0.0);
        }
    }

    public void actualizarPersonas(List<String> nuevasPersonas) {
    	
    	
        // Sustituir la lista de personas por la nueva
        this.personas = new ArrayList<>(nuevasPersonas);

        // Mantener los saldos existentes de las personas que sigan estando
        Map<String, Double> nuevosSaldos = new HashMap<>();

        for (String persona : nuevasPersonas) {
            nuevosSaldos.put(persona, saldos.getOrDefault(persona, 0.0));
        }

        this.saldos = nuevosSaldos;

        // Mantener porcentajes existentes solo para personas que sigan en la cuenta
        Map<String, Double> nuevosPorcentajes = new HashMap<>();

        for (String persona : nuevasPersonas) {
            if (porcentajes.containsKey(persona)) {
                nuevosPorcentajes.put(persona, porcentajes.get(persona));
            }
        }

        this.porcentajes = nuevosPorcentajes;
    }

    public boolean aplicarGastoSiCorresponde(Gasto gasto) {

        // Si el gasto no pertenece a esta cuenta, no se aplica
        if (gasto.getCuenta() == null || !gasto.getCuenta().equalsIgnoreCase(nombre)) {
            
        	return false;
        }

        String pagador = gasto.getPagador();
        double cantidad = gasto.getCantidad();

        // El pagador del gasto debe estar dentro de la cuenta compartida
        if (!personas.contains(pagador)) {
           
        	throw new IllegalArgumentException("El pagador no pertenece a la cuenta compartida");
        }

        /*
         * Si no hay porcentajes configurados, el reparto es equitativo,pero
         * si existen porcentajes se usa el reparto personalizado definido por el usuario
         */
        if (porcentajes == null || porcentajes.isEmpty()) {
            añadirGastoEquitativo(cantidad, pagador);
        } else {
            añadirGastoPorcentual(cantidad, pagador);
        }

        return true;
    }

    // pa devolver  el nombre de la cuenta
    public String getNombre() {
        return nombre;
    }

    // Devuelve la lista de personas de la cuenta
    public List<String> getPersonas() {
        return personas;
    }

    // devuelve los saldos de las personas
    public Map<String, Double> getSaldos() {
        return saldos;
    }

    // Devuelve los porcentajes configurados
    public Map<String, Double> getPorcentajes() {
        return porcentajes;
    }

    // Modificar el nombre de la cuenta
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Modifica la lista de personas
    public void setPersonas(List<String> personas) {
        this.personas = personas;
    }

    // Modifica los saldos
    public void setSaldos(Map<String, Double> saldos) {
        this.saldos = saldos;
    }

    // Modifica los porcentajes
    public void setPorcentajes(Map<String, Double> porcentajes) {
        this.porcentajes = porcentajes;
    }
}