package es.udc.paproject.backend.model.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import es.udc.paproject.backend.model.entities.Sala;

@Entity
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Sala sala;

    @ManyToOne
    private Pelicula pelicula;

    private LocalDateTime fechaHora;
    private BigDecimal precio;

    private Integer localidadesLibres;

    private Long version;

    /* Constructor vacío obligatorio para JPA */
    public Sesion() {
    }

    public Sesion(Sala sala, Pelicula pelicula, LocalDateTime fechaHora, BigDecimal precio, Integer localidadesLibres) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.fechaHora = fechaHora;
        this.precio = precio;
        this.localidadesLibres = localidadesLibres;
    }

    // ===== Getters =====
    public Long getId() {
        return id;
    }

    public Sala getSala() {
        return sala;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getLocalidadesLibres() {return localidadesLibres;}

    // ===== Setters =====
    public void setId(Long id) {
        this.id = id;
    }
    public void setSala(Sala sala) {
        this.sala = sala;
    }
    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public void setLocalidadesLibres(Integer localidadesLibres) {
        this.localidadesLibres = localidadesLibres;
    }

    @Version
    public Long getVersion(){
        return version;
    }
    public void setVersion(Long version){this.version = version;}

    // ===== Lógica de dominio =====
    public int getEntradasDisponibles() {
        return this.localidadesLibres;
    }

    public boolean haComenzado() {
        return LocalDateTime.now().isAfter(this.fechaHora);
    }

    public boolean hayDisponibilidad(int numeroEntradas) {
        return numeroEntradas > 0 && numeroEntradas <= 10 && numeroEntradas <= getEntradasDisponibles();
    }
}