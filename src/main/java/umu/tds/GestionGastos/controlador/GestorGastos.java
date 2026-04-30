package umu.tds.GestionGastos.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import umu.tds.GestionGastos.modelo.Gasto;

public class GestorGastos {

      //instancia única del gestor de gastos
    private static GestorGastos instancia;

    // Lista de gastos mantenida en memoria durante la ejecución
      private List<Gasto> gastos;

    // Constr privado para aplicar el patrón Singleton
    private GestorGastos() {
        gastos = new ArrayList<>();
    }

    // para devolver la única instancia del gestor
    public static GestorGastos getInstancia() {
        if (instancia == null) {
            instancia = new GestorGastos();
        }
        return instancia;
    }

    // para añadir un gasto a la lista en memoria
    public void añadirGasto(Gasto g) {
    	
        gastos.add(g);
        
    }

      // Elimina un gasto de la lista en memoria
    public void eliminarGasto(Gasto g) {
    	
        gastos.remove(g);
        
    }

      // Devuelve todos los gastos cargados en memoria
    public List<Gasto> getGastos() {
    	
        return gastos;
        
    }

    //Calcula el total gastado sumando todas las cantidades
     public double calcularTotal() {
    	 
    	 
        return gastos.stream()
        		
                .mapToDouble(Gasto::getCantidad)
                .sum();
        
    }

    //devuelve los gastos cuya cantidad es mayor que la indicada
    public List<Gasto> filtrarPorCantidadMayor(double cantidad) {
    	
    	
        return gastos.stream()
                .filter(g -> g.getCantidad() > cantidad)
                .collect(Collectors.toList());
        
    }
}