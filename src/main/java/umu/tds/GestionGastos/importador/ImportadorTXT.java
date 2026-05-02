package umu.tds.GestionGastos.importador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import umu.tds.GestionGastos.modelo.Gasto;

public class ImportadorTXT implements Importador {

    // Formato del profesor: 8/5/2026 10:11
    private final DateTimeFormatter formatoProfesor =
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);

    @Override
    public List<Gasto> importar(String fichero) {

           // Lista donde se almacenan los gastos leídos correctamente
        List<Gasto> gastos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {

            String linea;

             // saltar la cabecera del fichero
            br.readLine();

            // Leer el fichero línea por línea
            while ((linea = br.readLine()) != null) {

                // Ignorar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Separar la línea según el separador detectado
                String[] p = separarLinea(linea);

                // El formato esperado tiene 8 campos
                if (p.length != 8) {
                    System.out.println("Línea TXT ignorada por número de campos (" + p.length + "): " + linea);
                    continue;
                }

                try {
                	
                	
                    // Convertir la fecha del texto a LocalDateTime
                    LocalDateTime fecha = LocalDateTime.parse(p[0].trim(), formatoProfesor);

                    // Crear el gasto a partir de los campos del fichero
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
                    System.out.println("Línea TXT inválida: " + linea);
                    System.out.println("Motivo: " + e.getMessage());
                }
            }

        } catch (Exception e) {
        	
        	
        	
            // Error general al abrir o leer el fichero
              e.printStackTrace();
        }

        // Devolver los gastos importados correctamente
        return gastos;
    }

    private String[] separarLinea(String linea) {

        // Si contiene punto y coma, se interpreta como TXT separado por ;
        if (linea.contains(";")) {
        	
        	
            return linea.split(";", -1);
        }

        // Si contiene tabulador, se interpreta como TXT separado por tabulaciones
        if (linea.contains("\t")) {
        	
            return linea.split("\t", -1);
        }

        // Si no contiene ; ni tabulador, se intenta leer como CSV separado por comas
        return separarCSV(linea);
    }

    private String[] separarCSV(String linea) {
    	
    	
    	
        // Lista temporal donde se guardan los campos encontrados
        List<String> partes = new ArrayList<>();

        // Campo actual que se está construyendo
        StringBuilder actual = new StringBuilder();

        // Indica si estamos dentro de comillas dobles
        boolean dentroComillas = false;

        // Recorrer todos los caracteres de la línea
        for (int i = 0; i < linea.length(); i++) {
        	
        	
            char c = linea.charAt(i);

            // Alternar estado cuando aparece una comilla
            if (c == '"') {
            	
                dentroComillas = !dentroComillas;

            // La coma separa campos solo si no estamos dentro de comillas
            } else if (c == ',' && !dentroComillas) {
            	
            	
                partes.add(actual.toString());
                actual.setLength(0);

            // Añadir cualquier otro carácter al campo actual
            } else {
            	
                actual.append(c);
            }
        }

        // Añadir el último campo
        partes.add(actual.toString());

        // Convertir la lista a array
        return partes.toArray(new String[0]);
    }
}