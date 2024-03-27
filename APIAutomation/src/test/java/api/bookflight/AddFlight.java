package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.util.FileUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONObject;
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
		logger=LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String appID = CoreUtil.getProperty("APPID");
		String Authorization = (String) context.getAttribute("AUTH_TOKEN");
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		String serachRq = (String) context.getAttribute("serachRq");
		String selectedFlights = (String) context.getAttribute("selectedFlights");
		String addFlightURI = baseUrl + "/api/itinerary/AddFlight";
		logger.info("***   addFlight URI Request  *********");
		
		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("AddFlightPayload"));
		payloadData.put("searchRequest",serachRq);
		payloadData.put("selectedFlights",selectedFlights);
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("AddFlightResponse"))));
		Response res = given()
						.contentType("application/json")
						.header("Authorization", Authorization)
						.header("appID", appID)
						.header("SecurityToken", SecurityToken)
						.body(payloadData.toString())
				      .when()
				         .post(addFlightURI);
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
		 * Set below response to global access AddFlighRS
		 * 
		 */
		String AddFlighRS = res.jsonPath().get("AddFlighRS");
		context.setAttribute("AddFlighRS", AddFlighRS);
		
	}
	

	@Test
	void lowestprice() {
		JSONObject jo=CoreUtil.getJSONObject(CoreUtil.getProperty("FlightSearchResponse"));
		
		JSONObject lowestTotal=new JSONObject();
		lowestTotal.put("segments", jo.getJSONArray("segments"));
		lowestTotal.put("lowestTotalFare", jo.getJSONObject("lowestTotalFare"))	;
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println(lowestTotal.length());
		System.out.println("+++++++++++++++++++++++++++++++++");
		
	}
	
//	String setLowestFareFlight(JSONObject obj,cabin)
//	{
//	   
//	   var lowestTotal = _.pick(response, ['segments', 'lowestTotalFare']);
//	    
//	   lowest=  _.first(lowestTotal.lowestTotalFare, function(fare) {
//	       
//	    return fare.cabin.toUpperCase() ===cabin.toUpperCase() ;
//	});

}
