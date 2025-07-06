package dineeasy4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

class AdminPanel extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dineeasy";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    private JComboBox<String> monthDropdown;
    private JLabel lunchStatsLabel, dinnerStatsLabel;

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Month Selection
        monthDropdown = new JComboBox<>(new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"});
        monthDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthDropdown.addActionListener(e -> updateStatistics());
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        monthPanel.add(new JLabel("Select Month:"));
        monthPanel.add(monthDropdown);
        headerPanel.add(monthPanel, BorderLayout.EAST);

        // Statistics Panel
        JPanel statsPanel = createStatsPanel();

        // Button Panel (updated with logout button)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton viewAttendanceButton = createStyledButton("View Detailed Attendance", new Color(70, 130, 180));
        viewAttendanceButton.addActionListener(e -> viewDetailedAttendance());

        JButton updateMenuButton = createStyledButton("Update Menu", new Color(70, 130, 180));
        updateMenuButton.addActionListener(e -> updateMenu());

        // Logout Button
        JButton logoutButton = createStyledButton("Logout", new Color(70, 130, 180));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new adminLogin();
        });

        buttonPanel.add(viewAttendanceButton);
        buttonPanel.add(updateMenuButton);
        buttonPanel.add(logoutButton);

        // Main Layout
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateStatistics();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateMenu() {
        JDialog dialog = new JDialog(this, "Update Menu", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Date selection using JSpinner
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateSpinner);

        // Meal type selection
        JComboBox<String> mealTypeCombo = new JComboBox<>(new String[]{"Lunch", "Dinner"});
        inputPanel.add(new JLabel("Meal Type:"));
        inputPanel.add(mealTypeCombo);

        // Menu items input
        JTextArea itemsArea = new JTextArea(5, 20);
        itemsArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(itemsArea);
        inputPanel.add(new JLabel("Menu Items (comma-separated):"));
        inputPanel.add(scrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            // Get the date from the JSpinner
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            // Convert to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            String mealType = (String) mealTypeCombo.getSelectedItem();
            String[] items = itemsArea.getText().split("\\s*,\\s*");

            saveMenuToDatabase(sqlDate, mealType, items);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveMenuToDatabase(java.sql.Date date, String mealType, String[] items) {
        if (items.length == 0 || String.join("", items).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one menu item.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String itemsStr = String.join(", ", items);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Menus (menu_date, meal_type, items) VALUES (?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE items = VALUES(items)")) {

            stmt.setDate(1, date);
            stmt.setString(2, mealType);
            stmt.setString(3, itemsStr);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Menu updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving menu: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lunchStatsLabel = createStatLabel();
        dinnerStatsLabel = createStatLabel();

        JPanel lunchPanel = new JPanel(new BorderLayout());
        lunchPanel.setBorder(BorderFactory.createTitledBorder("Lunch Statistics"));
        lunchPanel.add(lunchStatsLabel, BorderLayout.CENTER);

        JPanel dinnerPanel = new JPanel(new BorderLayout());
        dinnerPanel.setBorder(BorderFactory.createTitledBorder("Dinner Statistics"));
        dinnerPanel.add(dinnerStatsLabel, BorderLayout.CENTER);

        panel.add(lunchPanel);
        panel.add(dinnerPanel);
        return panel;
    }

    private JLabel createStatLabel() {
        JLabel label = new JLabel("Loading...", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return button;
    }

    private void updateStatistics() {
        String selectedMonth = (String) monthDropdown.getSelectedItem();
        Map<String, Integer> stats = getMealStatistics(selectedMonth);
        int totalUsers = getTotalUsers();
        int daysInMonth = getDaysInMonth(selectedMonth);

        int lunchPresent = stats.getOrDefault("Lunch", 0);
        int dinnerPresent = stats.getOrDefault("Dinner", 0);
        int totalPossible = totalUsers * daysInMonth;

        lunchStatsLabel.setText(String.format("<html>Total present Users: %d<br>Remaining days of all Users data: %d</html>",
                lunchPresent, totalPossible - lunchPresent));
        dinnerStatsLabel.setText(String.format("<html>Total present Users: %d<br>Remaining days of all Users data: %d</html>",
                dinnerPresent, totalPossible - dinnerPresent));
    }

    private Map<String, Integer> getMealStatistics(String month) {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT meal, COUNT(*) as count FROM Attendance WHERE MONTHNAME(date) = ? GROUP BY meal")) {

            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("meal"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching statistics: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return stats;
    }

    private int getTotalUsers() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Users")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching total users: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    private int getDaysInMonth(String monthName) {
        try {
            int year = YearMonth.now().getYear();
            YearMonth yearMonth = YearMonth.of(year, java.time.Month.valueOf(monthName.toUpperCase()));
            return yearMonth.lengthOfMonth();
        } catch (Exception e) {
            return 30; // Default to 30 days if there's an error
        }
    }

    private void viewDetailedAttendance() {
        JFrame frame = new JFrame("Detailed Attendance Records");
        frame.setSize(1200, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Lunch", createMealTable("Lunch"));
        tabbedPane.addTab("Dinner", createMealTable("Dinner"));

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private JScrollPane createMealTable(String mealType) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Date", "Meal Type", "Menu Items", "User Names"}, 0);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.date, a.meal AS meal_type, m.items AS menu_items, " +
                             "GROUP_CONCAT(u.username SEPARATOR ', ') AS user_names " +
                             "FROM Attendance a " +
                             "JOIN Users u ON a.user_id = u.user_id " +
                             "LEFT JOIN Menus m ON a.date = m.menu_date AND a.meal = m.meal_type " +
                             "WHERE a.meal = ? " +
                             "GROUP BY a.date, a.meal, m.items " +
                             "ORDER BY a.date DESC")) {

            stmt.setString(1, mealType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("date"),
                        rs.getString("meal_type"),
                        rs.getString("menu_items") != null ? rs.getString("menu_items") : "N/A",
                        rs.getString("user_names")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        styleTable(table);
        return new JScrollPane(table);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setAutoCreateRowSorter(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPanel::new);
    }
}