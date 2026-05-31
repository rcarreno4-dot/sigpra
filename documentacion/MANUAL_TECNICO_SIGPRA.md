# Manual Tecnico SIGPRA

## 1. Alcance

Este manual tecnico describe la estructura interna del sistema SIGPRA desktop, el flujo de ejecucion, la capa de datos y los artefactos necesarios para compilar, ejecutar y mantener el proyecto.

Incluye evidencias tecnicas reales del codigo fuente y de las vistas operativas.

## 2. Arquitectura general

La solucion usa arquitectura **Modelo Vista Controlador (MVC)** en aplicacion de escritorio:

- **Modelo**: entidades de dominio, estado de sesion y capa de persistencia JDBC (`co.udi.integrador.model`, `co.udi.integrador.session`, `co.udi.integrador.data`).
- **Vista**: interfaces Swing por rol y modulo (`co.udi.integrador.ui`).
- **Controlador**: logica de flujo en frames y servicios DAO que coordinan validaciones, navegacion y operaciones de BD.

### 2.2 Componentes de arquitectura MVC

- Cliente de escritorio Swing con navegacion por rol.
- Capa de sesion (`AppSession`) para controlar usuario autenticado y modo (`Oracle` / `Demo`).
- Capa DAO para encapsular SQL por modulo (`PracticeDao`, `BitacoraDao`, `ReportsDao`, etc.).
- Motor Oracle como persistencia transaccional.
- Recursos de branding para UI y reportes PDF.

### 2.3 Flujo tecnico de ejecucion

1. Login valida credenciales y rol.
2. Se crea sesion activa con contexto de modo de ejecucion.
3. Cada vista invoca su DAO correspondiente.
4. DAO ejecuta SQL en Oracle y devuelve datos a UI.
5. UI renderiza tablas, estados y mensajes de validacion.

### 2.1 Paquetes principales

- `src/co/udi/integrador/ui`: frames por modulo y navegacion por rol.
- `src/co/udi/integrador/data`: consultas SQL, CRUD y logica de acceso a datos.
- `src/co/udi/integrador/config`: configuracion base.
- `src/co/udi/integrador/session`: estado de sesion y modo de ejecucion.
- `src/branding`: recursos de imagen usados en interfaz y reportes PDF.

## 3. Requisitos tecnicos

- Java JDK instalado (compilacion con `javac`).
- Driver Oracle JDBC (`ojdbc11-21.9.0.0.jar`).
- Esquema Oracle creado con scripts SQL del proyecto.
- Windows (rutas y portable orientado a entorno Windows).

## 4. Estructura de ejecucion en este repositorio

Rutas activas de trabajo:

- Codigo fuente principal:
  - `C:/Users/Fabian/OneDrive/Desktop/Sigpra/codigo/PROYECTO_VISTAS_NETBEANS_ANT/src`
- Documentacion:
  - `C:/Users/Fabian/OneDrive/Desktop/Sigpra/documentacion`
- Ejecutable portable:
  - `C:/Users/Fabian/OneDrive/Desktop/Sigpra/portable_java/PROYECTO_VISTAS_NETBEANS.jar`

### 4.1 Estructura del proyecto (referencia tecnica)

