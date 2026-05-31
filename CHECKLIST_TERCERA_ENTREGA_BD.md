# Checklist de Cumplimiento - Tercera Entrega BD II

## Estado general
- Cumplimiento estimado: **Completo (1.1 a 2.6.2)** con los archivos listados abajo.

## 1. Correccion

### 1.1 Modelo Entidad-Relacion
- Evidencia: `anexos/ANEXO_E1_DIAGRAMA_ENTIDAD_RELACION_FINAL.drawio`
- Evidencia visual: `anexos/10_modelo_entidad_relacion.png`

### 1.2 Modelo Relacional
- Evidencia: `anexos/ANEXO_E2_MODELO_RELACIONAL_FINAL.drawio`
- Evidencia visual: `anexos/11_entidades_pk_fk.png`

### 1.3 Diccionario de Datos
- Evidencia: `anexos/ANEXO_F_DICCIONARIO_DATOS_POR_TABLA_FINAL.xlsx`

## 2. Nuevo

### 2.1 Sentencia de creacion de usuario con perfiles
- Evidencia principal: `ENTREGA_3_BD/TERCERA_ENTREGA_SQL_COMPLETA.sql`
- Secciones: `CREATE PROFILE`, `CREATE USER`

### 2.2 Sentencias de creacion de tablas
- Evidencia principal: `ENTREGA_2_COMPLETA/03_BASE_DATOS/bd/01_crear_esquema_oracle.sql`
- Ajustes y tablas nuevas: `ENTREGA_2_COMPLETA/03_BASE_DATOS/bd/04_ajustes_reunion_20260310.sql`

### 2.3 Usuarios y Roles
#### 2.3.1 Roles
- Evidencia principal: `ENTREGA_3_BD/TERCERA_ENTREGA_SQL_COMPLETA.sql`
- Seccion: `CREATE ROLE`

#### 2.3.2 Privilegios por Roles
- Evidencia principal: `ENTREGA_3_BD/TERCERA_ENTREGA_SQL_COMPLETA.sql`
- Secciones: `GRANT ... TO ROL_SIGPRA_*`

### 2.4 Procedimientos almacenados
#### 2.4.1 Tabla con Procedimientos
- Evidencia principal: `ENTREGA_3_BD/TablaBaseDatosTerceraEntrega_diligenciada.xlsx`
- Hoja: `PROCEDIMIENTOS ALMACENADOS`

#### 2.4.2 Sentencias Procedimientos
- Evidencia principal: `ENTREGA_3_BD/TERCERA_ENTREGA_SQL_COMPLETA.sql`
- Procedimientos incluidos:
  - `SP_REGISTRAR_ACTIVIDAD`
  - `SP_VALIDAR_ACTIVIDAD`
  - `SP_REGISTRAR_JORNADA`
  - `SP_CERRAR_PRACTICA`
  - `SP_CREAR_REPORTE_PERIODO`

### 2.5 Disparadores
#### 2.5.1 Tabla con Disparadores
- Evidencia principal: `ENTREGA_3_BD/TablaBaseDatosTerceraEntrega_diligenciada.xlsx`
- Hoja: `DISPARADORES`

#### 2.5.2 Sentencias Disparadores
- Evidencia principal: `ENTREGA_3_BD/TERCERA_ENTREGA_SQL_COMPLETA.sql`
- Disparadores incluidos:
  - `TRG_ACTUALIZAR_CUPOS_ENTIDAD_AI`
  - `TRG_RESTAURAR_CUPOS_ENTIDAD_AD`
- Disparadores base existentes:
  - `ENTREGA_2_COMPLETA/03_BASE_DATOS/bd/01_crear_esquema_oracle.sql`
  - `ENTREGA_2_COMPLETA/03_BASE_DATOS/bd/04_ajustes_reunion_20260310.sql`

### 2.6 Reportes
#### 2.6.1 Tabla con reportes
- Evidencia principal: `ENTREGA_3_BD/TablaBaseDatosTerceraEntrega_diligenciada.xlsx`
- Hoja: `CONSULTAS`

#### 2.6.2 Sentencias Reportes
- Evidencia principal: `ENTREGA_2_COMPLETA/03_BASE_DATOS/bd/03_consultas_utiles.sql`
- Complemento: `PROYECTO_VISTAS_NETBEANS/docs/BD_03_CONSULTAS.sql`

## Archivo plantilla diligenciado
- Salida final: `ENTREGA_3_BD/TablaBaseDatosTerceraEntrega_diligenciada.xlsx`
- Plantilla origen: `C:/Users/Fabian/Downloads/TablaBaseDatosTerceraEntrega.xlsx`
