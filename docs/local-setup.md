# Cómo levantar la API localmente

Este proyecto es una aplicación **Servlet + JDBC** empaquetada como **WAR**, no una aplicación Spring Boot con `main`. Para ejecutarla necesitás un **servidor de aplicaciones compatible con `javax.servlet`** (recomendado: **Apache Tomcat 9.x**) y una instancia de **MySQL**.

## Requisitos

- **Java 17** (JDK), alineado con `pom.xml` (`maven.compiler.source` / `target`).
- **Apache Maven 3.x** (no hay Maven Wrapper en el repositorio).
- **Apache Tomcat 9.x** (u otro contenedor que exponga la API **Servlet 4.0** con el namespace **`javax.*`**). Tomcat 10+ usa **Jakarta EE** (`jakarta.servlet`) y no coincide con las dependencias actuales del proyecto sin migración.
- **MySQL** accesible donde indique la clase `Conexion` (por defecto `localhost:3306`).

**Docker:** no hay `Dockerfile` ni `docker-compose.yml` en el repo; no es obligatorio usar Docker. Podés instalar Tomcat y MySQL de forma local o contenerizarlos por tu cuenta.

## Configuración

### Base de datos (variables de entorno y `.env`)

La conexión JDBC la resuelve `src/main/java/com/cac/peliculas/ConfiguracionJdbc.java`:

| Variable | Obligatoria | Valor por defecto (si no se define) |
|----------|-------------|-------------------------------------|
| `MOVIES_JDBC_URL` | No | `jdbc:mysql://localhost:3306/movies_cac` |
| `MOVIES_DB_USER` | No | `root` |
| `MOVIES_DB_PASSWORD` | **Sí** (entorno o `.env`) | Ninguno en código |

