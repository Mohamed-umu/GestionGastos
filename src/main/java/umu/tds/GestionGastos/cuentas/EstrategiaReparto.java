package umu.tds.GestionGastos.cuentas;

import java.util.List;

public interface EstrategiaReparto {

	//reparte un gasto compartido entre las personas de la cuenta
    void repartir(GastoCompartido gasto, List<Persona> personas);
}