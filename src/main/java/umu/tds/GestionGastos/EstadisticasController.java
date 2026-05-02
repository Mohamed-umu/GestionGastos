package umu.tds.GestionGastos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import umu.tds.GestionGastos.modelo.Gasto;
import umu.tds.GestionGastos.persistencia.RepositorioGastos;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;

public class EstadisticasController {

    // eetiquetas superiores donde se muestran los datos generales
    @FXML private Label labelTotal;
    @FXML private Label labelNumeroGastos;
    @FXML private Label labelMedia;

    // Filtros disponibles en la ventana de estadísticas
    @FXML private ComboBox<String> comboMes;
    @FXML private ComboBox<String> comboCuenta;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;

    // Gráficos de distribución de gastos
    @FXML private PieChart graficoCircular;
    @FXML private BarChart<String, Number> graficoBarras;
    @FXML private CategoryAxis ejeCategorias;
    @FXML private NumberAxis ejeCantidades;

    // Tabla de resumen por categoría
    @FXML private TableView<ResumenCategoria> tablaResumen;
    @FXML private TableColumn<ResumenCategoria, String> colCategoria;
    @FXML private TableColumn<ResumenCategoria, Double> colTotal;
    @FXML private TableColumn<ResumenCategoria, Integer> colNumero;
    @FXML private TableColumn<ResumenCategoria, Double> colMedia;

    // Ruta del fichero donde se guardan los gastos
    private final String RUTA = System.getProperty("user.dir") + "/gastos.json";

    @FXML
    public void initialize() {
    	
        // Configurar tabla y filtros al cargar la ventana
        configurarTabla();
        configurarFiltros();

        // Configuración visual de los gráficos
        graficoBarras.setAnimated(false);
        graficoCircular.setAnimated(true);
        ejeCategorias.setAnimated(false);
        ejeCantidades.setAnimated(false);

        // Ocultar leyenda del gráfico de barras para dejarlo más limpio
        graficoBarras.setLegendVisible(false);

        // Cargar las estadísticas iniciales
        actualizarEstadisticas();
    }

