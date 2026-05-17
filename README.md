# api-peliculas



API de películas en Java: aplicación **WAR** con **Servlets** (`javax.servlet`), **Jackson** para JSON y **MySQL** mediante JDBC.



## Levantar API localmente



1. **Requisitos:** JDK **17**, **Maven 3.x**, **Apache Tomcat 9.x** (compatible con `javax.servlet`), **MySQL** en ejecución.

2. **Variables de entorno / `.env`:** la conexión a MySQL se configura con **`MOVIES_JDBC_URL`**, **`MOVIES_DB_USER`** y **`MOVIES_DB_PASSWORD`** (obligatoria: entorno o `.env`). Valores por defecto solo para URL y usuario si no los definís. Copiá **[`.env.example`](.env.example)** a **`.env`** en la raíz del repo y completá al menos la contraseña. El archivo **`.env`** está en `.gitignore` y no debe subirse al repositorio.

3. **Tomcat y el archivo `.env`:** al arrancar Tomcat desde la terminal, el directorio de trabajo suele ser `bin` del Tomcat, no la raíz del proyecto. Si el `.env` no se carga, definí las mismas variables en el sistema, en `setenv.bat` / `setenv.sh`, o en la configuración del servidor en tu IDE; opcionalmente podés fijar **`MOVIES_DOTENV_DIRECTORY`** apuntando a la carpeta donde está tu `.env` (definila como variable de **sistema** o en el IDE, no hace falta repetirla dentro del `.env`).

4. **Dependencias de infraestructura:** creá la base y la tabla `peliculas` en MySQL (no hay migraciones en el repo). Detalle y URLs en la guía enlazada abajo.

5. **Build y despliegue:** `mvn clean package` → copiá o publicá `target/api-peliculas.war` en Tomcat e iniciá el servidor.

6. **Comprobar que funciona:** con Tomcat en el puerto por defecto **8080**, abrí  

   `http://localhost:8080/api-peliculas/peliculas` (GET devuelve JSON) y  

   `http://localhost:8080/api-peliculas/` (página de bienvenida).



Documentación detallada: **[docs/local-setup.md](docs/local-setup.md)**.


