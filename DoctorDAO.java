package hospital.persistence;

import hospital.db.DatabaseConnection;
import hospital.model.Doctor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * طبقة الوصول للبيانات (Persistence Layer) الخاصة بكيان Doctor.
 * يحتوي فقط على SQL الخاص بجدول {@code doctors}، بدون أي تحقق من صحة البيانات.
 *
 * @author Student
 * @version 1.0
 */
public class DoctorDAO {

    /**
     * يحفظ كائن طبيب جديد في قاعدة البيانات.
     * @param doctor كائن الطبيب (تم بناؤه مسبقاً في طبقة Application عبر PersonFactory)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void insert(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors(name,age,phone,email,specialty,license_number,available)"
                   + " VALUES(?,?,?,?,?,?,1)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setInt(2, doctor.getAge());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            ps.setString(5, doctor.getSpecialty());
            ps.setString(6, doctor.getLicenseNumber());
            ps.executeUpdate();
        }
    }

    /**
     * يحذف طبيباً بالمعرف.
     * @param id معرف الطبيب
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM doctors WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب طبيباً واحداً بالمعرف (لاستخدامه بنموذج التعديل).
     * @param id معرف الطبيب
     * @return ResultSet يحتوي على صف الطبيب (لو موجود)
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findById(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM doctors WHERE id=?");
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    /**
     * يحدّث بيانات طبيب موجود.
     * @param id معرف الطبيب
     * @param doctor كائن يحمل القيم الجديدة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void update(int id, Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET name=?, age=?, phone=?, email=?, "
                   + "specialty=?, license_number=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setInt(2, doctor.getAge());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            ps.setString(5, doctor.getSpecialty());
            ps.setString(6, doctor.getLicenseNumber());
            ps.setInt(7, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب جميع الأطباء من قاعدة البيانات.
     * @return ResultSet يحتوي على صفوف جدول doctors
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findAll() throws SQLException {
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery("SELECT * FROM doctors");
    }
}
