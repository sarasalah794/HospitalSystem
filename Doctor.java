package hospital.model;

/**
 * Represents a doctor in the hospital management system.
 * Extends {@link Person} and adds doctor-specific attributes.
 *
 * @author Student
 * @version 1.0
 */
public class Doctor extends Person {

    /** Medical specialty of the doctor (e.g., "Cardiology") */
    private String specialty;

    /** License number of the doctor */
    private String licenseNumber;

    /** Availability status: true if available */
    private boolean available;

    /**
     * Constructs a Doctor with full details.
     *
     * @param id            the unique doctor ID
     * @param name          the full name
     * @param age           the age
     * @param phone         the phone number
     * @param email         the email address
     * @param specialty     the medical specialty
     * @param licenseNumber the medical license number
     */
    public Doctor(int id, String name, int age, String phone, String email,
                  String specialty, String licenseNumber) {
        super(id, name, age, phone, email);
        this.specialty     = specialty;
        this.licenseNumber = licenseNumber;
        this.available     = true;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the doctor's specialty.
     * @return the specialty
     */
    public String getSpecialty() { return specialty; }

    /**
     * Returns the doctor's license number.
     * @return the license number
     */
    public String getLicenseNumber() { return licenseNumber; }

    /**
     * Returns whether the doctor is currently available.
     * @return true if available
     */
    public boolean isAvailable() { return available; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the doctor's specialty.
     * @param specialty the new specialty
     */
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    /**
     * Sets the doctor's license number.
     * @param licenseNumber the new license number
     */
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    /**
     * Sets the doctor's availability.
     * @param available true to mark as available
     */
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Returns the role of this person.
     * @return "Doctor"
     */
    @Override
    public String getRole() { return "Doctor"; }

    /**
     * Returns a string representation of the doctor.
     * @return formatted doctor details
     */
    @Override
    public String toString() {
        return super.toString()
                + " | Specialty: " + specialty
                + " | Available: " + (available ? "Yes" : "No");
    }
}
