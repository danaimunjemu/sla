package zw.co.afc.orbit.sla.exception;

import java.io.Serial;

public class AgreementDoesNotExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code AgreementDoesNotExistException} with the specified detail message.
     *
     * @param message the detail message
     */
    public AgreementDoesNotExistException(String message) {
        super(message);
    }

}
