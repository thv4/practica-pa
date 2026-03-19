package es.udc.paproject.backend.rest.dtos;

public class EntregaConversor {

    private EntregaConversor() {}

    public final static EntregaResultDto toSuccessResult() {
        return new EntregaResultDto("Entregas realizadas correctamente", true);
    }

    public final static EntregaResultDto toSuccessResult(String mensaje) {
        return new EntregaResultDto(mensaje, true);
    }

    public final static EntregaResultDto toErrorResult(String mensaje) {
        return new EntregaResultDto(mensaje, false);
    }
}