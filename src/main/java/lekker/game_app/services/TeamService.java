package lekker.game_app.services;

import java.util.Stack;

import org.springframework.http.HttpStatus;
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
    
    public String requestToJoin(String teamName, String token) {
        Team team = teamRepository.findByTeamName(teamName).get();
        String requesterName = jwtService.extractUsernameFromToken(token);
        
        if (team == null)
            return null;
        if (team.getTeamRequests().search(requesterName) == -1 
            && team.getOwnerName() != requesterName) {
                return team.getTeamRequests().push(requesterName);
        }
        return null;
    }
}
