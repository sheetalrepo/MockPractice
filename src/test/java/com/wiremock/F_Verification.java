package com.wiremock;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/*
 * Fault Simulations:
 * 1. Adding fixed delay in response
 * 
 * 
 */
public class F_Verification {

	
	
	private static final String HOST = "localhost";
	private static final int PORT = 1111;
	private static final String END_POINT = "/readfromfile/index";
	private static WireMockServer server = new WireMockServer(PORT);

	@BeforeClass
	public void initializeServer() {
		server.start();
		WireMock.configureFor(HOST, PORT);
	}

	@Test
	public void testFixedDelay() {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);

		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		// test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);

		RestAssured.given().get(testApi).then().statusCode(201).extract().response();

	}

	@Test
	public void testsss() {
		ResponseDefinitionBuilder mockResponse1 = new ResponseDefinitionBuilder();
		mockResponse1.withStatus(201);
		
//		WireMock.stubFor(WireMock.get(END_POINT).inScenario("To do list").whenScenarioStateIs("STARTED")
//				.willReturn(mockResponse1))
//				.willSetStateTo("");

		WireMock.stubFor(WireMock.get(END_POINT).inScenario("To do list").whenScenarioStateIs("STARTED")
				.willReturn(mockResponse1));
		

//		WireMock.stubFor(WireMock.get(END_POINT).inScenario("To do list")
//				.whenScenarioStateIs("Cancel newspaper item added").willReturn(aResponse().withBody("<items>"
//						+ "   <item>Buy milk</item>" + "   <item>Cancel newspaper subscription</item>" + "</items>")));
//
//		WireMockResponse response = testClient.get("/todo/items");
//		assertThat(response.content(), containsString("Buy milk"));
//		assertThat(response.content(), not(containsString("Cancel newspaper subscription")));
//
//		response = testClient.postWithBody("/todo/items", "Cancel newspaper subscription", "text/plain", "UTF-8");
//		assertThat(response.statusCode(), is(201));
//
//		response = testClient.get("/todo/items");
//		assertThat(response.content(), containsString("Buy milk"));
//		assertThat(response.content(), containsString("Cancel newspaper subscription"));
	}

}
