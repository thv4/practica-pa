package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.exceptions.*;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.exceptions.*;
import org.springframework.data.domain.Slice;


public interface CompraService {

    /**
<<<<<<< HEAD
=======
     * Realiza la compra de unas entradas
     * @param sesionId Identificador de la sesión
     * @param usuarioId Identificador del usuario
     * @throws InstanceNotFoundException si no existe la sesión
     * @throws MaxLocalidadesExceedException si no quedan entradas disponibles
     * @throws SesionExpiredException si la sesión ya ha comenzado
     */
    public Compra comprarEntradas(Long sesionId, Long usuarioId, int n, String tarjeta) throws InstanceNotFoundException,MaxLocalidadesExceedException, SesionExpiredException;

    /*
    /**
     * Devuelve todas las compras del usuario
     * @param usuarioId Identificador del usuario
     * @param page Número de pagina del Pageable
     * @param size Tamaño de la página del Pageable
     * @throws InstanceNotFoundException si no existe el usuario
     */
 //  public Slice<Compra> findHistoricoCompras(Long usuarioId, int page, int size) throws InstanceNotFoundException;


    /**
>>>>>>> [FUNC-4] Se añade la funcionalidad de comprar compra con sus test y sus excepciones
     * Entrega las entradas de una compra
     * @param compraId Identificador de la compra
     * @param tarjetaBancaria Número de tarjeta para verificar
     * @throws InstanceNotFoundException si no existe la compra
     * @throws IncorrectCreditCardException si la tarjeta no coincide
     * @throws TicketsAlreadyDeliveredException si las entradas ya fueron entregadas
     * @throws SessionAlreadyStartedException si la sesión ya ha comenzado
     */

    void entregarEntradas(Long compraId, String tarjetaBancaria)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            TicketsAlreadyDeliveredException, SessionAlreadyStartedException;
}