package view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.LogHandler;
import controller.MainCtrl;
import model.ApiClient;

public class LoginPanel extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final MainUI parentFrame;
    private final JLabel usernameErrorLabel;
    private final JLabel passwordErrorLabel;

    public LoginPanel(MainUI parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#F5F5F5"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Đăng Nhập");
        titleLabel.setFont(Style.MONS_24);
        titleLabel.setForeground(Style.MAIN_CL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Tên người dùng:");
        usernameLabel.setFont(Style.MONS_16);
        usernameLabel.setForeground(Style.MAIN_CL);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        usernameErrorLabel = new JLabel("");
        usernameErrorLabel.setFont(Style.MONS_12);
        usernameErrorLabel.setForeground(Color.RED);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        add(usernameErrorLabel, gbc);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(Style.MONS_16);
        passwordLabel.setForeground(Style.MAIN_CL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(passwordField, gbc);

        passwordErrorLabel = new JLabel("");
        passwordErrorLabel.setFont(Style.MONS_12);
        passwordErrorLabel.setForeground(Color.RED);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 10, 10);
        add(passwordErrorLabel, gbc);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(Style.MAIN_CL);
        loginButton.setForeground(Style.LIGHT_CL);
        loginButton.setFont(Style.MONS_16);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> attemptLogin());
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        LogHandler.logInfo("LoginPanel initialized");
    }

    private void attemptLogin() {

        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            usernameErrorLabel.setText("Vui lòng nhập tên người dùng");
            usernameField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            passwordErrorLabel.setText("Vui lòng nhập mật khẩu");
            passwordField.requestFocusInWindow();
            return;
        }
        handleLogin(username, password);
    }

    private void handleLogin(String username, String password) {
        LogHandler.logInfo("Thử đăng nhập - Tên người dùng: " + username);

        boolean success = ApiClient.login(username, password);

        if (success) {
            LogHandler.logInfo("Đăng nhập thành công cho người dùng: " + username);
            String authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            MainCtrl.setAuthHeader(authHeader);
            parentFrame.showMainInterface();
        } else {
            LogHandler.logError("Đăng nhập thất bại cho người dùng: " + username);
            passwordErrorLabel.setText("Tài khoản hoặc mật khẩu không đúng");
            usernameField.requestFocusInWindow();
        }
    }
}