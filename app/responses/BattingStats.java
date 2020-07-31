package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BattingStats
{
    private Integer runs = 0;
    private Integer balls = 0;
    private Integer innings = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Integer notOuts = 0;
    private Integer highest;
    private Double average;
    private Double strikeRate;
    private Integer fifties = 0;
    private Integer hundreds = 0;
    private Integer twoHundreds = 0;
    private Integer threeHundreds = 0;
    private Integer fourHundreds = 0;

    public BattingStats(Map<String, Integer> basicStats)
    {
        this.runs = basicStats.getOrDefault("runs", 0);
        this.balls = basicStats.getOrDefault("balls", 0);
        this.innings = basicStats.getOrDefault("innings", 0);
        this.fours = basicStats.getOrDefault("fours", 0);
        this.sixes = basicStats.getOrDefault("sixes", 0);
        this.highest = basicStats.get("highest");
        this.fifties = basicStats.get("fifties");
        this.hundreds = basicStats.get("hundreds");
        this.twoHundreds = basicStats.get("twoHundreds");
        this.threeHundreds = basicStats.get("threeHundreds");
        this.fourHundreds = basicStats.get("fourHundreds");
    }
}
