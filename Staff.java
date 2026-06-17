package hospital.model;

/**
 * Represents a hospital staff member.
 * Extends {@link Person} and implements {@link Manageable}.
 *
 * @author Student
 * @version 1.0
 */
public class Staff extends Person implements Manageable {

    private String staffRole;
    private String employeeNumber;
    private String department;

    /**
     * Constructs a Staff member with full details.
     */
    public Staff(int id, String name, int age, String phone, String email,
                 String staffRole, String employeeNumber, String department) {
        super(id, name, age, phone, email);
        this.staffRole      = staffRole;
        this.employeeNumber = employeeNumber;
        this.department     = department;
    }

    public String getStaffRole() { return staffRole; }
    public String getEmployeeNumber() { return employeeNumber; }
    public String getDepartment() { return department; }

    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() { return staffRole; }

    @Override
    public String getSummary() {
        return "Staff: " + getName() + " | Role: " + staffRole + " | Dept: " + department;
    }

    @Override
    public boolean isValid() {
        return getName() != null && !getName().isEmpty() && staffRole != null && !staffRole.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString() + " | Dept: " + department + " | Emp#: " + employeeNumber;
    }
}
