package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.json.JSONArray;
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
		System.out.println("FLight Search test executing   ");
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

		String searchFlightURI = baseUrl + "/api/flights/1";
		logger.info("***   Sending Request  *********");
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("FlightSearchResponse"))));
		Response res = given()
						.contentType("application/json")
						.header("Authorization", "")
						.header("appID", appID)
						.body(payloadData.toString())
					.when()
					   .post(searchFlightURI);
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json; charset=utf-8");
		logger.info("Asserting response time is less than 3000 millisecond");
		ValidatableResponse valRes = res.then();
		valRes.time(Matchers.lessThan(3000L));


		String SecurityToken = res.headers().get("SecurityToken").toString();
		SecurityToken = SecurityToken.replace("securityToken=", "");
		
		String serachRq = payloadData.toString();
		System.out.println("serachRq: "+serachRq);
		
		JSONObject respBody= new JSONObject(res.getBody().asString());
		JSONObject segment_0=(JSONObject)((JSONArray)respBody.get("segments")).get(0);
		JSONObject selectedFlights=(JSONObject)((JSONArray)segment_0.get("flights")).get(0);
		JSONObject selectedFare=(JSONObject)((JSONArray)selectedFlights.get("fareTypes")).get(0);
	
		selectedFlights.put("selectedFare", selectedFare);
		
		JSONObject ab= (JSONObject)selectedFlights;
		ab.put("selectedFare", selectedFare);
		JSONArray obj=new JSONArray();
		obj.put(0,selectedFlights);
		
		context.setAttribute("serachRq", payloadData);
		context.setAttribute("selectedFlights", obj);
		context.setAttribute("SecurityToken", SecurityToken);
		System.out.println("SecurityToken: "+SecurityToken);

	}

}
