package umu.tds.GestionGastos.importador;

public class FactoriaImportador {

    // para crear el importador adecuado según la extensión del fichero
     public static Importador crear(String fichero) {

        // Convertir a minúsculas para evitar problemas con .CSV o .TXT
        String f = fichero.toLowerCase();

           // Si el fichero es CSV, se usa el importador CSV
          if (f.endsWith(".csv")) {
        	  
            return new ImportadorCSV();
        }

         // si el fichero es TXT, se usa el importador TXT
        if (f.endsWith(".txt")) {
        	
            return new ImportadorTXT();
        }

        //si la extensión no está soportada, se lanza un error
        throw new IllegalArgumentException("Formato no soportado: " + fichero);
    }
}