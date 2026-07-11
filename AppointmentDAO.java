package hospital.persistence;

import hospital.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * طبقة الوصول للبيانات (Persistence Layer) الخاصة بكيان Appointment.
 * يحتوي فقط على SQL الخاص بجدول {@code appointments}.
 *
 * @author Student
 * @version 1.0
 */
public class AppointmentDAO {

    /**
     * يضيف موعداً جديداً بحالة "Scheduled".
     * @param patientId معرف المريض
     * @param doctorId معرف الطبيب
     * @param date التاريخ
     * @param time الوقت
     * @param notes ملاحظات
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void insert(int patientId, int doctorId, String date, String time, String notes) throws SQLException {
        String sql = "INSERT INTO appointments(patient_id,doctor_id,date,time,status,notes)"
                   + " VALUES(?,?,?,?,'Scheduled',?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, patientId); ps.setInt(2, doctorId);
            ps.setString(3, date); ps.setString(4, time); ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب موعداً واحداً بالمعرف (لاستخدامه بنموذج التعديل).
     * @param id معرف الموعد
     * @return ResultSet يحتوي على صف الموعد (لو موجود)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findById(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM appointments WHERE appointment_id=?");
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    /**
     * يحدّث تاريخ/وقت/ملاحظات موعد موجود (بدون تغيير المريض أو الطبيب أو الحالة).
     * @param id معرف الموعد
     * @param date التاريخ الجديد
     * @param time الوقت الجديد
     * @param notes الملاحظات الجديدة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void update(int id, String date, String time, String notes) throws SQLException {
        String sql = "UPDATE appointments SET date=?, time=?, notes=? WHERE appointment_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, date); ps.setString(2, time); ps.setString(3, notes);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    /**
     * يحدث حالة موعد.
     * @param id معرف الموعد
     * @param status الحالة الجديدة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void updateStatus(int id, String status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE appointments SET status=? WHERE appointment_id=?")) {
            ps.setString(1, status); ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب جميع المواعيد مع أسماء المرضى والأطباء (JOIN).
     * @return ResultSet يحتوي على بيانات المواعيد
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findAllJoined() throws SQLException {
        String sql = "SELECT a.appointment_id, p.name, d.name, a.date, a.time, a.status, a.notes "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id = p.id "
                   + "JOIN doctors  d ON a.doctor_id  = d.id";
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery(sql);
    }
}
