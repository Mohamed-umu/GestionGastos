package umu.tds.GestionGastos.alertas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepositorioHistorialAlertas {

    // Instancia única del repositorio
    private static RepositorioHistorialAlertas instancia;

    // Mapper de Jackson para guardar y cargar JSON
    private ObjectMapper mapper;

    // Constr privado para aplicar el patrón Singleton
    private RepositorioHistorialAlertas() {
        mapper = new ObjectMapper();

        // rregistrar módulos adicionales, por ejemplo para LocalDate
        mapper.findAndRegisterModules();
    }

    // Devuelve la única instancia del repositorio
    public static RepositorioHistorialAlertas getInstancia() {
    	
    	
        if (instancia == null) {
        	
            instancia = new RepositorioHistorialAlertas();
        }
        
        
        return instancia;
    }

    // Guarda el historial de notificaciones en un fichero JSON
    public void guardar(List<Notificacion> historial, String fichero) {
    	
    	
        try {
        	
        	
            mapper.writeValue(new File(fichero), historial);
        } catch (Exception e) {
        	
            e.printStackTrace();
        }
    }

    // Carga el historial de notificaciones desde un fichero JSON
    public List<Notificacion> cargar(String fichero) {
    	
        try {
        	
        	
            File file = new File(fichero);

            // Si el fichero todavía no existe devuelve una lista vacía
            if (!file.exists()) {
            	
            	
                return new ArrayList<>();
            }

            // Jackson lee el JSON como array y luego lo convertimos a ArrayList
            Notificacion[] array = mapper.readValue(file, Notificacion[].class);
            return new ArrayList<>(Arrays.asList(array));

        } catch (Exception e) {
            e.printStackTrace();

            // Si ocurre un error al cargar, se devuelve lista vacía
            return new ArrayList<>();
        }
    }
}