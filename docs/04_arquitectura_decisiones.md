# Arquitectura de la aplicación y decisiones de diseño

## 1. Introducción

La aplicación **Gestión de Gastos** se ha desarrollado como una aplicación de escritorio en JavaFX para gestionar gastos personales.

El objetivo principal de la arquitectura es separar las responsabilidades del sistema para que el código sea más claro, mantenible y fácil de ampliar.

La aplicación permite gestionar gastos desde dos interfaces diferentes:

- Interfaz gráfica desarrollada con JavaFX y FXML.
- Línea de comandos básica mediante la clase `CLI`.

Ambas interfaces trabajan sobre los mismos datos persistidos en ficheros JSON.

---

## 2. Organización general del proyecto

El proyecto se organiza en varios paquetes, cada uno con una responsabilidad concreta.

```text
umu.tds.GestionGastos
├── alertas
├── compartidas
├── controlador
├── cuentas
├── importador
├── modelo
├── persistencia
├── App.java
├── AppController.java
├── PrincipalController.java
├── EstadisticasController.java
├── AlertasController.java
├── CuentaCompartidaController.java
└── CLI.java
```

---

## 3. Capa de interfaz gráfica

La interfaz gráfica está desarrollada con **JavaFX** y se define principalmente mediante ficheros FXML.

Los ficheros FXML se encuentran en:

```text
src/main/resources/umu/tds/GestionGastos
```

Las vistas principales son:

- `app.fxml`: estructura general de la aplicación.
- `principal.fxml`: gestión principal de gastos.
- `estadisticas.fxml`: estadísticas y filtros.
- `alertas.fxml`: configuración de alertas e historial.
- `cuenta_compartida.fxml`: gestión de cuentas compartidas.

Cada vista tiene asociado un controlador Java:

- `AppController`
- `PrincipalController`
- `EstadisticasController`
- `AlertasController`
- `CuentaCompartidaController`

Esta separación permite que el diseño visual quede en los ficheros FXML y la lógica de interacción quede en las clases controladoras.

---

## 4. Controlador principal de navegación

La clase `AppController` se encarga de cambiar la vista central de la aplicación.

Para ello utiliza un `StackPane`, donde se carga dinámicamente la vista seleccionada.

Por ejemplo:

- Al pulsar **Principal**, se carga `principal.fxml`.
- Al pulsar **Estadísticas**, se carga `estadisticas.fxml`.
- Al pulsar **Alertas**, se carga `alertas.fxml`.
- Al pulsar **Cuentas compartidas**, se carga `cuenta_compartida.fxml`.

Esta decisión permite tener una única ventana principal y cambiar solo el contenido central, evitando abrir muchas ventanas diferentes.

---

## 5. Modelo de dominio

El modelo principal está representado por la clase `Gasto`.

Un gasto contiene la información necesaria para registrar una operación:

- Fecha y hora.
- Cuenta.
- Categoría.
- Subcategoría.
- Descripción.
- Pagador.
- Cantidad.
- Moneda.

También existen clases relacionadas con alertas y cuentas compartidas.

En el paquete `compartidas` se encuentran las clases principales para gestionar cuentas compartidas:

- `CuentaCompartida`
- `PersonaReparto`
- `FilaSaldo`
- `RepositorioCuentaCompartida`

La clase `CuentaCompartida` mantiene la lista de personas, los saldos y los porcentajes de reparto.

---

## 6. Persistencia de datos

La persistencia se realiza mediante ficheros JSON usando la librería **Jackson**.

Los datos principales se guardan en los siguientes ficheros:

```text
gastos.json
alertas.json
historial_alertas.json
cuentas_compartidas.json
```

Para evitar mezclar la lógica de negocio con el acceso a ficheros, se usa el patrón **Repositorio**.

Los repositorios principales son:

- `RepositorioGastos`
- `RepositorioAlertas`
- `RepositorioHistorialAlertas`
- `RepositorioCuentaCompartida`

Cada repositorio se encarga de guardar y cargar un tipo concreto de información.

Esta decisión mejora el desacoplamiento, porque el resto de la aplicación no necesita saber cómo se escriben o leen los ficheros JSON.

---

## 7. Gestión de gastos

La gestión de gastos se centraliza en la clase `GestorGastos`.

Esta clase mantiene una lista de gastos en memoria y ofrece operaciones básicas como:

- Añadir gasto.
- Eliminar gasto.
- Obtener lista de gastos.
- Calcular total gastado.
- Filtrar por cantidad.

La clase se implementa como **Singleton** porque se necesita una instancia global compartida durante la ejecución de la aplicación.

---

## 8. Interfaz principal

La ventana principal permite:

- Añadir gastos manualmente.
- Consultar gastos guardados.
- Editar algunos campos directamente desde la tabla.
- Eliminar gastos.
- Importar gastos desde ficheros.
- Guardar gastos importados.
- Exportar gastos a CSV.

