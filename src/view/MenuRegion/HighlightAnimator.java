// package view.MenuRegion;

// import java.awt.Rectangle;

// import javax.swing.*;

// public class HighlightAnimator {

//     private final MenuView menuView;
//     private final Timer animationTimer;

//     public HighlightAnimator(MenuView menuView) {
//         this.menuView = menuView;
//         this.animationTimer = new Timer(10, e -> animateHighlight());
//     }

//     public void setHighlightY(int y) {
//         menuView.getHighlightRect().y = y;
//         menuView.repaint();
//     }

//     public void animateTo(int targetY) {
//         animationTimer.start();
//         menuView.getHighlightRect().y = targetY - (menuView.getHighlightRect().y - targetY);
//     }

//     private void animateHighlight() {
//         Rectangle highlightRect = menuView.getHighlightRect();
//         int targetY = getTargetY();
//         int dy = targetY - highlightRect.y;
//         if (Math.abs(dy) <= 1) {
//             highlightRect.y = targetY;
//             animationTimer.stop();
//         } else {
//             int step = (int) (dy * 0.16);
//             if (step == 0) step = (dy > 0) ? 1 : -1;
//             highlightRect.y += step;
//         }
//         menuView.repaint();
//     }

//     private int getTargetY() {
//         return menuView.getHighlightRect().y;
//     }
// }