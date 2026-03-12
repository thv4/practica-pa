package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;

public interface PeliculaService {
    Pelicula getDetallePelicula(Long peliculaId) throws InstanceNotFoundException;
}
