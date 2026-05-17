package com.cac.peliculas.repository;

import com.cac.peliculas.config.DatabaseConnection;
import com.cac.peliculas.dto.MovieFilter;
import com.cac.peliculas.exception.DatabaseException;
import com.cac.peliculas.model.Movie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieRepository {

    private static final String BASE_SELECT = "SELECT id, title, director, cast_members, synopsis, "
            + "release_year, genre, duration_minutes, language, country, rating, poster_url FROM movies";

    public List<Movie> findAll(MovieFilter filter) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter.getTitle() != null) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
            params.add("%" + filter.getTitle() + "%");
        }
        if (filter.getGenre() != null) {
            sql.append(" AND genre = ?");
            params.add(filter.getGenre());
        }
        if (filter.getDirector() != null) {
            sql.append(" AND director = ?");
            params.add(filter.getDirector());
        }
        if (filter.getReleaseYear() != null) {
            sql.append(" AND release_year = ?");
            params.add(filter.getReleaseYear());
        }
        if (filter.getCountry() != null) {
            sql.append(" AND country = ?");
            params.add(filter.getCountry());
        }
        if (filter.getLanguage() != null) {
            sql.append(" AND language = ?");
            params.add(filter.getLanguage());
        }
        if (filter.getMinRating() != null) {
            sql.append(" AND rating >= ?");
            params.add(filter.getMinRating());
        }
        if (filter.getMaxRating() != null) {
            sql.append(" AND rating <= ?");
            params.add(filter.getMaxRating());
        }

        sql.append(" ORDER BY id");

        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql.toString())) {
            bindParams(statement, params);
            try (ResultSet rs = statement.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (rs.next()) {
                    movies.add(mapRow(rs));
                }
                return movies;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list movies: " + e.getMessage());
        }
    }

    public Optional<Movie> findById(int id) {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find movie: " + e.getMessage());
        }
    }

    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check movie existence: " + e.getMessage());
        }
    }

    public Movie create(Movie movie) {
        String sql = "INSERT INTO movies (title, director, cast_members, synopsis, release_year, genre, "
                + "duration_minutes, language, country, rating, poster_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindMovie(statement, movie);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    movie.setId(keys.getInt(1));
                }
            }
            return movie;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create movie: " + e.getMessage());
        }
    }

    public boolean update(int id, Movie movie) {
        String sql = "UPDATE movies SET title = ?, director = ?, cast_members = ?, synopsis = ?, "
                + "release_year = ?, genre = ?, duration_minutes = ?, language = ?, country = ?, "
                + "rating = ?, poster_url = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            bindMovie(statement, movie);
            statement.setInt(12, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update movie: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete movie: " + e.getMessage());
        }
    }

    private static void bindMovie(PreparedStatement statement, Movie movie) throws SQLException {
        statement.setString(1, movie.getTitle());
        statement.setString(2, movie.getDirector());
        statement.setString(3, movie.getCast());
        statement.setString(4, movie.getSynopsis());
        setShort(statement, 5, movie.getReleaseYear());
        statement.setString(6, movie.getGenre());
        setInteger(statement, 7, movie.getDurationMinutes());
        statement.setString(8, movie.getLanguage());
        statement.setString(9, movie.getCountry());
        if (movie.getRating() != null) {
            statement.setBigDecimal(10, movie.getRating());
        } else {
            statement.setNull(10, Types.DECIMAL);
        }
        statement.setString(11, movie.getPosterUrl());
    }

    private static void bindParams(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            int index = i + 1;
            if (param instanceof String) {
                statement.setString(index, (String) param);
            } else if (param instanceof Short) {
                statement.setShort(index, (Short) param);
            } else if (param instanceof java.math.BigDecimal) {
                statement.setBigDecimal(index, (java.math.BigDecimal) param);
            } else {
                statement.setObject(index, param);
            }
        }
    }

    private static void setShort(PreparedStatement statement, int index, Short value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.SMALLINT);
        } else {
            statement.setShort(index, value);
        }
    }

    private static void setInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private static Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("director"),
                rs.getString("cast_members"),
                rs.getString("synopsis"),
                getShort(rs, "release_year"),
                rs.getString("genre"),
                getInteger(rs, "duration_minutes"),
                rs.getString("language"),
                rs.getString("country"),
                rs.getBigDecimal("rating"),
                rs.getString("poster_url"));
    }

    private static Short getShort(ResultSet rs, String column) throws SQLException {
        short value = rs.getShort(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
