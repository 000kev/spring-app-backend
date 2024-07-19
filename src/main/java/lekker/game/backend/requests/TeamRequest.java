package lekker.game.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequest {
    
    private String teamName;
    private String ownerName;
    private Integer maxMembers;
}
