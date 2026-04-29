package es.udc.paproject.backend.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SesionDto {

    private Long id;
    private Long idPelicula;
    private String tituloPelicula;
    private Integer duracionPelicula;
    private String nombreSala;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHora;

    private BigDecimal precio;
    private Integer localidadesDisponibles;

    public SesionDto() {}

    public SesionDto(Long id, Long idPelicula, String tituloPelicula,
                     Integer duracionPelicula, String nombreSala,
                     LocalDateTime fechaHora, BigDecimal precio,
                     Integer localidadesDisponibles) {
        this.id = id;
        this.idPelicula = idPelicula;
        this.tituloPelicula = tituloPelicula;
        this.duracionPelicula = duracionPelicula;
        this.nombreSala = nombreSala;
        this.fechaHora = fechaHora;
        this.precio = precio;
        this.localidadesDisponibles = localidadesDisponibles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPelicula() {return idPelicula;}

    public void setIdPelicula(Long idPelicula) {this.idPelicula = idPelicula;}

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
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
}