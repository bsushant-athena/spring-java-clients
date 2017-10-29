package com.athena.platform.gatewayclient.sample;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kavinash on 08/22/17.
 */
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.athena.platform.gatewayclient.main.EnableGatewayService;
import com.athena.platform.gatewayclient.main.GatewayClientBean;

@SpringBootApplication
@ComponentScan("com.athena.platform.gatewayclient")
@RestController
@EnableGatewayService
public class AthenaEurekaClient {

	
	public static class Info {
		public List<String> services;
		public Map<String, List<ServiceInstance>> instances = new HashMap<String, List<ServiceInstance>>();
		public String description;
	}

	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/")
	public String index() {
		 System.getenv().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e
		 -> System.out.println(e.getKey() + "=" + e.getValue()));
		return "Hello Eureka";
	}

	@RequestMapping("/services")
	public List<String> getRegisteredServices() {
		return discoveryClient.getServices();
	}

	@RequestMapping("/infos")
	public Info getInfo() {
		Info result = new Info();

		result.services = discoveryClient.getServices();
		for (String s : result.services) {
			result.instances.put(s, discoveryClient.getInstances(s));
		}
		result.description = discoveryClient.description();
		return result;
	}

	@RequestMapping("/setCookie")
	public String setCookie(HttpServletResponse response) {
		response.addCookie(new Cookie("testCookie", "BAM"));
		return "Set Cookie page";
	}

	@RequestMapping("/verifyCookie")
	public String verifyCookie(@CookieValue(value = "testCookie", defaultValue = "hello") String fooCookie) {
		if (fooCookie.equals("BAM")) {
			return "Verified";
		} else
			return "Uh-Oh";
	}

	@RequestMapping("/setHeader")
	public String setHeader(HttpServletResponse response) {
		response.setHeader("X-MyCustom-Header", "C3PO");
		return "Setting headers";
	}

	@RequestMapping("/readHeader")
	public String readHeader(@RequestHeader(value = "X-MyCustom-Header", defaultValue = "hello") String userAgent) {
		if (userAgent.equals("C3PO")) {
			return "Success";
		} else
			return "Uh-Oh";
	}

	@RequestMapping("/redirect")
	public String getRedirect() {
		return "redirect:Welcome"; // redirect not working so using name as
									// Welcome
		// need to get redirect working for correct execution of test case
	}
	
	@RequestMapping("/Welcome")
	public String getfinal() {
		return "Welcome to Java service!!!";
	}

	public static void main(String[] args) throws IOException {
		new GatewayClientBean().initialize("/");
		SpringApplication.run(AthenaEurekaClient.class, args);
	}

}
