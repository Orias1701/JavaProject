package view.MainRegion;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import view.Style;

public class GridView {
    private final JPanel buttonPanel;
    private final JPanel containerPanel;
    private List<Map<String, String>> data; // Lưu dữ liệu
    private List<String> columnNames; // Lưu tên cột
    private List<String> columnComments; // Lưu chú thích cột
    private FormDialogHandler formDialogHandler; // Lưu handler
    private boolean canAdd;
    private boolean canEdit;
    private boolean canDelete;

    public GridView(TablePanel parent) {
        // GridLayout sẽ được thiết lập động trong updateView
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Style.LIGHT_CL);

        // Container để căn giữa buttonPanel
        containerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        containerPanel.setOpaque(true);
        containerPanel.setBackground(Style.LIGHT_CL);
        containerPanel.add(buttonPanel);

        // Thêm ComponentListener để tự động cập nhật khi kích thước thay đổi
        containerPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (data != null && columnNames != null && columnComments != null && formDialogHandler != null) {
                    updateView(data, columnNames, columnComments, formDialogHandler, canAdd, canEdit, canDelete);
                }
            }
        });
    }

    public JPanel getView() {
        return containerPanel;
    }

    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, FormDialogHandler formDialogHandler, boolean canAdd, boolean canEdit, boolean canDelete) {
        // Lưu các tham số để sử dụng khi resize
        this.data = data;
        this.columnNames = columnNames;
        this.columnComments = columnComments;
        this.formDialogHandler = formDialogHandler;
        this.canAdd = canAdd;
        this.canEdit = canEdit;
        this.canDelete = canDelete;

        buttonPanel.removeAll();

        if (data == null || data.isEmpty()) {
            buttonPanel.revalidate();
            buttonPanel.repaint();
            containerPanel.revalidate();
            containerPanel.repaint();
            return;
        }

        // Kích thước cố định cho mỗi button
        final int buttonMinWidth = 400; // Chiều rộng tối thiểu
        final int buttonHeight = 150; // Chiều cao cố định
        final int gap = 10;

        // Tính chiều rộng của buttonPanel dựa trên cửa sổ
        int windowWidth = containerPanel.getWidth();
        if (windowWidth == 0) {
            windowWidth = 4 * (buttonMinWidth + gap) + gap + 240;
        }
        int panelWidth = windowWidth - 240;

        // Tính số cột
        int columns = panelWidth / (buttonMinWidth + gap);
        if (columns < 1) columns = 1;

        // Thiết lập GridLayout với số cột động
        buttonPanel.setLayout(new GridLayout(0, columns, gap, gap));

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Map<String, String> row = data.get(rowIndex);
            StringBuilder buttonText = new StringBuilder("<html><body style='width:100%'>");
            for (int i = 0; i < Math.min(5, columnNames.size()); i++) {
                String columnName = columnNames.get(i);
                String comment = columnComments.size() > i ? columnComments.get(i) : columnName;
                String value = row.get(columnName);
                buttonText.append("<b style='color:#333;'>").append(comment).append(":</b> ")
                          .append(value != null ? value : "").append("<br>");
            }
            buttonText.append("</body></html>");

            Style.RoundedButton editButton = new Style.RoundedButton(buttonText.toString());

            // Modern style
            editButton.setFont(Style.ROB_14);
            editButton.setForeground(Style.DARK_CL);
            editButton.setBackground(Style.SEC_CL);
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setFocusPainted(false);
            editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editButton.setOpaque(false);
            editButton.setContentAreaFilled(false);

            // Apply modern border with subtle shadow
            editButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.ACT_CL, 3),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
            ));

            // Kích thước button
            editButton.setPreferredSize(new Dimension(buttonMinWidth, buttonHeight));
            editButton.setMinimumSize(new Dimension(buttonMinWidth, buttonHeight));

            // Optional: Hover effect
            editButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    editButton.setBackground(Style.MAIN_CL);
                    editButton.setOpaque(false);
                    editButton.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    editButton.setBackground(Style.SEC_CL);
                    editButton.setOpaque(false);
                    editButton.repaint();
                }
            });

            // Chỉ thêm hành động chỉnh sửa nếu có quyền
            if (canDelete) {
                final int finalRowIndex = rowIndex;
                editButton.addActionListener(e -> formDialogHandler.showFormDialog("all", finalRowIndex));
            } else if (canEdit) {
                final int finalRowIndex = rowIndex;
                editButton.addActionListener(e -> formDialogHandler.showFormDialog("edit", finalRowIndex));
            } else {
                final int finalRowIndex = rowIndex;
                editButton.addActionListener(e -> formDialogHandler.showFormDialog("detail", finalRowIndex));
            }

            buttonPanel.add(editButton);
        }

        // Tính số hàng
        int estimatedRows = (int) Math.ceil((double) data.size() / columns);
        int panelHeight = estimatedRows * (buttonHeight + gap) + gap;

        // Đặt kích thước cho buttonPanel
        buttonPanel.setMinimumSize(new Dimension(buttonMinWidth + gap + gap, panelHeight));
        buttonPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        buttonPanel.revalidate();
        buttonPanel.repaint();
        containerPanel.revalidate();
        containerPanel.repaint();
    }
}