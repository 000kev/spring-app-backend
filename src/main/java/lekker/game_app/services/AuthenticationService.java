package lekker.game_app.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lekker.game_app.entities.Role;
import lekker.game_app.entities.User;
import lekker.game_app.repositories.UserRepository;
import lekker.game_app.requests.AuthenticationRequest;
import lekker.game_app.requests.RegisterRequest;
import lekker.game_app.responses.AuthenticationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        if (
            userRepository.findByEmail(request.getEmail()).isEmpty()
        ) {
            userRepository.save(user);

            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(jwtToken).build();
        }
        return null;
        
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow();
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
        .builder()
        .password(request.getPassword())
        .token(jwtToken)
        .build();
    }
    
}
