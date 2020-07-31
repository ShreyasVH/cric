package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class BowlingStats
{
    private Integer balls = 0;
    private Integer maidens = 0;
    private Integer runs = 0;
    private Integer wickets = 0;
    private Double economy;
    private Double average;
    private Double strikeRate;
    private Integer fifers = 0;
    private Integer tenWickets = 0;

    public BowlingStats(Map<String, Integer> basicStats)
    {
        this.balls = basicStats.getOrDefault("balls", 0);
        this.maidens = basicStats.getOrDefault("maidens", 0);
        this.runs = basicStats.getOrDefault("runs", 0);
        this.wickets = basicStats.getOrDefault("wickets", 0);
        this.fifers = basicStats.get("fifers");
        this.tenWickets = basicStats.get("tenWickets");
    }
}
