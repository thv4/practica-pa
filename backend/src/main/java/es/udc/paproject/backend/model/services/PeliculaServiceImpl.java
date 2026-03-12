package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class PeliculaServiceImpl implements PeliculaService{

    @Autowired
    private PeliculaDao peliculaDao;

    @Override
    public Pelicula getDetallePelicula(Long peliculaId) throws InstanceNotFoundException {
        Optional<Pelicula> pelicula = peliculaDao.findById(peliculaId);

        if (!pelicula.isPresent()) {
            throw new InstanceNotFoundException("project.entities.pelicula", peliculaId);
        }
        return pelicula.get();
    }
}
