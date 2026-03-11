package es.udc.paproject.backend.model.exceptions;

public class TicketsAlreadyDeliveredException extends Exception {
    public TicketsAlreadyDeliveredException(Long compraId) {
        super("Tickets already delivered for purchase with id: " + compraId);
    }
}