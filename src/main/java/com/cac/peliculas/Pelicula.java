package com.cac.peliculas;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String genero;
    private String duracion;
    private String imagen;

    // Constructor con todos los atributos
    public Pelicula(int idPelicula, String titulo, String genero, String duracion, String imagen) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.duracion = duracion;
        this.imagen = imagen;
    }

    // Constructor por defecto
    public Pelicula() {}

    public int getIdPelicula() {
        return idPelicula;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
            return genero;
    }

    public String getDuracion() {
            return duracion;
    }

    public String getImagen() {
            return imagen;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "idPelicula=" + idPelicula +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", duracion='" + duracion + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
