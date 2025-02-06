package org.example.stockmonitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class App {
    private static final String API_URL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=%5EDJI";
    private static final Queue<String> stockDataQueue = new LinkedList<>();

    public static void main(String[] args) {
        // Get Client ID from environment variable
        String clientId = System.getenv("YAHOO_CLIENT_ID");

        if (clientId == null || clientId.isEmpty()) {
            System.out.println("Error: Missing API credentials. Set YAHOO_CLIENT_ID.");
            return;
        }

        while (true) {
            fetchStockData(clientId);
            try {
                Thread.sleep(5000); // Fetch data every 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void fetchStockData(String clientId) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Add Client ID in headers (for public OAuth)
            connection.setRequestProperty("dj0yJmk9UnVXenQ4WDlMTElrJmQ9WVdrOU1uaFRiR2s0ZDFBbWNHbzlNQT09JnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWE3", clientId);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response;
                StringBuilder responseContent = new StringBuilder();
                while ((response = reader.readLine()) != null) {
                    responseContent.append(response);
                }
                reader.close();

                stockDataQueue.add(responseContent.toString());
                System.out.println("Stock data added to queue: " + responseContent);
            } else {
                System.out.println("Error fetching stock data: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
