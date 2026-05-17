package com.cac.peliculas.util;

import com.cac.peliculas.dto.ErrorResponse;
import com.cac.peliculas.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public final class HttpJson {

    private HttpJson() {}

    public static void writeJson(HttpServletResponse response, ObjectMapper mapper, int status, Object body)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    public static void writeError(HttpServletResponse response, ObjectMapper mapper, ApiException exception)
            throws IOException {
        writeJson(response, mapper, exception.getStatus(),
                new ErrorResponse(exception.getErrorCode(), exception.getMessage()));
    }

    public static void writeError(HttpServletResponse response, ObjectMapper mapper, int status,
                                  String errorCode, String message) throws IOException {
        writeJson(response, mapper, status, new ErrorResponse(errorCode, message));
    }

    public static void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