    private void configurarTabla() {
    	
    	
        // Asociar cada columna de la tabla con su atributo correspondiente
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategoria()));

        colTotal.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTotal()));

        colNumero.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getNumero()).asObject());

        colMedia.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getMedia()));
    }

    private void configurarFiltros() {
    	
        // Cargar los meses disponibles para filtrar
        comboMes.getItems().addAll(
                "Todos",
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        comboMes.setValue("Todos");

        // Cargar cuentas y categorías existentes desde los gastos guardados
        cargarCuentas();
        cargarCategorias();
    }

    private void cargarCuentas() {
        comboCuenta.getItems().clear();
        comboCuenta.getItems().add("Todas");

        // Leer gastos guardados para extraer las cuentas existentes
        List<Gasto> gastos = RepositorioGastos.getInstancia().cargar(RUTA);

        // Obtener cuentas distintas, ignorando valores vacíos
        List<String> cuentas = gastos.stream()
                .map(Gasto::getCuenta)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        comboCuenta.getItems().addAll(cuentas);
        comboCuenta.setValue("Todas");
    }

    private void cargarCategorias() {
        comboCategoria.getItems().clear();
        comboCategoria.getItems().add("Todas");

        // Leer gastos guardados para extraer las categorías existentes
        List<Gasto> gastos = RepositorioGastos.getInstancia().cargar(RUTA);

        // Obtener categorías distintas, ignorando valores vacíos
        List<String> categorias = gastos.stream()
                .map(Gasto::getCategoria)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        comboCategoria.getItems().addAll(categorias);
        comboCategoria.setValue("Todas");
    }

    @FXML
    public void aplicarFiltros() {
        // Recalcular estadísticas usando los filtros seleccionados
        actualizarEstadisticas();
    }

    @FXML
    public void limpiarFiltros() {
    	
        // Restaurar los filtros a su estado inicial
        comboMes.setValue("Todos");
        comboCuenta.setValue("Todas");
        comboCategoria.setValue("Todas");

        fechaInicio.setValue(null);
        fechaFin.setValue(null);

        actualizarEstadisticas();
    }

    public void actualizarEstadisticas() {

        // Cargar todos los gastos desde el repositorio
        List<Gasto> gastos = RepositorioGastos
                .getInstancia()
                .cargar(RUTA);

        // Aplicar filtros seleccionados por el usuario
        gastos = filtrarGastos(gastos);

        // Limpiar gráficos y tabla antes de volver a cargar datos
        graficoCircular.getData().clear();
        graficoBarras.getData().clear();
        ejeCategorias.getCategories().clear();
        tablaResumen.getItems().clear();

        // Si no hay gastos filtrados, se muestran valores a cero
        if (gastos.isEmpty()) {
            labelTotal.setText("0 €");
            labelNumeroGastos.setText("0");
            labelMedia.setText("0 €");
            return;
        }

        // Calcular total, número de gastos y media general
        double totalGeneral = gastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();

        int numeroGastos = gastos.size();
        double mediaGeneral = totalGeneral / numeroGastos;

        // Mostrar estadísticas generales
        labelTotal.setText(String.format("%.2f €", totalGeneral));
        labelNumeroGastos.setText(String.valueOf(numeroGastos));
        labelMedia.setText(String.format("%.2f €", mediaGeneral));

        // Mapas auxiliares para agrupar importes y número de gastos por categoría
        Map<String, Double> totalPorCategoria = new HashMap<>();
        Map<String, Integer> numeroPorCategoria = new HashMap<>();

        // Agrupar los gastos por categoría
        for (Gasto g : gastos) {
            String categoria = g.getCategoria();

            // Si un gasto no tiene categoría, se agrupa como "Sin categoría"
            if (categoria == null || categoria.isBlank()) {
                categoria = "Sin categoría";
            }

            totalPorCategoria.put(
                    categoria,
                    totalPorCategoria.getOrDefault(categoria, 0.0) + g.getCantidad()
            );

            numeroPorCategoria.put(
                    categoria,
                    numeroPorCategoria.getOrDefault(categoria, 0) + 1
            );
        }

        // Serie de datos para el gráfico de barras
        XYChart.Series<String, Number> serieBarras = new XYChart.Series<>();
        serieBarras.setName("Gastos");

        // Ordenar categorías alfabéticamente para mostrarlas de forma estable
        List<String> categoriasOrdenadas = totalPorCategoria.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        // Fijar categorías del eje X para evitar problemas visuales al filtrar
        ejeCategorias.setCategories(FXCollections.observableArrayList(categoriasOrdenadas));

        // Rellenar gráfico circular,gráfico de barras y tabla resumen
        for (String categoria : categoriasOrdenadas) {

            double totalCategoria = totalPorCategoria.get(categoria);
            int numeroCategoria = numeroPorCategoria.get(categoria);
            double mediaCategoria = totalCategoria / numeroCategoria;

            graficoCircular.getData().add(
                    new PieChart.Data(categoria, totalCategoria)
            );

            serieBarras.getData().add(
                    new XYChart.Data<>(categoria, totalCategoria)
            );

            tablaResumen.getItems().add(
                    new ResumenCategoria(
                            categoria,
                            redondear(totalCategoria),
                            numeroCategoria,
                            redondear(mediaCategoria)
                    )
            );
        }

        // actualizar el gráfico de barras con la nueva serie
        graficoBarras.getData().setAll(serieBarras);
    }

    private List<Gasto> filtrarGastos(List<Gasto> gastos) {

        // Valores seleccionados en los filtros
        String mesSeleccionado = comboMes.getValue();
        String cuentaSeleccionada = comboCuenta.getValue();
        String categoriaSeleccionada = comboCategoria.getValue();

        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        return gastos.stream()

                // Filtro por mes
                .filter(g -> {
                    if (mesSeleccionado == null || mesSeleccionado.equals("Todos")) {
                        return true;
                    }

                    int mes = nombreMesANumero(mesSeleccionado);
                    return g.getFecha().getMonthValue() == mes;
                })

                // Filtro por cuenta
                .filter(g -> {
                    if (cuentaSeleccionada == null || cuentaSeleccionada.equals("Todas")) {
                        return true;
                    }

                    return g.getCuenta() != null &&
                            g.getCuenta().equalsIgnoreCase(cuentaSeleccionada);
                })

                // Filtro por categoría
                .filter(g -> {
                    if (categoriaSeleccionada == null || categoriaSeleccionada.equals("Todas")) {
                        return true;
                    }

                    return g.getCategoria() != null &&
                            g.getCategoria().equalsIgnoreCase(categoriaSeleccionada);
                })

                // Filtro por fecha inicial
                .filter(g -> {
                    if (inicio == null) {
                        return true;
                    }

                    return !g.getFecha().toLocalDate().isBefore(inicio);
                })

                // Filtro por fecha final
                .filter(g -> {
                    if (fin == null) {
                        return true;
                    }

                    return !g.getFecha().toLocalDate().isAfter(fin);
                })

                .collect(Collectors.toList());
    }

    private int nombreMesANumero(String mes) {
    	
        // Convertir el nombre del mes a su número correspondiente
        return switch (mes) {
            case "Enero" -> 1;
            case "Febrero" -> 2;
            case "Marzo" -> 3;
            case "Abril" -> 4;
            case "Mayo" -> 5;
            case "Junio" -> 6;
            case "Julio" -> 7;
            case "Agosto" -> 8;
            case "Septiembre" -> 9;
            case "Octubre" -> 10;
            case "Noviembre" -> 11;
            case "Diciembre" -> 12;
            default -> 0;
        };
    }

    private double redondear(double valor) {
        // Redondear a dos decimales
        return Math.round(valor * 100.0) / 100.0;
    }

    public static class ResumenCategoria {

        // Datos que se muestran en la tabla de resumen
        private String categoria;
        private double total;
        private int numero;
        private double media;

        public ResumenCategoria(String categoria, double total, int numero, double media) {
            this.categoria = categoria;
            this.total = total;
            this.numero = numero;
            this.media = media;
        }

        public String getCategoria() {
            return categoria;
        }

        public double getTotal() {
            return total;
        }

        public int getNumero() {
            return numero;
        }

        public double getMedia() {
            return media;
        }
    }
}