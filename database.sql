CREATE SCHEMA IF NOT EXISTS sistemasCorrelativas DEFAULT CHARACTER SET utf8mb4;
USE sistemasCorrelativas;

-- 1. Tabla: Carrera (Planes y programas de estudio)
CREATE TABLE IF NOT EXISTS carrera (
    id_carrera INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL COMMENT 'Código identificador de la carrera',
    nombre VARCHAR(100) NOT NULL,
    institucion VARCHAR(100) NOT NULL,
    duracion_anios INT NOT NULL
);

-- 2. Tabla: Alumno (Perfil y datos del estudiante)
CREATE TABLE IF NOT EXISTS alumno (
    id_alumno INT AUTO_INCREMENT PRIMARY KEY,
    id_carrera INT NOT NULL,
    legajo VARCHAR(20) UNIQUE NOT NULL COMMENT 'Legajo del alumno',
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    anio_ingreso INT NOT NULL,
    CONSTRAINT fk_alumno_carrera 
        FOREIGN KEY (id_carrera) 
        REFERENCES carrera(id_carrera) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- 3. Tabla: Materia (Asignaturas pertenecientes a una carrera)
CREATE TABLE IF NOT EXISTS materia (
    id_materia INT AUTO_INCREMENT PRIMARY KEY,
    id_carrera INT NOT NULL,
    codigo VARCHAR(20) UNIQUE NOT NULL COMMENT 'Código único de la materia',
    nombre VARCHAR(100) NOT NULL,
    anio_plan INT NOT NULL COMMENT 'Año al que pertenece la materia (1, 2, 3, etc.)',
    cuatrimestre ENUM('ANUAL', 'PRIMER_CUATRIMESTRE', 'SEGUNDO_CUATRIMESTRE') NOT NULL,
    carga_horaria INT NOT NULL COMMENT 'Carga horaria total en horas',
    CONSTRAINT fk_materia_carrera 
        FOREIGN KEY (id_carrera) 
        REFERENCES carrera(id_carrera) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- 4. Tabla: Historial Académico (Estado y notas del alumno en cada materia)
-- Relación 1:N desde alumno y 1:N desde materia
CREATE TABLE IF NOT EXISTS historial_academico (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno INT NOT NULL,
    id_materia INT NOT NULL,
    estado ENUM('CURSANDO', 'REGULAR', 'APROBADA', 'DESAPROBADA') NOT NULL,
    nota_final DECIMAL(4,2) NULL COMMENT 'Nota final numérica (si aplica)',
    ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_historial_alumno 
        FOREIGN KEY (id_alumno) 
        REFERENCES alumno(id_alumno) 
        ON DELETE CASCADE,
    CONSTRAINT fk_historial_materia 
        FOREIGN KEY (id_materia) 
        REFERENCES materia(id_materia) 
        ON DELETE RESTRICT,
    CONSTRAINT uk_alumno_materia UNIQUE (id_alumno, id_materia) COMMENT 'Evita duplicidad de estados para la misma materia en un alumno'
);

-- 5. Tabla: Correlatividad (Relación N:M recursiva sobre materia)
CREATE TABLE IF NOT EXISTS correlatividad (
    id_materia_destino INT NOT NULL COMMENT 'Materia que el alumno desea cursar o rendir',
    id_materia_requisito INT NOT NULL COMMENT 'Materia previa que actúa como condición',
    tipo_requisito ENUM('REGULAR', 'APROBADA') NOT NULL COMMENT 'Tipo de condición requerida',
    PRIMARY KEY (id_materia_destino, id_materia_requisito),
    CONSTRAINT chk_no_autodependencia CHECK (id_materia_destino <> id_materia_requisito),
    CONSTRAINT fk_correlatividad_destino 
        FOREIGN KEY (id_materia_destino) 
        REFERENCES materia(id_materia) 
        ON DELETE CASCADE,
    CONSTRAINT fk_correlatividad_requisito 
        FOREIGN KEY (id_materia_requisito) 
        REFERENCES materia(id_materia) 
        ON DELETE CASCADE
);

-- =====================================================================
-- DATOS DE PRUEBA (SEED DATA) — UTN INSPT
-- =====================================================================

-- 1. Carrera
INSERT INTO carrera (id_carrera, codigo, nombre, institucion, duracion_anios) VALUES
(1, 'INF-APLICADA', 'Tecnicatura Superior en Informática Aplicada', 'UTN - INSPT', 3)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- 2. Materias (Plan 2006 - Tecnicatura Superior en Informática Aplicada)
INSERT INTO materia (id_materia, id_carrera, codigo, nombre, anio_plan, cuatrimestre, carga_horaria) VALUES
-- Año 1
(1, 1, 'AM-1', 'Análisis Matemático I', 1, 'ANUAL', 96),
(2, 1, 'CFQ', 'Complementos de Física y Química', 1, 'ANUAL', 96),
(3, 1, 'EYO', 'Economía y Organización', 1, 'ANUAL', 64),
(4, 1, 'ING-1', 'Inglés Técnico I', 1, 'ANUAL', 64),
(5, 1, 'LAB', 'Laboratorio', 1, 'ANUAL', 96),
(6, 1, 'PROG-1', 'Programación I', 1, 'ANUAL', 128),
(7, 1, 'APE', 'Álgebra, Probabilidades y Estadística', 1, 'ANUAL', 96),

-- Año 2
(8, 1, 'AM-2', 'Análisis Matemático II', 2, 'ANUAL', 96),
(9, 1, 'EBD', 'Estructura y Base de Datos', 2, 'ANUAL', 128),
(10, 1, 'ING-2', 'Inglés Técnico II', 2, 'ANUAL', 64),
(11, 1, 'MD', 'Modelos Discretos', 2, 'ANUAL', 96),
(12, 1, 'PROG-2', 'Programación II', 2, 'ANUAL', 128),
(13, 1, 'SC-1', 'Sistemas de Computación I', 2, 'ANUAL', 96),

-- Año 3
(14, 1, 'ING-3', 'Inglés Técnico III', 3, 'ANUAL', 64),
(15, 1, 'PA', 'Probabilidad Aplicada', 3, 'ANUAL', 96),
(16, 1, 'PRC', 'Problemática de la Realidad Contemporánea', 3, 'ANUAL', 64),
(17, 1, 'PROG-3', 'Programación III', 3, 'ANUAL', 128),
(18, 1, 'SEM', 'Seminario', 3, 'ANUAL', 96),
(19, 1, 'SC-2', 'Sistemas de Computación II', 3, 'ANUAL', 96),
(20, 1, 'TD', 'Técnicas Digitales', 3, 'ANUAL', 96)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), anio_plan = VALUES(anio_plan);

-- 3. Correlatividades (Requisitos directos)
-- Inglés Técnico III <- Inglés Técnico II
-- Probabilidad Aplicada <- Modelos Discretos, Análisis Matemático II
-- Programación III <- Programación II, Sistemas de Computación I, Estructura y Base de Datos
-- Seminario <- Modelos Discretos, Programación II, Sistemas de Computación I, Estructura y Base de Datos
-- Sistemas de Computación II <- Sistemas de Computación I, Estructura y Base de Datos
-- Técnicas Digitales <- Modelos Discretos, Sistemas de Computación I
INSERT INTO correlatividad (id_materia_destino, id_materia_requisito, tipo_requisito) VALUES
(14, 10, 'APROBADA'),
(15, 11, 'APROBADA'),
(15, 8, 'APROBADA'),
(17, 12, 'APROBADA'),
(17, 13, 'APROBADA'),
(17, 9, 'APROBADA'),
(18, 11, 'APROBADA'),
(18, 12, 'APROBADA'),
(18, 13, 'APROBADA'),
(18, 9, 'APROBADA'),
(19, 13, 'APROBADA'),
(19, 9, 'APROBADA'),
(20, 11, 'APROBADA'),
(20, 13, 'APROBADA')
ON DUPLICATE KEY UPDATE tipo_requisito = VALUES(tipo_requisito);

-- 4. Alumno de prueba
INSERT INTO alumno (id_alumno, id_carrera, legajo, nombre, apellido, anio_ingreso) VALUES
(1, 1, 'ALU-2024-001', 'Juan', 'Pérez', 2024)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

