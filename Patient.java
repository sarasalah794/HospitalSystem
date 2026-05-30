package hospital.model;

/**
 * Represents a patient in the hospital management system.
 * Extends {@link Person} and adds medical-specific attributes.
 *
 * @author Student
 * @version 1.0
 */
public class Patient extends Person {

    /** Blood type of the patient */
    private String bloodType;

    /** Known allergies of the patient */
    private String allergies;

    /** Current status: "Outpatient" or "Inpatient" */
    private String status;

    /** Room number assigned (if inpatient) */
    private String roomNumber;

    /**
     * Constructs a Patient with full details.
     *
     * @param id         the unique patient ID
     * @param name       the full name
     * @param age        the age
     * @param phone      the phone number
     * @param email      the email address
     * @param bloodType  the blood type (e.g., "A+")
     * @param allergies  known allergies
     * @param status     "Outpatient" or "Inpatient"
     */
    public Patient(int id, String name, int age, String phone, String email,
                   String bloodType, String allergies, String status) {
        super(id, name, age, phone, email);
        this.bloodType  = bloodType;
        this.allergies  = allergies;
        this.status     = status;
        this.roomNumber = "N/A";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the patient's blood type.
     * @return the blood type
     */
    public String getBloodType() { return bloodType; }

    /**
     * Returns the patient's known allergies.
     * @return the allergies
     */
    public String getAllergies() { return allergies; }

    /**
     * Returns the patient's current status.
     * @return "Outpatient" or "Inpatient"
     */
    public String getStatus() { return status; }

    /**
     * Returns the assigned room number.
     * @return room number, or "N/A" if not assigned
     */
    public String getRoomNumber() { return roomNumber; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the patient's blood type.
     * @param bloodType the new blood type
     */
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    /**
     * Sets the patient's allergies.
     * @param allergies the new allergies description
     */
    public void setAllergies(String allergies) { this.allergies = allergies; }

    /**
     * Sets the patient's status.
     * @param status "Outpatient" or "Inpatient"
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Assigns a room to the patient (sets status to Inpatient automatically).
     * @param roomNumber the room number to assign
     */
    public void assignRoom(String roomNumber) {
        this.roomNumber = roomNumber;
        this.status     = "Inpatient";
    }

    /**
     * Discharges the patient (clears room and sets status to Outpatient).
     */
    public void discharge() {
        this.roomNumber = "N/A";
        this.status     = "Outpatient";
    }

    /**
     * Returns the role of this person.
     * @return "Patient"
     */
    @Override
    public String getRole() { return "Patient"; }

    /**
     * Returns a string representation of the patient.
     * @return formatted patient details
     */
    @Override
    public String toString() {
        return super.toString()
                + " | Blood: " + bloodType
                + " | Status: " + status
                + " | Room: " + roomNumber;
    }
}
