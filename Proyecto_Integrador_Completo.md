# Segunda Entrega - Proyecto Integrador

**Universidad de Investigación y Desarrollo (UDI)**  
**Programa:** Licenciatura (Proyecto Integrador)  
**Fecha:** 31/03/2026  
**Equipo:** Rafael Fabian Carreño Barrera, Yeison Nicolas Marino Roberto, Santiago Andres Rojas  

---

## Tabla de contenido

1. Introducción  
1.1 Propósito  
1.2 Alcance  
1.3 Definiciones  
1.4 Referencias  
1.5 Apreciación global  
2. Descripción del problema  
2.1 Perspectiva del producto  
2.2 Funciones del producto  
2.3 Características de los usuarios  
2.4 Restricciones  
2.5 Suposiciones  
3. Objetivos  
4. Justificación  
5. Plan del proyecto  
6. Requerimientos  
7. Diseño UML  
8. Base de datos  
9. Diseño de interfaz  
10. Referencias  
11. Anexos  
12. Ajustes realizados  
13. Ajuste de reducción  

---

# 1. Introducción

Las prácticas académicas son un eje del proceso formativo porque conectan la teoría con contextos reales de trabajo. Actualmente, la gestión se realiza con archivos dispersos, lo que genera problemas de trazabilidad y eficiencia.

---

## 1.1 Propósito

Definir, diseñar y validar un prototipo funcional para la gestión de prácticas académicas.

---

## 1.2 Alcance

Incluye:
- Autenticación
- Registro de prácticas
- Bitácora
- Evidencias
- Validación
- Reportes

No incluye:
- Producción
- Integraciones externas
- App móvil

---

## 1.3 Definiciones

- Práctica académica  
- Bitácora  
- Evidencia  
- UML  
- ER  

---

## 1.4 Referencias

- SWEBOK  
- ISO 25010  
- UML 2.5.1  

---

## 1.5 Apreciación global

Documento consolidado con enfoque técnico y académico.

---

# 2. Descripción del problema

Problemas:
- Fragmentación de información  
- Falta de trazabilidad  
- Reportes deficientes  
- Alta carga operativa  

---

## 2.1 Perspectiva del producto

Sistema Java Swing con base de datos Oracle.

---

## 2.2 Funciones del producto

- Login  
- Registro  
- Bitácora  
- Evidencias  
- Validación  
- Reportes  

---

## 2.3 Usuarios

- Estudiante  
- Docente  
- Director  

---

## 2.4 Restricciones

- Uso de Oracle  
- Control por roles  

---

## 2.5 Suposiciones

- Infraestructura disponible  

---

# 3. Objetivos

## Objetivo general
Implementar un sistema funcional de prácticas académicas.

## Objetivos específicos
- Definir requisitos  
- Diseñar sistema  
- Construir prototipo  
- Realizar pruebas  

---

# 4. Justificación

Centralizar mejora trazabilidad, reduce errores y facilita el control.

---

# 5. Plan del proyecto

| Fase | Actividades | Entregables |
|------|------------|------------|
| Análisis | Requisitos | Documento |
| Diseño | UML | Diagramas |
| Construcción | Desarrollo | Prototipo |
| Pruebas | Validación | Evidencias |

---

# 6. Requerimientos

## Funcionales
* RF01 Autenticar usuario por correo, contraseña y rol.
* RF02 Registrar estudiante mediante autoregistro público.
* RF03 Registrar y mantener docentes asesores por la directora/coordinadora.
* RF04 Gestionar entidades receptoras.
* RF05 Registrar práctica académica.
* RF06 Asignar docente asesor y entidad receptora a la práctica.
* RF07 Registrar entradas de bitácora con fecha, actividad, descripción y horas.
* RF08 Cargar evidencias asociadas a una entrada de bitácora.
* RF09 Validar o rechazar entradas de bitácora con observación obligatoria en rechazo.
* RF10 Consultar estado, historial y avance de horas de la práctica.
* RF11 Cerrar o reabrir práctica según cumplimiento de horas y estado.
* RF12 Generar reportes por período, programa y estado.
  
