package lekker.game.backend.services;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lekker.game.backend.entities.Role;
import lekker.game.backend.entities.Team;
import lekker.game.backend.entities.User;
import lekker.game.backend.repositories.TeamRepository;
import lekker.game.backend.repositories.UserRepository;
import lekker.game.backend.requests.EditRequest;
import lekker.game.backend.requests.TeamRequest;
import lekker.game.backend.responses.TeamResponse;
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

        if (user.getRole() == Role.TEAM_LEADER) {
            return null;
        }
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
                .status(HttpStatus.UNAUTHORIZED)
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
                .status(HttpStatus.UNAUTHORIZED)
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
            User newUser = userRepository.findByUsername(username).orElseThrow();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();

            // if team is full
            if (team.getCurrentMembers() == team.getMaxMembers())
            return ResponseEntity
            .status(HttpStatus.INSUFFICIENT_STORAGE)
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

            if (team.getCurrentMembers() > team.getMaxMembers())
                return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();

            team.setTeamRequests(updatedStack);
            team.setTeamMembers(updatedTeamMembers);
            team.setCurrentMembers(team.getCurrentMembers()+1);
            team.setTotalScore(team.getTotalScore()+newUser.getScore());
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

    public ResponseEntity<HttpStatus> editTeam(String teamName, EditRequest request, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();

            if (request.getTeamName() != null)
                team.setTeamName(request.getTeamName());

            // change max members in a team if > 10 and > than number of members in a team
            int maxMembers = Integer.valueOf(request.getMaxMembers());
            if (request.getMaxMembers() != null & maxMembers > 10 & maxMembers > team.getCurrentMembers()) {
                team.setMaxMembers(maxMembers);
                String[] updatedTeamArray = new String[maxMembers];
                String[] oldTeamArray = team.getTeamMembers();
                for (int i = 0; i < oldTeamArray.length; i++) {
                    updatedTeamArray[i] = oldTeamArray[i];
                }
                team.setTeamMembers(updatedTeamArray);
            }

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

    public ResponseEntity<HttpStatus> removeUser(String teamName, String username, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();
            User removeUser = userRepository.findByUsername(username).orElseThrow();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();

            if (Arrays.asList(team.getTeamMembers()).contains(username)) {
                String[] updatedTeamMembers = new String[team.getTeamMembers().length];
                String[] oldTeamMembers = team.getTeamMembers();
                int j = 0;
                for (int i = 0; i < oldTeamMembers.length; i++) {
                    if (oldTeamMembers[i] != username)
                        updatedTeamMembers[j++] = oldTeamMembers[i];
                }
                team.setTeamMembers(updatedTeamMembers);
                team.setCurrentMembers(team.getCurrentMembers()-1);
                team.setTotalScore(team.getTotalScore()-removeUser.getScore());
                teamRepository.save(team);
            }
            else return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();

            return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
        }
    }

    public ResponseEntity<HttpStatus> deleteTeam(String teamName, String token) {
        try {
            Team team = teamRepository.findByTeamName(teamName).orElseThrow();

            User user = userRepository
                .findByUsername(jwtService.extractUsernameFromToken(token))
                .get();

            if ((!user.getRole().equals(Role.TEAM_LEADER))
                & (user.getUsername().equals(team.getOwnerName()))) 
                return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();

            teamRepository.deleteById(team.getId());
            // set the user back to a normal user after deleting team
            user.setRole(Role.USER);
            userRepository.save(user);

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
