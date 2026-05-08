# Patrones de diseño utilizados

## 1. Introducción

En el proyecto **Gestión de Gastos** se han aplicado varios patrones de diseño para mejorar la organización del código, reducir el acoplamiento y facilitar futuras ampliaciones.

El enunciado de la práctica solicita aplicar correctamente algunos patrones, especialmente:

- Patrón **Estrategia**.
- Patrón **Adaptador**.
- Patrón **Método Factoría**.
- Patrón **Singleton** cuando sea necesario.

Además, también se utiliza el patrón **Repositorio** para separar la persistencia del resto de la aplicación.

---

## 2. Patrón Singleton

## 2.1. Idea general

El patrón **Singleton** se utiliza cuando una clase debe tener una única instancia compartida en toda la aplicación.

En este proyecto se usa en clases que gestionan información global o acceso común a datos.

---

## 2.2. Clases donde se aplica

Algunas clases que aplican Singleton son:

- `GestorGastos`
- `RepositorioGastos`
- `RepositorioAlertas`
- `RepositorioHistorialAlertas`
- `RepositorioCuentaCompartida`

---

## 2.3. Ejemplo: GestorGastos

La clase `GestorGastos` mantiene la lista de gastos cargados en memoria.

Se utiliza una única instancia porque distintas partes de la aplicación necesitan acceder a la misma lista de gastos.

```java
private static GestorGastos instancia;

private GestorGastos() {
    gastos = new ArrayList<>();
}

public static GestorGastos getInstancia() {
    if (instancia == null) {
        instancia = new GestorGastos();
    }
    return instancia;
}
```

---

## 2.4. Ventajas en el proyecto

El uso de Singleton permite:

- Evitar crear varias instancias con información distinta.
- Compartir datos entre controladores.
- Centralizar el acceso a repositorios y gestores.
- Mantener coherencia durante la ejecución de la aplicación.

Por ejemplo, `PrincipalController` y otras partes de la aplicación pueden acceder al mismo gestor de gastos o al mismo repositorio.

---

## 3. Patrón Repositorio

## 3.1. Idea general

El patrón **Repositorio** se utiliza para separar la lógica de almacenamiento del resto de la aplicación.

Gracias a este patrón, las clases de la interfaz o de negocio no necesitan saber cómo se guardan realmente los datos.

En este proyecto, los datos se guardan en ficheros JSON usando Jackson.

---

## 3.2. Clases donde se aplica

Los principales repositorios del proyecto son:

- `RepositorioGastos`
- `RepositorioAlertas`
- `RepositorioHistorialAlertas`
- `RepositorioCuentaCompartida`

Cada repositorio se encarga de un tipo concreto de información.

---

## 3.3. Ejemplo: RepositorioGastos

La clase `RepositorioGastos` permite guardar y cargar gastos desde un fichero JSON.

```java
public void guardar(List<Gasto> gastos, String fichero) {
    mapper.writeValue(new File(fichero), gastos);
}

public List<Gasto> cargar(String fichero) {
    Gasto[] array = mapper.readValue(file, Gasto[].class);
    return new ArrayList<>(Arrays.asList(array));
}
```

---

## 3.4. Ventajas en el proyecto

El patrón Repositorio aporta varias ventajas:

- Desacopla la persistencia de la interfaz gráfica.
- Evita repetir código de lectura y escritura de JSON.
- Facilita cambiar el sistema de almacenamiento en el futuro.
- Mejora la organización del proyecto.

Por ejemplo, si en el futuro se quisiera usar una base de datos en lugar de JSON, se podría modificar principalmente la capa de repositorios sin cambiar toda la aplicación.

---

## 4. Patrón Estrategia

## 4.1. Idea general

El patrón **Estrategia** permite definir varias formas de realizar una operación y elegir cuál usar en tiempo de ejecución.

En este proyecto se utiliza principalmente en el sistema de alertas.

Una alerta semanal y una alerta mensual tienen el mismo objetivo: comprobar si se ha superado un límite de gasto. Sin embargo, la forma de comprobarlo es diferente.

---

