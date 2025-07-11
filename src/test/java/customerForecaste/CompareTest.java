package customerForecaste;

import org.testng.annotations.*;
import utils.*;
import java.util.*;

public class CompareTest extends BaseAPI {

    @BeforeClass
    public void setUp() {
        loginAndGetToken();
    }

    @Test
    public void testForecastComparisonForAllStores() {
        List<Map<String, Object>> stores = StoreFetcher.getAllStores();
        String todayName = SevenDaysForecast.getCurrentDayName();
        
        System.out.println("Processing " + stores.size() + " stores...");
        System.out.println("Comparing " + todayName + " Forecast Average vs Yesterday's Actual Data");
        System.out.println("Today is: " + todayName + " - Using same-day filtering logic");

        for (Map<String, Object> store : stores) {
            int storeId;
            Object idObj = store.get("id");
            if (idObj instanceof Integer) {
                storeId = (Integer) idObj;
            } else if (idObj instanceof String) {
                storeId = Integer.parseInt((String) idObj);
            } else {
                System.err.println("Invalid store ID type for store: " + store.get("name"));
                continue;
            }
            
            String storeName = (String) store.get("name");
            if (storeName == null) storeName = "Unknown Store";

            try {
                // Get same-day average forecast (e.g., all Mondays if today is Monday)
                int sameDayAverage = SevenDaysForecast.getSameDayAverage(storeId);
                
                // Get yesterday's actual data
                int yesterdayActual = PeopleCount.getYesterdayPredictedMean(storeId);

                if (sameDayAverage >= 0 && yesterdayActual >= 0) {
                    // Both data points available - do comparison with same-day logic
                    DataComparator.compareAndLog(storeName, storeId, sameDayAverage, yesterdayActual, todayName);
                } else {
                    // Missing data - log it with reason
                    String reason = "No Data Found";
                    if (sameDayAverage < 0 && yesterdayActual < 0) {
                        reason = "No " + todayName + " Forecast Data & No Yesterday Data";
                    } else if (sameDayAverage < 0) {
                        reason = "No " + todayName + " Forecast Data";
                    } else if (yesterdayActual < 0) {
                        reason = "No Yesterday Data";
                    }
                    
                    DataComparator.logNoData(storeName, storeId, reason);
                }

            } catch (Exception e) {
                System.err.println("Error processing store: " + storeName + " (ID: " + storeId + ")");
                e.printStackTrace();
                
                DataComparator.logNoData(storeName, storeId, "Error: " + e.getMessage());
            }
        }

        ExcelWriter.saveExcel();
        System.out.println("Same-day forecast vs Yesterday comparison completed!");
        System.out.println("Filtered by: " + todayName + " forecasts only");
    }
}