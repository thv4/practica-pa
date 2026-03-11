package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SesionServiceImpl implements SesionService {

    @Autowired
    private SesionDao sesionDao;

    @Override
    public Sesion getDetalleSesion(Long sesionId)
            throws InstanceNotFoundException {

        return sesionDao.findById(sesionId)
                .orElseThrow(() ->
                        new InstanceNotFoundException("project.entities.sesion",
                                sesionId));
    }
}