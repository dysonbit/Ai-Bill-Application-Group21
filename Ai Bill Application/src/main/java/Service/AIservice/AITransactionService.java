package Service.AIservice;

import DAO.CsvTransactionDao;
import model.Transaction;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.*;

public class AITransactionService {
    private static final String API_KEY = System.getenv("ARK_API_KEY");
    private static final ArkService service = ArkService.builder()
            .timeout(Duration.ofSeconds(1800))
            .connectTimeout(Duration.ofSeconds(20))
            .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
            .apiKey(API_KEY)
            .build();

    private final CsvTransactionDao transactionDao = new CsvTransactionDao();

    public String analyzeTransactions(String userRequest, String filePath) {
        try {
            List<Transaction> transactions = transactionDao.loadFromCSV(filePath);
            List<String> transactionDetails = formatTransactions(transactions);

            String aiPrompt = userRequest + "\n" + "以下是我的账单信息，请分析：\n" + String.join("\n", transactionDetails);
            return askAi(aiPrompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "AI分析失败: " + e.getMessage();
        }
    }

    private List<String> formatTransactions(List<Transaction> transactions) {
        return transactions.stream().map(t -> String.format(
                "交易时间: %s, 商品: %s, 收/支: %s, 金额: %.2f元",
                t.getTransactionTime(),
                t.getCommodity(),
                t.getInOut(),
                t.getPaymentAmount()
        )).collect(Collectors.toList());
    }

    private String askAi(String prompt) {
        try {
            List<ChatMessage> messages = List.of(
                    ChatMessage.builder().role(ChatMessageRole.USER).content(prompt).build()
            );

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("ep-20250308174053-7pbkq")
                    .messages(messages)
                    .build();

            return (String) service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "AI请求失败: " + e.getMessage();
        }
    }

    public void runAiInThread(String userRequest, String filePath) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String result = analyzeTransactions(userRequest, filePath);
            System.out.println("AI分析结果: " + result);
        });
        executor.shutdown();
    }
}
