package api.bookflight;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import api.utilities.CoreUtil;
import io.restassured.response.Response;

public class CreateToken {
	public Logger logger;

	@Test
	void getAuthToken(ITestContext context) throws IOException {
		logger=LogManager.getLogger(this.getClass());
		String auth_url_base = CoreUtil.getProperty("auth_url_base");
		String pwd = CoreUtil.getProperty("pwd");
		String userName = CoreUtil.getProperty("userName");
		String tokenURI = auth_url_base + "/connect/token";
		logger.info("*** POST Request API: "+tokenURI);
		Response res =
				given()
				 .header("Content-Type", "application/x-www-form-urlencoded")
				 .formParam("grant_type","password")
				 .formParam("client_id","TA_FZ_P")
				 .formParam("client_secret","secret")
				 .formParam("scope","sprintauthapi travelagencyapi")
				 .formParam("password",pwd)
				 .formParam("username",userName)
				.when()
				  .post(tokenURI);
		res.then().log().all();
		logger.info("Skipping Validation of Create Token API");
//		res.then().statusCode(200);
		String AUTH_TOKEN="";
		context.setAttribute("AUTH_TOKEN", AUTH_TOKEN);
	}

}
