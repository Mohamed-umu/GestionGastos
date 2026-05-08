# Historias de usuario

Este documento recoge las principales historias de usuario del proyecto **Gestión de Gastos**.  
Las historias describen las funcionalidades principales de la aplicación desde el punto de vista del usuario.

---

## HU-01. Registrar un gasto manualmente

**Como** usuario de la aplicación,  
**quiero** registrar un gasto indicando fecha, cuenta, categoría, subcategoría, descripción, pagador, cantidad y moneda,  
**para** poder llevar un control de mis gastos personales.

### Criterios de aceptación

- El usuario puede introducir los datos de un gasto desde la ventana principal.
- La fecha, cuenta, categoría, pagador y cantidad son campos obligatorios.
- El gasto se guarda en la tabla de gastos guardados.
- El gasto se persiste en el fichero JSON correspondiente.
- Si los datos son incorrectos, se muestra un mensaje de error.

---

## HU-02. Consultar los gastos registrados

**Como** usuario,  
**quiero** ver una tabla con todos los gastos registrados,  
**para** poder revisar fácilmente mi historial de gastos.

### Criterios de aceptación

- La aplicación muestra los gastos en formato de tabla.
- Cada gasto muestra fecha, cuenta, categoría, subcategoría, descripción, pagador, cantidad y moneda.
- Los gastos se muestran ordenados de más reciente a más antiguo.
- Al reiniciar la aplicación, los gastos guardados vuelven a cargarse desde JSON.

---

## HU-03. Modificar un gasto guardado

**Como** usuario,  
**quiero** poder modificar los datos de un gasto guardado,  
**para** corregir errores o actualizar información.

### Criterios de aceptación

- El usuario puede editar directamente algunos campos desde la tabla.
- Los cambios realizados se guardan automáticamente.
- Los gastos de cuentas compartidas tienen restricciones para evitar inconsistencias en los saldos.
- No se permite modificar la cuenta, el pagador o la cantidad de un gasto compartido si afecta a los saldos.

---

## HU-04. Eliminar un gasto guardado

**Como** usuario,  
**quiero** eliminar un gasto registrado,  
**para** quitar gastos incorrectos o que ya no quiero conservar.

### Criterios de aceptación

- El usuario puede seleccionar un gasto guardado y eliminarlo.
- El gasto desaparece de la tabla principal.
- El fichero JSON de gastos se actualiza.
- Si el gasto pertenece a una cuenta compartida, los saldos se recalculan.

---

## HU-05. Importar gastos desde un fichero externo

**Como** usuario,  
**quiero** importar gastos desde un fichero CSV o TXT,  
**para** incorporar datos procedentes de una fuente externa, como una plataforma bancaria.

### Criterios de aceptación

- El usuario puede seleccionar un fichero desde la interfaz gráfica.
- La aplicación reconoce ficheros CSV y TXT.
- Los gastos importados aparecen primero en una tabla de pendientes.
- Los gastos duplicados no se añaden de nuevo.
- El usuario puede revisar los gastos importados antes de guardarlos definitivamente.

---

## HU-06. Guardar gastos importados

**Como** usuario,  
**quiero** guardar los gastos importados después de revisarlos,  
**para** incorporarlos definitivamente a mi aplicación.

### Criterios de aceptación

- El usuario puede guardar todos los gastos importados pendientes.
- Los gastos pasan a la tabla principal de gastos guardados.
- Los datos se persisten en JSON.
- Si algún gasto pertenece a una cuenta compartida, se actualizan los saldos correspondientes.

---

## HU-07. Exportar gastos a CSV

**Como** usuario,  
**quiero** exportar mis gastos a un fichero CSV,  
**para** poder conservar una copia externa o abrir los datos con otras herramientas.

### Criterios de aceptación

- El usuario puede elegir dónde guardar el fichero CSV.
- El fichero exportado contiene una cabecera.
- Cada gasto se escribe con fecha, cuenta, categoría, subcategoría, nota, pagador, cantidad y moneda.
- Si el usuario no escribe la extensión `.csv`, la aplicación la añade automáticamente.

---

## HU-08. Consultar estadísticas de gastos

**Como** usuario,  
**quiero** ver estadísticas de mis gastos,  
**para** entender mejor cómo se distribuye mi dinero.

### Criterios de aceptación

- La aplicación muestra el total gastado.
- La aplicación muestra el número de gastos.
- La aplicación muestra el gasto medio.
- Se incluye un gráfico circular por categorías.
- Se incluye un gráfico de barras por categorías.
- Se incluye una tabla resumen por categoría.

---

## HU-09. Filtrar los gastos

**Como** usuario,  
**quiero** filtrar los gastos por mes, cuenta, categoría o intervalo de fechas,  
**para** consultar solo la información que me interesa.

### Criterios de aceptación

