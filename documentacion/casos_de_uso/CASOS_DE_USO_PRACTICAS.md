# Casos de Uso - Sistema de Gestion de Practicas Academicas

## Actores (actualizados segun reunion 2026-03-10)
- Director del Programa (interno)
- Tutor Academico (interno)
- Estudiante de Licenciatura (interno)
- Asesor Pedagogico de Institucion Receptora (externo, acceso restringido)

## UC-01 Iniciar Sesion
- Actor principal: Director, Tutor Academico, Estudiante.
- Actor secundario: Asesor Pedagogico (si fue habilitado por el Director).
- Proposito: Permitir acceso al sistema segun perfil.
- Precondiciones: Usuario registrado y activo.
- Flujo principal:
1. El actor ingresa correo y contrasena.
2. El sistema valida credenciales.
3. El sistema identifica el perfil.
4. El sistema muestra la vista correspondiente.
- Flujos alternos:
1. Credenciales invalidas: mostrar mensaje y permitir reintento.
2. Usuario inactivo: bloquear acceso y mostrar aviso.
- Postcondiciones: Sesion iniciada.

## UC-02 Gestionar Accesos de Practica
- Actor principal: Director del Programa.
- Proposito: Habilitar o revocar acceso de tutores y asesores pedagogicos.
- Precondiciones: Director autenticado.
- Flujo principal:
1. El Director consulta usuarios de practica.
2. Selecciona usuario y define perfil de acceso.
3. El sistema guarda permisos.
- Flujos alternos:
1. Usuario duplicado o datos invalidos: sistema rechaza guardado.
- Postcondiciones: Accesos actualizados.

## UC-03 Configurar Plantilla de Bitacora
- Actor principal: Director del Programa.
- Actor secundario: Tutor Academico.
- Proposito: Definir preguntas de bitacora por semestre y modalidad.
- Precondiciones: Usuario con permisos de configuracion.
- Flujo principal:
1. El actor selecciona semestre/modalidad.
2. Crea o edita preguntas orientadoras.
3. Publica plantilla activa.
- Flujos alternos:
1. Plantilla incompleta: sistema bloquea publicacion.
- Postcondiciones: Plantilla disponible para estudiantes.

## UC-04 Registrar Practica Academica
- Actor principal: Estudiante de Licenciatura.
- Actor secundario: Director del Programa.
- Proposito: Crear el registro formal de practica.
- Precondiciones: Estudiante autenticado.
- Flujo principal:
1. El estudiante registra periodo, semestre, modalidad e institucion.
2. Confirma datos de inicio.
3. El sistema crea practica en estado PENDIENTE.
- Flujos alternos:
1. Datos incompletos: sistema solicita completar.
- Postcondiciones: Practica registrada.

## UC-05 Asignar Tutor y Asesor Pedagogico
- Actor principal: Director del Programa.
- Proposito: Definir responsables de seguimiento de cada practica.
- Precondiciones: Practica registrada.
- Flujo principal:
1. El Director consulta practicas pendientes.
2. Asigna Tutor Academico.
3. Habilita Asesor Pedagogico externo (solo observacion/calificacion).
4. El sistema cambia estado a EN_CURSO.
- Flujos alternos:
1. Sin disponibilidad de tutor: mantener en PENDIENTE.
- Postcondiciones: Asignacion completada.

## UC-06 Registrar Bitacora y Horas
- Actor principal: Estudiante de Licenciatura.
- Proposito: Registrar bitacora investigativa y horas de jornada.
- Precondiciones: Practica en estado EN_CURSO.
- Flujo principal:
1. El estudiante registra fecha, hora de ingreso y salida.
2. Responde preguntas de bitacora.
3. Envia registro para validacion.
- Flujos alternos:
1. Horas invalidas o datos faltantes: sistema rechaza registro.
- Postcondiciones: Bitacora registrada en estado PENDIENTE.

