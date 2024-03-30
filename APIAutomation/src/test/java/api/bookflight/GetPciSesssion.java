package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import api.utilities.CoreUtil;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class GetPciSesssion {
	public Logger logger;

	@Test
	void getPciSession(ITestContext context) throws IOException {
		logger=LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		String pciSessionURI = (String) context.getAttribute("pciSessionUrl");
		Object serachRq =  context.getAttribute("serachRq");
		Object selectedFlights =  context.getAttribute("selectedFlights");
		String confirmUrl=baseUrl+"/api/itinerary/confirm";
		
		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("PciSessionPayload"));
		payloadData.put("searchRequest",serachRq);
		payloadData.put("selectedFlights",selectedFlights);
		payloadData.put("confirmUrl",confirmUrl);
		logger.info("*** GET Request API: "+pciSessionURI);
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("PciSessionSchema"))));
		Response res = given()
							.contentType("application/json")
							.header("SecurityToken", SecurityToken)
							.body(payloadData.toString())
				       .when()
				          .get(pciSessionURI);
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json");

		String sessionId = res.jsonPath().get("sessionId");
		String paymentId = res.jsonPath().get("paymentId");
		String currency = res.jsonPath().get("currency");
		String amount = res.jsonPath().get("amountDue");

		context.setAttribute("sessionId", sessionId);
		context.setAttribute("paymentId", paymentId);
		context.setAttribute("currency", currency);
		context.setAttribute("amount", amount);
	}
}
