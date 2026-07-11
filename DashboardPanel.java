package hospital.ui;

import hospital.service.HospitalService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * لوحة التحكم (Dashboard) — طبقة العرض (Presentation Layer).
 * تعرض إحصائيات حقيقية ورسم بياني دائري للمواعيد.
 *
 * @author Student
 * @version 3.0
 */
public class DashboardPanel extends JPanel {

    private final HospitalService hospitalService = new HospitalService();

    /** يترجم اسم حالة الموعد المخزّن بالإنجليزي إلى عربي للعرض فقط. */
    private static String ar(String status) {
        if (status == null) return "";
        switch (status) {
            case "Scheduled": return "مجدول";
            case "Completed": return "مكتمل";
            case "Cancelled": return "ملغى";
            default: return status;
        }
    }

    private JLabel patientsValue;
    private JLabel doctorsValue;
    private JLabel revenueValue;
    private PieChartPanel chartPanel;

    /**
     * ينشئ لوحة التحكم ويحمل الإحصائيات.
     */
    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        add(buildStatCards(), BorderLayout.NORTH);

        chartPanel = new PieChartPanel("توزيع المواعيد حسب الحالة");
        add(chartPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("🔄 تحديث الإحصائيات");
        refreshBtn.setBackground(new Color(0, 70, 127));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
        refreshBtn.addActionListener(e -> loadStatistics());
        JPanel south = new JPanel();
        south.setBackground(new Color(245, 245, 245));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        loadStatistics();
    }

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));
        row.setBackground(new Color(245, 245, 245));

        patientsValue = new JLabel("0", SwingConstants.CENTER);
        doctorsValue  = new JLabel("0", SwingConstants.CENTER);
        revenueValue  = new JLabel("$0.00", SwingConstants.CENTER);

        row.add(buildCard("إجمالي المرضى",    patientsValue, new Color(52, 152, 219)));
        row.add(buildCard("إجمالي الأطباء",    doctorsValue,  new Color(46, 204, 113)));
        row.add(buildCard("إجمالي الإيرادات", revenueValue,  new Color(230, 126, 34)));

        return row;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * يحدث جميع الإحصائيات من قاعدة البيانات عبر HospitalService.
     */
    public void loadStatistics() {
        patientsValue.setText(String.valueOf(hospitalService.getCount("patients")));
        doctorsValue.setText(String.valueOf(hospitalService.getCount("doctors")));
        revenueValue.setText(String.format("$%.2f", hospitalService.getTotalRevenue()));

        Map<String, Integer> statusCounts = hospitalService.getAppointmentStatusCounts();
        Map<String, Integer> arabicCounts = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            arabicCounts.put(ar(entry.getKey()), entry.getValue());
        }
        chartPanel.setData(arabicCounts);
    }

    // ── Pie Chart ─────────────────────────────────────────────────────────────

    /**
     * رسم بياني دائري يعرض توزيع المواعيد.
     */
    private static class PieChartPanel extends JPanel {

        private Map<String, Integer> data = new LinkedHashMap<>();
        private final String title;

        private static final Color[] COLORS = {
            new Color(52, 152, 219),   // أزرق - Scheduled
            new Color(46, 204, 113),   // أخضر - Completed
            new Color(231, 76, 60)     // أحمر - Cancelled
        };

        PieChartPanel(String title) {
            this.title = title;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title,
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(0, 70, 127)
            ));
        }

        void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) {
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.setColor(Color.GRAY);
                g2.drawString("لا توجد مواعيد", getWidth()/2 - 60, getHeight()/2);
                return;
            }

            // رسم الـ Pie Chart
            int size   = Math.min(getWidth(), getHeight()) - 120;
            int x      = (getWidth() - size) / 2 - 60;
            int y      = (getHeight() - size) / 2;
            double startAngle = 0;
            int i = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double arc = 360.0 * entry.getValue() / total;
                g2.setColor(COLORS[i % COLORS.length]);
                g2.fill(new Arc2D.Double(x, y, size, size, startAngle, arc, Arc2D.PIE));

                // حدود بيضاء بين الأقسام
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Arc2D.Double(x, y, size, size, startAngle, arc, Arc2D.PIE));

                startAngle += arc;
                i++;
            }

            // Legend على اليمين
            int legendX = x + size + 30;
            int legendY = getHeight() / 2 - (data.size() * 35) / 2;
            i = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                // مربع اللون
                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillRoundRect(legendX, legendY + i * 35, 20, 20, 5, 5);

                // النسبة والاسم
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                int pct = (int) Math.round(100.0 * entry.getValue() / total);
                g2.drawString(entry.getKey() + "  " + entry.getValue() + "  (" + pct + "%)",
                        legendX + 28, legendY + i * 35 + 15);
                i++;
            }

            // الإجمالي في المنتصف
            g2.setColor(new Color(50, 50, 50));
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            String totalStr = "الإجمالي: " + total;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totalStr, x + (size - fm.stringWidth(totalStr)) / 2,
                    y + size + 25);
        }
    }
}
