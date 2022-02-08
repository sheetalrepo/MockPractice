package com.todo;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;


/**
 * Basic idea how to implement a Mock in any project
 * reference: https://www.ivankrizsan.se/2020/10/06/wiremock-request-logging/
 * @author Sheetal_Singh
 *
 */
public class TestClass {

	public static final WireMockServer wmServer = new WireMockServer(Options.DYNAMIC_PORT);
	public static List<Request> mockRequests = new ArrayList<>();
	MockManager mockManager = new MockManager();
	public static final String ENDPOINT_URL = "/thirdpartyuserurl";
    protected ObjectMapper objMapper = new ObjectMapper();

	
	/**
	 * Before starting any test mock all responses
	 * given in folder resources/todo
	 */
	@BeforeClass
	public void init() {
		mockManager.mockBaseResponses();
	}
	
	@AfterClass
	public void tearDown() {
		mockManager.resetAll();
	}
	
	public static WireMockServer getServer(){
		createRequestListeners();
		
		if (!wmServer.isRunning()) {
			wmServer.start();
        }
		return wmServer;
	}
	
	/**
	 * In case we want to analyze request sent to Wiremock server(Pixel)
	 * from our service. We need to create listeners before we start wiremock
	 * 
	 */
	public static void createRequestListeners() {
		wmServer.addMockServiceRequestListener((request, response)
                -> mockRequests.add(LoggedRequest.createFrom(request)));

	}
	
	
	/**
	 * RequestPojo : Assume we have pojo class which maps request of 3rd party 
	 * 
	 * No need to mock case as before method will do all basic mocking
	 */
	@Test
	public void verifyRequestToMockServer() {
		String requestBody = "";
		
		//We analyzed all request sent to 3rd party and filter out which we need
		for(Request request : mockRequests){
            if(ENDPOINT_URL.equals(request.getUrl())){
            	requestBody = request.getBodyAsString();
                break;
            }
        }

		//Map request to Pojo for further fetching data
		//RequestPojo requestPojo = objMapper.readValue(requestBody, RequestPojo.class);

	}
	
	/**
	 * Mocking specific case
	 */
	@Test
	public void abc() {
		mockManager.mockResponse("invalid.xml");
	}
	
}
