package hospital.ui;

import hospital.service.HospitalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * لوحة التقارير — طبقة العرض (Presentation Layer).
 * تعرض ثلاثة تقارير: المرضى حسب الحالة، الأطباء حسب التخصص، والملخص المالي.
 * كل تقرير معروض كجدول (JTable) لتفادي مشاكل ترتيب النص عند خلط
 * العربي بالإنجليزي/الأرقام، وكل البيانات تُبنى عبر {@link HospitalService}
 * بدون أي SQL هنا.
 *
 * @author Student
 * @version 2.0
 */
public class ReportsPanel extends JPanel {

    private final HospitalService hospitalService = new HospitalService();

    private DefaultTableModel patientsModel;
    private DefaultTableModel doctorsModel;
    private DefaultTableModel financialModel;

    private JLabel patientsTotalLabel;
    private JLabel doctorsTotalLabel;
    private JLabel financialTotalLabel;

    /**
     * ينشئ لوحة التقارير الثلاثة.
     */
    public ReportsPanel() {
        setLayout(new GridLayout(1, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        patientsModel  = new DefaultTableModel(new String[]{"الحالة", "العدد", "النسبة"}, 0);
        doctorsModel   = new DefaultTableModel(new String[]{"التخصص", "عدد الأطباء"}, 0);
        financialModel = new DefaultTableModel(new String[]{"البند", "القيمة"}, 0);

        add(buildReportCard("تقرير المرضى حسب الحالة", patientsModel,
                patientsTotalLabel = new JLabel(), this::refreshPatientsReport));
        add(buildReportCard("تقرير الأطباء حسب التخصص", doctorsModel,
                doctorsTotalLabel = new JLabel(), this::refreshDoctorsReport));
        add(buildReportCard("التقرير المالي", financialModel,
                financialTotalLabel = new JLabel(), this::refreshFinancialReport));

        refreshPatientsReport();
        refreshDoctorsReport();
        refreshFinancialReport();
    }

    private JPanel buildReportCard(String title, DefaultTableModel model,
                                   JLabel totalLabel, Runnable refreshAction) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(0, 70, 127)));
        card.setBackground(Color.WHITE);

        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setRowHeight(26);
        table.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.getTableHeader().applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        card.add(totalLabel, BorderLayout.NORTH);

        JButton refreshBtn = new JButton("🔄 تحديث التقرير");
        refreshBtn.addActionListener(e -> refreshAction.run());
        JPanel south = new JPanel();
        south.add(refreshBtn);
        card.add(south, BorderLayout.SOUTH);

        return card;
    }

    /** يحدّث تقرير المرضى حسب الحالة. */
    public void refreshPatientsReport() {
        Map<String, Integer> data = hospitalService.getPatientsByStatusReport();
        int total = data.values().stream().mapToInt(Integer::intValue).sum();

        patientsModel.setRowCount(0);
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            String label = "Inpatient".equals(e.getKey()) ? "مريض داخلي" :
                            "Outpatient".equals(e.getKey()) ? "مريض خارجي" : e.getKey();
            int pct = total == 0 ? 0 : (int) Math.round(100.0 * e.getValue() / total);
            patientsModel.addRow(new Object[]{label, e.getValue(), pct + "%"});
        }
        patientsTotalLabel.setText("إجمالي المرضى: " + total);
    }

    /** يحدّث تقرير الأطباء حسب التخصص. */
    public void refreshDoctorsReport() {
        Map<String, Integer> data = hospitalService.getDoctorsBySpecialtyReport();
        int total = data.values().stream().mapToInt(Integer::intValue).sum();

        doctorsModel.setRowCount(0);
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            String specialty = (e.getKey() == null || e.getKey().isEmpty()) ? "غير محدد" : e.getKey();
            doctorsModel.addRow(new Object[]{specialty, e.getValue()});
        }
        doctorsTotalLabel.setText("إجمالي الأطباء: " + total);
    }

    /** يحدّث التقرير المالي. */
    public void refreshFinancialReport() {
        double[] s = hospitalService.getFinancialSummaryReport();
        double paidTotal = s[0]; int paidCount = (int) s[1];
        double unpaidTotal = s[2]; int unpaidCount = (int) s[3];
        double grandTotal = paidTotal + unpaidTotal;

        financialModel.setRowCount(0);
        financialModel.addRow(new Object[]{"عدد الفواتير المدفوعة", paidCount});
        financialModel.addRow(new Object[]{"إجمالي المدفوع", String.format("$%.2f", paidTotal)});
        financialModel.addRow(new Object[]{"عدد الفواتير غير المدفوعة", unpaidCount});
        financialModel.addRow(new Object[]{"إجمالي غير المدفوع", String.format("$%.2f", unpaidTotal)});
        financialTotalLabel.setText(String.format("إجمالي الإيرادات (الكل): $%.2f", grandTotal));
    }
}
