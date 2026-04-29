# Base de Datos - Oracle SQL*Plus

## Archivos
- `00_instalar_bd.sql`: ejecuta esquema + datos iniciales.
- `01_crear_esquema_oracle.sql`: crea tablas, PK/FK, checks, secuencias, triggers e indices.
- `01_tablas_oracle.sql`: crea unicamente tablas con PK/FK/UQ/CHECK.
- `02_datos_iniciales.sql`: datos semilla.
- `03_consultas_utiles.sql`: consultas de validacion y reportes.
- `04_ajustes_reunion_20260310.sql`: ajustes incrementales por nuevos requerimientos funcionales.
- `05_ajuste_roles_video_20260310.sql`: migracion de roles al esquema del video (director, tutor, estudiante, asesor).

## Ajuste por nuevos actores
- Roles soportados en `usuario.rol`: `ESTUDIANTE`, `TUTOR_ACADEMICO`, `DIRECTOR`, `ASESOR_PEDAGOGICO`.
- Tabla `usuario_entidad`: vincula usuarios institucionales con entidad receptora.
- Tabla `validacion_institucional`: soporta el caso de uso UC-13.

## Ejecucion
Desde SQL*Plus, ubicado en la carpeta `bd`:

```sql
@00_instalar_bd.sql
```

Si solo necesitas crear tablas:

```sql
@01_tablas_oracle.sql
```

Luego para validar:

```sql
@03_consultas_utiles.sql
```

Si ya tenias esquema instalado y quieres incorporar los cambios de la reunion:

```sql
@04_ajustes_reunion_20260310.sql
@05_ajuste_roles_video_20260310.sql
@03_consultas_utiles.sql
```

## Nota
Las fechas se almacenan en tipo `DATE`. Para visualizacion en formato `DD/MM/AA` usa:

```sql
ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/RR';
```
