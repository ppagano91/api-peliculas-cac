-- Inicialización de base de datos para api-peliculas (Servlet + JDBC)
-- Compatible con MOVIES_JDBC_URL por defecto: jdbc:mysql://localhost:3306/movies_cac

CREATE DATABASE IF NOT EXISTS movies_cac
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE movies_cac;

DROP TABLE IF EXISTS movies;

CREATE TABLE movies (
  id               INT            NOT NULL AUTO_INCREMENT,
  title            VARCHAR(255)   NOT NULL,
  director         VARCHAR(255)   NULL,
  cast_members     TEXT           NULL,
  synopsis         TEXT           NULL,
  release_year     SMALLINT       NULL,
  genre            VARCHAR(100)   NULL,
  duration_minutes INT            NULL,
  language         VARCHAR(50)    NULL,
  country          VARCHAR(100)   NULL,
  rating           DECIMAL(3, 1)  NULL,
  poster_url       VARCHAR(500)   NULL,
  PRIMARY KEY (id),
  CONSTRAINT chk_release_year CHECK (release_year IS NULL OR (release_year >= 1888 AND release_year <= 2100)),
  CONSTRAINT chk_duration CHECK (duration_minutes IS NULL OR (duration_minutes > 0 AND duration_minutes <= 600)),
  CONSTRAINT chk_rating CHECK (rating IS NULL OR (rating >= 0 AND rating <= 10))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

INSERT INTO movies (
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
    'Inception',
    'Christopher Nolan',
    'Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page',
    'Un ladrón especializado en infiltrarse en los sueños ajenos.',
    2010,
    'Sci-Fi',
    148,
    'English',
    'USA',
    8.8,
    'https://example.com/posters/inception.jpg'
  );
