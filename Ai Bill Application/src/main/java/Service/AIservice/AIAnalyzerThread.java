package Service.AIservice;

public class AIAnalyzerThread implements Runnable {
    private final String userRequest;
    private final String filePath;
    private final int days;

    public AIAnalyzerThread(String userRequest, String filePath, int days) {
        this.userRequest = userRequest;
        this.filePath = filePath;
        this.days = days;
    }

    @Override
    public void run() {
        String result = new AITransactionService().analyzeTransactions(userRequest, filePath, days);
        System.out.println("AI分析结果: " + result);
        // TODO: 如果是UI程序，可用 SwingUtilities.invokeLater() 更新UI组件
    }
}
