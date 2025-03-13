package Service.Impl;

import DAO.CsvTransactionDao;
import Service.TransactionService;
import model.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private static final String CSV_PATH = "src\\main\\resources\\CSVForm\\0001.csv";
    private static CsvTransactionDao csvTransactionDao;

    /**
     * 新增交易
     * @param transaction
     */
    @Override
    public void addTransaction(Transaction transaction) throws IOException {
        csvTransactionDao = new CsvTransactionDao();
        csvTransactionDao.addTransaction(CSV_PATH, transaction);
    }

    @Override
    public void changeTransaction(Transaction transaction) {

    }

    /**
     * 根据订单号删除交易 (若成功则返回true)
     * @param orderNumber
     */
    @Override
    public boolean deleteTransaction(String orderNumber) throws IOException {
        csvTransactionDao = new CsvTransactionDao();
        return csvTransactionDao.deleteTransaction(CSV_PATH, orderNumber);
    }

    /**
     * 按照查询条件查询交易信息
     * @param searchCriteria
     * @return
     */
    @Override
    public List<Transaction> searchTransaction(Transaction searchCriteria) {
        try {
            // 1. 读取所有交易记录（假设 filePath 已在类中定义或通过其他方式获取）
            List<Transaction> allTransactions = csvTransactionDao.loadFromCSV("your_file_path.csv");

            // 2. 动态模糊匹配
            List<Transaction> matched = new ArrayList<>();
            for (Transaction t : allTransactions) {
                if (matchesCriteria(t, searchCriteria)) {
                    matched.add(t);
                }
            }

            // 3. 按交易时间倒序排序
            matched.sort((t1, t2) -> compareTransactionTime(t2.getTransactionTime(), t1.getTransactionTime()));

            return matched;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // 实际应用中应处理异常
        }
    }



    // 判断单个交易记录是否匹配条件
    private boolean matchesCriteria(Transaction transaction, Transaction criteria) {
        return containsIgnoreCase(transaction.getTransactionTime(), criteria.getTransactionTime())
                && containsIgnoreCase(transaction.getTransactionType(), criteria.getTransactionType())
                && containsIgnoreCase(transaction.getCounterparty(), criteria.getCounterparty())
                && containsIgnoreCase(transaction.getCommodity(), criteria.getCommodity())
                && containsIgnoreCase(transaction.getInOut(), criteria.getInOut())
                && containsIgnoreCase(transaction.getPaymentMethod(), criteria.getPaymentMethod());
    }

    // 字符串模糊匹配（空条件视为匹配）
    private boolean containsIgnoreCase(String source, String target) {
        if (target == null || target.trim().isEmpty()) return true; // 空条件不参与筛选
        if (source == null) return false;
        return source.toLowerCase().contains(target.toLowerCase().trim());
    }

    // 安全的时间比较（处理 null 值）
    private int compareTransactionTime(String time1, String time2) {
        if (time1 == null && time2 == null) return 0;
        if (time1 == null) return -1;
        if (time2 == null) return 1;
        return time2.compareTo(time1); // 倒序排序
    }

}
