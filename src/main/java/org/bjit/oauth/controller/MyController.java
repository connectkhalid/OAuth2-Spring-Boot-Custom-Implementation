/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 4/25/2024
 * Version: 1.0
 */

package org.bjit.oauth.controller;

import jakarta.validation.Valid;
import org.bjit.oauth.dto.ApiResponse;
import org.bjit.oauth.dto.LoginRequest;
import org.bjit.oauth.dto.RegistrationRequest;
import org.bjit.oauth.service.JwtService;
import org.bjit.oauth.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MyController {
    private final MyService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    private final OAuth2AuthorizedClientService authorizedClientService;
    Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();
    @Autowired
    private  ClientRegistrationRepository clientRegistrationRepository;
    public MyController(MyService service, AuthenticationManager authenticationManager, JwtService jwtService, OAuth2AuthorizedClientService authorizedClientService) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authorizedClientService = authorizedClientService;
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser (@Valid @RequestBody RegistrationRequest request) {
        return  ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/secured")
    public ResponseEntity<String> secured(OAuth2AuthenticationToken authentication) {
        return ResponseEntity.ok("Hello from secured End-point" +  authentication.getCredentials().toString());
    }
    @GetMapping("/")
    public  ResponseEntity<String> home(){
        return ResponseEntity.ok("Hello from home end-point open for all");
    }
    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(),
                authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth_login";
    }
    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<String>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            model.addAttribute("name", userAttributes.get("name"));
        }
        return "loginSuccess";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/ping")
    public String test() {
        try {
            return "Welcome";
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