## 4.2. Clases donde se aplica

El patrón Estrategia se aplica con estas clases:

- `EstrategiaAlerta`
- `AlertaMensual`
- `AlertaSemanal`

La interfaz `EstrategiaAlerta` define las operaciones comunes:

```java
public interface EstrategiaAlerta {

    boolean comprobar(List<Gasto> gastos);

    String getMensaje();

    String getDescripcion();
}
```

---

## 4.3. Estrategias concretas

La clase `AlertaMensual` comprueba el gasto del mes actual.

La clase `AlertaSemanal` comprueba el gasto de la semana actual.

Ambas implementan la misma interfaz, pero con lógica diferente.

```java
public class AlertaMensual implements EstrategiaAlerta {
    ...
}

public class AlertaSemanal implements EstrategiaAlerta {
    ...
}
```

---

## 4.4. Uso en GestorAlertas

La clase `GestorAlertas` trabaja con objetos de tipo `EstrategiaAlerta`, sin depender directamente de si la alerta es mensual o semanal.

```java
for (EstrategiaAlerta alerta : alertas) {
    if (alerta.comprobar(gastos)) {
        historial.add(new Notificacion(alerta.getMensaje()));
    }
}
```

Esto permite añadir nuevos tipos de alerta en el futuro sin modificar demasiado el gestor.

---

## 4.5. Ventajas en el proyecto

El patrón Estrategia permite:

- Separar la lógica de alerta semanal y mensual.
- Evitar grandes estructuras `if` dentro del gestor.
- Facilitar la ampliación con nuevas alertas.
- Cumplir con el requisito del enunciado.

Por ejemplo, se podría añadir en el futuro una `AlertaDiaria` creando una nueva clase que implemente `EstrategiaAlerta`.

---

## 5. Método Factoría

## 5.1. Idea general

El **Método Factoría** permite centralizar la creación de objetos.

En lugar de crear directamente un objeto con `new` en muchas partes del código, se usa una clase que decide qué objeto concreto crear.

En este proyecto se utiliza en la importación de ficheros.

---

## 5.2. Clase donde se aplica

La clase principal es:

- `FactoriaImportador`

Esta clase decide qué importador crear dependiendo de la extensión del fichero.

```java
public class FactoriaImportador {

    public static Importador crear(String fichero) {

        String f = fichero.toLowerCase();

        if (f.endsWith(".csv")) {
            return new ImportadorCSV();
        }

        if (f.endsWith(".txt")) {
            return new ImportadorTXT();
        }

        throw new IllegalArgumentException("Formato no soportado: " + fichero);
    }
}
```

---

## 5.3. Uso desde PrincipalController

Cuando el usuario selecciona un fichero para importar, el controlador no crea directamente un `ImportadorCSV` o un `ImportadorTXT`.

En su lugar, pide a la factoría que cree el importador adecuado:

```java
var imp = FactoriaImportador.crear(archivo.getAbsolutePath());
List<Gasto> importados = imp.importar(archivo.getAbsolutePath());
```

---

## 5.4. Ventajas en el proyecto

El Método Factoría permite:

- Centralizar la creación de importadores.
- Evitar que el controlador conozca todos los tipos concretos.
- Facilitar la incorporación de nuevos formatos.
- Reducir el acoplamiento entre la interfaz y los importadores.

Por ejemplo, para añadir un importador de Excel, se podría crear `ImportadorExcel` y añadir un caso nuevo en la factoría.

---

## 6. Patrón Adaptador

## 6.1. Idea general

El patrón **Adaptador** permite que datos o formatos externos se adapten al formato interno de la aplicación.

En este proyecto, los ficheros externos CSV o TXT tienen una estructura propia, pero la aplicación necesita trabajar con objetos `Gasto`.

Los importadores hacen de adaptadores entre el formato externo y el modelo interno.

---

## 6.2. Clases donde se aplica

Las clases relacionadas son:

- `Importador`
- `ImportadorCSV`
- `ImportadorTXT`

La interfaz `Importador` define una operación común:

```java
public interface Importador {
    List<Gasto> importar(String fichero);
}
```

