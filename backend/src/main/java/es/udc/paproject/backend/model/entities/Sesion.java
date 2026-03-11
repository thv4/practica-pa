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

    /* Constructor vacío obligatorio para JPA */
    public Sesion() {
    }

    public Sesion(Sala sala, Pelicula pelicula, LocalDateTime fechaHora, BigDecimal precio) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.fechaHora = fechaHora;
        this.precio = precio;
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

    // ===== Lógica de dominio =====
    public int getEntradasDisponibles() {
        return sala.getCapacidad();
    }

    public boolean haComenzado() {
        return LocalDateTime.now().isAfter(this.fechaHora);
    }

    public boolean hayDisponibilidad(int numeroEntradas) {
        return numeroEntradas > 0 && numeroEntradas <= getEntradasDisponibles();
    }
}