**Precedencia:** primero **variables de entorno del sistema**; si no están definidas, valores del archivo **`.env`** en el directorio de trabajo del proceso (carga con [dotenv-java](https://github.com/cdimascio/dotenv-java)); para URL y usuario, si tampoco están en `.env`, se usan los defaults de la tabla.

**Plantilla:** copiá `.env.example` a `.env` en la raíz del repositorio y ajustá valores (`.env` no se versiona).

**Tomcat desde consola:** el directorio de trabajo suele ser `CATALINA_HOME\bin`, no la raíz del proyecto, por lo que **`.env` puede no encontrarse**. Opciones:

- Definir `MOVIES_JDBC_URL`, `MOVIES_DB_USER` y `MOVIES_DB_PASSWORD` en `setenv.bat` / `setenv.sh` o en variables de usuario/sistema de Windows.
- O definir **`MOVIES_DOTENV_DIRECTORY`** como variable de sistema (o en el IDE) con la **ruta absoluta** a la carpeta que contiene tu `.env` (por ejemplo la raíz del clon del repo).

**IDE:** al publicar en Tomcat integrado, muchas veces el *working directory* es el del proyecto y el `.env` en la raíz del repo se carga sin pasos extra.

No hay `application.properties` ni perfiles Spring: no es Spring Boot.

### Esquema esperado

No hay migraciones (**Flyway** / **Liquibase**) ni scripts SQL versionados en el repositorio. El código asume una tabla `peliculas` con columnas coherentes con `Controlador` y `Pelicula`, por ejemplo:

- `id_pelicula` (clave autoincremental, usada como clave generada en `INSERT`)
- `titulo`, `genero`, `duracion`, `imagen` (`VARCHAR` o equivalente)

Creá la base `movies_cac` y la tabla en MySQL antes de probar los endpoints (o reutilizá el script que use tu curso/material de Codo a Codo si lo tenés).

### Context path y servlet

- El WAR generado se llama **`api-peliculas.war`** (`finalName` en `pom.xml`), por lo que el **context path** por defecto en Tomcat suele ser **`/api-peliculas`**.
- El servlet está anotado con `@WebServlet("/peliculas")` en `Controlador.java`.

**URL base típica de la API (Tomcat en puerto 8080):**

`http://localhost:8080/api-peliculas/peliculas`

La raíz del contexto sirve `index.jsp` (página simple de bienvenida).

### Variables de entorno

- **`MOVIES_DB_PASSWORD`:** obligatoria vía entorno o `.env` (cadena vacía es válida si MySQL no tiene contraseña para ese usuario).
- **`MOVIES_JDBC_URL`** y **`MOVIES_DB_USER`:** opcionales; si faltan en entorno y en `.env`, se usan los valores por defecto indicados arriba.
- **`MOVIES_DOTENV_DIRECTORY`:** opcional; carpeta donde buscar el archivo `.env` (útil con Tomcat arrancado fuera del directorio del proyecto). Debe definirse en el **sistema** o en el IDE antes de levantar la JVM, no solo dentro del `.env` (ese archivo se busca ya dentro del directorio configurado).

### Perfiles

No aplica: no es Spring Boot; no hay `application.yml` ni perfiles `dev`/`local`/`prod` en el proyecto.

## Comandos de instalación / build

Desde la raíz del repositorio:

```powershell
mvn clean package
```

El artefacto queda en:

`target/api-peliculas.war`

(Omitir tests ya está cubierto si hace falta: `mvn clean package -DskipTests`.)

## Comando para iniciar la API

El repositorio **no** define un plugin Maven para arrancar Tomcat o Jetty embebido. El flujo soportado por los archivos del proyecto es:

1. Generar el WAR con Maven (comando anterior).
2. **Copiar** `target/api-peliculas.war` al directorio `webapps` de una instalación de **Tomcat 9**, o **publicar** el mismo WAR desde tu IDE (Run/Debug en servidor Tomcat).
3. **Iniciar** Tomcat (script `startup` / servicio / botón del IDE).

**Ejemplo de despliegue manual (rutas ilustrativas):**

```powershell
# Ajustá CATALINA_HOME a tu instalación de Tomcat 9
Copy-Item "target\api-peliculas.war" "$env:CATALINA_HOME\webapps\"
```

Tras el despliegue, Tomcat descomprime el WAR y expone el contexto (normalmente `/api-peliculas`).

## Verificación

1. **Página raíz del contexto:** abrir en el navegador  
   `http://localhost:8080/api-peliculas/`  
   Deberías ver el título de `index.jsp` (“API - Películas”).

2. **Listado JSON (GET):**  
   `http://localhost:8080/api-peliculas/peliculas`  
   Respuesta esperada: **200** y un arreglo JSON (posiblemente `[]` si la tabla está vacía).

3. **Alta (POST):** enviar JSON con cuerpo compatible con `Pelicula` (campos usados en el `INSERT`: `titulo`, `genero`, `duracion`, `imagen`) a la misma URL. El código devuelve el id generado y estado **201** en flujo exitoso.

**Health check / Swagger:** no hay endpoint dedicado de health ni documentación OpenAPI/Swagger en el proyecto; la comprobación práctica es **GET** `/api-peliculas/peliculas` y la página de inicio del contexto.

**Puerto:** el predeterminado de Tomcat es **8080**; si lo cambiás en `server.xml`, ajustá las URLs.

## Problemas comunes

| Problema | Qué revisar |
|----------|-------------|
| **Puerto ocupado (8080)** | Otro Tomcat, IDE u app usando el mismo puerto. Cambiá el conector en `conf/server.xml` de Tomcat o detené el proceso que ocupa el puerto. |
| **Versión incorrecta de Java** | Compilar y ejecutar Tomcat con **JDK 17** (o al menos compatible con el bytecode generado para 17). |
| **Error de conexión a MySQL** | Que el servicio MySQL esté levantado, que exista la base indicada en `MOVIES_JDBC_URL`, usuario/contraseña correctos, y que el conector MySQL alcance el host/puerto de la URL. |
| **`IllegalStateException` / “Falta MOVIES_DB_PASSWORD”** | Definir la contraseña en `.env` o en variables de entorno; revisar que Tomcat encuentre el `.env` (directorio de trabajo o `MOVIES_DOTENV_DIRECTORY`). |
| **`.env` ignorado bajo Tomcat** | Usar `setenv` / variables de sistema o `MOVIES_DOTENV_DIRECTORY` apuntando a la carpeta del `.env`. |
| **404 en `/peliculas`** | Context path equivocado (nombre del WAR), Tomcat no desplegó el WAR, o URL sin el prefijo `/api-peliculas`. |
| **`ClassNotFoundException` / errores `javax.servlet`** | Tomcat o contenedor incorrecto para el namespace `javax` (usar Tomcat 9.x para este proyecto tal como está). |
| **Errores SQL al listar o insertar** | Tabla `peliculas` inexistente o columnas que no coinciden con las consultas en `Controlador.java`. |
| **Dependencias Maven** | Conexión a internet para descargar artefactos; en entornos corporativos, proxy o repositorio Nexus/Artifactory. |

## Referencias en el repositorio

| Archivo | Uso |
|---------|-----|
| `pom.xml` | Java 17, empaquetado WAR, dependencias Servlet, Jackson, MySQL. |
| `src/main/java/com/cac/peliculas/Controlador.java` | Mapeo `/peliculas`, GET/POST, CORS en cabeceras. |
| `src/main/java/com/cac/peliculas/ConfiguracionJdbc.java` | Resolución de URL, usuario y contraseña (entorno, `.env`, defaults parciales). |
| `src/main/java/com/cac/peliculas/Conexion.java` | Apertura de conexión JDBC usando `ConfiguracionJdbc`. |
| `.env.example` | Plantilla de variables para copiar a `.env`. |
| `src/main/webapp/WEB-INF/web.xml` | Descriptores mínimos; el servlet se registra por anotación. |

## Dudas que quedan sin resolver solo con el repo

- **Script SQL inicial** exacto del curso (si existe) no está en este repositorio: hay que crearlo o importarlo aparte.
- **Puerto y context path** finales dependen de la instalación de Tomcat y del nombre del WAR; arriba se indica el caso estándar con `api-peliculas.war` y puerto 8080.
