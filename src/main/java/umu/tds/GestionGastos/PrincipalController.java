package umu.tds.GestionGastos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import umu.tds.GestionGastos.alertas.GestorAlertas;
import umu.tds.GestionGastos.alertas.Notificacion;
import umu.tds.GestionGastos.alertas.RepositorioHistorialAlertas;
import umu.tds.GestionGastos.compartidas.CuentaCompartida;
import umu.tds.GestionGastos.compartidas.RepositorioCuentaCompartida;
import umu.tds.GestionGastos.controlador.GestorGastos;
import umu.tds.GestionGastos.modelo.Gasto;
import umu.tds.GestionGastos.persistencia.RepositorioGastos;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrincipalController {

    // Campos del formulario para añadir un gasto manual
    @FXML private DatePicker campoFecha;
    @FXML private ComboBox<String> campoCuenta;
    @FXML private ComboBox<String> campoCategoria;
    @FXML private TextField campoSubcategoria;
    @FXML private TextField campoDescripcion;
    @FXML private ComboBox<String> campoPagador;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoMoneda;

    // Tabla de gastos guardados en la aplicación
    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, String> colFecha;
    @FXML private TableColumn<Gasto, String> colCuenta;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colSubcategoria;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colPagador;
    @FXML private TableColumn<Gasto, Double> colCantidad;
    @FXML private TableColumn<Gasto, String> colMoneda;

    // Tabla de gastos importados pendientes de guardar
    @FXML private TableView<Gasto> tablaImportados;
    @FXML private TableColumn<Gasto, String> colFechaImp;
    @FXML private TableColumn<Gasto, String> colCuentaImp;
    @FXML private TableColumn<Gasto, String> colCategoriaImp;
    @FXML private TableColumn<Gasto, String> colSubcategoriaImp;
    @FXML private TableColumn<Gasto, String> colDescripcionImp;
    @FXML private TableColumn<Gasto, String> colPagadorImp;
    @FXML private TableColumn<Gasto, Double> colCantidadImp;
    @FXML private TableColumn<Gasto, String> colMonedaImp;

    // Gestor principal de gastos
    private final GestorGastos gestor = GestorGastos.getInstancia();

    // Repositorio encargado de guardar y cargar los gastos
    private final RepositorioGastos repo = RepositorioGastos.getInstancia();

    // gestor de alertas ccompartido con AlertasController
    private final GestorAlertas gestorAlertas = AlertasController.getGestorAlertasCompartido();

    // Repositorio del historial de alertas
    private final RepositorioHistorialAlertas repoHistorial = RepositorioHistorialAlertas.getInstancia();

    // rRepositorio de cuentas compartidas
    private final RepositorioCuentaCompartida repoCuentaCompartida =
            RepositorioCuentaCompartida.getInstancia();

    // Rutas de los ficheros JSON usados por la aplicación
    private final String RUTA = System.getProperty("user.dir") + "/gastos.json";
    private final String RUTA_HISTORIAL = System.getProperty("user.dir") + "/historial_alertas.json";
    private final String RUTA_CUENTA_COMPARTIDA =
            System.getProperty("user.dir") + "/cuentas_compartidas.json";

    // Formato igual al fichero del profesor: 3/2/2022 10:11
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    // Listas observables usadas por las tablas de JavaFX
    private final ObservableList<Gasto> datosGuardados = FXCollections.observableArrayList();
    private final ObservableList<Gasto> datosImportados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Fecha por defecto: fecha actual
        campoFecha.setValue(java.time.LocalDate.now());

        // Categorías predefinidas de la aplicación
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

        // Cargar cuentas disponibles, incluyendo cuentas compartidas
        cargarCuentasDisponibles();

        // Al cambiar la cuenta seleccionada, se actualizan los posibles pagadores
        campoCuenta.valueProperty().addListener((obs, oldValue, newValue) -> {
            actualizarPagadoresSegunCuenta();
        });

        // también se actualiza si el usuario escribe manualmente el nombre de la cuenta
        campoCuenta.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            actualizarPagadoresSegunCuenta();
        });

        // Configurar tablas y cargar datos guardados
        configurarTablaGuardados();
        configurarTablaImportados();
        refrescar();
    }

    private void configurarTablaGuardados() {
    	
    	
    	
    	
        // La tabla principal permite edición
        tablaGastos.setEditable(true);

        // Para que se vea fecha + hora completa
        colFecha.setPrefWidth(140);

        // Mostrar la fecha formateada
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha().format(formatoFecha)));

        // Configurar columnas de texto editables
        
        configurarColumnaTexto(colCuenta, Gasto::getCuenta, Gasto::setCuenta, true);
        configurarColumnaTexto(colCategoria, Gasto::getCategoria, Gasto::setCategoria, true);
        configurarColumnaTexto(colSubcategoria, Gasto::getSubcategoria, Gasto::setSubcategoria, true);
        configurarColumnaTexto(colDescripcion, Gasto::getDescripcion, Gasto::setDescripcion, true);
        configurarColumnaTexto(colPagador, Gasto::getPagador, Gasto::setPagador, true);

        // Configurar columna numérica de cantidad
        colCantidad.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getCantidad()));

        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        colCantidad.setOnEditCommit(event -> {
            Gasto gasto = event.getRowValue();

            // No se permite modificar la cantidad de gastos compartidos para evitar descuadrar saldos
            if (esGastoDeCuentaCompartida(gasto)) {
               
            	mostrarError("No puedes modificar la cantidad de un gasto de cuenta compartida porque afecta a los saldos.");
                tablaGastos.refresh();
                
                return;
            }

            gasto.setCantidad(event.getNewValue());
            guardarGuardados();
      
        });

        configurarColumnaTexto(colMoneda, Gasto::getMoneda, Gasto::setMoneda, true);
    }

    private void configurarTablaImportados() {
       
    	// La tabla de importados también permite edición antes de guardar
        tablaImportados.setEditable(true);

        // Para que se vea fecha + hora completa
        colFechaImp.setPrefWidth(140);

        colFechaImp.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha().format(formatoFecha)));

        // En importados no se guarda automáticamente hasta pulsar "Guardar importados"
        configurarColumnaTexto(colCuentaImp, Gasto::getCuenta, Gasto::setCuenta, false);
        configurarColumnaTexto(colCategoriaImp, Gasto::getCategoria, Gasto::setCategoria, false);
        configurarColumnaTexto(colSubcategoriaImp, Gasto::getSubcategoria, Gasto::setSubcategoria, false);
        configurarColumnaTexto(colDescripcionImp, Gasto::getDescripcion, Gasto::setDescripcion, false);
        configurarColumnaTexto(colPagadorImp, Gasto::getPagador, Gasto::setPagador, false);

       
        colCantidadImp.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getCantidad()));

        colCantidadImp.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        colCantidadImp.setOnEditCommit(event ->
                event.getRowValue().setCantidad(event.getNewValue())
        );

        configurarColumnaTexto(colMonedaImp, Gasto::getMoneda, Gasto::setMoneda, false);
    }

    
    
    // Interfaz auxiliar para obtener texto de un gasto
    private interface Getter {
        String get(Gasto g);
    }

    // Interfaz auxiliar para modificar texto de un gasto
    private interface Setter {
        void set(Gasto g, String valor);
    }

    private void configurarColumnaTexto(TableColumn<Gasto, String> columna,
                                        Getter getter,
                                        Setter setter,
                                        boolean guardarAutomatico) {

      
    	// Indicar qué texto debe mostrar la columna
        columna.setCellValueFactory(data ->
                new SimpleStringProperty(getter.get(data.getValue())));

        // Permitir edición como campo de texto
        columna.setCellFactory(TextFieldTableCell.forTableColumn());

        columna.setOnEditCommit(event -> {
            Gasto gasto = event.getRowValue();

            /*
             * En gastos compartidos se bloquea la edición de cuenta y pagador, ya q
             * estos dos campos afectan directamente a los saldos de la cuenta compartida
             */
            if (guardarAutomatico &&
                    esGastoDeCuentaCompartida(gasto) &&
                    (columna == colCuenta || columna == colPagador)) {

                mostrarError("No puedes modificar la cuenta o el pagador de un gasto compartido porque afecta a los saldos.");
                tablaGastos.refresh();
                return;
            }

            setter.set(gasto, event.getNewValue());

            // Si es un gasto ya guardado, se persiste automáticamente
            if (guardarAutomatico) {
                guardarGuardados();
            }
        });
    }

    public void refrescar() {
    	
    	
        // Recargar cuentas por si se han creado nuevas cuentas compartidas
        cargarCuentasDisponibles();

        // Limpiar datos actuales en memoria y en la tabla
        gestor.getGastos().clear();
        datosGuardados.clear();

        // Cargar gastos desde JSON
        List<Gasto> lista = repo.cargar(RUTA);

        // ordenar gastos de más reciente a más antiguo
        lista.sort((g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));

        // añadir los gastos al gestor y a la lista observable
        for (Gasto g : lista) {
            gestor.añadirGasto(g);
            datosGuardados.add(g);
        }

        // asignar listas a las tablas
        tablaGastos.setItems(datosGuardados);
        tablaImportados.setItems(datosImportados);
    }

    @FXML
    public void añadirGasto() {
        try {
            // validar cantidad
            if (campoCantidad.getText().isEmpty()) {
                throw new Exception();
            }

            double cantidad = Double.parseDouble(campoCantidad.getText());

            // Obtener valores de los campos editables
            String cuentaTexto = obtenerTextoCombo(campoCuenta);
            String categoriaTexto = obtenerTextoCombo(campoCategoria);
            String pagadorTexto = obtenerTextoCombo(campoPagador);

            // Validar campos obligatorios
            if (campoFecha.getValue() == null ||
                    cuentaTexto == null || cuentaTexto.isBlank() ||
                    categoriaTexto == null || categoriaTexto.isBlank() ||
                    pagadorTexto == null || pagadorTexto.isBlank()) {
                throw new Exception();
            }

            // Crear gasto usando la fecha seleccionada y la hora actual
            Gasto gasto = new Gasto(
                    campoFecha.getValue().atTime(LocalTime.now().withSecond(0).withNano(0)),
                    cuentaTexto,
                    categoriaTexto,
                    campoSubcategoria.getText(),
                    campoDescripcion.getText(),
                    pagadorTexto,
                    cantidad,
                    campoMoneda.getText()
            );

            // Si pertenece a una cuenta compartida, se actualizan sus saldos
            actualizarCuentaCompartidaSiHaceFalta(gasto);

            // Añadir gasto al gestor y a la tabla
            gestor.añadirGasto(gasto);
            datosGuardados.add(gasto);

            // Ordenar datos después de añadir
            datosGuardados.sort((g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));
            gestor.getGastos().sort((g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));

            // Guardar gastos y comprobar alertas
            guardarGuardados();
            comprobarAlertas();

            // Limpiar formulario
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Datos incorrectos. Revisa fecha, cuenta, categoría, pagador y cantidad.");
            alert.showAndWait();
        }
    }

    @FXML
    public void importarArchivo() {
    	
    	
        try {
            // Abrir selector de archivos
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Seleccionar fichero de gastos");

            // Formatos admitidos
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Ficheros CSV", "*.csv"),
                    new javafx.stage.FileChooser.ExtensionFilter("Ficheros TXT", "*.txt"),
                    new javafx.stage.FileChooser.ExtensionFilter("Todos los ficheros", "*.*")
            );

            java.io.File archivo = fileChooser.showOpenDialog(null);

            // Si el usuario cancela, no se hace nada
            if (archivo == null) {
                return;
            }

            // La factoría decide qué importador usar según el formato del archivo
            var imp = umu.tds.GestionGastos.importador.FactoriaImportador.crear(
                    archivo.getAbsolutePath()
            );

            List<Gasto> importados = imp.importar(archivo.getAbsolutePath());

            datosImportados.clear();

            // Añadir solo gastos que no estén ya guardados
            for (Gasto g : importados) {
                boolean yaExiste = datosGuardados.stream().anyMatch(x -> mismoGasto(x, g));

                if (!yaExiste) {
                    datosImportados.add(g);
                }
            }

            tablaImportados.setItems(datosImportados);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Importación");
            alert.setHeaderText(null);
            alert.setContentText("Archivo cargado. Gastos pendientes: " + datosImportados.size());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No se pudo importar el fichero");
            alert.showAndWait();
        }
    }

    @FXML
    public void guardarImportados() {
        if (datosImportados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No hay gastos importados pendientes");
            alert.showAndWait();
            return;
        }

        int guardados = 0;

        // Pasar los gastos importados a la lista de gastos guardados
        for (Gasto g : datosImportados) {
            boolean yaExiste = gestor.getGastos().stream().anyMatch(x -> mismoGasto(x, g));

            if (!yaExiste) {
                // Si el gasto importado pertenece a una cuenta compartida, se actualizan saldos
                actualizarCuentaCompartidaSiHaceFalta(g);

                gestor.añadirGasto(g);
                datosGuardados.add(g);
                guardados++;
            }
        }

        // Limpiar tabla de importados
        datosImportados.clear();

        // Ordenar gastos guardados
        gestor.getGastos().sort((g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));
        datosGuardados.sort((g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));

        // Guardar y comprobar alertas
        guardarGuardados();
        comprobarAlertas();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guardado");
        alert.setHeaderText(null);
        alert.setContentText("Gastos importados guardados: " + guardados);
        alert.showAndWait();
    }

    @FXML
    public void eliminarGastoGuardado() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();

        // Validar que haya un gasto seleccionado
        if (seleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Selecciona un gasto guardado");
            alert.showAndWait();
            return;
        }

        // Eliminar de memoria y de la tabla
        gestor.getGastos().remove(seleccionado);
        datosGuardados.remove(seleccionado);

        // Guardar gastos restantes
        guardarGuardados();

         // Recalcular saldos de cuentas compartidas tras eliminar un gasto
        recalcularTodasLasCuentasCompartidas();
    }

    @FXML
    public void eliminarGastoImportado() {
        Gasto seleccionado = tablaImportados.getSelectionModel().getSelectedItem();

         // Validar que haya un gasto importado seleccionado
        if (seleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Selecciona un gasto importado");
            alert.showAndWait();
            return;
        }

         // Eliminar solo de la tabla de importados
        datosImportados.remove(seleccionado);
    }

    private void guardarGuardados() {
       // Guardar la lista de gastos en JSON
        repo.guardar(gestor.getGastos(), RUTA);

       //    Refrescar tabla visualmente
        tablaGastos.refresh();
    }

    private void comprobarAlertas() {
    	
    	
    	
         // Guardamos el tamaño del historial antes de comprobar
        int historialAntes = gestorAlertas.getHistorial().size();

        // Comprobar alertas configuradas sobre los gastos actuales
        gestorAlertas.comprobarAlertas(gestor.getGastos());

        int historialDespues = gestorAlertas.getHistorial().size();

        // Guardar historial actualizado
        repoHistorial.guardar(gestorAlertas.getHistorial(), RUTA_HISTORIAL);

        // Solo mostrar popup si se ha generado una notificación nueva
        if (historialDespues > historialAntes) {

            Notificacion ultima = gestorAlertas
                    .getHistorial()
                    .get(gestorAlertas.getHistorial().size() - 1);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta");
            alert.setHeaderText(null);
            alert.setContentText(ultima.getMensaje());
            alert.showAndWait();
        }
    }

    @FXML
    public void exportarCSV() {
        try {
        	
        	
            // Abrir selector para elegir dónde guardar el CSV
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar fichero CSV");

            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
            );

            fileChooser.setInitialFileName("datos.csv");

            java.io.File file = fileChooser.showSaveDialog(null);

            // Si el usuario cancela, no se exporta
            if (file == null) {
                return;
            }

            // Añadir extensión .csv si el usuario no la escribe
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }

            java.io.PrintWriter writer = new java.io.PrintWriter(file);

            // Cabecera compatible con el formato externo del profesor
            writer.println("Date,Account,Category,Subcategory,Note,Payer,Amount,Currency");

            // Eescribir cada gasto en formato CSV
            for (Gasto g : gestor.getGastos()) {
                writer.println(
                        limpiarCSV(g.getFecha().format(formatoFecha)) + "," +
                        limpiarCSV(g.getCuenta()) + "," +
                        limpiarCSV(g.getCategoria()) + "," +
                        limpiarCSV(g.getSubcategoria()) + "," +
                        limpiarCSV(g.getDescripcion()) + "," +
                        limpiarCSV(g.getPagador()) + "," +
                        g.getCantidad() + "," +
                        limpiarCSV(g.getMoneda())
                );
            }

            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación");
            alert.setHeaderText(null);
            alert.setContentText("CSV exportado correctamente en:\n" + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo exportar el CSV");
            alert.showAndWait();
        }
    }

    private boolean mismoGasto(Gasto a, Gasto b) {
        // Comparar todos los campos para evitar duplicados al importar
        return a.getFecha().equals(b.getFecha())
                && textoIgual(a.getCuenta(), b.getCuenta())
                && textoIgual(a.getCategoria(), b.getCategoria())
                && textoIgual(a.getSubcategoria(), b.getSubcategoria())
                && textoIgual(a.getDescripcion(), b.getDescripcion())
                && textoIgual(a.getPagador(), b.getPagador())
                && a.getCantidad() == b.getCantidad()
                && textoIgual(a.getMoneda(), b.getMoneda());
    }

    private boolean textoIgual(String a, String b) {
        // Comparación segura de textos que pueden ser null
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    private void limpiarCampos() {
        // Restaurar formulario después de añadir un gasto
        campoFecha.setValue(java.time.LocalDate.now());

        campoCuenta.setValue("Personal");
        campoCuenta.getEditor().setText("Personal");

        campoCategoria.setValue(null);
        campoCategoria.getEditor().clear();

        campoSubcategoria.clear();
        campoDescripcion.clear();

        campoPagador.setEditable(true);
        campoPagador.setValue("Yo");
        campoPagador.getEditor().setText("Yo");

        campoCantidad.clear();

        campoMoneda.clear();
        campoMoneda.setText("EUR");

        actualizarPagadoresSegunCuenta();
    }

    private String limpiarCSV(String texto) {
    	
    	
    	
        // Evitar errores si el texto es null
        if (texto == null) return "";

        // Escapar comillas dobles
        texto = texto.replace("\"", "\"\"");

        // Si contiene coma o comillas, se encierra entre comillas
        if (texto.contains(",") || texto.contains("\"")) {
        	
        	
            return "\"" + texto + "\"";
        }

        return texto;
    }

    private void cargarCuentasDisponibles() {
        campoCuenta.getItems().clear();

        // Cuenta personal siempre disponible
        campoCuenta.getItems().add("Personal");

        // Añadir cuentas compartidas guardadas
        List<CuentaCompartida> cuentasCompartidas =
                repoCuentaCompartida.cargarTodas(RUTA_CUENTA_COMPARTIDA);

        for (CuentaCompartida c : cuentasCompartidas) {
        	
        	
            if (c.getNombre() != null && !c.getNombre().isBlank()) {
                campoCuenta.getItems().add(c.getNombre());
            }
        }

        campoCuenta.setValue("Personal");
        campoCuenta.getEditor().setText("Personal");

        actualizarPagadoresSegunCuenta();
    }

    private void actualizarPagadoresSegunCuenta() {
        String cuentaSeleccionada = obtenerTextoCombo(campoCuenta);

        campoPagador.getItems().clear();

        // Buscar si la cuenta seleccionada es una cuenta compartida
        CuentaCompartida cuentaCompartida =
                repoCuentaCompartida.buscarPorNombre(cuentaSeleccionada, RUTA_CUENTA_COMPARTIDA);

        if (cuentaCompartida != null) {

            // Si es compartida, solo pueden pagar las personas de esa cuenta
            campoPagador.setEditable(false);
            campoPagador.getItems().addAll(cuentaCompartida.getPersonas());

            if (!cuentaCompartida.getPersonas().isEmpty()) {
                campoPagador.setValue(cuentaCompartida.getPersonas().get(0));
            }

        } else {
            // Si no es compartida, se usa "Yo" como pagador por defecto
            campoPagador.setEditable(true);
            campoPagador.getItems().add("Yo");
            campoPagador.setValue("Yo");
            campoPagador.getEditor().setText("Yo");
        }
    }

    private String obtenerTextoCombo(ComboBox<String> combo) {
    	
    	
        if (combo == null) {
            return null;
        }

        // En ComboBox editable se prioriza el texto escrito por el usuario
        if (combo.isEditable()) {
            String texto = combo.getEditor().getText();

            if (texto != null && !texto.isBlank()) {
                return texto.trim();
            }
        }

        // Si no hay texto escrito, se toma el valor seleccionado
        String valor = combo.getValue();

        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }

        return null;
    }

    private void actualizarCuentaCompartidaSiHaceFalta(Gasto gasto) {
    	
    	
        List<CuentaCompartida> cuentas =
                repoCuentaCompartida.cargarTodas(RUTA_CUENTA_COMPARTIDA);

        // Buscar si el gasto pertenece a alguna cuenta compartida
        for (CuentaCompartida c : cuentas) {
            if (c.getNombre() != null &&
                    gasto.getCuenta() != null &&
                    c.getNombre().equalsIgnoreCase(gasto.getCuenta())) {

                try {
                    // Aplicar el gasto para actualizar saldos
                    c.aplicarGastoSiCorresponde(gasto);

                    // Guardar cuentas compartidas actualizadas
                    repoCuentaCompartida.guardarTodas(cuentas, RUTA_CUENTA_COMPARTIDA);
                    return;

                } catch (Exception e) {
                    throw new RuntimeException("El pagador no pertenece a la cuenta compartida", e);
                }
            }
        }
    }

    private boolean esGastoDeCuentaCompartida(Gasto gasto) {
    	
    	
        List<CuentaCompartida> cuentas =
                repoCuentaCompartida.cargarTodas(RUTA_CUENTA_COMPARTIDA);

        // Comprobar si la cuenta del gasto coincide con una cuenta compartida existente
        for (CuentaCompartida c : cuentas) {
        	
        	
        	
            if (c.getNombre() != null &&
                    gasto.getCuenta() != null &&
                    c.getNombre().equalsIgnoreCase(gasto.getCuenta())) {
                return true;
            }
        }

        return false;
    }

    private void mostrarError(String mensaje) {
    	
    	
        // Mostrar diálogo de error reutilizable
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void recalcularTodasLasCuentasCompartidas() {

        List<CuentaCompartida> cuentas =
                repoCuentaCompartida.cargarTodas(RUTA_CUENTA_COMPARTIDA);

        // Reiniciar saldos antes de recalcular
        for (CuentaCompartida cuenta : cuentas) {
            cuenta.reiniciarSaldos();
        }

        /*
         * Se recalculan todas las cuentas compartidas desde cero, ya q
         * cuando se elimina un gasto, los saldos deben quedar de nuevo coherentes con la lista real de gastos guardados
         */
        for (Gasto gasto : gestor.getGastos()) {

            for (CuentaCompartida cuenta : cuentas) {

                if (cuenta.getNombre() != null &&
                        gasto.getCuenta() != null &&
                        cuenta.getNombre().equalsIgnoreCase(gasto.getCuenta())) {

                    cuenta.aplicarGastoSiCorresponde(gasto);
                }
            }
        }

        // Guardar los saldos recalculados
        repoCuentaCompartida.guardarTodas(cuentas, RUTA_CUENTA_COMPARTIDA);
    }
}