## UC-07 Vincular Evidencias
- Actor principal: Estudiante de Licenciatura.
- Proposito: Asociar evidencias (enlace o archivo) a la bitacora.
- Precondiciones: Registro de bitacora creado.
- Flujo principal:
1. El estudiante agrega enlace de evidencia institucional.
2. Marca evidencia destacada si aplica.
3. El sistema valida y guarda.
- Flujos alternos:
1. Enlace no valido: sistema rechaza.
- Postcondiciones: Evidencia asociada a la actividad.

## UC-08 Validar Horas y Bitacora
- Actor principal: Tutor Academico.
- Actor secundario: Asesor Pedagogico.
- Proposito: Aprobar o rechazar registro de actividad y horas.
- Precondiciones: Registros pendientes.
- Flujo principal:
1. El Tutor consulta registros pendientes.
2. Revisa horas, respuestas y evidencias.
3. Marca VALIDADA o RECHAZADA con observacion.
4. El sistema actualiza horas acumuladas.
- Flujos alternos:
1. Evidencia insuficiente: rechazar con ajuste.
- Postcondiciones: Registro validado o devuelto.

## UC-09 Evaluar Practica por Rubrica
- Actor principal: Tutor Academico.
- Actor secundario: Asesor Pedagogico.
- Proposito: Evaluar el desempeno del estudiante.
- Precondiciones: Practica con avance minimo definido.
- Flujo principal:
1. El Tutor selecciona practica y rubrica vigente.
2. Registra puntajes por criterio.
3. Consolida observaciones del asesor (si existen).
4. El sistema calcula puntaje total.
- Flujos alternos:
1. Rubrica no configurada: sistema bloquea evaluacion.
- Postcondiciones: Evaluacion almacenada.

## UC-10 Cerrar o Reabrir Practica
- Actor principal: Director del Programa.
- Actor secundario: Tutor Academico.
- Proposito: Emitir decision final de practica.
- Precondiciones: Evaluacion y horas consolidadas.
- Flujo principal:
1. El Director revisa resumen de la practica.
2. Define CERRADA/APROBADA o DEVUELTA CON AJUSTES.
3. El sistema notifica al estudiante.
- Flujos alternos:
1. Informacion incompleta: mantener en EN_CURSO.
- Postcondiciones: Estado final actualizado.

## UC-11 Consultar Estado e Historial
- Actor principal: Director, Tutor Academico, Estudiante.
- Proposito: Consultar trazabilidad de practicas y bitacoras.
- Precondiciones: Usuario autenticado.
- Flujo principal:
1. El actor aplica filtros (periodo, semestre, estado, modalidad).
2. El sistema muestra historial y detalle.
- Postcondiciones: Informacion consultada.

## UC-12 Generar Reportes Institucionales
- Actor principal: Director del Programa.
- Actor secundario: Tutor Academico.
- Proposito: Generar reportes para seguimiento y renovacion de programa.
- Precondiciones: Datos consolidados en el sistema.
- Flujo principal:
1. El actor selecciona tipo de reporte (R1, R2, R3).
2. Define filtros.
3. El sistema consolida resultados.
4. El actor exporta PDF/Excel.
- Flujos alternos:
1. Sin datos para filtros: mostrar reporte vacio.
- Postcondiciones: Reporte generado.

## UC-13 Registrar Observacion Institucional
- Actor principal: Asesor Pedagogico de Institucion Receptora.
- Actor secundario: Tutor Academico.
- Proposito: Registrar observacion y concepto del desempeno en campo.
- Precondiciones: Asesor habilitado por Director para una practica activa.
- Flujo principal:
1. El asesor consulta practicantes asignados.
2. Registra observacion y concepto.
3. El sistema notifica al Tutor Academico.
- Flujos alternos:
1. Practica no asignada al asesor: sistema bloquea registro.
- Postcondiciones: Observacion institucional registrada.
