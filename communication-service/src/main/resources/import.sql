-- COMUNICADOS (autor_id '1' es el profe Roberto)
INSERT INTO comunicados (titulo, contenido, autor_id, fecha_publicacion, destinatario) VALUES ('Reunión de Apoderados', 'Se cita a reunión para el viernes 15 de mayo a las 18:00 hrs. Asistencia obligatoria.', '1', '2026-05-11 08:00:00', 'GENERAL');
INSERT INTO comunicados (titulo, contenido, autor_id, fecha_publicacion, destinatario) VALUES ('Feria Científica', 'Recuerden traer los materiales reciclados para la exposición.', '2', '2026-05-10 10:30:00', 'CURSO_1');

-- MENSAJES (De Profe Roberto '1' a la apoderada Carolina '1')
INSERT INTO mensajes (remitente_id, destinatario_id, contenido, fecha_envio, tipo_notificacion) VALUES ('1', '1', 'Estimada apoderada, le informo que Martina tuvo un excelente rendimiento esta semana.', '2026-05-11 09:00:00', 'ACADEMICA');
INSERT INTO mensajes (remitente_id, destinatario_id, contenido, fecha_envio, tipo_notificacion) VALUES ('1', '1', 'Recuerde enviar la autorización para la salida a terreno.', '2026-05-11 11:15:00', 'ADMINISTRATIVA');