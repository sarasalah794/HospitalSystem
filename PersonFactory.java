package hospital.factory;

import hospital.exception.HospitalException;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.Person;
import hospital.model.Staff;

/**
 * Factory class that centralizes creation of {@link Person} subtypes
 * (Doctor, Patient, Staff).
 *
 * Design Pattern: Factory Method.
 * Instead of calling "new Doctor(...)" / "new Patient(...)" directly
 * in the UI or business logic, this factory provides one place that
 * decides which concrete class to instantiate, based on a type code.
 * This makes it easy to add new Person subtypes later without changing
 * the calling code (Open/Closed Principle).
 *
 * Layer: this class belongs to the Business/Service layer of the
 * four-layer architecture (Presentation -> Business -> Data Access -> Model).
 * It must not live inside the "model" package, since the model package
 * should only hold plain entity classes (Person, Doctor, Patient, Staff...),
 * not object-creation logic.
 *
 * @author Student
 * @version 1.1
 */
public class PersonFactory {

    /** Type constant for creating a Doctor. */
    public static final String TYPE_DOCTOR  = "DOCTOR";

    /** Type constant for creating a Patient. */
    public static final String TYPE_PATIENT = "PATIENT";

    /** Type constant for creating a Staff member. */
    public static final String TYPE_STAFF   = "STAFF";

    private PersonFactory() {
        // Utility class: no instances needed.
    }

    /**
     * Creates a {@link Person} subtype (Doctor, Patient, or Staff) based on
     * the given type code. The meaning of extra1/extra2/extra3 depends on
     * the type being created:
     * <ul>
     *   <li>DOCTOR:  extra1 = specialty,   extra2 = licenseNumber,  extra3 = (unused)</li>
     *   <li>PATIENT: extra1 = bloodType,   extra2 = allergies,      extra3 = status</li>
     *   <li>STAFF:   extra1 = staffRole,   extra2 = employeeNumber, extra3 = department</li>
     * </ul>
     *
     * @param type   one of {@link #TYPE_DOCTOR}, {@link #TYPE_PATIENT}, {@link #TYPE_STAFF}
     * @param id     the person's ID
     * @param name   the person's name
     * @param age    the person's age
     * @param phone  the phone number
     * @param email  the email address
     * @param extra1 first type-specific field (see table above)
     * @param extra2 second type-specific field (see table above)
     * @param extra3 third type-specific field (see table above)
     * @return a new {@link Person} instance of the requested subtype
     * @throws HospitalException if the type is missing/unknown or the name is empty
     */
    public static Person createPerson(String type, int id, String name, int age,
                                       String phone, String email, String extra1, String extra2, String extra3)
            throws HospitalException {

        if (type == null || type.isEmpty()) {
            throw new HospitalException("Person type must be specified.");
        }
        if (name == null || name.isEmpty()) {
            throw new HospitalException("Name cannot be empty.");
        }

        switch (type.toUpperCase()) {
            case TYPE_DOCTOR:
                // extra1 = specialty, extra2 = licenseNumber
                return new Doctor(id, name, age, phone, email, extra1, extra2);

            case TYPE_PATIENT:
                // extra1 = bloodType, extra2 = allergies, extra3 = status
                return new Patient(id, name, age, phone, email, extra1, extra2, extra3);

            case TYPE_STAFF:
                // extra1 = staffRole, extra2 = employeeNumber, extra3 = department
                return new Staff(id, name, age, phone, email, extra1, extra2, extra3);

            default:
                throw new HospitalException("Unknown person type: " + type);
        }
    }
}
