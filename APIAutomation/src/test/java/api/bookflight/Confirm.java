package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import api.utilities.CoreUtil;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class Confirm {
	public Logger logger;

	@Test
	void confirm(ITestContext context) throws IOException {
		logger=LogManager.getLogger(this.getClass());
		String baseUrl = CoreUtil.getProperty("baseUrl");
		String appID = CoreUtil.getProperty("APPID");
		String Authorization = (String) context.getAttribute("AUTH_TOKEN");
		String SecurityToken = (String) context.getAttribute("SecurityToken");
		String confirmURI = baseUrl + "/api/itinerary/confirm";
		logger.info("***   Sending Request  *********");
		String expRespJsonSchema = new String(Files.readAllBytes(Paths.get(CoreUtil.getProperty("ConfirmSchema"))));
		Response res = given()
						.contentType("application/json")
						.header("Authorization", Authorization)
						.header("appID", appID)
						.header("SecurityToken", SecurityToken)
				      .when()
				         .post(confirmURI);
		res.then().log().all();
		
		res.then().log().all();
		logger.info("Validating Json Schema and Status code");
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expRespJsonSchema));
		res.then().statusCode(200);
		logger.info("Validating Json Content-Type");
		res.then().header("Content-Type", "application/json");
		logger.info("Asserting response time is less than 3000 millisecond");
		ValidatableResponse valRes = res.then();
		valRes.time(Matchers.lessThan(3000L));
       

		String pnrReference = res.jsonPath().get("pnrInformation.bookingReference");
		System.out.println("PNR Reference: " + pnrReference);
	}

}
