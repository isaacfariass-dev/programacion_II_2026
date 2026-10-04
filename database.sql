-- Creación del esquema
CREATE SCHEMA IF NOT EXISTS sicra_db DEFAULT CHARACTER SET utf8mb4;
USE sicra_db;

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

-- 3. Tabla: Usuario (Gestión de credenciales y roles)
-- Relación 1:1 opcional con alumno (NULL si el rol es ADMIN)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno INT UNIQUE NULL COMMENT 'Clave foránea UNIQUE (1:1). Es NULL para administradores',
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

-- 4. Tabla: Materia (Asignaturas pertenecientes a una carrera)
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

-- 5. Tabla: Historial Académico (Estado y notas del alumno en cada materia)
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

-- 6. Tabla: Correlatividad (Relación N:M recursiva sobre materia)
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
