package es.udc.paproject.backend.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SesionDao extends JpaRepository<Sesion, Long> {

    // Sesiones entre dos fechas (para un día concreto)
    List<Sesion> findByFechaHoraBetweenOrderByFechaHoraAsc(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    // Sesiones futuras desde un momento dado
    List<Sesion> findByFechaHoraAfterOrderByFechaHoraAsc(
            LocalDateTime fechaHora
    );

    // Sesiones de una película concreta
    List<Sesion> findByPeliculaIdOrderByFechaHoraAsc(
            Long peliculaId
    );

    // Sesiones de una película entre fechas
    List<Sesion> findByPeliculaIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long peliculaId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}