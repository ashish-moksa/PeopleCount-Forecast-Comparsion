package customerForecaste;

import java.util.List;
import java.util.Map;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class StoreFetcher extends BaseAPI {

    public static List<Map<String, Object>> getAllStores() {
        String endpoint = "/store/getAllStoresForDropdown";

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get(endpoint);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch stores. Status: " + response.statusCode());
        }

        // TO Extract list from nested "data.data"
        List<Map<String, Object>> storeList = response.jsonPath().getList("data.data");
        if (storeList == null || storeList.isEmpty()) {
            throw new RuntimeException("No stores found or unexpected JSON structure.");
        }

        return storeList;
    }
}
