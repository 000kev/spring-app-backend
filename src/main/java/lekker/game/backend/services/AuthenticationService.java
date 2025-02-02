package lekker.game.backend.services;

import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lekker.game.backend.entities.Role;
import lekker.game.backend.entities.User;
import lekker.game.backend.repositories.UserRepository;
import lekker.game.backend.requests.AuthenticationRequest;
import lekker.game.backend.requests.RegisterRequest;
import lekker.game.backend.responses.AuthenticationResponse;
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

            var role = (user.getRole() == Role.USER ) ? Role.USER : Role.TEAM_LEADER;
            var score = user.getScore();

            return AuthenticationResponse
            .builder()
            .role(role.toString())
            .score(score)
            .username(request.getUsername())
            .token(jwtToken)
            .build();
        }
        else throw new Error("Username already exists");
        
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

        var role = user.getRole().toString();
        var score = user.getScore();


        return AuthenticationResponse
        .builder()
        .role(role)
        .username(request.getUsername())
        .score(score)
        // .password(request.getPassword())
        .token(jwtToken)
        .build();
    }
    
}
