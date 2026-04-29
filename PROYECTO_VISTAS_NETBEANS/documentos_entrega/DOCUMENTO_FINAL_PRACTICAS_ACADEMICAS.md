# Documento Final - Sistema de Gestion de Practicas Academicas

## Tabla de Contenido
1. Introduccion  
1.1 Proposito  
1.2 Alcance  
1.3 Definiciones, Acronimos y Abreviaturas  
1.4 Referencias  
1.5 Apreciacion Global  
2. Descripcion del Problema  
2.1 Perspectiva del Producto  
2.2 Funciones del Producto  
2.3 Caracteristicas de los Usuarios  
2.4 Restricciones  
2.5 Suposiciones y Dependencias  
3. Objetivos  
3.1 Objetivo General  
3.2 Objetivos Especificos  
4. Justificacion  
5. Propuesta del Plan del Proyecto  
6. Analisis de Requerimientos del Software  
7. Diseno UML  
7.1 Diagrama de Casos de Uso  
7.2 Diagrama de Dominio  
8. Modelamiento de la Base de Datos  
8.1 Modelo Entidad-Relacion  
8.2 Modelo Relacional  
8.3 Diccionario de Datos  
9. Diseno de Interfaz  
10. Referencias Bibliograficas  
11. Anexos  
12. Actualizacion Funcional 2026-03-10
13. Vistas JSP Agregadas y Ajustadas

---

## 1. Introduccion
Las practicas academicas son un componente esencial en la formacion de los estudiantes de educacion superior, ya que permiten aplicar conocimientos en contextos reales. Sin embargo, su gestion suele realizarse con herramientas dispersas (correos, hojas de calculo y documentos fisicos), lo que dificulta el control, el seguimiento y la trazabilidad.

Este proyecto propone el desarrollo de un sistema de software que centralice la gestion de practicas academicas para programas de licenciatura, mejorando la eficiencia administrativa y academica, y fortaleciendo la toma de decisiones con informacion confiable y oportuna.

### 1.1 Proposito
Desarrollar un sistema que permita gestionar de manera eficiente las practicas academicas de los estudiantes, facilitando el registro, seguimiento, control y evaluacion de las actividades realizadas durante su proceso formativo.

### 1.2 Alcance
El sistema permitira:
- Registrar y administrar usuarios por rol.
- Gestionar entidades receptoras y cupos disponibles.
- Asignar estudiantes a tutor academico y asesor pedagogico de institucion.
- Registrar actividades, horas y evidencias.
- Evaluar y validar practicas.
- Consultar estados y generar reportes.

La solucion se implementara con arquitectura cliente-servidor y acceso por rol (director del programa, tutor academico y estudiante), con participacion externa restringida del asesor pedagogico de institucion receptora.

### 1.3 Definiciones, Acronimos y Abreviaturas
- Practica academica: actividad formativa en contexto real supervisada por la universidad.
- Entidad receptora: institucion donde el estudiante realiza su practica.
- Tutor academico: profesor universitario que acompania, valida y evalua el proceso de practica.
- Asesor pedagogico: profesional de la institucion receptora con acceso externo restringido para observacion y concepto.
- Director del programa: responsable academico-administrativo del programa.
- Evidencia: archivo o registro que soporta actividades realizadas.
- UML: Unified Modeling Language.
- ER: Modelo Entidad-Relacion.
- SRS: Especificacion de Requisitos de Software.

### 1.4 Referencias
- Reglamento institucional de practicas academicas.
- Normativa interna de evaluacion y seguimiento estudiantil.
- Estandares de ingenieria de software y modelado UML.

### 1.5 Apreciacion Global
El documento presenta el problema, los objetivos, el analisis de requisitos, el diseno UML, el modelo de base de datos y el diseno de interfaz, con el fin de definir una solucion integral para la gestion de practicas academicas.

---

## 2. Descripcion del Problema
En muchas instituciones, la gestion de practicas academicas se realiza con procesos manuales y dispersos. Esto genera duplicidad de informacion, errores de seguimiento, retrasos en validaciones y dificultad para generar reportes consolidados. Como resultado, los actores institucionales invierten mas tiempo en tareas operativas que en acompanamiento academico.

Se requiere una plataforma centralizada que mejore el control del proceso completo: asignacion, ejecucion, validacion, evaluacion y reporte.

