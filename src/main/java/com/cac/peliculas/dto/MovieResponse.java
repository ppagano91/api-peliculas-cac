package com.cac.peliculas.dto;

import com.cac.peliculas.model.Movie;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class MovieResponse {

    private int id;
    private String title;
    private String director;

    @JsonProperty("cast")
    private String cast;

    private String synopsis;
    private Short releaseYear;
    private String genre;
    private Integer durationMinutes;
    private String language;
    private String country;
    private BigDecimal rating;
    private String posterUrl;

    public static MovieResponse from(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.id = movie.getId();
        response.title = movie.getTitle();
        response.director = movie.getDirector();
        response.cast = movie.getCast();
        response.synopsis = movie.getSynopsis();
        response.releaseYear = movie.getReleaseYear();
        response.genre = movie.getGenre();
        response.durationMinutes = movie.getDurationMinutes();
        response.language = movie.getLanguage();
        response.country = movie.getCountry();
        response.rating = movie.getRating();
        response.posterUrl = movie.getPosterUrl();
        return response;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getCast() {
        return cast;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public Short getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
