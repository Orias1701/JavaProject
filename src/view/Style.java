package view;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Style {
    // Color
    public static final Color LIGHT_CL = Color.WHITE;
    public static final Color DARK_CL = Color.BLACK;
    public static final Color GRAY_CL = Color.decode("#444444");
    public static final Color MAIN_CL = Color.decode("#9E88FF");
    public static final Color SEC_CL = Color.decode("#D7CEFF");
    public static final Color TRD_CL = Color.decode("#7C68D4");
    public static final Color ACT_CL = Color.decode("#6430AD");
    public static final Color BUTTON_CL = Color.decode("#43327C");
    public static final Color NO_CL = new Color(0, 0, 0, 0);

    // Font
    public static final Font HEADER_16 = new Font("Arial", Font.BOLD, 16);
    public static final Font HEADER_20 = new Font("Arial", Font.BOLD, 20);
    public static final Font HEADER_24 = new Font("Arial", Font.BOLD, 24);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font TITLE_16 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BUTTON = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TEXT_N = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TEXT_N16 = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font TEXT_B = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TEXT_I = new Font("Segoe UI", Font.ITALIC, 12);
    public static final Font TEXT_BI = new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12);

    // Size
    public static final Dimension MENU_WIDTH = new Dimension(240, 0);
    public static final Dimension BUTTON_SIZE = new Dimension(240, 50);

    // Padding & spacing
    public static final int WIN_WIDTH = 1440;
    public static final int WIN_HEIGHT = 900;
    public static final int MENU_TOP_PADDING = 20;
    public static final int MENU_BOTTOM_PADDING = 10;
    public static final int BUTTON_SPACING = 7;

    // Border
    public static final Border BORDER_L20 = BorderFactory.createEmptyBorder(0, 20, 0, 0);
}
