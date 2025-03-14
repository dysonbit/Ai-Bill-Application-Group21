package Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Vector;

public class menu {
    private static final String FILE_PATH = "src\\main\\resources\\CSVForm\\order(20250210-20250310).csv";
    private static DefaultTableModel tableModel;
    private static Vector<Vector<String>> allData = new Vector<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Swing UI Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 500);
        frame.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        JButton menuButton = new JButton("Menu");
        JButton aiButton = new JButton("AI");
        leftPanel.add(menuButton);
        leftPanel.add(aiButton);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("AI Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField input1 = new JTextField(8), input2 = new JTextField(8), input3 = new JTextField(8),
                input4 = new JTextField(8), input5 = new JTextField(8);
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"", "In", "Out"});
        JButton searchButton = new JButton("Search");

        inputPanel.add(new JLabel("Transaction time"));
        inputPanel.add(input1);
        inputPanel.add(new JLabel("Transaction type"));
        inputPanel.add(input2);
        inputPanel.add(new JLabel("Transaction counterparty"));
        inputPanel.add(input3);
        inputPanel.add(new JLabel("Commodity"));
        inputPanel.add(input4);
        inputPanel.add(new JLabel("InOut"));
        inputPanel.add(comboBox);
        inputPanel.add(new JLabel("Payment method"));
        inputPanel.add(input5);
        inputPanel.add(searchButton);

        String[] columnNames = {"Transaction time", "Transaction type", "Transaction counterparty", "Commodity", "InOut", "Payment amount(yuan)", "Payment method", "Current status", "Transaction order number", "Merchant order number", "Remarks", "Operation"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(11).setPreferredWidth(150); // 让“操作”列更宽，按钮更容易点到
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(1000, 250));

        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        loadCSVData();

        // 设置表格操作列（修改 & 删除按钮）
        table.getColumnModel().getColumn(11).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(11).setCellEditor(new ButtonEditor(new JCheckBox()));

        searchButton.addActionListener(e -> searchData(input1.getText().trim(), input2.getText().trim(),
                input3.getText().trim(), input4.getText().trim(), (String) comboBox.getSelectedItem(), input5.getText().trim()));

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void loadCSVData() {
        allData.clear();
        tableModel.setRowCount(0);

        try (InputStream inputStream = menu.class.getClassLoader().getResourceAsStream("CSVForm\\order(20250210-20250310).csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "GBK"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowData = line.split(",");
                if (rowData.length == 11) {
                    Vector<String> row = new Vector<>();
                    for (String cell : rowData) row.add(cell.trim());
                    row.add("Search");
                    allData.add(row);
                    tableModel.addRow(row);
                }
            }
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "CSV文件读取失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static void deleteRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < allData.size()) {
            allData.remove(rowIndex);
            tableModel.removeRow(rowIndex);
            saveCSVData();
        }
    }

    private static void editRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < allData.size()) {
            Vector<String> rowData = allData.get(rowIndex);

            JTextField[] fields = new JTextField[rowData.size() - 1];
            JPanel panel = new JPanel(new GridLayout(rowData.size() - 1, 2));

            for (int i = 0; i < rowData.size() - 1; i++) {
                panel.add(new JLabel(tableModel.getColumnName(i)));
                fields[i] = new JTextField(rowData.get(i));
                panel.add(fields[i]);
            }

            int result = JOptionPane.showConfirmDialog(null, panel, "Modify data", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                for (int i = 0; i < fields.length; i++) {
                    rowData.set(i, fields[i].getText().trim());
                }
                tableModel.setValueAt(rowData.get(0), rowIndex, 0);
                saveCSVData();
            }
        }
    }

    private static void saveCSVData() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), "UTF-8"))) {
            for (Vector<String> row : allData) {
                bw.write(String.join(",", row.subList(0, row.size() - 1)));
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "CSV文件保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void searchData(String query1, String query2, String query3, String query4, String query6, String query5) {
        tableModel.setRowCount(0);
        for (Vector<String> row : allData) {
            boolean match = (query1.isEmpty() || row.get(0).contains(query1)) &&
                    (query2.isEmpty() || row.get(1).contains(query2)) &&
                    (query3.isEmpty() || row.get(2).contains(query3)) &&
                    (query4.isEmpty() || row.get(3).contains(query4)) &&
                    (query6.isEmpty() || row.get(4).equals(query6)) &&
                    (query5.isEmpty() || row.get(6).contains(query5));

            if (match) tableModel.addRow(row);
        }
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        JButton editButton = new JButton("Modify");
        JButton deleteButton = new JButton("Delete");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        JButton editButton, deleteButton;
        int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            editButton = new JButton("Modify");
            deleteButton = new JButton("Delete");
            editButton.addActionListener(e -> editRow(row));
            deleteButton.addActionListener(e -> deleteRow(row));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            JPanel panel = new JPanel();
            panel.add(editButton);
            panel.add(deleteButton);
            return panel;
        }
    }
}
