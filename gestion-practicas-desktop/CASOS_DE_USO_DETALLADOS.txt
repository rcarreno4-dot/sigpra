# Casos de Uso - Sistema de Gestion de Practicas Academicas (Version Reducida)

## Alcance de esta version
- Se mantiene: login, registro de practica, bitacora, evidencias, validacion docente, aprobacion final por directora y reportes.
- Registro publico: solo estudiante desde pantalla principal.
- Registro de docentes: solo directora.
- Se elimina: modulo de rubrica y evaluacion detallada por criterio.

## Actores
- Estudiante
- Docente asesor
- Directora de programa

## UC-01 Iniciar Sesion
- Actor principal: Estudiante, Docente asesor, Directora.
- Proposito: Autenticar usuario y habilitar dashboard por rol.
- Precondiciones: Usuario activo con credenciales validas.
- Flujo principal:
1. El usuario ingresa correo, contrasena y rol.
2. El sistema valida credenciales.
3. El sistema redirige al panel correspondiente.
- Postcondicion: Sesion iniciada.

## UC-02 Registrar Estudiante (Autoregistro)
- Actor principal: Estudiante.
- Proposito: Permitir registro publico de cuenta estudiantil.
- Precondiciones: No tener cuenta previa registrada.
- Flujo principal:
1. El estudiante diligencia nombre, correo, codigo, programa y semestre.
2. El sistema valida datos obligatorios.
3. El sistema crea la cuenta de estudiante.
- Postcondicion: Cuenta estudiantil creada.

## UC-03 Registrar Docente Asesor
- Actor principal: Directora.
- Proposito: Registrar docentes disponibles para asignacion de practicas.
- Precondiciones: Directora autenticada.
- Flujo principal:
1. La directora registra datos del docente.
2. El sistema valida datos obligatorios.
3. El sistema guarda el docente asesor.
- Postcondicion: Docente disponible para asignaciones.

## UC-04 Registrar Practica Academica
- Actor principal: Directora.
- Proposito: Crear practica academica y asociar datos base del proceso.
- Precondiciones: Estudiante existente y directora autenticada.
- Flujo principal:
1. La directora selecciona estudiante y periodo.
2. Registra entidad, docente, fechas y objetivo.
3. El sistema guarda la practica en estado `PENDIENTE` o `EN_CURSO` segun configuracion.
- Postcondicion: Practica registrada.

## UC-05 Registrar Bitacora y Horas
- Actor principal: Estudiante.
- Proposito: Registrar actividades y horas de avance.
- Precondiciones: Practica activa del estudiante.
- Flujo principal:
1. El estudiante registra fecha, actividad, descripcion y horas.
2. El sistema valida formato y rango de horas.
3. El sistema guarda entrada en estado `PENDIENTE`.
- Postcondicion: Bitacora registrada para revision docente.

## UC-06 Cargar Evidencias
- Actor principal: Estudiante.
- Proposito: Asociar archivo/ruta de evidencia a una entrada de bitacora.
- Precondiciones: Bitacora existente.
- Flujo principal:
1. El estudiante selecciona actividad.
2. Registra tipo y ruta de evidencia.
3. El sistema valida y guarda la evidencia.
- Postcondicion: Evidencia asociada a la actividad.

## UC-07 Validar o Rechazar Actividades
- Actor principal: Docente asesor.
- Proposito: Revisar entradas de bitacora y decidir validacion.
- Precondiciones: Entradas pendientes asignadas al docente.
- Flujo principal:
1. El docente consulta cola de validacion.
2. Revisa actividad, horas y evidencia.
3. Marca `VALIDADA` o `RECHAZADA` con observacion.
4. El sistema actualiza estado y trazabilidad.
- Postcondicion: Entrada validada o rechazada.

## UC-08 Aprobar Cierre de Practica
- Actor principal: Directora.
- Proposito: Decidir cierre final cuando se cumplan horas y validaciones.
- Precondiciones: Practica en `PENDIENTE_APROBACION`.
- Flujo principal:
1. La directora revisa consolidado de horas y observaciones docentes.
2. Aprueba cierre (`FINALIZADA`) o devuelve a seguimiento (`EN_CURSO`).
3. El sistema registra la decision.
- Postcondicion: Practica cerrada o devuelta.

## UC-09 Consultar Estado e Historial
- Actor principal: Estudiante, Docente asesor, Directora.
- Proposito: Consultar trazabilidad por estado/periodo.
- Precondiciones: Sesion iniciada.
- Flujo principal:
1. El usuario aplica filtros de consulta.
2. El sistema muestra historial y estado actual.
- Postcondicion: Estado consultado.

## UC-10 Generar Reportes por Filtros
- Actor principal: Directora.
- Proposito: Consultar consolidado por periodo, programa y estado operativo con avance de horas.
- Precondiciones: Directora autenticada.
- Flujo principal:
1. Selecciona filtros (periodo, programa, estado).
2. El sistema consolida total practicas, horas y distribucion por estado.
3. La directora consulta resultados.
- Postcondicion: Reporte visualizado.

## Trazabilidad con reduccion (proyecto_integrador_completo)
- [SE_MANTIENE] login, registro de practica, bitacora, evidencias, validacion y reportes.
- [MODIFICADO] registro publico solo para estudiante.
- [MODIFICADO] registro de docentes solo por directora.
- [MODIFICADO] cierre final aprobado por directora.
- [ELIMINADO] modulo de rubrica.
- [ELIMINADO] evaluacion detallada por criterio.
