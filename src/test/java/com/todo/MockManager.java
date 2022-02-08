package com.todo;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * Mock Manager Sample
 * Assume Pixel is 3rd party and we need to mock its endpoints e.g. /thirdpartyuserurl
 * BASE_RESPONSES_DIR = folder containing all mock response
 * INVALID_PWD_FILE = mock response for specific scenario
 * 
 * @author Sheetal_Singh
 *
 */
public class MockManager {
    private static final String PIXEL_USER_URL = "/thirdpartyuserurl";
    private static final String PIXEL_RESPONSES_DIR = "todo/";
    private static final String INVALID_PWD_FILE = "invalid.xml";
    WireMockServer wmServer; // need to initialize
    
    
    /**
     * Standard Mock Method taking 3 params
     */
	public void mockResponse(String requestBodyMatching, String responseFileName, int statusCode) {
        var responsePath = PIXEL_RESPONSES_DIR + responseFileName;
        var responseBody = FileUtils.getResourceAsString(responsePath);
        var requestRegex = String.format(".*%s.*", requestBodyMatching);
        
        wmServer.stubFor(post(urlMatching(PIXEL_USER_URL))
                .withRequestBody(matching(requestRegex))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader(CONTENT_TYPE, APPLICATION_XML)
                        .withBody(responseBody)
                )
        );
    }
	
	//Overloaded methods
	public void mockResponse(String requestBodyMatching, String responseFileName) {
        mockResponse(requestBodyMatching, responseFileName, HttpStatus.SC_OK);
    }

	//Overloaded methods
    public void mockResponse(String responseFileName) {
        mockResponse(StringUtils.EMPTY, responseFileName);
    }
    
    //To mock specific file
    public void mockInvalidResponse() {
        mockResponse(INVALID_PWD_FILE);
    }

    
    /**
     * This method will load all mocked file from folder todo before starting any test
     */
    public void mockBaseResponses() {
        FileUtils.getAvailableResourceFiles(PIXEL_RESPONSES_DIR)
                .forEach(file -> {
                    var username = extractUserName(file.getFileName().toString());
                    mockResponse(username, file.getFileName().toString());
                });
    }
	
    public void resetAll() {
    	wmServer.resetRequests();
    	wmServer.resetMappings();
    }
    
    /*
     * Mock Response File Name example = device_user123.xml
     * 
     * this method will return = user123
     */
    private String extractUserName(String filename) {
        return filename.substring(filename.indexOf("_") + 1, filename.indexOf("."));
    }
}
