package customerForecaste;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.stream.Collectors;

public class SevenDaysForecast extends BaseAPI {
    
    
    public static class ForecastItem {
        public String date;
        public int predictedMean;
        public String day_name;
        public boolean isBusyDay;
        
        // Constructor
        public ForecastItem(String date, int predictedMean, String day_name, boolean isBusyDay) {
            this.date = date;
            this.predictedMean = predictedMean;
            this.day_name = day_name;
            this.isBusyDay = isBusyDay;
        }
    }
    
    // Get complete forecast data
    public static List<ForecastItem> getCompleteForecastData(int storeId) {
        String endpoint = "/people/getCustomerForecastGraph/" + storeId + "/nextthirtydays";

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get(endpoint);

        if (response.statusCode() != 200) {
            System.err.println("Error fetching forecast for store " + storeId);
            return null;
        }

        try {
            // Parse the complete response data
            List<Map<String, Object>> dataList = response.jsonPath().getList("data");
            
            if (dataList == null || dataList.isEmpty()) {
                System.err.println("No forecast data found for store " + storeId);
                return null;
            }
            
            // Convert to ForecastItem objects
            return dataList.stream()
                    .map(item -> new ForecastItem(
                            (String) item.get("date"),
                            getIntValue(item.get("predictedMean")),
                            (String) item.get("day_name"),
                            getBooleanValue(item.get("isBusyDay"))
                    ))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            System.err.println("Error parsing forecast response for store " + storeId + ": " + e.getMessage());
            return null;
        }
    }
    
    // Helper method to safely get integer value
    private static int getIntValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        return 0;
    }
    
    // Helper method to safely get boolean value
    private static boolean getBooleanValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }
    
    // Get current day name
    public static String getCurrentDayName() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.ENGLISH);
    }
    
    // New method to get same-day average forecast
    public static int getSameDayAverage(int storeId) {
        List<ForecastItem> allForecasts = getCompleteForecastData(storeId);
        if (allForecasts == null || allForecasts.isEmpty()) {
            return -1;
        }
        
        String today = getCurrentDayName();
        System.out.println("Today is: " + today + " - Filtering forecast data for same day");
        
        // Filter forecasts for the same day as today
        List<ForecastItem> sameDayForecasts = allForecasts.stream()
                .filter(item -> item.day_name.equals(today))
                .collect(Collectors.toList());
        
        if (sameDayForecasts.isEmpty()) {
            System.err.println("No " + today + " forecasts found for store " + storeId);
            return -1;
        }
        
        // Calculate average of same-day forecasts
        double average = sameDayForecasts.stream()
                .mapToInt(item -> item.predictedMean)
                .average()
                .orElse(0);
        
        System.out.println("Found " + sameDayForecasts.size() + " " + today + " forecasts for store " + storeId);
        System.out.println(today + " forecast values: " + 
                sameDayForecasts.stream()
                    .map(item -> String.valueOf(item.predictedMean))
                    .collect(Collectors.joining(", ")));
        
        return (int) Math.round(average);
    }
 
}
