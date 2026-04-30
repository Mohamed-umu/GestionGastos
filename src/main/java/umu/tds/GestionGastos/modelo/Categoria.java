package umu.tds.GestionGastos.modelo;

public class Categoria {

    //el noombre de la categoría del gasto
      private String nombre;

    // Constructor vacío necesario para que Jackson pueda cargar desde JSON
    public Categoria() {

    }

    // Constructor usado para crear una categoría con nombre
    public Categoria(String nombre) {
        
    	this.nombre = nombre;
    }

    // Devuelve el nombre de la categoría
    public String getNombre() {
        return nombre;
    }
}