- El usuario puede filtrar por mes.
- El usuario puede filtrar por cuenta.
- El usuario puede filtrar por categoría.
- El usuario puede filtrar por fecha inicial y fecha final.
- Los filtros afectan a los gráficos y a la tabla resumen.
- El usuario puede limpiar los filtros y volver a ver todos los gastos.

---

## HU-10. Crear alertas de gasto

**Como** usuario,  
**quiero** crear alertas semanales o mensuales con un límite de gasto,  
**para** recibir avisos cuando supere una cantidad determinada.

### Criterios de aceptación

- El usuario puede elegir si la alerta es semanal o mensual.
- El usuario puede introducir un límite económico.
- La categoría es opcional.
- Si no se indica categoría, la alerta se aplica a todos los gastos.
- La alerta se guarda en JSON.
- Si el límite ya está superado, se genera una notificación.

---

## HU-11. Consultar historial de notificaciones

**Como** usuario,  
**quiero** consultar las notificaciones generadas por las alertas,  
**para** revisar avisos anteriores.

### Criterios de aceptación

- La aplicación muestra una tabla con las notificaciones.
- Cada notificación muestra fecha y mensaje.
- El historial se guarda en JSON.
- El usuario puede refrescar el historial.
- El usuario puede limpiar el historial.

---

## HU-12. Eliminar alertas configuradas

**Como** usuario,  
**quiero** eliminar alertas que ya no necesito,  
**para** dejar de recibir avisos de límites antiguos.

### Criterios de aceptación

- El usuario puede seleccionar una alerta activa.
- El usuario puede eliminar la alerta seleccionada.
- La tabla de alertas se actualiza.
- El fichero JSON de alertas se actualiza.

---

## HU-13. Crear una cuenta compartida

**Como** usuario,  
**quiero** crear una cuenta compartida con varias personas,  
**para** registrar gastos comunes y calcular saldos pendientes.

### Criterios de aceptación

- El usuario puede introducir el nombre de la cuenta.
- El usuario puede añadir personas antes de crear la cuenta.
- La cuenta debe tener al menos dos personas.
- No se pueden crear dos cuentas con el mismo nombre.
- La cuenta se guarda en JSON.
- Una vez creada, la lista de personas no puede modificarse.

---

## HU-14. Registrar gastos en una cuenta compartida

**Como** usuario,  
**quiero** asociar un gasto a una cuenta compartida y a un pagador,  
**para** calcular cuánto debe o cuánto le deben a cada persona.

### Criterios de aceptación

- Al seleccionar una cuenta compartida, el pagador se elige entre las personas de esa cuenta.
- Al guardar el gasto, se actualizan los saldos.
- Si no hay porcentajes configurados, el reparto es equitativo.
- Si hay porcentajes configurados, se aplica el reparto personalizado.
- El pagador debe pertenecer a la cuenta compartida.

---

## HU-15. Configurar porcentajes en una cuenta compartida

**Como** usuario,  
**quiero** configurar el porcentaje de gasto que asume cada persona,  
**para** permitir repartos no equitativos.

### Criterios de aceptación

- El usuario puede editar los porcentajes de las personas.
- La suma de los porcentajes debe ser 100%.
- Si la suma no es 100%, se muestra un error.
- Los porcentajes se guardan en JSON.
- Los gastos posteriores se calculan usando esos porcentajes.

---

## HU-16. Consultar saldos de una cuenta compartida

**Como** usuario,  
**quiero** consultar los saldos de una cuenta compartida,  
**para** saber quién debe dinero y a quién le deben dinero.

### Criterios de aceptación

- La aplicación muestra una tabla de saldos.
- Cada fila muestra una persona y su saldo.
- Un saldo positivo indica que a esa persona le deben dinero.
- Un saldo negativo indica que esa persona debe dinero.
- Los saldos se actualizan al añadir o eliminar gastos asociados.

---

## HU-17. Usar la línea de comandos

**Como** usuario,  
**quiero** poder registrar, modificar, listar y borrar gastos desde una línea de comandos,  
**para** gestionar gastos sin usar la interfaz gráfica.

### Criterios de aceptación

- La aplicación ofrece un menú por consola.
- El usuario puede listar gastos.
- El usuario puede añadir gastos.
- El usuario puede modificar gastos.
- El usuario puede borrar gastos.
- Los cambios realizados desde consola se guardan en los mismos ficheros JSON que usa la interfaz gráfica.
- Si se modifican gastos de cuentas compartidas, los saldos se recalculan.

---

## Resumen

Las historias de usuario anteriores cubren las funcionalidades principales solicitadas en el enunciado:

- Gestión completa de gastos.
- Persistencia de información.
- Interfaz gráfica.
- Línea de comandos.
- Estadísticas y filtros.
- Alertas configurables.
- Historial de notificaciones.
- Cuentas compartidas.
- Importación y exportación de datos.