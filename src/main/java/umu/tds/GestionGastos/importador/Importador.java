package umu.tds.GestionGastos.importador;

import java.util.List;
import umu.tds.GestionGastos.modelo.Gasto;

public interface Importador {
    List<Gasto> importar(String fichero);
}