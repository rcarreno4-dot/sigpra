# Guia de sustentacion: procedimientos almacenados y roles/usuarios en Oracle

## Objetivo de la seccion

En esta parte se demuestra que SIGPRA no solo tiene interfaz grafica, sino tambien control de seguridad y logica de negocio en Oracle mediante:

- Usuarios de base de datos.
- Roles por perfil funcional.
- Privilegios sobre tablas y procedimientos.
- Procedimientos almacenados compilados y ejecutables.
- Triggers de autoincremento y soporte operativo.

## Conexion para la demo

```sql
sqlplus PRACTICAS_APP/Practicas2026@//localhost:1521/XE
```

## 1. Mostrar procedimientos almacenados

Consulta:

```sql
SET LINESIZE 220
COLUMN object_name FORMAT A35
COLUMN object_type FORMAT A18
COLUMN status FORMAT A10

SELECT object_name, object_type, status
FROM user_objects
WHERE object_type = 'PROCEDURE'
ORDER BY object_name;
```

Resultado esperado:

```text
SP_CERRAR_PRACTICA              PROCEDURE   VALID
SP_CREAR_PLANTILLA_BITACORA     PROCEDURE   VALID
SP_REGISTRAR_ACTIVIDAD          PROCEDURE   VALID
SP_REGISTRAR_EVIDENCIA          PROCEDURE   VALID
SP_VALIDAR_ACTIVIDAD            PROCEDURE   VALID
```

Que decir:

> Estos procedimientos almacenados centralizan reglas de negocio en Oracle. Por ejemplo, registrar una actividad no solo inserta una bitacora, tambien actualiza las horas acumuladas de la practica. Validar una actividad guarda el estado, la observacion del docente y la fecha de validacion. Esto evita duplicar reglas en la aplicacion.

## 2. Explicar cada procedimiento

| Procedimiento | Rol principal | Funcion |
|---|---|---|
| `SP_REGISTRAR_ACTIVIDAD` | Estudiante | Registra una actividad en bitacora y suma horas a la practica. |
| `SP_VALIDAR_ACTIVIDAD` | Docente | Cambia el estado de una actividad y registra observacion docente. |
| `SP_REGISTRAR_EVIDENCIA` | Estudiante | Registra una evidencia asociada a una actividad. |
| `SP_CERRAR_PRACTICA` | Director | Cambia el estado final de una practica. |
| `SP_CREAR_PLANTILLA_BITACORA` | Director | Crea una plantilla de bitacora por semestre/modalidad. |

## 3. Mostrar codigo de un procedimiento

Consulta:

```sql
SET LINESIZE 220
COLUMN text FORMAT A180

SELECT line, text
FROM user_source
WHERE name = 'SP_REGISTRAR_ACTIVIDAD'
ORDER BY line;
```

Que decir:

> Este procedimiento recibe la practica, fecha, descripcion y horas. Inserta la actividad en `BITACORA`, retorna el ID generado y actualiza `PRACTICA.HORAS_ACUMULADAS`. Si se cumple el objetivo de horas, deja la practica lista para aprobacion.

## 4. Ejecutar prueba controlada de procedimiento

Esta prueba ejecuta un procedimiento con el usuario estudiante y luego hace `ROLLBACK` para no alterar datos definitivos.

```sql
CONNECT U_SIGPRA_EST/"Sigpra2026*Est"@//localhost:1521/XE

VAR v_id NUMBER

EXEC PRACTICAS_APP.SP_REGISTRAR_ACTIVIDAD(
  8,
  SYSDATE,
  'Prueba sustentacion',
  'Actividad registrada desde procedimiento almacenado',
  1,
  :v_id
);

PRINT v_id
ROLLBACK;
```

Resultado esperado:

```text
Procedimiento PL/SQL terminado correctamente.
V_ID
-----
500xx
Rollback terminado.
```

Que decir:

> Aqui se ve que un usuario con rol estudiante puede ejecutar el procedimiento autorizado sin acceder directamente como administrador del esquema.

## 5. Mostrar roles creados

Consulta:

```sql
CONNECT PRACTICAS_APP/Practicas2026@//localhost:1521/XE

COLUMN role FORMAT A35

SELECT role
FROM dba_roles
WHERE role LIKE 'ROL_SIGPRA%'
ORDER BY role;
```

Resultado esperado:

```text
ROL_SIGPRA_AUDITOR
ROL_SIGPRA_DIRECTOR
ROL_SIGPRA_DOCENTE
ROL_SIGPRA_ESTUDIANTE
```

Que decir:

> Se separaron permisos por perfil. El estudiante puede registrar bitacora y evidencias; el docente puede validar; el director administra cierres, plantillas y asignaciones; el auditor solo consulta.

## 6. Mostrar usuarios de base de datos

Consulta:

```sql
COLUMN username FORMAT A30
COLUMN account_status FORMAT A20

SELECT username, account_status
FROM dba_users
WHERE username LIKE 'U_SIGPRA%'
ORDER BY username;
```

Resultado esperado:

```text
U_SIGPRA_AUD   OPEN
U_SIGPRA_DIR   OPEN
U_SIGPRA_DOC   OPEN
U_SIGPRA_EST   OPEN
```

## 7. Mostrar asignacion de roles a usuarios

Consulta:

```sql
COLUMN grantee FORMAT A30
COLUMN granted_role FORMAT A30

SELECT grantee, granted_role
FROM dba_role_privs
WHERE grantee LIKE 'U_SIGPRA%'
ORDER BY grantee;
```

Resultado esperado:

```text
U_SIGPRA_AUD   ROL_SIGPRA_AUDITOR
U_SIGPRA_DIR   ROL_SIGPRA_DIRECTOR
U_SIGPRA_DOC   ROL_SIGPRA_DOCENTE
U_SIGPRA_EST   ROL_SIGPRA_ESTUDIANTE
```

## 8. Mostrar privilegios por rol

Consulta:

```sql
COLUMN grantee FORMAT A30
COLUMN table_name FORMAT A35
COLUMN privilege FORMAT A20

SELECT grantee, table_name, privilege
FROM dba_tab_privs
WHERE grantee LIKE 'ROL_SIGPRA%'
  AND owner = 'PRACTICAS_APP'
ORDER BY grantee, table_name, privilege;
```

Que decir:

> Esta consulta evidencia el principio de minimo privilegio: cada rol recibe solo las operaciones que necesita. Por ejemplo, `ROL_SIGPRA_ESTUDIANTE` tiene `INSERT` sobre `BITACORA` y `EVIDENCIA`, pero no permisos administrativos de director.

## 9. Mostrar triggers validos

Consulta:

```sql
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type = 'TRIGGER'
ORDER BY object_name;
```

Que decir:

> Los triggers se usan para autoincrementar llaves primarias mediante secuencias y mantener consistencia operativa.

## Cierre recomendado

> En conclusion, SIGPRA usa Oracle para reforzar seguridad y reglas de negocio. La interfaz permite operar el sistema, pero la base de datos protege los datos mediante roles, usuarios, privilegios y procedimientos almacenados que encapsulan acciones clave del proceso de practica academica.

