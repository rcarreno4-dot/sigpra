-- Modelo Entidad-Relacion - Practicas Academicas
-- Oracle SQL compatible con Oracle 10g/11g (sin IDENTITY)

SET DEFINE OFF;

-- =====================================================================
-- LIMPIEZA (permite re-ejecutar el script)
-- =====================================================================
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE detalle_evaluacion CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE evaluacion CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE evidencia CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE actividad_practica CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE practica CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE criterio_rubrica CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE rubrica CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE entidad_receptora CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE director_programa CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE docente_asesor CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE estudiante CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE usuario CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_detalle_evaluacion'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_evaluacion'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_evidencia'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_actividad_practica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_practica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_criterio_rubrica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_rubrica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_entidad_receptora'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_director_programa'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_docente_asesor'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_estudiante'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER bi_usuario'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -4080 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_detalle_evaluacion'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_evaluacion'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_evidencia'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_actividad_practica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_practica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_criterio_rubrica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_rubrica'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_entidad_receptora'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_director_programa'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_docente_asesor'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_estudiante'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_usuario'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;
/

-- =====================================================================
-- TABLAS
-- =====================================================================
CREATE TABLE usuario (
    id_usuario       NUMBER        NOT NULL,
    nombres          VARCHAR2(80)  NOT NULL,
    apellidos        VARCHAR2(80)  NOT NULL,
    correo           VARCHAR2(120) NOT NULL,
    identificacion   VARCHAR2(30)  NOT NULL,
    hash_password    VARCHAR2(255) NOT NULL,
    rol              VARCHAR2(20)  NOT NULL,
    estado           VARCHAR2(20)  DEFAULT 'ACTIVO' NOT NULL,
    fecha_creacion   TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT uq_usuario_identificacion UNIQUE (identificacion),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ESTUDIANTE', 'DOCENTE', 'DIRECTOR')),
    CONSTRAINT ck_usuario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE estudiante (
    id_estudiante    NUMBER        NOT NULL,
    codigo           VARCHAR2(30)  NOT NULL,
    programa         VARCHAR2(120) NOT NULL,
    semestre         NUMBER(2),
    id_usuario       NUMBER        NOT NULL,
    CONSTRAINT pk_estudiante PRIMARY KEY (id_estudiante),
    CONSTRAINT uq_estudiante_codigo UNIQUE (codigo),
    CONSTRAINT uq_estudiante_id_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_estudiante_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),
    CONSTRAINT ck_estudiante_semestre CHECK (semestre IS NULL OR (semestre >= 1 AND semestre <= 20))
);

CREATE TABLE docente_asesor (
    id_docente       NUMBER        NOT NULL,
    especialidad     VARCHAR2(120),
    id_usuario       NUMBER        NOT NULL,
    CONSTRAINT pk_docente_asesor PRIMARY KEY (id_docente),
    CONSTRAINT uq_docente_id_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_docente_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);

CREATE TABLE director_programa (
    id_director      NUMBER        NOT NULL,
    programa         VARCHAR2(120) NOT NULL,
    id_usuario       NUMBER        NOT NULL,
    CONSTRAINT pk_director_programa PRIMARY KEY (id_director),
    CONSTRAINT uq_director_id_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_director_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);

CREATE TABLE entidad_receptora (
    id_entidad         NUMBER        NOT NULL,
    nombre             VARCHAR2(150) NOT NULL,
    direccion          VARCHAR2(200),
    ciudad             VARCHAR2(100),
    contacto_nombre    VARCHAR2(120),
    contacto_email     VARCHAR2(120),
    contacto_telefono  VARCHAR2(30),
    cupos_totales      NUMBER(5) DEFAULT 0 NOT NULL,
    cupos_disponibles  NUMBER(5) DEFAULT 0 NOT NULL,
    estado             VARCHAR2(20) DEFAULT 'ACTIVA' NOT NULL,
    CONSTRAINT pk_entidad_receptora PRIMARY KEY (id_entidad),
    CONSTRAINT ck_entidad_estado CHECK (estado IN ('ACTIVA', 'INACTIVA')),
    CONSTRAINT ck_entidad_cupos CHECK (
        cupos_totales >= 0
        AND cupos_disponibles >= 0
        AND cupos_disponibles <= cupos_totales
    )
);

CREATE TABLE rubrica (
    id_rubrica        NUMBER        NOT NULL,
    nombre            VARCHAR2(120) NOT NULL,
    version           VARCHAR2(20),
    estado            VARCHAR2(20) DEFAULT 'ACTIVA' NOT NULL,
    CONSTRAINT pk_rubrica PRIMARY KEY (id_rubrica),
    CONSTRAINT ck_rubrica_estado CHECK (estado IN ('ACTIVA', 'INACTIVA'))
);

