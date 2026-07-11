package hospital.ui;

import hospital.db.DatabaseConnection;
import hospital.exception.HospitalException;
import hospital.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * Main GUI for the Hospital Management System.
 *
 * @author Student
 * @version 1.0
 */
public class HospitalGUI extends JFrame {

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
     * Constructs the main hospital GUI window.
     */
    public HospitalGUI() {
        setTitle("Hospital Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            DatabaseConnection.initializeDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard",      new DashboardPanel());
        tabs.addTab("Patients",       buildPatientPanel());
        tabs.addTab("Doctors",        buildDoctorPanel());
        tabs.addTab("Appointments",   buildAppointmentPanel());
        tabs.addTab("Medical Records",buildRecordPanel());
        tabs.addTab("Billing",        buildBillPanel());
        add(tabs);
    }

    // ── PATIENT PANEL ─────────────────────────────────────────────────────────
    private JPanel buildPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fields
        JTextField fName     = new JTextField(15);
        JTextField fAge      = new JTextField(5);
        JTextField fPhone    = new JTextField(15);
        JTextField fEmail    = new JTextField(15);
        JTextField fBlood    = new JTextField(5);
        JTextField fAllergy  = new JTextField(15);
        JComboBox<String> fStatus = new JComboBox<>(new String[]{"Outpatient","Inpatient"});

        // Form using GridBagLayout for clean label-field pairs
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Patient"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        // Row 1: Name | Age
        g.gridx=0; g.gridy=0; form.add(new JLabel("Name:"), g);
        g.gridx=1; form.add(fName, g);
        g.gridx=2; form.add(new JLabel("Age:"), g);
        g.gridx=3; form.add(fAge, g);

        // Row 2: Phone | Blood Type
        g.gridx=0; g.gridy=1; form.add(new JLabel("Phone:"), g);
        g.gridx=1; form.add(fPhone, g);
        g.gridx=2; form.add(new JLabel("Blood Type:"), g);
        g.gridx=3; form.add(fBlood, g);

        // Row 3: Email | Allergies
        g.gridx=0; g.gridy=2; form.add(new JLabel("Email:"), g);
        g.gridx=1; form.add(fEmail, g);
        g.gridx=2; form.add(new JLabel("Allergies:"), g);
        g.gridx=3; form.add(fAllergy, g);

        // Row 4: Status
        g.gridx=0; g.gridy=3; form.add(new JLabel("Status:"), g);
        g.gridx=1; form.add(fStatus, g);

        // Table
        patientTableModel = new DefaultTableModel(
                new String[]{"ID","Name","Age","Phone","Blood","Status","Room"}, 0);
        patientTable = new JTable(patientTableModel);
        loadPatients();

