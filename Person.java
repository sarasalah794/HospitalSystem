package hospital.model;

/**
 * Abstract base class representing a person in the hospital system.
 * Provides common attributes shared by all person types.
 *
 * @author Student
 * @version 1.0
 */
public abstract class Person {

    /** Unique identifier for the person */
    private int id;

    /** Full name of the person */
    private String name;

    /** Age of the person */
    private int age;

    /** Phone number of the person */
    private String phone;

    /** Email address of the person */
    private String email;

    /**
     * Constructs a Person with the specified details.
     *
     * @param id    the unique identifier
     * @param name  the full name
     * @param age   the age
     * @param phone the phone number
     * @param email the email address
     */
    public Person(int id, String name, int age, String phone, String email) {
        this.id    = id;
        this.name  = name;
        this.age   = age;
        this.phone = phone;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the person's ID.
     * @return the ID
     */
    public int getId() { return id; }

    /**
     * Returns the person's name.
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Returns the person's age.
     * @return the age
     */
    public int getAge() { return age; }

    /**
     * Returns the person's phone number.
     * @return the phone number
     */
    public String getPhone() { return phone; }

    /**
     * Returns the person's email.
     * @return the email
     */
    public String getEmail() { return email; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the person's ID.
     * @param id the new ID
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets the person's name.
     * @param name the new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the person's age.
     * @param age the new age
     */
    public void setAge(int age) { this.age = age; }

    /**
     * Sets the person's phone number.
     * @param phone the new phone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Sets the person's email.
     * @param email the new email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Abstract method that returns a string describing the person's role.
     * Must be implemented by all subclasses.
     *
     * @return the role description
     */
    public abstract String getRole();

    /**
     * Returns a string representation of the person.
     * @return formatted person details
     */
    @Override
    public String toString() {
        return "[" + getRole() + "] ID: " + id + " | Name: " + name
                + " | Age: " + age + " | Phone: " + phone;
    }
}
