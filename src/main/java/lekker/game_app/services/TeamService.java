package lekker.game_app.services;

import org.springframework.stereotype.Service;

import lekker.game_app.entities.Team;
import lekker.game_app.repositories.TeamRepository;
import lekker.game_app.requests.TeamRequest;
import lekker.game_app.responses.TeamResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamResponse create(TeamRequest request) {
        Team team = Team.builder()
            .teamName(request.getTeamName())
            .maxMembers(request.getMaxMembers())
            .currentMembers(1)
            .ownerName("Owner's name")
            .totalScore(0)
            .build();

        if (teamRepository.findByTeamName(request.getTeamName()).isEmpty()) {
            teamRepository.save(team);
            
            return TeamResponse.builder()
            .teamName(request.getTeamName())
            .maxMembers(request.getMaxMembers())
            .currentMembers(1)
            .ownerName("Owner's name")
            .totalScore(0)
            .build();
        }
        return null;
    }
    
}
