SET DEFINE OFF;

PROMPT ======================
PROMPT Creacion de tablas
PROMPT ======================

CREATE TABLE usuario (
  id_usuario         NUMBER(10)     NOT NULL,
  nombres            VARCHAR2(80)   NOT NULL,
  apellidos          VARCHAR2(80)   NOT NULL,
  correo             VARCHAR2(120)  NOT NULL,
  identificacion     VARCHAR2(20)   NOT NULL,
  hash_password      VARCHAR2(255)  NOT NULL,
  rol                VARCHAR2(20)   NOT NULL,
  estado             VARCHAR2(10)   NOT NULL,
  fecha_creacion     DATE           DEFAULT SYSDATE NOT NULL,
  CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
  CONSTRAINT uq_usuario_correo UNIQUE (correo),
  CONSTRAINT uq_usuario_identificacion UNIQUE (identificacion),
  CONSTRAINT ck_usuario_rol CHECK (rol IN ('ESTUDIANTE','TUTOR_ACADEMICO','DIRECTOR','ASESOR_PEDAGOGICO')),
  CONSTRAINT ck_usuario_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
);

CREATE TABLE estudiante (
  id_estudiante      NUMBER(10)     NOT NULL,
  codigo             VARCHAR2(20)   NOT NULL,
  programa           VARCHAR2(120)  NOT NULL,
  semestre           NUMBER(2)      NOT NULL,
  id_usuario         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_estudiante PRIMARY KEY (id_estudiante),
  CONSTRAINT uq_estudiante_codigo UNIQUE (codigo),
  CONSTRAINT uq_estudiante_usuario UNIQUE (id_usuario),
  CONSTRAINT fk_estudiante_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE docente_asesor (
  id_docente         NUMBER(10)     NOT NULL,
  especialidad       VARCHAR2(120),
  id_usuario         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_docente_asesor PRIMARY KEY (id_docente),
  CONSTRAINT uq_docente_usuario UNIQUE (id_usuario),
  CONSTRAINT fk_docente_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE director_programa (
  id_director        NUMBER(10)     NOT NULL,
  programa           VARCHAR2(120)  NOT NULL,
  id_usuario         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_director_programa PRIMARY KEY (id_director),
  CONSTRAINT uq_director_usuario UNIQUE (id_usuario),
  CONSTRAINT fk_director_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE entidad_receptora (
  id_entidad         NUMBER(10)     NOT NULL,
  nombre             VARCHAR2(150)  NOT NULL,
  direccion          VARCHAR2(160)  NOT NULL,
  ciudad             VARCHAR2(80)   NOT NULL,
  programa_receptor  VARCHAR2(120),
  contacto_nombre    VARCHAR2(120),
  contacto_email     VARCHAR2(120),
  contacto_telefono  VARCHAR2(30),
  cupos_totales      NUMBER(4)      NOT NULL,
  cupos_disponibles  NUMBER(4)      NOT NULL,
  estado             VARCHAR2(10)   NOT NULL,
  CONSTRAINT pk_entidad_receptora PRIMARY KEY (id_entidad),
  CONSTRAINT ck_entidad_estado CHECK (estado IN ('ACTIVA','INACTIVA')),
  CONSTRAINT ck_entidad_cupos CHECK (
    cupos_totales >= 0 AND cupos_disponibles >= 0 AND cupos_disponibles <= cupos_totales
  )
);

CREATE TABLE usuario_entidad (
  id_usuario_entidad NUMBER(10)     NOT NULL,
  id_usuario         NUMBER(10)     NOT NULL,
  id_entidad         NUMBER(10)     NOT NULL,
  cargo              VARCHAR2(80),
  estado             VARCHAR2(10)   NOT NULL,
  CONSTRAINT pk_usuario_entidad PRIMARY KEY (id_usuario_entidad),
  CONSTRAINT uq_usuario_entidad_usuario UNIQUE (id_usuario),
  CONSTRAINT uq_usuario_entidad_par UNIQUE (id_usuario, id_entidad),
  CONSTRAINT fk_usuario_entidad_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_usuario_entidad_entidad FOREIGN KEY (id_entidad) REFERENCES entidad_receptora(id_entidad),
  CONSTRAINT ck_usuario_entidad_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
);

CREATE TABLE rubrica (
  id_rubrica         NUMBER(10)     NOT NULL,
  nombre             VARCHAR2(120)  NOT NULL,
  version            VARCHAR2(30)   NOT NULL,
  estado             VARCHAR2(10)   NOT NULL,
  CONSTRAINT pk_rubrica PRIMARY KEY (id_rubrica),
  CONSTRAINT ck_rubrica_estado CHECK (estado IN ('ACTIVA','INACTIVA'))
);

CREATE TABLE criterio_rubrica (
  id_criterio        NUMBER(10)     NOT NULL,
  nombre             VARCHAR2(120)  NOT NULL,
  descripcion        VARCHAR2(500),
  ponderacion        NUMBER(5,2)    NOT NULL,
  id_rubrica         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_criterio_rubrica PRIMARY KEY (id_criterio),
  CONSTRAINT fk_criterio_rubrica FOREIGN KEY (id_rubrica) REFERENCES rubrica(id_rubrica),
  CONSTRAINT ck_criterio_ponderacion CHECK (ponderacion > 0 AND ponderacion <= 100)
);

CREATE TABLE practica (
  id_practica        NUMBER(10)     NOT NULL,
  periodo            VARCHAR2(10)   NOT NULL,
  fecha_inicio       DATE           NOT NULL,
  fecha_fin          DATE,
  horas_objetivo     NUMBER(6,2)    NOT NULL,
  horas_acumuladas   NUMBER(6,2)    DEFAULT 0 NOT NULL,
  estado             VARCHAR2(15)   NOT NULL,
  id_estudiante      NUMBER(10)     NOT NULL,
  id_entidad         NUMBER(10)     NOT NULL,
  id_docente         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_practica PRIMARY KEY (id_practica),
  CONSTRAINT fk_practica_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
  CONSTRAINT fk_practica_entidad FOREIGN KEY (id_entidad) REFERENCES entidad_receptora(id_entidad),
  CONSTRAINT fk_practica_docente FOREIGN KEY (id_docente) REFERENCES docente_asesor(id_docente),
  CONSTRAINT ck_practica_estado CHECK (estado IN ('PENDIENTE','EN_CURSO','FINALIZADA','APROBADA','RECHAZADA')),
  CONSTRAINT ck_practica_horas CHECK (horas_objetivo > 0 AND horas_acumuladas >= 0)
);

CREATE TABLE actividad_practica (
  id_actividad       NUMBER(10)     NOT NULL,
  fecha_actividad    DATE           NOT NULL,
  descripcion        VARCHAR2(1000) NOT NULL,
  horas              NUMBER(5,2)    NOT NULL,
  estado_validacion  VARCHAR2(10)   NOT NULL,
  comentario_docente VARCHAR2(1000),
  id_practica        NUMBER(10)     NOT NULL,
  CONSTRAINT pk_actividad_practica PRIMARY KEY (id_actividad),
  CONSTRAINT fk_actividad_practica FOREIGN KEY (id_practica) REFERENCES practica(id_practica) ON DELETE CASCADE,
  CONSTRAINT ck_actividad_estado CHECK (estado_validacion IN ('PENDIENTE','VALIDADA','RECHAZADA')),
  CONSTRAINT ck_actividad_horas CHECK (horas > 0)
);

CREATE TABLE evidencia (
  id_evidencia       NUMBER(10)     NOT NULL,
  nombre_archivo     VARCHAR2(160)  NOT NULL,
  tipo_archivo       VARCHAR2(20)   NOT NULL,
  url_archivo        VARCHAR2(255)  NOT NULL,
  fecha_carga        DATE           DEFAULT SYSDATE NOT NULL,
  id_actividad       NUMBER(10)     NOT NULL,
  CONSTRAINT pk_evidencia PRIMARY KEY (id_evidencia),
  CONSTRAINT fk_evidencia_actividad FOREIGN KEY (id_actividad) REFERENCES actividad_practica(id_actividad) ON DELETE CASCADE
);

CREATE TABLE evaluacion (
  id_evaluacion      NUMBER(10)     NOT NULL,
  fecha_evaluacion   DATE           NOT NULL,
  puntaje_total      NUMBER(5,2)    NOT NULL,
  observaciones      VARCHAR2(1000),
  estado             VARCHAR2(10)   NOT NULL,
  id_practica        NUMBER(10)     NOT NULL,
  id_docente         NUMBER(10)     NOT NULL,
  id_rubrica         NUMBER(10)     NOT NULL,
  CONSTRAINT pk_evaluacion PRIMARY KEY (id_evaluacion),
  CONSTRAINT fk_evaluacion_practica FOREIGN KEY (id_practica) REFERENCES practica(id_practica) ON DELETE CASCADE,
  CONSTRAINT fk_evaluacion_docente FOREIGN KEY (id_docente) REFERENCES docente_asesor(id_docente),
  CONSTRAINT fk_evaluacion_rubrica FOREIGN KEY (id_rubrica) REFERENCES rubrica(id_rubrica),
  CONSTRAINT ck_evaluacion_estado CHECK (estado IN ('PENDIENTE','EMITIDA','AJUSTE')),
  CONSTRAINT ck_evaluacion_puntaje CHECK (puntaje_total >= 0 AND puntaje_total <= 100)
);

CREATE TABLE detalle_evaluacion (
  id_detalle         NUMBER(10)     NOT NULL,
  puntaje_criterio   NUMBER(5,2)    NOT NULL,
  observacion        VARCHAR2(1000),
  id_evaluacion      NUMBER(10)     NOT NULL,
  id_criterio        NUMBER(10)     NOT NULL,
  CONSTRAINT pk_detalle_evaluacion PRIMARY KEY (id_detalle),
  CONSTRAINT fk_detalle_evaluacion FOREIGN KEY (id_evaluacion) REFERENCES evaluacion(id_evaluacion) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_criterio FOREIGN KEY (id_criterio) REFERENCES criterio_rubrica(id_criterio),
  CONSTRAINT ck_detalle_puntaje CHECK (puntaje_criterio >= 0 AND puntaje_criterio <= 100)
);

CREATE TABLE validacion_institucional (
  id_validacion         NUMBER(10)     NOT NULL,
  fecha_validacion      DATE           DEFAULT SYSDATE NOT NULL,
  asistencia            VARCHAR2(2)    NOT NULL,
  estado_validacion     VARCHAR2(10)   NOT NULL,
  observacion           VARCHAR2(1000),
  id_practica           NUMBER(10)     NOT NULL,
  id_entidad            NUMBER(10)     NOT NULL,
  id_usuario_institucion NUMBER(10)    NOT NULL,
  CONSTRAINT pk_validacion_institucional PRIMARY KEY (id_validacion),
  CONSTRAINT fk_validacion_practica FOREIGN KEY (id_practica) REFERENCES practica(id_practica) ON DELETE CASCADE,
  CONSTRAINT fk_validacion_entidad FOREIGN KEY (id_entidad) REFERENCES entidad_receptora(id_entidad),
  CONSTRAINT fk_validacion_usuario FOREIGN KEY (id_usuario_institucion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_validacion_usuario_entidad FOREIGN KEY (id_usuario_institucion, id_entidad)
    REFERENCES usuario_entidad(id_usuario, id_entidad),
  CONSTRAINT ck_validacion_asistencia CHECK (asistencia IN ('SI','NO')),
  CONSTRAINT ck_validacion_estado CHECK (estado_validacion IN ('PENDIENTE','VALIDADA','RECHAZADA'))
);

PROMPT Tablas creadas correctamente.
