package hospital.model;

/**
 * Represents a hospital staff member (Receptionist, Admin, or Nurse).
 * Extends {@link Person} and adds role-specific behavior.
 *
 * @author Student
 * @version 1.0
 */
public class Staff extends Person {

    /** The staff member's role: "Receptionist", "Admin", or "Nurse" */
    private String staffRole;

    /** Employee number */
    private String employeeNumber;

    /** Department the staff member belongs to */
    private String department;

    /**
     * Constructs a Staff member with full details.
     *
     * @param id             the unique staff ID
     * @param name           the full name
     * @param age            the age
     * @param phone          the phone number
     * @param email          the email address
     * @param staffRole      "Receptionist", "Admin", or "Nurse"
     * @param employeeNumber the employee number
     * @param department     the department name
     */
    public Staff(int id, String name, int age, String phone, String email,
                 String staffRole, String employeeNumber, String department) {
        super(id, name, age, phone, email);
        this.staffRole      = staffRole;
        this.employeeNumber = employeeNumber;
        this.department     = department;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the staff role.
     * @return "Receptionist", "Admin", or "Nurse"
     */
    public String getStaffRole() { return staffRole; }

    /**
     * Returns the employee number.
     * @return the employee number
     */
    public String getEmployeeNumber() { return employeeNumber; }

    /**
     * Returns the department name.
     * @return the department
     */
    public String getDepartment() { return department; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the staff role.
     * @param staffRole the new role
     */
    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }

    /**
     * Sets the employee number.
     * @param employeeNumber the new employee number
     */
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    /**
     * Sets the department.
     * @param department the new department
     */
    public void setDepartment(String department) { this.department = department; }

    /**
     * Returns the role of this person.
     * @return the staff role (e.g., "Receptionist")
     */
    @Override
    public String getRole() { return staffRole; }

    /**
     * Returns a string representation of the staff member.
     * @return formatted staff details
     */
    @Override
    public String toString() {
        return super.toString()
                + " | Dept: " + department
                + " | Emp#: " + employeeNumber;
    }
}
