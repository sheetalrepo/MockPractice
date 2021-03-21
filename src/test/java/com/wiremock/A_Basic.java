package com.wiremock;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.restassured.RestAssured;


/**
 * 
 * 1. start the wiremock server:    java -jar wiremock-jre8-standalone-2.27.2.jar
 * 2. add req response mapping: \Documents\wiremock_standalone_jar\mappings
 * 3. run the test 
 * 
 * @author Sheetal_Singh
 */
public class A_Basic {

	
	//@Test
	public void testOne() {
		RestAssured.
			given().
				get("http://localhost:8080/users/1").
			then().
				assertThat().
				statusCode(200);
	}

	//@Test
	public void testTwo() {
		RestAssured.
			given().
				get("http://localhost:8080/users/2").
			then().
				assertThat().
				statusCode(201);
	}
	
	
	@Test
	public void testResponseHeaders() {
		String contentType = 
			RestAssured.
			given().
				get("http://localhost:8080/users/3").
			then().
				assertThat().
				statusCode(200).
			extract().
				header("Content-Type");
		
		System.out.println("testResponseHeaders: "+contentType);
		Assert.assertEquals("text/plain", contentType);	
	}

}
