# CHECKLIST CRUD SIGPRA

Fecha de verificacion: 2026-05-15
Proyecto: `codigo/PROYECTO_VISTAS_NETBEANS_ANT`

## 1) Practicas (rol Directora)

- Create: SI
  - Boton: `Guardar registro`
  - DAO: `PracticeDao.createPractice(...)`
- Read/List: SI
  - Tabla: `Listado de practicas (CRUD)`
  - DAO: `PracticeDao.listPracticesForGrid()`
- Update: SI
  - Flujo: seleccionar fila -> `Cargar seleccionado` -> editar -> `Actualizar`
  - DAO: `PracticeDao.updatePractice(...)`
- Delete: SI (con restriccion de integridad)
  - Boton: `Eliminar`
  - DAO: `PracticeDao.deletePractice(...)`
  - Nota: solo elimina si la practica no tiene bitacoras asociadas.

## 2) Bitacora (rol Estudiante)

- Create: SI
  - Boton: `Guardar`
  - DAO: `BitacoraDao.createStudentEntry(...)`
- Read/List: SI
  - Tabla: `Historial de bitacora`
  - DAO: `BitacoraDao.findByUser(...)`
- Update: SI
  - Flujo: seleccionar fila pendiente -> editar -> `Actualizar`
  - DAO: `BitacoraDao.updateStudentEntry(...)`
  - Nota: solo permite estado `PENDIENTE`.
- Delete: SI
  - Boton: `Eliminar`
  - DAO: `BitacoraDao.deleteStudentEntry(...)`
  - Nota: solo permite estado `PENDIENTE`.

## 3) Regla funcional aplicada

- El registro de practica ya no lo realiza el estudiante.
- Solo la directora puede crear/editar/eliminar practicas.
- Las fechas en registro y bitacora usan selector dinamico, no escritura manual.
