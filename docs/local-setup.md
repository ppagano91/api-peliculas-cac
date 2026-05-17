# Cómo levantar la API localmente

Aplicación **Servlet + JDBC** empaquetada como **WAR**. Se despliega en **Apache Tomcat 9.x** (namespace `javax.servlet`). No es Spring Boot.

## Requisitos

- **Java 17**
- **Maven 3.x**
- **Tomcat 9.x**
- **MySQL**

## Configuración

`ConfiguracionJdbc.java` lee:

| Variable | Obligatoria | Default |
|----------|-------------|---------|
| `MOVIES_JDBC_URL` | No | `jdbc:mysql://localhost:3306/movies_cac` |
| `MOVIES_DB_USER` | No | `root` |
| `MOVIES_DB_PASSWORD` | **Sí** | — |
| `MOVIES_DOTENV_DIRECTORY` | No | — |

Copiá `.env.example` a `.env`. Si Tomcat arranca fuera de la raíz del repo, definí las variables en `setenv.bat` / `setenv.sh` o usá `MOVIES_DOTENV_DIRECTORY`.

## Base de datos

```powershell
Get-Content sql\init.sql | mysql -u root -p
```

El servlet espera la tabla `peliculas` con las columnas definidas en `sql/init.sql`.

## Build y despliegue

```powershell
mvn clean package
Copy-Item "target\api-peliculas.war" "$env:CATALINA_HOME\webapps\"
```

Iniciá Tomcat. Context path habitual: **`/api-peliculas`**.

## Verificación

| Prueba | URL |
|--------|-----|
| Inicio | http://localhost:8080/api-peliculas/ |
| GET listado | http://localhost:8080/api-peliculas/peliculas |

## Problemas comunes

| Problema | Revisar |
|----------|---------|
| Falta `MOVIES_DB_PASSWORD` | `.env` o variables de entorno |
| `.env` no cargado en Tomcat | `MOVIES_DOTENV_DIRECTORY` o `setenv` |
| 404 en `/peliculas` | Context path (`/api-peliculas`) |
| Error SQL | Ejecutar `sql/init.sql`; columnas deben coincidir con `Controlador.java` |
| `javax.servlet` no encontrado | Usar Tomcat 9, no Tomcat 10+ sin migrar a Jakarta |

## Archivos clave

- `Controlador.java` — `@WebServlet("/peliculas")`, GET y POST
- `Conexion.java` — JDBC
- `Pelicula.java` — modelo JSON
- `sql/init.sql` — esquema MySQL
