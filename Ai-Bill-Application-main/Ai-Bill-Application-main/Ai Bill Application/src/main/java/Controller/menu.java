package Controller;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

public class menu {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("Swing UI Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());

        // 创建左侧按钮面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1));
        JButton menuButton = new JButton("菜单");
        JButton aiButton = new JButton("AI");
        leftPanel.add(menuButton);
        leftPanel.add(aiButton);

        // 创建右侧显示面板
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // 添加标题
        JLabel titleLabel = new JLabel("AI Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建输入和表格的组合面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // 创建输入区域（水平排列）
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        inputPanel.add(new JLabel("交易时间"));
        JTextField input1 = new JTextField(8);
        inputPanel.add(input1);

        inputPanel.add(new JLabel("交易类型"));
        JTextField input2 = new JTextField(8);
        inputPanel.add(input2);

        inputPanel.add(new JLabel("交易对象"));
        JTextField input3 = new JTextField(8);
        inputPanel.add(input3);

        inputPanel.add(new JLabel("商品"));
        JTextField input4 = new JTextField(8);
        inputPanel.add(input4);

        inputPanel.add(new JLabel("收支"));
        String[] listData = {"收入", "支出"};
        JComboBox<String> comboBox = new JComboBox<>(listData);
        inputPanel.add(comboBox);

        inputPanel.add(new JLabel("支付方式"));
        JTextField input5 = new JTextField(8);
        inputPanel.add(input5);

        JButton searchButton = new JButton("搜索");
        inputPanel.add(searchButton);

        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columnNames = {"交易时间", "交易类型", "交易对方", "商品", "收/支", "金额(元)", "支付方式", "当前状态", "交易单号", "商户单号", "备注"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));

        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        rightPanel.add(centerPanel, BorderLayout.CENTER);

        // 将面板添加到主窗口
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        // 直接加载CSV文件到表格
        File file = new File("C:\\Users\\陈冠儒\\Desktop\\微信支付账单(20250210-20250310).csv"); // 直接指定文件
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "文件不存在！", "错误", JOptionPane.ERROR_MESSAGE);
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"))) {
                String line;
                tableModel.setRowCount(0); // 清空表格
                while ((line = br.readLine()) != null) {
                    String[] rowData = line.split(","); // 假设 CSV 以逗号分隔
                    if (rowData.length == columnNames.length) {
                        tableModel.addRow(rowData);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "导入失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 显示窗口
        frame.setVisible(true);
    }
}
