package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FieldingStats
{
    private Integer catches = 0;
    private Integer runOuts = 0;
    private Integer stumpings = 0;

    public FieldingStats(Map<String, Integer> fieldingStats)
    {
        this.catches = fieldingStats.getOrDefault("Caught", 0);
        this.runOuts = fieldingStats.getOrDefault("Run Out", 0);
        this.stumpings = fieldingStats.getOrDefault("Stumped", 0);
    }
}
