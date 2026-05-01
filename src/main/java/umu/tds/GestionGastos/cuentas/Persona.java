package umu.tds.GestionGastos.cuentas;

public class Persona {

    //noombre de la persona dentro de la cuenta compartida
    private String nombre;

      // Saldo pendiente de la persona
    private double saldo;

     // Constructor que crea una persona con saldo inicial 0
    public Persona(String nombre) {
        this.nombre = nombre;
        this.saldo = 0;
    }

    // Devuelve el nombre de la persona
    public String getNombre() {
        return nombre;
    }

    // Devuelve el saldo actual de la persona
    public double getSaldo() {
        return saldo;
    }

    // Suma o resta una cantidad al saldo de la persona
    public void añadirSaldo(double cantidad) {
        this.saldo += cantidad;
    }
}