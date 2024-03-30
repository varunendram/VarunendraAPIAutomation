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

public class Payment {
	public Logger logger;

	@Test
	void payment(ITestContext context) throws IOException {
		System.out.println(" Payment test executing   ");
		logger=LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String appID = CoreUtil.getProperty("APPID");
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		Object serachRq = context.getAttribute("serachRq");
		Object selectedFlights =context.getAttribute("selectedFlights");
	

		String paymentURI = baseUrl + "/api/itinerary/Payment";
		
		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("PaymentPayload"));
		payloadData.put("searchRequest",serachRq);
		payloadData.put("selectedFlights",selectedFlights);
		logger.info("*** POST Request API: "+paymentURI);
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("PaymentSchema"))));
		Response res = given()
							.contentType("application/json")
							.header("Authorization", "")
							.header("appID", appID.toString())
							.header("SecurityToken", SecurityToken.toString())
							.body(payloadData.toString())
				      .when()
				            .post(paymentURI);
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json; charset=utf-8");

		String pciURLtoRedirect = res.jsonPath().get("pciURLtoRedirect");
		String PCI_PostUrl = pciURLtoRedirect.replace("paymentui/", "payments/0.1/cards");
		String pciSessionUrl = pciURLtoRedirect.replace("paymentui/", "payments/getSessionDetails");
		
		context.setAttribute("PCI_PostUrl", PCI_PostUrl);
		context.setAttribute("pciSessionUrl", pciSessionUrl);

	}

}
