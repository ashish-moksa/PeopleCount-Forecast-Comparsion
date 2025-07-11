package utils;

public class DataComparator {

    // Method to compare same-day average vs yesterday with deviation percentage
    public static void compareAndLog(String storeName, int storeId, int sameDayAverage, int yesterdayActual, String dayName) {
        int deviation = yesterdayActual - sameDayAverage; // Deviation: Actual - Expected
        double deviationPercentage = calculateDeviationPercentage(sameDayAverage, yesterdayActual);
        
        // Determine status based on deviation percentage
        String status = Math.abs(deviationPercentage) > 30 ? 
                       "Count deviated check with the high priority" : 
                       "Status Ok";

        System.out.println("Store: " + storeName + " | ID: " + storeId);
        System.out.println("Expected (" + dayName + " Avg): " + sameDayAverage + ", Actual (Yesterday): " + yesterdayActual);
        System.out.println("Deviation: " + deviation + ", Percentage Deviation: " + String.format("%+.2f", deviationPercentage) + "%");
        System.out.println("Status: " + status);
        System.out.println("------------------------------------------------");

        ExcelWriter.writeComparisonResult(storeName, storeId, sameDayAverage, yesterdayActual, 
                                        deviation, deviationPercentage, status, dayName);
    }
    
    // Logic method to calculate SIGNED deviation percentage
    public static double calculateDeviationPercentage(int expected, int actual) {
        if (expected == 0) {
            if (actual == 0) {
                return 0.0;
            } else {
                return actual > 0 ? 100.0 : -100.0;
            }
        }
        
        int difference = actual - expected;
        double deviationPercentage = ((double) difference / expected) * 100;
        
        return deviationPercentage;
    }
    
    public static void logNoData(String storeName, int storeId, String reason) {
        System.out.println("Store: " + storeName + " | ID: " + storeId);
        System.out.println("Status: " + reason);
        System.out.println("------------------------------------------------");
        
        ExcelWriter.writeNoDataResult(storeName, storeId, reason);
    }
}
