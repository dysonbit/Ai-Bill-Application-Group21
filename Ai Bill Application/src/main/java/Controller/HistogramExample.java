package Controller;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

// 数据生成类
class DataGenerator {
    public static int[] generateData(int numberOfDataPoints, int maxValue) {
        Random random = new Random();
        int[] data = new int[numberOfDataPoints];
        for (int i = 0; i < numberOfDataPoints; i++) {
            data[i] = random.nextInt(maxValue);
        }
        return data;
    }
}

// 直方图计算类
class Histogram {
    private int binSize;
    private int[] data;

    public Histogram(int[] data, int binSize) {
        this.data = data;
        this.binSize = binSize;
    }

    public HashMap<Integer, Integer> computeFrequency() {
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (int number : data) {
            int bin = number / binSize;
            frequencyMap.put(bin, frequencyMap.getOrDefault(bin, 0) + 1);
        }
        return frequencyMap;
    }
}

// 直方图 GUI 绘制类
class HistogramPanel extends JPanel {
    HashMap<Integer, Integer> frequencyMap;

    public HistogramPanel(HashMap<Integer, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int barWidth = 40; // 每个条形的宽度
        int gap = 10; // 条形之间的间隔
        int maxFrequency = frequencyMap.values().stream().mapToInt(v -> v).max().orElse(1);

        int index = 0;
        for (Integer key : frequencyMap.keySet()) {
            int height = (int) ((frequencyMap.get(key) / (double) maxFrequency) * getHeight());
            g.setColor(Color.BLUE);
            g.fillRect(index * (barWidth + gap), getHeight() - height, barWidth, height);
            g.setColor(Color.BLACK);
            g.drawRect(index * (barWidth + gap), getHeight() - height, barWidth, height); // 绘制边框
            g.drawString("Bin " + key, index * (barWidth + gap) + 5, getHeight() - 5);
            index++;
        }
    }
}



public class HistogramExample {
    private JFrame frame;
    private JSplitPane splitPane;
    private HistogramPanel histogramPanel;
    private JTextArea textArea;
    private JScrollPane textScrollPane;
    private boolean isHistogramVisible = true;
    private boolean isTextVisible = true;

    public HistogramExample() {
        frame = new JFrame("数据分析界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnShowHistogram = new JButton("显示直方图");
        JButton btnShowText1 = new JButton("显示文本 1");
        JButton btnShowText2 = new JButton("显示文本 2");

        buttonPanel.add(btnShowHistogram);
        buttonPanel.add(btnShowText1);
        buttonPanel.add(btnShowText2);
        frame.add(buttonPanel, BorderLayout.NORTH);

        // 初始化文本显示区域
        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        textArea.setLineWrap(true); // 允许自动换行
        textArea.setWrapStyleWord(true); // 按单词换行，避免切割单词
        textArea.setEditable(false); // 设为只读模式

        textScrollPane = new JScrollPane(textArea);
        textScrollPane.setPreferredSize(new Dimension(300, 600));
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 直方图
        histogramPanel = new HistogramPanel(new HashMap<>());

        // 使用 JSplitPane 让文本和直方图同时显示
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScrollPane, histogramPanel);
        splitPane.setResizeWeight(0.5); // 初始时，文本和直方图各占 50%
        frame.add(splitPane, BorderLayout.CENTER);

        // 按钮功能
        btnShowHistogram.addActionListener(e -> toggleHistogram());
        btnShowText1.addActionListener(e -> toggleText("这是一段非常长的文本这是一段非常长的文本这是一段非常长的文本这是一段非常长的文本这是一段非常长的文本这是一段非常长的文本"));
        btnShowText2.addActionListener(e -> toggleText("222222222222222"));

        frame.setVisible(true);
    }

    // 显示或隐藏直方图
    private void toggleHistogram() {
        if (isHistogramVisible) {
            histogramPanel.setVisible(false);
            isHistogramVisible = false;
        } else {
            showHistogram();
            histogramPanel.setVisible(true);
            isHistogramVisible = true;
        }

        SwingUtilities.invokeLater(() -> {
            if (isHistogramVisible) {
                splitPane.setDividerLocation(0.5);
            } else {
                splitPane.setDividerLocation(0.0);
            }
            splitPane.revalidate();
            splitPane.repaint();
        });
    }

    // 切换文本显示/隐藏
    private void toggleText(String text) {
        if (isTextVisible) {
            textArea.setText(""); // 清空文本
            isTextVisible = false;
        } else {
            textArea.setText(text); // 显示文本
            isTextVisible = true;
        }
    }

    // 生成直方图数据
    private void showHistogram() {
        int[] data = DataGenerator.generateData(1000, 100);
        Histogram histogram = new Histogram(data, 10);
        HashMap<Integer, Integer> frequencyMap = histogram.computeFrequency();
        histogramPanel.frequencyMap = frequencyMap;

        SwingUtilities.invokeLater(histogramPanel::repaint);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HistogramExample::new);
    }
}

