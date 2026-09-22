-- Creación del esquema
CREATE SCHEMA IF NOT EXISTS sicra_db DEFAULT CHARACTER SET utf8mb4;
USE sicra_db;

-- 1. Tabla: Perfil del Estudiante (Datos biográficos)
CREATE TABLE IF NOT EXISTS student_profile (
    id_student INT AUTO_INCREMENT PRIMARY KEY,
    file_number VARCHAR(20) UNIQUE NOT NULL COMMENT 'Legajo del alumno',
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    enrollment_year INT NOT NULL
);

-- 2. Tabla: Cuenta de Usuario (Gestión de sesión y autenticación)
-- Relación 1:1 con student_profile
CREATE TABLE IF NOT EXISTS user_account (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    id_student INT UNIQUE COMMENT 'Clave foránea UNIQUE para garantizar relación 1:1',
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_student 
        FOREIGN KEY (id_student) 
        REFERENCES student_profile(id_student) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

-- 3. Tabla: Materia (Catálogo de asignaturas)
CREATE TABLE IF NOT EXISTS subject (
    id_subject INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    academic_year INT NOT NULL COMMENT 'Año al que pertenece la materia (1, 2, 3)',
    term ENUM('ANUAL', 'CUATRIMESTRE_1', 'CUATRIMESTRE_2') NOT NULL
);

-- 4. Tabla: Historial Académico (Rendimiento del alumno)
-- Relación 1:N desde student_profile y 1:N desde subject
CREATE TABLE IF NOT EXISTS academic_record (
    id_record INT AUTO_INCREMENT PRIMARY KEY,
    id_student INT NOT NULL,
    id_subject INT NOT NULL,
    status ENUM('ENROLLED', 'REGULAR', 'FINAL_APPROVED') NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_record_student 
        FOREIGN KEY (id_student) 
        REFERENCES student_profile(id_student) 
        ON DELETE CASCADE,
    CONSTRAINT fk_record_subject 
        FOREIGN KEY (id_subject) 
        REFERENCES subject(id_subject) 
        ON DELETE RESTRICT,
    CONSTRAINT uk_student_subject UNIQUE (id_student, id_subject) COMMENT 'Evita duplicidad de estados para la misma materia'
);

-- 5. Tabla: Correlatividades (Motor de dependencias)
-- Relación N:M recursiva sobre la tabla subject
CREATE TABLE IF NOT EXISTS subject_correlativity (
    id_subject_target INT NOT NULL COMMENT 'Materia que el alumno desea cursar',
    id_subject_requirement INT NOT NULL COMMENT 'Materia que funciona como requisito',
    requirement_type ENUM('REGULAR', 'FINAL_APPROVED') NOT NULL COMMENT 'Exigencia: ¿Cursada o Final?',
    PRIMARY KEY (id_subject_target, id_subject_requirement),
    CONSTRAINT fk_target 
        FOREIGN KEY (id_subject_target) 
        REFERENCES subject(id_subject) 
        ON DELETE CASCADE,
    CONSTRAINT fk_requirement 
        FOREIGN KEY (id_subject_requirement) 
        REFERENCES subject(id_subject) 
        ON DELETE CASCADE
);