# api-peliculas

API de películas en Java: aplicación **WAR** con **Servlets** (`javax.servlet`), **Jackson** para JSON y **MySQL** mediante JDBC.

**No usa Spring Boot** ni dependencias de Spring. Los endpoints los expone `MovieServlet` con `@WebServlet(urlPatterns = {"/movies", "/movies/*"})`.

## Requisitos

- **JDK 17**
- **Maven 3.x**
- **Apache Tomcat 9.x** (compatible con `javax.servlet`)
- **MySQL** en ejecución

## Configuración

1. Copiá [`.env.example`](.env.example) a **`.env`** en la raíz del repositorio.
2. Completá al menos **`MOVIES_DB_PASSWORD`**.

| Variable | Obligatoria | Valor por defecto |
|----------|-------------|-------------------|
| `MOVIES_JDBC_URL` | No | `jdbc:mysql://localhost:3306/movies_cac` |
| `MOVIES_DB_USER` | No | `root` |
| `MOVIES_DB_PASSWORD` | **Sí** | — |
| `MOVIES_DOTENV_DIRECTORY` | No | directorio de trabajo del proceso |

Precedencia de configuración JDBC: **variables de entorno del sistema** → **`.env`** → **defaults** (solo URL y usuario). Lo resuelve `com.cac.peliculas.config.ConfiguracionJdbc`.

Si Tomcat no arranca con el directorio de trabajo en la raíz del repo, definí `MOVIES_DOTENV_DIRECTORY` o las variables en `setenv.bat` / `setenv.sh` (ver [docs/local-setup.md](docs/local-setup.md)).

## Crear la base de datos

```powershell
Get-Content sql\init.sql | mysql -u root -p
```

Crea la base `movies_cac`, la tabla `movies` y datos de ejemplo (incluye *Inception* de Christopher Nolan para probar filtros).

### Tabla `movies`

| Columna | Tipo | En JSON (camelCase) |
|---------|------|---------------------|
| `id` | INT, PK, autoincrement | `id` (solo en respuestas) |
| `title` | VARCHAR(255), NOT NULL | `title` |
| `director` | VARCHAR(255) | `director` |
| `cast_members` | TEXT | `cast` |
| `synopsis` | TEXT | `synopsis` |
| `release_year` | SMALLINT | `releaseYear` |
| `genre` | VARCHAR(100) | `genre` |
| `duration_minutes` | INT | `durationMinutes` |
| `language` | VARCHAR(50) | `language` |
| `country` | VARCHAR(100) | `country` |
| `rating` | DECIMAL(3,1) | `rating` (0–10) |
| `poster_url` | VARCHAR(500) | `posterUrl` |

Los **query params** de filtro usan **snake_case** (`release_year`, `min_rating`).

## Compilar

```powershell
mvn clean package
```

Genera `target/api-peliculas.war`.

## Ejecutar

1. Copiá el WAR a Tomcat: `webapps/api-peliculas.war`
2. Iniciá Tomcat (puerto **8080** por defecto)
3. Asegurate de que Tomcat encuentre el `.env` o las variables JDBC (ver [docs/local-setup.md](docs/local-setup.md))

**URLs base (context path `/api-peliculas`):**

| Recurso | URL |
|---------|-----|
| Página de bienvenida | http://localhost:8080/api-peliculas/ |
| Swagger UI | http://localhost:8080/api-peliculas/swagger-ui/ |
| OpenAPI (YAML) | http://localhost:8080/api-peliculas/openapi/openapi.yaml |

## Endpoints

Rutas relativas al context path. Ejemplo completo: `http://localhost:8080/api-peliculas/movies`.

| Método | Ruta | Descripción | Respuesta exitosa |
|--------|------|-------------|-------------------|
| GET | `/movies` | Lista con filtros opcionales | **200** + array JSON |
| GET | `/movies/{id}` | Una película por ID | **200** + objeto JSON |
| POST | `/movies` | Crea una película | **201** + objeto JSON |
| PUT | `/movies/{id}` | Reemplazo completo del recurso | **200** + objeto JSON |
| PATCH | `/movies/{id}` | Actualización parcial | **200** + objeto JSON |
| DELETE | `/movies/{id}` | Elimina por ID | **204** sin cuerpo |
| OPTIONS | cualquiera | Preflight CORS | **204** sin cuerpo |

`POST` solo está permitido en `/movies` (no en `/movies/{id}` → **405**).

### Filtros en GET `/movies`

Todos opcionales y combinables:

