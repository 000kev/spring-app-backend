package lekker.game.backend.entities;

import java.util.Stack;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
    @GeneratedValue
    private Integer id;
    private String teamName;
    private String ownerName;
    private Integer totalScore;
    private Integer maxMembers;
    private Integer currentMembers;
    private String[] teamMembers;
    private Stack<String> teamRequests; 
}
