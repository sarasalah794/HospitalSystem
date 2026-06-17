package hospital.model;

/**
 * Represents a doctor in the hospital management system.
 * Extends {@link Person} and implements {@link Manageable}.
 *
 * @author Student
 * @version 1.0
 */
public class Doctor extends Person implements Manageable {

    private String specialty;
    private String licenseNumber;
    private boolean available;

    /**
     * Constructs a Doctor with full details.
     */
    public Doctor(int id, String name, int age, String phone, String email,
                  String specialty, String licenseNumber) {
        super(id, name, age, phone, email);
        this.specialty     = specialty;
        this.licenseNumber = licenseNumber;
        this.available     = true;
    }

    public String getSpecialty() { return specialty; }
    public String getLicenseNumber() { return licenseNumber; }
    public boolean isAvailable() { return available; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String getRole() { return "Doctor"; }

    @Override
    public String getSummary() {
        return "Doctor: Dr. " + getName() + " | Specialty: " + specialty + " | Available: " + (available ? "Yes" : "No");
    }

    @Override
    public boolean isValid() {
        return getName() != null && !getName().isEmpty() && licenseNumber != null && !licenseNumber.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString() + " | Specialty: " + specialty + " | Available: " + (available ? "Yes" : "No");
    }
}
