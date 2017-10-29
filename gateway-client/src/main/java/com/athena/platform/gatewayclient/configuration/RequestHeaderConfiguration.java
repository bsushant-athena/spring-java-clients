package com.athena.platform.gatewayclient.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.athena.platform.gatewayclient.service.AuthenticationClientService;
import com.athena.platform.gatewayclient.util.StringUtil;
import com.netflix.discovery.DiscoveryClient.DiscoveryClientOptionalArgs;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;



@Configuration
@EnableConfigurationProperties
@AutoConfigureBefore(EurekaClientAutoConfiguration.class)
public class RequestHeaderConfiguration {
	
	public final static Logger logger = Logger.getLogger(RequestHeaderConfiguration.class);
	
	@Autowired
	private AuthenticationClientService authenticationClientService;
	
	/*
	 * if value in properties file use "${eureka.client.gatewayClientId}" 
	 */
	@Value( "#{environment.GATEWAY_CLIENT_ID}" )
	private String gatewayClientId;
	
	/*
	 * if value in properties file use "${eureka.client.gatewaySecret}" 
	 */
	@Value( "#{environment.GATEWAY_SECRET}" )
	private String gatewaySecret;
	
	/*
	 * if value in properties file use "${eureka.client.gatewayScope}" 
	 */
	@Value( "#{environment.GATEWAY_SCOPE}" )
	private String gatewayScope;
	
	/*
	 * if value in properties file use "${eureka.client.gatewayTokenPath}" 
	 */
	@Value( "#{environment.GATEWAY_TOKEN_PATH}" )
	private String gatewayTokenPath;
	
	@Value( "${eureka.client.serviceUrl.defaultZone}" )
	private String gatewayServerServiceUrl;
	
	@Value( "#{environment.EUREKA_HOST}")
	private String eurekaHost;
	
	@Value( "#{environment.EUREKA_PORT}")
	private String eurekaPort;
	
	@Value( "#{environment.EUREKA_PATH}")
	private String eurekaPath;

	@Bean
	@ConditionalOnMissingBean(DiscoveryClientOptionalArgs.class)
	public  DiscoveryClientOptionalArgs discoveryClientOptionalArgs() {
		
		logger.info("Setting up DiscoveryClientOptionalArgs");
		
		/*
		logger.debug("Printing env key-values");
		System.getenv().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e
				 -> logger.info(e.getKey() + "=" + e.getValue()));
		logger.debug("GATEWAY_CLIENT_ID --> " + gatewayClientId);
		logger.debug("GATEWAY_SECRET --> " + gatewaySecret);
		logger.debug("GATEWAY_SCOPE --> " + gatewayScope);
		logger.debug("GATEWAY_CLIENT_ID --> " + gatewayTokenPath);
		*/
		
		DiscoveryClientOptionalArgs args = new DiscoveryClientOptionalArgs();
		List<ClientFilter> filters = new ArrayList<ClientFilter>();
		
		if(!(StringUtil.isNullOrEmpty(eurekaHost) || StringUtil.isNullOrEmpty(eurekaPort) || 
				StringUtil.isNullOrEmpty(eurekaPath))) {
			gatewayServerServiceUrl = "http://"+eurekaHost+ ":" + eurekaPort + eurekaPath;
			logger.info("Setting Gateway url to " + gatewayServerServiceUrl);
		}

		if (authenticationClientService.isRegistrationURLPublic(gatewayServerServiceUrl)) {
			
			logger.info("Connecting as public gateway service");
			return args;
		}

		
		logger.info("Connecting as private gateway service");
		if( StringUtil.isNullOrEmpty(gatewayClientId) || 
				StringUtil.isNullOrEmpty(gatewaySecret) || 
				StringUtil.isNullOrEmpty(gatewayScope) || 
				StringUtil.isNullOrEmpty(gatewayTokenPath) || 
				StringUtil.isNullOrEmpty(gatewayServerServiceUrl)) {
			
			logger.error("Env variable required to connect as a private gateway service is unset. "
					+ "Please set the following environment vars and then retry"
					+ "\nGATEWAY_CLIENT_ID"
					+ "\nGATEWAY_SECRET"
					+ "\nGATEWAY_SCOPE"
					+ "\nGATEWAY_TOKEN_PATH");
			throw new RuntimeException("Invalid env vars");
		}
		
		filters.add(new ClientFilter() {
			
			@Override
			public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
				
				// Authenticate service with IAM and set JWT token from response to the Authorization header in registration request
				String bearerToken = authenticationClientService.authenticateService(gatewayClientId, 
						gatewaySecret, gatewayScope, gatewayTokenPath);
				logger.debug("Adding Auth header as ClientOptionalArg with token --> \n" + bearerToken);
				cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
				return getNext().handle(cr);
			}
		});

		args.setAdditionalFilters(filters);
		return args;
	}
}
