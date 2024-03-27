package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import api.utilities.CoreUtil;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class FlightSeachDayOne {
	public Logger logger;
	@Test
	void searchFlight(ITestContext context) throws IOException {
		logger=LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String appID = CoreUtil.getProperty("APPID");
		String cabin = CoreUtil.getProperty("cabin");
		String origin = CoreUtil.getProperty("origin");
		String dest = CoreUtil.getProperty("dest");
		String date = CoreUtil.getProperty("date");

		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("FlightSearchPayload"));

		payloadData.getJSONArray("searchCriteria").getJSONObject(0).put("origin", origin);
		payloadData.getJSONArray("searchCriteria").getJSONObject(0).put("dest", dest);
		payloadData.getJSONArray("searchCriteria").getJSONObject(0).put("date", date);
		payloadData.getJSONArray("searchCriteria").getJSONObject(0).put("cabin", cabin);

		String searchFlightURI = baseUrl + "/api/flights/1";
		logger.info("***   Sending Request  *********");
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("FlightSearchResponse"))));
		Response res = given()
						.contentType("application/json")
						.header("Authorization", (String)context.getAttribute("AUTH_TOKEN"))
						.header("appID", appID)
						.body(payloadData.toString())
					.when()
					   .post(searchFlightURI);
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json");
		logger.info("Asserting response time is less than 3000 millisecond");
		ValidatableResponse valRes = res.then();
		valRes.time(Matchers.lessThan(3000L));

		/*
		 * Setting response to global access serachRq selectedFlights SecurityToken
		 * 
		 */

		String SecurityToken = res.headers().get("SecurityToken").toString();
		String serachRq = res.body().asString();
		String selectedFlights = res.jsonPath().get("lowestTotalFare.lowestFlights[0]");

		context.setAttribute("serachRq", serachRq);
		context.setAttribute("selectedFlights", selectedFlights);
		context.setAttribute("SecurityToken", SecurityToken);

	}

}
