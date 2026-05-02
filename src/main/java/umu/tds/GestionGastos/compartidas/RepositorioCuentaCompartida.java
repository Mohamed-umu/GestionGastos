package umu.tds.GestionGastos.compartidas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepositorioCuentaCompartida {

    //instancia única del repositorio
    private static RepositorioCuentaCompartida instancia;

    // Mapper de Jackson para convertir cuentas a JSON y JSON a cuentas
    private ObjectMapper mapper;

       // cConstructor privado para aplicar el patrón Singleton
    private RepositorioCuentaCompartida() {
    	
    	
        mapper = new ObjectMapper();

        // Registra módulos adicionales necesarios para Jackson
        mapper.findAndRegisterModules();
    }

    // Devuelve la única instancia del repositorio
    public static RepositorioCuentaCompartida getInstancia() {
    	
    	
        if (instancia == null) {
            instancia = new RepositorioCuentaCompartida();
        }
        return instancia;
    }

    // Guarda todas las cuentas compartidas en un fichero JSON
    public void guardarTodas(List<CuentaCompartida> cuentas, String fichero) {
    	
        try {
        	
        	
            mapper.writeValue(new File(fichero), cuentas);
            
            
        } catch (Exception e) {
           
        	
        	e.printStackTrace();
        }
    }

    // pa cargar todas las cuentas compartidas desde un fichero JSON
    public List<CuentaCompartida> cargarTodas(String fichero) {
        try {
            File file = new File(fichero);

            // Si el fichero aún no existe, se devuelve una lista vacía
            if (!file.exists()) {
                
            	return new ArrayList<>();
            }

            // Jackson lee el JSON como array y luego se convierte a ArrayList
            CuentaCompartida[] array = mapper.readValue(file, CuentaCompartida[].class);
            
            
            return new ArrayList<>(Arrays.asList(array));

        } catch (Exception e) {
        	
        	
            // Si el fichero es incompatible o está dañado, evitamos que falle la aplicación
            System.out.println("Archivo de cuentas compartidas incompatible. Se usará lista vacía.");
            return new ArrayList<>();
        }
    }

    // buscar una cuenta compartida por su nombre
    public CuentaCompartida buscarPorNombre(String nombre, String fichero) {
    	
        if (nombre == null) return null;

        // Cargar cuentas guardadas
        List<CuentaCompartida> cuentas = cargarTodas(fichero);

        // Comparar ignorando mayúsculas y minúsculas
        for (CuentaCompartida c : cuentas) {
        	
        	
            if (c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre)) {
            	
            	
                return c;
                
            }
        }

          //si no se encuentra ninguna cuenta entonces devuelve null
        return null;
    }
}