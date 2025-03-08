package DAOTest;

import DAO.CsvTransactionDao;
import model.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class CsvTransactionDaoTest {
    // 测试文件路径（根据实际结构调整）
    private static final String TEST_CSV_PATH = "src\\main\\resources\\CSVForm\\0001.csv";
    private static CsvTransactionDao dao;

    @Test
    void testLoadFromCSV_ValidFile_ReturnsTransactions() throws Exception {
        // Given
        CsvTransactionDao dao = new CsvTransactionDao();

        // When
        List<Transaction> transactions = dao.loadFromCSV(TEST_CSV_PATH);

        // Then
        assertThat(transactions)
                .isNotNull()
                .isNotEmpty();
        // 验证第一条记录的字段
        for (int i = 0; i < transactions.size(); i++) {
            System.out.println(transactions.get(i).getRemarks());
            System.out.println(transactions.get(i).getCommodity());
        }


    }
//    @BeforeAll
//    static void setup() throws IOException {
//        dao = new CsvTransactionDao();
//
//        testCsvPath = Path.of("src\\main\\resources\\CSVForm\\0001.csv");
//
//
//        // 创建测试 CSV 文件
//        try (BufferedWriter writer = Files.newBufferedWriter(testCsvPath)) {
//            writer.write("交易时间,交易类型,交易对方,商品,收/支,金额(元),支付方式,当前状态,交易单号,商户单号,备注\n");
//            writer.write("2025-03-08 14:30,支付,小红,电子书,支,¥29.99,支付宝,已完成,TX987654,M123456,无备注\n");
//
//        }
//    }

    @Test
    void testAddTransaction() throws IOException {
        dao=new CsvTransactionDao();
        Transaction newTx = new Transaction("2025-03-09 10:00", "转账", "小明", "书籍", "支", 99.99, "微信", "已完成", "TX123456", "M789012", "");

        dao.addTransaction("src\\main\\resources\\CSVForm\\0001.csv", newTx);

        List<Transaction> transactions = dao.loadFromCSV(TEST_CSV_PATH);

    }

    @Test
    void testDeleteTransaction() throws IOException {
        dao=new CsvTransactionDao();

        dao.deleteTransaction("src\\main\\resources\\CSVForm\\0001.csv", "4200057899202502250932735481");

        List<Transaction> transactions = dao.loadFromCSV("src\\main\\resources\\CSVForm\\0001.csv");
    }
    @Test
    void testChangeInfo() throws IOException{
        dao=new CsvTransactionDao();
        dao.changeInformation("TX123456","remarks","测试修改信息",TEST_CSV_PATH);
        dao.changeInformation("TX123456","paymentAmount","116156",TEST_CSV_PATH);
    }

}
