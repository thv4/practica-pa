package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Sesion;

import java.util.List;
import java.util.stream.Collectors;

public class SesionResumenConversor {

    private SesionResumenConversor() {}

    public final static SesionResumenDto toSesionResumenDto(Sesion sesion) {

        SesionResumenDto dto = new SesionResumenDto();

        dto.setId(sesion.getId());
        dto.setHora(sesion.getFechaHora().toLocalTime());

        return dto;
    }

    public final static List<SesionResumenDto> toSesionResumenDto(List<Sesion> sesiones) {
        return sesiones.stream()
                .map(SesionResumenConversor::toSesionResumenDto)
                .collect(Collectors.toList());
    }
}
