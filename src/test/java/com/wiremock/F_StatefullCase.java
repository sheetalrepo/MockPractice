package com.wiremock;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/*
 * Fault Simulations:
 * 1. Adding fixed delay in response
 * 
 * 
 */
public class F_StatefullCase {

	private static final String HOST = "localhost";
	private static final int PORT = 1111;
	private static final String END_POINT = "/myendpoint/1";
	private static WireMockServer server = new WireMockServer(PORT);

	
	private static final String THIRD_STATE = "third";
	private static final String SECOND_STATE = "second";
	private static final String TIP_01 = "finally block is not called when System.exit()"
			+ " is called in the try block";
	private static final String TIP_02 = "keep your code clean";
	private static final String TIP_03 = "use composition rather than inheritance";
	private static final String TEXT_PLAIN = "text/plain";

	static int port = 9999;

	
	@BeforeClass
	public void initializeServer() {
		server.start();
		WireMock.configureFor(HOST, PORT);
	}

	
	@Test
	public void changeStateOnEachCallTest() throws IOException {
		createWireMockStub(Scenario.STARTED, SECOND_STATE, TIP_01);
		createWireMockStub(SECOND_STATE, THIRD_STATE, TIP_02);
		createWireMockStub(THIRD_STATE, Scenario.STARTED, TIP_03);

	}

	private void createWireMockStub(String currentState, String nextState, String responseBody) {
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
		mockResponse.withStatus(200);
		mockResponse.withHeader("Content-Type", TEXT_PLAIN);	
		mockResponse.withBody(responseBody);
		
		WireMock.stubFor(WireMock.get(END_POINT).inScenario("java tips").whenScenarioStateIs(currentState)
				.willSetStateTo(nextState)
				.willReturn(mockResponse));
	}

}
