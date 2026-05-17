-- Inicialización de base de datos para api-peliculas (Servlet + JDBC)
-- Compatible con MOVIES_JDBC_URL por defecto: jdbc:mysql://localhost:3306/movies_cac
-- y con las consultas en Controlador.java

CREATE DATABASE IF NOT EXISTS movies_cac
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE movies_cac;

-- Si migrás desde el esquema anterior (titulo, genero, duracion, imagen), recreá la tabla
-- o ejecutá un ALTER manual; este script asume instalación nueva o recreación controlada.
DROP TABLE IF EXISTS peliculas;

CREATE TABLE peliculas (
  id               INT            NOT NULL AUTO_INCREMENT,
  title            VARCHAR(255)   NOT NULL,
  director         VARCHAR(255)   NOT NULL,
  cast_members     TEXT           NULL,
  synopsis         TEXT           NULL,
  release_year     SMALLINT       NOT NULL,
  genre            VARCHAR(100)   NOT NULL,
  duration_minutes INT            NOT NULL,
  language         VARCHAR(50)    NULL,
  country          VARCHAR(100)   NULL,
  rating           DECIMAL(3, 1)  NULL,
  poster_url       VARCHAR(500)   NULL,
  PRIMARY KEY (id),
  CONSTRAINT chk_release_year CHECK (release_year >= 1888 AND release_year <= 2100),
  CONSTRAINT chk_duration CHECK (duration_minutes > 0 AND duration_minutes <= 600),
  CONSTRAINT chk_rating CHECK (rating IS NULL OR (rating >= 0 AND rating <= 10))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

INSERT INTO peliculas (
  title, director, cast_members, synopsis, release_year, genre,
  duration_minutes, language, country, rating, poster_url
) VALUES
  (
    'El Padrino',
    'Francis Ford Coppola',
    'Marlon Brando, Al Pacino, James Caan',
    'La saga de una familia de la mafia italiana en Nueva York.',
    1972,
    'Drama',
    175,
    'English',
    'USA',
    9.2,
    'https://example.com/posters/el-padrino.jpg'
  ),
  (
    'Pulp Fiction',
    'Quentin Tarantino',
    'John Travolta, Uma Thurman, Samuel L. Jackson',
    'Historias entrelazadas de crimen en Los Ángeles.',
    1994,
    'Crime',
    154,
    'English',
    'USA',
    8.9,
    'https://example.com/posters/pulp-fiction.jpg'
  ),
  (
    'El Señor de los Anillos: La Comunidad del Anillo',
    'Peter Jackson',
    'Elijah Wood, Ian McKellen, Viggo Mortensen',
    'Un hobbit emprende un viaje para destruir un anillo poderoso.',
    2001,
    'Fantasy',
    178,
    'English',
    'New Zealand',
    8.8,
    'https://example.com/posters/lotr-fellowship.jpg'
  );
