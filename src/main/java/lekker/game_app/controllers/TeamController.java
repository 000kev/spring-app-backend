package lekker.game_app.controllers;

import java.util.Base64;
import java.util.Stack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lekker.game_app.entities.Team;
import lekker.game_app.entities.User;
import lekker.game_app.requests.TeamRequest;
import lekker.game_app.responses.TeamResponse;
import lekker.game_app.services.JwtService;
import lekker.game_app.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;
    
    // create a new team
    @PostMapping("/create")
    public ResponseEntity<TeamResponse> create(
        @RequestBody TeamRequest request,
        @RequestHeader("Authorization") String token
        ) {
        return ResponseEntity.ok(service.create(request, token));
    }

    // view all teams
    @GetMapping("/viewAll")
    public ResponseEntity<Iterable<Team>> viewAllTeams(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.findAll());
    }

    // view the users inside a team
    @GetMapping("/view/{teamName}")
    public ResponseEntity<String[]> viewTeam(@PathVariable String teamName) {
        return ResponseEntity.ok(service.findUsersInTeam(teamName));
    }
    
    // request to join a team
    @PostMapping("/request/{teamName}")
    public ResponseEntity<String> requestToJoinTeam(
        @PathVariable String teamName,
        @RequestHeader("Authorization") String token
        ) {
        return ResponseEntity.ok(service.requestToJoin(teamName, token));
    }

    // TEAM OWNERS ONLY - view team requests
    @GetMapping("/request/{teamName}")
    public ResponseEntity<Stack<String>> viewAllTeamRequests(
        @PathVariable String teamName,
        @RequestHeader("Authorization") String token
    ) {
        return service.getAllRequests(teamName, token);
    } 

    @PostMapping("/request/accept/{teamName}/{username}")
    public ResponseEntity<HttpStatus> acceptRequest(
        @PathVariable("teamName") String teamName,
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    ) {
        return service.acceptRequest(teamName, username, token);
    }

    @PostMapping("/request/decline/{teamName}/{username}")
    public ResponseEntity<HttpStatus> declineRequest(
        @PathVariable("teamName") String teamName,
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    ) {
        
        return service.declineRequest(teamName, username, token);
    }
    
    
    
}
