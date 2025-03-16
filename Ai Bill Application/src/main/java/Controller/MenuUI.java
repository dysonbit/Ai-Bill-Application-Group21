package Controller;

import DAO.CsvTransactionDao;
import Service.Impl.TransactionServiceImpl;
import model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MenuUI {
    private static final String FILE_PATH = "src\\main\\resources\\CSVForm\\0003.csv";
    private static DefaultTableModel tableModel;
    private static Vector<Vector<String>> allData = new Vector<>();
    private static TransactionServiceImpl transactionService = new TransactionServiceImpl();
    private JTable table;  // 把table定义为静态字段

    public MenuUI(){
        String[] columnNames = {"交易时间", "交易类型", "交易对方", "商品", "收/支", "金额(元)", "支付方式", "当前状态", "交易单号", "商户单号", "备注", "Modify", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
    }

    public JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        JButton menuButton = new JButton("Menu");
        JButton aiButton = new JButton("AI");
        leftPanel.add(menuButton);
        leftPanel.add(aiButton);
        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("AI Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createInputPanel();

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(1000, 250));

        // 设置表格的行高
        table.setRowHeight(30); // 你可以根据需要调整这个值

        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        panel.add(rightPanel, BorderLayout.CENTER);

        // 为 Modify 列设置渲染器和编辑器
        table.getColumnModel().getColumn(11).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(11).setCellEditor(new ButtonEditor(this));

        // 为 Delete 列设置渲染器和编辑器
        table.getColumnModel().getColumn(12).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(12).setCellEditor(new ButtonEditor(this));

        loadCSVData(FILE_PATH);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField input1 = new JTextField(8), input2 = new JTextField(8), input3 = new JTextField(8),
                input4 = new JTextField(8), input5 = new JTextField(8);
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"", "收入", "支出"});
        JButton searchButton = new JButton("Search");

        inputPanel.add(new JLabel("交易时间"));
        inputPanel.add(input1);
        inputPanel.add(new JLabel("交易类型"));
        inputPanel.add(input2);
        inputPanel.add(new JLabel("交易对方"));
        inputPanel.add(input3);
        inputPanel.add(new JLabel("商品"));
        inputPanel.add(input4);
        inputPanel.add(new JLabel("收/支"));
        inputPanel.add(comboBox);
        inputPanel.add(new JLabel("支付方式"));
        inputPanel.add(input5);
        inputPanel.add(searchButton);

        searchButton.addActionListener(e -> searchData(input1.getText().trim(), input2.getText().trim(),
                input3.getText().trim(), input4.getText().trim(), (String) comboBox.getSelectedItem(), input5.getText().trim()));

        return inputPanel;
    }

    public void loadCSVData(String filePath) {
        allData.clear();
        tableModel.setRowCount(0);

        CsvTransactionDao csvTransactionDao = new CsvTransactionDao();

        try {
            List<Transaction> transactions = csvTransactionDao.loadFromCSV(filePath);
            for (Transaction transaction : transactions) {
                Vector<String> row = createRowFromTransaction(transaction);
                allData.add(row);
                tableModel.addRow(row);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "CSV文件读取失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchData(String query1, String query2, String query3, String query4, String query6, String query5) {
        tableModel.setRowCount(0);
        Transaction searchCriteria = new Transaction(query1, query2, query3, query4, query6, 0, query5, "", "", "", "");

        try {
            List<Transaction> transactions = transactionService.searchTransaction(searchCriteria);
            for (Transaction transaction : transactions) {
                Vector<String> row = createRowFromTransaction(transaction);
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vector<String> createRowFromTransaction(Transaction transaction) {
        Vector<String> row = new Vector<>();
        row.add(transaction.getTransactionTime());
        row.add(transaction.getTransactionType());
        row.add(transaction.getCounterparty());
        row.add(transaction.getCommodity());
        row.add(transaction.getInOut());
        row.add(String.valueOf(transaction.getPaymentAmount()));
        row.add(transaction.getPaymentMethod());
        row.add(transaction.getCurrentStatus());
        row.add(transaction.getOrderNumber());
        row.add(transaction.getMerchantNumber());
        row.add(transaction.getRemarks());
        row.add("Modify"); // Modify 按钮
        row.add("Delete"); // 添加 Delete 按钮
        return row;
    }

    public void deleteRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < allData.size()) {
            String orderNumber = allData.get(rowIndex).get(8).trim();
            System.out.println("尝试删除的交易单号: " + orderNumber); // 打印交易单号

            try {
                if (transactionService.deleteTransaction(orderNumber)) {
                    allData.remove(rowIndex);
                    tableModel.removeRow(rowIndex);
                    JOptionPane.showMessageDialog(null, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "删除失败：未找到对应的交易单号", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void editRow(int rowIndex) {
        System.out.println("编辑行: " + rowIndex);
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
                Transaction transaction = new Transaction(
                        fields[0].getText().trim(),
                        fields[1].getText().trim(),
                        fields[2].getText().trim(),
                        fields[3].getText().trim(),
                        fields[4].getText().trim(),
                        Double.parseDouble(fields[5].getText().trim()),
                        fields[6].getText().trim(),
                        fields[7].getText().trim(),
                        fields[8].getText().trim(),
                        fields[9].getText().trim(),
                        fields[10].getText().trim()
                );
                try {
                    transactionService.changeTransaction(transaction);
                    loadCSVData(FILE_PATH);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // 添加此方法以便测试时可以获取table
    public JTable getTable() {
        return table;
    }

}
