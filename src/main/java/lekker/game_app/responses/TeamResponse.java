package lekker.game_app.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    
    private String teamName;
    private String ownerName;
    private Integer totalScore;
    private Integer maxMembers;
    private Integer currentMembers;
}
