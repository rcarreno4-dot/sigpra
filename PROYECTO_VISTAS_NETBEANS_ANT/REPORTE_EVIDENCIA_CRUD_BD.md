# Reporte de Evidencia CRUD - Base de Datos (Oracle)

Proyecto: `PROYECTO_VISTAS_NETBEANS_ANT`  
Fecha de ejecucion: `____/____/______`  
Responsable: `____________________`

## Objetivo

Evidenciar el cumplimiento del requisito de montaje y pruebas CRUD (Create, Read, Update, Delete) sobre tablas basicas de la base de datos.

## Script ejecutado

- `CRUD_BD_USUARIO.sql`

## Tabla validada

- `usuario`

## Evidencias por operacion

### 1) CREATE (Insertar)

Consulta ejecutada: `INSERT INTO usuario (...) VALUES (...);`

Resultado esperado:
- Registro insertado correctamente.

Evidencia (captura):
- `[PEGAR_CAPTURA_CREATE_AQUI]`

### 2) READ (Consultar)

Consulta ejecutada: `SELECT ... FROM usuario WHERE correo = 'crud.prueba@udi.edu.co';`

Resultado esperado:
- Retorna 1 fila con el usuario de prueba.

Evidencia (captura):
- `[PEGAR_CAPTURA_READ_AQUI]`

### 3) UPDATE (Actualizar)

Consulta ejecutada: `UPDATE usuario SET nombres = 'CRUD Actualizado' ...;`

Resultado esperado:
- Nombre actualizado en el registro.

Evidencia (captura):
- `[PEGAR_CAPTURA_UPDATE_AQUI]`

### 4) DELETE (Eliminar)

Consulta ejecutada: `DELETE FROM usuario WHERE correo = 'crud.prueba@udi.edu.co';`

Resultado esperado:
- Registro eliminado.
- Verificacion final con `COUNT(*) = 0`.

Evidencia (captura):
- `[PEGAR_CAPTURA_DELETE_AQUI]`

## Conclusion

Estado final de la prueba CRUD: `APROBADO / NO APROBADO`  
Observaciones: `________________________________________________________`