        // Buttons
        JButton btnAdd    = new JButton("Add Patient");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh= new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            try {
                addPatient(fName.getText().trim(), fAge.getText().trim(),
                        fPhone.getText().trim(), fEmail.getText().trim(),
                        fBlood.getText().trim(), fAllergy.getText().trim(),
                        (String) fStatus.getSelectedItem());
                fName.setText(""); fAge.setText(""); fPhone.setText("");
                fEmail.setText(""); fBlood.setText(""); fAllergy.setText("");
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) patientTableModel.getValueAt(row, 0);
                try { deletePatient(id); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadPatients());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── DOCTOR PANEL ──────────────────────────────────────────────────────────
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
        form.setBorder(BorderFactory.createTitledBorder("Add New Doctor"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("Name:"), g);
        g.gridx=1; form.add(fName, g);
        g.gridx=2; form.add(new JLabel("Age:"), g);
        g.gridx=3; form.add(fAge, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Phone:"), g);
        g.gridx=1; form.add(fPhone, g);
        g.gridx=2; form.add(new JLabel("Email:"), g);
        g.gridx=3; form.add(fEmail, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("Specialty:"), g);
        g.gridx=1; form.add(fSpec, g);
        g.gridx=2; form.add(new JLabel("License#:"), g);
        g.gridx=3; form.add(fLicense, g);

        doctorTableModel = new DefaultTableModel(
                new String[]{"ID","Name","Age","Specialty","License","Available"}, 0);
        doctorTable = new JTable(doctorTableModel);
        loadDoctors();

        JButton btnAdd    = new JButton("Add Doctor");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh= new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            try {
                addDoctor(fName.getText().trim(), fAge.getText().trim(),
                        fPhone.getText().trim(), fEmail.getText().trim(),
                        fSpec.getText().trim(), fLicense.getText().trim());
                fName.setText(""); fAge.setText(""); fPhone.setText("");
                fEmail.setText(""); fSpec.setText(""); fLicense.setText("");
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) doctorTableModel.getValueAt(row, 0);
                try { deleteDoctor(id); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadDoctors());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── APPOINTMENT PANEL ─────────────────────────────────────────────────────
    private JPanel buildAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fPatientId = new JTextField(5);
        JTextField fDoctorId  = new JTextField(5);
        JTextField fDate      = new JTextField(LocalDate.now().toString(), 10);
        JTextField fTime      = new JTextField("09:00", 8);
        JTextField fNotes     = new JTextField(20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Schedule Appointment"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("Patient ID:"), g);
        g.gridx=1; form.add(fPatientId, g);
        g.gridx=2; form.add(new JLabel("Doctor ID:"), g);
        g.gridx=3; form.add(fDoctorId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Date (YYYY-MM-DD):"), g);
        g.gridx=1; form.add(fDate, g);
        g.gridx=2; form.add(new JLabel("Time (HH:MM):"), g);
        g.gridx=3; form.add(fTime, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("Notes:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fNotes, g); g.gridwidth=1;

        appointmentTableModel = new DefaultTableModel(
                new String[]{"ID","Patient","Doctor","Date","Time","Status","Notes"}, 0);
        appointmentTable = new JTable(appointmentTableModel);
        loadAppointments();

        JButton btnAdd      = new JButton("Schedule");
        JButton btnCancel   = new JButton("Cancel");
        JButton btnComplete = new JButton("Complete");
        JButton btnRefresh  = new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            try {
                scheduleAppointment(fPatientId.getText().trim(), fDoctorId.getText().trim(),
                        fDate.getText().trim(), fTime.getText().trim(), fNotes.getText().trim());
                fPatientId.setText(""); fDoctorId.setText(""); fNotes.setText("");
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) appointmentTableModel.getValueAt(row, 0);
                try { updateAppointmentStatus(id, "Cancelled"); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnComplete.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) appointmentTableModel.getValueAt(row, 0);
                try { updateAppointmentStatus(id, "Completed"); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnRefresh.addActionListener(e -> loadAppointments());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnCancel);
        btnPanel.add(btnComplete); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── MEDICAL RECORD PANEL ──────────────────────────────────────────────────
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
        form.setBorder(BorderFactory.createTitledBorder("Add Medical Record"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("Patient ID:"), g);
        g.gridx=1; form.add(fPatientId, g);
        g.gridx=2; form.add(new JLabel("Doctor ID:"), g);
        g.gridx=3; form.add(fDoctorId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Diagnosis:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fDiagnosis, g); g.gridwidth=1;

        g.gridx=0; g.gridy=2; form.add(new JLabel("Treatment:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fTreatment, g); g.gridwidth=1;

        g.gridx=0; g.gridy=3; form.add(new JLabel("Medications:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fMedications, g); g.gridwidth=1;

        g.gridx=0; g.gridy=4; form.add(new JLabel("Notes:"), g);
        g.gridx=1; g.gridwidth=3; form.add(fNotes, g); g.gridwidth=1;

        recordTableModel = new DefaultTableModel(
                new String[]{"ID","Patient","Doctor","Date","Diagnosis","Treatment","Medications"}, 0);
        recordTable = new JTable(recordTableModel);
        loadRecords();

        JButton btnAdd    = new JButton("Add Record");
        JButton btnRefresh= new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            try {
                addRecord(fPatientId.getText().trim(), fDoctorId.getText().trim(),
                        fDiagnosis.getText().trim(), fTreatment.getText().trim(),
                        fMedications.getText().trim(), fNotes.getText().trim());
                fPatientId.setText(""); fDoctorId.setText(""); fDiagnosis.setText("");
                fTreatment.setText(""); fMedications.setText(""); fNotes.setText("");
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> loadRecords());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── BILLING PANEL ─────────────────────────────────────────────────────────
    private JPanel buildBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fPatientId    = new JTextField(5);
        JTextField fConsultation = new JTextField("0.0", 8);
        JTextField fMedication   = new JTextField("0.0", 8);
        JTextField fLab          = new JTextField("0.0", 8);
        JTextField fRoom         = new JTextField("0.0", 8);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Generate Bill"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("Patient ID:"), g);
        g.gridx=1; form.add(fPatientId, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Consultation Fee:"), g);
        g.gridx=1; form.add(fConsultation, g);
        g.gridx=2; form.add(new JLabel("Medication Fee:"), g);
        g.gridx=3; form.add(fMedication, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("Lab Fee:"), g);
        g.gridx=1; form.add(fLab, g);
        g.gridx=2; form.add(new JLabel("Room Fee:"), g);
        g.gridx=3; form.add(fRoom, g);

        billTableModel = new DefaultTableModel(
                new String[]{"ID","Patient","Date","Consultation","Medication","Lab","Room","Total","Status"}, 0);
        billTable = new JTable(billTableModel);
        loadBills();

        JButton btnGenerate = new JButton("Generate Bill");
        JButton btnPay      = new JButton("Mark as Paid");
        JButton btnReceipt  = new JButton("View Receipt");
        JButton btnRefresh  = new JButton("Refresh");

        btnGenerate.addActionListener(e -> {
            try {
                generateBill(fPatientId.getText().trim(), fConsultation.getText().trim(),
                        fMedication.getText().trim(), fLab.getText().trim(), fRoom.getText().trim());
                fPatientId.setText("");
            } catch (HospitalException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPay.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) billTableModel.getValueAt(row, 0);
                try { markBillPaid(id); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnReceipt.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row >= 0) {
                String receipt = "Bill ID: " + billTableModel.getValueAt(row, 0)
                        + "\nPatient: " + billTableModel.getValueAt(row, 1)
                        + "\nDate: " + billTableModel.getValueAt(row, 2)
                        + "\nConsultation: $" + billTableModel.getValueAt(row, 3)
                        + "\nMedication: $" + billTableModel.getValueAt(row, 4)
                        + "\nLab: $" + billTableModel.getValueAt(row, 5)
                        + "\nRoom: $" + billTableModel.getValueAt(row, 6)
                        + "\n-----------------"
                        + "\nTOTAL: $" + billTableModel.getValueAt(row, 7)
                        + "\nStatus: " + billTableModel.getValueAt(row, 8);
                JOptionPane.showMessageDialog(this, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> loadBills());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnGenerate); btnPanel.add(btnPay);
        btnPanel.add(btnReceipt); btnPanel.add(btnRefresh);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── DATABASE OPERATIONS ───────────────────────────────────────────────────

    private void addPatient(String name, String age, String phone, String email,
                            String blood, String allergies, String status)
            throws HospitalException, SQLException {
        if (name.isEmpty()) throw new HospitalException("Patient name cannot be empty.");
        if (age.isEmpty())  throw new HospitalException("Age cannot be empty.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("Age must be a number."); }

        String sql = "INSERT INTO patients(name,age,phone,email,blood_type,allergies,status,room_number)"
                   + " VALUES(?,?,?,?,?,?,?,'N/A')";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, name); ps.setInt(2, ageInt);
        ps.setString(3, phone); ps.setString(4, email);
        ps.setString(5, blood); ps.setString(6, allergies);
        ps.setString(7, status);
        ps.executeUpdate(); ps.close();
        loadPatients();
        JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deletePatient(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM patients WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
        loadPatients();
    }

    private void loadPatients() {
        patientTableModel.setRowCount(0);
        try {
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM patients");
            while (rs.next()) {
                patientTableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                    rs.getString("phone"), rs.getString("blood_type"),
                    rs.getString("status"), rs.getString("room_number")
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void addDoctor(String name, String age, String phone, String email,
                           String specialty, String license)
            throws HospitalException, SQLException {
        if (name.isEmpty())    throw new HospitalException("Doctor name cannot be empty.");
        if (license.isEmpty()) throw new HospitalException("License number cannot be empty.");
        int ageInt;
        try { ageInt = Integer.parseInt(age); }
        catch (NumberFormatException e) { throw new HospitalException("Age must be a number."); }

        String sql = "INSERT INTO doctors(name,age,phone,email,specialty,license_number,available)"
                   + " VALUES(?,?,?,?,?,?,1)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, name); ps.setInt(2, ageInt);
        ps.setString(3, phone); ps.setString(4, email);
        ps.setString(5, specialty); ps.setString(6, license);
        ps.executeUpdate(); ps.close();
        loadDoctors();
        JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteDoctor(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM doctors WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
        loadDoctors();
    }

    private void loadDoctors() {
        doctorTableModel.setRowCount(0);
        try {
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM doctors");
            while (rs.next()) {
                doctorTableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                    rs.getString("specialty"), rs.getString("license_number"),
                    rs.getInt("available") == 1 ? "Yes" : "No"
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void scheduleAppointment(String patientIdStr, String doctorIdStr,
                                     String date, String time, String notes)
            throws HospitalException, SQLException {
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        if (doctorIdStr.isEmpty())  throw new HospitalException("Doctor ID is required.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("IDs must be numbers."); }

        String sql = "INSERT INTO appointments(patient_id,doctor_id,date,time,status,notes)"
                   + " VALUES(?,?,?,?,'Scheduled',?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setInt(2, did);
        ps.setString(3, date); ps.setString(4, time); ps.setString(5, notes);
        ps.executeUpdate(); ps.close();
        loadAppointments();
    }

    private void updateAppointmentStatus(int id, String status) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE appointments SET status=? WHERE appointment_id=?");
        ps.setString(1, status); ps.setInt(2, id);
        ps.executeUpdate(); ps.close();
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        try {
            String sql = "SELECT a.appointment_id, p.name, d.name, a.date, a.time, a.status, a.notes "
                       + "FROM appointments a "
                       + "JOIN patients p ON a.patient_id = p.id "
                       + "JOIN doctors  d ON a.doctor_id  = d.id";
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                appointmentTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), "Dr. " + rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void addRecord(String patientIdStr, String doctorIdStr, String diagnosis,
                           String treatment, String medications, String notes)
            throws HospitalException, SQLException {
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        if (diagnosis.isEmpty())    throw new HospitalException("Diagnosis cannot be empty.");
        int pid, did;
        try { pid = Integer.parseInt(patientIdStr); did = Integer.parseInt(doctorIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("IDs must be numbers."); }

        String sql = "INSERT INTO medical_records(patient_id,doctor_id,date,diagnosis,treatment,medications,notes)"
                   + " VALUES(?,?,date('now'),?,?,?,?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setInt(2, did);
        ps.setString(3, diagnosis); ps.setString(4, treatment);
        ps.setString(5, medications); ps.setString(6, notes);
        ps.executeUpdate(); ps.close();
        loadRecords();
    }

    private void loadRecords() {
        recordTableModel.setRowCount(0);
        try {
            String sql = "SELECT r.record_id, p.name, d.name, r.date, r.diagnosis, r.treatment, r.medications "
                       + "FROM medical_records r "
                       + "JOIN patients p ON r.patient_id = p.id "
                       + "JOIN doctors  d ON r.doctor_id  = d.id";
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                recordTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), "Dr. " + rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void generateBill(String patientIdStr, String consultation,
                               String medication, String lab, String room)
            throws HospitalException, SQLException {
        if (patientIdStr.isEmpty()) throw new HospitalException("Patient ID is required.");
        int pid;
        try { pid = Integer.parseInt(patientIdStr); }
        catch (NumberFormatException e) { throw new HospitalException("Patient ID must be a number."); }
        double c, m, l, r;
        try {
            c = Double.parseDouble(consultation); m = Double.parseDouble(medication);
            l = Double.parseDouble(lab);          r = Double.parseDouble(room);
        } catch (NumberFormatException e) { throw new HospitalException("Fees must be numbers."); }

        String sql = "INSERT INTO bills(patient_id,bill_date,consultation_fee,medication_fee,lab_fee,room_fee,payment_status)"
                   + " VALUES(?,date('now'),?,?,?,?,'Unpaid')";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, pid); ps.setDouble(2, c); ps.setDouble(3, m);
        ps.setDouble(4, l); ps.setDouble(5, r);
        ps.executeUpdate(); ps.close();
        loadBills();
    }

    private void markBillPaid(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bills SET payment_status='Paid' WHERE bill_id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
        loadBills();
    }

    private void loadBills() {
        billTableModel.setRowCount(0);
        try {
            String sql = "SELECT b.bill_id, p.name, b.bill_date, b.consultation_fee, "
                       + "b.medication_fee, b.lab_fee, b.room_fee, "
                       + "(b.consultation_fee+b.medication_fee+b.lab_fee+b.room_fee) AS total, "
                       + "b.payment_status "
                       + "FROM bills b JOIN patients p ON b.patient_id = p.id";
            Statement st = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                billTableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getDouble(4), rs.getDouble(5), rs.getDouble(6),
                    rs.getDouble(7), rs.getDouble(8), rs.getString(9)
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    /**
     * Application entry point.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalGUI::new);
    }
}
