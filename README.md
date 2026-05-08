# Gestión de Gastos

Proyecto desarrollado para la asignatura **Tecnologías de Desarrollo de Software** de la Facultad de Informática de la Universidad de Murcia.

## Integrantes

- Nombre: Mohamed Abdelali Benmahdjoub
- Email: ma.benmahdjoub@um.es
- Grupo y subgrupo de prácticas: 1.1

## Descripción del proyecto

Gestión de Gastos es una aplicación de escritorio desarrollada en JavaFX para registrar, consultar, modificar y borrar gastos personales.

La aplicación permite organizar los gastos por fecha, cuenta, categoría, subcategoría, descripción, pagador, cantidad y moneda. Además, incluye persistencia en formato JSON, importación y exportación de datos, estadísticas visuales, alertas configurables y cuentas de gasto compartidas.

## Funcionalidades principales

- Registro manual de gastos.
- Edición y eliminación de gastos.
- Persistencia de datos en JSON mediante Jackson.
- Importación de gastos desde ficheros CSV y TXT.
- Exportación de gastos a CSV.
- Visualización de estadísticas mediante gráficos circulares y de barras.
- Filtros por mes, cuenta, categoría e intervalo de fechas.
- Sistema de alertas semanales y mensuales.
- Historial de notificaciones.
- Gestión de cuentas compartidas.
- Reparto equitativo y reparto por porcentajes.
- Línea de comandos básica para registrar, modificar y borrar gastos.

## Tecnologías utilizadas

- Java
- JavaFX
- Maven
- Jackson
- Git
- GitHub

## Ejecución del proyecto

Para ejecutar la aplicación gráfica desde Maven:

```bash
mvn javafx:run
```

Para ejecutar la versión de línea de comandos desde PowerShell:

```bash
mvn exec:java "-Dexec.mainClass=umu.tds.GestionGastos.CLI"
```

## Documentación

- [Historias de usuario](docs/01_historias_usuario.md)
- [Diagrama de clases](docs/02_diagrama_clases.md)
- [Diagrama de interacción](docs/03_diagrama_interaccion.md)
- [Arquitectura y decisiones](docs/04_arquitectura_decisiones.md)
- [Patrones de diseño](docs/05_patrones_diseno.md)
- [Manual de usuario](docs/06_manual_usuario.md)