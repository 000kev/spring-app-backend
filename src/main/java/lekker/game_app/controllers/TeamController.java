package lekker.game_app.controllers;

import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lekker.game_app.requests.TeamRequest;
import lekker.game_app.responses.TeamResponse;
import lekker.game_app.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;
    
    @PostMapping("/create")
    public ResponseEntity<TeamResponse> create(@RequestBody TeamRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/token")
    public String basePath(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }
    
}
