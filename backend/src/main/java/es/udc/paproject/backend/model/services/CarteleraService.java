package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CarteleraService {
    Map<Pelicula, List<Sesion>> getCartelera(LocalDate dia) throws PastDateException, InvalidPost6DaysDateException;
}
