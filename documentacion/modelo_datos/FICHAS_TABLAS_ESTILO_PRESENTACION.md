# Fichas de Tablas SIGPRA (Estilo Presentacion)

> Archivo pensado para vista en Markdown Preview de VS Code y exportacion a PDF.

---

## Tabla: USUARIO

| Atributo | Valor |
|---|---|
| Codigo | TB-001 |
| Nombre | USUARIO |
| Descripcion | Almacena credenciales y datos base de identidad de todos los roles del sistema (estudiante, docente y director), incluyendo estado y fecha de creacion. |

## Tabla: ESTUDIANTE

| Atributo | Valor |
|---|---|
| Codigo | TB-002 |
| Nombre | ESTUDIANTE |
| Descripcion | Extension del usuario para rol estudiante; guarda codigo, programa y semestre, enlazado 1 a 1 con USUARIO. |

## Tabla: DOCENTE_ASESOR

| Atributo | Valor |
|---|---|
| Codigo | TB-003 |
| Nombre | DOCENTE_ASESOR |
| Descripcion | Extension del usuario para rol docente; registra especialidad del docente asesor y su enlace 1 a 1 con USUARIO. |

## Tabla: DIRECTOR_PROGRAMA

| Atributo | Valor |
|---|---|
| Codigo | TB-004 |
| Nombre | DIRECTOR_PROGRAMA |
| Descripcion | Extension del usuario para rol director; registra el programa academico bajo su direccion y su enlace 1 a 1 con USUARIO. |

## Tabla: ENTIDAD_RECEPTORA

| Atributo | Valor |
|---|---|
| Codigo | TB-005 |
| Nombre | ENTIDAD_RECEPTORA |
| Descripcion | Catalogo de entidades donde se realizan practicas; incluye datos de contacto, ubicacion, cupos y estado operativo. |

## Tabla: PRACTICA

| Atributo | Valor |
|---|---|
| Codigo | TB-006 |
| Nombre | PRACTICA |
| Descripcion | Registro principal de cada proceso de practica academica; relaciona estudiante, docente y entidad, con fechas, estado y control de horas. |

## Tabla: BITACORA

| Atributo | Valor |
|---|---|
| Codigo | TB-007 |
| Nombre | BITACORA |
| Descripcion | Registro de actividades y horas reportadas por practica; soporta flujo de validacion docente con estado, observacion y trazabilidad de fechas. |

## Tabla: EVIDENCIA

| Atributo | Valor |
|---|---|
| Codigo | TB-008 |
| Nombre | EVIDENCIA |
| Descripcion | Soportes asociados a cada entrada de bitacora (archivo/ruta/comentario), usados para sustentar actividades y validaciones. |
