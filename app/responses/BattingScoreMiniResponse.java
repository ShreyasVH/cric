package responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattingScoreMiniResponse
{
    private int runs;
    private int balls;
    private int fours;
    private int sixes;
    private long teamId;
    private int gameType;
    private long matchTime;
    private String series;
    private String team;
    private String opposingTeam;
}