### 2.1 Perspectiva del Producto
Sistema web institucional, integrado a la operacion academica, con acceso por roles y base de datos centralizada.

### 2.2 Funciones del Producto
- Autenticacion y control de acceso por rol.
- Gestion de accesos, asignaciones y trazabilidad de practica.
- Registro de practicas, actividades y evidencias.
- Validacion de horas y evaluaciones por rubrica.
- Consulta de estado e historial.
- Generacion de reportes academicos y administrativos.

### 2.3 Caracteristicas de los Usuarios
- Director del programa: supervisa en tiempo real avances, horas, evaluaciones y aprueba el cierre de practicas.
- Tutor academico: valida actividades, registra observaciones y evalua practicas con rubrica.
- Estudiantes de licenciatura: registran bitacora, horas y evidencias; consultan estado de su practica.
- Asesor pedagogico (externo): registra observaciones y concepto institucional con acceso restringido.

### 2.4 Restricciones
- Cumplimiento de politicas institucionales y proteccion de datos.
- Uso de credenciales institucionales.
- Dependencia de conectividad y disponibilidad del servidor.
- Acceso restringido segun rol.

### 2.5 Suposiciones y Dependencias
- Existencia de lineamientos institucionales claros de practica.
- Disponibilidad de infraestructura tecnologica minima.
- Participacion activa de tutores academicos y asesores pedagogicos.

---

## 3. Objetivos

### 3.1 Objetivo General
Desarrollar un software de gestion de practicas academicas que optimice el registro, seguimiento y evaluacion de las actividades realizadas por los estudiantes.

### 3.2 Objetivos Especificos
- Disenar una interfaz intuitiva para todos los roles.
- Implementar modulos de usuarios, entidades, practicas y actividades.
- Implementar evaluacion con rubricas parametrizables.
- Integrar carga y gestion de evidencias.
- Generar reportes para seguimiento y acreditacion.

---

## 4. Justificacion
El sistema permitira centralizar la informacion de practicas academicas, reducir errores administrativos, mejorar la trazabilidad del proceso y apoyar la toma de decisiones con reportes confiables. Ademas, fortalecera la calidad del acompanamiento formativo y la eficiencia institucional.

---

## 5. Propuesta del Plan del Proyecto

### Metodologia
1. Recoleccion y analisis de requisitos.  
2. Diseno del sistema (arquitectura, UML, BD, prototipos).  
3. Implementacion por modulos.  
4. Pruebas funcionales y ajustes.  
5. Despliegue y validacion institucional.

### Recursos tecnicos
- Lenguaje: Java.
- IDE: NetBeans.
- Base de datos: Oracle SQL.
- Control de versiones: GitHub.
- Diseno UML: Astah.
- Colaboracion: Google Docs, Canva.

---

## 6. Analisis de Requerimientos del Software

### Requisitos funcionales
- RF01: Iniciar y cerrar sesion.
- RF02: Gestionar accesos de practica (tutores y asesores pedagogicos).
- RF03: Configurar plantillas de bitacora por semestre y modalidad.
- RF04: Registrar practicas academicas.
- RF05: Asignar tutor academico y asesor pedagogico a cada practica.
- RF06: Registrar bitacora y horas con ingreso/salida.
- RF07: Vincular evidencias por archivo o enlace institucional.
- RF08: Validar/rechazar bitacoras y horas.
- RF09: Evaluar practicas por rubrica.
- RF10: Cerrar/reabrir practica por decision de direccion.
- RF11: Consultar estado e historial.
- RF12: Generar reportes institucionales (R1, R2, R3).
- RF13: Registrar observaciones institucionales por asesor pedagogico.
- RF14: Consolidar hallazgos (vacios, tensiones, fortalezas) para mejora del programa.

### Requisitos no funcionales
- RNF01: Interfaz usable y responsive.
- RNF02: Seguridad con control de acceso por rol.
- RNF03: Tiempos de respuesta adecuados (<3 s en consultas comunes).
- RNF04: Integridad y consistencia de datos.
- RNF05: Trazabilidad de acciones (auditoria).
- RNF06: Mantenibilidad modular.
- RNF07: Disponibilidad durante periodos academicos.

---

## 7. Diseno UML

