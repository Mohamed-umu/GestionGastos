package umu.tds.GestionGastos;

import umu.tds.GestionGastos.modelo.Gasto;
import umu.tds.GestionGastos.persistencia.RepositorioGastos;
import umu.tds.GestionGastos.compartidas.CuentaCompartida;
import umu.tds.GestionGastos.compartidas.RepositorioCuentaCompartida;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CLI {

    // Ruta del fichero donde se guardan los gastos
    private static final String RUTA_GASTOS =
            System.getProperty("user.dir") + "/gastos.json";

    // Ruta del fichero donde se guardan las cuentas compartidas
    private static final String RUTA_CUENTAS =
            System.getProperty("user.dir") + "/cuentas_compartidas.json";

    // Repositorio para cargar y guardar gastos
    private static final RepositorioGastos repoGastos =
            RepositorioGastos.getInstancia();

    // Reepositorio para cargar y guardar cuentas compartidas
    private static final RepositorioCuentaCompartida repoCuentas =
            RepositorioCuentaCompartida.getInstancia();

    // Formatoo usadopor ejmp: 3/2/2022 10:11
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);

    public static void main(String[] args) {

        // Scanner para leer datos introducidos por teclado
        Scanner sc = new Scanner(System.in);

        // Menú principal de la línea de comandos
        while (true) {
            System.out.println();
            System.out.println("=================================");
            System.out.println("   GESTIÓN DE GASTOS - CONSOLA   ");
            System.out.println("=================================");
            System.out.println("1. Listar gastos");
            System.out.println("2. Añadir gasto");
            System.out.println("3. Modificar gasto");
            System.out.println("4. Borrar gasto");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            String opcion = sc.nextLine();

            // Ejecuta la opción elegida por el usuario
            switch (opcion) {
                case "1" -> listarGastos();
                case "2" -> añadirGasto(sc);
                case "3" -> modificarGasto(sc);
                case "4" -> borrarGasto(sc);
                case "0" -> {
                    System.out.println("Saliendo de la línea de comandos...");
                    return;
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void listarGastos() {

        // Cargar gastos guardados desde JSON
        List<Gasto> gastos = repoGastos.cargar(RUTA_GASTOS);

        if (gastos.isEmpty()) {
            System.out.println("No hay gastos registrados.");
            return;
        }

        // Cabecera de la tabla mostrada por consola
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s %-16s %-15s %-18s %-18s %-25s %-15s %-12s %-8s%n",
                "ID", "Fecha", "Cuenta", "Categoría", "Subcategoría", "Descripción", "Pagador", "Cantidad", "Moneda");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");

        // Mostrar cada gasto con un índice para poder modificarlo o borrarlo después
        for (int i = 0; i < gastos.size(); i++) {
            Gasto g = gastos.get(i);

            System.out.printf("%-4d %-16s %-15s %-18s %-18s %-25s %-15s %-12.2f %-8s%n",
                    i,
                    g.getFecha().format(FORMATO),
                    cortar(texto(g.getCuenta()), 15),
                    cortar(texto(g.getCategoria()), 18),
                    cortar(texto(g.getSubcategoria()), 18),
                    cortar(texto(g.getDescripcion()), 25),
                    cortar(texto(g.getPagador()), 15),
                    g.getCantidad(),
                    texto(g.getMoneda()));
        }

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void añadirGasto(Scanner sc) {

        try {
            // Cargar lista actual de gastos
            List<Gasto> gastos = repoGastos.cargar(RUTA_GASTOS);

            System.out.println();
            System.out.println("----------- AÑADIR GASTO -----------");

            // Mostrar cuentas compartidas disponibles para que el usuario pueda elegir una
            mostrarCuentasCompartidas();

            // Leer datos del gasto
            System.out.print("Fecha y hora (M/d/yyyy H:mm), ejemplo 3/2/2022 10:11: ");
            LocalDateTime fecha = LocalDateTime.parse(sc.nextLine(), FORMATO);

            System.out.print("Cuenta: ");
            String cuenta = sc.nextLine();

            System.out.print("Categoría: ");
            String categoria = sc.nextLine();

            System.out.print("Subcategoría: ");
            String subcategoria = sc.nextLine();

            System.out.print("Descripción: ");
            String descripcion = sc.nextLine();

            System.out.print("Pagador: ");
            String pagador = sc.nextLine();

            System.out.print("Cantidad: ");
            double cantidad = Double.parseDouble(sc.nextLine());

            System.out.print("Moneda: ");
            String moneda = sc.nextLine();

            // Si no se introduce moneda, se usa EUR por defecto
            if (moneda.isBlank()) {
                moneda = "EUR";
            }

            // Crear objeto gasto con los datos introducidos
            Gasto nuevo = new Gasto(
                    fecha,
                    cuenta,
                    categoria,
                    subcategoria,
                    descripcion,
                    pagador,
                    cantidad,
                    moneda
            );

            // Añadir el gasto a la lista
            gastos.add(nuevo);

            /*
             * Después de añadir, modificar o borrar gastos se recalculan las cuentas
             * compartidas desde cero, asilos saldos quedan siempre coherentes con la lista real de gastos guardados
             */
            recalcularCuentasCompartidas(gastos);

            // Guardar la nueva lista de gastos
            repoGastos.guardar(gastos, RUTA_GASTOS);

            System.out.println("Gasto añadido correctamente.");

        } catch (Exception e) {
            System.out.println("Error al añadir el gasto.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }

    private static void modificarGasto(Scanner sc) {

        try {
            // Cargar gastos actuales
            List<Gasto> gastos = repoGastos.cargar(RUTA_GASTOS);

            if (gastos.isEmpty()) {
                System.out.println("No hay gastos para modificar.");
                return;
            }

            // Mostrar lista para que el usuario elija por índice
            listarGastos();

            System.out.println();
            System.out.print("Número del gasto a modificar: ");
            int index = Integer.parseInt(sc.nextLine());

            // Validar índice
            if (index < 0 || index >= gastos.size()) {
                System.out.println("Índice no válido.");
                return;
            }

            Gasto g = gastos.get(index);

            System.out.println();
            System.out.println("Pulsa ENTER para mantener el valor actual.");

            // Modificar fecha si el usuario introduce una nueva
            System.out.print("Fecha actual (" + g.getFecha().format(FORMATO) + "), nueva fecha (M/d/yyyy H:mm): ");
            String nuevaFecha = sc.nextLine();
            if (!nuevaFecha.isBlank()) {
                g.setFecha(LocalDateTime.parse(nuevaFecha, FORMATO));
            }

            // Modificar cuenta
            System.out.print("Cuenta actual (" + texto(g.getCuenta()) + "), nueva cuenta: ");
            String nuevaCuenta = sc.nextLine();
            if (!nuevaCuenta.isBlank()) {
                g.setCuenta(nuevaCuenta);
            }

            // Modificar categoría
            System.out.print("Categoría actual (" + texto(g.getCategoria()) + "), nueva categoría: ");
            String nuevaCategoria = sc.nextLine();
            if (!nuevaCategoria.isBlank()) {
                g.setCategoria(nuevaCategoria);
            }

            // Modificar subcategoría
            System.out.print("Subcategoría actual (" + texto(g.getSubcategoria()) + "), nueva subcategoría: ");
            String nuevaSubcategoria = sc.nextLine();
            if (!nuevaSubcategoria.isBlank()) {
                g.setSubcategoria(nuevaSubcategoria);
            }

            // Modificar descripción
            System.out.print("Descripción actual (" + texto(g.getDescripcion()) + "), nueva descripción: ");
            String nuevaDescripcion = sc.nextLine();
            if (!nuevaDescripcion.isBlank()) {
                g.setDescripcion(nuevaDescripcion);
            }

            // Modificar pagador
            System.out.print("Pagador actual (" + texto(g.getPagador()) + "), nuevo pagador: ");
            String nuevoPagador = sc.nextLine();
            if (!nuevoPagador.isBlank()) {
                g.setPagador(nuevoPagador);
            }

            // Modificar cantidad
            System.out.print("Cantidad actual (" + g.getCantidad() + "), nueva cantidad: ");
            String nuevaCantidad = sc.nextLine();
            if (!nuevaCantidad.isBlank()) {
                g.setCantidad(Double.parseDouble(nuevaCantidad));
            }

            // Modificar moneda
            System.out.print("Moneda actual (" + texto(g.getMoneda()) + "), nueva moneda: ");
            String nuevaMoneda = sc.nextLine();
            if (!nuevaMoneda.isBlank()) {
                g.setMoneda(nuevaMoneda);
            }

            // Recalcular saldos de cuentas compartidas tras modificar el gasto
            recalcularCuentasCompartidas(gastos);

            // Guardar cambios
            repoGastos.guardar(gastos, RUTA_GASTOS);

            System.out.println("Gasto modificado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al modificar el gasto.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }

    private static void borrarGasto(Scanner sc) {

        try {
            // Cargar gastos actuales
            List<Gasto> gastos = repoGastos.cargar(RUTA_GASTOS);

            if (gastos.isEmpty()) {
                System.out.println("No hay gastos para borrar.");
                return;
            }

            // Mostrar lista para elegir el gasto a borrar
            listarGastos();

            System.out.println();
            System.out.print("Número del gasto a borrar: ");
            int index = Integer.parseInt(sc.nextLine());

            // Validar índice
            if (index < 0 || index >= gastos.size()) {
                System.out.println("Índice no válido.");
                return;
            }

            Gasto eliminado = gastos.get(index);

            // Mostrar resumen del gasto antes de confirmar el borrado
            System.out.println("Vas a borrar:");
            System.out.println(
                    eliminado.getFecha().format(FORMATO) + " | " +
                            texto(eliminado.getCuenta()) + " | " +
                            texto(eliminado.getCategoria()) + " | " +
                            eliminado.getCantidad() + " " +
                            texto(eliminado.getMoneda())
            );

            // Confirmación para evitar borrados accidentales
            System.out.print("¿Seguro? (s/n): ");
            String respuesta = sc.nextLine();

            if (!respuesta.equalsIgnoreCase("s")) {
                System.out.println("Borrado cancelado.");
                return;
            }

            // Eliminar gasto de la lista
            gastos.remove(index);

            // Recalcular saldos de cuentas compartidas tras borrar el gasto
            recalcularCuentasCompartidas(gastos);

            // Guardar cambios
            repoGastos.guardar(gastos, RUTA_GASTOS);

            System.out.println("Gasto borrado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al borrar el gasto.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }

    private static void recalcularCuentasCompartidas(List<Gasto> gastos) {

        // Cargar todas las cuentas compartidas
        List<CuentaCompartida> cuentas = repoCuentas.cargarTodas(RUTA_CUENTAS);

        // Reiniciar saldos antes de recalcular
        for (CuentaCompartida cuenta : cuentas) {
            cuenta.reiniciarSaldos();
        }

        // Aplicar de nuevo todos los gastos que pertenezcan a cuentas compartidas
        for (Gasto gasto : gastos) {
            for (CuentaCompartida cuenta : cuentas) {

                if (gasto.getCuenta() != null &&
                        cuenta.getNombre() != null &&
                        gasto.getCuenta().equalsIgnoreCase(cuenta.getNombre())) {

                    cuenta.aplicarGastoSiCorresponde(gasto);
                }
            }
        }

        // Guardar las cuentas con los saldos actualizados
        repoCuentas.guardarTodas(cuentas, RUTA_CUENTAS);
    }

    private static void mostrarCuentasCompartidas() {

        // Cargar cuentas compartidas disponibles
        List<CuentaCompartida> cuentas = repoCuentas.cargarTodas(RUTA_CUENTAS);

        if (cuentas.isEmpty()) {
            System.out.println("Cuentas disponibles: Personal");
            return;
        }

        System.out.print("Cuentas disponibles: Personal");

        // Mostrar nombres de cuentas compartidas existentes
        for (CuentaCompartida c : cuentas) {
            System.out.print(", " + c.getNombre());
        }

        System.out.println();
    }

    private static String texto(String s) {
        // Evita mostrar null en consola
        if (s == null) {
            return "";
        }
        return s;
    }

    private static String cortar(String texto, int max) {
        // Evita errores con texto nulo
        if (texto == null) {
            return "";
        }

        // Si cabe en la columna, se devuelve igual
        if (texto.length() <= max) {
            return texto;
        }

        // Si es demasiado largo, se acorta con puntos suspensivos
        return texto.substring(0, max - 3) + "...";
    }
}