package com.cac.peliculas.exception;

import javax.servlet.http.HttpServletResponse;

public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, "validation_error", message);
    }
}
