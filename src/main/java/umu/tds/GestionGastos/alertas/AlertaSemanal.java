package umu.tds.GestionGastos.alertas;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import umu.tds.GestionGastos.modelo.Gasto;

public class AlertaSemanal implements EstrategiaAlerta {

    // Límite máximo de gasto semanal permitido
    private double limite;

    // Categoría opcional de la alerta; si está vacía, se aplica a todas
    private String categoria;

    // Constructor de la alerta semanal
    public AlertaSemanal(double limite, String categoria) {
        this.limite = limite;
        this.categoria = categoria;
    }

    @Override
    public boolean comprobar(List<Gasto> gastos) {

         // Obtener la numeración de semanas según la configuración regional
    	
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

            // Obtener la semana actual dell año
        int semanaActual = LocalDate.now().get(weekFields.weekOfWeekBasedYear());

        //sumar los gastos de la semana actual y, si procede, de la categoría indicada
      
        double total = gastos.stream()
        		
                .filter(g -> g.getFecha().get(weekFields.weekOfWeekBasedYear()) == semanaActual)
                .filter(g -> categoria == null || categoria.isBlank()
                        || g.getCategoria().equalsIgnoreCase(categoria))
                .mapToDouble(Gasto::getCantidad)
                .sum();

        //La alerta se activa si el total supera el limite
        return total > limite;
    }

    @Override
    public String getMensaje() {
    	
    	
        //Mensaje cuando la alerta no está asociada a ninguna categoría concreta
        if (categoria == null || categoria.isBlank()) {
        	
        	
            return "Has superado el límite semanal de " + limite + "€";
        }

        
        // Mensaje cuando la alerta pertenece a una categoría específica
        return "Has superado el límite semanal de " + limite + "€ en la categoría " + categoria;
    }

    @Override
    public String getDescripcion() {
    	
    	
        // Descripción corta de una alerta semanal general
        if (categoria == null || categoria.isBlank()) {
            return "Semanal | límite: " + limite + "€";
        }

        // Descripción corta de una alerta semanal por categoría
        return "Semanal | categoría: " + categoria + " | límite: " + limite + "€";
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