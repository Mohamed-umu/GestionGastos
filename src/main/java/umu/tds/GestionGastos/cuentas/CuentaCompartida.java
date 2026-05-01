package umu.tds.GestionGastos.cuentas;

import java.util.ArrayList;
import java.util.List;

public class CuentaCompartida {

    // prsonas que forman parte de esta cuenta compartida
    private List<Persona> personas;

    // Lista de gastos asociados a la cuenta compartida
    private List<GastoCompartido> gastos;

       // Estrategia usada para repartir cada gasto entre las personas
    private EstrategiaReparto estrategia;

    // Constructor de la cuenta compartida
    public CuentaCompartida(List<Persona> personas, EstrategiaReparto estrategia) {
        this.personas = personas;
        
        this.gastos = new ArrayList<>();
        
         this.estrategia = estrategia;
    }

    public void añadirGasto(GastoCompartido gasto) {
    	
    	
        //Guardar el gasto dentro de la cuenta
        gastos.add(gasto);

        // Aplicar la estrategia de reparto configurada
        estrategia.repartir(gasto, personas);
    }

    //pa devuelver las personas que pertenecen a la cuenta compartida
    public List<Persona> getPersonas() {
        return personas;
    }
}