# Casos de Uso - Sistema de Gestion de Practicas Academicas

## Actores (actualizados segun reunion 2026-03-10)
- Director del Programa (interno)
- Tutor Academico (interno)
- Estudiante de Licenciatura (interno)
- Asesor Pedagogico de Institucion Receptora (externo, acceso restringido)

## UC-01 Autenticar Usuario (tecnico)
- Actor principal: Director, Tutor Academico, Estudiante.
- Actor secundario: Asesor Pedagogico (si fue habilitado por el Director).
- Proposito: Validar credenciales y habilitar sesion por perfil.
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
- Nota UML: este caso de uso se modela como `<<include>>` de los casos funcionales de negocio.

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
- Actor principal: Director del Programa.
- Actor secundario: Estudiante de Licenciatura.
- Proposito: Crear el registro formal de practica y dejarla lista para asignacion.
- Precondiciones: Director autenticado y estudiante existente.
- Flujo principal:
1. El Director selecciona estudiante y periodo academico.
2. Registra institucion, fechas y datos base de practica.
3. El sistema crea practica en estado PENDIENTE.
- Flujos alternos:
1. Datos incompletos: sistema solicita completar.
- Postcondiciones: Practica registrada.
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<include>>` UC-01 y `<<extend>>` de UC-04 cuando la practica ya existe.

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
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<extend>>` de UC-06.

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
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<include>>` UC-01 y `<<include>>` UC-09.

## UC-11 Consultar Estado e Historial
- Actor principal: Director, Tutor Academico, Estudiante.
- Proposito: Consultar trazabilidad de practicas y bitacoras.
- Precondiciones: Usuario autenticado.
- Flujo principal:
1. El actor aplica filtros (periodo, semestre, estado, modalidad).
2. El sistema muestra historial y detalle.
- Postcondiciones: Informacion consultada.
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<include>>` UC-01.

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
- Relacion UML: `<<include>>` UC-01 y `<<extend>>` UC-09.

## UC-14 Gestionar Convenios con Instituciones
- Actor principal: Director del Programa.
- Proposito: Registrar y mantener convenios de practica con instituciones receptoras.
- Precondiciones: Director autenticado.
- Flujo principal:
1. El Director registra o actualiza datos del convenio.
2. El sistema valida vigencia y estado.
3. El sistema habilita la institucion para nuevas practicas.
- Postcondiciones: Convenio disponible para asignacion.
- Relacion UML: `<<include>>` UC-01.

## UC-15 Gestionar Banco de Preguntas de Bitacora
- Actor principal: Director del Programa.
- Actor secundario: Tutor Academico.
- Proposito: Administrar preguntas orientadoras para bitacora investigativa.
- Precondiciones: Usuario con permiso de configuracion.
- Flujo principal:
1. El actor crea o ajusta preguntas por semestre y modalidad.
2. Publica la version activa de la plantilla.
- Postcondiciones: Plantilla vigente disponible para estudiantes.
- Relacion UML: `<<include>>` UC-01.

## UC-16 Retroalimentar Respuestas de Bitacora
- Actor principal: Tutor Academico.
- Actor secundario: Asesor Pedagogico.
- Proposito: Registrar comentarios de mejora sobre respuestas del estudiante.
- Precondiciones: Bitacora registrada por el estudiante.
- Flujo principal:
1. El Tutor revisa respuestas y evidencias.
2. Registra retroalimentacion puntual por actividad.
3. El sistema notifica al estudiante.
- Postcondiciones: Retroalimentacion almacenada en la trazabilidad de la practica.
- Relacion UML: `<<include>>` UC-01 y `<<extend>>` UC-08.

## UC-17 Registrar Visitas Pedagogicas
- Actor principal: Tutor Academico.
- Actor secundario: Director del Programa.
- Proposito: Registrar visitas de seguimiento pedagogico a la institucion receptora.
- Precondiciones: Practica activa y tutor asignado.
- Flujo principal:
1. El Tutor agenda y registra la visita.
2. Documenta hallazgos y compromisos.
3. El sistema actualiza el historial de seguimiento.
- Postcondiciones: Visita registrada para consulta y auditoria.
- Relacion UML: `<<include>>` UC-01.

## Nota de Alcance (2026-04-20)
- Se revisaron observaciones de retroalimentacion en `Carreño-Mariño.Rojas.pdf`.
- Para esta version de SIGPRA se mantiene asignacion directa estudiante-practica-docente, sin modulo de grupos.
- Los casos de uso de convenios, banco de preguntas y visitas pedagogicas quedan definidos para cierre documental y evolucion funcional.
