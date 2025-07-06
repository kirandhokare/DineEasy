package dineeasy4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class profile extends JFrame implements ActionListener {
    private JButton userButton, adminButton;
    private final Color PRIMARY_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 20);

    public profile() {
        configureWindow();
        initUI();
    }

    private void configureWindow() {
        setTitle("DineEasy - Profile Selection");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 650, 50, 50));
    }

    private void initUI() {
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background shadow
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(20, 20, getWidth()-40, getHeight()-40, 50, 50);

                // Draw main content panel
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 50, 50);
            }
        };
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));

        JLabel title = new JLabel("DineEasy");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);

        panel.add(title);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);

        // Subtitle
        JLabel subtitle = new JLabel("Select Your Access Level");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitle.setForeground(new Color(90, 90, 90));
        gbc.gridy = 0;
        panel.add(subtitle, gbc);

        // User Button
        userButton = createElevatedButton("User Portal", 320, 60);
        gbc.gridy = 1;
        panel.add(userButton, gbc);

        // Admin Button
        adminButton = createElevatedButton("Admin Portal", 320, 60);
        gbc.gridy = 2;
        panel.add(adminButton, gbc);

        // About Us Section
        JPanel aboutPanel = createAboutPanel();
        gbc.gridy = 3;
        gbc.insets = new Insets(40, 30, 15, 30);  // Increased top margin
        panel.add(aboutPanel, gbc);

        return panel;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String aboutText = "<html><div style='text-align: center; width: 300px;'>" +
                "<h2 style='margin-bottom: 10px;'>About DineEasy</h2>" +
                "📞 Contact: +91-8767777342/+91-9322575816<br>" +
                "📧 Email: dineeasy11@gmail.com<br>" +
                "📍 Location: Pune, India<br>" +
                "<br>Developed by Kunal Gholap & Kiran Dhokare</div></html>";

        JLabel aboutLabel = new JLabel(aboutText);
        aboutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aboutLabel.setForeground(new Color(80, 80, 80));
        aboutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(aboutLabel);
        return panel;
    }

    private JButton createElevatedButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 15, 15);

                // Button background
                g2.setColor(getModel().isRollover() ? PRIMARY_COLOR.brighter() : PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 15, 15);

                // Button text
                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(width, height));
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JLabel footer = new JLabel("© 2025 DineEasy");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(150, 150, 150));
        panel.add(footer);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == userButton) {
            dispose();
            new LoginFrame();
        } else if (e.getSource() == adminButton) {
            dispose();
            new adminLogin();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new profile();
        });
    }
}