# Base de Datos SIGPRA - Creacion, Estructura y Registros

## 1. Objetivo
Este documento resume toda la configuracion de base de datos del proyecto SIGPRA (version reducida por gestion de horas), desde la creacion del esquema en Oracle hasta la carga de registros iniciales.

## 2. Archivos fuente usados
- Esquema reducido oficial: `gestion-practicas-desktop/sql/01_schema_oracle_reducido_horas.sql`
- Script de usuario Oracle: `fix_usuario_practicas_app.sql`
- Registros de referencia (director/docente): `directores_y_docentes_bd.txt`

## 3. Motor y esquema
- Motor: Oracle (compatible con Oracle XE 10g/11g)
- Esquema de aplicacion: `PRACTICAS_APP`

## 4. Creacion del usuario/esquema en Oracle
Ejecutar como `SYSTEM` o `SYS`:

```sql
-- Archivo: fix_usuario_practicas_app.sql
CREATE USER PRACTICAS_APP IDENTIFIED BY Practicas2026;
ALTER USER PRACTICAS_APP IDENTIFIED BY Practicas2026 ACCOUNT UNLOCK;

GRANT CONNECT, RESOURCE TO PRACTICAS_APP;
GRANT CREATE VIEW, CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO PRACTICAS_APP;
```

Conexion esperada:

```sql
CONNECT PRACTICAS_APP/Practicas2026@XE
```

## 5. Creacion de estructura (tablas, constraints, secuencias, triggers, indices)
Ejecutar conectado como `PRACTICAS_APP`:

```sql
@gestion-practicas-desktop/sql/01_schema_oracle_reducido_horas.sql
```

El script:
- Limpia objetos previos (tablas, triggers, secuencias).
- Elimina tablas fuera de alcance anterior (rubrica/evaluacion/plantillas/hallazgos).
- Crea el modelo reducido oficial.

## 6. Tablas del modelo reducido

### 6.1 `usuario`
Campos:
- `id_usuario` (PK)
- `nombres`
- `apellidos`
- `correo` (UQ)
- `identificacion` (UQ)
- `hash_password`
- `rol` (`ESTUDIANTE`, `DOCENTE`, `DIRECTOR`)
- `estado` (`ACTIVO`, `INACTIVO`)
- `fecha_creacion`

### 6.2 `estudiante`
Campos:
- `id_estudiante` (PK)
- `codigo` (UQ)
- `programa`
- `semestre` (1..20)
- `id_usuario` (FK -> `usuario.id_usuario`, UQ)

### 6.3 `docente_asesor`
Campos:
- `id_docente` (PK)
- `especialidad`
- `id_usuario` (FK -> `usuario.id_usuario`, UQ)

### 6.4 `director_programa`
Campos:
- `id_director` (PK)
- `programa`
- `id_usuario` (FK -> `usuario.id_usuario`, UQ)

### 6.5 `entidad_receptora`
Campos:
- `id_entidad` (PK)
- `nombre`
- `direccion`
- `ciudad`
- `contacto_nombre`
- `contacto_email`
- `contacto_telefono`
- `cupos_totales`
- `cupos_disponibles`
- `estado` (`ACTIVA`, `INACTIVA`)

### 6.6 `practica`
Campos:
- `id_practica` (PK)
- `periodo`
- `fecha_inicio`
- `fecha_fin`
- `estado` (`PENDIENTE`, `EN_CURSO`, `PENDIENTE_APROBACION`, `FINALIZADA`)
- `horas_objetivo`
- `horas_acumuladas`
- `fecha_registro`
- `id_estudiante` (FK -> `estudiante.id_estudiante`)
- `id_entidad` (FK -> `entidad_receptora.id_entidad`)
- `id_docente` (FK -> `docente_asesor.id_docente`)

### 6.7 `bitacora`
Campos:
- `id_bitacora` (PK)
- `id_practica` (FK -> `practica.id_practica`)
- `fecha_actividad`
- `actividad`
- `descripcion`
- `horas_reportadas`
- `estado_validacion` (`PENDIENTE`, `VALIDADA`, `RECHAZADA`)
- `observacion_docente`
- `id_docente_validador` (FK -> `docente_asesor.id_docente`, opcional)
- `fecha_validacion`
- `fecha_registro`

