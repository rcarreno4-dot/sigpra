SET DEFINE OFF;

PROMPT ======================
PROMPT Carga de datos iniciales
PROMPT ======================

INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (1, 'Carlos', 'Ramirez', 'carlos.ramirez@universidad.edu.co', '1001001001', 'HASH_TEMP_123', 'DIRECTOR', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (2, 'Laura', 'Perez', 'laura.perez@universidad.edu.co', '1001001002', 'HASH_TEMP_123', 'TUTOR_ACADEMICO', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (3, 'Andres', 'Gomez', 'andres.gomez@universidad.edu.co', '1001001003', 'HASH_TEMP_123', 'ESTUDIANTE', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (4, 'Sofia', 'Martinez', 'sofia.martinez@universidad.edu.co', '1001001004', 'HASH_TEMP_123', 'ESTUDIANTE', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (5, 'Diana', 'Lopez', 'diana.lopez@universidad.edu.co', '1001001005', 'HASH_TEMP_123', 'TUTOR_ACADEMICO', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (6, 'Paula', 'Suarez', 'paula.suarez@universidad.edu.co', '1001001006', 'HASH_TEMP_123', 'DIRECTOR', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (7, 'Marta', 'Ruiz', 'marta.ruiz@sanjose.edu.co', '1001001007', 'HASH_TEMP_123', 'ASESOR_PEDAGOGICO', 'ACTIVO', SYSDATE);
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado, fecha_creacion)
VALUES (8, 'Luis', 'Parra', 'luis.parra@horizonte.edu.co', '1001001008', 'HASH_TEMP_123', 'ASESOR_PEDAGOGICO', 'ACTIVO', SYSDATE);

INSERT INTO docente_asesor (id_docente, especialidad, id_usuario)
VALUES (1, 'Didactica y Practica Pedagogica', 2);
INSERT INTO director_programa (id_director, programa, id_usuario)
VALUES (1, 'Licenciatura en Pedagogia Infantil', 1);

INSERT INTO estudiante (id_estudiante, codigo, programa, semestre, id_usuario)
VALUES (1, '20231001', 'Licenciatura en Pedagogia Infantil', 8, 3);
INSERT INTO estudiante (id_estudiante, codigo, programa, semestre, id_usuario)
VALUES (2, '20231002', 'Licenciatura en Pedagogia Infantil', 7, 4);

INSERT INTO entidad_receptora (id_entidad, nombre, direccion, ciudad, programa_receptor, contacto_nombre, contacto_email, contacto_telefono, cupos_totales, cupos_disponibles, estado)
VALUES (1, 'I.E. Tecnico Damaso Zapata', 'Carrera 14 # 70-44', 'Bucaramanga', 'Licenciatura en Pedagogia Infantil', 'Marta Ruiz', 'marta.ruiz@damasozapata.edu.co', '3205551111', 20, 18, 'ACTIVA');
INSERT INTO entidad_receptora (id_entidad, nombre, direccion, ciudad, programa_receptor, contacto_nombre, contacto_email, contacto_telefono, cupos_totales, cupos_disponibles, estado)
VALUES (2, 'Colegio Tecnico Vicente Azuero', 'Carrera 8 # 8-10', 'Floridablanca', 'Licenciatura en Pedagogia Infantil', 'Luis Parra', 'luis.parra@vicenteazuero.edu.co', '3205552222', 15, 14, 'ACTIVA');

INSERT INTO usuario_entidad (id_usuario_entidad, id_usuario, id_entidad, cargo, estado)
VALUES (1, 7, 1, 'Coordinadora de Practicas', 'ACTIVO');
INSERT INTO usuario_entidad (id_usuario_entidad, id_usuario, id_entidad, cargo, estado)
VALUES (2, 8, 2, 'Coordinador Academico', 'ACTIVO');

INSERT INTO rubrica (id_rubrica, nombre, version, estado)
VALUES (1, 'Rubrica Practica Pedagogica', '1.0', 'ACTIVA');
INSERT INTO criterio_rubrica (id_criterio, nombre, descripcion, ponderacion, id_rubrica)
VALUES (1, 'Planeacion', 'Planifica actividades de aula coherentes', 25.00, 1);
INSERT INTO criterio_rubrica (id_criterio, nombre, descripcion, ponderacion, id_rubrica)
VALUES (2, 'Ejecucion', 'Desarrolla actividades con dominio del grupo', 25.00, 1);
INSERT INTO criterio_rubrica (id_criterio, nombre, descripcion, ponderacion, id_rubrica)
VALUES (3, 'Comunicacion', 'Comunica con claridad y asertividad', 25.00, 1);
INSERT INTO criterio_rubrica (id_criterio, nombre, descripcion, ponderacion, id_rubrica)
VALUES (4, 'Responsabilidad', 'Cumple tiempos y compromisos', 25.00, 1);

INSERT INTO practica (id_practica, periodo, fecha_inicio, fecha_fin, horas_objetivo, horas_acumuladas, estado, id_estudiante, id_entidad, id_docente)
VALUES (1, '2026-1', TO_DATE('01/02/2026','DD/MM/YYYY'), NULL, 120, 8, 'EN_CURSO', 1, 1, 1);
INSERT INTO practica (id_practica, periodo, fecha_inicio, fecha_fin, horas_objetivo, horas_acumuladas, estado, id_estudiante, id_entidad, id_docente)
VALUES (2, '2026-1', TO_DATE('01/02/2026','DD/MM/YYYY'), NULL, 120, 4, 'EN_CURSO', 2, 2, 1);

INSERT INTO actividad_practica (id_actividad, fecha_actividad, descripcion, horas, estado_validacion, comentario_docente, id_practica)
VALUES (1, TO_DATE('10/02/2026','DD/MM/YYYY'), 'Observacion de aula y registro de diario de campo', 4, 'VALIDADA', 'Actividad pertinente', 1);
INSERT INTO actividad_practica (id_actividad, fecha_actividad, descripcion, horas, estado_validacion, comentario_docente, id_practica)
VALUES (2, TO_DATE('15/02/2026','DD/MM/YYYY'), 'Planeacion de secuencia didactica', 4, 'VALIDADA', 'Buena estructuracion', 1);
INSERT INTO actividad_practica (id_actividad, fecha_actividad, descripcion, horas, estado_validacion, comentario_docente, id_practica)
VALUES (3, TO_DATE('16/02/2026','DD/MM/YYYY'), 'Ejecucion de actividad ludica', 4, 'PENDIENTE', NULL, 2);

INSERT INTO evidencia (id_evidencia, nombre_archivo, tipo_archivo, url_archivo, fecha_carga, id_actividad)
VALUES (1, 'diario_campo_10022026.pdf', 'PDF', '/evidencias/diario_campo_10022026.pdf', SYSDATE, 1);
INSERT INTO evidencia (id_evidencia, nombre_archivo, tipo_archivo, url_archivo, fecha_carga, id_actividad)
VALUES (2, 'planeacion_15022026.pdf', 'PDF', '/evidencias/planeacion_15022026.pdf', SYSDATE, 2);

INSERT INTO evaluacion (id_evaluacion, fecha_evaluacion, puntaje_total, observaciones, estado, id_practica, id_docente, id_rubrica)
VALUES (1, TO_DATE('20/02/2026','DD/MM/YYYY'), 88.50, 'Desempeno sobresaliente en planeacion y comunicacion', 'EMITIDA', 1, 1, 1);
INSERT INTO detalle_evaluacion (id_detalle, puntaje_criterio, observacion, id_evaluacion, id_criterio)
VALUES (1, 22.00, 'Planeacion adecuada', 1, 1);
INSERT INTO detalle_evaluacion (id_detalle, puntaje_criterio, observacion, id_evaluacion, id_criterio)
VALUES (2, 21.50, 'Buena ejecucion en aula', 1, 2);
INSERT INTO detalle_evaluacion (id_detalle, puntaje_criterio, observacion, id_evaluacion, id_criterio)
VALUES (3, 23.00, 'Muy buena comunicacion', 1, 3);
INSERT INTO detalle_evaluacion (id_detalle, puntaje_criterio, observacion, id_evaluacion, id_criterio)
VALUES (4, 22.00, 'Responsabilidad constante', 1, 4);

INSERT INTO validacion_institucional (id_validacion, fecha_validacion, asistencia, estado_validacion, observacion, id_practica, id_entidad, id_usuario_institucion)
VALUES (1, TO_DATE('18/02/2026','DD/MM/YYYY'), 'SI', 'VALIDADA', 'Asistencia y desempeno adecuados en campo', 1, 1, 7);
INSERT INTO validacion_institucional (id_validacion, fecha_validacion, asistencia, estado_validacion, observacion, id_practica, id_entidad, id_usuario_institucion)
VALUES (2, TO_DATE('18/02/2026','DD/MM/YYYY'), 'SI', 'PENDIENTE', 'Pendiente visto bueno final de la institucion', 2, 2, 8);

COMMIT;

PROMPT Datos iniciales cargados correctamente.
