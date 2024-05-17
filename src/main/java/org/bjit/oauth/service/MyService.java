/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 4/25/2024
 * Version: 1.0
 */

package org.bjit.oauth.service;
import org.bjit.oauth.dto.ApiResponse;
import org.bjit.oauth.dto.LoginRequest;
import org.bjit.oauth.dto.RegistrationRequest;
import org.bjit.oauth.exceptions.UserAlreadyExistsException;
import org.bjit.oauth.model.UserEntity;
import org.bjit.oauth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public MyService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    public ApiResponse register(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new UserAlreadyExistsException("Email already exists");
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new ApiResponse(token);
    }
    public ApiResponse authenticate(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken
                (request.getEmail(), request.getPassword()));
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new ApiResponse(token);
    }
}
