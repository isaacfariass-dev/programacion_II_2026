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

-- 2.1. Tabla: Usuario (Credenciales de acceso del alumno — relación 1:1 con alumno)
-- Preparada para una futura implementación de login; no se usa todavía para autenticar.
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno INT NOT NULL UNIQUE COMMENT 'Un alumno tiene a lo sumo un usuario (1:1)',
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    clave VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'ALUMNO') NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_alumno
        FOREIGN KEY (id_alumno)
        REFERENCES alumno(id_alumno)
        ON DELETE CASCADE
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

-- 5. Usuario de prueba (1:1 con el alumno de prueba)
INSERT INTO usuario (id_usuario, id_alumno, nombre_usuario, clave, rol) VALUES
(1, 1, 'jperez', 'cambiar-en-login-futuro', 'ALUMNO')
ON DUPLICATE KEY UPDATE nombre_usuario = VALUES(nombre_usuario);


-- =====================================================================
-- CARRERA DE PRUEBA: Ingeniería genérica de 5 años con materias cuatrimestrales
-- Sirve para probar correlatividades entre 1.er y 2.º cuatrimestre (incluso dentro del mismo año)
-- y su convivencia con materias anuales.
-- =====================================================================

-- 6. Carrera
INSERT INTO carrera (id_carrera, codigo, nombre, institucion, duracion_anios) VALUES
(2, 'ING-GEN', 'Ingeniería en Sistemas (plan genérico de prueba)', 'Universidad de Prueba', 5)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- 7. Materias (ids 101+ para no chocar con la tecnicatura)
INSERT INTO materia (id_materia, id_carrera, codigo, nombre, anio_plan, cuatrimestre, carga_horaria) VALUES
-- Año 1
(101, 2, 'IG-AM1',  'Análisis Matemático I',              1, 'PRIMER_CUATRIMESTRE',  128),
(102, 2, 'IG-AGA',  'Álgebra y Geometría Analítica',      1, 'PRIMER_CUATRIMESTRE',  128),
(103, 2, 'IG-ISI',  'Introducción a la Ingeniería',       1, 'PRIMER_CUATRIMESTRE',   64),
(104, 2, 'IG-FIS1', 'Física I',                           1, 'SEGUNDO_CUATRIMESTRE', 128),
(105, 2, 'IG-PRG1', 'Programación I',                     1, 'SEGUNDO_CUATRIMESTRE', 128),
(106, 2, 'IG-SR',   'Sistemas de Representación',         1, 'ANUAL',                 96),
(107, 2, 'IG-QUI',  'Química General',                    1, 'SEGUNDO_CUATRIMESTRE',  96),
-- Año 2
(108, 2, 'IG-AM2',  'Análisis Matemático II',             2, 'PRIMER_CUATRIMESTRE',  128),
(109, 2, 'IG-FIS2', 'Física II',                          2, 'PRIMER_CUATRIMESTRE',  128),
(110, 2, 'IG-PRG2', 'Programación II',                    2, 'PRIMER_CUATRIMESTRE',  128),
(111, 2, 'IG-PYE',  'Probabilidad y Estadística',         2, 'SEGUNDO_CUATRIMESTRE',  96),
(112, 2, 'IG-ED',   'Estructuras de Datos',               2, 'SEGUNDO_CUATRIMESTRE', 128),
(113, 2, 'IG-ARQ',  'Arquitectura de Computadoras',       2, 'ANUAL',                128),
(114, 2, 'IG-ING1', 'Inglés I',                           2, 'ANUAL',                 64),
-- Año 3
(115, 2, 'IG-MN',   'Métodos Numéricos',                  3, 'PRIMER_CUATRIMESTRE',   96),
(116, 2, 'IG-BD1',  'Bases de Datos I',                   3, 'PRIMER_CUATRIMESTRE',  128),
(117, 2, 'IG-SO',   'Sistemas Operativos',                3, 'PRIMER_CUATRIMESTRE',  128),
(118, 2, 'IG-POO',  'Paradigmas de Programación',         3, 'SEGUNDO_CUATRIMESTRE', 128),
(119, 2, 'IG-RED1', 'Redes de Datos I',                   3, 'SEGUNDO_CUATRIMESTRE',  96),
(120, 2, 'IG-ECO',  'Economía',                           3, 'ANUAL',                 64),
(121, 2, 'IG-ING2', 'Inglés II',                          3, 'ANUAL',                 64),
-- Año 4
(122, 2, 'IG-ISW1', 'Ingeniería de Software I',           4, 'PRIMER_CUATRIMESTRE',  128),
(123, 2, 'IG-BD2',  'Bases de Datos II',                  4, 'PRIMER_CUATRIMESTRE',   96),
(124, 2, 'IG-IO',   'Investigación Operativa',            4, 'PRIMER_CUATRIMESTRE',   96),
(125, 2, 'IG-RED2', 'Redes de Datos II',                  4, 'SEGUNDO_CUATRIMESTRE',  96),
(126, 2, 'IG-SEG',  'Seguridad Informática',              4, 'SEGUNDO_CUATRIMESTRE',  96),
(127, 2, 'IG-ADM',  'Administración de Proyectos',        4, 'ANUAL',                 96),
(128, 2, 'IG-SIM',  'Simulación',                         4, 'SEGUNDO_CUATRIMESTRE',  96),
-- Año 5
(129, 2, 'IG-ISW2', 'Ingeniería de Software II',          5, 'PRIMER_CUATRIMESTRE',  128),
(130, 2, 'IG-IA',   'Inteligencia Artificial',            5, 'PRIMER_CUATRIMESTRE',   96),
(131, 2, 'IG-SD',   'Sistemas Distribuidos',              5, 'PRIMER_CUATRIMESTRE',   96),
(132, 2, 'IG-LEG',  'Legislación',                        5, 'SEGUNDO_CUATRIMESTRE',  64),
(133, 2, 'IG-ELEC', 'Electiva',                           5, 'SEGUNDO_CUATRIMESTRE',  64),
(134, 2, 'IG-PF',   'Proyecto Final',                     5, 'ANUAL',                192)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), anio_plan = VALUES(anio_plan), cuatrimestre = VALUES(cuatrimestre);

