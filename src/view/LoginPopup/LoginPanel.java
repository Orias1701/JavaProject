package view.LoginPopup;

// import java.awt.Color;
// import java.awt.GridBagConstraints;
// import java.awt.GridBagLayout;
// import java.awt.Insets;
// import javax.swing.JButton;
// import javax.swing.JLabel;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JPasswordField;
// import javax.swing.JTextField;

// import model.ApiClient;

// public class LoginPanel extends JPanel {

//     private final JTextField usernameField;
//     private final JPasswordField passwordField;
//     private final MainUI parentFrame;

//     public LoginPanel(MainUI parentFrame) {
//         this.parentFrame = parentFrame;
//         setLayout(new GridBagLayout());
//         setBackground(Color.decode("#F5F5F5"));

//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 10, 10, 10);

//         JLabel titleLabel = new JLabel("Đăng Nhập");
//         titleLabel.setFont(Style.MONS_24);
//         titleLabel.setForeground(Style.MAIN_CL);
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 2;
//         gbc.anchor = GridBagConstraints.CENTER;
//         add(titleLabel, gbc);

//         JLabel usernameLabel = new JLabel("Tên người dùng:");
//         usernameLabel.setFont(Style.MONS_16);
//         usernameLabel.setForeground(Style.MAIN_CL);
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
//         passwordLabel.setFont(Style.MONS_16);
//         passwordLabel.setForeground(Style.MAIN_CL);
//         gbc.gridx = 0;
//         gbc.gridy = 2;
//         add(passwordLabel, gbc);

//         passwordField = new JPasswordField(20);
//         gbc.gridx = 1;
//         gbc.gridy = 2;
//         add(passwordField, gbc);

//         JButton loginButton = new JButton("Đăng nhập");
//         loginButton.setBackground(Style.MAIN_CL);
//         loginButton.setForeground(Style.LIGHT_CL);
//         loginButton.setFont(Style.MONS_16);
//         loginButton.setOpaque(true);
//         loginButton.setBorderPainted(false);
//         gbc.gridx = 0;
//         gbc.gridy = 3;
//         gbc.gridwidth = 2;
//         gbc.anchor = GridBagConstraints.CENTER;
//         add(loginButton, gbc);

//         loginButton.addActionListener(e -> handleLogin());
//     }

//     private void handleLogin() {
//         String username = usernameField.getText();
//         String password = new String(passwordField.getPassword());
//         System.out.println("Login attempt - Username: " + username + ", Password: " + password);

//         boolean success = ApiClient.login(username, password);

//         if (success) {
//             parentFrame.showMainInterface();
//         } else {
//             JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không đúng",
//                 "Lỗi", JOptionPane.ERROR_MESSAGE);
//         }
//     }
// }