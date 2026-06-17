package hospital.model;

/**
 * Interface representing manageable entities in the hospital system.
 * Any class that implements this interface must provide
 * display and validation behavior.
 *
 * @author Student
 * @version 1.0
 */
public interface Manageable {

    /**
     * Returns a summary of the entity's details.
     * @return formatted summary string
     */
    String getSummary();

    /**
     * Validates the entity's required fields.
     * @return true if valid, false otherwise
     */
    boolean isValid();
}
