package responses;

import lombok.Getter;
import lombok.Setter;
import models.Country;
import models.Player;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class PlayerResponse
{
    private Long id;
    private String name;
    private Country country;
    private String image;
    private Date createdAt;
    private Date updatedAt;
    private Map<String, Integer> dismissalStats;
    private BattingStats battingStats = new BattingStats();
    private FieldingStats fieldingStats = new FieldingStats();
    private BowlingStats bowlingStats = new BowlingStats();

    public PlayerResponse(Player player)
    {
        this.id = player.getId();
        this.name = player.getName();
        this.country = player.getCountry();
        this.image = player.getImage();
        this.createdAt = player.getCreatedAt();
        this.updatedAt = player.getUpdatedAt();
    }
}
