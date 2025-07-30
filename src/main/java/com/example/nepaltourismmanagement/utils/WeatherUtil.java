package com.example.nepaltourismmanagement.utils;

public class WeatherUtil {

    // Constants
    private static final String CURRENT_DATE = "2025-07-30 06:26:05";

    public String getCurrentWeather(String location) {
        // In a real app, this would call a weather API
        // For demonstration, we'll return a static value
        if ("Kathmandu".equalsIgnoreCase(location)) {
            return "Partly Cloudy";
        } else if ("Pokhara".equalsIgnoreCase(location)) {
            return "Sunny";
        } else if ("Everest".equalsIgnoreCase(location)) {
            return "Snowy";
        } else {
            return "Clear";
        }
    }

    public double getTemperature(String location) {
        // In a real app, this would call a weather API
        if ("Kathmandu".equalsIgnoreCase(location)) {
            return 25.0;
        } else if ("Pokhara".equalsIgnoreCase(location)) {
            return 28.0;
        } else if ("Everest".equalsIgnoreCase(location)) {
            return -5.0;
        } else {
            return 22.0;
        }
    }

    public int getHumidity(String location) {
        // In a real app, this would call a weather API
        if ("Kathmandu".equalsIgnoreCase(location)) {
            return 60;
        } else if ("Pokhara".equalsIgnoreCase(location)) {
            return 65;
        } else if ("Everest".equalsIgnoreCase(location)) {
            return 30;
        } else {
            return 50;
        }
    }

    public double getWindSpeed(String location) {
        // In a real app, this would call a weather API
        if ("Kathmandu".equalsIgnoreCase(location)) {
            return 10.0;
        } else if ("Pokhara".equalsIgnoreCase(location)) {
            return 8.0;
        } else if ("Everest".equalsIgnoreCase(location)) {
            return 40.0;
        } else {
            return 12.0;
        }
    }

    public String getForecast(String location, int days) {
        // In a real app, this would call a weather API
        return "Mostly " + getCurrentWeather(location) + " for the next " + days + " days";
    }

    public boolean isHikingRecommended(String location) {
        // Basic logic for hiking recommendation
        String weather = getCurrentWeather(location);
        double temperature = getTemperature(location);
        double windSpeed = getWindSpeed(location);

        return !weather.contains("Rain") && !weather.contains("Snow") &&
                temperature > 10.0 && temperature < 30.0 && windSpeed < 30.0;
    }
}