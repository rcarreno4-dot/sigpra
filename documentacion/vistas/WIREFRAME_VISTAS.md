# Wireframes de Vistas - SIGPRA (Version Reducida)

## Regla de logos
- Vista de inicio (`LoginFrame`): usar `logo2`.
- Resto de vistas (`BaseFrame`, `StudentSelfRegistrationFrame` y demas): usar `logo`.

## 1) Inicio de sesion (`LoginFrame`) - Logo: `logo2`
```text
+--------------------------------------------------------------------------------+
| [Panel contexto azul]                                  [Panel acceso]           |
| [LOGO2 SIGPRA]                                         Iniciar sesion            |
| SIGPRA - Contexto del sistema                          Correo institucional      |
| - Registro publico                                     [____________________]    |
| - Estudiante registra practica                         Contrasena                |
| - Docente valida                                       [____________________]    |
| - Directora aprueba cierre                             Rol [Estudiante v]        |
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
| Header comun + Nav directora                                                   |
| Estudiante [v] | Nombre | Codigo | Programa | Periodo                         |
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
| Directora: KPIs (Pendientes, Validadas, Rechazadas)                            |
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
| Header comun + Nav: Dashboard | Validar | Bitacora | Cerrar sesion             |
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

## 9) Dashboard directora (`DirectorDashboardFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                   |
| [ KPI Pend. aprobacion ] [ KPI En curso ] [ KPI Finalizadas ]                  |
| -----------------------------------------------------------------------------  |
| Estado general por programa                                                    |
| [ Tabla ]                                                                      |
+--------------------------------------------------------------------------------+
```

## 10) Aprobacion de cierre (`DirectorApprovalFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                   |
| Practicas pendientes por aprobacion de directora                               |
| [ Tabla ]                                                                      |
| [ Ver revision docente ] [ Aprobar cierre ] [ Devolver a seguimiento ]         |
+--------------------------------------------------------------------------------+
```

## 11) Registro de docentes (`TeacherRegistrationFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                   |
| Crear docente asesor                                                           |
| Nombre completo      [________________________]                                |
| Correo institucional [________________________]                                |
| Contrasena           [________________________]                                |
| Area de asesoria     [________________________]                                |
|                        [ Registrar docente ] [ Limpiar ]                       |
+--------------------------------------------------------------------------------+
```

## 12) Reportes (`ReportsFrame`) - Logo: `logo`
```text
+--------------------------------------------------------------------------------+
| Header comun + Nav directora                                                   |
| Filtros: Periodo [v] | Programa [v] | Estado [v]                               |
| Consolidado [ Tabla ]                                                           |
| [ Consultar ] [ Exportar PDF ] [ Exportar Excel ]                              |
+--------------------------------------------------------------------------------+
```

## 13) Menu de vistas (`AbrirMenuVistas`) - Logo: icono de ventana
```text
+----------------------------------------------------------------------------+
| SIGPRA - Abrir vistas una a una                                            |
| [Login] [Autoregistro] [Dashboard estudiante] [Registro practica] ...      |
| (Grid de botones para abrir cada vista de prueba)                          |
+----------------------------------------------------------------------------+
```

## Modulos fuera de alcance en esta entrega
- [ELIMINADO] Evaluacion por rubrica.
- [ELIMINADO] Plantillas de bitacora.
- [ELIMINADO] Consolidacion de hallazgos.
