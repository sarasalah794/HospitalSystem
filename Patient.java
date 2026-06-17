package hospital.model;

/**
 * Represents a patient in the hospital management system.
 * Extends {@link Person} and implements {@link Manageable}.
 *
 * @author Student
 * @version 1.0
 */
public class Patient extends Person implements Manageable {

    private String bloodType;
    private String allergies;
    private String status;
    private String roomNumber;

    /**
     * Constructs a Patient with full details.
     */
    public Patient(int id, String name, int age, String phone, String email,
                   String bloodType, String allergies, String status) {
        super(id, name, age, phone, email);
        this.bloodType  = bloodType;
        this.allergies  = allergies;
        this.status     = status;
        this.roomNumber = "N/A";
    }

    public String getBloodType() { return bloodType; }
    public String getAllergies() { return allergies; }
    public String getStatus() { return status; }
    public String getRoomNumber() { return roomNumber; }

    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setStatus(String status) { this.status = status; }

    public void assignRoom(String roomNumber) {
        this.roomNumber = roomNumber;
        this.status = "Inpatient";
    }

    public void discharge() {
        this.roomNumber = "N/A";
        this.status = "Outpatient";
    }

    @Override
    public String getRole() { return "Patient"; }

    @Override
    public String getSummary() {
        return "Patient: " + getName() + " | Blood: " + bloodType + " | Status: " + status;
    }

    @Override
    public boolean isValid() {
        return getName() != null && !getName().isEmpty() && getAge() > 0;
    }

    @Override
    public String toString() {
        return super.toString() + " | Blood: " + bloodType + " | Status: " + status;
    }
}
