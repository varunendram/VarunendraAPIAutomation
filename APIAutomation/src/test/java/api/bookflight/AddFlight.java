package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import api.utilities.CoreUtil;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class AddFlight {
	public Logger logger;

	@Test
	void addFlight(ITestContext context) throws IOException {
		System.out.println(" Add FLight test executing   ");
		logger = LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String appID = CoreUtil.getProperty("APPID");
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		Object serachRq = context.getAttribute("serachRq");
		Object selectedFlights = context.getAttribute("selectedFlights");
		System.out.println("selectedFlights:   " + selectedFlights);
		String addFlightURI = baseUrl + "/api/itinerary/AddFlight";
		logger.info("***   addFlight URI Request  *********");
		System.out.println("SecurityToken: " + SecurityToken);
		String Authorization = "x";
		System.out.println("addFlightURI: " + addFlightURI);

		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("AddFlightPayload"));
		payloadData.put("searchRequest", serachRq);
		payloadData.put("selectedFlights",selectedFlights);
		
		System.out.println("***************************");
		System.out.println("Add Flight payloadData: " + payloadData.toString());

		String data = payloadData.toString();
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("AddFlightResponse"))));
		Response res = given().contentType("application/json")
						.header("Authorization", "")
				.header("appID", appID.toString()).
				header("SecurityToken", SecurityToken.toString())
			    .body(payloadData.toString())
				.when()
				  .post(addFlightURI);
		
		
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json; charset=utf-8");
		logger.info("Asserting response time is less than 3000 millisecond");
		ValidatableResponse valRes = res.then();
		valRes.time(Matchers.lessThan(3000L));
		String AddFlighRS = res.jsonPath().toString();
		context.setAttribute("AddFlighRS", AddFlighRS);

	}

}
