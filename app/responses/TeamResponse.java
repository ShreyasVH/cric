package responses;

import enums.TeamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Country;
import models.Team;

@Getter
@Setter
@NoArgsConstructor
public class TeamResponse
{
    private Long id;
    private String name;
    private Country country;
    private TeamType teamType;

    public TeamResponse(Team team)
    {
        this.id = team.getId();
        this.name = team.getName();
        this.teamType = team.getTeamType();
    }
}
