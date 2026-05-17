# Cómo levantar la API localmente

Aplicación **Servlet + JDBC** empaquetada como **WAR**. Se despliega en **Apache Tomcat 9.x** (`javax.servlet`). **No es Spring Boot.**

Context path por defecto del artefacto: **`/api-peliculas`** (nombre del WAR).

## Requisitos

- **Java 17**
- **Maven 3.x**
- **Tomcat 9.x** (no Tomcat 10+ sin migrar a Jakarta EE)
- **MySQL**

## Configuración JDBC

`com.cac.peliculas.config.ConfiguracionJdbc` lee:

| Variable | Obligatoria | Default |
|----------|-------------|---------|
| `MOVIES_JDBC_URL` | No | `jdbc:mysql://localhost:3306/movies_cac` |
| `MOVIES_DB_USER` | No | `root` |
| `MOVIES_DB_PASSWORD` | **Sí** | — |
| `MOVIES_DOTENV_DIRECTORY` | No | directorio de trabajo del proceso |

1. Copiá `.env.example` a `.env` en la raíz del repo y completá `MOVIES_DB_PASSWORD`.

2. Si Tomcat **no** usa la raíz del repo como directorio de trabajo, elegí una de estas opciones:

**Opción A — ruta al `.env`:**

```bat
set MOVIES_DOTENV_DIRECTORY=C:\Proyectos\Codo A Codo\Java\api-peliculas
```

**Opción B — variables en Tomcat** (`%CATALINA_HOME%\bin\setenv.bat` en Windows):

```bat
set "MOVIES_JDBC_URL=jdbc:mysql://localhost:3306/movies_cac"
set "MOVIES_DB_USER=root"
set "MOVIES_DB_PASSWORD=tu_password"
```

En Linux/macOS, el equivalente es `bin/setenv.sh` con `export MOVIES_DB_PASSWORD=...`.

## Base de datos

```powershell
Get-Content sql\init.sql | mysql -u root -p
```

Crea `movies_cac`, la tabla `movies` y tres películas de ejemplo. El repositorio JDBC espera exactamente ese esquema (`sql/init.sql`).

## Build y despliegue

```powershell
mvn clean package
Copy-Item "target\api-peliculas.war" "$env:CATALINA_HOME\webapps\"
```

Iniciá Tomcat y esperá a que despliegue el WAR. Revisá `logs/catalina.out` si falla el arranque por `MOVIES_DB_PASSWORD`.

## Endpoints a verificar

Base: `http://localhost:8080/api-peliculas`

| Método | Ruta | Resultado esperado |
|--------|------|--------------------|
| GET | `/` | Página de bienvenida (JSP) |
| GET | `/movies` | **200**, array JSON |
| GET | `/movies/3` | **200**, *Inception* (si corriste `init.sql`) |
| GET | `/movies?director=Christopher%20Nolan` | **200**, al menos una película |
| GET | `/movies?genre=Sci-Fi&min_rating=8` | **200**, filtrado |
| GET | `/swagger-ui/` | Swagger UI |
| GET | `/openapi/openapi.yaml` | Especificación OpenAPI |

## Probar CRUD

### curl (Git Bash o curl en PATH)

```powershell
curl http://localhost:8080/api-peliculas/movies

curl http://localhost:8080/api-peliculas/movies/1

curl -X POST http://localhost:8080/api-peliculas/movies ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Test\",\"genre\":\"Drama\",\"releaseYear\":2020,\"durationMinutes\":90}"

curl -X PUT http://localhost:8080/api-peliculas/movies/1 ^
  -H "Content-Type: application/json" ^
  -d @movie.json

curl -X PATCH http://localhost:8080/api-peliculas/movies/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"rating\":9.5}"

curl -X DELETE http://localhost:8080/api-peliculas/movies/99
```

`DELETE` exitoso responde **204** sin cuerpo. `POST` exitoso responde **201** con la película creada.

### PowerShell (`Invoke-RestMethod`)

```powershell
$base = "http://localhost:8080/api-peliculas"

Invoke-RestMethod "$base/movies"

Invoke-RestMethod "$base/movies" -Method Post -ContentType "application/json" -Body (@{
  title = "Test"
  genre = "Drama"
  releaseYear = 2020
  durationMinutes = 90
} | ConvertTo-Json)

Invoke-RestMethod "$base/movies/1" -Method Patch -ContentType "application/json" -Body '{"rating":9.5}'
```

## Capas (dónde mirar si algo falla)

```
HTTP  →  servlet/MovieServlet.java
         service/MovieService.java
         repository/MovieRepository.java  →  MySQL (tabla movies)
```

Validación y filtros: `util/MovieValidator.java`, `dto/MovieFilter.java`.

Errores JSON: `dto/ErrorResponse.java` (`error` + `message`).

## Problemas comunes

| Problema | Revisar |
|----------|---------|
| `IllegalStateException: Falta MOVIES_DB_PASSWORD` | `.env`, `MOVIES_DOTENV_DIRECTORY` o `setenv` |
| 404 en `/movies` | Context path: la URL debe incluir `/api-peliculas` |
| 404 en `/movies/1` con BD vacía | Ejecutar `sql/init.sql` |
| 400 en filtros | `release_year`, `min_rating`, `max_rating` deben ser numéricos |
| 500 / `database_error` | MySQL en ejecución, credenciales, tabla `movies` |
| `javax.servlet` no encontrado al compilar | Dependencia `provided`; desplegar en Tomcat 9 |
| CORS desde un front en otro puerto | El servlet ya envía cabeceras CORS; usar `OPTIONS` si el navegador lo pide |

## Archivos clave

| Archivo | Rol |
|---------|-----|
| `servlet/MovieServlet.java` | `@WebServlet("/movies", "/movies/*")`, CRUD + CORS |
| `service/MovieService.java` | Lógica de negocio |
| `repository/MovieRepository.java` | JDBC contra `movies` |
| `config/ConfiguracionJdbc.java` | Variables de entorno y `.env` |
| `sql/init.sql` | Esquema y datos de prueba |

Documentación general del API: [README.md](../README.md).
