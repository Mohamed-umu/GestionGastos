package umu.tds.GestionGastos.alertas;

public class AlertaConfig {

    // Tipo de alerta: Mensual o Semanal
    private String tipo;

    // Límite máximo de gasto permitido
    private double limite;

    // Categoría asociada a la alerta, y puede estar vacía si aplica a todas
    private String categoria;

    // este Constructor vacío necesario para que Jackson pueda cargar el objeto desde JSON
    public AlertaConfig() {
    }

    // Constructor usado para crear una configuración de alerta manualmente
    public AlertaConfig(String tipo, double limite, String categoria) {
        this.tipo = tipo;
        this.limite = limite;
        this.categoria = categoria;
    }

    // Devuelve el tipo de alerta
    public String getTipo() {
        return tipo;
    }

    // Modifica el tipo de alerta
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Devuelve el límite de gasto
    public double getLimite() {
        return limite;
    }

    // Modifica el límite de gasto
    public void setLimite(double limite) {
        this.limite = limite;
    }

    // Devuelve la categoría asociada a la alerta
    public String getCategoria() {
        return categoria;
    }

    // Modifica la categoría asociada a la alerta
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}