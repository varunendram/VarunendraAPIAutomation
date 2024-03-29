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

public class Paylater {
	public Logger logger;
	@Test
	void paylater(ITestContext context) throws IOException {
		System.out.println(" Paylater test executing   ");
		logger=LogManager.getLogger(this.getClass());
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		String sessionId = (String) context.getAttribute("sessionId");
		String paymentId = (String) context.getAttribute("paymentId");
		String currency = (String) context.getAttribute("currency");
		String amount = (String) context.getAttribute("amount");
		String paylaterURI = (String) context.getAttribute("PCI_PostUrl");
		
		JSONObject payloadData = CoreUtil.getJSONObject(CoreUtil.getProperty("PaylaterPayload"));
		payloadData.put("amount",amount);
		payloadData.put("currency",currency);
		payloadData.put("paymentId",paymentId);
		payloadData.put("sessionId",sessionId);
		logger.info("***   Sending Request  *********");
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("PaylaterSchema"))));
		Response res = given()
							.contentType("application/json")
							.header("SecurityToken", SecurityToken)
							.body(payloadData.toString())
				       .when()
				          .post(paylaterURI);
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json");
	}

}
