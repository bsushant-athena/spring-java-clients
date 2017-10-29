package com.athena.platform.gatewayclient.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.athena.platform.gatewayclient.model.JWTModel;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.representation.Form;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Client.class})
public class AuthenticationClientServiceTest {
	
	@InjectMocks
	AuthenticationClientService authenticationClientService;
	
	@Test
	public void authenticateClientOKStatus() {
		
		JWTModel model = new JWTModel();
		model.setAccess_token("tokenValue");
		ClientResponse mockResponse = mock(ClientResponse.class);
    	when(mockResponse.getStatus()).thenReturn(200);
    	when(mockResponse.getEntity(JWTModel.class)).thenReturn(model);
    	
    	PowerMockito.mockStatic(Client.class);
    	Client mockClient = mock(Client.class);
    	WebResource mockWebResource = mock(WebResource.class);
    	Builder mockBuilder = mock(Builder.class);
    	
    	when(Client.create()).thenReturn(mockClient);
    	when(mockClient.resource(Mockito.anyString())).thenReturn(mockWebResource);
    	when(mockWebResource.type(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.header(Mockito.anyString(), Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.post(Mockito.eq(ClientResponse.class), Matchers.any(Form.class))).thenReturn(mockResponse);
    	
    	assertNotNull(authenticationClientService.authenticateService("clientId", "clientSecret", "scope", "http://localhost:18888/public/iam/v1/token"));
	}
	
	@Test(expected = RuntimeException.class)
	public void authenticateClientFail400() {
		
    	ClientResponse mockResponse = mock(ClientResponse.class);
    	when(mockResponse.getStatus()).thenReturn(400);
    	
    	PowerMockito.mockStatic(Client.class);
    	Client mockClient = mock(Client.class);
    	WebResource mockWebResource = mock(WebResource.class);
    	Builder mockBuilder = mock(Builder.class);
    	
    	when(Client.create()).thenReturn(mockClient);
    	when(mockClient.resource(Mockito.anyString())).thenReturn(mockWebResource);
    	when(mockWebResource.type(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.header(Mockito.anyString(), Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.post(Mockito.eq(ClientResponse.class), Matchers.any(Form.class))).thenReturn(mockResponse);
    	
    	authenticationClientService.authenticateService("clientId", "clientSecret", "scope", "http://localhost:18888/public/iam/v1/token");
    	
	}
	
	@Test(expected = RuntimeException.class)
	public void authenticateClientFail401() {
		
    	ClientResponse mockResponse = mock(ClientResponse.class);
    	when(mockResponse.getStatus()).thenReturn(401);
    	
    	PowerMockito.mockStatic(Client.class);
    	Client mockClient = mock(Client.class);
    	WebResource mockWebResource = mock(WebResource.class);
    	Builder mockBuilder = mock(Builder.class);
    	
    	when(Client.create()).thenReturn(mockClient);
    	when(mockClient.resource(Mockito.anyString())).thenReturn(mockWebResource);
    	when(mockWebResource.type(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.header(Mockito.anyString(), Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.post(Mockito.eq(ClientResponse.class), Matchers.any(Form.class))).thenReturn(mockResponse);
    	
    	authenticationClientService.authenticateService("clientId", "clientSecret", "scope", "http://localhost:18888/public/iam/v1/token");
 	}
	
	@Test(expected = RuntimeException.class)
	public void authenticateClientFail403() {
		
    	ClientResponse mockResponse = mock(ClientResponse.class);
    	when(mockResponse.getStatus()).thenReturn(403);
    	
    	PowerMockito.mockStatic(Client.class);
    	Client mockClient = mock(Client.class);
    	WebResource mockWebResource = mock(WebResource.class);
    	Builder mockBuilder = mock(Builder.class);
    	
    	when(Client.create()).thenReturn(mockClient);
    	when(mockClient.resource(Mockito.anyString())).thenReturn(mockWebResource);
    	when(mockWebResource.type(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.header(Mockito.anyString(), Mockito.anyString())).thenReturn(mockBuilder);
    	when(mockBuilder.post(Mockito.eq(ClientResponse.class), Matchers.any(Form.class))).thenReturn(mockResponse);
    	
    	authenticationClientService.authenticateService("clientId", "clientSecret", "scope", "http://localhost:18888/public/iam/v1/token");
 	}
	
	@Test
	@Ignore
	public void isRegistrationURLPublicWithValidRelativeURL() {
		
		assertTrue(authenticationClientService.isRegistrationURLPublic("/public/eureka/apps"));
	}
	
	@Test
	@Ignore
	public void isRegistrationURLPublicWithInvalidRelativeURL() {
		
		assertFalse(authenticationClientService.isRegistrationURLPublic("/eureka/apps"));
	}

	@Test
	public void isRegistrationURLPublicWithValidURL() {
		
		assertTrue(authenticationClientService.isRegistrationURLPublic("http://localhost:8888/public/eureka/apps"));
	}
	
	@Test
	public void isRegistrationURLPublicWithInvalidURL() {
		
		assertFalse(authenticationClientService.isRegistrationURLPublic("http://localhost:8888/eureka/apps"));		
	}
	
	@Test
	public void isRegistrationURLPublicWithValidLBURL() {
		
		assertTrue(authenticationClientService.isRegistrationURLPublic("http://api-development.dcos-lb-dev-us-east-1.aws.athenahealth.com/public/eureka"));
	}
	
	@Test
	public void isRegistrationURLPublicWithInvalidLBURL() {
		
		assertFalse(authenticationClientService.isRegistrationURLPublic("http://api-development.dcos-lb-dev-us-east-1.aws.athenahealth.com/eureka"));		
	}
	
}
