package org.example.stockmonitor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class App extends Application {
    private static final String API_URL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=%5EDJI";
    private static final Queue<Double> stockDataQueue = new LinkedList<>();
    private static XYChart.Series<Number, Number> series;
    private static long timestamp = System.currentTimeMillis();

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                fetchStockData();
                try {
                    Thread.sleep(5000); // Fetch data every 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create X and Y axes for the chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        yAxis.setLabel("Stock Price");

        // Create LineChart and Series
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        series = new XYChart.Series<>();
        lineChart.getData().add(series);

        // Timeline to update the chart every 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> updateChart()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Set up the scene and stage
        StackPane root = new StackPane();
        root.getChildren().add(lineChart);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Stock Price Dashboard");
        stage.show();
    }

    private static void fetchStockData() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response;
                StringBuilder responseContent = new StringBuilder();
                while ((response = reader.readLine()) != null) {
                    responseContent.append(response);
                }
                reader.close();
                double stockPrice = extractStockPrice(responseContent.toString());
                stockDataQueue.add(stockPrice);
                System.out.println("Stock data added to queue: " + stockPrice);
            } else {
                System.out.println("Error fetching stock data: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double extractStockPrice(String responseContent) {
        // Extract stock price from the JSON response
        // You can use a JSON library like Gson or Jackson to parse it
        // For simplicity, assuming the stock price is in a specific location
        String priceString = responseContent.split("\"regularMarketPrice\":")[1].split(",")[0];
        return Double.parseDouble(priceString);
    }

    private void updateChart() {
        if (!stockDataQueue.isEmpty()) {
            double stockPrice = stockDataQueue.poll();
            long timeElapsed = (System.currentTimeMillis() - timestamp) / 1000;
            series.getData().add(new XYChart.Data<>(timeElapsed, stockPrice));
            if (series.getData().size() > 100) {
                series.getData().remove(0); // Limit to the last 100 points
            }
        }
    }
}
