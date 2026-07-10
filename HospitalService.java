package hospital.service;

import hospital.db.DatabaseConnection;
import hospital.exception.HospitalException;

import java.sql.*;

/**
 * طبقة منطق الأعمال لنظام إدارة المستشفى.
 *
 * <p><b>المعمارية الرباعية الطبقات - Four Layer Architecture:</b></p>
 * <ul>
 *   <li>Layer 1: Presentation  - hospital.ui.HospitalGUI</li>
 *   <li>Layer 2: Business Logic - hospital.service.HospitalService (هذا الكلاس)</li>
 *   <li>Layer 3: Data Access   - hospital.db.DatabaseConnection</li>
 *   <li>Layer 4: Database      - SQLite</li>
 * </ul>
 *
 * <p>الـ GUI يستدعي هذا الكلاس، وهذا الكلاس يستدعي قاعدة البيانات.</p>
 *
 * @author Student
 * @version 3.0
 */
public class HospitalService {

    // ── Patient Operations ────────────────────────────────────────────────────

    /**
     * يضيف مريضاً جديداً بعد التحقق من صحة البيانات.
     *
     * @param name      اسم المريض
     * @param age       العمر (نص)
     * @param phone     رقم الهاتف
     * @param email     البريد الإلكتروني
     * @param blood     فصيلة الدم
     * @param allergies الحساسيات
     * @param status    الحالة (Outpatient/Inpatient)
     * @throws HospitalException إذا كانت البيانات غير صالحة
     * @throws SQLException      إذا فشلت عملية قاعدة البيانات
     */
    public void addPatient(String name, String age, String phone, String email,
                           String blood, String allergies, String status)
            throws HospitalException, SQLException {
        // Business Logic: التحقق من البيانات
        if (name.isEmpty()) throw new HospitalException("Patient name cannot be empty.");
        if (age.isEmpty())  throw new HospitalException("Age cannot be empty.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("Age must be a number."); }

        // Data Access: حفظ في قاعدة البيانات
        String sql = "INSERT INTO patients(name,age,phone,email,blood_type,allergies,status,room_number)"
                   + " VALUES(?,?,?,?,?,?,?,'N/A')";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, name); ps.setInt(2, ageInt);
        ps.setString(3, phone); ps.setString(4, email);
        ps.setString(5, blood); ps.setString(6, allergies);
        ps.setString(7, status);
        ps.executeUpdate(); ps.close();
    }