CREATE TABLE criterio_rubrica (
    id_criterio       NUMBER        NOT NULL,
    nombre            VARCHAR2(120) NOT NULL,
    descripcion       VARCHAR2(500),
    ponderacion       NUMBER(5,2)   NOT NULL,
    id_rubrica        NUMBER        NOT NULL,
    CONSTRAINT pk_criterio_rubrica PRIMARY KEY (id_criterio),
    CONSTRAINT fk_criterio_rubrica FOREIGN KEY (id_rubrica)
        REFERENCES rubrica(id_rubrica),
    CONSTRAINT ck_criterio_ponderacion CHECK (ponderacion >= 0 AND ponderacion <= 100)
);

CREATE TABLE practica (
    id_practica       NUMBER        NOT NULL,
    periodo           VARCHAR2(20)  NOT NULL,
    fecha_inicio      DATE          NOT NULL,
    fecha_fin         DATE          NOT NULL,
    horas_objetivo    NUMBER(6)     DEFAULT 160 NOT NULL,
    horas_acumuladas  NUMBER(8,2)   DEFAULT 0   NOT NULL,
    estado            VARCHAR2(30)  DEFAULT 'EN_CURSO' NOT NULL,
    id_estudiante     NUMBER        NOT NULL,
    id_entidad        NUMBER        NOT NULL,
    id_docente        NUMBER        NOT NULL,
    CONSTRAINT pk_practica PRIMARY KEY (id_practica),
    CONSTRAINT fk_practica_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiante(id_estudiante),
    CONSTRAINT fk_practica_entidad FOREIGN KEY (id_entidad)
        REFERENCES entidad_receptora(id_entidad),
    CONSTRAINT fk_practica_docente FOREIGN KEY (id_docente)
        REFERENCES docente_asesor(id_docente),
    CONSTRAINT ck_practica_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT ck_practica_horas CHECK (horas_objetivo >= 0 AND horas_acumuladas >= 0),
    CONSTRAINT ck_practica_estado CHECK (estado IN ('PENDIENTE', 'EN_CURSO', 'PENDIENTE_APROBACION', 'FINALIZADA'))
);

CREATE TABLE actividad_practica (
    id_actividad        NUMBER        NOT NULL,
    fecha_actividad     DATE          NOT NULL,
    descripcion         VARCHAR2(1000) NOT NULL,
    horas               NUMBER(6,2)   NOT NULL,
    estado_validacion   VARCHAR2(20)  DEFAULT 'PENDIENTE' NOT NULL,
    comentario_docente  VARCHAR2(500),
    id_practica         NUMBER        NOT NULL,
    CONSTRAINT pk_actividad_practica PRIMARY KEY (id_actividad),
    CONSTRAINT fk_actividad_practica FOREIGN KEY (id_practica)
        REFERENCES practica(id_practica),
    CONSTRAINT ck_actividad_horas CHECK (horas > 0),
    CONSTRAINT ck_actividad_estado CHECK (estado_validacion IN ('PENDIENTE', 'VALIDADA', 'RECHAZADA'))
);

CREATE TABLE evidencia (
    id_evidencia      NUMBER         NOT NULL,
    nombre_archivo    VARCHAR2(255)  NOT NULL,
    tipo_archivo      VARCHAR2(50),
    url_archivo       VARCHAR2(1000) NOT NULL,
    fecha_carga       TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    id_actividad      NUMBER         NOT NULL,
    CONSTRAINT pk_evidencia PRIMARY KEY (id_evidencia),
    CONSTRAINT fk_evidencia_actividad FOREIGN KEY (id_actividad)
        REFERENCES actividad_practica(id_actividad)
);

CREATE TABLE evaluacion (
    id_evaluacion     NUMBER        NOT NULL,
    fecha_evaluacion  DATE          NOT NULL,
    puntaje_total     NUMBER(6,2)   DEFAULT 0 NOT NULL,
    observaciones     VARCHAR2(1000),
    estado            VARCHAR2(20)  DEFAULT 'EN_REVISION' NOT NULL,
    id_practica       NUMBER        NOT NULL,
    id_docente        NUMBER        NOT NULL,
    id_rubrica        NUMBER        NOT NULL,
    CONSTRAINT pk_evaluacion PRIMARY KEY (id_evaluacion),
    CONSTRAINT fk_evaluacion_practica FOREIGN KEY (id_practica)
        REFERENCES practica(id_practica),
    CONSTRAINT fk_evaluacion_docente FOREIGN KEY (id_docente)
        REFERENCES docente_asesor(id_docente),
    CONSTRAINT fk_evaluacion_rubrica FOREIGN KEY (id_rubrica)
        REFERENCES rubrica(id_rubrica),
    CONSTRAINT ck_evaluacion_estado CHECK (estado IN ('EN_REVISION', 'APROBADA', 'RECHAZADA')),
    CONSTRAINT ck_evaluacion_puntaje CHECK (puntaje_total >= 0)
);

CREATE TABLE detalle_evaluacion (
    id_detalle         NUMBER       NOT NULL,
    puntaje_criterio   NUMBER(6,2)  NOT NULL,
    observacion        VARCHAR2(500),
    id_evaluacion      NUMBER       NOT NULL,
    id_criterio        NUMBER       NOT NULL,
    CONSTRAINT pk_detalle_evaluacion PRIMARY KEY (id_detalle),
    CONSTRAINT fk_det_eval_evaluacion FOREIGN KEY (id_evaluacion)
        REFERENCES evaluacion(id_evaluacion),
    CONSTRAINT fk_det_eval_criterio FOREIGN KEY (id_criterio)
        REFERENCES criterio_rubrica(id_criterio),
    CONSTRAINT ck_detalle_puntaje CHECK (puntaje_criterio >= 0)
);

