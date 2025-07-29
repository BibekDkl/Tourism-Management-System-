package com.example.nepaltourismmanagement.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeatherUtil {

    // In a real application, you would use an actual weather API
    // For this example, we'll simulate weather data
    private final Map<String, String> weatherConditions;
    private final Random random;

    public WeatherUtil() {
        weatherConditions = new HashMap<>();
        random = new Random();

        // Initialize some sample weather conditions for popular trekking areas
        weatherConditions.put("Kathmandu", "Partly Cloudy, 25°C");
        weatherConditions.put("Pokhara", "Sunny, 28°C");
        weatherConditions.put("Everest Base Camp", "Cold, -5°C");
        weatherConditions.put("Annapurna", "Cloudy, 15°C");
        weatherConditions.put("Langtang", "Light Rain, 18°C");
    }

    /**
     * Get current weather for a location
     * @param location The name of the location
     * @return A string representing the weather condition
     */
    public String getCurrentWeather(String location) {
        if (weatherConditions.containsKey(location)) {
            return weatherConditions.get(location);
        } else {
            // Generate random weather for unknown locations
            String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Foggy"};
            int temperature = random.nextInt(35) - 5; // Temperature between -5 and 30
            return conditions[random.nextInt(conditions.length)] + ", " + temperature + "°C";
        }
    }

    /**
     * Check if weather is safe for trekking in a high altitude location
     * @param location The name of the location
     * @return True if weather is safe for trekking, false otherwise
     */
    public boolean isSafeTrekkingWeather(String location) {
        // In a real app, this would check actual weather conditions
        // For this example, we'll simulate a safety check
        if (location.toLowerCase().contains("everest")) {
            // 70% chance that Everest weather is unsafe
            return random.nextDouble() > 0.7;
        } else if (location.toLowerCase().contains("annapurna")) {
            // 50% chance that Annapurna weather is unsafe
            return random.nextDouble() > 0.5;
        } else {
            // Most other locations are generally safe
            return random.nextDouble() > 0.2;
        }
    }

    /**
     * Get a weather forecast for the next few days
     * @param location The name of the location
     * @param days Number of days to forecast
     * @return A string array with forecasts for each day
     */
    public String[] getWeatherForecast(String location, int days) {
        String[] forecast = new String[days];
        String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Foggy", "Windy", "Stormy"};

        for (int i = 0; i < days; i++) {
            int temperature = random.nextInt(35) - 5; // Temperature between -5 and 30
            forecast[i] = "Day " + (i + 1) + ": " + conditions[random.nextInt(conditions.length)] +
                    ", " + temperature + "°C";
        }

        return forecast;
    }

    /**
     * In a real application, this method would call an external weather API
     * For now, it's a placeholder for future implementation
     */
    private String fetchWeatherFromAPI(String location) {
        try {
            // This is just a placeholder for what would be a real API call
            String apiKey = "your_api_key_here";
            String urlString = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + location;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // In a real app, you would parse this JSON response and extract weather data
            return content.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Weather data unavailable";
        }
    }
}