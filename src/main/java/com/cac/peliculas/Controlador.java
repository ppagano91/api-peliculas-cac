package com.cac.peliculas;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/peliculas")
public class Controlador extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection()) {
            String query = "SELECT id, title, director, cast_members, synopsis, release_year, "
                    + "genre, duration_minutes, language, country, rating, poster_url FROM peliculas";

            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                List<Pelicula> peliculas = new ArrayList<>();
                while (resultSet.next()) {
                    peliculas.add(mapRow(resultSet));
                }

                response.setContentType("application/json");
                response.getWriter().write(mapper.writeValueAsString(peliculas));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            conexion.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection()) {
            Pelicula pelicula = mapper.readValue(request.getInputStream(), Pelicula.class);

            String query = "INSERT INTO peliculas (title, director, cast_members, synopsis, "
                    + "release_year, genre, duration_minutes, language, country, rating, poster_url) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, pelicula.getTitle());
                statement.setString(2, pelicula.getDirector());
                statement.setString(3, pelicula.getCastMembers());
                statement.setString(4, pelicula.getSynopsis());
                statement.setShort(5, pelicula.getReleaseYear());
                statement.setString(6, pelicula.getGenre());
                statement.setInt(7, pelicula.getDurationMinutes());
                statement.setString(8, pelicula.getLanguage());
                statement.setString(9, pelicula.getCountry());
                statement.setBigDecimal(10, pelicula.getRating());
                statement.setString(11, pelicula.getPosterUrl());

                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        response.setContentType("application/json");
                        response.getWriter().write(mapper.writeValueAsString(keys.getLong(1)));
                    }
                }
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            conexion.close();
        }
    }

    private static Pelicula mapRow(ResultSet resultSet) throws SQLException {
        return new Pelicula(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("director"),
                resultSet.getString("cast_members"),
                resultSet.getString("synopsis"),
                resultSet.getShort("release_year"),
                resultSet.getString("genre"),
                resultSet.getInt("duration_minutes"),
                resultSet.getString("language"),
                resultSet.getString("country"),
                resultSet.getBigDecimal("rating"),
                resultSet.getString("poster_url"));
    }

    private static void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