-- 8. Correlatividades (todas cumplen: el requisito termina antes de que empiece la materia destino)
INSERT INTO correlatividad (id_materia_destino, id_materia_requisito, tipo_requisito) VALUES
-- Año 2
(108, 101, 'APROBADA'), (108, 102, 'REGULAR'),   -- AM2  <- AM1, AGA
(109, 104, 'REGULAR'),  (109, 101, 'APROBADA'),  -- FIS2 <- FIS1, AM1
(110, 105, 'APROBADA'),                          -- PRG2 <- PRG1
(111, 101, 'APROBADA'), (111, 102, 'APROBADA'),  -- PYE  <- AM1, AGA
(112, 110, 'REGULAR'),                           -- ED (2C) <- PRG2 (1C del mismo año)
(113, 105, 'REGULAR'),                           -- ARQ (anual, año 2) <- PRG1
-- Año 3
(115, 108, 'APROBADA'), (115, 110, 'REGULAR'),   -- MN   <- AM2, PRG2
(116, 112, 'APROBADA'),                          -- BD1  <- ED
(117, 113, 'REGULAR'),  (117, 112, 'REGULAR'),   -- SO   <- ARQ, ED
(118, 112, 'APROBADA'),                          -- POO  <- ED
(119, 117, 'REGULAR'),                           -- RED1 (2C) <- SO (1C del mismo año)
(120, 103, 'APROBADA'),                          -- ECO  <- ISI
(121, 114, 'APROBADA'),                          -- ING2 <- ING1
-- Año 4
(122, 118, 'APROBADA'), (122, 116, 'REGULAR'),   -- ISW1 <- POO, BD1
(123, 116, 'APROBADA'),                          -- BD2  <- BD1
(124, 111, 'APROBADA'), (124, 115, 'REGULAR'),   -- IO   <- PYE, MN
(125, 119, 'APROBADA'),                          -- RED2 <- RED1
(126, 119, 'REGULAR'),  (126, 117, 'APROBADA'),  -- SEG  <- RED1, SO
(127, 120, 'APROBADA'),                          -- ADM  <- ECO
(128, 124, 'REGULAR'),                           -- SIM (2C) <- IO (1C del mismo año)
-- Año 5
(129, 122, 'APROBADA'),                          -- ISW2 <- ISW1
(130, 124, 'APROBADA'), (130, 111, 'APROBADA'),  -- IA   <- IO, PYE
(131, 125, 'REGULAR'),  (131, 117, 'APROBADA'),  -- SD   <- RED2, SO
(132, 127, 'REGULAR'),                           -- LEG  <- ADM
(133, 122, 'APROBADA'),                          -- ELEC <- ISW1
(134, 122, 'APROBADA'), (134, 123, 'APROBADA'),  -- PF   <- ISW1, BD2
(134, 127, 'REGULAR'),  (134, 126, 'APROBADA')   --      <- ADM, SEG
ON DUPLICATE KEY UPDATE tipo_requisito = VALUES(tipo_requisito);

-- 9. Alumno de prueba de ingeniería, con 1.er año parcialmente avanzado
INSERT INTO alumno (id_alumno, id_carrera, legajo, nombre, apellido, anio_ingreso) VALUES
(2, 2, 'ING-2025-001', 'Ana', 'Gómez', 2025)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO historial_academico (id_alumno, id_materia, estado, nota_final) VALUES
(2, 101, 'APROBADA', 8.00),   -- AM1
(2, 102, 'APROBADA', 7.00),   -- AGA
(2, 103, 'APROBADA', 9.00),   -- ISI
(2, 105, 'REGULAR',  NULL),   -- PRG1
(2, 104, 'CURSANDO', NULL)    -- FIS1
ON DUPLICATE KEY UPDATE estado = VALUES(estado), nota_final = VALUES(nota_final);
