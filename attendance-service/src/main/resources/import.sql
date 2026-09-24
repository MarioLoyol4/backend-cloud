-- ASISTENCIAS (1 = Martina, 2 = Lucas)
INSERT INTO asistencia (estudiante_id, fecha, estado) VALUES (1, '2026-05-10', 'PRESENTE');
INSERT INTO asistencia (estudiante_id, fecha, estado) VALUES (1, '2026-05-11', 'PRESENTE');
INSERT INTO asistencia (estudiante_id, fecha, estado) VALUES (2, '2026-05-10', 'AUSENTE');
INSERT INTO asistencia (estudiante_id, fecha, estado) VALUES (2, '2026-05-11', 'PRESENTE');

-- ANOTACIONES
INSERT INTO anotaciones (estudiante_id, tipo, descripcion, fecha) VALUES (1, 'POSITIVA', 'Excelente participación en la feria científica.', '2026-05-10');
INSERT INTO anotaciones (estudiante_id, tipo, descripcion, fecha) VALUES (2, 'NEGATIVA', 'No presenta la tarea de Lenguaje y Comunicación.', '2026-05-11');