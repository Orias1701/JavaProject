package view.HeaderRegion;

import view.Style;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;

public class HeaderPanel extends javax.swing.JPanel {
    public HeaderPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(Style.WIN_WIDTH, 70));
        setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(0x654DB7)));
    }
}
