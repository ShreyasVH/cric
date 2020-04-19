package responses;

import enums.GameType;
import lombok.Getter;
import lombok.Setter;
import models.Country;
import models.Player;

import java.util.Date;
import java.util.HashMap;
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
    private Map<GameType, Map<String, Integer>> dismissalStats = new HashMap<>();
    private Map<GameType, BattingStats> battingStats = new HashMap<>();
    private Map<GameType, FieldingStats> fieldingStats = new HashMap<>();
    private Map<GameType, BowlingStats> bowlingStats = new HashMap<>();

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
