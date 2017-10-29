package com.athena.platform.gatewayclient.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.athena.platform.gatewayclient.model.JWTModel;
import com.athena.platform.gatewayclient.util.AuthConstants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

@Component
public class AuthenticationClientService {

	public final static Logger logger = Logger.getLogger(AuthenticationClientService.class);
	
	public String authenticateService(String clientId, String clientSecret, String scope, String tokenPath) {
		
		JWTModel output = null;
		try {

			Client client = Client.create();

			Form form = new Form();
			form.add(AuthConstants.GRANT_TYPE_KEY, AuthConstants.GRANT_TYPE_VALUE);
			form.add(AuthConstants.SCOPE_KEY, scope);
			
			WebResource webResource = client.resource(tokenPath);
			webResource.addFilter(new HTTPBasicAuthFilter(clientId, clientSecret));
			
			ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON)
					.header(AuthConstants.HEADER_X_SERVICE_NAME, AuthConstants.HEADER_VALUE_X_SERVICE_NAME).post(ClientResponse.class, form);

			
			if (response.getStatus() != 200) {
				logger.error("Error while fetching JWT token: " + response.getStatus());
				throw new RuntimeException();
			}

			output = response.getEntity(JWTModel.class);
			return output.getAccess_token();

		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException("Error while fetching JWT from IAMs service");
		}
	}
	
	/*
	 * We presume that gateway-server-url should be a complete URL 
	 * since it is being populated if a complete url is not given - 
	 * http://10.8.110.49:18888/public/iam/oauth2/v1/token 
	 */
	public boolean isRegistrationURLPublic(String url) {
		
		try {
			URL gatewayServerURL = new URL(url);
			logger.debug("Gateway url --> " + gatewayServerURL.getPath());
			if(gatewayServerURL.getPath().startsWith("/public")) {
				return true;
			} else {
				return false;
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

}
