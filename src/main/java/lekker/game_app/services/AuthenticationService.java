package lekker.game_app.services;

import java.util.Random;

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
        Random random = new Random();
        int randomScore = 0;
        while (randomScore == 0) {
            randomScore = random.nextInt(21);
        }
        User user = User.builder()
            .username(request.getUsername())
            .score(randomScore)
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        if (
            userRepository.findByUsername(request.getUsername()).isEmpty()
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
                request.getUsername(),
                request.getPassword()
            )
        );
        var user = userRepository.findByUsername(request.getUsername())
        .orElseThrow();
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
        .builder()
        .password(request.getPassword())
        .token(jwtToken)
        .build();
    }
    
}
