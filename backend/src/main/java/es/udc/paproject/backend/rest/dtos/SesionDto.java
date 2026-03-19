package es.udc.paproject.backend.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SesionDto {

    private Long id;
    private String tituloPelicula;
    private String resumenPelicula;
    private Integer duracionPelicula;
    private String nombreSala;
    private Integer capacidadSala;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHora;

    private BigDecimal precio;
    private Integer localidadesDisponibles;
    private Boolean puedeComprar;

    public SesionDto() {}

    //por si necesita en test
    public SesionDto(Long id, String tituloPelicula, String resumenPelicula,
                     Integer duracionPelicula, String nombreSala, Integer capacidadSala,
                     LocalDateTime fechaHora, BigDecimal precio,
                     Integer localidadesDisponibles, Boolean puedeComprar) {
        this.id = id;
        this.tituloPelicula = tituloPelicula;
        this.resumenPelicula = resumenPelicula;
        this.duracionPelicula = duracionPelicula;
        this.nombreSala = nombreSala;
        this.capacidadSala = capacidadSala;
        this.fechaHora = fechaHora;
        this.precio = precio;
        this.localidadesDisponibles = localidadesDisponibles;
        this.puedeComprar = puedeComprar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public String getResumenPelicula() {
        return resumenPelicula;
    }

    public void setResumenPelicula(String resumenPelicula) {
        this.resumenPelicula = resumenPelicula;
    }

    public Integer getDuracionPelicula() {
        return duracionPelicula;
    }

    public void setDuracionPelicula(Integer duracionPelicula) {
        this.duracionPelicula = duracionPelicula;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public Integer getCapacidadSala() {
        return capacidadSala;
    }

    public void setCapacidadSala(Integer capacidadSala) {
        this.capacidadSala = capacidadSala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getLocalidadesDisponibles() {
        return localidadesDisponibles;
    }

    public void setLocalidadesDisponibles(Integer localidadesDisponibles) {
        this.localidadesDisponibles = localidadesDisponibles;
    }

    public Boolean getPuedeComprar() {
        return puedeComprar;
    }

    public void setPuedeComprar(Boolean puedeComprar) {
        this.puedeComprar = puedeComprar;
    }
}