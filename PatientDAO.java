package hospital.persistence;

import hospital.db.DatabaseConnection;
import hospital.model.Patient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * طبقة الوصول للبيانات (Persistence Layer) الخاصة بكيان Patient.
 *
 * <p>هذا الكلاس هو المكان الوحيد الذي يحتوي على SQL الخاص بجدول
 * {@code patients}. لا يحتوي على أي تحقق من صحة البيانات (Business Logic) —
 * تلك مسؤولية طبقة Application (hospital.service.HospitalService).</p>
 *
 * @author Student
 * @version 1.0
 */
public class PatientDAO {

    /**
     * يحفظ كائن مريض جديد في قاعدة البيانات.
     * @param patient كائن المريض (تم بناؤه مسبقاً في طبقة Application عبر PersonFactory)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void insert(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients(name,age,phone,email,blood_type,allergies,status,room_number)"
                   + " VALUES(?,?,?,?,?,?,?,'N/A')";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getEmail());
            ps.setString(5, patient.getBloodType());
            ps.setString(6, patient.getAllergies());
            ps.setString(7, patient.getStatus());
            ps.executeUpdate();
        }
    }

    /**
     * يحذف مريضاً بالمعرف.
     * @param id معرف المريض
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM patients WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب مريضاً واحداً بالمعرف (لاستخدامه بنموذج التعديل).
     * @param id معرف المريض
     * @return ResultSet يحتوي على صف المريض (لو موجود)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findById(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM patients WHERE id=?");
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    /**
     * يحدّث بيانات مريض موجود.
     * @param id معرف المريض
     * @param patient كائن يحمل القيم الجديدة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void update(int id, Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name=?, age=?, phone=?, email=?, "
                   + "blood_type=?, allergies=?, status=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getEmail());
            ps.setString(5, patient.getBloodType());
            ps.setString(6, patient.getAllergies());
            ps.setString(7, patient.getStatus());
            ps.setInt(8, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب جميع المرضى من قاعدة البيانات.
     * @return ResultSet يحتوي على صفوف جدول patients
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findAll() throws SQLException {
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery("SELECT * FROM patients");
    }
}
