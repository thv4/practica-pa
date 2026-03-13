package es.udc.paproject.backend.model.services;


import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraDao compraDao;

    @Autowired
    private SesionDao sesionDao;

    @Autowired
    private PermissionChecker permissionChecker;

    @Override
    public Compra comprarEntradas(Long sesionId, Long usuarioId, int n, String tarjeta) throws InstanceNotFoundException, MaxLocalidadesExceedException, SesionExpiredException {

        User user = permissionChecker.checkUser(usuarioId);

        Sesion sesion = sesionDao.findById(sesionId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.sesion", sesionId));

        if(!sesion.hayDisponibilidad(n)){
            throw new MaxLocalidadesExceedException();
        }

        if(sesion.haComenzado()){
            throw new SesionExpiredException();
        }
        //sesionDao.save(sesion.getSala().setCapacidad(sesion.getEntradasDisponibles() - n));
        sesion.setLocalidadesLibres(sesion.getLocalidadesLibres() - n);


        Compra compra = new Compra(user,sesion, LocalDateTime.now(),n,tarjeta,false);

        compraDao.save(compra);

        return compra;
    }

    @Override
    public Slice<Compra> findHistoricoCompras(Long usuarioId, int page, int size) throws InstanceNotFoundException {

        permissionChecker.checkUser(usuarioId);

        Pageable pageable = PageRequest.of(page,size);

        return compraDao.findByUser_IdOrderByFechaRegistroCompraDesc(usuarioId,pageable);
    }


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