Una decisión importante es que los gastos importados no se guardan directamente. Primero se muestran en una tabla de gastos pendientes. Así el usuario puede revisarlos antes de incorporarlos definitivamente a la aplicación.

---

## 9. Importación de datos

La importación de gastos externos se realiza mediante el paquete `importador`.

Las clases principales son:

- `Importador`
- `ImportadorCSV`
- `ImportadorTXT`
- `FactoriaImportador`

La aplicación soporta importación desde ficheros CSV y TXT.

La clase `FactoriaImportador` decide qué importador crear según la extensión del fichero.

Esta decisión permite ampliar fácilmente el sistema en el futuro. Si se quisiera añadir otro formato, bastaría con crear otro importador e incorporarlo a la factoría.

---

## 10. Estadísticas y filtros

La clase `EstadisticasController` se encarga de calcular y mostrar la información estadística.

La vista de estadísticas incluye:

- Total gastado.
- Número de gastos.
- Gasto medio.
- Gráfico circular por categorías.
- Gráfico de barras por categorías.
- Tabla resumen por categoría.

También permite aplicar filtros por:

- Mes.
- Cuenta.
- Categoría.
- Fecha inicial.
- Fecha final.

Los filtros se aplican usando streams de Java, lo que permite expresar las operaciones de forma clara y compacta.

---

## 11. Sistema de alertas

El sistema de alertas permite crear límites de gasto semanales o mensuales.

Las alertas pueden aplicarse:

- A todos los gastos.
- A una categoría concreta.

El paquete `alertas` contiene las clases principales:

- `EstrategiaAlerta`
- `AlertaMensual`
- `AlertaSemanal`
- `GestorAlertas`
- `Notificacion`
- `AlertaConfig`
- `RepositorioAlertas`
- `RepositorioHistorialAlertas`

El diseño utiliza el patrón **Estrategia**, ya que una alerta mensual y una alerta semanal tienen formas diferentes de comprobar si se ha superado el límite.

---

## 12. Cuentas compartidas

La aplicación permite crear cuentas compartidas formadas por varias personas.

Cuando se añade un gasto a una cuenta compartida, el sistema actualiza los saldos de las personas.

Existen dos formas de reparto:

- Reparto equitativo.
- Reparto por porcentajes personalizados.

Por defecto, si no hay porcentajes configurados, el gasto se reparte de forma equitativa.

Si el usuario configura porcentajes, la suma debe ser 100%.

Una decisión importante es que una vez creada una cuenta compartida, la lista de personas no se puede modificar. Esto se hace para cumplir el enunciado y evitar inconsistencias en los saldos.

---

## 13. Restricciones sobre gastos compartidos

Para evitar inconsistencias, la aplicación limita la edición de algunos campos en los gastos de cuentas compartidas.

En concreto, no se permite modificar directamente:

- Cuenta.
- Pagador.
- Cantidad.

Estos campos afectan directamente al cálculo de saldos.

Si se elimina un gasto asociado a una cuenta compartida, la aplicación recalcula los saldos desde cero usando los gastos restantes.

Esta decisión evita errores acumulados en los saldos.

---

## 14. Línea de comandos

Además de la interfaz gráfica, el proyecto incluye una línea de comandos mediante la clase `CLI`.

La línea de comandos permite:

- Listar gastos.
- Añadir gastos.
- Modificar gastos.
- Borrar gastos.

La clase `CLI` usa los mismos ficheros JSON que la interfaz gráfica. Por tanto, los cambios realizados desde consola también aparecen en la aplicación gráfica.



---

## 15. Estilo visual

La aplicación utiliza una hoja CSS llamada `estilos.css`.

Esta hoja permite separar el diseño visual de la lógica del programa.

Se han definido estilos para:

- Botones principales.
- Botones secundarios.
- Botones de peligro.
- Paneles.
- Tablas.
- Títulos y subtítulos.

También se ha añadido un logo a la aplicación para mejorar la presentación visual.

---

## 16. Decisiones de diseño importantes

Las principales decisiones de diseño tomadas han sido:

1. Separar la interfaz gráfica en varios ficheros FXML.
2. Usar controladores específicos para cada vista.
3. Usar repositorios para la persistencia.
4. Guardar los datos en JSON mediante Jackson.
5. Centralizar los gastos en `GestorGastos`.
6. Usar una factoría para crear importadores.
7. Usar estrategias para las alertas.
8. Recalcular saldos de cuentas compartidas cuando se eliminan gastos.
9. Evitar modificar datos sensibles de gastos compartidos.
10. Usar una línea de comandos que comparte los mismos datos que la interfaz gráfica.

---

## 17. Conclusión

La arquitectura de la aplicación busca mantener una separación clara entre interfaz, lógica de negocio y persistencia.

Esta organización facilita el mantenimiento del código y permite ampliar el proyecto en el futuro, por ejemplo añadiendo nuevos formatos de importación, nuevos tipos de alertas o nuevas visualizaciones estadísticas.
