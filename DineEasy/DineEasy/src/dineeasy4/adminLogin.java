package dineeasy4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class adminLogin extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox showPassword;

    public adminLogin() {
        setTitle("DineEasy - Admin Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Main container panel with shadow effect
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(255, 255, 255));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(40, 50, 40, 50),
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1)
        ));

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("DineEasy");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(40, 40, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Admin Login");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitleLabel.setForeground(new Color(40, 40, 40));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);



        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(25));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(Box.createVerticalStrut(4));

        // Form Section
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Centered Input Fields
        JPanel centeredFormPanel = new JPanel();
        centeredFormPanel.setLayout(new BoxLayout(centeredFormPanel, BoxLayout.Y_AXIS));
        centeredFormPanel.setBackground(Color.WHITE);
        centeredFormPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        JPanel usernamePanel = createInputPanel("👤 Username");
        usernameField = new JTextField();
        styleTextField(usernameField);
        usernamePanel.add(createCenteredComponent(usernameField));

        // Password Field
        JPanel passwordPanel = createInputPanel("🔒 Password");
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordPanel.add(createCenteredComponent(passwordField));

        // Show Password Checkbox
        showPassword = new JCheckBox("Show password");
        styleCheckBox(showPassword);
        showPassword.addItemListener(e ->
                passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•')
        );
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.add(showPassword);
        passwordPanel.add(Box.createVerticalStrut(5));
        passwordPanel.add(createCenteredComponent(checkboxPanel));

        // Login Button
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 0, 0), Color.WHITE);
        loginButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);

        // Add components to form panel
        centeredFormPanel.add(usernamePanel);
        centeredFormPanel.add(Box.createVerticalStrut(20));
        centeredFormPanel.add(passwordPanel);
        centeredFormPanel.add(Box.createVerticalStrut(30));
        centeredFormPanel.add(buttonPanel);

        formPanel.add(centeredFormPanel);

        // Assemble main panel
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Reused styling methods from LoginFrame
    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(80, 80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(createCenteredComponent(label));
        panel.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200, 200, 200));
        separator.setPreferredSize(new Dimension(250, 2));
        panel.add(createCenteredComponent(separator));

        return panel;
    }

    private JPanel createCenteredComponent(Component comp) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(comp);
        return wrapper;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(60, 60, 60));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(300, 40));
        field.setBackground(new Color(250, 250, 250));
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setForeground(new Color(120, 120, 120));
        checkBox.setBackground(Color.WHITE);
        checkBox.setFocusPainted(false);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.brighter().darker(), 1),
                        BorderFactory.createEmptyBorder(12, 40, 12, 40)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 1),
                        BorderFactory.createEmptyBorder(12, 40, 12, 40)
                ));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/dineeasy", "root", "1234")) {

            String query = "SELECT * FROM Admin WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, user);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPass = rs.getString("password");
                if (storedPass.equals(pass)) {
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        new AdminPanel();
                    });
                } else {
                    showError("Incorrect password");
                }
            } else {
                showError("Admin not found");
            }
        } catch (SQLException ex) {
            showError("Database connection error");
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new adminLogin();
        });
    }
}