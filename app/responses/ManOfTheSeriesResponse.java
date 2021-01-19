package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.ManOfTheSeries;

@Setter
@Getter
@NoArgsConstructor
public class ManOfTheSeriesResponse
{
    private Long id;
    private Long playerId;
    private String playerName;
    private Long teamId;
    private String teamName;

    public ManOfTheSeriesResponse(ManOfTheSeries mots)
    {
        this.id = mots.getId();
        this.playerId = mots.getPlayerId();
        this.teamId = mots.getTeamId();
    }
}
