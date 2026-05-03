package umu.tds.GestionGastos;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class AppController {

    // donde se cargan las distintas vistas de la aplicación
    @FXML private StackPane contenido;

    @FXML
    public void initialize() {
    	
        // Al iniciar la aplicación se muestra la pantalla principal
        cargarVista("principal.fxml");
    }

    @FXML
    public void irPrincipal() {
    	
        // Cambia a la vista principal de gestión de gastos
        cargarVista("principal.fxml");
    }

    @FXML
    public void irEstadisticas() {
    	
        // Cambia a la vista de estadísticas y gráficos
        cargarVista("estadisticas.fxml");
    }

    @FXML
    public void irAlertas() {
    	
        // Cambia a la vista de configuración de alertas
        cargarVista("alertas.fxml");
    }

    @FXML
    public void irCompartidas() {
    	
        // Cambia a la vista de cuentas compartidas
        cargarVista("cuenta_compartida.fxml");
    }

    private void cargarVista(String nombre) {
        try {
        	
            // Carga el fichero FXML indicado
            javafx.scene.Parent vista = javafx.fxml.FXMLLoader.load(
                    getClass().getResource(nombre)
            );

            // Suustituye el contenido actual por la nueva vista
            contenido.getChildren().setAll(vista);

        } catch (Exception e) {
        	
            // Muestra el error si la vista no se puede cargar
            e.printStackTrace();
        }
    }
}