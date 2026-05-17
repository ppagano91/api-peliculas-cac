package com.cac.peliculas.service;

import com.cac.peliculas.dto.MovieFilter;
import com.cac.peliculas.dto.MoviePatchRequest;
import com.cac.peliculas.dto.MovieRequest;
import com.cac.peliculas.dto.MovieResponse;
import com.cac.peliculas.dto.MovieUpdateRequest;
import com.cac.peliculas.exception.MovieNotFoundException;
import com.cac.peliculas.exception.ValidationException;
import com.cac.peliculas.model.Movie;
import com.cac.peliculas.repository.MovieRepository;
import com.cac.peliculas.util.MovieValidator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {

    private final MovieRepository repository;

    public MovieService() {
        this(new MovieRepository());
    }

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public List<MovieResponse> listMovies(MovieFilter filter) {
        MovieValidator.validateFilterNumericParams(filter.hasInvalidNumericParams());
        return repository.findAll(filter).stream()
                .map(MovieResponse::from)
                .collect(Collectors.toList());
    }

    public MovieResponse getMovie(int id) {
        return repository.findById(id)
                .map(MovieResponse::from)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public MovieResponse createMovie(MovieRequest request) {
        MovieValidator.validateCreate(request);
        Movie movie = MovieValidator.toMovie(request);
        return MovieResponse.from(repository.create(movie));
    }

    public MovieResponse replaceMovie(int id, MovieUpdateRequest request) {
        ensureExists(id);
        MovieValidator.validateFullUpdate(request);
        Movie movie = MovieValidator.toMovie(request);
        if (!repository.update(id, movie)) {
            throw new MovieNotFoundException(id);
        }
        movie.setId(id);
        return MovieResponse.from(movie);
    }

    public MovieResponse patchMovie(int id, MoviePatchRequest request) {
        Movie existing = repository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
        MovieValidator.validatePatch(request);
        MovieValidator.applyPatch(existing, request);
        MovieValidator.validateOptionalFieldsOnMovie(existing);
        if (!repository.update(id, existing)) {
            throw new MovieNotFoundException(id);
        }
        return MovieResponse.from(existing);
    }

    public void deleteMovie(int id) {
        if (!repository.delete(id)) {
            throw new MovieNotFoundException(id);
        }
    }

    private void ensureExists(int id) {
        if (!repository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
    }
}
