# Anexo Resumido (1 Pagina) - Evidencia CRUD en Base de Datos

## Objetivo

Comprobar el cumplimiento del requisito de pruebas CRUD (Create, Read, Update, Delete) en la base de datos del proyecto SIGPRA, utilizando Oracle y la tabla `usuario`.

## Recurso ejecutado

- Script: `CRUD_BD_USUARIO.sql`
- Tabla evaluada: `usuario`
- Entorno: Oracle SQL Developer

## Procedimiento

1. Ejecutar el esquema general de BD (`01_schema_oracle.sql`).
2. Ejecutar `CRUD_BD_USUARIO.sql`.
3. Validar resultados en cada bloque CRUD.

## Resultados esperados

- CREATE: insercion correcta de usuario de prueba (`crud.prueba@udi.edu.co`).
- READ: consulta retorna el registro insertado.
- UPDATE: actualizacion correcta del campo `nombres`.
- DELETE: eliminacion correcta y verificacion final con `COUNT(*) = 0`.

## Conclusion

La prueba confirma el funcionamiento correcto de las operaciones CRUD basicas en la tabla `usuario`, cumpliendo el requisito de montaje y pruebas de base de datos del entregable.

## Evidencias (capturas)

- CREATE: `[INSERTAR_CAPTURA]`
- READ: `[INSERTAR_CAPTURA]`
- UPDATE: `[INSERTAR_CAPTURA]`
- DELETE: `[INSERTAR_CAPTURA]`
