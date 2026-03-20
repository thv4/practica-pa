package es.udc.paproject.backend.model.services;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;

public interface SesionService {

    Sesion getDetalleSesion(Long sesionId) throws InstanceNotFoundException, PastDateException;

}
