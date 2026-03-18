package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Pelicula;

import java.util.List;
import java.util.stream.Collectors;

public class PeliculaConversor {

    private PeliculaConversor() {}

    public final static PeliculaDto toPeliculaDto(Pelicula pelicula) {
        return new PeliculaDto(pelicula.getId(), pelicula.getTitulo(), pelicula.getResumen(), pelicula.getDuracion());
    }

    public final static List<PeliculaDto> toPeliculaDto(List<Pelicula> peliculas) {
        return peliculas.stream().map(c -> toPeliculaDto(c)).collect(Collectors.toList());
    }
}
