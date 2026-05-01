package umu.tds.GestionGastos.cuentas;

import java.util.List;

public class RepartoEquitativo implements EstrategiaReparto {

    @Override
    public void repartir(GastoCompartido gasto, List<Persona> personas) {

        // paracalcular la parte que debe asumir cada persona
           double parte = gasto.getCantidad() / personas.size();

        //Recorrer todas las personas para actualizar sus saldos
        for (Persona p : personas) {

            // El pagador recupera lo pagado menos su propia parte
            if (p.equals(gasto.getPagador())) {
            	
            	
                p.añadirSaldo(gasto.getCantidad() - parte);

            // El resto de personas deben su parte del gasto
            } else {
            	
                p.añadirSaldo(-parte);
                
                
            }
        }
    }
}