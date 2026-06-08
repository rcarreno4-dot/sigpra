SET LINESIZE 220
SET PAGESIZE 100

PROMPT 1) PROCEDIMIENTOS VALIDOS
COLUMN object_name FORMAT A35
COLUMN object_type FORMAT A18
COLUMN status FORMAT A10
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type = 'PROCEDURE'
ORDER BY object_name;

PROMPT 2) CODIGO DE SP_REGISTRAR_ACTIVIDAD
COLUMN text FORMAT A180
SELECT line, text
FROM user_source
WHERE name = 'SP_REGISTRAR_ACTIVIDAD'
ORDER BY line;

PROMPT 3) ROLES SIGPRA
COLUMN role FORMAT A35
SELECT role
FROM dba_roles
WHERE role LIKE 'ROL_SIGPRA%'
ORDER BY role;

PROMPT 4) USUARIOS SIGPRA
COLUMN username FORMAT A30
COLUMN account_status FORMAT A20
SELECT username, account_status
FROM dba_users
WHERE username LIKE 'U_SIGPRA%'
ORDER BY username;

PROMPT 5) ROLES ASIGNADOS A USUARIOS
COLUMN grantee FORMAT A30
COLUMN granted_role FORMAT A30
SELECT grantee, granted_role
FROM dba_role_privs
WHERE grantee LIKE 'U_SIGPRA%'
ORDER BY grantee;

PROMPT 6) PRIVILEGIOS POR ROL
COLUMN table_name FORMAT A35
COLUMN privilege FORMAT A20
SELECT grantee, table_name, privilege
FROM dba_tab_privs
WHERE grantee LIKE 'ROL_SIGPRA%'
  AND owner = 'PRACTICAS_APP'
ORDER BY grantee, table_name, privilege;

PROMPT 7) TRIGGERS VALIDOS
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type = 'TRIGGER'
ORDER BY object_name;

