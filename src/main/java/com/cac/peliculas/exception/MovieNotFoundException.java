package com.cac.peliculas.exception;

import javax.servlet.http.HttpServletResponse;

public class MovieNotFoundException extends ApiException {

    public MovieNotFoundException(int id) {
        super(HttpServletResponse.SC_NOT_FOUND, "not_found", "Movie not found with id: " + id);
    }
}
