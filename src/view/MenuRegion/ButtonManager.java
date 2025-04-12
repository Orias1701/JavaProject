// package view.MenuRegion;

// import javax.swing.*;

// import java.awt.Component;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import view.Style;
// import model.ApiService;

// public class ButtonManager {

//     private final MenuView menuView;
//     private final HighlightAnimator highlightAnimator;
//     private final List<MenuButton> menuButtons = new ArrayList<>();
//     private MenuButton activeButton;

//     public ButtonManager(MenuView menuView, HighlightAnimator highlightAnimator) {
//         this.menuView = menuView;
//         this.highlightAnimator = highlightAnimator;

//         initializeButtons();
//     }

//     private void initializeButtons() {
//         int y = 20;

//         // Home button
//         MenuButton homeButton = createMenuButton("TRANG CHỦ", y);
//         homeButton.setFont(Style.MONS_16);
//         homeButton.setForeground(Style.LIGHT_CL);
//         homeButton.putClientProperty("tableName", "HOME");
//         homeButton.setBounds(0, y, 240, 60);
//         homeButton.setBorder(Style.BORDER_L20);
//         homeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
//         activeButton = homeButton;
//         highlightAnimator.setHighlightY(homeButton.getY());
//         menuView.add(homeButton);
//         menuButtons.add(homeButton);
//         y += 60;

//         // Table buttons
//         Map<String, String> tableInfo = ApiService.getTableInfo();
//         for (Map.Entry<String, String> entry : tableInfo.entrySet()) {
//             String tableName = entry.getKey();
//             String tableComment = entry.getValue();
//             MenuButton button = createMenuButton(tableComment, y);
//             button.putClientProperty("tableName", tableName);
//             menuView.add(button);
//             menuButtons.add(button);
//             y += 60;
//         }
//     }

//     private MenuButton createMenuButton(String text, int y) {
//         MenuButton button = new MenuButton(text);
//         button.setBounds(0, y, 240, 60);
//         button.setFont(Style.MONS_16);
//         button.setForeground(Style.GRAY_CL);
//         button.setBackground(Style.NO_CL);
//         button.setBorder(BorderFactory.createEmptyBorder());
//         button.setHorizontalAlignment(SwingConstants.LEFT);
//         button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
//         button.addActionListener(e -> moveHighlightTo(button));

//         button.addMouseListener(new java.awt.event.MouseAdapter() {
//             @Override
//             public void mouseEntered(java.awt.event.MouseEvent e) {
//                 if (!button.isActive()) {
//                     button.setBackground(Style.SEC_CL);
//                     button.repaint();
//                 }
//             }

//             @Override
//             public void mouseExited(java.awt.event.MouseEvent e) {
//                 if (!button.isActive()) {
//                     button.setBackground(Style.NO_CL);
//                     button.setForeground(Style.GRAY_CL);
//                     button.repaint();
//                 }
//             }
//         });

//         return button;
//     }

//     private void moveHighlightTo(MenuButton targetButton) {
//         if (activeButton != null) {
//             activeButton.setActive(false);
//             activeButton.setBackground(Style.NO_CL);
//             activeButton.setForeground(Style.GRAY_CL);
//             activeButton.repaint();
//         }
//         activeButton = targetButton;
//         activeButton.setActive(true);
//         activeButton.setBackground(Style.NO_CL);
//         activeButton.setForeground(Style.LIGHT_CL);
//         highlightAnimator.animateTo(activeButton.getY());
//     }

//     public MenuButton getActiveButton() {
//         return activeButton;
//     }

//     public List<MenuButton> getMenuButtons() {
//         return menuButtons;
//     }
// }