package umu.tds.GestionGastos.compartidas;

public class FilaSaldo {

    //nombre de la persona que se mostrará en la tabla de saldos
    private String persona;

    // Saldo pendiente de esa persona dentro de la cuenta compartida
    private double saldo;

    // Constructor usado pa crear una fila de la tabla
    public FilaSaldo(String persona, double saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    // para devolver el nombre de la persona
    public String getPersona() {
        return persona;
    }

    // Devuelve el saldo de la persona
    public double getSaldo() {
        return saldo;
    }
}