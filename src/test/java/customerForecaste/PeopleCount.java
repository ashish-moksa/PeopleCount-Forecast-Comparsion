package customerForecaste;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;

public class PeopleCount extends BaseAPI {

    // Method to get yesterday's data 
    public static int getYesterdayPredictedMean(int storeId) {
        String endpoint = "/people/getPeopleCount/" + storeId;

        RequestSpecification request = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .header("datetype", "yesterday")
                .header("pagenumber", "1")
                .header("pagepersize", "10");

        Response response = request.get(endpoint);

        if (response.statusCode() != 200) {
            System.err.println("Error: Store ID " + storeId + " failed with status " + response.statusCode());
            return -1;
        }

        try {
            // Checks if data.data exists and is not empty
            List<Map<String, Object>> dataList = response.jsonPath().getList("data.data");
            
            if (dataList == null || dataList.isEmpty()) {
                System.err.println("Warning: No yesterday data found for store ID " + storeId);
                return -1;
            }
            
            // Checks if the first item has predictedmean field
            Map<String, Object> firstItem = dataList.get(0);
            if (firstItem == null || !firstItem.containsKey("noofcustomers")) {
                System.err.println("Warning: No Noofcustomers field found for store ID " + storeId);
                return -1;
            }
            
            // Safely gets the predictedmean value
            Object predictedMeanObj = firstItem.get("noofcustomers");
            if (predictedMeanObj == null) {
                System.err.println("Warning: yesterday total count is null for store ID " + storeId);
                return -1;
            }
            
            // Handle different number types
            if (predictedMeanObj instanceof Integer) {
                return (Integer) predictedMeanObj;
            } else if (predictedMeanObj instanceof Double) {
                return ((Double) predictedMeanObj).intValue();
            } else if (predictedMeanObj instanceof String) {
                try {
                    return Integer.parseInt((String) predictedMeanObj);
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Cannot parse yesterday predictedmean as integer for store ID " + storeId + ": " + predictedMeanObj);
                    return -1;
                }
            }
            
            System.err.println("Warning: Unexpected yesterday predictedmean type for store ID " + storeId + ": " + predictedMeanObj.getClass());
            return -1;
            
        } catch (Exception e) {
            System.err.println("Error parsing yesterday response for store ID " + storeId + ": " + e.getMessage());
            System.err.println("Response body: " + response.getBody().asString());
            return -1;
        }
    }
    
}