# api-peliculas

API de películas en Java: aplicación **WAR** con **Servlets** (`javax.servlet`), **Jackson** para JSON y **MySQL** mediante JDBC.

**No usa Spring Boot** ni dependencias de Spring. Los endpoints se exponen con un servlet anotado con `@WebServlet`.

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

La conexión la resuelve `config/ConfiguracionJdbc.java` (variables de sistema → `.env` → defaults parciales).

## Crear la base de datos

```powershell
Get-Content sql\init.sql | mysql -u root -p
```

Crea la base `movies_cac`, la tabla `movies` y datos de ejemplo (incluye una película de Christopher Nolan para probar filtros).

### Tabla `movies`

| Columna | Tipo | Notas |
|---------|------|--------|
| `id` | INT, PK, autoincrement | |
| `title` | VARCHAR(255), NOT NULL | |
| `director` | VARCHAR(255) | Opcional |
| `cast_members` | TEXT | En JSON: `cast` |
| `synopsis` | TEXT | |
| `release_year` | SMALLINT | En JSON: `releaseYear` |
| `genre` | VARCHAR(100) | |
| `duration_minutes` | INT | En JSON: `durationMinutes` |
| `language` | VARCHAR(50) | |
| `country` | VARCHAR(100) | |
| `rating` | DECIMAL(3,1) | 0–10 |
| `poster_url` | VARCHAR(500) | En JSON: `posterUrl` |

## Compilar

```powershell
mvn clean package
```

Genera `target/api-peliculas.war`.

## Ejecutar

1. Copiá el WAR a Tomcat: `webapps/api-peliculas.war`
2. Iniciá Tomcat (puerto **8080** por defecto)
3. Asegurate de que Tomcat encuentre el `.env` (ver [docs/local-setup.md](docs/local-setup.md))

**URLs base (context path `/api-peliculas`):**

| Recurso | URL |
|---------|-----|
| Página de bienvenida | http://localhost:8080/api-peliculas/ |
| Swagger UI | http://localhost:8080/api-peliculas/swagger-ui/ |
| OpenAPI (YAML) | http://localhost:8080/api-peliculas/openapi/openapi.yaml |

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/movies` | Lista películas (filtros opcionales en query) |
| GET | `/movies/{id}` | Obtiene una película por ID |
| POST | `/movies` | Crea una película |
| PUT | `/movies/{id}` | Actualización completa |
| PATCH | `/movies/{id}` | Actualización parcial |
| DELETE | `/movies/{id}` | Elimina por ID |

### Filtros en GET `/movies`

Todos opcionales y combinables:

| Parámetro | Ejemplo |
|-----------|---------|
| `title` | `?title=padrino` (búsqueda parcial, sin distinguir mayúsculas) |
| `genre` | `?genre=Drama` |
| `director` | `?director=Christopher Nolan` |
| `release_year` | `?release_year=2010` |
| `country` | `?country=USA` |
| `language` | `?language=English` |
| `min_rating` | `?min_rating=8` |
| `max_rating` | `?max_rating=9` |

Ejemplos:

```
GET /api-peliculas/movies?genre=Drama
GET /api-peliculas/movies?director=Christopher%20Nolan
GET /api-peliculas/movies?release_year=2010
GET /api-peliculas/movies?genre=Sci-Fi&min_rating=8
```

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

### Respuestas de error (JSON)

```json
{
  "error": "not_found",
  "message": "Movie not found with id: 99"
}
```

Códigos habituales: **400** validación/ID inválido, **404** no encontrada, **500** error de base de datos.

## Swagger / OpenAPI

- Especificación: `src/main/resources/openapi/openapi.yaml` (copia servida en `webapp/openapi/`).
- UI estática con Swagger UI (CDN): http://localhost:8080/api-peliculas/swagger-ui/
- Sin `springdoc-openapi` ni Spring Boot.

## Estructura del proyecto

```
src/main/java/com/cac/peliculas/
  config/       ConfiguracionJdbc, DatabaseConnection
  model/        Movie
  dto/          requests, responses, filtros, errores
  repository/   MovieRepository (SQL JDBC)
  service/      MovieService (validación y reglas)
  servlet/      MovieServlet (@WebServlet /movies)
  exception/    ApiException y derivadas
  util/         validación, JSON HTTP, paths
src/main/resources/openapi/
src/main/webapp/swagger-ui/
sql/init.sql
```

## Archivos principales

| Archivo | Rol |
|---------|-----|
| `pom.xml` | Dependencias Maven y empaquetado WAR |
| `sql/init.sql` | Esquema y datos iniciales |
| `servlet/MovieServlet.java` | Endpoints HTTP |
| `service/MovieService.java` | Lógica de negocio |
| `repository/MovieRepository.java` | Acceso a datos |
| `config/DatabaseConnection.java` | Conexión JDBC |

Guía detallada: **[docs/local-setup.md](docs/local-setup.md)**.
