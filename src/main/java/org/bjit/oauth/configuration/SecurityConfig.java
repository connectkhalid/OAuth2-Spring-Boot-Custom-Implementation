/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 4/25/2024
 * Version: 1.0
 */

package org.bjit.oauth.configuration;

import org.bjit.oauth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class SecurityConfig {
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final static List<String> clients = Arrays.asList("google", "facebook", "github");
    private final static String CLIENT_PROPERTY_KEY
            = "spring.security.oauth2.client.registration.";
    private final Environment env;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, Environment env) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.env = env;
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/oauth_login", "/loginFailure", "/")
//                        .permitAll()
//                        .anyRequest()
//                        .authenticated()
//                )
//                .oauth2Login(oauth2Login -> oauth2Login
//                        .loginPage("/oauth_login")
//                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
//                                .baseUri("/oauth2/authorize-client")
//                                .authorizationRequestRepository(authorizationRequestRepository())
//                        )
//                        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
//                                .accessTokenResponseClient(accessTokenResponseClient())
//                        )
//                        .defaultSuccessUrl("/loginSuccess")
//                        .failureUrl("/loginFailure")
//                );
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/login", "/register", "/oauth_login")
                                .permitAll()
                                .requestMatchers("/secured")
                                .authenticated()
                )
                .oauth2Login(configurer -> {
                    configurer
                            .loginPage("/oauth_login") // Custom OAuth2 login page
                            .defaultSuccessUrl("/secured") // Redirect URL after successful login
                            .failureUrl("/login?error=true") // Redirect URL after failed login
                            .authorizationEndpoint(authorizationEndpointConfig ->
                                    authorizationEndpointConfig
                                            .baseUri("/oauth2/authorize-client")
                                            .authorizationRequestRepository(authorizationRequestRepository())
                            )
                            .redirectionEndpoint(redirectionEndpointConfig ->
                                    redirectionEndpointConfig
                                            .baseUri("/secured")
                            )
                            .clientRegistrationRepository(clientRegistrationRepository())
                            .authorizedClientService(authorizedClientService());
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {

        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }
    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
//            String errorMessage= "No client id found for "+ client+ "client";
//            logger.info(errorMessage);
            return null;
        }

        String clientSecret = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("github")) {
            return CommonOAuth2Provider.GITHUB.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null;
    }
    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
    authorizationRequestRepository() {

        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
    accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

}



//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(
//                        req -> req
//                                .requestMatchers("/", "/login", "/register"
//                                        )
//                                .permitAll()
//                                .anyRequest()
//                                .authenticated()
//                )
//                .oauth2Client(Customizer.withDefaults())
//                .sessionManagement(
//                        session -> session
//                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }