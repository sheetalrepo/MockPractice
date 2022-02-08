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

import com.github.tomakehurst.wiremock.core.Options;

public class E_DynamicPort {
	private static final String HOST = "localhost";
	WireMockServer server = new WireMockServer(Options.DYNAMIC_PORT);
	int dynamicPort;
	
	@BeforeClass
	public void initializeServer() {
		//Setup
		server.start();
		dynamicPort = server.port();
		System.out.println("Dynamic Port: "+ dynamicPort);
		WireMock.configureFor(HOST, dynamicPort);
		
		//Response
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);

		//Request
		WireMock.stubFor(WireMock.get("/emps/1").willReturn(mockResponse));
	}

	@Test
	public void testCode() {
		String testApi = "http://localhost:" + dynamicPort + "/emps/1";
		System.out.println("Service to be hit: " + testApi);
		
		Response response =RestAssured.
				given().
					get(testApi).
				then().
					extract().response();
	
		Assert.assertEquals(response.getStatusCode(), 201);
	}

	@AfterClass
	public void closeServer() {
		if (server.isRunning() && null != server) {
			System.out.println("Shutdown");
			server.shutdown();
		}
	}
}