## No funcionales
* RNF01 Usabilidad: el usuario debe completar las operaciones frecuentes del rol en máximo 3 clics desde el dashboard principal.
* RNF02 Seguridad: el sistema debe impedir acceso a pantallas y operaciones no autorizadas según rol autenticado.
* RNF03 Rendimiento: consultas frecuentes de dashboard y reportes deben responder en menos de 3 segundos en entorno de laboratorio.
* RNF04 Integridad: Oracle debe aplicar PK, FK, CHECK y UNIQUE en tablas críticas.
* RNF05 Trazabilidad: cada validación o rechazo debe almacenar fecha, usuario responsable, observación y estado resultante.
* RNF06 Mantenibilidad: el prototipo debe conservar estructura modular por capas.
* RNF07 Disponibilidad: el sistema debe poder ejecutarse sin fallos críticos durante una sesión continua de pruebas académicas.

---

# 7. Diseño UML

Incluye:
- Casos de uso

  <img width="826" height="962" alt="image" src="https://github.com/user-attachments/assets/a9832b6f-9a31-4bef-a249-69474ddc9e8c" />

- Diagramas de dominio

<img width="770" height="1095" alt="image" src="https://github.com/user-attachments/assets/1a9bade6-31ab-40e0-abd2-bab6d345aad9" />

  
- Diagramas de secuencia  

---

# 8. Base de datos

## Modelo ER

