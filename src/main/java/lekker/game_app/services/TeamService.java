package lekker.game_app.services;

import java.util.Iterator;
import java.util.Stack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lekker.game_app.entities.Role;
import lekker.game_app.entities.Team;
import lekker.game_app.entities.User;
import lekker.game_app.repositories.TeamRepository;
import lekker.game_app.repositories.UserRepository;
import lekker.game_app.requests.TeamRequest;
import lekker.game_app.responses.TeamResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Iterable<Team> findAll() {
        return teamRepository.findAll();
    }

    public TeamResponse create(TeamRequest request, String token) {
        String ownerName = jwtService.extractUsernameFromToken(token);
        User user = userRepository.findByUsername(ownerName).get();
        Stack<String> teamRequestStack = new Stack<String>();
        String[] userArray = new String[request.getMaxMembers()];
        userArray[0] = ownerName;

        if (request.getMaxMembers() < 10) 
            return null;

        Team team = Team.builder()
            .teamName(request.getTeamName())
            .maxMembers(request.getMaxMembers())
            .currentMembers(1)
            .ownerName(ownerName)
            .teamMembers(userArray)
            .teamRequests(teamRequestStack)
            .totalScore(user.getScore())
            .build();

        user.setRole(Role.TEAM_LEADER);
        userRepository.save(user);

        if (teamRepository.findByTeamName(request.getTeamName()).isEmpty()) {
            teamRepository.save(team);
            
            return TeamResponse.builder()
            .teamName(team.getTeamName())
            .maxMembers(team.getMaxMembers())
            .currentMembers(1)
            .ownerName(team.getOwnerName())
            .teamRequests(teamRequestStack)
            .totalScore(team.getTotalScore())
            .teamMembers(team.getTeamMembers())
            .build();
        }
        return null;
    }

    public String[] findUsersInTeam(String teamName) {
        Team team = teamRepository.findByTeamName(teamName).get();
        if (team == null)
            return null;
        return team.getTeamMembers();
    }

    public ResponseEntity<Stack<String>> getAllRequests(String teamName, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();
                

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();

            return ResponseEntity.ok(team.getTeamRequests());
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
        }
    }
    
    public String requestToJoin(String teamName, String token) {
        Team team = teamRepository.findByTeamName(teamName).get();
        String requesterName = jwtService.extractUsernameFromToken(token);
        
        if (team == null)
            return null;
            
        if (team.getTeamRequests().search(requesterName) == -1 
            & !team.getOwnerName().equals(requesterName)) {
                String response = team.getTeamRequests().push(requesterName) + " requested to join " + team.getTeamName() + "!";
                teamRepository.save(team);
                return response;
        }
        return null;
    }

    public ResponseEntity<HttpStatus> declineRequest(String teamName, String username, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();

            Stack<String> updatedStack = new Stack<String>();
            Iterator<String> requestee = team.getTeamRequests().iterator();
            while (requestee.hasNext()) {
                String keepUser = requestee.next();
                if (!keepUser.equals(username))
                    updatedStack.add(keepUser);
            }
            team.setTeamRequests(updatedStack);
            teamRepository.save(team);

            return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
        }
    }
    
    public ResponseEntity<HttpStatus> acceptRequest(String teamName, String username, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();

            Stack<String> updatedStack = new Stack<String>();
            Iterator<String> requestee = team.getTeamRequests().iterator();
            while (requestee.hasNext()) {
                String keepUser = requestee.next();
                if (!keepUser.equals(username))
                    updatedStack.add(keepUser);
            }

            String[] updatedTeamMembers = team.getTeamMembers();
            for (int i = 0; i < updatedTeamMembers.length; i++) {
                if (updatedTeamMembers[i] == null) {
                    updatedTeamMembers[i] = username;
                    break;
                }
            }

            team.setTeamRequests(updatedStack);
            team.setTeamMembers(updatedTeamMembers);
            teamRepository.save(team);

            return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
        }
    } 
}
