package umu.tds.GestionGastos.alertas;

import java.time.LocalDate;
import java.util.List;

import umu.tds.GestionGastos.modelo.Gasto;

public class AlertaMensual implements EstrategiaAlerta {

    // Límite máximo de gasto mensual permitido
    private double limite;

    // Categoría opcional de la alerta; si está vacía, se aplica a todas
    private String categoria;

    // Constructor de la alerta mensual
    public AlertaMensual(double limite, String categoria) {
        this.limite = limite;
        this.categoria = categoria;
    }

    @Override
    public boolean comprobar(List<Gasto> gastos) {

        // Obtener el mes actual
        int mesActual = LocalDate.now().getMonthValue();

        // Sumar los gastos del mes actual y, si procede, de la categoría indicada
        double total = gastos.stream()
                .filter(g -> g.getFecha().getMonthValue() == mesActual)
                .filter(g -> categoria == null || categoria.isBlank()
                        || g.getCategoria().equalsIgnoreCase(categoria))
                .mapToDouble(Gasto::getCantidad)
                .sum();

        // La alerta se activa si el total supera el límite
        return total > limite;
    }

    @Override
    public String getMensaje() {
    	
    	
    	
        // Mensaje cuando la alerta no está asociada a ninguna categoría concreta
        if (categoria == null || categoria.isBlank()) {
            return "Has superado el límite mensual de " + limite + "€";
        }

        // Mensaje cuando la alerta pertenece a una categoría específica
        return "Has superado el límite mensual de " + limite + "€ en la categoría " + categoria;
    }

    @Override
    public String getDescripcion() {
    	
    	
        // Descripción corta de una alerta mensual general
        if (categoria == null || categoria.isBlank()) {
            return "Mensual | límite: " + limite + "€";
        }

        // Descripción corta de una alerta mensual por categoría
        return "Mensual | categoría: " + categoria + " | límite: " + limite + "€";
    }

    // Devuelve el límite configurado
    public double getLimite() {
        return limite;
    }

    // Devuelve la categoría asociada a la alerta
    public String getCategoria() {
        return categoria;
    }
}