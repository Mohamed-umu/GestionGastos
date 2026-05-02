package umu.tds.GestionGastos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import umu.tds.GestionGastos.alertas.*;
import umu.tds.GestionGastos.controlador.GestorGastos;

import java.util.ArrayList;
import java.util.List;

public class AlertasController {

    // Campos del formulario para crear una nueva alerta
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField campoLimite;
    @FXML private ComboBox<String> campoCategoria;

    // Tabla donde se muestran las alertas activas
    @FXML private TableView<AlertaFila> tablaAlertas;
    @FXML private TableColumn<AlertaFila, String> colTipo;
    @FXML private TableColumn<AlertaFila, String> colCategoria;
    @FXML private TableColumn<AlertaFila, Double> colLimite;

    // Tabla donde se muestra el historial de notificaciones generadas
    @FXML private TableView<Notificacion> tablaHistorial;
    @FXML private TableColumn<Notificacion, String> colFechaHistorial;
    @FXML private TableColumn<Notificacion, String> colMensajeHistorial;

    /*
     * Este gestor es compartido entre varias ventanas, las alertas se crean y eliminan desde AlertasController, pero se comprueban 
     * desde PrincipalController cuando el usuario añade un gasto nuevo
     */
    private static GestorAlertas gestorAlertasCompartido = new GestorAlertas();

    // Gestor de gastos usado para comprobar alertas sobre los gastos actuales
    private GestorGastos gestor = GestorGastos.getInstancia();

    // Repositorios encargados de guardar y cargar las alertas y el historial
    private RepositorioAlertas repo = RepositorioAlertas.getInstancia();
    private RepositorioHistorialAlertas repoHistorial = RepositorioHistorialAlertas.getInstancia();

    // Ficheros JSON donde se persisten las alertas y el historial
    private final String RUTA_ALERTAS = System.getProperty("user.dir") + "/alertas.json";
    private final String RUTA_HISTORIAL = System.getProperty("user.dir") + "/historial_alertas.json";

    // Permite que otros controladores usen el mismo gestor de alertas
    public static GestorAlertas getGestorAlertasCompartido() {
        return gestorAlertasCompartido;
    }

    @FXML
    public void initialize() {
        configurarTablas();

        // Cargar los tipos de alerta disponibles
        comboTipo.getItems().clear();
        comboTipo.getItems().addAll("Mensual", "Semanal");

        cargarCategorias();

        // Recuperar datos persistidos al abrir la ventana
        cargarAlertasDesdeJSON();
        cargarHistorialDesdeJSON();

        refrescarAlertas();
        refrescarHistorial();
    }