### 7.1 Diagrama de Casos de Uso
Se propone un diagrama general con actores:
- Director del programa
- Tutor academico
- Estudiantes de licenciatura
- Asesor pedagogico de institucion receptora (externo)

Casos principales:
- Iniciar sesion.
- Gestionar accesos de practica.
- Configurar plantilla de bitacora.
- Registrar practica.
- Registrar bitacora y horas.
- Vincular evidencia.
- Validar bitacora y horas.
- Evaluar practica.
- Cerrar/reabrir practica.
- Registrar observacion institucional.
- Consultar historial.
- Generar reportes.

### 7.2 Diagrama de Dominio
Entidades de dominio:
- Usuario
- Estudiante
- TutorAcademico
- AsesorPedagogico
- DirectorPrograma
- EntidadReceptora
- Practica
- ActividadPractica
- Evidencia
- Evaluacion
- Rubrica
- CriterioRubrica
- DetalleEvaluacion

Relaciones clave:
- Estudiante 1..N Practica.
- Practica 1..N Actividad.
- Actividad 1..N Evidencia.
- Practica 1..N Evaluacion.
- Evaluacion 1..N Detalle por criterio.

---

## 8. Modelamiento de la Base de Datos

### 8.1 Modelo Entidad-Relacion
El modelo ER incluye las entidades descritas en el dominio y sus claves primarias/foraneas para garantizar trazabilidad completa del proceso de practica.

### 8.2 Modelo Relacional
Tablas principales:
- usuario
- estudiante
- docente_asesor
- director_programa
- entidad_receptora
- usuario_entidad
- practica
- actividad_practica
- evidencia
- rubrica
- criterio_rubrica
- evaluacion
- detalle_evaluacion
- validacion_institucional

### 8.3 Diccionario de Datos (resumen)
- `usuario.rol`: ESTUDIANTE, TUTOR_ACADEMICO, DIRECTOR, ASESOR_PEDAGOGICO.
- `practica.estado`: PENDIENTE, EN_CURSO, FINALIZADA, APROBADA, RECHAZADA.
- `actividad_practica.estado_validacion`: PENDIENTE, VALIDADA, RECHAZADA.
- `evaluacion.puntaje_total`: rango 0-100.
- Fechas almacenadas en tipo `DATE` y presentadas en interfaz como `DD/MM/AA`.

---

## 9. Diseno de Interfaz

### Pantallas principales
- Inicio de sesion.
- Dashboard por rol.
- Gestion de accesos de practica.
- Configuracion de plantilla de bitacora.
- Registro de practica.
- Registro de bitacora y horas.
- Vinculacion de evidencias.
- Evaluacion por rubrica.
- Reportes y exportacion.

### Criterios de diseno
- Navegacion simple por menu lateral.
- Validaciones en formularios.
- Filtros de busqueda por periodo, programa y estado.
- Visualizacion diferenciada segun rol.
- Diseno responsive para escritorio y movil.

---

## 10. Referencias Bibliograficas
- IEEE Computer Society. (2014). *SWEBOK V3.0*.
- ISO/IEC/IEEE. (2017). *ISO/IEC/IEEE 12207:2017*.
- ISO/IEC. (2011). *ISO/IEC 25010:2011*.
- Sommerville, I. (2016). *Ingenieria de software* (10.a ed.).
- Pressman, R. S., & Maxim, B. R. (2020). *Ingenieria de software* (9.a ed.).
- OMG. (2017). *UML 2.5.1*.
- Universitaria de Investigacion y Desarrollo (UDI). (2018). *Reglamento de practicas profesionales*. https://www.udi.edu.co/images/manuales/reglamentos_2018/practicas.pdf

---

## 11. Anexos
- Anexo A: Actas de levantamiento de requisitos.
- Anexo B: Historias de usuario.
- Anexo C: Diagrama de casos de uso.
- Anexo D: Diagrama de dominio.
- Anexo E: Modelo ER final.
- Anexo F: Modelo relacional y diccionario completo.
- Anexo G: Prototipos de interfaz.
- Anexo H: Matriz de trazabilidad.
- Anexo I: Plan y resultados de pruebas.
- Anexo J: Manual de usuario.
- Anexo K: Manual tecnico.
- Anexo L: Evidencias y repositorio del proyecto.