```text
Sigpra/
├── codigo/
│   └── PROYECTO_VISTAS_NETBEANS_ANT/
│       ├── src/
│       │   ├── branding/
│       │   │   ├── sigpra-logo.png
│       │   │   └── udi-logo.jpeg
│       │   └── co/udi/integrador/
│       │       ├── config/
│       │       ├── data/
│       │       │   ├── AuthDao.java
│       │       │   ├── PracticeDao.java
│       │       │   ├── BitacoraDao.java
│       │       │   ├── ReportsDao.java
│       │       │   └── DbSchemaHelper.java
│       │       ├── model/
│       │       ├── session/
│       │       └── ui/
│       │           ├── LoginFrame.java
│       │           ├── PracticeRegistrationFrame.java
│       │           ├── BitacoraFrame.java
│       │           └── ReportsFrame.java
│       ├── LIMPIEZA_TABLAS_SOBRANTES.sql
│       └── RENOMBRAR_TABLA_DIRECTOR.sql
├── documentacion/
│   ├── MANUAL_USUARIO_SIGPRA.md
│   ├── MANUAL_TECNICO_SIGPRA.md
│   ├── MANUAL_TECNICO_SIGPRA.pdf
│   ├── vistas/png_portable/
│   ├── imagenes_codigo_real/
│   └── modelo_datos/
├── gestion-practicas-desktop/
│   └── sql/
│       ├── 01_schema_oracle.sql
│       ├── 01_schema_oracle_reducido_horas.sql
│       └── DOCUMENTACION_BD.txt
└── portable_java/
    └── PROYECTO_VISTAS_NETBEANS.jar
```

## 5. Build, despliegue y sincronizacion

### 5.1 Compilacion local

Compilar fuentes Java a `build/classes`:

```powershell
javac -encoding UTF-8 -d build/classes @build/sources_codex.txt
```

### 5.2 Recursos de branding

Despues de compilar, copiar branding al classpath:

```powershell
Copy-Item src/branding/* build/classes/branding -Recurse -Force
```

### 5.3 Actualizacion del portable

Actualizar el JAR en `portable_java` con las clases compiladas para que el ejecutable use la version vigente.

## 6. Base de datos (BD)

Archivo de configuracion:

- `db.properties` (portable y app).

Puntos clave:

- URL Oracle (`db.url`)
- Usuario y clave (`db.user`, `db.password`)
- Servicio (`XE`, `XEPDB1`, etc.)

Si Oracle falla, el sistema puede operar en modo demo para navegacion funcional.

### 6.1 Tablas principales de la version actual

- `usuario`
- `estudiante`
- `docente_asesor`
- `director`
- `entidad_receptora`
- `practica`
- `bitacora`
- `evidencia`

### 6.2 Relaciones clave

- `estudiante.id_usuario -> usuario.id_usuario`
- `docente_asesor.id_usuario -> usuario.id_usuario`
- `director.id_usuario -> usuario.id_usuario`
- `practica.id_estudiante -> estudiante.id_estudiante`
- `practica.id_docente -> docente_asesor.id_docente`
- `practica.id_entidad -> entidad_receptora.id_entidad`
- `bitacora.id_practica -> practica.id_practica`
- `evidencia.id_bitacora -> bitacora.id_bitacora`

### 6.3 Scripts BD de referencia

- `gestion-practicas-desktop/sql/01_schema_oracle.sql`
- `gestion-practicas-desktop/sql/01_schema_oracle_reducido_horas.sql`
- `codigo/PROYECTO_VISTAS_NETBEANS_ANT/LIMPIEZA_TABLAS_SOBRANTES.sql`
- `codigo/PROYECTO_VISTAS_NETBEANS_ANT/RENOMBRAR_TABLA_DIRECTOR.sql`

## 7. Flujo tecnico por modulos

### 7.1 Registro de practica (Directora)

- UI: `PracticeRegistrationFrame`
- DAO: `PracticeDao`
- Objetivo: asignar estudiante, docente asesor, entidad y periodo.

### 7.2 Bitacora de estudiante

- UI: `BitacoraFrame`
- DAO: `BitacoraDao`
- Objetivo: crear, actualizar y eliminar entradas en estado pendiente.

### 7.3 Reportes

- UI: `ReportsFrame`
- DAO: `ReportsDao`
- PDF: `SimplePdfWriter`
- Objetivo: consolidado por filtros y exportacion PDF con logo.

## 8. Metodos principales (capa de datos)

### 8.1 Autenticacion y sesion

- `AuthDao.authenticate(correo, password, role)`
- `RoleIdentityDao.requireStudentIdByUserId(userId)`
- `RoleIdentityDao.requireTeacherIdByUserId(userId)`
- `RoleIdentityDao.requireDirectorIdByUserId(userId)`

