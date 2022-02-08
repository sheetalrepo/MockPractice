package com.wiremock;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;

import org.apache.http.HttpStatus;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
/**
 * Condition applied in Req Header
 * withHeader("Accept", matching("condition")
 *
 * @author Sheetal_Singh
 *
 */
public class D_ConditionalMocking {
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private static final String END_POINT = "/movies/1";
	private static WireMockServer server = new WireMockServer(PORT);

	@BeforeClass
	public void initializeServer() {
		server.start();
		WireMock.configureFor(HOST, PORT);

		//Response 1
		ResponseDefinitionBuilder mockResponse1 = new ResponseDefinitionBuilder();
		mockResponse1.withStatus(503);
		mockResponse1.withHeader("Content-Type", "text/html"); //text
		mockResponse1.withBody("Service Not Available");
		
		
		//Response 2
		ResponseDefinitionBuilder mockResponse2 = new ResponseDefinitionBuilder();
		mockResponse2.withStatus(200);
		mockResponse2.withHeader("Content-Type", "application/json"); //json
		mockResponse2.withBody("{\"Current-Status\": \"running\"}");
		mockResponse2.withFixedDelay(2500);
		
		//Same Endpoint for both response
		//condition depends on what we sent in request header - text/plain or application/json
		WireMock.stubFor(WireMock.get(END_POINT).withHeader("Accept", matching("text/plain")).willReturn(mockResponse1));
		WireMock.stubFor(WireMock.get(END_POINT).withHeader("Accept", matching("application/json")).willReturn(mockResponse2));
		

		//Mocking pattern 2
//		stubFor(get(urlEqualTo(END_POINT)).withHeader("Accept", matching("application/json"))
//				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
//						.withBody("{\"serviceStatus\": \"running\"}").withFixedDelay(2500)));
	
	}

	
	/**
	 * Req headers is sent with value text/plain
	 * Hence Mock response 1 will be returned
	 */
	//@Test
	public void testCode1() {
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		Response r = RestAssured.
				given().
	            	header(new Header("Accept", "text/plain")).
	            when().
	            	get(testApi);
		
	   Assert.assertEquals(r.statusCode(), HttpStatus.SC_SERVICE_UNAVAILABLE);
	}
	
	//@Test
	public void testCode2() {
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		Response r = RestAssured.given()
	            .header(new Header("Accept", "application/json"))
	            .when()
	            .get(testApi);
		
		//Response r = given().header(new Header("Accept", "application/json")).when().get("/some/thing");
		Assert.assertEquals(r.statusCode(), HttpStatus.SC_OK);
		Assert.assertEquals(r.jsonPath().getString("Current-Status"), "running");
	}

	@Test
	public void testCode3() {

		String testApi = "http://localhost:" + PORT + END_POINT + "/wrongapi";
		System.out.println("Service to be hit: " + testApi);
		
		Response r = RestAssured.given()
	            .header(new Header("Accept", "application/json"))
	            .when()
	            .get(testApi);
		//Response r = given().header(new Header("Accept", "application/json")).when().get("/some/thing/is/wrong");
		Assert.assertEquals(r.statusCode(), HttpStatus.SC_NOT_FOUND);
	}
	
	

	@AfterClass
	public void closeServer() {
		if (server.isRunning() && null != server) {
			System.out.println("Shutdown");
			server.shutdown();
		}
	}

}
