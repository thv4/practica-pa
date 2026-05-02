package es.udc.paproject.backend.rest.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

    public class BuyParamsDto {

        @NotNull
        private Long sesionId;

        @Min(1)
        private int numLocalidades;

        @NotNull
        @Size(min = 16, max = 16)
        private String tarjetaBancaria;

        public BuyParamsDto() {}

        public BuyParamsDto(Long sesionId, int numLocalidades, String tarjetaBancaria) {
            this.sesionId = sesionId;
            this.numLocalidades = numLocalidades;
            this.tarjetaBancaria = tarjetaBancaria;
        }

        public Long getSesionId() {
            return sesionId;
        }

        public void setSesionId(Long sesionId) {
            this.sesionId = sesionId;
        }

        public int getNumLocalidades() {
            return numLocalidades;
        }

        public void setNumLocalidades(int numLocalidades) {
            this.numLocalidades = numLocalidades;
        }

        public String getTarjetaBancaria() {
            return tarjetaBancaria;
        }

        public void setTarjetaBancaria(String tarjetaBancaria) {
            this.tarjetaBancaria = tarjetaBancaria;
        }
    }