### 6.8 `evidencia`
Campos:
- `id_evidencia` (PK)
- `id_bitacora` (FK -> `bitacora.id_bitacora`)
- `tipo_archivo`
- `nombre_archivo`
- `ruta_archivo`
- `comentario`
- `fecha_carga`

## 6.9 Diccionario de tablas numerado (001-008)

| Numero | Nombre tabla | Descripcion |
|---|---|---|
| 001 | `usuario` | Almacena credenciales y datos base de identidad de todos los roles del sistema (estudiante, docente y director), incluyendo estado y fecha de creacion. |
| 002 | `estudiante` | Extension del usuario para rol estudiante; guarda codigo, programa y semestre, enlazado 1 a 1 con `usuario`. |
| 003 | `docente_asesor` | Extension del usuario para rol docente; registra especialidad del docente asesor y su enlace 1 a 1 con `usuario`. |
| 004 | `director_programa` | Extension del usuario para rol director; registra el programa academico bajo su direccion y su enlace 1 a 1 con `usuario`. |
| 005 | `entidad_receptora` | Catalogo de entidades donde se realizan practicas; incluye datos de contacto, ubicacion, cupos y estado operativo. |
| 006 | `practica` | Registro principal de cada proceso de practica academica; relaciona estudiante, docente y entidad, con fechas, estado y control de horas. |
| 007 | `bitacora` | Registro de actividades y horas reportadas por practica; soporta flujo de validacion docente con estado, observacion y trazabilidad de fechas. |
| 008 | `evidencia` | Soportes asociados a cada entrada de bitacora (archivo/ruta/comentario), usados para sustentar actividades y validaciones. |

## 7. Secuencias y triggers de autoincremento
Secuencias:
- `seq_usuario`
- `seq_estudiante`
- `seq_docente_asesor`
- `seq_director_programa`
- `seq_entidad_receptora`
- `seq_practica`
- `seq_bitacora`
- `seq_evidencia`

Triggers `BEFORE INSERT`:
- `bi_usuario`
- `bi_estudiante`
- `bi_docente_asesor`
- `bi_director_programa`
- `bi_entidad_receptora`
- `bi_practica`
- `bi_bitacora`
- `bi_evidencia`

Nota: Si el ID llega en `NULL`, el trigger asigna `NEXTVAL` de su secuencia.

## 8. Indices de apoyo
- `ix_usuario_rol_estado`
- `ix_practica_estudiante`
- `ix_practica_entidad`
- `ix_practica_docente`
- `ix_practica_estado`
- `ix_practica_periodo`
- `ix_bitacora_practica`
- `ix_bitacora_estado`
- `ix_bitacora_fecha`
- `ix_evidencia_bitacora`

## 9. Registros base documentados (existentes en el proyecto)
Registros reportados para usuarios de rol directivo/docente:

```sql
-- Director
INSERT INTO USUARIO (ID_USUARIO, NOMBRES, APELLIDOS, CORREO, IDENTIFICACION, HASH_PASSWORD, ROL, ESTADO, FECHA_CREACION)
VALUES (10, 'Jhon Willian', 'Director', 'jhon@udi.edu.co', '123456', '123456', 'DIRECTOR', 'ACTIVO', SYSTIMESTAMP);

-- Docente
INSERT INTO USUARIO (ID_USUARIO, NOMBRES, APELLIDOS, CORREO, IDENTIFICACION, HASH_PASSWORD, ROL, ESTADO, FECHA_CREACION)
VALUES (11, 'Alexandra Beltran', 'Docente', 'alexandra@udi.edu.co', '987654321', '123456', 'DOCENTE', 'ACTIVO', SYSTIMESTAMP);
```

## 10. Script recomendado de registros iniciales completos (flujo reducido)
Este bloque crea datos minimos para probar el flujo completo (estudiante -> practica -> bitacora -> evidencia -> validacion/aprobacion):

