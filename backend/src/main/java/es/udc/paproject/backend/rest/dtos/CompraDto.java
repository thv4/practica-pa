package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Sesion;

import java.time.LocalDateTime;

public class CompraDto {

    private Long compraId;
    private UserDto user;
    private SesionDto sesion;

    private LocalDateTime fechaRegistroCompra;
    private int numLocalidades;
    private String tarjetaBancaria;
    private boolean entregada;

    public CompraDto(){}

    public CompraDto(Long compraId, UserDto user, SesionDto sesion, LocalDateTime fechaRegistroCompra, int numLocalidades, String tarjetaBancaria, boolean entregada){
        this.compraId = compraId;
        this.user = user;
        this.sesion = sesion;
        this.fechaRegistroCompra = fechaRegistroCompra;
        this.numLocalidades = numLocalidades;
        this.tarjetaBancaria = tarjetaBancaria;
        this.entregada = entregada;
    }

    public Long getCompraId() {
        return compraId;
    }

    public void setCompraId(Long compraId) {
        this.compraId = compraId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public SesionDto getSesion() {
        return sesion;
    }

    public void setSesion(SesionDto sesion) {
        this.sesion = sesion;
    }

    public LocalDateTime getFechaRegistroCompra() {
        return fechaRegistroCompra;
    }

    public void setFechaRegistroCompra(LocalDateTime fechaRegistroCompra) {
        this.fechaRegistroCompra = fechaRegistroCompra;
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

    public boolean isEntregada() {
        return entregada;
    }

    public void setEntregada(boolean entregada) {
        this.entregada = entregada;
    }
}
