package customerForecaste; 

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BaseAPI {
    protected static String token;

    public static void loginAndGetToken() {
        RestAssured.baseURI = "https://api.moksa.ai"; 
        final String endPoint = "/auth/login";
        final String requestBody = "{\r\n"
        		+ "  \"email\":\"ashish.qa@moksa.ai\",\"password\":\"Ashish@moksa\"\r\n"
        		+ "}";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(endPoint);

        response.then().statusCode(200);
        token = response.jsonPath().getString("data.token");
        System.out.println("Token generated successfully: " + token);
    }
}