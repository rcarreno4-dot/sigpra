# Anexo - Montaje y Pruebas de Requisitos de Base de Datos (CRUD)

## 1. Objetivo

Validar el cumplimiento del requisito de pruebas CRUD (Create, Read, Update y Delete) sobre una tabla basica del modelo de datos del sistema SIGPRA, verificando el comportamiento esperado de insercion, consulta, actualizacion y eliminacion de registros.

## 2. Alcance

La validacion se realiza sobre la tabla `usuario` en ambiente Oracle, utilizando el script `CRUD_BD_USUARIO.sql`, como evidencia tecnica del funcionamiento basico de las operaciones sobre la base de datos.

## 3. Recursos utilizados

- Motor de base de datos: Oracle Database.
- Cliente de ejecucion: Oracle SQL Developer.
- Script de esquema:
  - Desde la raiz del workspace: `sigpra/gestion-practicas-desktop/sql/01_schema_oracle.sql`.
  - Desde esta carpeta del proyecto: `../../gestion-practicas-desktop/sql/01_schema_oracle.sql`.
- Script de prueba CRUD: `sigpra/codigo/PROYECTO_VISTAS_NETBEANS_ANT/CRUD_BD_USUARIO.sql`.

## 4. Procedimiento de prueba

1. Ejecutar el script de estructura de base de datos (`01_schema_oracle.sql`).
2. Verificar disponibilidad de la tabla `usuario`.
3. Ejecutar el script `CRUD_BD_USUARIO.sql`.
4. Registrar capturas de pantalla de los resultados para cada bloque:
   - CREATE
   - READ
   - UPDATE
   - DELETE

## 5. Evidencia de operaciones CRUD

### 5.1 CREATE

Se inserta un usuario de prueba con correo `crud.prueba@udi.edu.co`.  
Resultado esperado: insercion exitosa del registro.

### 5.2 READ

Se consulta el usuario insertado por correo.  
Resultado esperado: retorno de una fila con los datos registrados.

### 5.3 UPDATE

Se actualiza el campo `nombres` del usuario de prueba a `CRUD Actualizado`.  
Resultado esperado: modificacion aplicada correctamente.

### 5.4 DELETE

Se elimina el usuario de prueba por correo.  
Resultado esperado: registro eliminado y verificacion final con `COUNT(*) = 0`.

## 6. Resultado general

Las operaciones CRUD basicas sobre la tabla `usuario` se ejecutan de forma correcta, cumpliendo el requisito de montaje y pruebas de base de datos establecido para el entregable.

## 7. Conclusion

Con la ejecucion del script de prueba y las evidencias adjuntas, se demuestra el cumplimiento del requisito funcional de persistencia de datos para las operaciones fundamentales de creacion, consulta, actualizacion y eliminacion en la base de datos del proyecto.

## 8. Anexo de capturas (para completar)

- Captura CREATE: `[INSERTAR_IMAGEN]`
- Captura READ: `[INSERTAR_IMAGEN]`
- Captura UPDATE: `[INSERTAR_IMAGEN]`
- Captura DELETE: `[INSERTAR_IMAGEN]`
