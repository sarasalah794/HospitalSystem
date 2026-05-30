package hospital.model;

/**
 * Represents a billing record for a patient's hospital services.
 *
 * @author Student
 * @version 1.0
 */
public class Bill {

    /** Unique bill ID */
    private int billId;

    /** The patient being billed */
    private Patient patient;

    /** Date the bill was generated (YYYY-MM-DD) */
    private String billDate;

    /** Cost of consultation */
    private double consultationFee;

    /** Cost of medications */
    private double medicationFee;

    /** Cost of lab tests */
    private double labFee;

    /** Cost of room (if inpatient) */
    private double roomFee;

    /** Payment status: "Unpaid" or "Paid" */
    private String paymentStatus;

    /**
     * Constructs a Bill for a patient.
     *
     * @param billId          the unique bill ID
     * @param patient         the patient being billed
     * @param billDate        the billing date
     * @param consultationFee the consultation charge
     * @param medicationFee   the medication charge
     * @param labFee          the lab test charge
     * @param roomFee         the room charge (0 if outpatient)
     */
    public Bill(int billId, Patient patient, String billDate,
                double consultationFee, double medicationFee,
                double labFee, double roomFee) {
        this.billId          = billId;
        this.patient         = patient;
        this.billDate        = billDate;
        this.consultationFee = consultationFee;
        this.medicationFee   = medicationFee;
        this.labFee          = labFee;
        this.roomFee         = roomFee;
        this.paymentStatus   = "Unpaid";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /** @return the bill ID */
    public int getBillId() { return billId; }

    /** @return the patient */
    public Patient getPatient() { return patient; }

    /** @return the bill date */
    public String getBillDate() { return billDate; }

    /** @return the consultation fee */
    public double getConsultationFee() { return consultationFee; }

    /** @return the medication fee */
    public double getMedicationFee() { return medicationFee; }

    /** @return the lab fee */
    public double getLabFee() { return labFee; }

    /** @return the room fee */
    public double getRoomFee() { return roomFee; }

    /** @return the payment status */
    public String getPaymentStatus() { return paymentStatus; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /** @param consultationFee the updated consultation fee */
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    /** @param medicationFee the updated medication fee */
    public void setMedicationFee(double medicationFee) { this.medicationFee = medicationFee; }

    /** @param labFee the updated lab fee */
    public void setLabFee(double labFee) { this.labFee = labFee; }

    /** @param roomFee the updated room fee */
    public void setRoomFee(double roomFee) { this.roomFee = roomFee; }

    /**
     * Calculates and returns the total bill amount.
     *
     * @return total = consultation + medication + lab + room fees
     */
    public double getTotal() {
        return consultationFee + medicationFee + labFee + roomFee;
    }

    /**
     * Marks the bill as paid.
     */
    public void markAsPaid() {
        this.paymentStatus = "Paid";
    }

    /**
     * Returns a formatted receipt string for this bill.
     *
     * @return the formatted bill receipt
     */
    public String generateReceipt() {
        return "========== BILL RECEIPT ==========\n"
                + "Bill ID     : " + billId       + "\n"
                + "Patient     : " + patient.getName() + "\n"
                + "Date        : " + billDate     + "\n"
                + "----------------------------------\n"
                + "Consultation: $" + consultationFee + "\n"
                + "Medications : $" + medicationFee   + "\n"
                + "Lab Tests   : $" + labFee          + "\n"
                + "Room Charge : $" + roomFee         + "\n"
                + "----------------------------------\n"
                + "TOTAL       : $" + getTotal()      + "\n"
                + "Status      : " + paymentStatus    + "\n"
                + "==================================";
    }

    /**
     * Returns a string representation of the bill.
     * @return formatted bill summary
     */
    @Override
    public String toString() {
        return "Bill #" + billId
                + " | Patient: " + patient.getName()
                + " | Total: $" + getTotal()
                + " | Status: " + paymentStatus;
    }
}
