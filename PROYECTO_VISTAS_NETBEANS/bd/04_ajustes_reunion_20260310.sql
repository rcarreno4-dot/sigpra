SET DEFINE OFF;

PROMPT ==========================================
PROMPT Ajustes por reunion funcional 2026-03-10
PROMPT ==========================================

PROMPT 1) Extension de PRACTICA para nuevos criterios
ALTER TABLE practica ADD (
  semestre_practica NUMBER(2) DEFAULT 1 NOT NULL,
  modalidad         VARCHAR2(15) DEFAULT 'PRESENCIAL' NOT NULL,
  arl_vigencia_hasta DATE
);

ALTER TABLE practica ADD CONSTRAINT ck_practica_semestre
  CHECK (semestre_practica BETWEEN 1 AND 8);

ALTER TABLE practica ADD CONSTRAINT ck_practica_modalidad
  CHECK (modalidad IN ('PRESENCIAL','VIRTUAL'));

PROMPT 2) Extension de EVIDENCIA para seleccion de pares
ALTER TABLE evidencia ADD (
  es_destacada   VARCHAR2(2) DEFAULT 'NO' NOT NULL,
  categoria      VARCHAR2(30)
);

ALTER TABLE evidencia ADD CONSTRAINT ck_evidencia_destacada
  CHECK (es_destacada IN ('SI','NO'));

PROMPT 3) Plantillas y preguntas de bitacora por semestre/modalidad
CREATE TABLE plantilla_bitacora (
  id_plantilla      NUMBER(10)     NOT NULL,
  nombre            VARCHAR2(120)  NOT NULL,
  semestre_practica NUMBER(2)      NOT NULL,
  modalidad         VARCHAR2(15)   NOT NULL,
  version           VARCHAR2(30)   NOT NULL,
  estado            VARCHAR2(10)   NOT NULL,
  id_creado_por     NUMBER(10)     NOT NULL,
  fecha_creacion    DATE           DEFAULT SYSDATE NOT NULL,
  CONSTRAINT pk_plantilla_bitacora PRIMARY KEY (id_plantilla),
  CONSTRAINT fk_plantilla_usuario FOREIGN KEY (id_creado_por) REFERENCES usuario(id_usuario),
  CONSTRAINT ck_plantilla_semestre CHECK (semestre_practica BETWEEN 1 AND 8),
  CONSTRAINT ck_plantilla_modalidad CHECK (modalidad IN ('PRESENCIAL','VIRTUAL')),
  CONSTRAINT ck_plantilla_estado CHECK (estado IN ('ACTIVA','INACTIVA'))
);

CREATE TABLE pregunta_plantilla_bitacora (
  id_pregunta       NUMBER(10)      NOT NULL,
  id_plantilla      NUMBER(10)      NOT NULL,
  orden_pregunta    NUMBER(3)       NOT NULL,
  pregunta          VARCHAR2(1000)  NOT NULL,
  obligatoria       VARCHAR2(2)     DEFAULT 'SI' NOT NULL,
  estado            VARCHAR2(10)    NOT NULL,
  CONSTRAINT pk_pregunta_plantilla PRIMARY KEY (id_pregunta),
  CONSTRAINT uq_pregunta_orden UNIQUE (id_plantilla, orden_pregunta),
  CONSTRAINT fk_pregunta_plantilla FOREIGN KEY (id_plantilla)
    REFERENCES plantilla_bitacora(id_plantilla) ON DELETE CASCADE,
  CONSTRAINT ck_pregunta_obligatoria CHECK (obligatoria IN ('SI','NO')),
  CONSTRAINT ck_pregunta_estado CHECK (estado IN ('ACTIVA','INACTIVA'))
);

