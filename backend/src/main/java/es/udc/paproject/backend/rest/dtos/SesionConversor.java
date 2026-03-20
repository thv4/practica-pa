package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Sesion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class SesionConversor {

    private SesionConversor() {}

    public final static SesionDto toSesionDto(Sesion sesion) {

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

        return dto;
    }

    public final static List<SesionDto> toSesionDto(List<Sesion> sesiones) {
        return sesiones.stream()
                .map(SesionConversor::toSesionDto)
                .collect(Collectors.toList());
    }
}