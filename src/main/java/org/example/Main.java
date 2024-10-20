package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        try {
            String baseUrl = "https://api.weather.yandex.ru/v2/forecast";
            String lat = "54.7551";
            String lon = "83.0967";
            int daysNumberLag = 7;
            String urlString = String.format("%s?lat=%s&lon=%s&limit=%d", baseUrl, lat, lon, daysNumberLag);
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-API-Key", "93122a7c-a950-4647-b53b-f607f3fd2469");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = input.readLine()) != null) {
                    response.append(inputLine);
                }

                input.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                System.out.println("Весь ответ:");
                System.out.println(jsonResponse.toString(4));

                int currentTemp = jsonResponse.getJSONObject("fact").getInt("temp");
                System.out.println("Текущая температура: " + currentTemp + "°C");

                JSONArray forecasts = jsonResponse.getJSONArray("forecasts");
                double sumTemp = 0;
                int daysNumber = Math.min(daysNumberLag, forecasts.length());

                for (int i = 0; i < daysNumber; i++) {
                    sumTemp += forecasts.getJSONObject(i)
                            .getJSONObject("parts")
                            .getJSONObject("day")
                            .getDouble("temp_avg");
                }

                double avgTemp = sumTemp / daysNumber;

                System.out.println("Средняя температура за " + daysNumber + " дней: " + Math.round(avgTemp) + "°C");
            } else {
                System.out.println("Ошибка: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}