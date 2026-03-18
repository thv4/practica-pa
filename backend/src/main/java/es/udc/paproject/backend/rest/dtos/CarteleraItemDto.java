package es.udc.paproject.backend.rest.dtos;

public class CarteleraItemDto {
    private PeliculaDto pelicula;
    private List<SesionDto> sesiones;

    public CarteleraItemDto(PeliculaDto pelicula, List<SesionDto> sesiones) {
        this.pelicula = pelicula;
        this.sesiones = sesiones;
    }

    public List<SesionDto> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionDto> sesiones) {
        this.sesiones = sesiones;
    }

    public PeliculaDto getPelicula() {
        return pelicula;
    }

    public void setPelicula(PeliculaDto pelicula) {
        this.pelicula = pelicula;
    }
}
