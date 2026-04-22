package es.udc.paproject.backend.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SesionDao extends JpaRepository<Sesion, Long> {
    List<Sesion> findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(
            LocalDateTime inicio, LocalDateTime fin);
}