package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Pelicula;

import java.util.List;
import java.util.stream.Collectors;

public class PeliculaResumenConversor {

    private PeliculaResumenConversor() {}

    public final static PeliculaResumenDto toPeliculaResumenDto(Pelicula pelicula) {
        return new PeliculaResumenDto(pelicula.getId(), pelicula.getTitulo());
    }

    public final static List<PeliculaResumenDto> toPeliculaResumenDto(List<Pelicula> peliculas) {
        return peliculas.stream().map(c -> toPeliculaResumenDto(c)).collect(Collectors.toList());
    }
}
