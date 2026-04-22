package es.udc.paproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class SesionResumenDto {
    private Long id;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;

    public SesionResumenDto() {}

    public SesionResumenDto(Long id, LocalTime hora) {
        this.id = id;
        this.hora = hora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}
