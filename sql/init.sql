-- Inicialización de base de datos para api-peliculas
-- Compatible con ConfiguracionJdbc (default: jdbc:mysql://localhost:3306/movies_cac)
-- y con las consultas en Controlador.java

CREATE DATABASE IF NOT EXISTS movies_cac
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE movies_cac;

CREATE TABLE IF NOT EXISTS peliculas (
  id_pelicula INT          NOT NULL AUTO_INCREMENT,
  titulo      VARCHAR(255) NOT NULL,
  genero      VARCHAR(100) NOT NULL,
  duracion    VARCHAR(50)  NOT NULL,
  imagen      VARCHAR(500) NOT NULL,
  PRIMARY KEY (id_pelicula)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- Datos de ejemplo (opcional; comentá o borrá si no los necesitás)
INSERT INTO peliculas (titulo, genero, duracion, imagen) VALUES
  ('El Padrino', 'Drama', '175 min', 'https://example.com/imagenes/el-padrino.jpg'),
  ('Pulp Fiction', 'Crimen', '154 min', 'https://example.com/imagenes/pulp-fiction.jpg'),
  ('El Señor de los Anillos: La Comunidad del Anillo', 'Fantasía', '178 min', 'https://example.com/imagenes/lotr-1.jpg');
