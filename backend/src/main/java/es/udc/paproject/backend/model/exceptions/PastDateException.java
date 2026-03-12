package es.udc.paproject.backend.model.exceptions;

public class PastDateException extends RuntimeException {
    private final String date;

    public PastDateException(String date) {
        super("Date is in the past => " + date);
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
