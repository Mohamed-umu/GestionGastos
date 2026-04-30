package umu.tds.GestionGastos.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import umu.tds.GestionGastos.modelo.Gasto;

import java.io.File;
import java.util.List;

public class RepositorioGastos {

    // iInstancia única del repositorio
      private static RepositorioGastos instancia;

      // Mapper de Jackson para convertir gastos a JSON y JSON a gastos
    private ObjectMapper mapper;

    // Constructo privado para aplicar el patrón Singleton
    private RepositorioGastos() {
    	
    	
        mapper = new ObjectMapper();

        // Registra módulos necesarios, por ejemplo para LocalDateTime
        mapper.findAndRegisterModules();
    }

    // Devuelve la única instancia del repositorio
    public static RepositorioGastos getInstancia() {
    	
    	
        if (instancia == null) {
        	
            instancia = new RepositorioGastos();
        }
        return instancia;
    }

    // Guarda la lista de gastos en un fichero JSON
    public void guardar(List<Gasto> gastos, String fichero) {
        try {
            mapper.writeValue(new File(fichero), gastos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // para cargar la lista de gastos desde un fichero JSON
    public List<Gasto> cargar(String fichero) {
    	
    	
        try {
        	
            File file = new File(fichero);

            // Si el fichero no existe todavía, se devuelve una lista vacía
            if (!file.exists()) {
            	
            	
                System.out.println("NO EXISTE EL FICHERO");
                return new java.util.ArrayList<>();
            }

            // Jackson lee el JSON como array de gastos
            Gasto[] array = mapper.readValue(file, Gasto[].class);

            // Convertir el array a una lista modificable
            return new java.util.ArrayList<>(java.util.Arrays.asList(array));

        } catch (Exception e) {
        	
            e.printStackTrace();

            // Si hay error al leer, devolvemos lista vacía para no romper la aplicación
            return new java.util.ArrayList<>();
        }
    }
}