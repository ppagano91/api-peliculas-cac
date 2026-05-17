package com.cac.peliculas.util;

import com.cac.peliculas.dto.MoviePatchRequest;
import com.cac.peliculas.dto.MovieRequest;
import com.cac.peliculas.exception.ValidationException;
import com.cac.peliculas.model.Movie;
import java.math.BigDecimal;

public final class MovieValidator {

    private static final int MIN_YEAR = 1888;
    private static final int MAX_YEAR = 2100;

    private MovieValidator() {}

    public static void validateCreate(MovieRequest request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        requireTitle(request.getTitle());
        validateOptionalFields(
                request.getReleaseYear(),
                request.getDurationMinutes(),
                request.getRating(),
                request.getGenre());
    }

    public static void validateFullUpdate(MovieRequest request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        requireTitle(request.getTitle());
        validateOptionalFields(
                request.getReleaseYear(),
                request.getDurationMinutes(),
                request.getRating(),
                request.getGenre());
    }

    public static void validatePatch(MoviePatchRequest request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        boolean hasField = request.getTitle() != null
                || request.getDirector() != null
                || request.getCast() != null
                || request.getSynopsis() != null
                || request.getReleaseYear() != null
                || request.getGenre() != null
                || request.getDurationMinutes() != null
                || request.getLanguage() != null
                || request.getCountry() != null
                || request.getRating() != null
                || request.getPosterUrl() != null;
        if (!hasField) {
            throw new ValidationException("At least one field must be provided for PATCH");
        }
        if (request.getTitle() != null) {
            requireTitle(request.getTitle());
        }
        validateOptionalFields(
                request.getReleaseYear(),
                request.getDurationMinutes(),
                request.getRating(),
                request.getGenre());
    }

    public static void validateFilterNumericParams(boolean hasInvalidNumericParams) {
        if (hasInvalidNumericParams) {
            throw new ValidationException("Invalid filter parameter: release_year, min_rating or max_rating");
        }
    }

    public static void validateOptionalFieldsOnMovie(Movie movie) {
        validateOptionalFields(
                movie.getReleaseYear(),
                movie.getDurationMinutes(),
                movie.getRating(),
                movie.getGenre());
    }

    private static void requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("title is required");
        }
    }

    private static void validateOptionalFields(Short releaseYear, Integer durationMinutes,
                                             BigDecimal rating, String genre) {
        if (genre != null && genre.isBlank()) {
            throw new ValidationException("genre cannot be empty when provided");
        }
        if (releaseYear != null && (releaseYear < MIN_YEAR || releaseYear > MAX_YEAR)) {
            throw new ValidationException("release_year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }
        if (durationMinutes != null && durationMinutes <= 0) {
            throw new ValidationException("duration_minutes must be greater than 0");
        }
        if (rating != null && (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.TEN) > 0)) {
            throw new ValidationException("rating must be between 0 and 10");
        }
    }

    public static Movie toMovie(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle().trim());
        movie.setDirector(trimOrNull(request.getDirector()));
        movie.setCast(trimOrNull(request.getCast()));
        movie.setSynopsis(trimOrNull(request.getSynopsis()));
        movie.setReleaseYear(request.getReleaseYear());
        movie.setGenre(trimOrNull(request.getGenre()));
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setLanguage(trimOrNull(request.getLanguage()));
        movie.setCountry(trimOrNull(request.getCountry()));
        movie.setRating(request.getRating());
        movie.setPosterUrl(trimOrNull(request.getPosterUrl()));
        return movie;
    }

    public static void applyPatch(Movie existing, MoviePatchRequest patch) {
        if (patch.getTitle() != null) {
            requireTitle(patch.getTitle());
            existing.setTitle(patch.getTitle().trim());
        }
        if (patch.getDirector() != null) {
            existing.setDirector(trimOrNull(patch.getDirector()));
        }
        if (patch.getCast() != null) {
            existing.setCast(trimOrNull(patch.getCast()));
        }
        if (patch.getSynopsis() != null) {
            existing.setSynopsis(trimOrNull(patch.getSynopsis()));
        }
        if (patch.getReleaseYear() != null) {
            existing.setReleaseYear(patch.getReleaseYear());
        }
        if (patch.getGenre() != null) {
            existing.setGenre(trimOrNull(patch.getGenre()));
        }
        if (patch.getDurationMinutes() != null) {
            existing.setDurationMinutes(patch.getDurationMinutes());
        }
        if (patch.getLanguage() != null) {
            existing.setLanguage(trimOrNull(patch.getLanguage()));
        }
        if (patch.getCountry() != null) {
            existing.setCountry(trimOrNull(patch.getCountry()));
        }
        if (patch.getRating() != null) {
            existing.setRating(patch.getRating());
        }
        if (patch.getPosterUrl() != null) {
            existing.setPosterUrl(trimOrNull(patch.getPosterUrl()));
        }
    }

    private static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
