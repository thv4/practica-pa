package es.udc.paproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.udc.paproject.backend.model.entities.Sesion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraDto {

    private Long compraId;

    // De la sesión: solo los atributos necesarios para el historial
    private Long sesionId;
    private String tituloPelicula;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHoraSesion;

    private String nombreSala;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaRegistroCompra;

    private int numLocalidades;
    private BigDecimal precioTotal;
    private boolean entregada;

    public CompraDto() {}

    public CompraDto(Long compraId, Long sesionId, String tituloPelicula,
                     LocalDateTime fechaHoraSesion, String nombreSala,
                     LocalDateTime fechaRegistroCompra, int numLocalidades,
                     BigDecimal precioTotal, boolean entregada) {
        this.compraId = compraId;
        this.sesionId = sesionId;
        this.tituloPelicula = tituloPelicula;
        this.fechaHoraSesion = fechaHoraSesion;
        this.nombreSala = nombreSala;
        this.fechaRegistroCompra = fechaRegistroCompra;
        this.numLocalidades = numLocalidades;
        this.precioTotal = precioTotal;
        this.entregada = entregada;
    }

    public Long getCompraId() {
        return compraId;
    }

    public void setCompraId(Long compraId) {
        this.compraId = compraId;
    }

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public LocalDateTime getFechaHoraSesion() {
        return fechaHoraSesion;
    }

    public void setFechaHoraSesion(LocalDateTime fechaHoraSesion) {
        this.fechaHoraSesion = fechaHoraSesion;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
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

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public boolean isEntregada() {
        return entregada;
    }

    public void setEntregada(boolean entregada) {
        this.entregada = entregada;
    }

}
