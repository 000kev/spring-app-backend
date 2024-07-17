package lekker.game_app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Team {
    @Id
    private String teamName;
    private String ownerName;
    private Integer totalScore;
    private Integer maxMembers;
    private Integer currentMembers;

}
