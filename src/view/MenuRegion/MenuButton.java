package view.MenuRegion;

import javax.swing.*;
import java.awt.*;

public class MenuButton extends JButton {
    private boolean isActive = false;

    public MenuButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.BLACK);
        setFont(getFont().deriveFont(14f));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isRollover() || isActive) {
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 10);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(240, 60);
    }
}
