package com.cac.peliculas.dto;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

public class MovieFilter {

    private String title;
    private String genre;
    private String director;
    private Short releaseYear;
    private String country;
    private String language;
    private BigDecimal minRating;
    private BigDecimal maxRating;
    private boolean invalidNumericParams;

    public static MovieFilter fromRequest(HttpServletRequest request) {
        MovieFilter filter = new MovieFilter();
        filter.title = trimToNull(request.getParameter("title"));
        filter.genre = trimToNull(request.getParameter("genre"));
        filter.director = trimToNull(request.getParameter("director"));
        filter.country = trimToNull(request.getParameter("country"));
        filter.language = trimToNull(request.getParameter("language"));

        String yearParam = trimToNull(request.getParameter("release_year"));
        if (yearParam != null) {
            try {
                filter.releaseYear = Short.parseShort(yearParam);
            } catch (NumberFormatException e) {
                filter.invalidNumericParams = true;
            }
        }

        String minRatingParam = trimToNull(request.getParameter("min_rating"));
        if (minRatingParam != null) {
            try {
                filter.minRating = new BigDecimal(minRatingParam);
            } catch (NumberFormatException e) {
                filter.invalidNumericParams = true;
            }
        }

        String maxRatingParam = trimToNull(request.getParameter("max_rating"));
        if (maxRatingParam != null) {
            try {
                filter.maxRating = new BigDecimal(maxRatingParam);
            } catch (NumberFormatException e) {
                filter.invalidNumericParams = true;
            }
        }

        return filter;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public Short getReleaseYear() {
        return releaseYear;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public BigDecimal getMinRating() {
        return minRating;
    }

    public BigDecimal getMaxRating() {
        return maxRating;
    }

    public boolean hasInvalidNumericParams() {
        return invalidNumericParams;
    }
}
