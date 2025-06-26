package com.servicea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicea.config.WebClientService;
import com.servicea.dto.SessionDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/service-a")
public class ReadController {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private WebClientService webClientService;
	
	@GetMapping("/read/{key}") //to get curently logged user seesion info
	ResponseEntity<?> readsession(@PathVariable String key) {
		SessionDetails sessionInfo = (SessionDetails) redisTemplate.opsForValue().get("session:" + key);
		if (sessionInfo != null) {
			System.out.println("CURRENTLY LOGGED USER NAME IS"+sessionInfo.getUsername());
			System.out.println("SESSION_KEY : " + sessionInfo.getSessionkey());
			System.out.println("JSESSION_ID : " + sessionInfo.getJsessionid());
			return ResponseEntity.ok(sessionInfo+"CURRENTLY LOGGED USRE FROM SERVICE A");
		}
		return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("No session found or session expired.");
	}	
	

    @GetMapping("/callserviceb") // from service A
    public Mono<ResponseEntity<String>> callServiceB() {
        return webClientService
                .callService("service-b", "/api/service-b/test", HttpMethod.GET, null, String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Error calling service-B: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Service B call failed: " + e.getMessage()));
                });
    }
}
