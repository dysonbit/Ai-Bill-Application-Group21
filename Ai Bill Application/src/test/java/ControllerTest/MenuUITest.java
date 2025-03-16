package ControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import Controller.MenuUI;
import Service.Impl.TransactionServiceImpl;
import DAO.CsvTransactionDao;

public class MenuUITest {

    private MenuUI menuUI;

    @BeforeEach
    public void setUp() {
        menuUI = new MenuUI();

        // 手动初始化 TransactionServiceImpl 的 csvTransactionDao
        TransactionServiceImpl.csvTransactionDao = new CsvTransactionDao();
    }

    @Test
    public void testSearchFunction() throws IOException {
        // 创建主面板
        JPanel mainPanel = menuUI.createMainPanel();

        // 显示界面以便手动验证
        showUI(mainPanel);

        // 获取输入面板中的组件
        JPanel inputPanel = findInputPanel(mainPanel);
        assertNotNull(inputPanel, "输入面板不应为空");

        // 获取输入字段和搜索按钮
        JTextField transactionTimeField = findTextField(inputPanel, 0);
        JTextField transactionTypeField = findTextField(inputPanel, 1);
        JTextField counterpartyField = findTextField(inputPanel, 2);
        JTextField commodityField = findTextField(inputPanel, 3);
        JComboBox<String> inOutComboBox = findComboBox(inputPanel);
        JTextField paymentMethodField = findTextField(inputPanel, 4);
        JButton searchButton = findButton(inputPanel);

        assertNotNull(transactionTimeField, "交易时间输入字段不应为空");
        assertNotNull(transactionTypeField, "交易类型输入字段不应为空");
        assertNotNull(counterpartyField, "交易对方输入字段不应为空");
        assertNotNull(commodityField, "商品输入字段不应为空");
        assertNotNull(inOutComboBox, "收/支下拉框不应为空");
        assertNotNull(paymentMethodField, "支付方式输入字段不应为空");
        assertNotNull(searchButton, "搜索按钮不应为空");

        // 设置搜索条件
        transactionTimeField.setText("2025/3/14 11:48"); // 交易时间
        transactionTypeField.setText("商户消费");       // 交易类型
        counterpartyField.setText("物美WUMART");         // 交易对方
        commodityField.setText("扫码支付");            // 商品
        inOutComboBox.setSelectedItem("支出");     // 收/支
        paymentMethodField.setText("零钱");    // 支付方式

        // 模拟点击搜索按钮
        searchButton.doClick();

        // 获取表格模型
        DefaultTableModel tableModel = (DefaultTableModel) menuUI.getTable().getModel();

        // 验证表格模型不为空
        assertNotNull(tableModel, "表格模型不应为空");

        // 验证搜索结果是否正确
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String rowTransactionTime = (String) tableModel.getValueAt(i, 0);
            String rowTransactionType = (String) tableModel.getValueAt(i, 1);
            String rowCounterparty = (String) tableModel.getValueAt(i, 2);
            String rowCommodity = (String) tableModel.getValueAt(i, 3);
            String rowInOut = (String) tableModel.getValueAt(i, 4);
            String rowPaymentMethod = (String) tableModel.getValueAt(i, 6);

            // 验证每一行数据是否符合搜索条件
            assertTrue(rowTransactionTime.contains("2025/3/14 11:48"), "交易时间应符合搜索条件");
            assertTrue(rowTransactionType.contains("商户消费"), "交易类型应符合搜索条件");
            assertTrue(rowCounterparty.contains("物美WUMART"), "交易对方应符合搜索条件");
            assertTrue(rowCommodity.contains("扫码支付"), "商品应符合搜索条件");
            assertTrue(rowInOut.contains("支出"), "收/支应符合搜索条件");
            assertTrue(rowPaymentMethod.contains("零钱"), "支付方式应符合搜索条件");
        }
    }

    @Test
    public void testDeleteFunction() throws IOException {
        // 创建主面板
        JPanel mainPanel = menuUI.createMainPanel();

        // 显示界面以便手动验证
        showUI(mainPanel);

        // 获取表格模型
        DefaultTableModel tableModel = (DefaultTableModel) menuUI.getTable().getModel();

        // 验证表格模型不为空
        assertNotNull(tableModel, "表格模型不应为空");

        // 获取初始行数
        int initialRowCount = tableModel.getRowCount();

        // 模拟删除第一行
        menuUI.deleteRow(0);

        // 验证行数是否减少
        assertEquals(initialRowCount - 1, tableModel.getRowCount(), "删除后行数应减少");

        // 验证删除后的数据是否正确
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String rowTransactionTime = (String) tableModel.getValueAt(i, 0);
            assertNotEquals("2025/3/14 11:48", rowTransactionTime, "删除的行不应再出现在表格中");
        }
    }

    @Test
    public void testEditFunction() throws IOException {
        // 创建主面板
        JPanel mainPanel = menuUI.createMainPanel();

        // 显示界面以便手动验证
        showUI(mainPanel);

        // 获取表格模型
        DefaultTableModel tableModel = (DefaultTableModel) menuUI.getTable().getModel();

        // 验证表格模型不为空
        assertNotNull(tableModel, "表格模型不应为空");

        // 模拟编辑第一行
        menuUI.editRow(0);

        // 验证编辑后的数据是否正确
        String editedTransactionTime = (String) tableModel.getValueAt(0, 0);
        String editedTransactionType = (String) tableModel.getValueAt(0, 1);
        String editedCounterparty = (String) tableModel.getValueAt(0, 2);
        String editedCommodity = (String) tableModel.getValueAt(0, 3);
        String editedInOut = (String) tableModel.getValueAt(0, 4);
        String editedPaymentMethod = (String) tableModel.getValueAt(0, 6);

        // 验证编辑后的数据是否符合预期
        assertNotNull(editedTransactionTime, "编辑后的交易时间不应为空");
        assertNotNull(editedTransactionType, "编辑后的交易类型不应为空");
        assertNotNull(editedCounterparty, "编辑后的交易对方不应为空");
        assertNotNull(editedCommodity, "编辑后的商品不应为空");
        assertNotNull(editedInOut, "编辑后的收/支不应为空");
        assertNotNull(editedPaymentMethod, "编辑后的支付方式不应为空");
    }

    /**
     * 显示界面以便手动验证
     *
     * @param panel 要显示的面板
     */
    private void showUI(JPanel panel) {
        JFrame frame = new JFrame("MenuUI Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.add(panel);
        frame.setVisible(true);

        // 保持界面显示一段时间（5秒）
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        frame.dispose(); // 关闭界面
    }

    /**
     * 查找输入面板
     *
     * @param mainPanel 主面板
     * @return 输入面板
     */
    private JPanel findInputPanel(JPanel mainPanel) {
        for (Component component : mainPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JLabel) {
                    return panel; // 输入面板通常包含 JLabel
                }
            }
        }
        return null;
    }

    /**
     * 查找输入字段
     *
     * @param panel     输入面板
     * @param index     输入字段的索引
     * @return 输入字段
     */
    private JTextField findTextField(JPanel panel, int index) {
        int count = 0;
        for (Component component : panel.getComponents()) {
            if (component instanceof JTextField) {
                if (count == index) {
                    return (JTextField) component;
                }
                count++;
            }
        }
        return null;
    }

    /**
     * 查找下拉框
     *
     * @param panel 输入面板
     * @return 下拉框
     */
    private JComboBox<String> findComboBox(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JComboBox) {
                return (JComboBox<String>) component;
            }
        }
        return null;
    }

    /**
     * 查找按钮
     *
     * @param panel 输入面板
     * @return 按钮
     */
    private JButton findButton(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                return (JButton) component;
            }
        }
        return null;
    }
}