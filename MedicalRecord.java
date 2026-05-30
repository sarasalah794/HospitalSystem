package hospital.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient's medical record.
 * Stores diagnosis, treatment, and test results.
 *
 * @author Student
 * @version 1.0
 */
public class MedicalRecord {

    /** Unique record ID */
    private int recordId;

    /** The patient this record belongs to */
    private Patient patient;

    /** The doctor who created/updated the record */
    private Doctor doctor;

    /** Date of the record entry (YYYY-MM-DD) */
    private String date;

    /** Diagnosis written by the doctor */
    private String diagnosis;

    /** Prescribed treatment */
    private String treatment;

    /** Prescribed medications */
    private String medications;

    /** List of lab test results */
    private List<String> testResults;

    /** Additional notes */
    private String notes;

    /**
     * Constructs a MedicalRecord with basic details.
     *
     * @param recordId  the unique record ID
     * @param patient   the patient
     * @param doctor    the attending doctor
     * @param date      the record date
     * @param diagnosis the diagnosis
     * @param treatment the treatment plan
     */
    public MedicalRecord(int recordId, Patient patient, Doctor doctor,
                         String date, String diagnosis, String treatment) {
        this.recordId    = recordId;
        this.patient     = patient;
        this.doctor      = doctor;
        this.date        = date;
        this.diagnosis   = diagnosis;
        this.treatment   = treatment;
        this.medications = "";
        this.notes       = "";
        this.testResults = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /** @return the record ID */
    public int getRecordId() { return recordId; }

    /** @return the patient */
    public Patient getPatient() { return patient; }

    /** @return the doctor */
    public Doctor getDoctor() { return doctor; }

    /** @return the record date */
    public String getDate() { return date; }

    /** @return the diagnosis */
    public String getDiagnosis() { return diagnosis; }

    /** @return the treatment plan */
    public String getTreatment() { return treatment; }

    /** @return prescribed medications */
    public String getMedications() { return medications; }

    /** @return list of test results */
    public List<String> getTestResults() { return testResults; }

    /** @return additional notes */
    public String getNotes() { return notes; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /** @param diagnosis the updated diagnosis */
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    /** @param treatment the updated treatment */
    public void setTreatment(String treatment) { this.treatment = treatment; }

    /** @param medications the updated medications */
    public void setMedications(String medications) { this.medications = medications; }

    /** @param notes the updated notes */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Adds a lab test result to this record.
     *
     * @param result the test result to add
     */
    public void addTestResult(String result) {
        testResults.add(result);
    }

    /**
     * Updates the record with new diagnosis and treatment.
     *
     * @param diagnosis the new diagnosis
     * @param treatment the new treatment
     * @param notes     updated notes
     */
    public void updateRecord(String diagnosis, String treatment, String notes) {
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes     = notes;
    }

    /**
     * Returns a string representation of the medical record.
     * @return formatted record details
     */
    @Override
    public String toString() {
        return "Record #" + recordId
                + " | Patient: " + patient.getName()
                + " | Doctor: Dr. " + doctor.getName()
                + " | Date: " + date
                + "\n  Diagnosis: " + diagnosis
                + "\n  Treatment: " + treatment
                + "\n  Medications: " + medications;
    }
}
