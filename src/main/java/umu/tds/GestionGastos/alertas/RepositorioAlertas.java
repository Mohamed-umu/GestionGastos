package umu.tds.GestionGastos.alertas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RepositorioAlertas {

    // Instancia única del repositorio
    private static RepositorioAlertas instancia;

    // Mapper de Jackson para convertir objetos a JSON y JSON a objetos
    private ObjectMapper mapper;

    // constr privado para aplicar el patrón Singleton
    private RepositorioAlertas() {
        mapper = new ObjectMapper();

        // para registraar módulos necesarios, por ejemplo para fechas de Java
        mapper.findAndRegisterModules();
    }

    // Devuelve la única instancia del repositorio
    public static RepositorioAlertas getInstancia() {
    	
    	
        if (instancia == null) {
            instancia = new RepositorioAlertas();
        }
        return instancia;
    }

    // Guarda la lista de alertas en un fichero JSON
    public void guardar(List<AlertaConfig> alertas, String fichero) {
    	
        try {
            mapper.writeValue(new File(fichero), alertas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Carga la lista de alertas desde un fichero JSON
    public List<AlertaConfig> cargar(String fichero) {
    	
    	
    	
        try {
            File file = new File(fichero);

            // sii el fichero todavía no existe, se devuelve una lista vacía
            if (!file.exists()) {
                return List.of();
            }

            // Jackson carga el JSON como array y luego se convierte a lista
            AlertaConfig[] array = mapper.readValue(file, AlertaConfig[].class);
            return Arrays.asList(array);

        } catch (Exception e) {
        	
            e.printStackTrace();

            // En caso de error, se devuelve una lista vacía para evitar que falle la aplicación
            return List.of();
        }
    }
}