package responses;

import enums.ResultType;
import enums.WinMarginType;
import lombok.Getter;
import lombok.Setter;
import models.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatchResponse
{
    private Long id;
    private Long series;
    private Long team1;
    private Long team2;
    private Long tossWinner;
    private Long batFirst;
    private ResultType result;
    private Long winner;
    private Integer winMargin;
    private WinMarginType winMarginType;
    private Long stadiumId;
    private Long startTime;
    private String tag;
    private List<BattingScoreResponse> battingScores = new ArrayList<>();
    private List<BowlingFigure> bowlingFigures = new ArrayList<>();
    private List<Extras> extras = new ArrayList<>();
    private List<MatchPlayerMap> players = new ArrayList<>();
    private List<ManOfTheMatch> manOfTheMatchList = new ArrayList<>();

    public MatchResponse(Match match)
    {
        this.id = match.getId();
        this.series = match.getSeries();
        this.team1 = match.getTeam1();
        this.team2 = match.getTeam2();
        this.tossWinner = match.getTossWinner();
        this.batFirst = match.getBatFirst();
        this.result = match.getResult();
        this.winner = match.getWinner();
        this.winMargin = match.getWinMargin();
        this.winMarginType = match.getWinMarginType();
        this.stadiumId = match.getStadium();
        this.startTime = match.getStartTime();
        this.tag = match.getTag();
    }
}
