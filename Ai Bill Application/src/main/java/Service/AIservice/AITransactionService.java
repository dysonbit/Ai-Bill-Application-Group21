package Service.AIservice;

import DAO.CsvTransactionDao;
import model.Transaction;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public String analyzeTransactions(String userRequest, String filePath,int days) {
        try {
            List<Transaction> transactions = transactionDao.loadFromCSV(filePath);
            List<String> transactionDetails = formatTransactions(transactions,days);

            String aiPrompt = userRequest + "\n" + "以下是我的账单信息，请在四百字内分析：\n" + String.join("\n", transactionDetails);
            return askAi(aiPrompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "AI分析失败: " + e.getMessage();
        }
    }


    //private List<String> formatTransactions(List<Transaction> transactions) {
        //return transactions.stream().map(t -> String.format(
               // "交易时间: %s, 商品: %s, 收/支: %s, 金额: %.2f元",
               // t.getTransactionTime(),
               // t.getCommodity(),
               // t.getInOut(),
               // t.getPaymentAmount()
        //)).collect(Collectors.toList());
    //}

    public List<String> formatTransactions(List<Transaction> transactions, int days) {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusDays(days);

        // 过滤最近 days 天的交易
        List<Transaction> recentTransactions = transactions.stream()
                .filter(t -> {
                    LocalDateTime time = parseDateTime(t.getTransactionTime());
                    return time != null && !time.isBefore(cutoff);
                })
                .collect(Collectors.toList());

        // 分组统计
        Map<String, double[]> groupedData = new HashMap<>();
        for (Transaction t : recentTransactions) {
            String counterparty = t.getCounterparty();
            double amount = t.getInOut().equals("支出") ? -t.getPaymentAmount() : t.getPaymentAmount();

            groupedData.putIfAbsent(counterparty, new double[]{0.0, 0});
            groupedData.get(counterparty)[0] += amount;
            groupedData.get(counterparty)[1] += 1;
        }

        List<String> results = groupedData.entrySet().stream()
                .map(entry -> {
                    String counterparty = entry.getKey();
                    double netAmount = entry.getValue()[0];
                    int count = (int) entry.getValue()[1];
                    String inOut = netAmount >= 0 ? "收入" : "支出";
                    return String.format("交易对方: %s, 收/支: %s, 金额: %.2f元，交易次数: %d",
                            counterparty, inOut, Math.abs(netAmount), count);
                })
                .collect(Collectors.toList());

        // 生成时间范围提示
        if (!recentTransactions.isEmpty()) {
            LocalDateTime minTime = recentTransactions.stream()
                    .map(t -> parseDateTime(t.getTransactionTime()))
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo).orElse(null);

            LocalDateTime maxTime = recentTransactions.stream()
                    .map(t -> parseDateTime(t.getTransactionTime()))
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo).orElse(null);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            if (minTime != null && maxTime != null) {
                results.add(String.format("交易时间范围：%s - %s", formatter.format(minTime), formatter.format(maxTime)));
            }
        } else {
            results.add("无最近 " + days + " 天的交易数据。");
        }

        return results;
    }




    private LocalDateTime parseDateTime(String timeStr) {
        // 你可以按实际情况支持不同格式，比如 yyyy/MM/dd HH:mm 或 yyyy-MM-dd HH:mm:ss
        List<String> patterns = Arrays.asList("yyyy/MM/dd HH:mm","yyyy/MM/dd H:mm", "yyyy/M/dd H:mm","yyyy/M/dd HH:mm","yyyy/M/d H:mm","yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(timeStr, formatter);
            } catch (Exception ignored) {}
        }
        return null;
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

    public void runAiInThread(String userRequest, String filePath,int days) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String result = analyzeTransactions(userRequest, filePath,days);
            System.out.println("AI分析结果: " + result);
        });
        executor.shutdown();
    }
}
