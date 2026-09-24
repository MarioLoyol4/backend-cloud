-- 1. CREAR CURSOS
INSERT INTO cursos (id, nivel, letra, año) VALUES (1, '1 Medio', 'A', 2026);
INSERT INTO cursos (id, nivel, letra, año) VALUES (2, '1 Medio', 'B', 2026);
ALTER TABLE cursos ALTER COLUMN id RESTART WITH 3;

-- 2. CREAR ASIGNATURAS
INSERT INTO asignaturas (id, nombre) VALUES (1, 'Matemáticas');
INSERT INTO asignaturas (id, nombre) VALUES (2, 'Lenguaje y Comunicación');
INSERT INTO asignaturas (id, nombre) VALUES (3, 'Historia y Geografía');
ALTER TABLE asignaturas ALTER COLUMN id RESTART WITH 4;

-- 3. CREAR DOCENTES (Contraseña encriptada para: 123456)
INSERT INTO docentes (id, rut, nombre, segundo_nombre, apellido, segundo_apellido, email, telefono, password) VALUES (1, '11111111-1', 'Roberto', 'Carlos', 'Gómez', 'Tapia', 'roberto@colegio.cl', '+56911111111', '$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO');
INSERT INTO docentes (id, rut, nombre, segundo_nombre, apellido, segundo_apellido, email, telefono, password) VALUES (2, '22222222-2', 'Camila', 'Andrea', 'Soto', 'Díaz', 'camila@colegio.cl', '+56922222222', '$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO');
ALTER TABLE docentes ALTER COLUMN id RESTART WITH 3;

-- Relación Docente-Asignatura
INSERT INTO docente_asignatura (docente_id, asignatura_id) VALUES (1, 1);
INSERT INTO docente_asignatura (docente_id, asignatura_id) VALUES (1, 2);
INSERT INTO docente_asignatura (docente_id, asignatura_id) VALUES (2, 3);

-- 4. CREAR ESTUDIANTES (Contraseña encriptada para: 123456)
INSERT INTO estudiantes (id, rut, nombre, segundo_nombre, apellido, segundo_apellido, email, password, curso_id) VALUES (1, '33333333-3', 'Martina', 'Ignacia', 'Pérez', 'López', 'martina@alumno.cl', '$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO', 1);
INSERT INTO estudiantes (id, rut, nombre, segundo_nombre, apellido, segundo_apellido, email, password, curso_id) VALUES (2, '44444444-4', 'Lucas', 'Mateo', 'Rojas', 'Silva', 'lucas@alumno.cl', '$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO', 1);
ALTER TABLE estudiantes ALTER COLUMN id RESTART WITH 3;

-- 5. CREAR APODERADOS (Contraseña encriptada para: 123456)
INSERT INTO apoderados (id, rut, nombre, segundo_nombre, apellido, segundo_apellido, email, telefono, password) VALUES (1, '55555555-5', 'Carolina', 'Paz', 'López', 'Muñoz', 'carolina@apoderado.cl', '+56955555555', '$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO');
ALTER TABLE apoderados ALTER COLUMN id RESTART WITH 2;

-- Relación Apoderado-Estudiante
INSERT INTO apoderado_estudiante (apoderado_id, estudiante_id) VALUES (1, 1);

-- 6. CREAR EVALUACIONES
INSERT INTO evaluaciones (id, nombre, fecha, asignatura_id) VALUES (1, 'Prueba de Álgebra', '2026-05-01', 1);
INSERT INTO evaluaciones (id, nombre, fecha, asignatura_id) VALUES (2, 'Control de Geometría', '2026-05-15', 1);
INSERT INTO evaluaciones (id, nombre, fecha, asignatura_id) VALUES (3, 'Lectura Complementaria', '2026-05-10', 2);
ALTER TABLE evaluaciones ALTER COLUMN id RESTART WITH 4;

-- 7. INGRESAR NOTAS
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (1, 6.5, 1, 1);
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (2, 7.0, 1, 2);
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (3, 5.8, 1, 3);
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (4, 4.5, 2, 1);
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (5, 5.0, 2, 2);
INSERT INTO notas (id, valor, estudiante_id, evaluacion_id) VALUES (6, 6.2, 2, 3);
ALTER TABLE notas ALTER COLUMN id RESTART WITH 7;