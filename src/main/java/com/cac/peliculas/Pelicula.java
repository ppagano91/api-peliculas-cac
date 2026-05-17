package com.cac.peliculas;

import java.math.BigDecimal;

public class Pelicula {

    private int id;
    private String title;
    private String director;
    private String castMembers;
    private String synopsis;
    private short releaseYear;
    private String genre;
    private int durationMinutes;
    private String language;
    private String country;
    private BigDecimal rating;
    private String posterUrl;

    public Pelicula() {}

    public Pelicula(int id, String title, String director, String castMembers, String synopsis,
                    short releaseYear, String genre, int durationMinutes, String language,
                    String country, BigDecimal rating, String posterUrl) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.castMembers = castMembers;
        this.synopsis = synopsis;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.country = country;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(String castMembers) {
        this.castMembers = castMembers;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public short getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(short releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
