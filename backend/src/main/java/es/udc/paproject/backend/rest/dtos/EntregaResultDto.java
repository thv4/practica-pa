package es.udc.paproject.backend.rest.dtos;

public class EntregaResultDto {

    private String mensaje;
    private boolean success;

    public EntregaResultDto() {}

    public EntregaResultDto(String mensaje, boolean success) {
        this.mensaje = mensaje;
        this.success = success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}