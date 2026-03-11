package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraDao compraDao;

    @Override
    public void entregarEntradas(Long compraId, String tarjetaBancaria)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            TicketsAlreadyDeliveredException, SessionAlreadyStartedException {

        // 1. Recuperar compra (o lanzar InstanceNotFoundException)
        Compra compra = compraDao.findById(compraId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.compra", compraId));

        // 2. Comprobar si se hizo con ese número de tarjeta
        if (!compra.getTarjetaBancaria().equals(tarjetaBancaria)) {
            throw new IncorrectCreditCardException(compraId);
        }

        // 3. Comprobar si las entradas ya fueron entregadas
        if (compra.getEntregada()) {
            throw new TicketsAlreadyDeliveredException(compraId);
        }

        // 4. Comprobar si la sesión ya ha comenzado
        if (compra.getSesion().haComenzado()) {
            throw new SessionAlreadyStartedException(compra.getSesion().getId());
        }

        // 5. Marcar entradas como entregadas
        compra.setEntregada(true);

        // No hace falta llamar a save() porque @Transactional y el objeto
        // está gestionado por JPA (persistente)
    }
}