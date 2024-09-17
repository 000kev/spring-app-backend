package lekker.game.backend.controllers;

import java.util.Base64;
import java.util.Stack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lekker.game.backend.entities.Team;
import lekker.game.backend.entities.User;
import lekker.game.backend.requests.EditRequest;
import lekker.game.backend.requests.TeamRequest;
import lekker.game.backend.responses.TeamResponse;
import lekker.game.backend.services.JwtService;
import lekker.game.backend.services.TeamService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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

    // TEAM OWNERS ONLY - delete a team
    @DeleteMapping("delete/{teamName}")
    public ResponseEntity<HttpStatus> deleteTeam(
        @PathVariable String teamName,
        @RequestHeader("Authorization") String token
    ) {
        return service.deleteTeam(teamName, token);
    }

    // TEAM OWNERS ONLY - remove user from team
    @PostMapping("/remove/{teamName}/{username}")
    public ResponseEntity<HttpStatus> postMethodName(
        @PathVariable String teamName,
        @PathVariable String username,
        @RequestHeader("Authorization") String token
    ) {
        return service.removeUser(teamName, username, token);
    }
    

    // TEAM OWNERS ONLY - edit team name
    @PostMapping("/edit/{teamName}")
    public ResponseEntity<HttpStatus> editTeam(
        @RequestBody EditRequest request,
        @PathVariable String teamName,
        @RequestHeader("Authorization") String token
        ) {
        return service.editTeam(teamName, request, token);
    }

    // TEAM OWNERS ONLY - view team requests
    @GetMapping("/request/{teamName}")
    public ResponseEntity<Stack<String>> viewAllTeamRequests(
        @PathVariable String teamName,
        @RequestHeader("Authorization") String token
    ) {
        return service.getAllRequests(teamName, token);
    } 

    // TEAM OWNERS ONLY - accept team requests & add to team
    @PostMapping("/request/accept/{teamName}/{username}")
    public ResponseEntity<HttpStatus> acceptRequest(
        @PathVariable("teamName") String teamName,
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    ) {
        return service.acceptRequest(teamName, username, token);
    }

    // TEAM OWNERS ONLY - decline team requests
    @PostMapping("/request/decline/{teamName}/{username}")
    public ResponseEntity<HttpStatus> declineRequest(
        @PathVariable("teamName") String teamName,
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    ) {
        
        return service.declineRequest(teamName, username, token);
    }
    
    
    
}