PROMPT 4) Control de ingreso/salida en institucion receptora
CREATE TABLE jornada_practica (
  id_jornada            NUMBER(10)    NOT NULL,
  id_practica           NUMBER(10)    NOT NULL,
  fecha_jornada         DATE          NOT NULL,
  hora_ingreso          VARCHAR2(5)   NOT NULL,
  hora_salida           VARCHAR2(5)   NOT NULL,
  horas_registradas     NUMBER(5,2)   NOT NULL,
  firma_ingreso_nombre  VARCHAR2(120) NOT NULL,
  firma_salida_nombre   VARCHAR2(120) NOT NULL,
  cargo_firmante        VARCHAR2(100),
  estado_validacion     VARCHAR2(12)  NOT NULL,
  observacion           VARCHAR2(1000),
  CONSTRAINT pk_jornada_practica PRIMARY KEY (id_jornada),
  CONSTRAINT fk_jornada_practica FOREIGN KEY (id_practica)
    REFERENCES practica(id_practica) ON DELETE CASCADE,
  CONSTRAINT ck_jornada_horas CHECK (horas_registradas > 0),
  CONSTRAINT ck_jornada_estado CHECK (estado_validacion IN ('PENDIENTE','VALIDADA','RECHAZADA'))
);

PROMPT 5) Reportes institucionales por periodo
CREATE TABLE reporte_periodo_practica (
  id_reporte         NUMBER(10)     NOT NULL,
  periodo            VARCHAR2(10)   NOT NULL,
  tipo_reporte       VARCHAR2(20)   NOT NULL,
  titulo             VARCHAR2(180)  NOT NULL,
  fecha_limite       DATE           NOT NULL,
  fecha_entrega      DATE,
  estado             VARCHAR2(12)   NOT NULL,
  id_responsable     NUMBER(10)     NOT NULL,
  url_documento      VARCHAR2(255),
  observacion        VARCHAR2(1000),
  CONSTRAINT pk_reporte_periodo PRIMARY KEY (id_reporte),
  CONSTRAINT uq_reporte_periodo_tipo UNIQUE (periodo, tipo_reporte),
  CONSTRAINT fk_reporte_responsable FOREIGN KEY (id_responsable) REFERENCES usuario(id_usuario),
  CONSTRAINT ck_reporte_tipo CHECK (
    tipo_reporte IN ('R1_HORAS','R2_BITACORA','R3_EVALUACION','ADICIONAL')
  ),
  CONSTRAINT ck_reporte_estado CHECK (
    estado IN ('BORRADOR','EN_CURSO','ENTREGADO','VENCIDO')
  )
);

PROMPT 6) Secuencias para nuevas tablas
CREATE SEQUENCE seq_plantilla_bitacora START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_pregunta_plantilla_bitacora START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_jornada_practica START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_reporte_periodo_practica START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

PROMPT 7) Triggers de autoincremento para nuevas tablas
CREATE OR REPLACE TRIGGER trg_plantilla_bitacora_bi
BEFORE INSERT ON plantilla_bitacora
FOR EACH ROW
WHEN (NEW.id_plantilla IS NULL)
BEGIN
  SELECT seq_plantilla_bitacora.NEXTVAL INTO :NEW.id_plantilla FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_pregunta_plantilla_bi
BEFORE INSERT ON pregunta_plantilla_bitacora
FOR EACH ROW
WHEN (NEW.id_pregunta IS NULL)
BEGIN
  SELECT seq_pregunta_plantilla_bitacora.NEXTVAL INTO :NEW.id_pregunta FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_jornada_practica_bi
BEFORE INSERT ON jornada_practica
FOR EACH ROW
WHEN (NEW.id_jornada IS NULL)
BEGIN
  SELECT seq_jornada_practica.NEXTVAL INTO :NEW.id_jornada FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_reporte_periodo_practica_bi
BEFORE INSERT ON reporte_periodo_practica
FOR EACH ROW
WHEN (NEW.id_reporte IS NULL)
BEGIN
  SELECT seq_reporte_periodo_practica.NEXTVAL INTO :NEW.id_reporte FROM dual;
END;
/