---

## 12. Actualizacion Funcional 2026-03-10

Con base en la sesion de levantamiento del 10 de marzo de 2026, se incorporaron ajustes funcionales para reforzar trazabilidad, control de horas, evaluacion y reportes de practicas.

### 12.1 Hallazgos clave incorporados
- La bitacora debe ser investigativa, no solo descriptiva.
- El sistema debe manejar control de ingreso/salida con soporte institucional.
- Se requiere separar seguimiento por modalidad (presencial y virtual).
- Las evidencias pueden gestionarse por enlace institucional (ej. Drive), con opcion de destacar evidencias para pares academicos.
- Se requieren reportes institucionales minimos por periodo (R1, R2, R3) para seguimiento y renovacion del programa.

### 12.2 Ajustes aplicados en base de datos
Archivos actualizados:
- `bd/04_ajustes_reunion_20260310.sql`
- `bd/03_consultas_utiles.sql`
- `bd/00_instalar_bd.sql`
- `bd/README.md`

Cambios principales:
- Extension de `practica` con:
  - `semestre_practica`
  - `modalidad` (`PRESENCIAL` o `VIRTUAL`)
  - `arl_vigencia_hasta`
- Extension de `evidencia` con:
  - `es_destacada`
  - `categoria`
- Nuevas tablas:
  - `plantilla_bitacora`
  - `pregunta_plantilla_bitacora`
  - `jornada_practica`
  - `reporte_periodo_practica`
- Nuevas consultas para:
  - Cumplimiento por semestre/modalidad
  - Jornadas con firma faltante
  - Evidencias destacadas
  - Estado de reportes institucionales

### 12.3 Ajustes aplicados en interfaz web (JSP)
Archivos actualizados:
- `apache_tomcat_vistas/webapp/registro-actividad.jsp`
- `apache_tomcat_vistas/webapp/evaluacion-rubrica.jsp`
- `apache_tomcat_vistas/webapp/reportes.jsp`
- `apache_tomcat_vistas/webapp/assets/css/styles.css`

Mejoras funcionales:
- Formularios con campos de contexto de practica (semestre, modalidad, ARL, tutor y asesor).
- Registro diario de bitacora por preguntas orientadas a vacios, tensiones y mejora.
- Captura de horas con entrada/salida y estado de validacion.
- Flujo de evaluacion por rol y estado (borrador, enviada, validada, cerrada).
- Panel de reportes con indicadores y reportes obligatorios por periodo.

---

## 13. Vistas JSP Agregadas y Ajustadas

### 13.1 Vista 06 - Registro de Actividad
Ruta:
- `apache_tomcat_vistas/webapp/registro-actividad.jsp`

Secciones agregadas:
- Datos de practica (periodo, semestre, modalidad, institucion, tutor, asesor, ARL).
- Registro de bitacora investigativa (preguntas guiadas).
- Enlace de evidencia institucional y marca de evidencia destacada.
- Tabla de cumplimiento con horas y validaciones.

### 13.2 Vista 07 - Evaluacion por Rubrica
Ruta:
- `apache_tomcat_vistas/webapp/evaluacion-rubrica.jsp`

Secciones agregadas:
- Contexto de evaluacion por periodo, semestre, modalidad y rol evaluador.
- Rubrica integrada con ponderaciones y observaciones por criterio.
- Estados de evaluacion y decision de cierre por direccion.
- Resumen de puntaje total y avance de horas.

### 13.3 Vista 08 - Reportes
Ruta:
- `apache_tomcat_vistas/webapp/reportes.jsp`

Secciones agregadas:
- Filtros por periodo, semestre, modalidad, institucion, tutor y estado.
- KPIs de cumplimiento academico y operativo.
- Reportes obligatorios del periodo:
  - R1: Seguimiento de horas y asistencia
  - R2: Calidad de bitacora investigativa
  - R3: Consolidado de evaluacion por rubrica
- Tabla de analisis cualitativo (vacios, tensiones, fortalezas).

### 13.4 Nota de implementacion
Las vistas actuales corresponden a prototipos funcionales de interfaz (UI) en JSP. El siguiente paso tecnico es conectar estas pantallas con capa de negocio y persistencia (servlets/DAO/JDBC) para guardar y consultar datos reales desde Oracle.