    private void configurarTablas() {
        // Asociar cada columna de alertas con su atributo correspondiente
        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipo()));

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategoria()));

        colLimite.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getLimite()));

        // Asociar cada columna del historial con su atributo correspondiente
        colFechaHistorial.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha().toString()));

        colMensajeHistorial.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMensaje()));
    }

    private void cargarCategorias() {
        campoCategoria.getItems().clear();

        // Categorías predefinidas mostradas en el desplegable
        campoCategoria.getItems().addAll(
                "Alimentación",
                "Transporte",
                "Entretenimiento",
                "Comida",
                "Ocio",
                "Salud",
                "Compras",
                "Gasolina"
        );
    }

    @FXML
    public void crearAlerta() {
        try {
            String tipo = comboTipo.getValue();

            // Comprobar que se ha seleccionado el tipo de alerta
            if (tipo == null || tipo.isBlank()) {
                mostrarError("Selecciona un tipo de alerta");
                return;
            }

            // Comprobar que el usuario ha introducido un límite
            if (campoLimite.getText() == null || campoLimite.getText().isBlank()) {
                mostrarError("Introduce un límite");
                return;
            }

            double limite = Double.parseDouble(campoLimite.getText());

            // La categoría es opcional; si está vacía se aplica a todas
            String categoria = obtenerTextoCombo(campoCategoria);

            // El límite debe ser un valor positivo
            if (limite <= 0) {
                mostrarError("El límite debe ser mayor que 0");
                return;
            }

            EstrategiaAlerta alerta;

            /*
             * Aquí se aplica el patrón Estrategia
             * Según lo que elija el usuario, se crea una estrategia distinta:
             * 
             * uuna para comprobar el gasto mensual y otra para comprobar el gasto semanal
             */
            if (tipo.equals("Mensual")) {
                alerta = new AlertaMensual(limite, categoria);
            } else {
                alerta = new AlertaSemanal(limite, categoria);
            }

            // Añadir la alerta al gestor compartido
            gestorAlertasCompartido.añadirAlerta(alerta);

            // Guardar la alerta en JSON para mantener la persistencia
            guardarAlertasEnJSON();

            // Comprobar si la alerta ya se cumple con los gastos existentes
            gestorAlertasCompartido.comprobarAlertas(gestor.getGastos());

            // Guardar posibles nuevas notificaciones generadas
            repoHistorial.guardar(gestorAlertasCompartido.getHistorial(), RUTA_HISTORIAL);

            // Actualizar las tablas de la interfaz
            refrescarAlertas();
            refrescarHistorial();

            // Limpiar el formulario después de crear la alerta
            campoLimite.clear();
            campoCategoria.setValue(null);
            campoCategoria.getEditor().clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alerta creada");
            alert.setHeaderText(null);
            alert.setContentText("Alerta creada correctamente");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Datos de alerta incorrectos");
        }
    }

    @FXML
    public void limpiarHistorial() {
        // Vaciar el historial en memoria
        gestorAlertasCompartido.getHistorial().clear();

        // Guardar el historial vacío en JSON
        repoHistorial.guardar(gestorAlertasCompartido.getHistorial(), RUTA_HISTORIAL);

        refrescarHistorial();
    }

    @FXML
    public void eliminarAlerta() {
        int index = tablaAlertas.getSelectionModel().getSelectedIndex();

        // Validar que haya una alerta seleccionada
        if (index < 0) {
            mostrarError("Selecciona una alerta");
            return;
        }

        // Eliminar la alerta seleccionada
        gestorAlertasCompartido.eliminarAlerta(index);

        // Guardar cambios y refrescar la tabla
        guardarAlertasEnJSON();
        refrescarAlertas();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alerta eliminada");
        alert.setHeaderText(null);
        alert.setContentText("Alerta eliminada correctamente");
        alert.showAndWait();
    }

    private void refrescarAlertas() {
        List<AlertaFila> filas = new ArrayList<>();

        // Convertir las alertas del modelo en filas visibles para la tabla
        for (EstrategiaAlerta alerta : gestorAlertasCompartido.getAlertas()) {

            if (alerta instanceof AlertaMensual a) {
                filas.add(new AlertaFila(
                        "Mensual",
                        categoriaVisible(a.getCategoria()),
                        a.getLimite()
                ));
            } else if (alerta instanceof AlertaSemanal a) {
                filas.add(new AlertaFila(
                        "Semanal",
                        categoriaVisible(a.getCategoria()),
                        a.getLimite()
                ));
            }
        }

        tablaAlertas.setItems(FXCollections.observableArrayList(filas));
    }

    private void refrescarHistorial() {
        // Mostrar en la tabla el historial actual
        tablaHistorial.setItems(
                FXCollections.observableArrayList(gestorAlertasCompartido.getHistorial())
        );

        // Guardar el historial para que se conserve al cerrar la aplicación
        repoHistorial.guardar(gestorAlertasCompartido.getHistorial(), RUTA_HISTORIAL);
    }

    private void guardarAlertasEnJSON() {
        List<AlertaConfig> configs = new ArrayList<>();

        /*
         * Las alertas reales son objetos de tipo AlertaMensual o AlertaSemanal.
         * Para guardarlas fácilmente en JSON se convierten a AlertaConfig, q almacena solo los datos necesarios: tipo, límite y categoría
         */
        for (EstrategiaAlerta alerta : gestorAlertasCompartido.getAlertas()) {
            if (alerta instanceof AlertaMensual a) {
                configs.add(new AlertaConfig("Mensual", a.getLimite(), a.getCategoria()));
            } else if (alerta instanceof AlertaSemanal a) {
                configs.add(new AlertaConfig("Semanal", a.getLimite(), a.getCategoria()));
            }
        }

        repo.guardar(configs, RUTA_ALERTAS);
    }

    private void cargarAlertasDesdeJSON() {
        // Limpiar antes de cargar para evitar alertas duplicadas
        gestorAlertasCompartido.limpiarAlertas();

        List<AlertaConfig> configs = repo.cargar(RUTA_ALERTAS);

        // Reconstruir las estrategias de alerta desde la configuración guardada
        for (AlertaConfig c : configs) {
            if ("Mensual".equals(c.getTipo())) {
                gestorAlertasCompartido.añadirAlerta(
                        new AlertaMensual(c.getLimite(), c.getCategoria())
                );
            } else if ("Semanal".equals(c.getTipo())) {
                gestorAlertasCompartido.añadirAlerta(
                        new AlertaSemanal(c.getLimite(), c.getCategoria())
                );
            }
        }
    }

    private void cargarHistorialDesdeJSON() {
        // Recuperar el historial guardado en ejecuciones anteriores
        gestorAlertasCompartido.setHistorial(repoHistorial.cargar(RUTA_HISTORIAL));
    }

    @FXML
    public void refrescarHistorialVista() {
        // Recargar historial desde fichero y actualizar la tabla
        cargarHistorialDesdeJSON();
        refrescarHistorial();
    }

    private String obtenerTextoCombo(ComboBox<String> combo) {
        if (combo == null) {
            return "";
        }

        // Si el ComboBox es editable, se prioriza el texto escrito por el usuario
        if (combo.isEditable()) {
            String texto = combo.getEditor().getText();

            if (texto != null && !texto.isBlank()) {
                return texto.trim();
            }
        }

        // Si no hay texto escrito, se usa el valor seleccionado
        String valor = combo.getValue();

        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }

        return "";
    }

    private String categoriaVisible(String categoria) {
        // Si no hay categoría concreta, la alerta se muestra como aplicable a todas
        if (categoria == null || categoria.isBlank()) {
            return "Todas";
        }

        return categoria;
    }

    private void mostrarError(String mensaje) {
        // Mostrar un diálogo de error reutilizable
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}