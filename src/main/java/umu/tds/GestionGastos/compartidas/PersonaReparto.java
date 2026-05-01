package umu.tds.GestionGastos.compartidas;

public class PersonaReparto {

     //nombre de la persona que pertenece a la cuenta compartida
    private String nombre;

      // Porcentaje de gasto que debe asumir esa persona
    private double porcentaje;

    // Constructor usado para mostrar una persona en la tabla de reparto
    public PersonaReparto(String nombre, double porcentaje) {
    	
    	
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    // Devuelve el nombre de la persona
    public String getNombre() {
        return nombre;
    }

    // devuelve el porcentaje asignado a la persona
    public double getPorcentaje() {
        return porcentaje;
    }

    // modifica el nombre de la persona
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Modifica el porcentaje asignado
    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}