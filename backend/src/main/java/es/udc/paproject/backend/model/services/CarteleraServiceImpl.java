package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class CarteleraServiceImpl implements CarteleraService {

    private static final EtmMonitor ETM_MONITOR = EtmManager.getEtmMonitor();

    @Autowired
    private SesionDao sesionDao;

    @Override
    public Map<Pelicula, List<Sesion>> getCartelera(LocalDate diaElegido) throws PastDateException, InvalidPost6DaysDateException {

        EtmPoint punto = ETM_MONITOR.createPoint("CarteleraService.getCartelera");
        try {
            return calcularCartelera(diaElegido);
        } finally {
            punto.collect();
        }
    }

    private Map<Pelicula, List<Sesion>> calcularCartelera(LocalDate diaElegido)
            throws PastDateException, InvalidPost6DaysDateException {

        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        if (diaElegido.isBefore(hoy)) {
            throw new PastDateException(diaElegido.toString());
        }

        if (diaElegido.isAfter(hoy.plusDays(6))) {
            throw new InvalidPost6DaysDateException();
        }
        LocalDateTime inicio;
        if (diaElegido.equals(hoy)) {
            inicio = ahora;
        } else {
            inicio = diaElegido.atStartOfDay();
        }
        LocalDateTime fin = diaElegido.atTime(LocalTime.MAX);
        List<Sesion> sesiones = consultarSesiones(inicio, fin);
        return agruparPorPelicula(sesiones);
    }

    private List<Sesion> consultarSesiones(LocalDateTime inicio, LocalDateTime fin) {
        EtmPoint punto = ETM_MONITOR.createPoint("CarteleraService.getCartelera:consulta");
        try {
            return sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(inicio, fin);
        } finally {
            punto.collect();
        }
    }

    private Map<Pelicula, List<Sesion>> agruparPorPelicula(List<Sesion> sesiones) {
        EtmPoint punto = ETM_MONITOR.createPoint("CarteleraService.getCartelera:agrupacion");
        try {
            return sesiones.stream()
                    .collect(Collectors.groupingBy(
                            Sesion::getPelicula,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
        } finally {
            punto.collect();
        }
    }
}
