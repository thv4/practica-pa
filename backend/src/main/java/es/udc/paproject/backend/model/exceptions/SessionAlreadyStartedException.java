package es.udc.paproject.backend.model.exceptions;

public class SessionAlreadyStartedException extends Exception {
    public SessionAlreadyStartedException(Long sesionId) {
        super("Session with id: " + sesionId + " has already started");
    }
}