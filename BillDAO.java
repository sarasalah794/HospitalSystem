package hospital.persistence;

import hospital.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * طبقة الوصول للبيانات (Persistence Layer) الخاصة بكيان Bill.
 * يحتوي فقط على SQL الخاص بجدول {@code bills}.
 *
 * @author Student
 * @version 1.0
 */
public class BillDAO {

    /**
     * يضيف فاتورة جديدة بحالة "Unpaid" وتاريخ اليوم.
     * @param patientId معرف المريض
     * @param consultation رسوم الكشف
     * @param medication رسوم الأدوية
     * @param lab رسوم التحاليل
     * @param room رسوم الغرفة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void insert(int patientId, double consultation, double medication,
                        double lab, double room) throws SQLException {
        String sql = "INSERT INTO bills(patient_id,bill_date,consultation_fee,medication_fee,lab_fee,room_fee,payment_status)"
                   + " VALUES(?,date('now'),?,?,?,?,'Unpaid')";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, patientId); ps.setDouble(2, consultation); ps.setDouble(3, medication);
            ps.setDouble(4, lab); ps.setDouble(5, room);
            ps.executeUpdate();
        }
    }

    /**
     * يعلم فاتورة كمدفوعة.
     * @param id معرف الفاتورة
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public void markPaid(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bills SET payment_status='Paid' WHERE bill_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * يجلب جميع الفواتير مع أسماء المرضى والإجمالي المحسوب (JOIN).
     * @return ResultSet يحتوي على الفواتير
     * @throws SQLException إذا فشلت عملية قاعدة البيانات
     */
    public ResultSet findAllJoined() throws SQLException {
        String sql = "SELECT b.bill_id, p.name, b.bill_date, b.consultation_fee, "
                   + "b.medication_fee, b.lab_fee, b.room_fee, "
                   + "(b.consultation_fee+b.medication_fee+b.lab_fee+b.room_fee) AS total, "
                   + "b.payment_status "
                   + "FROM bills b JOIN patients p ON b.patient_id = p.id";
        Statement st = DatabaseConnection.getConnection().createStatement();
        return st.executeQuery(sql);
    }
}