Las clases concretas adaptan cada formato externo:

- `ImportadorCSV` adapta ficheros `.csv`.
- `ImportadorTXT` adapta ficheros `.txt`.

---

## 6.3. Adaptación al modelo Gasto

Los importadores leen cada línea del fichero, separan sus campos y crean objetos `Gasto`.

Por ejemplo, el formato externo contiene campos como:

```text
Date,Account,Category,Subcategory,Note,Payer,Amount,Currency
```

La aplicación transforma esos datos en un objeto interno:

```java
Gasto gasto = new Gasto(
        fecha,
        p[1].trim(),
        p[2].trim(),
        p[3].trim(),
        p[4].trim(),
        p[5].trim(),
        Double.parseDouble(p[6].trim().replace(",", ".")),
        p[7].trim()
);
```

---

## 6.4. Ventajas en el proyecto

El uso de adaptadores permite:

- Leer datos externos sin modificar el modelo interno.
- Soportar distintos formatos de importación.
- Mantener la clase `Gasto` independiente del formato de los ficheros.
- Ampliar el sistema con nuevos adaptadores en el futuro.

---

## 7. Separación FXML-Controlador

## 7.1. Idea general

Aunque no es un patrón obligatorio del enunciado, la aplicación sigue una separación similar al enfoque MVC visto en prácticas.

La interfaz visual se define en ficheros FXML y la lógica de interacción se implementa en controladores Java.

---

## 7.2. Ejemplos del proyecto

Algunos ejemplos son:

- `principal.fxml` con `PrincipalController`
- `estadisticas.fxml` con `EstadisticasController`
- `alertas.fxml` con `AlertasController`
- `cuenta_compartida.fxml` con `CuentaCompartidaController`

---

## 7.3. Ventajas

Esta organización permite:

- Separar el diseño visual de la lógica.
- Facilitar cambios en la interfaz sin tocar tanto código Java.
- Mantener cada ventana con su propio controlador.
- Mejorar la legibilidad del proyecto.

---

## 8. Uso de streams y expresiones lambda

El proyecto utiliza streams y expresiones lambda en varios puntos.

Algunos ejemplos son:

- Cálculo del total de gastos.
- Filtrado de gastos.
- Obtención de categorías distintas.
- Comprobación de duplicados al importar.
- Comprobación de gastos asociados a cuentas compartidas.

Ejemplo:

```java
double totalGeneral = gastos.stream()
        .mapToDouble(Gasto::getCantidad)
        .sum();
```

También se usan filtros encadenados en `EstadisticasController` para aplicar condiciones por mes, cuenta, categoría y fechas.

---

## 9. Resumen de patrones aplicados

| Patrón | Uso en el proyecto | Clases principales |
|---|---|---|
| Singleton | Instancia única de gestores y repositorios | `GestorGastos`, `RepositorioGastos`, `RepositorioAlertas`, `RepositorioCuentaCompartida` |
| Repositorio | Separar persistencia JSON del resto de la lógica | `RepositorioGastos`, `RepositorioAlertas`, `RepositorioHistorialAlertas` |
| Estrategia | Diferentes formas de comprobar alertas | `EstrategiaAlerta`, `AlertaMensual`, `AlertaSemanal` |
| Método Factoría | Crear importadores según el tipo de fichero | `FactoriaImportador` |
| Adaptador | Convertir ficheros externos a objetos `Gasto` | `ImportadorCSV`, `ImportadorTXT` |
| Separación FXML-Controlador | Separar vista y lógica de interacción | FXML + controladores Java |

---

## 10. Conclusión

Los patrones de diseño utilizados ayudan a mantener el proyecto organizado y facilitan su mantenimiento.

El patrón Estrategia permite ampliar el sistema de alertas.  
El Método Factoría y los adaptadores facilitan la importación de diferentes formatos.  
El patrón Repositorio desacopla la persistencia del resto de la aplicación.  
El patrón Singleton permite compartir gestores y repositorios de forma controlada.

En conjunto, estas decisiones hacen que el código sea más claro, reutilizable y fácil de ampliar.
