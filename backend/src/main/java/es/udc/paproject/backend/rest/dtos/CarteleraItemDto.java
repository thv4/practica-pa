package es.udc.paproject.backend.rest.dtos;

import java.util.List;

public class CarteleraItemDto {
    private PeliculaResumenDto pelicula;
    private List<SesionResumenDto> sesiones;

    public CarteleraItemDto(PeliculaResumenDto pelicula, List<SesionResumenDto> sesiones) {
        this.pelicula = pelicula;
        this.sesiones = sesiones;
    }

    public List<SesionResumenDto> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionResumenDto> sesiones) {
        this.sesiones = sesiones;
    }

    public PeliculaResumenDto getPelicula() {
        return pelicula;
    }

    public void setPelicula(PeliculaResumenDto pelicula) {
        this.pelicula = pelicula;
    }
}
