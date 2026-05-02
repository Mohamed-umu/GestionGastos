package umu.tds.GestionGastos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import umu.tds.GestionGastos.compartidas.CuentaCompartida;
import umu.tds.GestionGastos.compartidas.FilaSaldo;
import umu.tds.GestionGastos.compartidas.PersonaReparto;
import umu.tds.GestionGastos.compartidas.RepositorioCuentaCompartida;
import umu.tds.GestionGastos.modelo.Gasto;
import umu.tds.GestionGastos.persistencia.RepositorioGastos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuentaCompartidaController {

    // ComboBox para seleccionar una cuenta compartida ya existente
    @FXML private ComboBox<String> comboCuentas;

    // Campos de texto para crear una cuenta y añadir personas
    @FXML private TextField campoNombreCuenta;
    @FXML private TextField campoPersona;

    // Tabla donde se muestran las personas de la cuenta y sus porcentajes
    @FXML private TableView<PersonaReparto> tablaPersonas;
    @FXML private TableColumn<PersonaReparto, String> colNombrePersona;
    @FXML private TableColumn<PersonaReparto, Double> colPorcentajePersona;

    // Etiqueta que indica qué cuenta está seleccionada actualmente
    @FXML private Label labelCuenta;

    // Tabla donde se muestran los saldos de cada persona
    @FXML private TableView<FilaSaldo> tablaSaldos;
    @FXML private TableColumn<FilaSaldo, String> colPersona;
    @FXML private TableColumn<FilaSaldo, Double> colSaldo;

    // Cuenta que se está visualizando o editando en este momento
    private CuentaCompartida cuentaActual;

    // Lista observable usada por la tabla de personas
    private final ObservableList<PersonaReparto> personasVista =
            FXCollections.observableArrayList();

    // Repositorio de cuentas compartidas
    private final RepositorioCuentaCompartida repo =
            RepositorioCuentaCompartida.getInstancia();

    // Repositorio de gastos, usado para comprobar si una cuenta tiene gastos asociados
    private final RepositorioGastos repoGastos =
            RepositorioGastos.getInstancia();

    // Ruta del fichero JSON donde se guardan las cuentas compartidas
    private final String RUTA =
            System.getProperty("user.dir") + "/cuentas_compartidas.json";

    // Ruta del fichero JSON donde se guardan los gastos
    private final String RUTA_GASTOS =
            System.getProperty("user.dir") + "/gastos.json";

    @FXML
    public void initialize() {
        // Configurar las tablas al cargar la ventana
        configurarTablaPersonas();
        configurarTablaSaldos();

        // aasociar la lista observable a la tabla de personas
        tablaPersonas.setItems(personasVista);

        // Cargar las cuentas existentes y preparar una nueva cuenta vacía
        cargarComboCuentas();
        nuevaCuenta();
    }

    private void configurarTablaPersonas() {
        // Permitir edición en la tabla de personas
        tablaPersonas.setEditable(true);

        // Columna del nombre de la persona
        colNombrePersona.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colNombrePersona.setCellFactory(TextFieldTableCell.forTableColumn());

        colNombrePersona.setOnEditCommit(event -> {
        	
            /*
             * La lista de personas solo puede modificarse antes de crear la cuenta, 
             * una vez creada la cuenta compartida, la lista de personas no podrá ser modificada
             */
            if (cuentaActual != null) {
                mostrarError("Una vez creada la cuenta, la lista de personas no puede modificarse");
                tablaPersonas.refresh();
                return;
            }

            event.getRowValue().setNombre(event.getNewValue());
        });

        // Columna del porcentaje asignado a cada persona
        colPorcentajePersona.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPorcentaje()));

        colPorcentajePersona.setCellFactory(
                TextFieldTableCell.forTableColumn(new DoubleStringConverter())
        );

        colPorcentajePersona.setOnEditCommit(event -> {
        	
            // El porcentaje sí se puede modificar para configurar el reparto
            event.getRowValue().setPorcentaje(event.getNewValue());
        });
    }

    private void configurarTablaSaldos() {
        // Columna del nombre de la persona
        colPersona.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPersona()));

        // Columna del saldo, redondeado a dos decimales
        colSaldo.setCellValueFactory(data ->
                new SimpleObjectProperty<>(redondear(data.getValue().getSaldo())));
    }

    private void cargarComboCuentas() {
    	
        // Limpiar el desplegable antes de volver a cargarlo
        comboCuentas.getItems().clear();

        // Cargar todas las cuentas compartidas guardadas
        List<CuentaCompartida> cuentas = repo.cargarTodas(RUTA);

        // Añadir al desplegable solo las cuentas con nombre válido
        for (CuentaCompartida c : cuentas) {
            if (c.getNombre() != null && !c.getNombre().isBlank()) {
                comboCuentas.getItems().add(c.getNombre());
            }
        }
    }

    @FXML
    public void seleccionarCuenta() {
        String nombre = comboCuentas.getValue();

        // Si no hay cuenta seleccionada, no se hace nada
        if (nombre == null || nombre.isBlank()) {
            return;
        }

        // Buscar la cuenta seleccionada en el repositorio
        cuentaActual = repo.buscarPorNombre(nombre, RUTA);

        // Mostrar la cuenta en la interfaz
        if (cuentaActual != null) {
            cargarCuentaEnVista();
        }
    }

    @FXML
    public void nuevaCuenta() {
        // Se borra la cuenta actual para indicar que se está creando una nueva
        cuentaActual = null;

        // Limpiar campos de texto
        campoNombreCuenta.clear();
        campoPersona.clear();

        // Limpiar tablas
        personasVista.clear();
        tablaSaldos.getItems().clear();

        // Actualizar etiqueta informativa
        labelCuenta.setText("Cuenta actual: nueva cuenta sin guardar");

        // Quitar selección del ComboBox
        comboCuentas.getSelectionModel().clearSelection();
    }

    @FXML
    public void añadirPersona() {
        // No se pueden añadir personas a una cuenta ya creada
        if (cuentaActual != null) {
            mostrarError("Una vez creada la cuenta, no se pueden añadir más personas");
            return;
        }

        String nombre = campoPersona.getText();

        // Validar que el nombre no esté vacío
        if (nombre == null || nombre.isBlank()) {
            mostrarError("Introduce el nombre de la persona");
            return;
        }

        nombre = nombre.trim();

        // Evitar personas repetidas dentro de la misma cuenta
        for (PersonaReparto p : personasVista) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                mostrarError("Esa persona ya existe");
                return;
            }
        }

        // Añadir persona con porcentaje inicial 0
        personasVista.add(new PersonaReparto(nombre, 0));
        campoPersona.clear();
    }

    @FXML
    public void eliminarPersona() {
        // No se pueden eliminar personas de una cuenta ya creada
        if (cuentaActual != null) {
            mostrarError("Una vez creada la cuenta, no se pueden eliminar personas");
            return;
        }

        PersonaReparto seleccionada =
                tablaPersonas.getSelectionModel().getSelectedItem();

        // Validar que haya una persona seleccionada
        if (seleccionada == null) {
            mostrarError("Selecciona una persona");
            return;
        }

        // Eliminar la persona de la lista temporal
        personasVista.remove(seleccionada);
    }

    @FXML
    public void guardarCuenta() {
        try {
            // Si cuentaActual no es null, significa que la cuenta ya existe
            if (cuentaActual != null) {
                mostrarError("La cuenta ya está creada. La lista de personas no puede modificarse.");
                return;
            }

            String nombreCuenta = campoNombreCuenta.getText();

            // Validar nombre de cuenta
            if (nombreCuenta == null || nombreCuenta.isBlank()) {
                mostrarError("Introduce el nombre de la cuenta");
                return;
            }

            nombreCuenta = nombreCuenta.trim();

            // Una cuenta compartida debe tener al menos dos personas
            if (personasVista.size() < 2) {
                mostrarError("La cuenta debe tener al menos 2 personas");
                return;
            }

            List<CuentaCompartida> cuentas = repo.cargarTodas(RUTA);

            // Evitar crear dos cuentas con el mismo nombre
            for (CuentaCompartida c : cuentas) {
                if (c.getNombre() != null &&
                        c.getNombre().equalsIgnoreCase(nombreCuenta)) {
                    mostrarError("Ya existe una cuenta compartida con ese nombre");
                    return;
                }
            }

            // Obtener solo los nombres de las personas de la tabla
            List<String> nombres = obtenerNombresPersonas();

            // Crear la nueva cuenta compartida
            CuentaCompartida nueva = new CuentaCompartida(nombreCuenta, nombres);

            // Añadir la nueva cuenta a la lista y guardarla
            cuentas.add(nueva);
            repo.guardarTodas(cuentas, RUTA);

            // La nueva cuenta pasa a ser la cuenta actual
            cuentaActual = nueva;

            // Actualizar el desplegable y seleccionar la cuenta creada
            
            cargarComboCuentas();
            comboCuentas.setValue(cuentaActual.getNombre());

            // Mostrar sus datos en pantalla
            cargarCuentaEnVista();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cuenta creada");
            alert.setHeaderText(null);
            alert.setContentText("Cuenta compartida creada correctamente");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo guardar la cuenta");
        }
    }

    @FXML
    public void guardarPorcentajes() {
        try {
        	
            // Para guardar porcentajes debe haber una cuenta seleccionada
        	
            if (cuentaActual == null) {
            	
                mostrarError("Primero selecciona o crea una cuenta");
                
                return;
            }

            Map<String, Double> porcentajes = new HashMap<>();

            double suma = 0;

            // Recoger los porcentajes escritos en la tabla
            for (PersonaReparto p : personasVista) {
                porcentajes.put(p.getNombre(), p.getPorcentaje());
                suma += p.getPorcentaje();
            }

            // La suma de porcentajes debe ser exactamente 100 %
            if (Math.abs(suma - 100.0) > 0.01) {
                mostrarError("La suma de porcentajes debe ser 100%. Ahora suma: " + suma);
                return;
            }

            // Guardar los porcentajes en la cuenta actual
            cuentaActual.configurarPorcentajes(porcentajes);

            // Persistir la cuenta modificada
            guardarCuentaActualEnLista();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Porcentajes guardados");
            alert.setHeaderText(null);
            alert.setContentText("Porcentajes guardados correctamente");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudieron guardar los porcentajes");
        }
    }

    @FXML
    public void eliminarCuenta() {
        try {
            // Debe haber una cuenta seleccionada
            if (cuentaActual == null) {
                mostrarError("Selecciona una cuenta para eliminar");
                return;
            }

            /*
             * antes de eliminar una cuenta se comprueba si existen gastos asociados,
             * esto evita dejar gastos antiguos apuntando a una cuenta compartida
             * que ya no existe, lo que podría provocar inconsistencias en los saldos
             */
            if (existenGastosAsociados(cuentaActual.getNombre())) {
                mostrarError(
                        "No se puede eliminar esta cuenta compartida porque existen gastos asociados a ella.\n\n" +
                        "Para evitar inconsistencias, primero elimina esos gastos o cambia su cuenta desde la ventana Principal."
                );
                return;
            }

            // Confirmación antes de borrar definitivamente la cuenta
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar cuenta");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Seguro que quieres eliminar la cuenta compartida?");

            var resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                List<CuentaCompartida> cuentas = repo.cargarTodas(RUTA);

                // Eliminar la cuenta de la lista
                cuentas.removeIf(c ->
                        c.getNombre() != null &&
                        c.getNombre().equalsIgnoreCase(cuentaActual.getNombre())
                );

                // Guardar la lista actualizada
                repo.guardarTodas(cuentas, RUTA);

                // Actualizar interfaz
                cargarComboCuentas();
                nuevaCuenta();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cuenta eliminada");
                alert.setHeaderText(null);
                alert.setContentText("Cuenta eliminada correctamente");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo eliminar la cuenta");
        }
    }

    @FXML
    public void reiniciarSaldos() {
        try {
        	
            // Deebe haber una cuenta seleccionada
            if (cuentaActual == null) {
                mostrarError("Selecciona una cuenta");
                return;
            }

            // Confirmación antes de poner los saldos a cero
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Reiniciar saldos");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Seguro que quieres poner todos los saldos a 0?");

            var resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // Reiniciar saldos en la cuenta actual
                cuentaActual.reiniciarSaldos();

                // Guardar y refrescar la tabla
                guardarCuentaActualEnLista();
                refrescarTablaSaldos();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Saldos reiniciados");
                alert.setHeaderText(null);
                alert.setContentText("Saldos reiniciados correctamente");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudieron reiniciar los saldos");
        }
    }

    private boolean existenGastosAsociados(String nombreCuenta) {
        // Cargar gastos guardados
        List<Gasto> gastos = repoGastos.cargar(RUTA_GASTOS);

        // Comprobar si algún gasto pertenece a la cuenta indicada
        return gastos.stream()
                .anyMatch(g ->
                        g.getCuenta() != null &&
                        nombreCuenta != null &&
                        g.getCuenta().equalsIgnoreCase(nombreCuenta)
                );
    }

    private void guardarCuentaActualEnLista() {
        // Cargar todas las cuentas guardadas
        List<CuentaCompartida> cuentas = repo.cargarTodas(RUTA);

        // Sustituir la cuenta antigua por la versión modificada
        for (int i = 0; i < cuentas.size(); i++) {
            if (cuentas.get(i).getNombre() != null &&
                    cuentas.get(i).getNombre().equalsIgnoreCase(cuentaActual.getNombre())) {
                cuentas.set(i, cuentaActual);
                break;
            }
        }

        // Guardar la lista de cuentas actualizada
        repo.guardarTodas(cuentas, RUTA);
    }

    private void cargarCuentaEnVista() {
    	
        // mostraar el nombre de la cuenta seleccionada
    	
        labelCuenta.setText("Cuenta actual: " + cuentaActual.getNombre());

        // Mostrar el nombre en el campo de texto
        campoNombreCuenta.setText(cuentaActual.getNombre());

        // Limpiar la tabla antes de cargar las personas
        personasVista.clear();

        // Cargar personas y porcentajes de la cuenta actual
        for (String persona : cuentaActual.getPersonas()) {
            double porcentaje = cuentaActual.getPorcentajes().getOrDefault(persona, 0.0);
            personasVista.add(new PersonaReparto(persona, porcentaje));
        }

        // Actualizar la tabla de saldoss
        refrescarTablaSaldos();
    }

    private List<String> obtenerNombresPersonas() {
        List<String> nombres = new ArrayList<>();

        // Extraer los nombres válidos de la tabla
        for (PersonaReparto p : personasVista) {
            if (p.getNombre() != null && !p.getNombre().isBlank()) {
                nombres.add(p.getNombre().trim());
            }
        }

        return nombres;
    }

    private void refrescarTablaSaldos() {
    	
        // Si no hay cuenta seleccionada,se limpia la tabla
        if (cuentaActual == null) {
            tablaSaldos.getItems().clear();
            return;
        }

        List<FilaSaldo> filas = new ArrayList<>();

        // Crear una fila de saldo por cada persona de la cuenta
        for (String persona : cuentaActual.getPersonas()) {
            double saldo = cuentaActual.getSaldos().getOrDefault(persona, 0.0);
            filas.add(new FilaSaldo(persona, saldo));
        }

        tablaSaldos.setItems(FXCollections.observableArrayList(filas));
    }

    private double redondear(double valor) {
    	
        // Redondear a dos decimales
        return Math.round(valor * 100.0) / 100.0;
    }

    private void mostrarError(String mensaje) {
    	
        // Mostrar mensaje de error reutilizable
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}