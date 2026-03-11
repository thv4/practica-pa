package es.udc.paproject.backend.model.exceptions;

public class IncorrectCreditCardException extends Exception {
    public IncorrectCreditCardException(Long compraId) {
        super("Incorrect credit card for purchase with id: " + compraId);
    }
}