// package view.LoginRegion;

// import javax.swing.*;
// import java.awt.*;

// public class LoginForm extends JPanel {

//     private final JTextField usernameField;
//     private final JPasswordField passwordField;
//     private final JButton loginButton;

//     public LoginForm() {
//         setLayout(new GridBagLayout());
//         setBackground(Color.decode("#F5F5F5"));

//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 10, 10, 10);

//         JLabel titleLabel = new JLabel("Đăng Nhập");
//         titleLabel.setFont(view.Style.MONS_24);
//         titleLabel.setForeground(view.Style.MAIN_CL);
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 2;
//         gbc.anchor = GridBagConstraints.CENTER;
//         add(titleLabel, gbc);

//         JLabel usernameLabel = new JLabel("Tên người dùng:");
//         usernameLabel.setFont(view.Style.MONS_16);
//         usernameLabel.setForeground(view.Style.MAIN_CL);
//         gbc.gridx = 0;
//         gbc.gridy = 1;
//         gbc.gridwidth = 1;
//         gbc.anchor = GridBagConstraints.WEST;
//         add(usernameLabel, gbc);

//         usernameField = new JTextField(20);
//         gbc.gridx = 1;
//         gbc.gridy = 1;
//         add(usernameField, gbc);

//         JLabel passwordLabel = new JLabel("Mật khẩu:");
//         passwordLabel.setFont(view.Style.MONS_16);
//         passwordLabel.setForeground(view.Style.MAIN_CL);
//         gbc.gridx = 0;
//         gbc.gridy = 2;
//         add(passwordLabel, gbc);

//         passwordField = new JPasswordField(20);
//         gbc.gridx = 1;
//         gbc.gridy = 2;
//         add(passwordField, gbc);

//         loginButton = new JButton("Đăng nhập");
//         loginButton.setBackground(view.Style.MAIN_CL);
//         loginButton.setForeground(view.Style.LIGHT_CL);
//         loginButton.setFont(view.Style.MONS_16);
//         loginButton.setOpaque(true);
//         loginButton.setBorderPainted(false);
//         gbc.gridx = 0;
//         gbc.gridy = 3;
//         gbc.gridwidth = 2;
//         gbc.anchor = GridBagConstraints.CENTER;
//         add(loginButton, gbc);
//     }

//     public JTextField getUsernameField() {
//         return usernameField;
//     }

//     public JPasswordField getPasswordField() {
//         return passwordField;
//     }

//     public JButton getLoginButton() {
//         return loginButton;
//     }
// }