package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Sala;

import java.util.List;
import java.util.stream.Collectors;

public class SalaConversor {

    private SalaConversor() {}

    public final static SalaDto toSalaDto(Sala sala) {
        return new SalaDto(sala.getId(), sala.getNombre(), sala.getCapacidad());
    }

    public final static List<SalaDto> toSalaDto(List<Sala> salas) {
        return salas.stream().map(c -> toSalaDto(c)).collect(Collectors.toList());
    }
}
