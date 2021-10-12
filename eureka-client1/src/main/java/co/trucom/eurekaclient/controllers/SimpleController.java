package co.trucom.eurekaclient.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/")
public class SimpleController {
	
	@Autowired
	private LoadBalancerClient loadBalancer;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("hello")
	public ResponseEntity<String> sayHello() {
		return new ResponseEntity<>("Hi", HttpStatus.OK);
	}
	
	@GetMapping("callother")
	public ResponseEntity<String> callOtherClient() {
		try {
			String content = restTemplate.getForObject(getBaseUrl() + "/api/v1/calling-another-client", String.class);
			
			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't get String from the other client. Error: " 
					+ e);
		}
	}
	
	@GetMapping("hey")
	public ResponseEntity<Map<String, String>> helloFromAWS() {
		String url = "http://ec2-18-119-163-251.us-east-2.compute.amazonaws.com:8080/api/v1/hello";
		
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> content = restTemplate.getForObject(url, Map.class);
			
			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't get content map from AWS. Error: " 
					+ e);
		}
	}
	
	@GetMapping("coldplay") 
	public ResponseEntity<Map<String, String>> coldplayLyrics() {
		String apiUrl = "https://api.lyrics.ovh/v1/Coldplay/Adventure of a Lifetime";
		try {
			String content = restTemplate.getForObject(apiUrl, String.class);
			Map<String, String> responseMap = new HashMap<>();
			responseMap.put("lyrics", content);
			
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't get lyrics from API. Error: " 
					+ e);
		}
	}

	@GetMapping(value = "json", produces = MediaType.APPLICATION_JSON)
	public Message helloWorld() {
		return new Message(333L, "Hello, the World!");
	}

	@GetMapping(value = "jsonlist", produces = MediaType.APPLICATION_JSON)
	public List<Message> helloWorldList() {
		return List.of(new Message(333L, "Hello, the World!"),
				new Message(1L, "Heyyy"),
				new Message(111L, "Hurrayyy!!!"));
	}

	private String getBaseUrl() {
		ServiceInstance serviceInstance = loadBalancer.choose("ANOTHER-CLIENT");
		
		return serviceInstance.getUri().toString();
	}

	private class Message {
		
		private Long id;
		private String content;
		
		public Message(Long id, String content) {
			this.id = id;
			this.content = content;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

}
