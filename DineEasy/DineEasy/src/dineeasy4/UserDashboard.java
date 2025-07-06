package dineeasy4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDashboard extends JFrame {
    private String username;
    private JTable attendanceTable;
    private JComboBox<String> monthDropdown;
    private int userId;
    private JLabel lunchLabel;
    private JLabel dinnerLabel;
    private JLabel summaryLabel;
    private JButton feedbackButton;

    public UserDashboard(String username) {
        this.username = username;
        initializeUser();
        setupUI();
        loadInitialData();
    }

    private void initializeUser() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT user_id FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.userId = rs.getInt("user_id");
            } else {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex);
        }
    }

    private void setupUI() {
        setTitle("DineEasy - User Dashboard");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Button Panel (contains both buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);

        // Create and add buttons in reversed order
        feedbackButton = createFeedbackButton();
        JButton logoutButton = createLogoutButton();
        buttonPanel.add(feedbackButton);
        buttonPanel.add(logoutButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Menu Section
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.NORTH);

        // Attendance Section
        JPanel attendancePanel = createAttendancePanel();
        mainPanel.add(attendancePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createFeedbackButton() {
        JButton button = new JButton("Feedback");
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(e -> showFeedbackDialog());
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(e -> logout());
        return button;
    }

    private void showFeedbackDialog() {
        JDialog feedbackDialog = new JDialog(this, "Submit Feedback", true);
        feedbackDialog.setSize(400, 300);
        feedbackDialog.setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rating components
        JLabel ratingLabel = new JLabel("Rating (1-5 stars):");
        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        // Feedback components
        JLabel feedbackLabel = new JLabel("Feedback Message:");
        JTextArea feedbackArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);

        formPanel.add(ratingLabel);
        formPanel.add(ratingCombo);
        formPanel.add(feedbackLabel);
        formPanel.add(scrollPane);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String message = feedbackArea.getText().trim();
            int rating = (int) ratingCombo.getSelectedItem();

            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(feedbackDialog,
                        "Please enter feedback message",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (saveFeedbackToDatabase(message, rating)) {
                JOptionPane.showMessageDialog(feedbackDialog,
                        "Thank you for your feedback!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                feedbackDialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> feedbackDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        feedbackDialog.add(formPanel, BorderLayout.CENTER);
        feedbackDialog.add(buttonPanel, BorderLayout.SOUTH);
        feedbackDialog.setLocationRelativeTo(this);
        feedbackDialog.setVisible(true);
    }

    private boolean saveFeedbackToDatabase(String message, int rating) {
        String sql = "INSERT INTO Feedback (user_id, feedback_message, rating) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.userId);
            pstmt.setString(2, message);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            handleDatabaseError(ex);
            return false;
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Close the UserDashboard
            new LoginFrame(); // Open the LoginFrame
        }
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10)); // Changed to 3 rows for separate buttons
        panel.setBorder(BorderFactory.createTitledBorder("Today's Menu"));

        lunchLabel = new JLabel("Loading lunch menu...", SwingConstants.CENTER);
        dinnerLabel = new JLabel("Loading dinner menu...", SwingConstants.CENTER);
        lunchLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dinnerLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(createAttendanceButton("Lunch"));
        buttonPanel.add(createAttendanceButton("Dinner"));

        panel.add(lunchLabel);
        panel.add(dinnerLabel);
        panel.add(buttonPanel); // Add button panel to the layout

        return panel;
    }

    private JButton createAttendanceButton(String mealType) {
        JButton button = new JButton("Mark " + mealType + " Attendance");
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "Confirm " + mealType + " attendance for today?",
                    "Attendance Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                markAttendance(mealType);
            }
        });
        return button;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        // Month selection
        monthDropdown = new JComboBox<>(getMonths());
        monthDropdown.setSelectedItem(getCurrentMonth());
        monthDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
        monthDropdown.addActionListener(e -> loadAttendance());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(new JLabel("Select Month:"));
        controlPanel.add(monthDropdown);

        // Attendance Table
        String[] columns = {"Date", "Meal", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(model);
        attendanceTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        attendanceTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);

        // Summary Panel
        summaryLabel = new JLabel("Total meals this month: 0 (Lunch: 0, Dinner: 0)");
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        summaryPanel.add(summaryLabel);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadInitialData() {
        loadMenu();
        loadAttendance();
        setVisible(true);
    }

    private void loadMenu() {
        try (Connection conn = getConnection()) {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            String lunchSql = "SELECT items FROM Menus WHERE menu_date = ? AND meal_type = 'Lunch'";
            PreparedStatement lunchStmt = conn.prepareStatement(lunchSql);
            lunchStmt.setString(1, today);
            ResultSet lunchRs = lunchStmt.executeQuery();
            lunchLabel.setText(lunchRs.next() ?
                    formatMenuItems("Lunch", lunchRs.getString("items")) :
                    "No lunch menu available");

            String dinnerSql = "SELECT items FROM Menus WHERE menu_date = ? AND meal_type = 'Dinner'";
            PreparedStatement dinnerStmt = conn.prepareStatement(dinnerSql);
            dinnerStmt.setString(1, today);
            ResultSet dinnerRs = dinnerStmt.executeQuery();
            dinnerLabel.setText(dinnerRs.next() ?
                    formatMenuItems("Dinner", dinnerRs.getString("items")) :
                    "No dinner menu available");
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private String formatMenuItems(String mealType, String items) {
        return "<html><b>" + mealType + ":</b><br>" + items.replaceAll(", ", "<br>") + "</html>";
    }

    private void markAttendance(String mealType) {
        try (Connection conn = getConnection()) {
            // Check for existing attendance
            String checkSql = "SELECT * FROM Attendance WHERE user_id = ? AND date = CURDATE() AND meal = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, mealType);
            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this,
                        "Attendance already marked for " + mealType + " today!",
                        "Duplicate Entry",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert new attendance
            String insertSql = "INSERT INTO Attendance (user_id, date, meal, month) VALUES (?, CURDATE(), ?, MONTHNAME(CURDATE()))";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setString(2, mealType);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Attendance marked successfully for " + mealType + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadAttendance();
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private void loadAttendance() {
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0); // Clear existing data

        int lunchCount = 0;
        int dinnerCount = 0;

        try (Connection conn = getConnection()) {
            String sql = "SELECT date, meal FROM Attendance " +
                    "WHERE user_id = ? AND MONTHNAME(date) = ? " +
                    "ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, (String) monthDropdown.getSelectedItem());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");
                String meal = rs.getString("meal");
                model.addRow(new Object[]{date, meal, "Present"});

                if (meal.equalsIgnoreCase("Lunch")) lunchCount++;
                else if (meal.equalsIgnoreCase("Dinner")) dinnerCount++;
            }

            summaryLabel.setText(String.format("Total meals this month: %d (Lunch: %d, Dinner: %d)",
                    lunchCount + dinnerCount, lunchCount, dinnerCount));
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private String[] getMonths() {
        return new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
    }

    private String getCurrentMonth() {
        return new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());
    }

    private void handleDatabaseError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/dineeasy", "root", "1234");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard("TestUser"));
    }
}