package hospital.ui;

import hospital.service.HospitalService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * لوحة التحكم (Dashboard) — طبقة العرض (Presentation Layer).
 * تعرض إحصائيات حقيقية ورسم بياني، وتحصل عليها حصراً عبر {@link HospitalService}
 * (طبقة Application)، بدون أي اتصال مباشر بقاعدة البيانات.
 *
 * @author Student
 * @version 3.0
 */
public class DashboardPanel extends JPanel {

    private final HospitalService hospitalService = new HospitalService();

    private JLabel patientsValue;
    private JLabel doctorsValue;
    private JLabel revenueValue;
    private BarChartPanel chartPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildStatCards(), BorderLayout.NORTH);

        chartPanel = new BarChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("تحديث الإحصائيات");
        refreshBtn.addActionListener(e -> loadStatistics());
        JPanel south = new JPanel();
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        loadStatistics();
    }

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));

        patientsValue = new JLabel("0", SwingConstants.CENTER);
        doctorsValue  = new JLabel("0", SwingConstants.CENTER);
        revenueValue  = new JLabel("$0.00", SwingConstants.CENTER);

        row.add(buildCard("إجمالي المرضى", patientsValue, new Color(52, 152, 219)));
        row.add(buildCard("إجمالي الأطباء", doctorsValue, new Color(46, 204, 113)));
        row.add(buildCard("إجمالي الإيرادات", revenueValue, new Color(230, 126, 34)));

        return row;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 26));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Reloads all statistics and the chart via the Application Layer
     * (hospital.service.HospitalService). No SQL lives in this class.
     */
    public void loadStatistics() {
        patientsValue.setText(String.valueOf(hospitalService.getCount("patients")));
        doctorsValue.setText(String.valueOf(hospitalService.getCount("doctors")));
        revenueValue.setText(String.format("$%.2f", hospitalService.getTotalRevenue()));

        Map<String, Integer> statusCounts = hospitalService.getAppointmentStatusCounts();

        // ترجمة أسماء الحالات للعربي لعرضها بالرسم البياني فقط —
        // القيم الخام بقاعدة البيانات تبقى بالإنجليزي (Scheduled/Completed/Cancelled)
        // زي ما هي بالـ Layer 3، الترجمة هنا بس لطبقة العرض (Presentation).
        Map<String, Integer> arabicStatusCounts = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            arabicStatusCounts.put(translateStatus(entry.getKey()), entry.getValue());
        }
        chartPanel.setData(arabicStatusCounts);
    }

    /** يترجم حالة الموعد الخام من قاعدة البيانات لنص عربي للعرض. */
    private static String translateStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "Scheduled": return "مجدول";
            case "Completed": return "مكتمل";
            case "Cancelled": return "ملغى";
            default: return status;
        }
    }

    // ── Inner class: clean navy/teal bar chart with value labels + bottom legend ──
    private static class BarChartPanel extends JPanel {

        private Map<String, Integer> data = new LinkedHashMap<>();

        /** Muted flat-UI palette (matches the reference chart's clean, professional tone). */
        private static final Color[] PALETTE = {
            new Color(41, 128, 185),   // navy blue  - مجدول
            new Color(39, 174, 96),    // green      - مكتمل
            new Color(192, 57, 43)     // deep red   - ملغى
        };

        BarChartPanel() {
            setBackground(Color.WHITE);
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width  = getWidth();
            int height = getHeight();

            int titleH  = 40;
            int legendH = 34;
            int labelH  = 24;   // space for value label above bar
            int axisH   = 26;   // space for category label below bar
            int topPad  = 20;
            int sidePad = 60;

            // Title
            g2.setColor(new Color(40, 40, 40));
            g2.setFont(new Font("Tahoma", Font.BOLD, 15));
            String title = "عدد المواعيد حسب الحالة";
            FontMetrics tfm = g2.getFontMetrics();
            g2.drawString(title, (width - tfm.stringWidth(title)) / 2, titleH - 12);

            int chartTop = titleH + topPad + labelH;
            int chartBottom = height - legendH - axisH;
            int chartHeight = Math.max(20, chartBottom - chartTop);
            int chartLeft = sidePad;
            int chartWidth = Math.max(20, width - 2 * sidePad);

            int max = data.values().stream().max(Integer::compareTo).orElse(1);
            if (max == 0) max = 1;

            int n = data.size();
            // أعمدة رفيعة زي المرجع: عرض ثابت وصغير + مسافات أوسع بينها، بدل ما ياخذ العمود العرض كله
            int barWidth = 34;
            int minGap = 55;
            int contentWidth = n * barWidth + (n + 1) * minGap;
            int barGap = contentWidth <= chartWidth
                    ? minGap
                    : Math.max(15, (chartWidth - n * barWidth) / (n + 1));
            int startX = chartLeft + Math.max(0, (chartWidth - (n * barWidth + (n + 1) * barGap)) / 2) + barGap;

            // Baseline
            g2.setColor(new Color(210, 210, 210));
            g2.drawLine(chartLeft - 10, chartBottom, chartLeft + chartWidth + 10, chartBottom);

            int x = startX;
            int i = 0;
            g2.setFont(new Font("Tahoma", Font.BOLD, 13));
            FontMetrics vfm = g2.getFontMetrics();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int value = entry.getValue();
                int barHeight = (int) (((double) value / max) * chartHeight);
                if (barHeight < 4 && value > 0) barHeight = 4;
                int y = chartBottom - barHeight;

                Color barColor = PALETTE[i % PALETTE.length];

                // Rounded-top bar
                g2.setColor(barColor);
                g2.fillRoundRect(x, y, barWidth, barHeight, 6, 6);
                g2.fillRect(x, y + Math.max(0, barHeight - 6), barWidth, Math.min(6, barHeight));

                // Value label above bar
                g2.setColor(new Color(50, 50, 50));
                String valStr = String.valueOf(value);
                g2.drawString(valStr, x + (barWidth - vfm.stringWidth(valStr)) / 2, y - 8);

                // Category label below bar (x-axis)
                g2.setFont(new Font("Tahoma", Font.PLAIN, 12));
                FontMetrics cfm = g2.getFontMetrics();
                String label = entry.getKey();
                g2.setColor(new Color(90, 90, 90));
                g2.drawString(label, x + (barWidth - cfm.stringWidth(label)) / 2, chartBottom + 18);
                g2.setFont(new Font("Tahoma", Font.BOLD, 13));

                x += barWidth + barGap;
                i++;
            }

            // Legend row at the bottom, centered, colored square + status name
            g2.setFont(new Font("Tahoma", Font.PLAIN, 12));
            FontMetrics lfm = g2.getFontMetrics();
            int swatch = 12;
            int gapBetween = 22;
            int totalLegendWidth = 0;
            String[] keys = data.keySet().toArray(new String[0]);
            int[] segWidths = new int[keys.length];
            for (int k = 0; k < keys.length; k++) {
                segWidths[k] = swatch + 6 + lfm.stringWidth(keys[k]);
                totalLegendWidth += segWidths[k];
                if (k < keys.length - 1) totalLegendWidth += gapBetween;
            }
            int lx = (width - totalLegendWidth) / 2;
            int ly = height - legendH + 10;
            for (int k = 0; k < keys.length; k++) {
                g2.setColor(PALETTE[k % PALETTE.length]);
                g2.fillRect(lx, ly - swatch + 2, swatch, swatch);
                g2.setColor(new Color(70, 70, 70));
                g2.drawString(keys[k], lx + swatch + 6, ly + 2);
                lx += segWidths[k] + gapBetween;
            }
        }
    }
}
