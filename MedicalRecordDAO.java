package hospital.persistence;

import hospital.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * طبقة الوصول للبيانات (Persistence Layer) الخاصة بكيان MedicalRecord.
 * يحتوي فقط على SQL الخاص بجدول {@code medical_records}.
 *
 * @author Student
 * @version 1.0
 */
public class MedicalRecordDAO {

    /**
     * يضيف سجلاً طبياً جديداً بتاريخ اليوم.
     * @param patientId معرف المريض
     * @param doctorId معرف الطبيب
     * @param diagnosis التشخيص
     * @param treatment العلاج
     * @param medications الأدوية
     * @param notes ملاحظات
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void insert(int patientId, int doctorId, String diagnosis,
                        String treatment, String medications, String notes) throws SQLException {
        String sql = "INSERT INTO medical_records(patient_id,doctor_id,date,diagnosis,treatment,medications,notes)"
                   + " VALUES(?,?,date('now'),?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, patientId); ps.setInt(2, doctorId);
            ps.setString(3, diagnosis); ps.setString(4, treatment);
            ps.setString(5, medications); ps.setString(6, notes);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب سجلاً طبياً واحداً بالمعرف (لاستخدامه بنموذج التعديل).
     * @param id معرف السجل
     * @return ResultSet يحتوي على صف السجل (لو موجود)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findById(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM medical_records WHERE record_id=?");
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    /**
     * يحدّث بيانات سجل طبي موجود (التشخيص/العلاج/الأدوية/الملاحظات).
     * @param id معرف السجل
     * @param diagnosis التشخيص الجديد
     * @param treatment العلاج الجديد
     * @param medications الأدوية الجديدة
     * @param notes الملاحظات الجديدة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void update(int id, String diagnosis, String treatment,
                        String medications, String notes) throws SQLException {
        String sql = "UPDATE medical_records SET diagnosis=?, treatment=?, medications=?, notes=? "
                   + "WHERE record_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, diagnosis); ps.setString(2, treatment);
            ps.setString(3, medications); ps.setString(4, notes);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب جميع السجلات الطبية مع أسماء المرضى والأطباء (JOIN).
     * @return ResultSet يحتوي على السجلات الطبية
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findAllJoined() throws SQLException {
        String sql = "SELECT r.record_id, p.name, d.name, r.date, r.diagnosis, r.treatment, r.medications "
                   + "FROM medical_records r "
                   + "JOIN patients p ON r.patient_id = p.id "
                   + "JOIN doctors  d ON r.doctor_id  = d.id";
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery(sql);
    }
}
