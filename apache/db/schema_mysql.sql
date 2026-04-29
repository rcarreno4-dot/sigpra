CREATE DATABASE IF NOT EXISTS app_horas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE app_horas;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS evaluaciones;
DROP TABLE IF EXISTS actividades;
DROP TABLE IF EXISTS practicas;
DROP TABLE IF EXISTS entidades;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  correo VARCHAR(120) NOT NULL UNIQUE,
  clave_hash VARCHAR(255) NOT NULL,
  rol_id INT NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE entidades (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  ciudad VARCHAR(80) NOT NULL,
  cupos INT NOT NULL DEFAULT 0,
  contacto VARCHAR(50) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE practicas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  estudiante_id INT NOT NULL,
  docente_id INT NOT NULL,
  entidad_id INT NOT NULL,
  meta_horas INT NOT NULL DEFAULT 120,
  estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
  fecha_inicio DATE NULL,
  fecha_fin DATE NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_practica_estudiante FOREIGN KEY (estudiante_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE,
  CONSTRAINT fk_practica_docente FOREIGN KEY (docente_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE,
  CONSTRAINT fk_practica_entidad FOREIGN KEY (entidad_id) REFERENCES entidades(id)
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE actividades (
  id INT AUTO_INCREMENT PRIMARY KEY,
  practica_id INT NOT NULL,
  titulo VARCHAR(180) NOT NULL,
  fecha DATE NULL,
  horas INT NOT NULL DEFAULT 1,
  estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
  evidencia VARCHAR(255) NULL,
  observaciones VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_actividad_practica FOREIGN KEY (practica_id) REFERENCES practicas(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE evaluaciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  practica_id INT NOT NULL,
  rubrica VARCHAR(120) NOT NULL,
  nota DECIMAL(4,2) NOT NULL,
  feedback VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_evaluacion_practica FOREIGN KEY (practica_id) REFERENCES practicas(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

INSERT INTO roles (nombre) VALUES
('Administrador'),
('Docente'),
('Estudiante');

-- Clave semilla: 123456 (se migra a hash seguro en el primer login)
INSERT INTO usuarios (nombre, correo, clave_hash, rol_id, estado) VALUES
('Admin Principal', 'admin@uni.edu', '123456', 1, 'Activo'),
('Luis Perez', 'docente@uni.edu', '123456', 2, 'Activo'),
('Daniel Rojas', 'docente2@uni.edu', '123456', 2, 'Activo'),
('Maria Gomez', 'estudiante@uni.edu', '123456', 3, 'Activo'),
('Johan Diaz', 'estudiante2@uni.edu', '123456', 3, 'Activo');

INSERT INTO entidades (nombre, ciudad, cupos, contacto) VALUES
('Colegio San Miguel', 'Bogota', 12, '3021112233'),
('I.E. Nueva Esperanza', 'Medellin', 8, '3102223344'),
('Colegio Prado Verde', 'Cali', 5, '3153334455');

INSERT INTO practicas (estudiante_id, docente_id, entidad_id, meta_horas, estado, fecha_inicio, fecha_fin) VALUES
(4, 2, 1, 120, 'En progreso', '2026-03-01', '2026-06-01'),
(5, 2, 2, 120, 'Aprobada', '2026-02-01', '2026-05-15'),
(4, 3, 3, 120, 'Pendiente', '2026-04-01', '2026-07-01');

INSERT INTO actividades (practica_id, titulo, fecha, horas, estado, observaciones) VALUES
(1, 'Planeacion de clase de algebra', '2026-04-01', 4, 'Validada', 'Buen desarrollo metodologico'),
(1, 'Observacion pedagogica aula 7B', '2026-04-03', 3, 'Pendiente', 'Pendiente evidencia fotografica'),
(2, 'Taller de comprension lectora', '2026-04-02', 5, 'Validada', 'Actividad completada');

INSERT INTO evaluaciones (practica_id, rubrica, nota, feedback) VALUES
(1, 'Dominio disciplinar', 4.20, 'Buen manejo de grupo, mejorar cierre de clase.'),
(2, 'Planeacion didactica', 4.80, 'Planeacion clara y evidencias completas.');