erDiagram
USUARIO ||--o{ ESTUDIANTE : es
USUARIO ||--o{ DOCENTE : es
ESTUDIANTE ||--o{ PRACTICA : tiene
PRACTICA ||--o{ ACTIVIDAD : registra
ACTIVIDAD ||--o{ EVIDENCIA : soporta

### Diccionario de datos

### 📌 Tabla: USUARIO

|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-001                                                                |
| Nombre Tabla       | USUARIO                                                               |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo        | Tipo de dato | Tamaño | Descripción                             | Restricción |
|--------------|-------------|--------|-----------------------------------------|------------|
| id_usuario   | NUMBER      | —      | Identificador único del usuario         | PK         |
| nombre       | VARCHAR2    | 50     | Nombre del usuario                      | NOT NULL   |
| apellido     | VARCHAR2    | 50     | Apellido del usuario                    | NOT NULL   |
| correo       | VARCHAR2    | 100    | Correo electrónico                      | —          |
| contraseña   | VARCHAR2    | 100    | Contraseña del usuario                  | NOT NULL   |
| id_rol       | NUMBER      | —      | Rol asignado al usuario                 | FK         |

#### Rol
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-002                                                                |
| Nombre Tabla       | ROL                                                                   |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo      | Tipo de dato | Tamaño | Descripción                            | Restricción |
| ---------- | ------------ | ------ | -------------------------------------- | ----------- |
| id_rol     | NUMBER       | —      | Identificador del rol                  | PK          |
| nombre_rol | VARCHAR2     | 50     | Nombre del rol                         | NOT NULL    |

### Estudiante
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-003                                                                |
| Nombre Tabla       | ESTUDIANTE                                                            |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo         | Tipo de dato | Tamaño | Descripción                  | Restricción |
| ------------- | ------------ | ------ | ---------------------------- | ----------- |
| id_estudiante | NUMBER       | —      | Identificador del estudiante | PK          |
| id_usuario    | NUMBER       | —      | Relación con usuario         | FK, UNIQUE  |
| programa      | VARCHAR2     | 100    | Programa académico           | —           |
| semestre      | NUMBER       | —      | Semestre actual              | —           |

### Docente
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-004                                                                |
| Nombre Tabla       | DOCENTE                                                               |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo        | Tipo de dato | Tamaño | Descripción                           | Restricción |
| ------------ | ------------ | ------ | ------------------------------------- | ----------- |
| id_docente   | NUMBER       | —      | Identificador del docente             | PK          |
| id_usuario   | NUMBER       | —      | Relación con usuario                  | FK, UNIQUE  |
| especialidad | VARCHAR2     | 100    | Área de especialización               | —           |


### Entidad_Receptora
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-005                                                                |
| Nombre Tabla       | ENTIDAD_RECEPTORA                                                     |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo             | Tipo de dato | Tamaño | Descripción                 | Restricción |
| ----------------- | ------------ | ------ | --------------------------- | ----------- |
| id_entidad        | NUMBER       | —      | Identificador de la entidad | PK          |
| nombre            | VARCHAR2     | 100    | Nombre de la entidad        | NOT NULL    |
| direccion         | VARCHAR2     | 150    | Dirección                   | —           |
| telefono          | VARCHAR2     | 20     | Teléfono de contacto        | —           |
| cupos_disponibles | VARCHAR2     | 20     | Número de cupos disponibles | —           |


###  Practica
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-006                                                                |
| Nombre Tabla       | PRACTICA                                                              |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo         | Tipo de dato | Tamaño | Descripción                  | Restricción |
| ------------- | ------------ | ------ | ---------------------------- | ----------- |
| id_practica   | NUMBER       | —      | Identificador de la práctica | PK          |
| id_estudiante | NUMBER       | —      | Estudiante asignado          | FK          |
| id_docente    | NUMBER       | —      | Docente asesor               | FK          |
| id_entidad    | NUMBER       | —      | Entidad receptora            | FK          |
| fecha_inicio  | DATE         | —      | Fecha de inicio              | —           |
| fecha_fin     | DATE         | —      | Fecha de finalización        | —           |
| estado        | VARCHAR2     | 50     | Estado de la práctica        | —           |

### Actividad
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-007                                                                |
| Nombre Tabla       | ACTIVIDAD                                                             |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo        | Tipo de dato | Tamaño | Descripción                   | Restricción |
| ------------ | ------------ | ------ | ----------------------------- | ----------- |
| id_actividad | NUMBER       | —      | Identificador de la actividad | PK          |
| id_practica  | NUMBER       | —      | Relación con práctica         | FK          |
| descripcion  | VARCHAR2     | 200    | Descripción de la actividad   | —           |
| fecha        | DATE         | —      | Fecha de la actividad         | —           |
| horas        | NUMBER       | —      | Número de horas registradas   | —           |
| estado       | VARCHAR2     | 50     | Estado (aprobada, pendiente)  | —           |

### Evidencia 
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-008                                                                |
| Nombre Tabla       | EVIDENCIA                                                             |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo        | Tipo de dato | Tamaño | Descripción                   | Restricción |
| ------------ | ------------ | ------ | ----------------------------- | ----------- |
| id_evidencia | NUMBER       | —      | Identificador de la evidencia | PK          |
| id_actividad | NUMBER       | —      | Relación con actividad        | FK          |
| archivo      | VARCHAR2     | 200    | Ruta o nombre del archivo     | —           |
| fecha_subida | DATE         | —      | Fecha de carga                | —           |

### Evaluacion 
|                    |                                                                       |
|--------------------|-----------------------------------------------------------------------|
| Código Tabla       | TB-009                                                                |
| Nombre Tabla       | EVALUACION                                                            |
| Descripción Tabla  | Se registra información de los usuarios independientemente del rol   |

| Campo         | Tipo de dato | Tamaño | Descripción                 | Restricción |
| ------------- | ------------ | ------ | --------------------------- | ----------- |
| id_evaluacion | NUMBER       | —      | Identificador de evaluación | PK          |
| id_practica   | NUMBER       | —      | Relación con práctica       | FK, UNIQUE  |
| calificacion  | NUMBER       | 5,2    | Nota final                  | —           |
| observaciones | VARCHAR2     | 200    | Comentarios del docente     | —           |

---

## 9. Interfaz

Login, dashboard, bitacora.
# Vistas del Portable (PNG) y su proposito

Ruta de imagenes: `documentacion/vistas/png_portable/`

## Listado de vistas

1. `01_login.png`
- Vista de autenticacion del sistema.
- Permite ingresar por rol (estudiante, docente, directora) y elegir modo Oracle o Demo.
<img width="860" height="520" alt="image" src="https://github.com/user-attachments/assets/81e15420-0477-44c4-8275-12d329ed3a35" />



2. `02_autoregistro_estudiante.png`
- Formulario de autoregistro para estudiantes.
- Permite crear cuenta institucional de estudiante para luego iniciar sesion.
<img width="760" height="560" alt="image" src="https://github.com/user-attachments/assets/709a393d-8ae5-4c1b-aa2e-415374f1c1df" />


3. `03_dashboard_estudiante.png`
- Panel principal del estudiante.
- Resume horas, estado de practica y tareas proximas.
<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/7b240397-46a0-410e-b71d-ad5f5b4759d4" />


4. `04_bitacora_estudiante.png`
- Bitacora en modo estudiante.
- Permite registrar actividades, horas y descripcion diaria.
<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/05d65836-4e79-43f7-ba70-8193bf4dff62" />


5. `05_evidencias_estudiante.png`
- Carga de evidencias del estudiante.
- Permite asociar archivos/enlaces a actividades de bitacora.
<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/204421da-747a-4d2b-ae08-2f0325925916" />


6. `06_dashboard_docente.png`
- Panel principal del docente asesor.
- Resume pendientes de validacion y acceso rapido a evaluacion.
<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/20009809-727a-42ba-850d-a15091ff2acd" />


7. `07_validacion_docente.png`
- Vista de validacion docente.
- Permite aprobar/rechazar actividades y horas registradas por estudiantes.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/403313fc-eb7a-43fb-bf92-16cd1548f1e1" />


8. `08_evaluacion_rubrica_docente.png`
- Evaluacion por rubrica del docente.
- Permite calificar practicas por criterios y dejar observaciones.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/e8944a6e-ef5e-45b6-b580-73838c053021" />


9. `09_bitacora_docente.png`
- Bitacora en modo docente.
- Permite consultar historial y pasar al flujo de validacion.
  
<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/2c381d00-46d9-4058-bc20-c65db326e7d4" />

10. `10_dashboard_directora.png`
- Panel principal de direccion del programa.
- Muestra control global de practicas y accesos a modulos de gestion.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/60b26115-579b-4f16-864e-0f852523d1f9" />


11. `11_asignaciones_practica_directora.png`
- Registro/asignacion de practica (directora).
- Permite crear practica y asignar docente/entidad/periodo.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/a762c84c-cc07-4179-9ebd-5fb5c76f9ab2" />


12. `12_aprobacion_cierre_directora.png`
- Aprobacion final de cierre.
- Permite revisar consolidado y decidir cierre o devolucion.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/ef0d25c6-4776-4155-9cd8-b143c7711ce1" />


13. `13_registro_docentes_directora.png`
- Administracion de docentes asesores.
- Permite registrar y mantener docentes disponibles para asignacion.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/9dbbc279-468a-453e-b4de-5c8d9524eca2" />


14. `14_plantillas_bitacora_directora.png`
- Configuracion de plantillas de bitacora.
- Permite definir preguntas y estructura por periodo/modalidad.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/07c7256c-e5d4-452c-b682-7b577b781d4b" />


15. `15_reportes_directora.png`
- Reportes institucionales.
- Permite filtrar y consultar consolidado de practicas por estado/programa/periodo.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/65837746-cfa9-4b01-ad41-57e72e1209f4" />


16. `16_hallazgos_directora.png`
- Consolidacion de hallazgos.
- Permite registrar acciones de mejora sobre vacios, tensiones y fortalezas.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/5bbdfe04-1715-4804-9246-61f107852a90" />


17. `17_bitacora_directora.png`
- Bitacora en modo directora.
- Permite seguimiento general de registros con resumen de estados.

<img width="1080" height="700" alt="image" src="https://github.com/user-attachments/assets/46647a74-6fea-4fa5-81dd-c2e13410dcc1" />

---

## 10. Referencias

Sommerville, Pressman.

---

## 11. Anexos

Diagramas, SQL, pruebas.
