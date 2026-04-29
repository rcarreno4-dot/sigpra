SET DEFINE OFF;

PROMPT ==================================
PROMPT Practicas por estudiante y estado
PROMPT ==================================
SELECT
  p.id_practica,
  u.nombres || ' ' || u.apellidos AS estudiante,
  p.periodo,
  p.estado,
  p.horas_acumuladas,
  p.horas_objetivo
FROM practica p
JOIN estudiante e ON e.id_estudiante = p.id_estudiante
JOIN usuario u ON u.id_usuario = e.id_usuario
ORDER BY p.id_practica;

PROMPT ==================================
PROMPT Entidades receptoras con cupos
PROMPT ==================================
SELECT
  id_entidad,
  nombre,
  ciudad,
  cupos_totales,
  cupos_disponibles
FROM entidad_receptora
WHERE estado = 'ACTIVA'
ORDER BY cupos_disponibles DESC;

PROMPT ==================================
PROMPT Actividades pendientes de validar
PROMPT ==================================
SELECT
  ap.id_actividad,
  p.id_practica,
  u.nombres || ' ' || u.apellidos AS estudiante,
  ap.fecha_actividad,
  ap.horas,
  ap.estado_validacion
FROM actividad_practica ap
JOIN practica p ON p.id_practica = ap.id_practica
JOIN estudiante e ON e.id_estudiante = p.id_estudiante
JOIN usuario u ON u.id_usuario = e.id_usuario
WHERE ap.estado_validacion = 'PENDIENTE'
ORDER BY ap.fecha_actividad;

PROMPT ==================================
PROMPT Validaciones institucionales
PROMPT ==================================
SELECT
  vi.id_validacion,
  vi.id_practica,
  er.nombre AS entidad_receptora,
  ui.nombres || ' ' || ui.apellidos AS responsable_institucion,
  vi.asistencia,
  vi.estado_validacion,
  vi.fecha_validacion
FROM validacion_institucional vi
JOIN entidad_receptora er ON er.id_entidad = vi.id_entidad
JOIN usuario ui ON ui.id_usuario = vi.id_usuario_institucion
ORDER BY vi.id_validacion;

PROMPT ==================================
PROMPT Reporte de evaluaciones emitidas
PROMPT ==================================
SELECT
  ev.id_evaluacion,
  p.id_practica,
  ue.nombres || ' ' || ue.apellidos AS estudiante,
  ud.nombres || ' ' || ud.apellidos AS docente,
  ev.puntaje_total,
  ev.estado
FROM evaluacion ev
JOIN practica p ON p.id_practica = ev.id_practica
JOIN estudiante e ON e.id_estudiante = p.id_estudiante
JOIN usuario ue ON ue.id_usuario = e.id_usuario
JOIN docente_asesor da ON da.id_docente = ev.id_docente
JOIN usuario ud ON ud.id_usuario = da.id_usuario
ORDER BY ev.id_evaluacion;

PROMPT ==================================
PROMPT Cumplimiento por semestre/modalidad
PROMPT ==================================
SELECT
  p.periodo,
  p.semestre_practica,
  p.modalidad,
  COUNT(*) AS practicas,
  ROUND(SUM(p.horas_acumuladas), 2) AS horas_acumuladas,
  ROUND(SUM(p.horas_objetivo), 2) AS horas_objetivo,
  ROUND((SUM(p.horas_acumuladas) / NULLIF(SUM(p.horas_objetivo), 0)) * 100, 2) AS avance_pct
FROM practica p
GROUP BY p.periodo, p.semestre_practica, p.modalidad
ORDER BY p.periodo, p.semestre_practica, p.modalidad;

PROMPT ==================================
PROMPT Jornadas con firma institucional faltante
PROMPT ==================================
SELECT
  jp.id_jornada,
  jp.fecha_jornada,
  jp.id_practica,
  jp.hora_ingreso,
  jp.hora_salida,
  jp.horas_registradas,
  jp.estado_validacion
FROM jornada_practica jp
WHERE jp.firma_ingreso_nombre IS NULL
   OR jp.firma_salida_nombre IS NULL
   OR NVL(TRIM(jp.firma_ingreso_nombre), 'X') = 'X'
   OR NVL(TRIM(jp.firma_salida_nombre), 'X') = 'X';

PROMPT ==================================
PROMPT Evidencias destacadas para pares
PROMPT ==================================
SELECT
  p.periodo,
  p.id_practica,
  e.id_evidencia,
  e.nombre_archivo,
  e.categoria,
  e.url_archivo
FROM evidencia e
JOIN actividad_practica ap ON ap.id_actividad = e.id_actividad
JOIN practica p ON p.id_practica = ap.id_practica
WHERE e.es_destacada = 'SI'
ORDER BY p.periodo, p.id_practica, e.id_evidencia;

PROMPT ==================================
PROMPT Estado de reportes institucionales
PROMPT ==================================
SELECT
  rp.periodo,
  rp.tipo_reporte,
  rp.titulo,
  rp.fecha_limite,
  rp.fecha_entrega,
  rp.estado,
  u.nombres || ' ' || u.apellidos AS responsable
FROM reporte_periodo_practica rp
JOIN usuario u ON u.id_usuario = rp.id_responsable
ORDER BY rp.periodo, rp.tipo_reporte;
