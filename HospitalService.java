package hospital.service;

import hospital.exception.HospitalException;
import hospital.factory.PersonFactory;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.persistence.AppointmentDAO;
import hospital.persistence.BillDAO;
import hospital.persistence.DoctorDAO;
import hospital.persistence.MedicalRecordDAO;
import hospital.persistence.PatientDAO;
import hospital.persistence.ReportDAO;
import hospital.persistence.StatisticsDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * طبقة التطبيق (Application Layer) لنظام إدارة المستشفى.
 *
 * <p><b>المعمارية الرباعية الطبقات - Four-Layer Architecture (Lecture #6):</b></p>
 * <ul>
 *   <li>Presentation:  hospital.ui.HospitalGUI / hospital.ui.DashboardPanel</li>
 *   <li>Application:   hospital.service.HospitalService (هذا الكلاس) — يحوي الـ use cases
 *       والتحقق من صحة البيانات (Business Logic)، ولا يحتوي على أي SQL.</li>
 *   <li>Domain:        hospital.model.* (Person, Doctor, Patient ...) — الكائنات ومنطقها،
 *       تُبنى عبر {@link PersonFactory} (نمط Factory Method).</li>
 *   <li>Persistence:   hospital.persistence.* (PatientDAO, DoctorDAO ...) — المكان الوحيد
 *       الذي يحتوي على SQL، عبر hospital.db.DatabaseConnection.</li>
 * </ul>
 *
 * <p>هذا الكلاس (Application) هو حلقة الوصل: يستدعيه الـ GUI، وهو يستدعي الـ DAOs.</p>
 *
 * @author Student
 * @version 4.0
 */
public class HospitalService {

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    private final BillDAO billDAO = new BillDAO();
    private final StatisticsDAO statisticsDAO = new StatisticsDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    // ── Patient Use Cases ─────────────────────────────────────────────────────

    /**
     * حالة استخدام: إضافة مريض جديد.
     * تتحقق من صحة البيانات، تبني كائن Patient عبر PersonFactory، ثم تفوض الحفظ لـ PatientDAO.
     */
    public void addPatient(String name, String age, String phone, String email,
                           String blood, String allergies, String status)
            throws HospitalException, SQLException {

        if (name == null || name.isEmpty()) throw new HospitalException("اسم المريض لا يمكن أن يكون فارغًا.");
        if (age == null || age.isEmpty())   throw new HospitalException("العمر لا يمكن أن يكون فارغًا.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("العمر يجب أن يكون رقمًا."); }

        // Domain object creation via Factory Method pattern
        Patient patient = (Patient) PersonFactory.createPerson(
                PersonFactory.TYPE_PATIENT, 0, name, ageInt, phone, email, blood, allergies, status);

        patientDAO.insert(patient);
    }

    /** حالة استخدام: حذف مريض. */
    public void deletePatient(int id) throws SQLException {
        patientDAO.delete(id);
    }

    /** حالة استخدام: عرض كل المرضى. */
    public ResultSet getAllPatients() throws SQLException {
        return patientDAO.findAll();
    }

    // ── Doctor Use Cases ──────────────────────────────────────────────────────

    /**
     * حالة استخدام: إضافة طبيب جديد.
     * تتحقق من صحة البيانات، تبني كائن Doctor عبر PersonFactory، ثم تفوض الحفظ لـ DoctorDAO.
     */
    public void addDoctor(String name, String age, String phone, String email,
                          String specialty, String license)
            throws HospitalException, SQLException {

        if (name == null || name.isEmpty())       throw new HospitalException("اسم الطبيب لا يمكن أن يكون فارغًا.");
        if (license == null || license.isEmpty()) throw new HospitalException("رقم الترخيص لا يمكن أن يكون فارغًا.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("العمر يجب أن يكون رقمًا."); }

        // Domain object creation via Factory Method pattern
        Doctor doctor = (Doctor) PersonFactory.createPerson(
                PersonFactory.TYPE_DOCTOR, 0, name, ageInt, phone, email, specialty, license, null);

        doctorDAO.insert(doctor);
    }

    /** حالة استخدام: حذف طبيب. */
    public void deleteDoctor(int id) throws SQLException {
        doctorDAO.delete(id);
    }

    /** حالة استخدام: عرض كل الأطباء. */
    public ResultSet getAllDoctors() throws SQLException {
        return doctorDAO.findAll();
    }

    // ── Appointment Use Cases ─────────────────────────────────────────────────

    /** حالة استخدام: جدولة موعد جديد بعد التحقق من صحة المعرفات. */
    public void scheduleAppointment(String patientIdStr, String doctorIdStr,
                                    String date, String time, String notes)
            throws HospitalException, SQLException {
        if (patientIdStr == null || patientIdStr.isEmpty()) throw new HospitalException("معرف المريض مطلوب.");
        if (doctorIdStr == null || doctorIdStr.isEmpty())   throw new HospitalException("معرف الطبيب مطلوب.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("المعرفات يجب أن تكون أرقامًا."); }

        appointmentDAO.insert(pid, did, date, time, notes);
    }

    /** حالة استخدام: تحديث حالة موعد (Completed/Cancelled). */
    public void updateAppointmentStatus(int id, String status) throws SQLException {
        appointmentDAO.updateStatus(id, status);
    }

    /** حالة استخدام: عرض كل المواعيد. */
    public ResultSet getAllAppointmentsJoined() throws SQLException {
        return appointmentDAO.findAllJoined();
    }

    // ── Medical Record Use Cases ──────────────────────────────────────────────

    /** حالة استخدام: إضافة سجل طبي بعد التحقق من صحة البيانات. */
    public void addRecord(String patientIdStr, String doctorIdStr, String diagnosis,
                          String treatment, String medications, String notes)
            throws HospitalException, SQLException {
        if (patientIdStr == null || patientIdStr.isEmpty()) throw new HospitalException("معرف المريض مطلوب.");
        if (diagnosis == null || diagnosis.isEmpty())        throw new HospitalException("التشخيص لا يمكن أن يكون فارغًا.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("المعرفات يجب أن تكون أرقامًا."); }

        recordDAO.insert(pid, did, diagnosis, treatment, medications, notes);
    }

    /** حالة استخدام: عرض كل السجلات الطبية. */
    public ResultSet getAllRecordsJoined() throws SQLException {
        return recordDAO.findAllJoined();
    }

    // ── Bill Use Cases ────────────────────────────────────────────────────────

    /** حالة استخدام: توليد فاتورة بعد التحقق من صحة البيانات وحساب الإجمالي. */
    public void generateBill(String patientIdStr, String consultation,
                             String medication, String lab, String room)
            throws HospitalException, SQLException {
        if (patientIdStr == null || patientIdStr.isEmpty()) throw new HospitalException("معرف المريض مطلوب.");
        int pid;
        try { pid = Integer.parseInt(patientIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("معرف المريض يجب أن يكون رقمًا."); }
        double c, m, l, r;
        try {
            c = Double.parseDouble(consultation); m = Double.parseDouble(medication);
            l = Double.parseDouble(lab);          r = Double.parseDouble(room);
        } catch (NumberFormatException e) { throw new HospitalException("الرسوم يجب أن تكون أرقامًا."); }

        double total = c + m + l + r;
        if (total < 0) throw new HospitalException("إجمالي الفاتورة لا يمكن أن يكون سالبًا.");

        billDAO.insert(pid, c, m, l, r);
    }

    /** حالة استخدام: تعليم فاتورة كمدفوعة. */
    public void markBillPaid(int id) throws SQLException {
        billDAO.markPaid(id);
    }

    /** حالة استخدام: عرض كل الفواتير. */
    public ResultSet getAllBillsJoined() throws SQLException {
        return billDAO.findAllJoined();
    }

    // ── Dashboard / Statistics Use Cases ────────────────────────────────────────

    /** حالة استخدام: عدد السجلات في جدول معين (لبطاقات لوحة التحكم). */
    public int getCount(String tableName) {
        return statisticsDAO.getCount(tableName);
    }

    /** حالة استخدام: إجمالي الإيرادات (لبطاقات لوحة التحكم). */
    public double getTotalRevenue() {
        return statisticsDAO.getTotalRevenue();
    }

    /** حالة استخدام: عدد المواعيد لكل حالة (للرسم البياني في لوحة التحكم). */
    public Map<String, Integer> getAppointmentStatusCounts() {
        return statisticsDAO.getAppointmentStatusCounts();
    }

    // ── Reports Use Cases ─────────────────────────────────────────────────────

    /** تقرير 1: عدد المرضى حسب الحالة (مريض داخلي / مريض خارجي). */
    public Map<String, Integer> getPatientsByStatusReport() {
        return reportDAO.getPatientsByStatus();
    }

    /** تقرير 2: عدد الأطباء حسب التخصص. */
    public Map<String, Integer> getDoctorsBySpecialtyReport() {
        return reportDAO.getDoctorsBySpecialty();
    }

    /** تقرير 3: ملخص مالي (إجمالي ومعدود الفواتير المدفوعة/غير المدفوعة). */
    public double[] getFinancialSummaryReport() {
        return reportDAO.getFinancialSummary();
    }
}
