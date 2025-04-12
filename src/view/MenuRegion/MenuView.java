// package view.MenuRegion;

// import javax.swing.*;
// import java.awt.*;

// public class MenuView extends JPanel {

//     private Rectangle highlightRect = new Rectangle(0, 0, 240, 60);

//     public MenuView() {
//         setLayout(null);
//         setOpaque(false);
//     }

//     public Rectangle getHighlightRect() {
//         return highlightRect;
//     }

//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         Graphics2D g2d = (Graphics2D) g.create();
//         GradientPaint gradient = new GradientPaint(
//                 0, 0, new Color(0xFB, 0xFA, 0xFC),
//                 0, getHeight(), new Color(0x8E, 0x84, 0xB9)
//         );
//         g2d.setPaint(gradient);
//         g2d.fillRect(0, 0, getWidth(), getHeight());
//         g2d.setColor(view.Style.MAIN_CL);
//         g2d.fillRoundRect(highlightRect.x, highlightRect.y, highlightRect.width, highlightRect.height, 7, 10);
//         g2d.dispose();
//     }
// }