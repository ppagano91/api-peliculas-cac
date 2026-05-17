package com.cac.peliculas.exception;

import javax.servlet.http.HttpServletResponse;

public class DatabaseException extends ApiException {

    public DatabaseException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database_error", message);
    }
}
