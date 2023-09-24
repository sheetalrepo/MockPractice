package com.wiremock;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Read response from file
 * Fetch value via Json path 
 *    To verify jsonpath: http://jsonpath.com/
 * 
 * @author Sheetal_Singh
 */
public class C_ReadResponseFromFile {
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private static final String END_POINT = "/readfromfile/index";
	private static WireMockServer server = new WireMockServer(PORT);

	@BeforeClass
	public void initializeServer() {
		server.start();
		WireMock.configureFor(HOST, PORT);

		//filepath: src/test/resource/__files/json/lordofthering.json
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);
		mockResponse.withBodyFile("json/lordofthering.json");
		
		//Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));
	}
	
	@Test
	public void testCode() {
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		Response response =RestAssured.
				given().
					get(testApi).
				then().statusCode(201).
					extract().response();

		Assert.assertEquals(response.jsonPath().get("glossary.title"),"Lord of the Ring");
		Assert.assertEquals(response.jsonPath().get("glossary.GlossaryDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso[1]"),"XML");
	}


	@Test
	public void testOne() {
		RestAssured.
				given().
				get("http://localhost:8080/users/1").
				then().
				assertThat().
				statusCode(200);
	}
	
	@AfterClass
	public void closeServer() {
		if (server.isRunning() && null != server) {
			System.out.println("Shutdown");
			server.shutdown();
		}
	}

}
