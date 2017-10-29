package com.athena.platform.gatewayclient.main;

import java.io.IOException;
import java.util.Optional;

import org.apache.log4j.Logger;

/**
 * Created by mgagliardi on 3/12/17.
 */
public class GatewayClientBean {

	public final static Logger logger = Logger.getLogger(GatewayClientBean.class);

    public void initialize(String bootstrapPath) throws IOException {
        // set the properties from the system.properties file
        new com.athena.platform.config.SystemPropertiesSetter().setAll(Optional.ofNullable(bootstrapPath));
        // now check for special env variables to propagate to eureka
        // PORT0
        Optional<String> port0 = Optional.ofNullable(System.getenv().get("PORT0"));
        port0.ifPresent(port -> {
            System.getProperties().setProperty("eureka.instance.nonSecurePort", port);
            logger.info("setting eureka.instance.nonSecurePort to " + port0.get() + " from env property PORT0");
        });

        Optional<String> host = Optional.ofNullable(System.getenv().get("HOST"));
        host.ifPresent(hostIp -> {
            System.getProperties().setProperty("eureka.instance.ipAddress", hostIp);
            logger.info("setting eureka.client.ipAddress to " + host.get() + " from env property HOST");
        });

        
        /* 
         * Commented this out for the time being as we can uncomment this if need to prioritize again
         * 
        Optional<String> apiGatewayRootUrl = Optional.ofNullable(System.getenv().get("API_GATEWAY_ROOT_URL"));
        apiGatewayRootUrl.ifPresent(gatewayUrl -> {
            System.getProperties().setProperty("eureka.client.serviceUrl.defaultZone", gatewayUrl);
            System.out.println("setting eureka.client.serviceUrl.defaultZone to " + apiGatewayRootUrl.get() + " from env property API_GATEWAY_ROOT_URL");
        });
        */
    }

}
