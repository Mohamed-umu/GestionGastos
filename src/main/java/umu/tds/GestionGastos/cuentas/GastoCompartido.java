package umu.tds.GestionGastos.cuentas;

public class GastoCompartido {

       //cantidad total del gasto compartido
    private double cantidad;

    //persona que ha pagado inicialmente el gasto
    private Persona pagador;

    // Constructor usado para crear un gasto compartido
    public GastoCompartido(double cantidad, Persona pagador) {
        this.cantidad = cantidad;
        this.pagador = pagador;
    }

    // Devuelve la cantidad del gasto
    public double getCantidad() {
        return cantidad;
    }

    // Devuelve la persona que pagó el gasto
    public Persona getPagador() {
        return pagador;
    }
}