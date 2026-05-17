package com.cac.peliculas.servlet;

import com.cac.peliculas.dto.MovieFilter;
import com.cac.peliculas.dto.MoviePatchRequest;
import com.cac.peliculas.dto.MovieRequest;
import com.cac.peliculas.dto.MovieUpdateRequest;
import com.cac.peliculas.exception.ApiException;
import com.cac.peliculas.service.MovieService;
import com.cac.peliculas.util.HttpJson;
import com.cac.peliculas.util.ServletPaths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MovieServlet", urlPatterns = {"/movies", "/movies/*"})
public class MovieServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MovieService movieService = new MovieService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpJson.setCorsHeaders(response);
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            try {
                doPatch(request, response);
            } catch (ApiException e) {
                HttpJson.writeError(response, mapper, e);
            } catch (Exception e) {
                e.printStackTrace();
                HttpJson.writeError(response, mapper, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "internal_error", "Unexpected server error");
            }
            return;
        }
        try {
            super.service(request, response);
        } catch (ApiException e) {
            HttpJson.writeError(response, mapper, e);
        } catch (Exception e) {
            e.printStackTrace();
            HttpJson.writeError(response, mapper, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "internal_error", "Unexpected server error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = ServletPaths.parseMovieId(request);
        if (id == null) {
            MovieFilter filter = MovieFilter.fromRequest(request);
            HttpJson.writeJson(response, mapper, HttpServletResponse.SC_OK,
                    movieService.listMovies(filter));
        } else {
            HttpJson.writeJson(response, mapper, HttpServletResponse.SC_OK, movieService.getMovie(id));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletPaths.parseMovieId(request) != null) {
            HttpJson.writeError(response, mapper, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "method_not_allowed", "POST is only allowed on /movies");
            return;
        }
        MovieRequest body = mapper.readValue(request.getInputStream(), MovieRequest.class);
        HttpJson.writeJson(response, mapper, HttpServletResponse.SC_CREATED, movieService.createMovie(body));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = requireId(request, response);
        if (id == null) {
            return;
        }
        MovieUpdateRequest body = mapper.readValue(request.getInputStream(), MovieUpdateRequest.class);
        HttpJson.writeJson(response, mapper, HttpServletResponse.SC_OK, movieService.replaceMovie(id, body));
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = requireId(request, response);
        if (id == null) {
            return;
        }
        MoviePatchRequest body = mapper.readValue(request.getInputStream(), MoviePatchRequest.class);
        HttpJson.writeJson(response, mapper, HttpServletResponse.SC_OK, movieService.patchMovie(id, body));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = requireId(request, response);
        if (id == null) {
            return;
        }
        movieService.deleteMovie(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private Integer requireId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = ServletPaths.parseMovieId(request);
        if (id == null) {
            HttpJson.writeError(response, mapper, HttpServletResponse.SC_BAD_REQUEST,
                    "validation_error", "Movie id is required in the path");
        }
        return id;
    }
}
