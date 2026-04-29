# SIGPRA - Prototipo Swing (Proyecto Integrador)

Repositorio del prototipo de gestion de practicas academicas (desktop Java Swing + Oracle).

## URL de entrega (GitHub Publico)
Pega aqui la URL final de tu repo cuando lo publiques:

`https://github.com/<tu-usuario>/PROYECTO_VISTAS_NETBEANS`

## Contenido principal
- `src/main/java`: codigo fuente Java (UI, DAO, modelos, sesion).
- `src/main/resources`: recursos (`db.properties`, branding/logos).
- `docs/`: documentacion de entrega en formato simple (sin subcarpetas extra).
  - `docs/CODIGO_DOCUMENTADO.md`
  - `docs/BD_01_TABLAS.sql`
  - `docs/BD_02_DATOS_INICIALES.sql`
  - `docs/BD_SOLO_TABLAS_LIMPIO.txt`
  - `docs/BD_SOLO_USUARIOS_LIMPIO.txt`
  - `docs/WIREFRAME_VISTAS.md`

## Requisitos
- JDK 17+ (probado con JDK 23).
- Oracle XE (servicio `XE`).
- Esquema/app user: `PRACTICAS_APP`.

## Configuracion de BD (app)
Archivo: `src/main/resources/db.properties`

Valores por defecto:
- `db.url=jdbc:oracle:thin:@localhost:1521/XE`
- `db.user=PRACTICAS_APP`
- `db.password=Practicas2026`

Prioridad de configuracion en runtime:
1. Variables de entorno: `DB_URL`, `DB_USER`, `DB_PASSWORD`
2. Propiedades JVM: `-Ddb.url`, `-Ddb.user`, `-Ddb.password`
3. `db.properties` externo (misma carpeta del `.exe/.jar` o en `app/`)
4. `db.properties` embebido en `resources`
5. Valores por defecto del codigo

### Nota sobre driver JDBC Oracle (portable)
- Si ejecutas el `.exe`/`.jar` portable y el driver no viene embebido, la app intenta cargar `ojdbc*.jar` desde:
  - carpeta actual del ejecutable,
  - subcarpeta `app/`,
  - repositorio local Maven (`~/.m2/repository`).
- Si aun asi falla, coloca manualmente `ojdbc11-21.9.0.0.jar` junto al `.exe` o dentro de `app/`.

## Instalacion BD (SQL*Plus)
Desde carpeta `docs/`:

```sql
@BD_00_INSTALAR.sql
```

Ese script ejecuta:
- `BD_01_ESQUEMA_COMPLETO.sql`
- `BD_02_DATOS_INICIALES.sql`
- `BD_04_AJUSTES_20260310.sql`
- `BD_05_ROLES_20260310.sql`

## Usuarios semilla cargados
En `docs/BD_02_DATOS_INICIALES.sql` se insertan usuarios con `hash_password = 'HASH_TEMP_123'`:
- `carlos.ramirez@universidad.edu.co` (DIRECTOR)
- `laura.perez@universidad.edu.co` (TUTOR_ACADEMICO)
- `andres.gomez@universidad.edu.co` (ESTUDIANTE)
- `sofia.martinez@universidad.edu.co` (ESTUDIANTE)
- `diana.lopez@universidad.edu.co` (TUTOR_ACADEMICO)
- `paula.suarez@universidad.edu.co` (DIRECTOR)
- `marta.ruiz@sanjose.edu.co` (ASESOR_PEDAGOGICO)
- `luis.parra@horizonte.edu.co` (ASESOR_PEDAGOGICO)

Nota: el login Swing usa roles `ESTUDIANTE`, `DOCENTE`, `DIRECTOR`.

## Ejecucion local
Clase launcher:
- `co.udi.integrador.ui.launchers.AbrirMenuVistas`

Desde NetBeans:
1. Open Project.
2. Seleccionar `PROYECTO_VISTAS_NETBEANS`.
3. Run File sobre `AbrirMenuVistas`.

## Cambios visuales recientes
- Regla de logos:
  - Inicio/Login usa `logo2`.
  - Resto de vistas usa `logo`.
- Mejora de nitidez en render/escalado de logos.