| Parámetro | Ejemplo |
|-----------|---------|
| `title` | `?title=padrino` (parcial, sin distinguir mayúsculas) |
| `genre` | `?genre=Drama` |
| `director` | `?director=Christopher Nolan` |
| `release_year` | `?release_year=2010` |
| `country` | `?country=USA` |
| `language` | `?language=English` |
| `min_rating` | `?min_rating=8` |
| `max_rating` | `?max_rating=9` |

Valores numéricos inválidos en `release_year`, `min_rating` o `max_rating` devuelven **400**.

Ejemplos:

```
GET /api-peliculas/movies?genre=Drama
GET /api-peliculas/movies?director=Christopher%20Nolan
GET /api-peliculas/movies?release_year=2010
GET /api-peliculas/movies?genre=Sci-Fi&min_rating=8
```

### Validación (POST, PUT, PATCH)

| Regla | POST | PUT | PATCH |
|-------|------|-----|-------|
| `title` obligatorio y no vacío | Sí | Sí | Solo si se envía `title` |
| Al menos un campo en el body | — | — | Sí |
| `releaseYear` entre 1888 y 2100 | Si se envía | Si se envía | Si se envía |
| `durationMinutes` > 0 | Si se envía | Si se envía | Si se envía |
| `rating` entre 0 y 10 | Si se envía | Si se envía | Si se envía |
| `genre` no puede ser cadena vacía | Si se envía | Si se envía | Si se envía |

### Ejemplo JSON para POST / PUT

```json
{
  "title": "Matrix",
  "director": "Lana Wachowski, Lilly Wachowski",
  "cast": "Keanu Reeves, Laurence Fishburne",
  "synopsis": "Un hacker descubre la verdadera naturaleza de la realidad.",
  "releaseYear": 1999,
  "genre": "Sci-Fi",
  "durationMinutes": 136,
  "language": "English",
  "country": "USA",
  "rating": 8.7,
  "posterUrl": "https://example.com/posters/matrix.jpg"
}
```

### Ejemplo PATCH (solo campos a cambiar)

```json
{
  "rating": 9.0,
  "genre": "Sci-Fi"
}
```

### CORS

El servlet habilita CORS para desarrollo: `Access-Control-Allow-Origin: *`, métodos `GET, POST, PUT, PATCH, DELETE, OPTIONS` y cabecera `Content-Type`.

### Respuestas de error (JSON)

```json
{
  "error": "not_found",
  "message": "Movie not found with id: 99"
}
```

| Código `error` | HTTP | Cuándo |
|----------------|------|--------|
| `validation_error` | 400 | Body o filtros inválidos, ID de path inválido |
| `not_found` | 404 | Película inexistente |
| `method_not_allowed` | 405 | Método no permitido en la ruta |
| `database_error` | 500 | Fallo JDBC |
| `internal_error` | 500 | Error inesperado |

## Swagger / OpenAPI

- Fuente: `src/main/resources/openapi/openapi.yaml`
- Copia servida en runtime: `src/main/webapp/openapi/openapi.yaml`
- UI estática (Swagger UI vía CDN): http://localhost:8080/api-peliculas/swagger-ui/

## Estructura del proyecto

```
src/main/java/com/cac/peliculas/
  config/         ConfiguracionJdbc, DatabaseConnection
  model/          Movie (entidad de dominio / persistencia)
  dto/            MovieRequest, MovieUpdateRequest, MoviePatchRequest,
                  MovieResponse, MovieFilter, ErrorResponse
  repository/     MovieRepository (SQL JDBC)
  service/        MovieService (reglas de negocio)
  servlet/        MovieServlet (@WebServlet /movies, /movies/*)
  exception/      ApiException, ValidationException, MovieNotFoundException, DatabaseException
  util/           MovieValidator, HttpJson, ServletPaths
src/main/resources/openapi/
src/main/webapp/
  index.jsp       Página de bienvenida
  swagger-ui/     UI de documentación
  openapi/        YAML publicado
sql/init.sql
```

## Archivos principales

| Archivo | Rol |
|---------|-----|
| `pom.xml` | Dependencias Maven y empaquetado WAR |
| `sql/init.sql` | Esquema `movies` y datos iniciales |
| `servlet/MovieServlet.java` | Entrada HTTP, CORS, serialización JSON |
| `service/MovieService.java` | Orquestación y validación |
| `repository/MovieRepository.java` | Consultas y comandos SQL |
| `util/MovieValidator.java` | Reglas de validación compartidas |
| `config/DatabaseConnection.java` | Obtiene conexión JDBC |

Guía paso a paso para levantar Tomcat y MySQL: **[docs/local-setup.md](docs/local-setup.md)**.