    /**
     * يحذف مريضاً بالمعرف.
     * @param id معرف المريض
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void deletePatient(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM patients WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }

    /**
     * يجلب جميع المرضى من قاعدة البيانات.
     * @return ResultSet يحتوي على بيانات المرضى
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet getAllPatients() throws SQLException {
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery("SELECT * FROM patients");
    }

    // ── Doctor Operations ─────────────────────────────────────────────────────

    /**
     * يضيف طبيباً جديداً بعد التحقق من صحة البيانات.
     *
     * @param name      اسم الطبيب
     * @param age       العمر
     * @param phone     رقم الهاتف
     * @param email     البريد الإلكتروني
     * @param specialty التخصص الطبي
     * @param license   رقم الترخيص
     * @throws HospitalException إذا كانت البيانات غير صالحة
     * @throws SQLException      إذا فشلت عملية قاعدة البيانات
     */
    public void addDoctor(String name, String age, String phone, String email,
                          String specialty, String license)
            throws HospitalException, SQLException {
        // Business Logic: التحقق من البيانات
        if (name.isEmpty())    throw new HospitalException("Doctor name cannot be empty.");
        if (license.isEmpty()) throw new HospitalException("License number cannot be empty.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("Age must be a number."); }

        // Data Access: حفظ في قاعدة البيانات
        String sql = "INSERT INTO doctors(name,age,phone,email,specialty,license_number,available)"
                   + " VALUES(?,?,?,?,?,?,1)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, name); ps.setInt(2, ageInt);
        ps.setString(3, phone); ps.setString(4, email);
        ps.setString(5, specialty); ps.setString(6, license);
        ps.executeUpdate(); ps.close();
    }

    /**
     * يحذف طبيباً بالمعرف.
     * @param id معرف الطبيب
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void deleteDoctor(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM doctors WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }

    /**
     * يجلب جميع الأطباء من قاعدة البيانات.
     * @return ResultSet يحتوي على بيانات الأطباء
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet getAllDoctors() throws SQLException {
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery("SELECT * FROM doctors");
    }

    // ── Appointment Operations ────────────────────────────────────────────────

    /**
     * يجدول موعداً جديداً بعد التحقق من صحة البيانات.
     *
     * @param patientIdStr معرف المريض (نص)
     * @param doctorIdStr  معرف الطبيب (نص)
     * @param date         التاريخ (YYYY-MM-DD)
     * @param time         الوقت (HH:MM)
     * @param notes        ملاحظات
     * @throws HospitalException إذا كانت البيانات غير صالحة
     * @throws SQLException      إذا فشلت عملية قاعدة البيانات
     */
    public void scheduleAppointment(String patientIdStr, String doctorIdStr,
                                    String date, String time, String notes)
            throws HospitalException, SQLException {
        // Business Logic: التحقق من البيانات
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        if (doctorIdStr.isEmpty())  throw new HospitalException("Doctor ID is required.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("IDs must be numbers."); }

        // Data Access: حفظ في قاعدة البيانات
        String sql = "INSERT INTO appointments(patient_id,doctor_id,date,time,status,notes)"
                   + " VALUES(?,?,?,?,'Scheduled',?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setInt(2, did);
        ps.setString(3, date); ps.setString(4, time); ps.setString(5, notes);
        ps.executeUpdate(); ps.close();
    }

    /**
     * يحدث حالة موعد.
     * @param id     معرف الموعد
     * @param status الحالة الجديدة (Completed/Cancelled)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void updateAppointmentStatus(int id, String status) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE appointments SET status=? WHERE appointment_id=?");
        ps.setString(1, status); ps.setInt(2, id);
        ps.executeUpdate(); ps.close();
    }

    // ── Medical Record Operations ─────────────────────────────────────────────

    /**
     * يضيف سجلاً طبياً جديداً بعد التحقق من صحة البيانات.
     *
     * @param patientIdStr معرف المريض
     * @param doctorIdStr  معرف الطبيب
     * @param diagnosis    التشخيص
     * @param treatment    العلاج
     * @param medications  الأدوية
     * @param notes        ملاحظات
     * @throws HospitalException إذا كانت البيانات غير صالحة
     * @throws SQLException      إذا فشلت عملية قاعدة البيانات
     */
    public void addRecord(String patientIdStr, String doctorIdStr, String diagnosis,
                          String treatment, String medications, String notes)
            throws HospitalException, SQLException {
        // Business Logic: التحقق من البيانات
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        if (diagnosis.isEmpty())    throw new HospitalException("Diagnosis cannot be empty.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("IDs must be numbers."); }

        // Data Access: حفظ في قاعدة البيانات
        String sql = "INSERT INTO medical_records(patient_id,doctor_id,date,diagnosis,treatment,medications,notes)"
                   + " VALUES(?,?,date('now'),?,?,?,?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setInt(2, did);
        ps.setString(3, diagnosis); ps.setString(4, treatment);
        ps.setString(5, medications); ps.setString(6, notes);
        ps.executeUpdate(); ps.close();
    }

    // ── Bill Operations ───────────────────────────────────────────────────────

    /**
     * يولد فاتورة جديدة للمريض بعد التحقق من صحة البيانات.
     *
     * @param patientIdStr معرف المريض
     * @param consultation رسوم الكشف
     * @param medication   رسوم الأدوية
     * @param lab          رسوم التحاليل
     * @param room         رسوم الغرفة
     * @throws HospitalException إذا كانت البيانات غير صالحة
     * @throws SQLException      إذا فشلت عملية قاعدة البيانات
     */
    public void generateBill(String patientIdStr, String consultation,
                             String medication, String lab, String room)
            throws HospitalException, SQLException {
        // Business Logic: التحقق من البيانات
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        int pid;
        try { pid = Integer.parseInt(patientIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("Patient ID must be a number."); }
        double c, m, l, r;
        try {
            c = Double.parseDouble(consultation); m = Double.parseDouble(medication);
            l = Double.parseDouble(lab);          r = Double.parseDouble(room);
        } catch (NumberFormatException e) { throw new HospitalException("Fees must be numbers."); }

        // Business Logic: حساب الإجمالي والتحقق منه
        double total = c + m + l + r;
        if (total < 0) throw new HospitalException("Total bill cannot be negative.");

        // Data Access: حفظ في قاعدة البيانات
        String sql = "INSERT INTO bills(patient_id,bill_date,consultation_fee,medication_fee,lab_fee,room_fee,payment_status)"
                   + " VALUES(?,date('now'),?,?,?,?,'Unpaid')";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setDouble(2, c); ps.setDouble(3, m);
        ps.setDouble(4, l); ps.setDouble(5, r);
        ps.executeUpdate(); ps.close();
    }

    /**
     * يعلم فاتورة كمدفوعة.
     * @param id معرف الفاتورة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void markBillPaid(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bills SET payment_status='Paid' WHERE bill_id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }

    // ── Statistics Operations ─────────────────────────────────────────────────

    /**
     * يحسب عدد السجلات في جدول معين.
     * @param tableName اسم الجدول
     * @return عدد السجلات
     */
    public int getCount(String tableName) {
        try {
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName);
            int count = rs.getInt(1);
            rs.close(); st.close();
            return count;
        } catch (SQLException e) { return 0; }
    }

    /**
     * يحسب إجمالي الإيرادات من الفواتير المدفوعة.
     * @return إجمالي الإيرادات
     */
    public double getTotalRevenue() {
        try {
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT SUM(consultation_fee+medication_fee+lab_fee+room_fee) FROM bills WHERE payment_status='Paid'");
            double total = rs.getDouble(1);
            rs.close(); st.close();
            return total;
        } catch (SQLException e) { return 0; }
    }
}
