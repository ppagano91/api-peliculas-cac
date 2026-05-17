package com.cac.peliculas.util;

import com.cac.peliculas.exception.ValidationException;
import javax.servlet.http.HttpServletRequest;

public final class ServletPaths {

    private ServletPaths() {}

    public static Integer parseMovieId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        String idPart = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (idPart.contains("/")) {
            throw new ValidationException("Invalid path: " + pathInfo);
        }
        try {
            return Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid movie id: " + idPart);
        }
    }
}