-- =====================================================================
-- SECUENCIAS
-- =====================================================================
CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_estudiante START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_docente_asesor START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_director_programa START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_entidad_receptora START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_rubrica START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_criterio_rubrica START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_practica START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_actividad_practica START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_evidencia START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_evaluacion START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_detalle_evaluacion START WITH 1 INCREMENT BY 1 NOCACHE;

-- =====================================================================
-- TRIGGERS AUTOINCREMENTALES
-- =====================================================================
CREATE OR REPLACE TRIGGER bi_usuario
BEFORE INSERT ON usuario
FOR EACH ROW
BEGIN
  IF :NEW.id_usuario IS NULL THEN
    SELECT seq_usuario.NEXTVAL INTO :NEW.id_usuario FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_estudiante
BEFORE INSERT ON estudiante
FOR EACH ROW
BEGIN
  IF :NEW.id_estudiante IS NULL THEN
    SELECT seq_estudiante.NEXTVAL INTO :NEW.id_estudiante FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_docente_asesor
BEFORE INSERT ON docente_asesor
FOR EACH ROW
BEGIN
  IF :NEW.id_docente IS NULL THEN
    SELECT seq_docente_asesor.NEXTVAL INTO :NEW.id_docente FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_director_programa
BEFORE INSERT ON director_programa
FOR EACH ROW
BEGIN
  IF :NEW.id_director IS NULL THEN
    SELECT seq_director_programa.NEXTVAL INTO :NEW.id_director FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_entidad_receptora
BEFORE INSERT ON entidad_receptora
FOR EACH ROW
BEGIN
  IF :NEW.id_entidad IS NULL THEN
    SELECT seq_entidad_receptora.NEXTVAL INTO :NEW.id_entidad FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_rubrica
BEFORE INSERT ON rubrica
FOR EACH ROW
BEGIN
  IF :NEW.id_rubrica IS NULL THEN
    SELECT seq_rubrica.NEXTVAL INTO :NEW.id_rubrica FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_criterio_rubrica
BEFORE INSERT ON criterio_rubrica
FOR EACH ROW
BEGIN
  IF :NEW.id_criterio IS NULL THEN
    SELECT seq_criterio_rubrica.NEXTVAL INTO :NEW.id_criterio FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_practica
BEFORE INSERT ON practica
FOR EACH ROW
BEGIN
  IF :NEW.id_practica IS NULL THEN
    SELECT seq_practica.NEXTVAL INTO :NEW.id_practica FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_actividad_practica
BEFORE INSERT ON actividad_practica
FOR EACH ROW
BEGIN
  IF :NEW.id_actividad IS NULL THEN
    SELECT seq_actividad_practica.NEXTVAL INTO :NEW.id_actividad FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_evidencia
BEFORE INSERT ON evidencia
FOR EACH ROW
BEGIN
  IF :NEW.id_evidencia IS NULL THEN
    SELECT seq_evidencia.NEXTVAL INTO :NEW.id_evidencia FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_evaluacion
BEFORE INSERT ON evaluacion
FOR EACH ROW
BEGIN
  IF :NEW.id_evaluacion IS NULL THEN
    SELECT seq_evaluacion.NEXTVAL INTO :NEW.id_evaluacion FROM dual;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER bi_detalle_evaluacion
BEFORE INSERT ON detalle_evaluacion
FOR EACH ROW
BEGIN
  IF :NEW.id_detalle IS NULL THEN
    SELECT seq_detalle_evaluacion.NEXTVAL INTO :NEW.id_detalle FROM dual;
  END IF;
END;
/

-- =====================================================================
-- INDICES DE APOYO
-- =====================================================================

CREATE INDEX ix_practica_estudiante ON practica(id_estudiante);
CREATE INDEX ix_practica_entidad ON practica(id_entidad);
CREATE INDEX ix_practica_docente ON practica(id_docente);
CREATE INDEX ix_practica_estado ON practica(estado);

CREATE INDEX ix_actividad_practica ON actividad_practica(id_practica);
CREATE INDEX ix_actividad_estado ON actividad_practica(estado_validacion);
CREATE INDEX ix_evidencia_actividad ON evidencia(id_actividad);

CREATE INDEX ix_criterio_rubrica ON criterio_rubrica(id_rubrica);
CREATE INDEX ix_evaluacion_practica ON evaluacion(id_practica);
CREATE INDEX ix_evaluacion_docente ON evaluacion(id_docente);
CREATE INDEX ix_evaluacion_rubrica ON evaluacion(id_rubrica);
CREATE INDEX ix_detalle_evaluacion ON detalle_evaluacion(id_evaluacion);
CREATE INDEX ix_detalle_criterio ON detalle_evaluacion(id_criterio);

