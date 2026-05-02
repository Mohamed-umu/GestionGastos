package umu.tds.GestionGastos.importador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import umu.tds.GestionGastos.modelo.Gasto;

public class ImportadorCSV implements Importador {

      // formato del fichero del profesor: 3/2/2026 10:11
    private final DateTimeFormatter formatoProfesor =
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);

    @Override
    public List<Gasto> importar(String fichero) {

          //lista donde se guardan los gastos importados correctamente
        List<Gasto> gastos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {

            String linea;

            // Saltar cabecera: Date,Account,Category,Subcategory,Note,Payer,Amount,Currency
            br.readLine();

            // Leer el fichero línea por línea
            while ((linea = br.readLine()) != null) {

                // Ignorar líneas vacías
                if (linea.trim().isEmpty()) {
                	
                    continue;
                }

                // sseparar la línea respetando posibles comillas en campos CSV
                String[] p = separarCSV(linea);

                 // El formato esperado tiene exactamente 8 campos
                if (p.length != 8) {
                    System.out.println("Línea CSV ignorada por número de campos (" + p.length + "): " + linea);
                    continue;
                }

                try {
                    // Convertir el texto de la fecha a LocalDateTime
                    LocalDateTime fecha = LocalDateTime.parse(p[0].trim(), formatoProfesor);

                    // Crear un gasto con los campos leídos del CSV
                    Gasto gasto = new Gasto(
                    		
                    		
                            fecha,
                            p[1].trim(), // Account
                            p[2].trim(), // Category
                            p[3].trim(), // Subcategory
                            p[4].trim(), // Note
                            p[5].trim(), // Payer
                            Double.parseDouble(p[6].trim().replace(",", ".")), // Amount
                            p[7].trim()  // Currency
                    );

                    // Añadir el gasto importado a la lista
                    gastos.add(gasto);

                } catch (Exception e) {
                	
                	
                    // Si una línea concreta falla, se ignora y se continúa con las demás
                    System.out.println("Línea CSV inválida: " + linea);
                    System.out.println("Motivo: " + e.getMessage());
                }
            }

        } catch (Exception e) {
        	
        	
            // Error general al abrir o leer el fichero
            e.printStackTrace();
        }

        // Devolver todos los gastos importados correctamente
        return gastos;
    }

    private String[] separarCSV(String linea) {
    	
    	
        // Lista temporal de campos encontrados
        List<String> partes = new ArrayList<>();

        // Campo que se está construyendo carácter a carácter
        StringBuilder actual = new StringBuilder();

        // Indica si estamos dentro de comillas dobles
        boolean dentroComillas = false;

        // Recorrer todos los caracteres de la línea
        for (int i = 0; i < linea.length(); i++) {
        	
        	
            char c = linea.charAt(i);

            // Al encontrar comillas, cambiamos el estado
            if (c == '"') {
                dentroComillas = !dentroComillas;

            // La coma separa campos solo si no estamos dentro de comillas
            } else if (c == ',' && !dentroComillas) {
            	
                partes.add(actual.toString());
                actual.setLength(0);

            // Cualquier otro carácter se añade al campo actual
            } else {
            	
                actual.append(c);
            }
        }

        // Añadir el último campo de la línea
        partes.add(actual.toString());

        // Convertir la lista a array
        return partes.toArray(new String[0]);
    }
}