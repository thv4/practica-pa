package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.exceptions.*;

public interface CompraService {

    /**
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