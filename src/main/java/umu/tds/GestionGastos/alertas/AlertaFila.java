package umu.tds.GestionGastos.alertas;

public class AlertaFila {

    // Tipo de alerta que se mostrará en la tabla puede ser  Mensual o Semanal
    private String tipo;

    // Categoría asociada a la alerta, y puede ser "Todas"
    private String categoria;

    // Límite económico configurado para la alerta
    private double limite;

    // Constructor usado para crear una fila visible en la tabla de alertas
    public AlertaFila(String tipo, String categoria, double limite) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.limite = limite;
    }

    // Devuelve el tipo de alerta
    public String getTipo() {
        return tipo;
    }

    // Devuelve la categoría de la alerta
    public String getCategoria() {
        return categoria;
    }

    // Devuelve el límite de gasto de la alerta
    public double getLimite() {
        return limite;
    }
}