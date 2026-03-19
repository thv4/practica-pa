package es.udc.paproject.backend.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EntregarEntradasParamsDto {

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long compraId;

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "La tarjeta debe tener 16 dígitos")
    private String tarjetaBancaria;

    public EntregarEntradasParamsDto() {}

    public EntregarEntradasParamsDto(Long compraId, String tarjetaBancaria) {
        this.compraId = compraId;
        this.tarjetaBancaria = tarjetaBancaria;
    }

    public Long getCompraId() {
        return compraId;
    }

    public void setCompraId(Long compraId) {
        this.compraId = compraId;
    }

    public String getTarjetaBancaria() {
        return tarjetaBancaria;
    }

    public void setTarjetaBancaria(String tarjetaBancaria) {
        this.tarjetaBancaria = tarjetaBancaria;
    }
}