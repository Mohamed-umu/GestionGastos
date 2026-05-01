package umu.tds.GestionGastos.cuentas;

import java.util.Map;
import java.util.List;

public class RepartoPorcentaje implements EstrategiaReparto {

    // Pporcentaje de gasto que debe asumir cada persona
     private Map<Persona, Double> porcentajes;

    public RepartoPorcentaje(Map<Persona, Double> porcentajes) {

        // Comprobar que la suma de porcentajes sea 100
        double total = porcentajes.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Si no suma 100, el reparto no es válido
        if (total != 100) {
            throw new IllegalArgumentException("Los porcentajes deben sumar 100");
        }

        this.porcentajes = porcentajes;
    }

    @Override
    public void repartir(GastoCompartido gasto, List<Persona> personas) {

          // Repartir el gasto según el porcentaje de cada persona
        for (Persona p : personas) {

            // Calcular la parte que corresponde a esta persona
            double porcentaje = porcentajes.get(p);
            
            double parte = gasto.getCantidad() * porcentaje / 100;

            // El pagador recupera lo pagado menos su parte correspondiente
            if (p.equals(gasto.getPagador())) {
            	
            	
                p.añadirSaldo(gasto.getCantidad() - parte);

            // El resto de personas deben su parte correspondiente
            } else {
                p.añadirSaldo(-parte);
            }
        }
    }
}