```sql
-- 1) Usuarios por rol
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado)
VALUES (NULL, 'Estudiante', 'Demo', 'estudiante.demo@udi.edu.co', 'EST-1001', '123456', 'ESTUDIANTE', 'ACTIVO');

INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado)
VALUES (NULL, 'Docente', 'Demo', 'docente.demo@udi.edu.co', 'DOC-2001', '123456', 'DOCENTE', 'ACTIVO');

INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado)
VALUES (NULL, 'Directora', 'Demo', 'directora.demo@udi.edu.co', 'DIR-3001', '123456', 'DIRECTOR', 'ACTIVO');

-- 2) Especializacion por rol
INSERT INTO estudiante (id_estudiante, codigo, programa, semestre, id_usuario)
SELECT NULL, '20261001', 'Ingenieria de Sistemas', 8, u.id_usuario
FROM usuario u
WHERE u.correo = 'estudiante.demo@udi.edu.co';

INSERT INTO docente_asesor (id_docente, especialidad, id_usuario)
SELECT NULL, 'Practicas empresariales', u.id_usuario
FROM usuario u
WHERE u.correo = 'docente.demo@udi.edu.co';

INSERT INTO director_programa (id_director, programa, id_usuario)
SELECT NULL, 'Ingenieria de Sistemas', u.id_usuario
FROM usuario u
WHERE u.correo = 'directora.demo@udi.edu.co';

-- 3) Entidad receptora
INSERT INTO entidad_receptora (
  id_entidad, nombre, direccion, ciudad, contacto_nombre, contacto_email, contacto_telefono,
  cupos_totales, cupos_disponibles, estado
)
VALUES (
  NULL, 'Empresa Demo SAS', 'Calle 10 # 20-30', 'Bucaramanga', 'Ana Contacto', 'contacto@empresademo.com', '3001112233',
  20, 15, 'ACTIVA'
);

-- 4) Practica
INSERT INTO practica (
  id_practica, periodo, fecha_inicio, fecha_fin, estado, horas_objetivo, horas_acumuladas,
  id_estudiante, id_entidad, id_docente
)
VALUES (
  NULL,
  '2026-1',
  DATE '2026-01-20',
  DATE '2026-05-20',
  'EN_CURSO',
  160,
  0,
  (SELECT e.id_estudiante FROM estudiante e JOIN usuario u ON u.id_usuario = e.id_usuario WHERE u.correo = 'estudiante.demo@udi.edu.co'),
  (SELECT MAX(id_entidad) FROM entidad_receptora),
  (SELECT d.id_docente FROM docente_asesor d JOIN usuario u ON u.id_usuario = d.id_usuario WHERE u.correo = 'docente.demo@udi.edu.co')
);

-- 5) Bitacora
INSERT INTO bitacora (
  id_bitacora, id_practica, fecha_actividad, actividad, descripcion, horas_reportadas, estado_validacion
)
VALUES (
  NULL,
  (SELECT MAX(id_practica) FROM practica),
  DATE '2026-02-01',
  'Levantamiento de requerimientos',
  'Recoleccion de informacion del proceso de practicas',
  4,
  'PENDIENTE'
);

-- 6) Evidencia
INSERT INTO evidencia (
  id_evidencia, id_bitacora, tipo_archivo, nombre_archivo, ruta_archivo, comentario
)
VALUES (
  NULL,
  (SELECT MAX(id_bitacora) FROM bitacora),
  'PDF',
  'acta_reunion_semana1.pdf',
  'C:/evidencias/acta_reunion_semana1.pdf',
  'Soporte de actividad semanal'
);

COMMIT;
```

## 11. Consultas de verificacion

```sql
-- Tablas creadas
SELECT table_name FROM user_tables ORDER BY table_name;

-- Usuarios por rol
SELECT rol, COUNT(*) FROM usuario GROUP BY rol ORDER BY rol;

-- Practicas con estudiante y docente
SELECT p.id_practica, p.periodo, p.estado,
       ue.nombres || ' ' || ue.apellidos AS estudiante,
       ud.nombres || ' ' || ud.apellidos AS docente
FROM practica p
JOIN estudiante e ON e.id_estudiante = p.id_estudiante
JOIN usuario ue ON ue.id_usuario = e.id_usuario
JOIN docente_asesor d ON d.id_docente = p.id_docente
JOIN usuario ud ON ud.id_usuario = d.id_usuario;

-- Bitacora y evidencias
SELECT b.id_bitacora, b.actividad, b.estado_validacion, evi.nombre_archivo
FROM bitacora b
LEFT JOIN evidencia evi ON evi.id_bitacora = b.id_bitacora
ORDER BY b.id_bitacora DESC;
```

## 12. Nota de alcance
La base de datos oficial para esta entrega es la **version reducida de gestion de horas**.
Quedan fuera de alcance las tablas: `rubrica`, `criterio_rubrica`, `evaluacion`, `detalle_evaluacion`, `plantilla_bitacora`, `hallazgo_mejora`.
