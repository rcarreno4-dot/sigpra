-- SIGPRA - Prueba CRUD basica sobre tabla USUARIO
-- Requisito: Montaje y pruebas CRUD (Create, Read, Update, Delete)
-- Nota: Ejecutar luego del script de esquema Oracle.

SET DEFINE OFF;

-- Limpieza preventiva para poder re-ejecutar la prueba
DELETE FROM usuario
WHERE correo = 'crud.prueba@udi.edu.co';

COMMIT;

-- =========================================================
-- CREATE
-- =========================================================
INSERT INTO usuario (
    id_usuario,
    nombres,
    apellidos,
    correo,
    identificacion,
    hash_password,
    rol,
    estado
) VALUES (
    NULL,
    'CRUD',
    'Prueba',
    'crud.prueba@udi.edu.co',
    'CRU9001',
    'hash_demo_123',
    'ESTUDIANTE',
    'ACTIVO'
);

COMMIT;

-- =========================================================
-- READ
-- =========================================================
SELECT
    id_usuario,
    nombres,
    apellidos,
    correo,
    identificacion,
    rol,
    estado
FROM usuario
WHERE correo = 'crud.prueba@udi.edu.co';

-- =========================================================
-- UPDATE
-- =========================================================
UPDATE usuario
SET nombres = 'CRUD Actualizado'
WHERE correo = 'crud.prueba@udi.edu.co';

COMMIT;

SELECT
    id_usuario,
    nombres,
    apellidos,
    correo
FROM usuario
WHERE correo = 'crud.prueba@udi.edu.co';

-- =========================================================
-- DELETE
-- =========================================================
DELETE FROM usuario
WHERE correo = 'crud.prueba@udi.edu.co';

COMMIT;

SELECT COUNT(*) AS registros_restantes
FROM usuario
WHERE correo = 'crud.prueba@udi.edu.co';
