package responses;

import lombok.Getter;
import lombok.Setter;
import models.BattingScore;
import models.BowlerDismissal;
import models.FielderDismissal;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BattingScoreResponse
{
    private Long id;
    private Long matchId;
    private Long playerId;
    private Long teamId;
    private int runs;
    private int balls;
    private int fours;
    private int sixes;
    private Integer dismissalMode;
    private Long bowlerDismissalId;
    private BowlerDismissal bowler;
    private int innings;
    private int teamInnings;
    private List<FielderDismissal> fielders = new ArrayList<>();

    public BattingScoreResponse(BattingScore battingScore)
    {
        this.id = battingScore.getId();
        this.matchId = battingScore.getMatchId();
        this.playerId = battingScore.getPlayerId();
        this.teamId = battingScore.getTeamId();
        this.runs = battingScore.getRuns();
        this.balls = battingScore.getBalls();
        this.fours = battingScore.getFours();
        this.sixes = battingScore.getSixes();
        this.dismissalMode = battingScore.getDismissalMode();
        this.innings = battingScore.getInnings();
        this.teamInnings = battingScore.getTeamInnings();
        this.bowlerDismissalId = battingScore.getBowlerDismissalId();
    }
}
