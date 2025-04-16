package view.MainRegion;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
// import javax.swing.border.EmptyBorder;
import view.Style;

public class GridView {
    private final JPanel buttonPanel;
    public GridView(TablePanel parent) {
        buttonPanel = new JPanel(new GridLayout(0, 6, 10, 10));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Style.LIGHT_CL);
    }

    public JPanel getView() {
        return buttonPanel;
    }

    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, FormDialogHandler formDialogHandler) {
        buttonPanel.removeAll();

        if (data == null || data.isEmpty()) {
            buttonPanel.revalidate();
            buttonPanel.repaint();
            return;
        }

        // Populate button grid
        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Map<String, String> row = data.get(rowIndex);
            StringBuilder buttonText = new StringBuilder("<html>");
            for (int i = 0; i < Math.min(4, columnNames.size()); i++) {
                String columnName = columnNames.get(i);
                String value = row.get(columnName);
                buttonText.append(value != null ? value : "").append("<br>");
            }
            buttonText.append("</html>");

            JButton editButton = new JButton(buttonText.toString());
            editButton.setFont(Style.ROB_14);
            editButton.setForeground(Style.DARK_CL);
            editButton.setBackground(Style.LIGHT_CL);
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            editButton.setFocusPainted(false);

            final int finalRowIndex = rowIndex;
            editButton.addActionListener(e -> formDialogHandler.showFormDialog("edit", finalRowIndex));
            buttonPanel.add(editButton);
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}