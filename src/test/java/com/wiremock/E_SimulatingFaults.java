package com.wiremock;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.http.Fault;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/*
 * Fault Simulations:
 * 1. Adding fixed delay in response
 * 
 * 
 */
public class E_SimulatingFaults {

	private static final String HOST = "localhost";
	private static final int PORT = 8080;
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
		mockResponse.withFixedDelay(10000); // 10 sec delay

		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		//test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		RestAssured.given().get(testApi).then().statusCode(201).extract().response();		
	}
	
	/**
	 * Case: A lognormal distribution is a pretty good approximation of long tailed latencies centered
	 * 		 on the 50th percentile
	 * https://www.wolframalpha.com/input/?i=lognormaldistribution%28log%2850%29%2C+0.9%29
	 * https://en.wikipedia.org/wiki/Log-normal_distribution
	 */
	@Test
	public void testLogNormalRandomDelay() {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);
		mockResponse.withLogNormalRandomDelay(90, 0.1); //
		
		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		//test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		RestAssured.given().get(testApi).then().statusCode(201).extract().response();		
	}
	
	
	/**
	 * Case: To simulate a stable latency of 20ms +/- 5ms, use lower = 15 and upper = 25.
	 */
	@Test
	public void testUniformRandomDelay() {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);
		mockResponse.withUniformRandomDelay(15, 25); // in ms
		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		//test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		RestAssured.given().get(testApi).then().statusCode(201).extract().response();		
	}
	
	
	/**
	 * Case: This is useful for simulating a slow network and testing deterministic timeouts.
	 * 
	 * numberOfChunks - how many chunks you want your response body divided up into
	 * totalDuration  - the total duration you want the response to take in milliseconds
	 */
	@Test
	public void testChunkedDribbleDelay() {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(201);
		mockResponse.withChunkedDribbleDelay(5, 1000);
		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		//test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		RestAssured.given().get(testApi).then().statusCode(201).extract().response();		
	}
	
	
	
	@Test
	public void testBadResponse() {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		//mockResponse.withFault(Fault.EMPTY_RESPONSE);
		mockResponse.withStatus(200);
		//mockResponse.withFault(Fault.MALFORMED_RESPONSE_CHUNK);
		mockResponse.withFault(Fault.CONNECTION_RESET_BY_PEER);// - bug not working on win os
		//mockResponse.withFault(Fault.RANDOM_DATA_THEN_CLOSE);
		
		// Mocking
		WireMock.stubFor(WireMock.get(END_POINT).willReturn(mockResponse));

		//test case
		String testApi = "http://localhost:" + PORT + END_POINT;
		System.out.println("Service to be hit: " + testApi);
		
		RestAssured.given().get(testApi).then().statusCode(200).extract().response();		
	}
}
