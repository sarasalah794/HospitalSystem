package hospital.ui;

import hospital.db.DatabaseConnection;
import hospital.exception.HospitalException;
import hospital.service.HospitalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * الواجهة الرئيسية لنظام إدارة المستشفى (طبقة العرض - Presentation Layer).
 *
 * <p>هذه الواجهة لا تحتوي على أي SQL مباشر: كل العمليات (إضافة/حذف/عرض)
 * تمر عبر {@link HospitalService} (طبقة Application)، والتي بدورها تستخدم
 * DAOs (طبقة Persistence) وتبني الكائنات عبر {@code PersonFactory} (Domain).</p>
 *
 * @author Student
 * @version 2.0
 */
public class HospitalGUI extends JFrame {

    private final HospitalService hospitalService = new HospitalService();

    private DefaultTableModel patientTableModel;
    private DefaultTableModel doctorTableModel;
    private DefaultTableModel appointmentTableModel;
    private DefaultTableModel recordTableModel;
    private DefaultTableModel billTableModel;

    private JTable patientTable;
    private JTable doctorTable;
    private JTable appointmentTable;
    private JTable recordTable;
    private JTable billTable;

    /**
     * ينشئ النافذة الرئيسية لنظام إدارة المستشفى.
     */
    public HospitalGUI() {
        setTitle("نظام إدارة المستشفى");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        try {
            DatabaseConnection.initializeDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "خطأ في قاعدة البيانات: " + e.getMessage(),
                    "خطأ بقاعدة البيانات", JOptionPane.ERROR_MESSAGE);
        }

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tabs.addTab("لوحة التحكم",   new DashboardPanel());
        tabs.addTab("المرضى",       buildPatientPanel());
        tabs.addTab("الأطباء",      buildDoctorPanel());
        tabs.addTab("المواعيد",     buildAppointmentPanel());
        tabs.addTab("السجلات الطبية", buildRecordPanel());
        tabs.addTab("الفواتير",     buildBillPanel());
        tabs.addTab("التقارير",     new ReportsPanel());
        add(tabs);
    }

    // ── ترجمة القيم الداخلية للعرض بالعربي (القيم بقاعدة البيانات تبقى إنجليزي) ──
    private static String ar(String status) {
        if (status == null) return "";
        switch (status) {
            case "Outpatient": return "مريض خارجي";
            case "Inpatient":  return "مريض داخلي";
            case "Scheduled":  return "مجدول";
            case "Completed":  return "مكتمل";
            case "Cancelled":  return "ملغى";
            case "Unpaid":     return "غير مدفوعة";
            case "Paid":       return "مدفوعة";
            default: return status;
        }
    }

    // ── لوحة المرضى ───────────────────────────────────────────────────────────
    private JPanel buildPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fName    = new JTextField(15);
        JTextField fAge     = new JTextField(5);
        JTextField fPhone   = new JTextField(15);
        JTextField fEmail   = new JTextField(15);
        JTextField fBlood   = new JTextField(5);
        JTextField fAllergy = new JTextField(15);
        // العرض بالعربي، والقيمة الفعلية المخزنة تبقى بالإنجليزي (Outpatient/Inpatient)
        JComboBox<String> fStatus = new JComboBox<>(new String[]{"مريض خارجي", "مريض داخلي"});

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("إضافة مريض جديد"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.EAST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("الاسم:"), g);
        g.gridx=1; form.add(fName, g);
        g.gridx=2; form.add(new JLabel("العمر:"), g);
        g.gridx=3; form.add(fAge, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("الهاتف:"), g);
        g.gridx=1; form.add(fPhone, g);
        g.gridx=2; form.add(new JLabel("فصيلة الدم:"), g);
        g.gridx=3; form.add(fBlood, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("البريد الإلكتروني:"), g);
        g.gridx=1; form.add(fEmail, g);
        g.gridx=2; form.add(new JLabel("الحساسية:"), g);
        g.gridx=3; form.add(fAllergy, g);

        g.gridx=0; g.gridy=3; form.add(new JLabel("الحالة:"), g);
        g.gridx=1; form.add(fStatus, g);

        patientTableModel = new DefaultTableModel(
                new String[]{"المعرف","الاسم","العمر","الهاتف","فصيلة الدم","الحالة","رقم الغرفة"}, 0);
        patientTable = new JTable(patientTableModel);
        patientTable.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadPatients();

        JButton btnAdd     = new JButton("إضافة مريض");
        JButton btnEdit    = new JButton("تعديل");
        JButton btnDelete  = new JButton("حذف");
        JButton btnRefresh = new JButton("تحديث");

        btnAdd.addActionListener(e -> {
            try {
                String status = "مريض داخلي".equals(fStatus.getSelectedItem()) ? "Inpatient" : "Outpatient";
                hospitalService.addPatient(fName.getText().trim(), fAge.getText().trim(),
                        fPhone.getText().trim(), fEmail.getText().trim(),
                        fBlood.getText().trim(), fAllergy.getText().trim(), status);
                fName.setText(""); fAge.setText(""); fPhone.setText("");
                fEmail.setText(""); fBlood.setText(""); fAllergy.setText("");
                loadPatients();
                JOptionPane.showMessageDialog(this, "تمت إضافة المريض بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> editSelectedPatient());

        btnDelete.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) patientTableModel.getValueAt(row, 0);
                try { hospitalService.deletePatient(id); loadPatients(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadPatients());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── لوحة الأطباء ──────────────────────────────────────────────────────────
    private JPanel buildDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fName    = new JTextField(15);
        JTextField fAge     = new JTextField(5);
        JTextField fPhone   = new JTextField(15);
        JTextField fEmail   = new JTextField(15);
        JTextField fSpec    = new JTextField(15);
        JTextField fLicense = new JTextField(10);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("إضافة طبيب جديد"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.EAST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("الاسم:"), g);
        g.gridx=1; form.add(fName, g);
        g.gridx=2; form.add(new JLabel("العمر:"), g);
        g.gridx=3; form.add(fAge, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("الهاتف:"), g);
        g.gridx=1; form.add(fPhone, g);
        g.gridx=2; form.add(new JLabel("البريد الإلكتروني:"), g);
        g.gridx=3; form.add(fEmail, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("التخصص:"), g);
        g.gridx=1; form.add(fSpec, g);
        g.gridx=2; form.add(new JLabel("رقم الترخيص:"), g);
        g.gridx=3; form.add(fLicense, g);

        doctorTableModel = new DefaultTableModel(
                new String[]{"المعرف","الاسم","العمر","التخصص","رقم الترخيص","متاح"}, 0);
        doctorTable = new JTable(doctorTableModel);
        doctorTable.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadDoctors();

        JButton btnAdd     = new JButton("إضافة طبيب");
        JButton btnEdit    = new JButton("تعديل");
        JButton btnDelete  = new JButton("حذف");
        JButton btnRefresh = new JButton("تحديث");

        btnAdd.addActionListener(e -> {
            try {
                hospitalService.addDoctor(fName.getText().trim(), fAge.getText().trim(),
                        fPhone.getText().trim(), fEmail.getText().trim(),
                        fSpec.getText().trim(), fLicense.getText().trim());
                fName.setText(""); fAge.setText(""); fPhone.setText("");
                fEmail.setText(""); fSpec.setText(""); fLicense.setText("");
                loadDoctors();
                JOptionPane.showMessageDialog(this, "تمت إضافة الطبيب بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> editSelectedDoctor());

        btnDelete.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) doctorTableModel.getValueAt(row, 0);
                try { hospitalService.deleteDoctor(id); loadDoctors(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadDoctors());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── لوحة المواعيد ─────────────────────────────────────────────────────────
    private JPanel buildAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fPatientId = new JTextField(5);
        JTextField fDoctorId  = new JTextField(5);
        JTextField fDate      = new JTextField(LocalDate.now().toString(), 10);
        JTextField fTime      = new JTextField("09:00", 8);
        JTextField fNotes     = new JTextField(20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("جدولة موعد"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.EAST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("معرف المريض:"), g);
        g.gridx=1; form.add(fPatientId, g);
        g.gridx=2; form.add(new JLabel("معرف الطبيب:"), g);
        g.gridx=3; form.add(fDoctorId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("التاريخ (YYYY-MM-DD):"), g);
        g.gridx=1; form.add(fDate, g);
        g.gridx=2; form.add(new JLabel("الوقت (HH:MM):"), g);
        g.gridx=3; form.add(fTime, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("ملاحظات:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fNotes, g); g.gridwidth=1;

        appointmentTableModel = new DefaultTableModel(
                new String[]{"المعرف","المريض","الطبيب","التاريخ","الوقت","الحالة","ملاحظات"}, 0);
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadAppointments();

        JButton btnAdd      = new JButton("جدولة");
        JButton btnEdit     = new JButton("تعديل");
        JButton btnCancel   = new JButton("إلغاء");
        JButton btnComplete = new JButton("إتمام");
        JButton btnRefresh  = new JButton("تحديث");

        btnAdd.addActionListener(e -> {
            try {
                hospitalService.scheduleAppointment(fPatientId.getText().trim(), fDoctorId.getText().trim(),
                        fDate.getText().trim(), fTime.getText().trim(), fNotes.getText().trim());
                fPatientId.setText(""); fDoctorId.setText(""); fNotes.setText("");
                loadAppointments();
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> editSelectedAppointment());

        btnCancel.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) appointmentTableModel.getValueAt(row, 0);
                try { hospitalService.updateAppointmentStatus(id, "Cancelled"); loadAppointments(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnComplete.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) appointmentTableModel.getValueAt(row, 0);
                try { hospitalService.updateAppointmentStatus(id, "Completed"); loadAppointments(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadAppointments());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnCancel);
        btnPanel.add(btnComplete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── لوحة السجلات الطبية ──────────────────────────────────────────────────
    private JPanel buildRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fPatientId   = new JTextField(5);
        JTextField fDoctorId    = new JTextField(5);
        JTextField fDiagnosis   = new JTextField(20);
        JTextField fTreatment   = new JTextField(20);
        JTextField fMedications = new JTextField(20);
        JTextField fNotes       = new JTextField(20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("إضافة سجل طبي"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.EAST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("معرف المريض:"), g);
        g.gridx=1; form.add(fPatientId, g);
        g.gridx=2; form.add(new JLabel("معرف الطبيب:"), g);
        g.gridx=3; form.add(fDoctorId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("التشخيص:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fDiagnosis, g); g.gridwidth=1;

        g.gridx=0; g.gridy=2; form.add(new JLabel("العلاج:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fTreatment, g); g.gridwidth=1;

        g.gridx=0; g.gridy=3; form.add(new JLabel("الأدوية:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fMedications, g); g.gridwidth=1;

        g.gridx=0; g.gridy=4; form.add(new JLabel("ملاحظات:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fNotes, g); g.gridwidth=1;

        recordTableModel = new DefaultTableModel(
                new String[]{"المعرف","المريض","الطبيب","التاريخ","التشخيص","العلاج","الأدوية"}, 0);
        recordTable = new JTable(recordTableModel);
        recordTable.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadRecords();

        JButton btnAdd     = new JButton("إضافة سجل");
        JButton btnEdit    = new JButton("تعديل");
        JButton btnRefresh = new JButton("تحديث");

        btnAdd.addActionListener(e -> {
            try {
                hospitalService.addRecord(fPatientId.getText().trim(), fDoctorId.getText().trim(),
                        fDiagnosis.getText().trim(), fTreatment.getText().trim(),
                        fMedications.getText().trim(), fNotes.getText().trim());
                fPatientId.setText(""); fDoctorId.setText(""); fDiagnosis.setText("");
                fTreatment.setText(""); fMedications.setText(""); fNotes.setText("");
                loadRecords();
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> editSelectedRecord());

        btnRefresh.addActionListener(e -> loadRecords());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── لوحة الفواتير ─────────────────────────────────────────────────────────
    private JPanel buildBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fPatientId    = new JTextField(5);
        JTextField fConsultation = new JTextField("0.0", 8);
        JTextField fMedication   = new JTextField("0.0", 8);
        JTextField fLab          = new JTextField("0.0", 8);
        JTextField fRoom         = new JTextField("0.0", 8);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("إصدار فاتورة"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.EAST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("معرف المريض:"), g);
        g.gridx=1; form.add(fPatientId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("رسوم الكشف:"), g);
        g.gridx=1; form.add(fConsultation, g);
        g.gridx=2; form.add(new JLabel("رسوم الأدوية:"), g);
        g.gridx=3; form.add(fMedication, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("رسوم التحاليل:"), g);
        g.gridx=1; form.add(fLab, g);
        g.gridx=2; form.add(new JLabel("رسوم الغرفة:"), g);
        g.gridx=3; form.add(fRoom, g);

        billTableModel = new DefaultTableModel(
                new String[]{"المعرف","المريض","التاريخ","الكشف","الأدوية","التحاليل","الغرفة","الإجمالي","حالة الدفع"}, 0);
        billTable = new JTable(billTableModel);
        billTable.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadBills();

        JButton btnGenerate = new JButton("إصدار الفاتورة");
        JButton btnEdit      = new JButton("تعديل");
        JButton btnPay      = new JButton("تعليم كمدفوعة");
        JButton btnReceipt  = new JButton("عرض الإيصال");
        JButton btnRefresh  = new JButton("تحديث");

        btnGenerate.addActionListener(e -> {
            try {
                hospitalService.generateBill(fPatientId.getText().trim(), fConsultation.getText().trim(),
                        fMedication.getText().trim(), fLab.getText().trim(), fRoom.getText().trim());
                fPatientId.setText("");
                loadBills();
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> editSelectedBill());

        btnPay.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) billTableModel.getValueAt(row, 0);
                try { hospitalService.markBillPaid(id); loadBills(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnReceipt.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row >= 0) {
                String receipt = "رقم الفاتورة: " + billTableModel.getValueAt(row, 0)
                        + "\nالمريض: " + billTableModel.getValueAt(row, 1)
                        + "\nالتاريخ: " + billTableModel.getValueAt(row, 2)
                        + "\nرسوم الكشف: $" + billTableModel.getValueAt(row, 3)
                        + "\nرسوم الأدوية: $" + billTableModel.getValueAt(row, 4)
                        + "\nرسوم التحاليل: $" + billTableModel.getValueAt(row, 5)
                        + "\nرسوم الغرفة: $" + billTableModel.getValueAt(row, 6)
                        + "\n-----------------"
                        + "\nالإجمالي: $" + billTableModel.getValueAt(row, 7)
                        + "\nالحالة: " + billTableModel.getValueAt(row, 8);
                JOptionPane.showMessageDialog(this, receipt, "الإيصال", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> loadBills());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnGenerate); btnPanel.add(btnEdit); btnPanel.add(btnPay);
        btnPanel.add(btnReceipt); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── نوافذ التعديل (Edit Dialogs) ──────────────────────────────────────────

    /** يعرض نافذة تعديل حقول نصية بسيطة ويرجع القيم الجديدة، أو null لو أُلغي. */
    private String[] showEditFieldsDialog(String title, String[] labels, String[] initialValues) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 6, 6));
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            fields[i] = new JTextField(initialValues[i]);
            panel.add(fields[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        String[] out = new String[labels.length];
        for (int i = 0; i < labels.length; i++) out[i] = fields[i].getText().trim();
        return out;
    }

    /** يعدّل بيانات المريض المحدد بالجدول. */
    private void editSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "اختر مريضًا أولاً من الجدول.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) patientTableModel.getValueAt(row, 0);
        try (ResultSet rs = hospitalService.getPatientById(id)) {
            if (!rs.next()) return;

            JTextField fName    = new JTextField(rs.getString("name"));
            JTextField fAge     = new JTextField(String.valueOf(rs.getInt("age")));
            JTextField fPhone   = new JTextField(rs.getString("phone"));
            JTextField fEmail   = new JTextField(rs.getString("email"));
            JTextField fBlood   = new JTextField(rs.getString("blood_type"));
            JTextField fAllergy = new JTextField(rs.getString("allergies"));
            JComboBox<String> fStatus = new JComboBox<>(new String[]{"مريض خارجي", "مريض داخلي"});
            fStatus.setSelectedIndex("Inpatient".equals(rs.getString("status")) ? 1 : 0);

            JPanel panel = new JPanel(new GridLayout(7, 2, 6, 6));
            panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            panel.add(new JLabel("الاسم:"));            panel.add(fName);
            panel.add(new JLabel("العمر:"));             panel.add(fAge);
            panel.add(new JLabel("الهاتف:"));            panel.add(fPhone);
            panel.add(new JLabel("البريد الإلكتروني:")); panel.add(fEmail);
            panel.add(new JLabel("فصيلة الدم:"));        panel.add(fBlood);
            panel.add(new JLabel("الحساسية:"));          panel.add(fAllergy);
            panel.add(new JLabel("الحالة:"));            panel.add(fStatus);

            int result = JOptionPane.showConfirmDialog(this, panel, "تعديل بيانات المريض",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String status = "مريض داخلي".equals(fStatus.getSelectedItem()) ? "Inpatient" : "Outpatient";
                hospitalService.updatePatient(id, fName.getText().trim(), fAge.getText().trim(),
                        fPhone.getText().trim(), fEmail.getText().trim(),
                        fBlood.getText().trim(), fAllergy.getText().trim(), status);
                loadPatients();
            }
        } catch (HospitalException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** يعدّل بيانات الطبيب المحدد بالجدول. */
    private void editSelectedDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "اختر طبيبًا أولاً من الجدول.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) doctorTableModel.getValueAt(row, 0);
        try (ResultSet rs = hospitalService.getDoctorById(id)) {
            if (!rs.next()) return;
            String[] labels = {"الاسم:", "العمر:", "الهاتف:", "البريد الإلكتروني:", "التخصص:", "رقم الترخيص:"};
            String[] initial = {
                rs.getString("name"), String.valueOf(rs.getInt("age")),
                rs.getString("phone"), rs.getString("email"),
                rs.getString("specialty"), rs.getString("license_number")
            };
            String[] edited = showEditFieldsDialog("تعديل بيانات الطبيب", labels, initial);
            if (edited != null) {
                hospitalService.updateDoctor(id, edited[0], edited[1], edited[2], edited[3], edited[4], edited[5]);
                loadDoctors();
            }
        } catch (HospitalException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** يعدّل تاريخ/وقت/ملاحظات الموعد المحدد بالجدول. */
    private void editSelectedAppointment() {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "اختر موعدًا أولاً من الجدول.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) appointmentTableModel.getValueAt(row, 0);
        try (ResultSet rs = hospitalService.getAppointmentById(id)) {
            if (!rs.next()) return;
            String[] labels = {"التاريخ (YYYY-MM-DD):", "الوقت (HH:MM):", "ملاحظات:"};
            String[] initial = {rs.getString("date"), rs.getString("time"), rs.getString("notes")};
            String[] edited = showEditFieldsDialog("تعديل الموعد", labels, initial);
            if (edited != null) {
                hospitalService.updateAppointment(id, edited[0], edited[1], edited[2]);
                loadAppointments();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** يعدّل بيانات السجل الطبي المحدد بالجدول. */
    private void editSelectedRecord() {
        int row = recordTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "اختر سجلًا أولاً من الجدول.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) recordTableModel.getValueAt(row, 0);
        try (ResultSet rs = hospitalService.getRecordById(id)) {
            if (!rs.next()) return;
            String[] labels = {"التشخيص:", "العلاج:", "الأدوية:", "ملاحظات:"};
            String[] initial = {
                rs.getString("diagnosis"), rs.getString("treatment"),
                rs.getString("medications"), rs.getString("notes")
            };
            String[] edited = showEditFieldsDialog("تعديل السجل الطبي", labels, initial);
            if (edited != null) {
                hospitalService.updateRecord(id, edited[0], edited[1], edited[2], edited[3]);
                loadRecords();
            }
        } catch (HospitalException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** يعدّل رسوم الفاتورة المحددة بالجدول. */
    private void editSelectedBill() {
        int row = billTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "اختر فاتورة أولاً من الجدول.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) billTableModel.getValueAt(row, 0);
        try (ResultSet rs = hospitalService.getBillById(id)) {
            if (!rs.next()) return;
            String[] labels = {"رسوم الكشف:", "رسوم الأدوية:", "رسوم التحاليل:", "رسوم الغرفة:"};
            String[] initial = {
                String.valueOf(rs.getDouble("consultation_fee")),
                String.valueOf(rs.getDouble("medication_fee")),
                String.valueOf(rs.getDouble("lab_fee")),
                String.valueOf(rs.getDouble("room_fee"))
            };
            String[] edited = showEditFieldsDialog("تعديل الفاتورة", labels, initial);
            if (edited != null) {
                hospitalService.updateBill(id, edited[0], edited[1], edited[2], edited[3]);
                loadBills();
            }
        } catch (HospitalException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── تحميل البيانات (عبر HospitalService فقط، بدون أي SQL هنا) ──────────────

    private void loadPatients() {
        patientTableModel.setRowCount(0);
        try (ResultSet rs = hospitalService.getAllPatients()) {
            while (rs.next()) {
                patientTableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                    rs.getString("phone"), rs.getString("blood_type"),
                    ar(rs.getString("status")), rs.getString("room_number")
                });
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void loadDoctors() {
        doctorTableModel.setRowCount(0);
        try (ResultSet rs = hospitalService.getAllDoctors()) {
            while (rs.next()) {
                doctorTableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                    rs.getString("specialty"), rs.getString("license_number"),
                    rs.getInt("available") == 1 ? "نعم" : "لا"
                });
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        try (ResultSet rs = hospitalService.getAllAppointmentsJoined()) {
            while (rs.next()) {
                appointmentTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), "د. " + rs.getString(3),
                    rs.getString(4), rs.getString(5), ar(rs.getString(6)), rs.getString(7)
                });
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void loadRecords() {
        recordTableModel.setRowCount(0);
        try (ResultSet rs = hospitalService.getAllRecordsJoined()) {
            while (rs.next()) {
                recordTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), "د. " + rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                });
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void loadBills() {
        billTableModel.setRowCount(0);
        try (ResultSet rs = hospitalService.getAllBillsJoined()) {
            while (rs.next()) {
                billTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getDouble(4), rs.getDouble(5), rs.getDouble(6),
                    rs.getDouble(7), rs.getDouble(8), ar(rs.getString(9))
                });
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    /**
     * نقطة بداية تشغيل التطبيق.
     * @param args معطيات سطر الأوامر
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalGUI::new);
    }
}
