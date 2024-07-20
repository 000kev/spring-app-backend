package lekker.game.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import lekker.game.backend.entities.Team;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
    Optional<Team> findByTeamName(String teamName);
}
