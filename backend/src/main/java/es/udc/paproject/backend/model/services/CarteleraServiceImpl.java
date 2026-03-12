package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class CarteleraServiceImpl implements CarteleraService {

    @Autowired
    private SesionDao sesionDao;

    @Override
    public Map<Pelicula, List<Sesion>> getCartelera(LocalDateTime diaElegido) throws PastDateException {

        LocalDateTime ahora = LocalDateTime.now();

        // Comprobamos si el día es anterior a hoy
        if (diaElegido.toLocalDate().isBefore(ahora.toLocalDate())) {
            throw new PastDateException(diaElegido.toString());
        }

        LocalDateTime inicio;
        if (diaElegido.toLocalDate().equals(ahora.toLocalDate())) {
            inicio = ahora;
        } else {
            inicio = diaElegido.toLocalDate().atStartOfDay();
        }

        LocalDateTime fin = diaElegido.toLocalDate().atTime(23, 59, 59);

        List<Sesion> sesiones = sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(inicio, fin);

        return sesiones.stream()
                .collect(Collectors.groupingBy(
                        Sesion::getPelicula,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}
