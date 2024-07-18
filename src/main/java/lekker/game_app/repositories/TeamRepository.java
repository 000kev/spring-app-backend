package lekker.game_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import lekker.game_app.entities.Team;

@Repository
public interface TeamRepository extends CrudRepository<Team, String> {
    Optional<Team> findByTeamName(String teamName);
}
