package com.cac.peliculas.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Resuelve URL, usuario y contraseña JDBC.
 * Precedencia: variables de entorno del sistema, luego archivo {@code .env}, luego valores por defecto (solo URL y usuario).
 */
public final class ConfiguracionJdbc {

    private static final Dotenv DOTENV = initDotenv();

    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/movies_cac";
    private static final String DEFAULT_USER = "root";

    private ConfiguracionJdbc() {}

    private static Dotenv initDotenv() {
        var builder = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing();
        String dir = System.getenv("MOVIES_DOTENV_DIRECTORY");
        if (dir != null && !dir.isBlank()) {
            builder.directory(dir.trim());
        }
        return builder.load();
    }

    public static String getJdbcUrl() {
        return withUtf8Params(firstNonBlank(
                System.getenv("MOVIES_JDBC_URL"), DOTENV.get("MOVIES_JDBC_URL"), DEFAULT_JDBC_URL));
    }

    private static String withUtf8Params(String jdbcUrl) {
        if (jdbcUrl.contains("characterEncoding=")) {
            return jdbcUrl;
        }
        String separator = jdbcUrl.contains("?") ? "&" : "?";
        return jdbcUrl + separator + "characterEncoding=UTF-8&useUnicode=true";
    }

    public static String getUser() {
        return firstNonBlank(System.getenv("MOVIES_DB_USER"), DOTENV.get("MOVIES_DB_USER"), DEFAULT_USER);
    }

    public static String getPassword() {
        if (System.getenv("MOVIES_DB_PASSWORD") != null) {
            return System.getenv("MOVIES_DB_PASSWORD");
        }
        if (DOTENV.get("MOVIES_DB_PASSWORD") != null) {
            return DOTENV.get("MOVIES_DB_PASSWORD");
        }
        throw new IllegalStateException(
                "Falta MOVIES_DB_PASSWORD: definila en variables de entorno o en .env (plantilla: .env.example).");
    }

    private static String firstNonBlank(String env, String fromDotenv, String fallback) {
        if (env != null && !env.isBlank()) {
            return env;
        }
        if (fromDotenv != null && !fromDotenv.isBlank()) {
            return fromDotenv;
        }
        return fallback;
    }
}
