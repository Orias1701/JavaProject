package project.appPack.UI;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

public class MenuButton extends JButton {

    public MenuButton(String text) {
        super(text);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 60);
    }
}