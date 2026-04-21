# Wireframes de Vistas - SIGPRA

## Regla de logos
- Vista de inicio (`LoginFrame`): usar `logo2`.
- Resto de vistas (`BaseFrame`, `StudentSelfRegistrationFrame` y demás): usar `logo`.

## 1) Inicio de sesion (`LoginFrame`) - Logo: `logo2`
```text
+--------------------------------------------------------------------------------+
| [Panel contexto azul]                                  [Panel acceso]           |
| [LOGO2 SIGPRA]                                         Iniciar sesion            |
| SIGPRA - Contexto del sistema                          Correo institucional      |
| - Registro publico                                     [____________________]    |
| - Estudiante registra practica                         Contrasena                |
| - Docente valida                                       [____________________]    |
| - Directora aprueba                                    Rol [Estudiante v]        |
|                                                        Modo [Oracle/Demo v]      |
|                                                        [ Ingresar ]              |
|                                                        [ Registrarse estudiante ] |
+--------------------------------------------------------------------------------+
```

## 2) Autoregistro estudiante (`StudentSelfRegistrationFrame`) - Logo: `logo`
```text
+------------------------------------------------------------------------------+
| [LOGO]  Autoregistro de estudiante                                           |
| Nombre completo          [______________________________]                    |
| Correo institucional     [______________________________]                    |
| Contrasena               [______________________________]                    |
| Codigo estudiantil       [______________________________]                    |
| Programa                 [______________________________]                    |
| Semestre                 [______________________________]                    |
|                             [ Crear cuenta de estudiante ] [ Volver a inicio ]|
+------------------------------------------------------------------------------+
```

## 3) Dashboard estudiante (`StudentDashboardFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Titulo + subtitulo + [LOGO] + Rol activo                                      |
| Nav: Dashboard | Registro | Bitacora | Evidencias | Cerrar sesion             |
| [ KPI Horas registradas ] [ KPI Horas validadas ] [ KPI Estado practica ]     |
| -----------------------------------------------------------------------------  |
| Proximas tareas                                                                |
| [ Tabla de tareas ]                                                            |
+--------------------------------------------------------------------------------+
```

## 4) Registro de practica (`PracticeRegistrationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav por rol                                                     |
| (Director incluye selector de estudiante)                                      |
| Estudiante* / Nombre / Codigo / Programa / Periodo                             |
| Entidad receptora [v] | Docente asesor [v] | Director programa [v]             |
| Fecha inicio | Fecha fin | Objetivo de practica (textarea)                     |
|                              [ Guardar registro ] [ Limpiar ]                  |
+--------------------------------------------------------------------------------+
```

## 5) Bitacora (`BitacoraFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav por rol                                                     |
| Historial de bitacora                                                          |
| [ Tabla de entradas ]                                                          |
| -----------------------------------------------------------------------------  |
| Estudiante: formulario nueva entrada (fecha, actividad, horas, descripcion)    |
| Docente: accion "Ir a validacion"                                              |
| Director: KPIs (Pendientes, Validadas, Rechazadas)                             |
+--------------------------------------------------------------------------------+
```

## 6) Evidencias (`EvidenceFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav: Dashboard | Bitacora | Evidencias | Cerrar sesion          |
| Actividad asociada [v]                                                         |
| Tipo archivo [PDF/IMG/DOC/OTRO v]                                              |
| Ruta de archivo [____________________________________]                          |
| Comentario [ textarea ]                                                        |
|                              [ Cargar evidencia ]                              |
+--------------------------------------------------------------------------------+
```

## 7) Dashboard docente (`TeacherDashboardFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav: Dashboard | Validar | Rubrica | Bitacora | Cerrar sesion   |
| [ KPI Estudiantes asignados ] [ KPI Entradas por validar ] [ KPI En curso ]    |
| -----------------------------------------------------------------------------  |
| Cola de validacion                                                             |
| [ Tabla de cola ]                                                              |
+--------------------------------------------------------------------------------+
```

## 8) Validacion de actividades (`ValidationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav docente                                                     |
| Entradas de bitacora del docente                                               |
| [ Tabla ]                                                                      |
|                     [ Validar seleccion ] [ Rechazar seleccion ]               |
+--------------------------------------------------------------------------------+
```

## 9) Evaluacion por rubrica (`RubricEvaluationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav docente                                                     |
| Registro de evaluacion                                                         |
| Practica [v]                                                                   |
| Planeacion [1..5] | Ejecucion [1..5] | Reflexion [1..5] | Evidencias [1..5]    |
| Promedio [valor]                                                               |
| Observacion [ textarea ]                                                       |
| [ Guardar evaluacion ] [ Limpiar ]                                             |
| -----------------------------------------------------------------------------  |
| Evaluaciones recientes [ Tabla ]                                               |
+--------------------------------------------------------------------------------+
```

## 10) Dashboard director (`DirectorDashboardFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| [ KPI Pend. aprobacion ] [ KPI En curso ] [ KPI Finalizadas ]                  |
| -----------------------------------------------------------------------------  |
| Estado general por programa                                                     |
| [ Tabla ]                                                                       |
+--------------------------------------------------------------------------------+
```

## 11) Aprobacion de cierre (`DirectorApprovalFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| Practicas pendientes por aprobacion de directora                                |
| [ Tabla ]                                                                       |
|                  [ Aprobar cierre ] [ Devolver a seguimiento ]                  |
+--------------------------------------------------------------------------------+
```

## 12) Registro de docentes (`TeacherRegistrationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| Crear docente asesor                                                            |
| Nombre completo      [________________________]                                 |
| Correo institucional [________________________]                                 |
| Contrasena           [________________________]                                 |
| Area de asesoria     [________________________]                                 |
|                         [ Registrar docente ] [ Limpiar ]                       |
+--------------------------------------------------------------------------------+
```

## 13) Plantillas de bitacora (`TemplateConfigFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| Nueva plantilla                                                                 |
| Periodo | Modalidad | Nombre plantilla | Descripcion                            |
| [ Guardar plantilla ] [ Limpiar ]                                               |
| -----------------------------------------------------------------------------  |
| Plantillas configuradas [ Tabla ]                                               |
+--------------------------------------------------------------------------------+
```

## 14) Reportes (`ReportsFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| Filtros: Periodo [v] | Programa [v] | Estado [v]                                |
| Consolidado [ Tabla ]                                                           |
| [ Consultar ] [ Exportar PDF ] [ Exportar Excel ]                               |
+--------------------------------------------------------------------------------+
```

## 15) Consolidacion de hallazgos (`FindingsConsolidationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                    |
| Consolidado institucional [ Tabla ]                                             |
| -----------------------------------------------------------------------------  |
| Plan de mejora                                                                  |
| [ textarea accion ]                                                             |
| [ Registrar accion ] [ Recargar ]                                               |
+--------------------------------------------------------------------------------+
```

## 16) Menu de vistas (`AbrirMenuVistas`) - Logo: icono de ventana
```text
+----------------------------------------------------------------------------+
| SIGPRA - Abrir vistas una a una                                            |
| [Login] [Autoregistro] [Dashboard estudiante] [Registro practica] ...      |
| (Grid de botones para abrir cada vista de prueba)                          |
+----------------------------------------------------------------------------+
```
