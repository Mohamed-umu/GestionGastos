package umu.tds.GestionGastos.alertas;

import java.util.List;
import umu.tds.GestionGastos.modelo.Gasto;

public interface EstrategiaAlerta {

    // Comprueba si una alerta se cumple a partir de la lista de gastos
    boolean comprobar(List<Gasto> gastos);

    // Devuelve el mensaje que se mostrará cuando se supere el límite
    String getMensaje();

    // devuelve una descripción corta de la alerta configurada
    String getDescripcion();
}