### 8.2 Registro y asignaciones

- `PracticeDao.listStudents()`
- `PracticeDao.listTeachers()`
- `PracticeDao.listDirectors()`
- `PracticeDao.listEntities()`
- `PracticeDao.createPractice(...)`

### 8.3 Bitacora y evidencias

- `BitacoraDao.findByUser(user)`
- `BitacoraDao.createStudentEntry(...)`
- `BitacoraDao.updateStudentEntry(...)`
- `BitacoraDao.deleteStudentEntry(...)`
- `EvidenceDao.listStudentActivities(studentUserId)`
- `EvidenceDao.insertEvidence(...)`

## 9. Cambios tecnicos aplicados

### 9.1 Compatibilidad de tabla director

Se normalizo el esquema para usar una unica tabla:

- `director`

### 9.2 Registro de estudiantes nuevos

- Mejora en listado activo de estudiantes.
- Boton de actualizacion de combos en pantalla de asignacion.

### 9.3 Reportes ajustados al requerimiento

- Se removio exportacion Excel.
- Se paso a detalle por estudiante.
- Campos: nombre, codigo, carrera, periodo, horas, meta y estado.
- PDF con prioridad de logo UDI (`udi-logo.png` / `udi-logo.jpeg`).

## 10. Evidencia real de codigo

Capturas generadas desde archivos reales del proyecto (ruta + lineas):

### 10.1 Guardado de practica

![Codigo savePractice](imagenes_codigo_real/01_practice_registration_save.png)

### 10.2 Carga de logo UDI para PDF

![Codigo loadLogoBytes](imagenes_codigo_real/02_reports_logo_loading.png)

### 10.3 Creacion de entrada de bitacora

![Codigo createStudentEntry](imagenes_codigo_real/03_bitacora_create_entry.png)

### 10.4 Resolucion de esquema director

![Codigo resolveDirectorTable](imagenes_codigo_real/04_schema_helper_director_table.png)

## 11. Evidencia de vistas funcionales

![Vista login](vistas/png_portable/01_login.png)
![Vista bitacora estudiante](vistas/png_portable/04_bitacora_estudiante.png)
![Vista asignaciones directoria](vistas/png_portable/10_asignaciones_practica_directora.png)
![Vista reportes directoria](vistas/png_portable/13_reportes_directora.png)

## 12. Modelo de datos y documentacion SQL

Evidencias del modelo:

![Modelo entidad relacion](modelo_datos/ANEXO_E1_DIAGRAMA_ENTIDAD_RELACION_DOCX.jpg)
![Modelo relacional](modelo_datos/ANEXO_E2_MODELO_RELACIONAL_DOCX.jpg)

Scripts y soporte SQL:

- `gestion-practicas-desktop/sql/01_schema_oracle.sql`
- `gestion-practicas-desktop/sql/01_schema_oracle_reducido_horas.sql`
- `gestion-practicas-desktop/sql/DOCUMENTACION_BD.txt`

## 13. Tablas consideradas en esta version

Tablas base:

- usuario
- estudiante
- docente_asesor
- director
- entidad_receptora
- practica
- bitacora
- evidencia

Vistas activas:

- `VW_HORAS_PRACTICA`
- `VW_REPORTE_PROGRAMA`

## 14. Diagnostico tecnico rapido

### 14.1 Reporte PDF sin logo correcto

Validar archivos:

- `src/branding/udi-logo.png`
- `src/branding/udi-logo.jpeg`

Si no existen, el sistema usa fallback SIGPRA.

### 14.2 Cambios de codigo no visibles en ejecucion

Revisar que la ejecucion provenga del JAR actualizado en:

- `portable_java/PROYECTO_VISTAS_NETBEANS.jar`

### 14.3 Error de practica activa en bitacora

Antes de registrar bitacora, verificar que el estudiante tenga practica asignada en estado `EN_CURSO` o `PENDIENTE`.
