# PROYECTO_VISTAS_NETBEANS_ANT

Proyecto Java con estructura NetBeans + Apache Ant listo para abrir.

## Abrir en NetBeans

1. File > Open Project.
2. Seleccionar carpeta `PROYECTO_VISTAS_NETBEANS_ANT`.
3. Ejecutar proyecto.

## Clase principal

`co.udi.integrador.ui.launchers.AbrirMenuVistas`

## Contenido

- Codigo fuente completo en `src/co/udi/integrador/...`
- Recursos en `src/branding` y `src/db.properties`
- Configuracion Ant en `build.xml` y `nbproject/`

## Senalizacion CRUD desde Base de Datos (Oracle)

Para cumplir el requisito de "Montaje y pruebas de requisitos de la Base de Datos (CRUD)", se incluye el script:

- `CRUD_BD_USUARIO.sql`
- `REPORTE_EVIDENCIA_CRUD_BD.md` (plantilla para anexar capturas de ejecucion)
- `ANEXO_CRUD_BD_ENTREGABLE.md` (version formal lista para el documento final)
- `ANEXO_CRUD_BD_RESUMIDO_1PAGINA.md` (version corta para entrega sintetica)

Tabla usada para evidencia basica:

- `usuario`

Operaciones cubiertas:

- `CREATE`: insercion de usuario de prueba.
- `READ`: consulta del usuario insertado.
- `UPDATE`: actualizacion de nombre del usuario.
- `DELETE`: eliminacion del usuario de prueba.

Ejecucion sugerida:

1. Ejecutar primero el esquema en `sigpra/gestion-practicas-desktop/sql/01_schema_oracle.sql`.
2. Ejecutar `CRUD_BD_USUARIO.sql` en Oracle SQL Developer.
3. Guardar capturas de cada bloque (CREATE, READ, UPDATE, DELETE) como evidencia para el entregable.
