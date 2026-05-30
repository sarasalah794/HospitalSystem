package hospital.exception;

/**
 * Custom exception class for Hospital Management System errors.
 * Used to signal domain-specific errors such as duplicate records,
 * invalid inputs, or unavailable resources.
 *
 * @author Student
 * @version 1.0
 */
public class HospitalException extends Exception {

    /**
     * Constructs a HospitalException with a descriptive message.
     *
     * @param message the detail message explaining the error
     */
    public HospitalException(String message) {
        super(message);
    }

    /**
     * Constructs a HospitalException with a message and a cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public HospitalException(String message, Throwable cause) {
        super(message, cause);
    }
}
