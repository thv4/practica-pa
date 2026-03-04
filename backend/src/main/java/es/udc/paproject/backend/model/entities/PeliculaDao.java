package es.udc.paproject.backend.model.entities;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PeliculaDao extends CrudRepository<Pelicula, Long> {
    List<Pelicula> findAllByOrderByTituloAsc();
}
