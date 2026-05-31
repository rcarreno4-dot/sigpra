BASE DE DATOS 3 ENTREGA - SIGPRA (ORACLE XE)
Fecha: 2026-05-25

Contenido de la carpeta
1) TERCERA_ENTREGA_SQL_COMPLETA.sql
2) CHECKLIST_TERCERA_ENTREGA_BD.md
3) SECCION_2_NUEVO_COMPLETA.txt
4) TablaBaseDatosTerceraEntrega_diligenciada.xlsx
5) 01_crear_esquema_oracle.sql
6) 02_datos_iniciales.sql
7) 03_consultas_utiles.sql
8) 04_ajustes_reunion_20260310.sql
9) crear_administrador_oracle.sql
10) resumen_administrador_dba.txt

Orden sugerido de ejecucion
1. Ejecutar esquema base:
   @01_crear_esquema_oracle.sql

2. Cargar datos iniciales:
   @02_datos_iniciales.sql

3. Aplicar ajustes de tablas/triggers:
   @04_ajustes_reunion_20260310.sql

4. Ejecutar script completo de entrega 3 (roles, perfiles, grants, procedimientos, triggers y reportes):
   @TERCERA_ENTREGA_SQL_COMPLETA.sql

5. Crear/actualizar administrador DBA:
   @crear_administrador_oracle.sql
   Nota: incluye manejo de ORA-28007 (reuso de clave).

6. Consultar reportes:
   @03_consultas_utiles.sql

Administrador DBA definido
- Usuario: ADMINISTRADOR
- Clave: administrador2026
- Perfil: PERFIL_DBA_UNICO
- Rol: DBA

Comandos de acceso
- Como SYSDBA:
  CONNECT sys/<clave_sys>@XE AS SYSDBA

- Como ADMINISTRADOR:
  CONNECT administrador/administrador2026@XE

Verificacion rapida
SELECT username, account_status, profile
FROM dba_users
WHERE username='ADMINISTRADOR';

SELECT granted_role
FROM dba_role_privs
WHERE grantee='ADMINISTRADOR';
