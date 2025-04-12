// package view.LoginRegion;

// import javax.swing.*;
// import model.AuthManager;
// import util.Logger;

// public class LoginHandler {

//     private final view.MainUI parentFrame;
//     private final LoginForm loginForm;

//     public LoginHandler(view.MainUI parentFrame, LoginForm loginForm) {
//         this.parentFrame = parentFrame;
//         this.loginForm = loginForm;

//         loginForm.getLoginButton().addActionListener(e -> handleLogin());
//     }

//     private void handleLogin() {
//         String username = loginForm.getUsernameField().getText();
//         String password = new String(loginForm.getPasswordField().getPassword());
//         Logger.log("Login attempt - Username: " + username); // Modified: Avoid logging password for security

//         boolean success = AuthManager.login(username, password);

//         if (success) {
//             parentFrame.showMainInterface();
//         } else {
//             JOptionPane.showMessageDialog(loginForm, "Tài khoản hoặc mật khẩu không đúng",
//                     "Lỗi", JOptionPane.ERROR_MESSAGE);
//         }
//     }
// }