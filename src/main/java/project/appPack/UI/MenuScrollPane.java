package project.appPack.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class MenuScrollPane extends JScrollPane {

    public MenuScrollPane(JPanel panel) {
        super(panel);
        getVerticalScrollBar().setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.decode("#FF9500"), 1, true));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(100, 100, 100);
                trackColor = new Color(255, 255, 255);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    @Override
    public JScrollBar createVerticalScrollBar() {
        JScrollBar customScrollBar = new JScrollBar() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(255, 255, 255));
                setForeground(new Color(255, 255, 255));
            }
        };
        customScrollBar.setPreferredSize(new Dimension(10, 0));
        return customScrollBar;
    }
}