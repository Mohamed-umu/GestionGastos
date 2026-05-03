package umu.tds.GestionGastos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Carga el fichero principal de la interfaz gráfica
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app.fxml"));

        
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        // Carga la hoja de estilos CSS de la aplicación
        scene.getStylesheets().add(
                App.class.getResource("/umu/tds/GestionGastos/estilos.css").toExternalForm()
        );

        
        stage.setTitle("Gestión de Gastos");

        // Carga el logo de la aplicación desde la carpeta de recursos
        var logoStream = App.class.getResourceAsStream(
                "/umu/tds/GestionGastos/imagenes/logo.png"
        );

        // Si el logo existe, se añade como icono de la ventana
        if (logoStream != null) {
            stage.getIcons().add(new Image(logoStream));
        } else {
            System.out.println("No se encontró el logo");
        }

        // Asigna la escena a la ventana principal
        stage.setScene(scene);

        
        stage.show();
    }

    public static void main(String[] args) {
       
    	
        launch();
    }
}