PROMPT 8) Datos semilla para pruebas de nuevas funcionalidades
UPDATE practica
SET semestre_practica = 8,
    modalidad = 'PRESENCIAL',
    arl_vigencia_hasta = TO_DATE('31/03/2026','DD/MM/YYYY')
WHERE id_practica = 1;

UPDATE practica
SET semestre_practica = 7,
    modalidad = 'VIRTUAL',
    arl_vigencia_hasta = TO_DATE('31/03/2026','DD/MM/YYYY')
WHERE id_practica = 2;

UPDATE evidencia
SET es_destacada = 'SI',
    categoria = 'RESULTADO_APRENDIZAJE'
WHERE id_evidencia = 1;

INSERT INTO plantilla_bitacora (
  id_plantilla, nombre, semestre_practica, modalidad, version, estado, id_creado_por
) VALUES (
  1, 'Bitacora Semestre 1 - Observacion de Contexto', 1, 'PRESENCIAL', '1.0', 'ACTIVA', 1
);

INSERT INTO pregunta_plantilla_bitacora (
  id_pregunta, id_plantilla, orden_pregunta, pregunta, obligatoria, estado
) VALUES (
  1, 1, 1, 'Que observaciones del contexto institucional identificaste hoy?', 'SI', 'ACTIVA'
);

INSERT INTO pregunta_plantilla_bitacora (
  id_pregunta, id_plantilla, orden_pregunta, pregunta, obligatoria, estado
) VALUES (
  2, 1, 2, 'Que vacios o tensiones pedagogicas detectaste durante la jornada?', 'SI', 'ACTIVA'
);

INSERT INTO pregunta_plantilla_bitacora (
  id_pregunta, id_plantilla, orden_pregunta, pregunta, obligatoria, estado
) VALUES (
  3, 1, 3, 'Que oportunidad de mejora propones con base en la evidencia recopilada?', 'SI', 'ACTIVA'
);

INSERT INTO jornada_practica (
  id_jornada, id_practica, fecha_jornada, hora_ingreso, hora_salida, horas_registradas,
  firma_ingreso_nombre, firma_salida_nombre, cargo_firmante, estado_validacion, observacion
) VALUES (
  1, 1, TO_DATE('08/03/2026','DD/MM/YYYY'), '07:00', '11:00', 4.00,
  'Marta Ruiz', 'Marta Ruiz', 'Coordinadora', 'VALIDADA', 'Jornada completa'
);

INSERT INTO reporte_periodo_practica (
  id_reporte, periodo, tipo_reporte, titulo, fecha_limite, fecha_entrega, estado, id_responsable, url_documento, observacion
) VALUES (
  1, '2026-1', 'R1_HORAS', 'Seguimiento de horas y asistencia', TO_DATE('30/03/2026','DD/MM/YYYY'),
  TO_DATE('28/03/2026','DD/MM/YYYY'), 'ENTREGADO', 2, '/reportes/2026-1-r1-horas.pdf', 'Consolidado por semestre y modalidad'
);

INSERT INTO reporte_periodo_practica (
  id_reporte, periodo, tipo_reporte, titulo, fecha_limite, fecha_entrega, estado, id_responsable, url_documento, observacion
) VALUES (
  2, '2026-1', 'R2_BITACORA', 'Analisis de bitacora investigativa', TO_DATE('10/04/2026','DD/MM/YYYY'),
  NULL, 'EN_CURSO', 1, NULL, 'Pendiente consolidar categoria de tensiones'
);

INSERT INTO reporte_periodo_practica (
  id_reporte, periodo, tipo_reporte, titulo, fecha_limite, fecha_entrega, estado, id_responsable, url_documento, observacion
) VALUES (
  3, '2026-1', 'R3_EVALUACION', 'Consolidado de evaluacion por rubrica', TO_DATE('20/04/2026','DD/MM/YYYY'),
  NULL, 'BORRADOR', 1, NULL, 'Estructura inicial de reporte'
);

COMMIT;

PROMPT Ajustes aplicados correctamente.
