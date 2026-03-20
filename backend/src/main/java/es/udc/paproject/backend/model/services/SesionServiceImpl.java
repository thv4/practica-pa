package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;

import es.udc.paproject.backend.model.exceptions.PastDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SesionServiceImpl implements SesionService {

    @Autowired
    private SesionDao sesionDao;

    @Override
    public Sesion getDetalleSesion(Long sesionId)
            throws InstanceNotFoundException, PastDateException {

        LocalDateTime ahora = LocalDateTime.now();

        Optional<Sesion> sesionOptional = sesionDao.findById(sesionId);

        if (!sesionOptional.isPresent()) {
            throw new InstanceNotFoundException("project.entities.sesion", sesionId);
        }

        Sesion sesion = sesionOptional.get();

        if (sesion.getFechaHora().isBefore(ahora)) {
            throw new PastDateException(sesion.getFechaHora().toString());
        }

        return sesion;
    }
}