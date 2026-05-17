# api-peliculas

API de películas en Java: aplicación **WAR** con **Servlets** (`javax.servlet`), **Jackson** para JSON y **MySQL** mediante JDBC.

No usa Spring Boot: el código es Java base con un servlet que expone los endpoints.

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

La conexión la resuelve `ConfiguracionJdbc.java` (variables de sistema → `.env` → defaults parciales).

## Crear la base de datos

```powershell
Get-Content sql\init.sql | mysql -u root -p
```

Crea la base `movies_cac`, la tabla `peliculas` y tres películas de ejemplo.

### Tabla `peliculas`

| Columna | Tipo | Notas |
|---------|------|--------|
| `id` | INT, PK, autoincrement | |
| `title` | VARCHAR(255), NOT NULL | |
| `director` | VARCHAR(255), NOT NULL | |
| `cast_members` | TEXT | Reparto (en JSON: `castMembers`) |
| `synopsis` | TEXT | |
| `release_year` | SMALLINT, NOT NULL | En JSON: `releaseYear` |
| `genre` | VARCHAR(100), NOT NULL | |
| `duration_minutes` | INT, NOT NULL | En JSON: `durationMinutes` |
| `language` | VARCHAR(50) | |
| `country` | VARCHAR(100) | |
| `rating` | DECIMAL(3,1) | 0–10 |
| `poster_url` | VARCHAR(500) | En JSON: `posterUrl` |

## Compilar

Desde la raíz del proyecto:

```powershell
mvn clean package
```

Genera `target/api-peliculas.war`.

## Ejecutar

1. Copiá el WAR a Tomcat: `webapps/api-peliculas.war`
2. Iniciá Tomcat (puerto **8080** por defecto)
3. Asegurate de que Tomcat encuentre el `.env` (ver [docs/local-setup.md](docs/local-setup.md))

**URLs (context path `/api-peliculas`):**

| Recurso | URL |
|---------|-----|
| Página de bienvenida | http://localhost:8080/api-peliculas/ |
| Listar películas (GET) | http://localhost:8080/api-peliculas/peliculas |
| Alta (POST) | http://localhost:8080/api-peliculas/peliculas |

### Ejemplo JSON para POST

```json
{
  "title": "Matrix",
  "director": "Lana Wachowski, Lilly Wachowski",
  "castMembers": "Keanu Reeves, Laurence Fishburne",
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

Respuesta exitosa de POST: **201** y el `id` numérico generado en JSON.

## Archivos principales

| Archivo | Rol |
|---------|-----|
| `pom.xml` | Dependencias Maven y empaquetado WAR |
| `sql/init.sql` | Esquema y datos iniciales |
| `ConfiguracionJdbc.java` | URL, usuario y contraseña |
| `Conexion.java` | Conexión JDBC a MySQL |
| `Pelicula.java` | Modelo Java (mapeo JSON con Jackson) |
| `Controlador.java` | Servlet `/peliculas` (GET y POST) |
| `src/main/webapp/index.jsp` | Página de inicio del WAR |

Guía detallada: **[docs/local-setup.md](docs/local-setup.md)**.
