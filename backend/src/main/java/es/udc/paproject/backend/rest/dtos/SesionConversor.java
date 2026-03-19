package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Sesion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class SesionConversor {

    private SesionConversor() {}

    public final static SesionDto toSesionDto(Sesion sesion, Boolean usuarioAutenticado) {

        SesionDto dto = new SesionDto();

        dto.setId(sesion.getId());
        dto.setFechaHora(sesion.getFechaHora());
        dto.setPrecio(sesion.getPrecio());
        dto.setLocalidadesDisponibles(sesion.getLocalidadesLibres());

        dto.setTituloPelicula(sesion.getPelicula().getTitulo());
        dto.setResumenPelicula(sesion.getPelicula().getResumen());
        dto.setDuracionPelicula(sesion.getPelicula().getDuracion());

        dto.setNombreSala(sesion.getSala().getNombre());
        dto.setCapacidadSala(sesion.getSala().getCapacidad());

        boolean puedeComprar = usuarioAutenticado &&
                !sesion.haComenzado() &&
                sesion.getLocalidadesLibres() > 0;
        dto.setPuedeComprar(puedeComprar);

        return dto;
    }

    public final static List<SesionDto> toSesionDto(List<Sesion> sesiones, Boolean usuarioAutenticado) {
        return sesiones.stream()
                .map(s -> toSesionDto(s, usuarioAutenticado))
                .collect(Collectors.toList());
    }

    // Versión sin autenticación
    public final static List<SesionDto> toSesionDto(List<Sesion> sesiones) {
        return sesiones.stream()
                .map(s -> toSesionDto(s, false))
                .collect(Collectors.toList());
    }
}