package hospital.model;

/**
 * Represents an appointment between a patient and a doctor.
 *
 * @author Student
 * @version 1.0
 */
public class Appointment {

    /** Unique appointment ID */
    private int appointmentId;

    /** The patient for this appointment */
    private Patient patient;

    /** The doctor for this appointment */
    private Doctor doctor;

    /** Date of the appointment (format: YYYY-MM-DD) */
    private String date;

    /** Time of the appointment (format: HH:MM) */
    private String time;

    /** Status: "Scheduled", "Completed", or "Cancelled" */
    private String status;

    /** Optional notes or reason for the visit */
    private String notes;

    /**
     * Constructs an Appointment.
     *
     * @param appointmentId the unique appointment ID
     * @param patient       the patient
     * @param doctor        the doctor
     * @param date          the date (YYYY-MM-DD)
     * @param time          the time (HH:MM)
     * @param notes         reason or notes for the visit
     */
    public Appointment(int appointmentId, Patient patient, Doctor doctor,
                       String date, String time, String notes) {
        this.appointmentId = appointmentId;
        this.patient       = patient;
        this.doctor        = doctor;
        this.date          = date;
        this.time          = time;
        this.notes         = notes;
        this.status        = "Scheduled";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /** @return the appointment ID */
    public int getAppointmentId() { return appointmentId; }

    /** @return the patient */
    public Patient getPatient() { return patient; }

    /** @return the doctor */
    public Doctor getDoctor() { return doctor; }

    /** @return the date */
    public String getDate() { return date; }

    /** @return the time */
    public String getTime() { return time; }

    /** @return the status */
    public String getStatus() { return status; }

    /** @return the notes */
    public String getNotes() { return notes; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /** @param date the new date */
    public void setDate(String date) { this.date = date; }

    /** @param time the new time */
    public void setTime(String time) { this.time = time; }

    /** @param notes the new notes */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Marks the appointment as Completed.
     */
    public void complete() { this.status = "Completed"; }

    /**
     * Cancels the appointment and marks the doctor as available.
     */
    public void cancel() {
        this.status = "Cancelled";
        doctor.setAvailable(true);
    }

    /**
     * Returns a string representation of the appointment.
     * @return formatted appointment details
     */
    @Override
    public String toString() {
        return "Appointment #" + appointmentId
                + " | Patient: " + patient.getName()
                + " | Doctor: Dr. " + doctor.getName()
                + " | Date: " + date + " " + time
                + " | Status: " + status;
    }
}
