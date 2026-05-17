-- ----------------------------------------------------------------------------
-- Put here INSERT statements for inserting data required by the application
-- in the "paproject" database.
-------------------------------------------------------------------------------

-- Usuarios (viewer y ticketseller)
-- Password 'pa2526' -> $2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u
-- Roles: ESPECTADOR = 0, TAQUILLERO = 1
INSERT INTO User (userName, password, firstName, lastName, email, role) VALUES
                                                                            ('viewer', '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Viewer', 'User', 'viewer@udc.es', 0),
                                                                            ('ticketseller', '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Ticket', 'Seller', 'seller@udc.es', 1);

-- 2 Películas
INSERT INTO Pelicula (titulo, resumen, duracion) VALUES
                                                     ('Pelicula A', 'Resumen de la pelicula A', 120),
                                                     ('Pelicula B', 'Resumen de la pelicula B', 150);

-- 2 Salas
-- Sala 1: Capacidad 15 (> 10)
-- Sala 2: Capacidad 9
INSERT INTO Sala (nombre, capacidad) VALUES
                                         ('Sala Grande', 15),
                                         ('Sala Pequeña', 9);

INSERT INTO Sesion (sala_id, pelicula_id, fechaHora, precio, localidadesLibres, version) VALUES

(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '0 00:05' DAY_MINUTE), 8.50, 10, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '0 23:55' DAY_MINUTE), 9.00, 9, 0),
-- Día +1
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '1 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 1, DATE_ADD(DATE(NOW()), INTERVAL '1 20:00' DAY_MINUTE), 9.00, 9, 0),
-- Día +2
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '2 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '2 20:00' DAY_MINUTE), 9.00, 9, 0),
-- Día +3
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '3 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 1, DATE_ADD(DATE(NOW()), INTERVAL '3 20:00' DAY_MINUTE), 9.00, 9, 0),
-- Día +4
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '4 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '4 20:00' DAY_MINUTE), 9.00, 9, 0),
-- Día +5
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '5 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 1, DATE_ADD(DATE(NOW()), INTERVAL '5 20:00' DAY_MINUTE), 9.00, 9, 0),
-- Día +6
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '6 17:00' DAY_MINUTE), 8.50, 15, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '6 20:00' DAY_MINUTE), 9.00, 9, 0);

INSERT INTO Compra (userId, sesionId, fechaRegistroCompra, numLocalidades, tarjetaBancaria, entregada, version) VALUES
                                                                                                                    (1, 1, NOW(), 2, '1234567890123456', 0, 0),
                                                                                                                    (1, 1, NOW(), 3, '1234567890123456', 0, 0);