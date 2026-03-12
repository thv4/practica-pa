package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.PastDateException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CarteleraService {
    Map<Pelicula, List<Sesion>> getCartelera(LocalDateTime dia) throws PastDateException;
}
