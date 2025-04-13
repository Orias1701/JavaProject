import controller.LogHandler;
import javax.swing.SwingUtilities;
import view.MainUI;

public class Run {
    public static void main(String[] args) {
        System.out.println("hahaha");
        SwingUtilities.invokeLater(() -> {
            LogHandler.logInfo("Start");
            new MainUI();
        });
    }
}